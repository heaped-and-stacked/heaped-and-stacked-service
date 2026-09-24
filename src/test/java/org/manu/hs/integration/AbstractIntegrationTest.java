package org.manu.hs.integration;

import org.junit.jupiter.api.AfterEach;
import org.manu.hs.application.common.MutableCurrentUserProvider;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base for integration tests: the real application on a random port, a real Postgres
 * container, and RestTestClient for HTTP calls.
 * <p>
 * Each subclass covers ONE endpoint. A test arranges the database directly with the
 * helpers below (independent of the API under test), calls that single endpoint, then
 * checks the response and the database. Every table is emptied after each test.
 * <p>
 * All subclasses share the same configuration, so Spring starts the application and
 * the container once and reuses them across test classes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final String AUTHOR = "author-1";
    protected static final String OTHER_USER = "other-user";
    protected static final String ADMIN = "admin-1";

    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    protected RestTestClient restClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MutableCurrentUserProvider currentUserProvider;

    @AfterEach
    void cleanDatabase() {
        // question_concept is emptied by CASCADE, as it references both tables.
        jdbcTemplate.execute("TRUNCATE TABLE concept, question RESTART IDENTITY CASCADE");
        currentUserProvider.reset();
    }

    /**
     * Switches the caller identity used by any endpoint that reads CurrentUserProvider.
     * Takes effect immediately; no request is needed to apply it.
     */
    protected void actingAs(String userId, boolean admin) {
        currentUserProvider.actingAs(userId, admin);
    }

    // ---------------------------------------------------------------------
    // Concept
    // ---------------------------------------------------------------------

    /** Inserts an ACTIVE concept created by {@link #AUTHOR}: the usual background for question tests. */
    protected void insertConcept(String tag) {
        insertConcept(tag, ConceptStatus.ACTIVE);
    }

    protected void insertConcept(String tag, ConceptStatus status) {
        insertConcept(tag, "Name of " + tag, "Description of " + tag, List.of(), status, AUTHOR);
    }

    protected void insertConcept(
            String tag,
            String name,
            String description,
            List<String> externalReferences) {

        insertConcept(tag, name, description, externalReferences, ConceptStatus.ACTIVE, AUTHOR);
    }

    protected void insertConcept(
            String tag,
            String name,
            String description,
            List<String> externalReferences,
            ConceptStatus status,
            String createdBy) {

        jdbcTemplate.update(
                """
                        INSERT INTO concept (
                            tag,
                            name,
                            description,
                            external_references,
                            concept_status,
                            created_by,
                            updated_by
                        )
                        VALUES (?, ?, ?, CAST(? AS jsonb), ?, ?, ?)
                        """,
                tag,
                name,
                description,
                objectMapper.writeValueAsString(externalReferences),
                status.name(),
                createdBy,
                createdBy);
    }

    protected Concept findConceptByTag(String tag) {
        return jdbcTemplate.queryForObject(
                """
                        SELECT tag, name, description, external_references, concept_status,
                               created_by, updated_by
                        FROM concept
                        WHERE tag = ?
                        """,
                (resultSet, rowNum) -> Concept.builder()
                        .tag(resultSet.getString("tag"))
                        .name(resultSet.getString("name"))
                        .description(resultSet.getString("description"))
                        .externalReferences(
                                objectMapper.readValue(
                                        resultSet.getString("external_references"),
                                        new TypeReference<>() {
                                        }))
                        .status(ConceptStatus.valueOf(resultSet.getString("concept_status")))
                        .createdBy(resultSet.getString("created_by"))
                        .updatedBy(resultSet.getString("updated_by"))
                        .build(),
                tag);
    }

    protected void assertConceptDoesNotExist(String tag) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM concept WHERE tag = ?",
                Integer.class,
                tag);

        assertEquals(0, count);
    }

    // ---------------------------------------------------------------------
    // Question
    // ---------------------------------------------------------------------

    /**
     * Inserts a question with its concept links and returns the generated id.
     * The concepts must exist first ({@link #insertConcept}).
     * <p>
     * Other fields get fixed values: requirement "Original requirement", sample solution
     * "Original solution", type DEFINITION, difficulty 2, duration 5, source "manu".
     */
    protected Long insertQuestion(QuestionStatus status, String createdBy, String... conceptTags) {
        Long questionId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO question (
                            requirement,
                            sample_solution,
                            question_type,
                            difficulty_score,
                            expected_duration_minutes,
                            question_status,
                            source,
                            created_by,
                            updated_by
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                "Original requirement",
                "Original solution",
                QuestionType.DEFINITION.name(),
                2,
                5,
                status.name(),
                "manu",
                createdBy,
                createdBy);

        for (String conceptTag : conceptTags) {
            jdbcTemplate.update(
                    "INSERT INTO question_concept (question_id, concept_tag) VALUES (?, ?)",
                    questionId,
                    conceptTag);
        }

        return questionId;
    }

    protected Question findQuestionById(Long id) {
        return jdbcTemplate.queryForObject(
                """
                        SELECT id, requirement, sample_solution, question_type, difficulty_score,
                               expected_duration_minutes, question_status, source,
                               created_by, updated_by, created_at, updated_at
                        FROM question
                        WHERE id = ?
                        """,
                (resultSet, rowNum) -> Question.builder()
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
                        .build(),
                id);
    }

    protected List<String> findConceptTagsForQuestion(Long questionId) {
        return jdbcTemplate.query(
                """
                        SELECT concept_tag
                        FROM question_concept
                        WHERE question_id = ?
                        ORDER BY concept_tag
                        """,
                (resultSet, rowNum) -> resultSet.getString("concept_tag"),
                questionId);
    }

    protected int countQuestions() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM question", Integer.class);
    }

    protected void assertQuestionDoesNotExist(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM question WHERE id = ?",
                Integer.class,
                id);

        assertEquals(0, count);
    }

    /**
     * A valid question body linked to the given concepts. Use it when the payload is not
     * what the test is about (permissions, not-found, ...); write the JSON out in full when
     * the payload itself is under test.
     */
    protected static String validQuestionJson(String... conceptTags) {
        String tags = Arrays.stream(conceptTags)
                .map(tag -> "\"" + tag + "\"")
                .collect(Collectors.joining(", "));

        return """
                {
                    "requirement": "Updated requirement",
                    "sampleSolution": "Updated solution",
                    "type": "GOTCHA",
                    "difficultyScore": 4,
                    "expectedDurationMinutes": 8,
                    "source": "manu",
                    "conceptTags": [%s]
                }
                """.formatted(tags);
    }
}
