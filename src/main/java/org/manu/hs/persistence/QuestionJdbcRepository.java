package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QuestionJdbcRepository implements QuestionRepository {

    // Aggregates each question's concept tags into a single array column in the same
    // round trip, instead of one extra query per question (N+1).
    private static final String SELECT_WITH_CONCEPT_TAGS = """
            SELECT q.id, q.title, q.requirement, q.sample_solution, q.difficulty_score,
                   q.question_status, q.source,
                   q.created_by, q.updated_by, q.created_at, q.updated_at,
                   COALESCE(
                       array_agg(qc.concept_tag::text ORDER BY qc.concept_tag)
                           FILTER (WHERE qc.concept_tag IS NOT NULL),
                       ARRAY[]::text[]
                   ) AS concept_tags
            FROM question q
            LEFT JOIN question_concept qc ON qc.question_id = q.id
            """;

    private static final String GROUP_BY_QUESTION = """
            GROUP BY q.id, q.title, q.requirement, q.sample_solution, q.difficulty_score,
                     q.question_status, q.source,
                     q.created_by, q.updated_by, q.created_at, q.updated_at
            """;

    private static final String FIND_BY_ID =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.id = :id " + GROUP_BY_QUESTION;

    private static final String FIND_BY_IDS =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.id IN (:ids) " + GROUP_BY_QUESTION;

    // Links of submitted questions still waiting on a concept owner's decision.
    private static final String SELECT_LINKS_AWAITING_REVIEW = """
            SELECT qc.question_id, qc.concept_tag
            FROM question_concept qc
            JOIN question q ON q.id = qc.question_id
            JOIN concept c ON c.tag = qc.concept_tag
            WHERE qc.link_status = 'PENDING'
              AND q.question_status = 'SUBMITTED'
            """;

    // Oldest question first: whoever waited longest is reviewed first.
    private static final String ORDER_LINKS_OLDEST_FIRST = " ORDER BY qc.question_id, qc.concept_tag";

    private static final String FIND_LINKS_AWAITING_REVIEW =
            SELECT_LINKS_AWAITING_REVIEW + ORDER_LINKS_OLDEST_FIRST;

    private static final String FIND_LINKS_AWAITING_REVIEW_BY_CONCEPT_OWNER =
            SELECT_LINKS_AWAITING_REVIEW + " AND c.created_by = :owner_id" + ORDER_LINKS_OLDEST_FIRST;

    // Only the links a learner may see: accepted, on a published concept. Inner joins drop
    // questions that have none.
    private static final String SELECT_AVAILABLE_FOR_LEARNING = """
            SELECT q.id, q.title, q.requirement, q.sample_solution, q.difficulty_score,
                   q.question_status, q.source,
                   q.created_by, q.updated_by, q.created_at, q.updated_at,
                   array_agg(qc.concept_tag::text ORDER BY qc.concept_tag) AS concept_tags
            FROM question q
            JOIN question_concept qc ON qc.question_id = q.id AND qc.link_status = 'ACCEPTED'
            JOIN concept c ON c.tag = qc.concept_tag AND c.concept_status = 'ACTIVE'
            WHERE q.question_status = 'SUBMITTED'
            """;

    private static final String FIND_AVAILABLE_FOR_LEARNING_BY_ID =
            SELECT_AVAILABLE_FOR_LEARNING + " AND q.id = :id " + GROUP_BY_QUESTION;

    // When this learner last answered the question; NULL if never.
    private static final String LAST_ANSWERED_AT = """
            (SELECT max(a.answered_at)
             FROM answer a
             WHERE a.user_id = :user_id
               AND a.question_id = q.id)""";

    // One available question for a learner, leaving out the skipped ones. With concept tags, it
    // must be accepted for at least one of them. Never answered by this learner first, in random
    // order. Then the answered ones, at random but weighted by age: weight = days since last
    // answered + 1, so one done 30 days ago is 31 times as likely to come first as one done today,
    // yet never certain. (Weighted random sampling: order by -ln(u) / weight, u uniform in (0, 1].)
    // IN (...) can't be empty: callers pass placeholder lists and :any_concept instead.
    private static final String FIND_NEXT_FOR_LEARNING =
            SELECT_AVAILABLE_FOR_LEARNING
                    + " AND q.id NOT IN (:skip_ids) "
                    + GROUP_BY_QUESTION
                    + " HAVING :any_concept OR bool_or(qc.concept_tag IN (:concept_tags))"
                    + " ORDER BY " + LAST_ANSWERED_AT + " IS NOT NULL,"
                    + " -ln(1 - random()) / (extract(epoch FROM now() - coalesce(" + LAST_ANSWERED_AT
                    + ", now())) / 86400 + 1)"
                    + " LIMIT 1";

    // The same available questions and concept filter as FIND_NEXT_FOR_LEARNING, never answered by
    // this learner, counted.
    private static final String COUNT_UNANSWERED_FOR_LEARNING =
            "SELECT count(*) FROM ("
                    + SELECT_AVAILABLE_FOR_LEARNING
                    + " AND " + LAST_ANSWERED_AT + " IS NULL "
                    + GROUP_BY_QUESTION
                    + " HAVING :any_concept OR bool_or(qc.concept_tag IN (:concept_tags))"
                    + ") unanswered";

    /** Matches no question id: stands in for "skip nothing", since IN (...) can't be empty. */
    private static final Long NO_QUESTION_ID = -1L;

    private static final String UPDATE_LINK_STATUS = """
            UPDATE question_concept
            SET link_status = :link_status,
                decided_by = :decided_by,
                decided_at = now()
            WHERE question_id = :question_id
              AND concept_tag = :concept_tag
            """;

    // Newest first: backs "my questions", where the latest submission matters most.
    private static final String FIND_BY_SUBMITTED_BY =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.created_by = :created_by " + GROUP_BY_QUESTION + " ORDER BY q.id DESC";

    private static final String INSERT = """
            INSERT INTO question (
                title, requirement, sample_solution, difficulty_score,
                question_status, source, created_by, updated_by
            )
            VALUES (
                :title, :requirement, :sample_solution, :difficulty_score,
                :question_status, :source, :created_by, :created_by
            )
            RETURNING id
            """;

    private static final String UPDATE = """
            UPDATE question
            SET title = :title,
                requirement = :requirement,
                sample_solution = :sample_solution,
                difficulty_score = :difficulty_score,
                source = :source,
                updated_by = :updated_by
            WHERE id = :id
            """;

    private static final String UPDATE_STATUS = """
            UPDATE question
            SET question_status = :status,
                updated_by = :updated_by
            WHERE id = :id
            """;

    private static final String DELETE = """
            DELETE FROM question
            WHERE id = :id
            """;

    // Links the question already has are left untouched, so their state survives edits.
    private static final String INSERT_CONCEPT_LINK = """
            INSERT INTO question_concept (question_id, concept_tag, link_status)
            VALUES (:question_id, :concept_tag, :link_status)
            ON CONFLICT (question_id, concept_tag) DO NOTHING
            """;

    private static final String DELETE_REMOVED_CONCEPT_LINKS = """
            DELETE FROM question_concept
            WHERE question_id = :question_id
              AND concept_tag NOT IN (:concept_tags)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Question insert(Question question, List<String> tagsAwaitingApproval) {
        Long id = jdbcTemplate.queryForObject(
                INSERT,
                insertParameters(question),
                Long.class);

        linkConceptTags(id, question.getConceptTags(), tagsAwaitingApproval);

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question was inserted but could not be retrieved"));
    }

    @Override
    @Transactional
    public Question update(Long id, Question question, List<String> tagsAwaitingApproval) {
        jdbcTemplate.update(
                UPDATE,
                updateParameters(id, question));

        // A question always has at least one concept, so the NOT IN list is never empty.
        jdbcTemplate.update(
                DELETE_REMOVED_CONCEPT_LINKS,
                Map.of("question_id", id, "concept_tags", question.getConceptTags()));
        linkConceptTags(id, question.getConceptTags(), tagsAwaitingApproval);

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question was updated but could not be retrieved"));
    }

    @Override
    public Question updateStatus(Long id, QuestionStatus status, Long updatedBy) {
        jdbcTemplate.update(
                UPDATE_STATUS,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("status", status.name())
                        .addValue("updated_by", updatedBy));

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question status was updated but could not be retrieved"));
    }

    @Override
    public void deleteById(Long id) {
        // question_concept rows are removed automatically via ON DELETE CASCADE.
        jdbcTemplate.update(
                DELETE,
                Map.of("id", id));
    }

    @Override
    public boolean isReferencedByAnyAnswer(Long id) {
        Boolean referenced = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM answer
                            WHERE question_id = :id
                        )
                        """,
                Map.of("id", id),
                Boolean.class);

        return Boolean.TRUE.equals(referenced);
    }

    @Override
    public Optional<Question> findById(Long id) {
        List<Question> questions = jdbcTemplate.query(
                FIND_BY_ID,
                Map.of("id", id),
                this::mapQuestion);

        return questions.stream().findFirst();
    }

    @Override
    public List<Question> findBySubmittedBy(Long userId) {
        return jdbcTemplate.query(
                FIND_BY_SUBMITTED_BY,
                Map.of("created_by", userId),
                this::mapQuestion);
    }

    @Override
    public List<ConceptLink> findLinksAwaitingReview() {
        return linksAwaitingReview(FIND_LINKS_AWAITING_REVIEW, Map.of());
    }

    @Override
    public List<ConceptLink> findLinksAwaitingReviewByConceptOwner(Long ownerId) {
        return linksAwaitingReview(FIND_LINKS_AWAITING_REVIEW_BY_CONCEPT_OWNER, Map.of("owner_id", ownerId));
    }

    @Override
    public Optional<Question> findAvailableForLearningById(Long id) {
        return jdbcTemplate.query(FIND_AVAILABLE_FOR_LEARNING_BY_ID, Map.of("id", id), this::mapQuestion)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Question> findNextForLearning(Long userId, List<String> conceptTags, List<Long> skippedIds) {
        return jdbcTemplate.query(
                        FIND_NEXT_FOR_LEARNING,
                        new MapSqlParameterSource()
                                .addValue("user_id", userId)
                                .addValue("any_concept", conceptTags.isEmpty())
                                .addValue("concept_tags", conceptTags.isEmpty() ? List.of("") : conceptTags)
                                .addValue("skip_ids", skippedIds.isEmpty() ? List.of(NO_QUESTION_ID) : skippedIds),
                        this::mapQuestion)
                .stream()
                .findFirst();
    }

    @Override
    public int countUnansweredForLearning(Long userId, List<String> conceptTags) {
        Integer count = jdbcTemplate.queryForObject(
                COUNT_UNANSWERED_FOR_LEARNING,
                new MapSqlParameterSource()
                        .addValue("user_id", userId)
                        .addValue("any_concept", conceptTags.isEmpty())
                        .addValue("concept_tags", conceptTags.isEmpty() ? List.of("") : conceptTags),
                Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public void updateLinkStatus(Long questionId, String conceptTag, ConceptLinkStatus status, Long decidedBy) {
        jdbcTemplate.update(
                UPDATE_LINK_STATUS,
                new MapSqlParameterSource()
                        .addValue("question_id", questionId)
                        .addValue("concept_tag", conceptTag)
                        .addValue("link_status", status.name())
                        .addValue("decided_by", decidedBy));
    }

    /**
     * Two round trips instead of one per link: the waiting (question, concept) pairs, then
     * their questions in a single query.
     */
    private List<ConceptLink> linksAwaitingReview(String sql, Map<String, ?> parameters) {
        List<Map.Entry<Long, String>> waiting = jdbcTemplate.query(
                sql,
                parameters,
                (resultSet, rowNumber) -> Map.entry(resultSet.getLong("question_id"), resultSet.getString("concept_tag")));

        if (waiting.isEmpty()) {
            return List.of();
        }

        Map<Long, Question> questionsById = jdbcTemplate.query(
                        FIND_BY_IDS,
                        Map.of("ids", waiting.stream().map(Map.Entry::getKey).distinct().toList()),
                        this::mapQuestion)
                .stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

        return waiting.stream()
                .map(link -> ConceptLink.builder()
                        .question(questionsById.get(link.getKey()))
                        .conceptTag(link.getValue())
                        .status(ConceptLinkStatus.PENDING)
                        .build())
                .toList();
    }

    private void linkConceptTags(Long questionId, List<String> conceptTags, List<String> tagsAwaitingApproval) {
        SqlParameterSource[] linkParameters = conceptTags.stream()
                .map(conceptTag -> new MapSqlParameterSource()
                        .addValue("question_id", questionId)
                        .addValue("concept_tag", conceptTag)
                        .addValue("link_status", tagsAwaitingApproval.contains(conceptTag)
                                ? ConceptLinkStatus.PENDING.name()
                                : ConceptLinkStatus.ACCEPTED.name()))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(INSERT_CONCEPT_LINK, linkParameters);
    }

    private MapSqlParameterSource insertParameters(Question question) {
        return new MapSqlParameterSource()
                .addValue("title", question.getTitle())
                .addValue("requirement", question.getRequirement())
                .addValue("sample_solution", question.getSampleSolution())
                .addValue("difficulty_score", question.getDifficultyScore())
                .addValue("question_status", question.getStatus().name())
                .addValue("source", question.getSource())
                .addValue("created_by", question.getCreatedBy());
    }

    private MapSqlParameterSource updateParameters(Long id, Question question) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", question.getTitle())
                .addValue("requirement", question.getRequirement())
                .addValue("sample_solution", question.getSampleSolution())
                .addValue("difficulty_score", question.getDifficultyScore())
                .addValue("source", question.getSource())
                .addValue("updated_by", question.getUpdatedBy());
    }

    private Question mapQuestion(ResultSet resultSet, int rowNumber) throws SQLException {
        return Question.builder()
                .id(resultSet.getLong("id"))
                .title(resultSet.getString("title"))
                .requirement(resultSet.getString("requirement"))
                .sampleSolution(resultSet.getString("sample_solution"))
                .difficultyScore(resultSet.getInt("difficulty_score"))
                .status(QuestionStatus.valueOf(resultSet.getString("question_status")))
                .source(resultSet.getString("source"))
                .createdBy(resultSet.getLong("created_by"))
                .updatedBy(resultSet.getLong("updated_by"))
                .createdAt(resultSet.getTimestamp("created_at").toInstant())
                .updatedAt(resultSet.getTimestamp("updated_at").toInstant())
                .conceptTags(readConceptTags(resultSet))
                .build();
    }

    private List<String> readConceptTags(ResultSet resultSet) throws SQLException {
        Array array = resultSet.getArray("concept_tags");
        return Arrays.asList((String[]) array.getArray());
    }
}
