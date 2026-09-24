package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
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

@Repository
@RequiredArgsConstructor
public class QuestionJdbcRepository implements QuestionRepository {

    // Aggregates each question's concept tags into a single array column in the same
    // round trip, instead of one extra query per question (N+1).
    private static final String SELECT_WITH_CONCEPT_TAGS = """
            SELECT q.id, q.requirement, q.sample_solution, q.question_type, q.difficulty_score,
                   q.expected_duration_minutes, q.question_status, q.source,
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
            GROUP BY q.id, q.requirement, q.sample_solution, q.question_type, q.difficulty_score,
                     q.expected_duration_minutes, q.question_status, q.source,
                     q.created_by, q.updated_by, q.created_at, q.updated_at
            """;

    private static final String FIND_BY_ID =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.id = :id " + GROUP_BY_QUESTION;

    // Oldest first: backs the review queue, where whoever waited longest goes first.
    private static final String FIND_BY_STATUS =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.question_status = :status " + GROUP_BY_QUESTION + " ORDER BY q.id";

    // Newest first: backs "my questions", where the latest submission matters most.
    private static final String FIND_BY_SUBMITTED_BY =
            SELECT_WITH_CONCEPT_TAGS + " WHERE q.created_by = :created_by " + GROUP_BY_QUESTION + " ORDER BY q.id DESC";

    private static final String INSERT = """
            INSERT INTO question (
                requirement, sample_solution, question_type, difficulty_score,
                expected_duration_minutes, question_status, source, created_by, updated_by
            )
            VALUES (
                :requirement, :sample_solution, :question_type, :difficulty_score,
                :expected_duration_minutes, :question_status, :source, :created_by, :created_by
            )
            RETURNING id
            """;

    private static final String UPDATE = """
            UPDATE question
            SET requirement = :requirement,
                sample_solution = :sample_solution,
                question_type = :question_type,
                difficulty_score = :difficulty_score,
                expected_duration_minutes = :expected_duration_minutes,
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

    private static final String INSERT_CONCEPT_LINK = """
            INSERT INTO question_concept (question_id, concept_tag)
            VALUES (:question_id, :concept_tag)
            """;

    private static final String DELETE_CONCEPT_LINKS = """
            DELETE FROM question_concept
            WHERE question_id = :question_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Question insert(Question question) {
        Long id = jdbcTemplate.queryForObject(
                INSERT,
                insertParameters(question),
                Long.class);

        linkConceptTags(id, question.getConceptTags());

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question was inserted but could not be retrieved"));
    }

    @Override
    @Transactional
    public Question update(Long id, Question question) {
        jdbcTemplate.update(
                UPDATE,
                updateParameters(id, question));

        // Full replace: simplest way to keep question_concept consistent with the
        // submitted tag set over plain JDBC, at the cost of a delete + re-insert per update.
        jdbcTemplate.update(
                DELETE_CONCEPT_LINKS,
                Map.of("question_id", id));
        linkConceptTags(id, question.getConceptTags());

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question was updated but could not be retrieved"));
    }

    @Override
    public Question updateStatus(Long id, QuestionStatus status, String updatedBy) {
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
    public Optional<Question> findById(Long id) {
        List<Question> questions = jdbcTemplate.query(
                FIND_BY_ID,
                Map.of("id", id),
                this::mapQuestion);

        return questions.stream().findFirst();
    }

    @Override
    public List<Question> findBySubmittedBy(String userId) {
        return jdbcTemplate.query(
                FIND_BY_SUBMITTED_BY,
                Map.of("created_by", userId),
                this::mapQuestion);
    }

    @Override
    public List<Question> findByStatus(QuestionStatus status) {
        return jdbcTemplate.query(
                FIND_BY_STATUS,
                Map.of("status", status.name()),
                this::mapQuestion);
    }

    private void linkConceptTags(Long questionId, List<String> conceptTags) {
        SqlParameterSource[] linkParameters = conceptTags.stream()
                .map(conceptTag -> new MapSqlParameterSource()
                        .addValue("question_id", questionId)
                        .addValue("concept_tag", conceptTag))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(INSERT_CONCEPT_LINK, linkParameters);
    }

    private MapSqlParameterSource insertParameters(Question question) {
        return new MapSqlParameterSource()
                .addValue("requirement", question.getRequirement())
                .addValue("sample_solution", question.getSampleSolution())
                .addValue("question_type", question.getType().name())
                .addValue("difficulty_score", question.getDifficultyScore())
                .addValue("expected_duration_minutes", question.getExpectedDurationMinutes())
                .addValue("question_status", question.getStatus().name())
                .addValue("source", question.getSource())
                .addValue("created_by", question.getCreatedBy());
    }

    private MapSqlParameterSource updateParameters(Long id, Question question) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("requirement", question.getRequirement())
                .addValue("sample_solution", question.getSampleSolution())
                .addValue("question_type", question.getType().name())
                .addValue("difficulty_score", question.getDifficultyScore())
                .addValue("expected_duration_minutes", question.getExpectedDurationMinutes())
                .addValue("source", question.getSource())
                .addValue("updated_by", question.getUpdatedBy());
    }

    private Question mapQuestion(ResultSet resultSet, int rowNumber) throws SQLException {
        return Question.builder()
                .id(resultSet.getLong("id"))
                .requirement(resultSet.getString("requirement"))
                .sampleSolution(resultSet.getString("sample_solution"))
                .type(QuestionType.valueOf(resultSet.getString("question_type")))
                .difficultyScore(resultSet.getInt("difficulty_score"))
                .expectedDurationMinutes(resultSet.getInt("expected_duration_minutes"))
                .status(QuestionStatus.valueOf(resultSet.getString("question_status")))
                .source(resultSet.getString("source"))
                .createdBy(resultSet.getString("created_by"))
                .updatedBy(resultSet.getString("updated_by"))
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
