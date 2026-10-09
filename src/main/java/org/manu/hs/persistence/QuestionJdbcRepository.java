package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.question.ReportStatus;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.question.VoteTotals;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QuestionJdbcRepository implements QuestionRepository {

    // Aggregates each question's concept tags into a single array column in the same
    // round trip, instead of one extra query per question (N+1). A concept whose owner declined
    // the question isn't one of its tags any more (the DECLINED link stays, as the decision).
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
            LEFT JOIN question_concept qc ON qc.question_id = q.id AND qc.link_status <> 'DECLINED'
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

    /** Open reports (one per learner at most) at which a question is held back from learners. */
    private static final int OPEN_REPORTS_TO_HOLD_BACK = 3;

    // Only the links a learner may see: accepted, on a published concept. Inner joins drop
    // questions that have none. Questions with too many open problem reports are held back until
    // their author, a concept owner or an admin resolves or dismisses them; praise never counts.
    private static final String SELECT_AVAILABLE_FOR_LEARNING = """
            SELECT q.id, q.title, q.requirement, q.sample_solution, q.difficulty_score,
                   q.question_status, q.source,
                   q.created_by, q.updated_by, q.created_at, q.updated_at,
                   array_agg(qc.concept_tag::text ORDER BY qc.concept_tag) AS concept_tags
            FROM question q
            JOIN question_concept qc ON qc.question_id = q.id AND qc.link_status = 'ACCEPTED'
            JOIN concept c ON c.tag = qc.concept_tag AND c.concept_status = 'ACTIVE'
            WHERE q.question_status = 'SUBMITTED'
              AND (SELECT count(*)
                   FROM question_report r
                   WHERE r.question_id = q.id
                     AND r.report_status = 'OPEN'
                     AND r.reason <> 'PRAISE') < %d
            """.formatted(OPEN_REPORTS_TO_HOLD_BACK);

    private static final String FIND_AVAILABLE_FOR_LEARNING_BY_ID =
            SELECT_AVAILABLE_FOR_LEARNING + " AND q.id = :id " + GROUP_BY_QUESTION;

    // When this learner last answered the question; NULL if never.
    private static final String LAST_ANSWERED_AT = """
            (SELECT max(a.answered_at)
             FROM answer a
             WHERE a.user_id = :user_id
               AND a.question_id = q.id)""";

    // How many learners like the question: an up vote or praise, each learner counted once.
    private static final String LIKES = """
            (SELECT count(*)
             FROM (SELECT v.user_id FROM question_vote v WHERE v.question_id = q.id AND v.vote = 'UP'
                   UNION
                   SELECT r.reported_by FROM question_report r WHERE r.question_id = q.id AND r.reason = 'PRAISE') likers)""";

    // One available question for a learner, leaving out the skipped ones. With concept tags, it
    // must be accepted for at least one of them. Never answered by this learner first, then the
    // answered ones, each group at random but weighted:
    //   - by age: days since last answered + 1 (1 for a new one), so one done 30 days ago is
    //     31 times as likely to come first as one done today
    //   - by likes: 1 + ln(1 + likes), a nudge that flattens out: 1 like 1.7, 5 likes 2.8,
    //     20 likes 4.0
    // The weights multiply, and nothing is ever certain. (Weighted random sampling: order by
    // -ln(u) / weight, u uniform in (0, 1].)
    // IN (...) can't be empty: callers pass placeholder lists and :any_concept instead.
    private static final String FIND_NEXT_FOR_LEARNING =
            SELECT_AVAILABLE_FOR_LEARNING
                    + " AND q.id NOT IN (:skip_ids) "
                    + GROUP_BY_QUESTION
                    + " HAVING :any_concept OR bool_or(qc.concept_tag IN (:concept_tags))"
                    + " ORDER BY " + LAST_ANSWERED_AT + " IS NOT NULL,"
                    + " -ln(1 - random()) / ((extract(epoch FROM now() - coalesce(" + LAST_ANSWERED_AT
                    + ", now())) / 86400 + 1) * (1 + ln(1 + " + LIKES + ")))"
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

    // What Coach's Corner can filter by. Each filter has an "any" flag rather than a NULL check,
    // so PostgreSQL never has to guess the type of a NULL parameter. A muscle matches any of the
    // question's concepts, not only its main one.
    private static final String CURATED_FILTER = """
             WHERE (:any_author OR q.created_by = :created_by)
               AND (:any_concept OR EXISTS (
                       SELECT 1 FROM question_concept f
                       WHERE f.question_id = q.id AND f.concept_tag = :concept_tag))
               AND (:any_title OR q.title ILIKE :title_pattern ESCAPE '\\')
            """;

    // Newest first, like "my questions".
    private static final String FIND_CURATED =
            SELECT_WITH_CONCEPT_TAGS + CURATED_FILTER + GROUP_BY_QUESTION
                    + " ORDER BY q.id DESC LIMIT :limit OFFSET :offset";

    private static final String COUNT_CURATED = "SELECT COUNT(*) FROM question q " + CURATED_FILTER;

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

    // Declined links are never removed: re-adding the concept then hits the kept row (ON CONFLICT
    // DO NOTHING above), so an author can't undo an owner's decline by removing and re-adding it.
    private static final String DELETE_REMOVED_CONCEPT_LINKS = """
            DELETE FROM question_concept
            WHERE question_id = :question_id
              AND concept_tag NOT IN (:concept_tags)
              AND link_status <> 'DECLINED'
            """;

    private static final String INSERT_REPORT = """
            INSERT INTO question_report (question_id, reported_by, reason, note)
            VALUES (:question_id, :reported_by, :reason, :note)
            RETURNING id
            """;

    private static final String SELECT_REPORTS = """
            SELECT r.id, r.question_id, r.reported_by, r.reason, r.note, r.report_status,
                   r.resolved_by, r.resolved_at, r.reported_at
            FROM question_report r
            """;

    private static final String FIND_REPORT_BY_ID = SELECT_REPORTS + " WHERE r.id = :id";

    // Oldest first, like reviews: whoever waited longest is looked at first.
    private static final String ORDER_REPORTS_OLDEST_FIRST = " ORDER BY r.reported_at, r.id";

    // Decided ones show the latest decision first, and only the latest few: a history, not a queue.
    private static final String ORDER_REPORTS_NEWEST_DECISION_FIRST =
            " ORDER BY r.resolved_at DESC, r.id DESC LIMIT :limit";

    // Reports a user may see and decide: on questions they wrote, or that are linked to a concept
    // they own, accepted or not.
    private static final String REPORTS_VISIBLE_TO = """
             JOIN question q ON q.id = r.question_id
            WHERE (q.created_by = :user_id
                   OR EXISTS (SELECT 1
                              FROM question_concept qc
                              JOIN concept c ON c.tag = qc.concept_tag
                              WHERE qc.question_id = q.id
                                AND c.created_by = :user_id))
            """;

    private static final String FIND_OPEN_REPORTS =
            SELECT_REPORTS + " WHERE r.report_status = 'OPEN'" + ORDER_REPORTS_OLDEST_FIRST;

    private static final String FIND_OPEN_REPORTS_VISIBLE_TO =
            SELECT_REPORTS + REPORTS_VISIBLE_TO + " AND r.report_status = 'OPEN'" + ORDER_REPORTS_OLDEST_FIRST;

    private static final String FIND_DECIDED_REPORTS =
            SELECT_REPORTS + " WHERE r.report_status <> 'OPEN'" + ORDER_REPORTS_NEWEST_DECISION_FIRST;

    private static final String FIND_DECIDED_REPORTS_VISIBLE_TO =
            SELECT_REPORTS + REPORTS_VISIBLE_TO + " AND r.report_status <> 'OPEN'" + ORDER_REPORTS_NEWEST_DECISION_FIRST;

    // Open reports have no resolved_at (chk_question_report_open_undecided), so they come first.
    private static final String FIND_REPORTS_BY_QUESTION = SELECT_REPORTS + """
             WHERE r.question_id = :question_id
            ORDER BY r.resolved_at DESC NULLS FIRST, r.reported_at, r.id
            """;

    private static final String HAS_OPEN_REPORT = """
            SELECT EXISTS (
                SELECT 1
                FROM question_report
                WHERE question_id = :question_id
                  AND reported_by = :reported_by
                  AND report_status = 'OPEN'
            )
            """;

    private static final String FIND_OPEN_REPORT = SELECT_REPORTS + """
             WHERE r.question_id = :question_id
              AND r.reported_by = :reported_by
              AND r.report_status = 'OPEN'
            """;

    private static final String UPDATE_OPEN_REPORT_CONTENT = """
            UPDATE question_report
            SET reason = :reason,
                note = :note
            WHERE id = :id
              AND report_status = 'OPEN'
            """;

    private static final String DELETE_OPEN_REPORT = "DELETE FROM question_report WHERE id = :id AND report_status = 'OPEN'";

    private static final String DECIDE_OPEN_REPORT = """
            UPDATE question_report
            SET report_status = :status,
                resolved_by = :resolved_by,
                resolved_at = now()
            WHERE id = :id
              AND report_status = 'OPEN'
            """;

    // One vote per learner and question: voting again replaces it.
    private static final String SAVE_VOTE = """
            INSERT INTO question_vote (question_id, user_id, vote)
            VALUES (:question_id, :user_id, :vote)
            ON CONFLICT (question_id, user_id)
            DO UPDATE SET vote = EXCLUDED.vote, voted_at = now()
            """;

    private static final String FIND_VOTE = "SELECT vote FROM question_vote WHERE question_id = :question_id AND user_id = :user_id";

    private static final String DELETE_VOTE = """
            DELETE FROM question_vote
            WHERE question_id = :question_id
              AND user_id = :user_id
            """;

    private static final String COUNT_VOTES = """
            SELECT question_id,
                   count(*) FILTER (WHERE vote = 'UP') AS up,
                   count(*) FILTER (WHERE vote = 'DOWN') AS down
            FROM question_vote
            WHERE question_id IN (:ids)
            GROUP BY question_id
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
    public List<Question> findCurated(Long createdBy, String conceptTag, String titleSearch, int limit, long offset) {
        final MapSqlParameterSource params = curatedFilter(createdBy, conceptTag, titleSearch)
                .addValue("limit", limit)
                .addValue("offset", offset);
        return jdbcTemplate.query(FIND_CURATED, params, this::mapQuestion);
    }

    @Override
    public long countCurated(Long createdBy, String conceptTag, String titleSearch) {
        Long count = jdbcTemplate.queryForObject(
                COUNT_CURATED, curatedFilter(createdBy, conceptTag, titleSearch), Long.class);
        return count == null ? 0 : count;
    }

    private static MapSqlParameterSource curatedFilter(Long createdBy, String conceptTag, String titleSearch) {
        return new MapSqlParameterSource()
                .addValue("any_author", createdBy == null)
                .addValue("created_by", createdBy == null ? 0L : createdBy)
                .addValue("any_concept", conceptTag == null)
                .addValue("concept_tag", conceptTag == null ? "" : conceptTag)
                .addValue("any_title", titleSearch == null)
                .addValue("title_pattern", titleSearch == null ? "" : "%" + escapeLike(titleSearch) + "%");
    }

    /** Makes \, % and _ match themselves in an ILIKE pattern with ESCAPE '\'. */
    private static String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
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

    @Override
    public Report insertReport(Report report) {
        Long id = jdbcTemplate.queryForObject(
                INSERT_REPORT,
                new MapSqlParameterSource()
                        .addValue("question_id", report.getQuestion().getId())
                        .addValue("reported_by", report.getReportedBy())
                        .addValue("reason", report.getReason().name())
                        .addValue("note", report.getNote()),
                Long.class);

        return findReportById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Report was inserted but could not be retrieved"));
    }

    @Override
    public Optional<Report> findReportById(Long id) {
        return reports(FIND_REPORT_BY_ID, Map.of("id", id)).stream().findFirst();
    }

    @Override
    public boolean hasOpenReport(Long questionId, Long reportedBy) {
        Boolean open = jdbcTemplate.queryForObject(
                HAS_OPEN_REPORT,
                Map.of("question_id", questionId, "reported_by", reportedBy),
                Boolean.class);

        return Boolean.TRUE.equals(open);
    }

    @Override
    public Optional<Report> findOpenReport(Long questionId, Long reportedBy) {
        return reports(FIND_OPEN_REPORT, Map.of("question_id", questionId, "reported_by", reportedBy))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Report> updateOpenReportContent(Long id, ReportReason reason, String note) {
        final int updated = jdbcTemplate.update(
                UPDATE_OPEN_REPORT_CONTENT,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("reason", reason.name())
                        .addValue("note", note));

        return updated == 0 ? Optional.empty() : findReportById(id);
    }

    @Override
    public boolean deleteOpenReport(Long id) {
        return jdbcTemplate.update(DELETE_OPEN_REPORT, Map.of("id", id)) > 0;
    }

    @Override
    public List<Report> findOpenReports() {
        return reports(FIND_OPEN_REPORTS, Map.of());
    }

    @Override
    public List<Report> findOpenReportsVisibleTo(Long userId) {
        return reports(FIND_OPEN_REPORTS_VISIBLE_TO, Map.of("user_id", userId));
    }

    @Override
    public List<Report> findDecidedReports(int limit) {
        return reports(FIND_DECIDED_REPORTS, Map.of("limit", limit));
    }

    @Override
    public List<Report> findDecidedReportsVisibleTo(Long userId, int limit) {
        return reports(FIND_DECIDED_REPORTS_VISIBLE_TO, Map.of("user_id", userId, "limit", limit));
    }

    @Override
    public List<Report> findReportsByQuestion(Long questionId) {
        return reports(FIND_REPORTS_BY_QUESTION, Map.of("question_id", questionId));
    }

    @Override
    public Optional<Report> decideOpenReport(Long id, ReportStatus status, Long resolvedBy) {
        final int updated = jdbcTemplate.update(
                DECIDE_OPEN_REPORT,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("status", status.name())
                        .addValue("resolved_by", resolvedBy));

        return updated == 0 ? Optional.empty() : findReportById(id);
    }

    @Override
    public void saveVote(Long questionId, Long userId, Vote vote) {
        jdbcTemplate.update(
                SAVE_VOTE,
                Map.of("question_id", questionId, "user_id", userId, "vote", vote.name()));
    }

    @Override
    public Optional<Vote> findVote(Long questionId, Long userId) {
        return jdbcTemplate.queryForList(FIND_VOTE, Map.of("question_id", questionId, "user_id", userId), String.class)
                .stream()
                .findFirst()
                .map(Vote::valueOf);
    }

    @Override
    public void deleteVote(Long questionId, Long userId) {
        jdbcTemplate.update(
                DELETE_VOTE,
                Map.of("question_id", questionId, "user_id", userId));
    }

    @Override
    public Map<Long, VoteTotals> countVotes(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Map.of();
        }

        return jdbcTemplate.query(
                        COUNT_VOTES,
                        Map.of("ids", questionIds),
                        (resultSet, rowNumber) -> Map.entry(
                                resultSet.getLong("question_id"),
                                new VoteTotals(resultSet.getInt("up"), resultSet.getInt("down"))))
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Two round trips instead of one per report, as for links: the reports, then their
     * questions in a single query.
     */
    private List<Report> reports(String sql, Map<String, ?> parameters) {
        List<Report> reports = jdbcTemplate.query(sql, parameters, this::mapReport);

        if (reports.isEmpty()) {
            return List.of();
        }

        Map<Long, Question> questionsById = jdbcTemplate.query(
                        FIND_BY_IDS,
                        Map.of("ids", reports.stream().map(report -> report.getQuestion().getId()).distinct().toList()),
                        this::mapQuestion)
                .stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

        return reports.stream()
                .map(report -> report.toBuilder()
                        .question(questionsById.get(report.getQuestion().getId()))
                        .build())
                .toList();
    }

    /** The report with a stand-in question that only carries its id; reports() loads the rest. */
    private Report mapReport(ResultSet resultSet, int rowNumber) throws SQLException {
        Timestamp resolvedAt = resultSet.getTimestamp("resolved_at");
        return Report.builder()
                .id(resultSet.getLong("id"))
                .question(Question.builder().id(resultSet.getLong("question_id")).build())
                .reportedBy(resultSet.getLong("reported_by"))
                .reason(ReportReason.valueOf(resultSet.getString("reason")))
                .note(resultSet.getString("note"))
                .status(ReportStatus.valueOf(resultSet.getString("report_status")))
                .resolvedBy(resultSet.getObject("resolved_by", Long.class))
                .resolvedAt(resolvedAt == null ? null : resolvedAt.toInstant())
                .reportedAt(resultSet.getTimestamp("reported_at").toInstant())
                .build();
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
