package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.answer.AnswerRepository;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnswerJdbcRepository implements AnswerRepository {

    private static final String SELECT_ANSWER = """
            SELECT id, user_id, question_id, answer_text, question_snapshot,
                   self_grade, perceived_difficulty, answered_at
            FROM answer
            """;

    private static final String FIND_BY_ID = SELECT_ANSWER + " WHERE id = :id";

    // Newest first: the latest workout matters most.
    private static final String FIND_BY_USER =
            SELECT_ANSWER + " WHERE user_id = :user_id ORDER BY answered_at DESC, id DESC";

    private static final String FIND_BY_USER_AND_QUESTION = SELECT_ANSWER
            + " WHERE user_id = :user_id AND question_id = :question_id ORDER BY answered_at DESC, id DESC";

    private static final String INSERT = """
            INSERT INTO answer (user_id, question_id, answer_text, question_snapshot)
            VALUES (:user_id, :question_id, :answer_text, CAST(:question_snapshot AS jsonb))
            RETURNING id
            """;

    private static final String UPDATE_ASSESSMENT = """
            UPDATE answer
            SET self_grade = :self_grade,
                perceived_difficulty = :perceived_difficulty
            WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Answer insert(Answer answer) {
        Long id = jdbcTemplate.queryForObject(
                INSERT,
                new MapSqlParameterSource()
                        .addValue("user_id", answer.getUserId())
                        .addValue("question_id", answer.getQuestionId())
                        .addValue("answer_text", answer.getAnswer())
                        .addValue("question_snapshot", serializeSnapshot(answer.getQuestion())),
                Long.class);

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Answer was inserted but could not be retrieved"));
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, Map.of("id", id), this::mapAnswer)
                .stream()
                .findFirst();
    }

    @Override
    public List<Answer> findByUser(Long userId) {
        return jdbcTemplate.query(FIND_BY_USER, Map.of("user_id", userId), this::mapAnswer);
    }

    @Override
    public List<Answer> findByUserAndQuestion(Long userId, Long questionId) {
        return jdbcTemplate.query(
                FIND_BY_USER_AND_QUESTION,
                Map.of("user_id", userId, "question_id", questionId),
                this::mapAnswer);
    }

    @Override
    public Answer updateAssessment(Long id, Integer selfGrade, Integer perceivedDifficulty) {
        jdbcTemplate.update(
                UPDATE_ASSESSMENT,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("self_grade", selfGrade)
                        .addValue("perceived_difficulty", perceivedDifficulty));

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Answer was assessed but could not be retrieved"));
    }

    private Answer mapAnswer(ResultSet resultSet, int rowNumber) throws SQLException {
        return Answer.builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .questionId(resultSet.getLong("question_id"))
                .answer(resultSet.getString("answer_text"))
                .question(deserializeSnapshot(resultSet.getString("question_snapshot")))
                .selfGrade(resultSet.getObject("self_grade", Integer.class))
                .perceivedDifficulty(resultSet.getObject("perceived_difficulty", Integer.class))
                .answeredAt(resultSet.getTimestamp("answered_at").toInstant())
                .build();
    }

    // The snapshot is written and read field by field, so the stored JSON doesn't change
    // shape when the Question class does.

    private String serializeSnapshot(Question question) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", question.getId());
        snapshot.put("title", question.getTitle());
        snapshot.put("requirement", question.getRequirement());
        snapshot.put("sampleSolution", question.getSampleSolution());
        snapshot.put("difficultyScore", question.getDifficultyScore());
        snapshot.put("source", question.getSource());
        snapshot.put("conceptTags", question.getConceptTags());
        snapshot.put("status", question.getStatus().name());
        snapshot.put("createdBy", question.getCreatedBy());
        snapshot.put("updatedBy", question.getUpdatedBy());
        snapshot.put("createdAt", question.getCreatedAt().toString());
        snapshot.put("updatedAt", question.getUpdatedAt().toString());
        return objectMapper.writeValueAsString(snapshot);
    }

    @SuppressWarnings("unchecked")
    private Question deserializeSnapshot(String json) {
        Map<String, Object> snapshot = objectMapper.readValue(json, new TypeReference<>() {
        });
        return Question.builder()
                .id(((Number) snapshot.get("id")).longValue())
                .title((String) snapshot.get("title"))
                .requirement((String) snapshot.get("requirement"))
                .sampleSolution((String) snapshot.get("sampleSolution"))
                .difficultyScore(((Number) snapshot.get("difficultyScore")).intValue())
                .source((String) snapshot.get("source"))
                .conceptTags(List.copyOf((List<String>) snapshot.get("conceptTags")))
                .status(QuestionStatus.valueOf((String) snapshot.get("status")))
                .createdBy(((Number) snapshot.get("createdBy")).longValue())
                .updatedBy(((Number) snapshot.get("updatedBy")).longValue())
                .createdAt(Instant.parse((String) snapshot.get("createdAt")))
                .updatedAt(Instant.parse((String) snapshot.get("updatedAt")))
                .build();
    }
}
