package org.manu.hs.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.security.SignedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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

    // Users inserted before every test, with fixed ids far away from the ids the
    // application generates itself (which restart at 1 for every test).
    protected static final Long AUTHOR = 1001L;      // a member
    protected static final Long OTHER_USER = 1002L;  // another member
    protected static final Long ADMIN = 1003L;       // an admin

    private static final String SESSION_COOKIE = "SESSION";
    private static final String SECURITY_CONTEXT_ATTRIBUTE = "SPRING_SECURITY_CONTEXT";

    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    /** Signed in as {@link #AUTHOR} at the start of every test; see {@link #actingAs}. */
    protected RestTestClient restClient;

    @Autowired
    private RestTestClient baseRestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SessionRepository<? extends Session> sessionRepository;
    @Autowired
    private UserApplication userApplication;

    private String sessionId;

    /**
     * Every test starts with the three users above and a real session, signed in as
     * {@link #AUTHOR}. The session and its CSRF token are sent with every request.
     */
    @BeforeEach
    void startSignedInSession() {
        insertUser(AUTHOR, UserRole.MEMBER);
        insertUser(OTHER_USER, UserRole.MEMBER);
        insertUser(ADMIN, UserRole.ADMIN);

        final OpenSession session = openSession();
        restClient = session.client;
        sessionId = session.id;
        actingAs(AUTHOR);
    }

    @AfterEach
    void cleanDatabase() {
        // CASCADE also empties question_concept, user_identity and the session attributes.
        jdbcTemplate.execute("TRUNCATE TABLE concept, question, app_user, spring_session RESTART IDENTITY CASCADE");
    }

    // ---------------------------------------------------------------------
    // Signing in
    // ---------------------------------------------------------------------

    /**
     * Signs the given user in on this test's session, exactly as a GitHub sign-in would:
     * the session holds a SignedInUser with the role from the user's row. Takes effect
     * on the next request.
     */
    protected void actingAs(Long userId) {
        storeInSession(sessionId, signedInContext(userId));
    }

    /** Signs out, keeping the session (and its CSRF token): the next request comes from a visitor. */
    protected void actingAsVisitor() {
        storeInSession(sessionId, null);
    }

    /**
     * Signs in with a GitHub account the way GitHub's callback does (registering the user
     * on their first sign-in), and returns a new "browser" signed in as that user.
     * Throws if the sign-in is refused, e.g. for a blocked account.
     */
    protected RestTestClient signInWithGitHub(String gitHubId, String gitHubLogin) {
        final User user = userApplication.signIn(SignInCommand.builder()
                .provider(AuthProvider.GITHUB)
                .providerUserId(gitHubId)
                .providerLogin(gitHubLogin)
                .build());

        return clientSignedInAs(user.getId());
    }

    /**
     * A second, independent "browser": a new session signed in as the given user. Use it
     * when a test needs two people signed in at once, e.g. an admin blocking a member
     * who is signed in elsewhere.
     */
    protected RestTestClient clientSignedInAs(Long userId) {
        final OpenSession session = openSession();
        storeInSession(session.id, signedInContext(userId));
        return session.client;
    }

    /** What a GitHub sign-in stores: a SignedInUser with the role from the user's row. */
    private SecurityContext signedInContext(Long userId) {
        final UserRole role = UserRole.valueOf(jdbcTemplate.queryForObject(
                "SELECT user_role FROM app_user WHERE id = ?",
                String.class,
                userId));
        final SignedInUser principal = new SignedInUser(userId, role);

        return new SecurityContextImpl(
                new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "github"));
    }

    /** Opens a session the way the UI does: by fetching a CSRF token. */
    private OpenSession openSession() {
        final EntityExchangeResult<Map<String, String>> result = baseRestClient
                .get()
                .uri("/csrf")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .returnResult();

        // Spring Session's cookie holds the session id, Base64-encoded.
        final String cookieValue = result.getResponseCookies().getFirst(SESSION_COOKIE).getValue();
        final String id = new String(Base64.getDecoder().decode(cookieValue), StandardCharsets.UTF_8);

        final Map<String, String> csrf = result.getResponseBody();
        final RestTestClient client = baseRestClient.mutate()
                .defaultCookie(SESSION_COOKIE, cookieValue)
                .defaultHeader(csrf.get("headerName"), csrf.get("token"))
                .build();

        return new OpenSession(client, id);
    }

    private void storeInSession(String id, SecurityContext context) {
        storeInSession(sessionRepository, id, context);
    }

    // Generic so the repository's own (non-public) session type never has to be named.
    private <S extends Session> void storeInSession(SessionRepository<S> repository, String id, SecurityContext context) {
        final S session = repository.findById(id);
        if (context == null) {
            session.removeAttribute(SECURITY_CONTEXT_ATTRIBUTE);
        } else {
            session.setAttribute(SECURITY_CONTEXT_ATTRIBUTE, context);
        }
        repository.save(session);
    }

    /** A client that sends a session's cookie and CSRF token, plus that session's id. */
    private static final class OpenSession {
        private final RestTestClient client;
        private final String id;

        private OpenSession(RestTestClient client, String id) {
            this.client = client;
            this.id = id;
        }
    }

    // ---------------------------------------------------------------------
    // User
    // ---------------------------------------------------------------------

    protected void insertUser(Long userId, UserRole role) {
        insertUser(userId, role, UserStatus.ACTIVE);
    }

    protected void insertUser(Long userId, UserRole role, UserStatus status) {
        jdbcTemplate.update(
                """
                        INSERT INTO app_user (id, display_name, avatar_url, user_role, user_status, deleted_at)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                userId,
                "User " + userId,
                "https://avatars.example.com/" + userId,
                role.name(),
                status.name(),
                status == UserStatus.DELETED ? Timestamp.from(Instant.now()) : null);
    }

    protected void insertIdentity(Long userId, AuthProvider provider, String providerUserId, String providerLogin) {
        jdbcTemplate.update(
                """
                        INSERT INTO user_identity (user_id, provider, provider_user_id, provider_login)
                        VALUES (?, ?, ?, ?)
                        """,
                userId,
                provider.name(),
                providerUserId,
                providerLogin);
    }

    protected UserIdentity findIdentity(AuthProvider provider, String providerUserId) {
        return jdbcTemplate.queryForObject(
                """
                        SELECT user_id, provider_login, last_login_at
                        FROM user_identity
                        WHERE provider = ? AND provider_user_id = ?
                        """,
                (resultSet, rowNum) -> UserIdentity.builder()
                        .userId(resultSet.getLong("user_id"))
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .providerLogin(resultSet.getString("provider_login"))
                        .lastLoginAt(resultSet.getTimestamp("last_login_at") == null
                                ? null
                                : resultSet.getTimestamp("last_login_at").toInstant())
                        .build(),
                provider.name(),
                providerUserId);
    }

    protected int countIdentitiesOf(Long userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_identity WHERE user_id = ?",
                Integer.class,
                userId);
    }

    protected User findUser(Long userId) {
        return jdbcTemplate.queryForObject(
                """
                        SELECT id, display_name, avatar_url, user_role, user_status, deleted_at
                        FROM app_user
                        WHERE id = ?
                        """,
                (resultSet, rowNum) -> User.builder()
                        .id(resultSet.getLong("id"))
                        .displayName(resultSet.getString("display_name"))
                        .avatarUrl(resultSet.getString("avatar_url"))
                        .role(UserRole.valueOf(resultSet.getString("user_role")))
                        .status(UserStatus.valueOf(resultSet.getString("user_status")))
                        .deletedAt(resultSet.getTimestamp("deleted_at") == null
                                ? null
                                : resultSet.getTimestamp("deleted_at").toInstant())
                        .build(),
                userId);
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
            Long createdBy) {

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
                        .createdBy(resultSet.getLong("created_by"))
                        .updatedBy(resultSet.getLong("updated_by"))
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
    protected Long insertQuestion(QuestionStatus status, Long createdBy, String... conceptTags) {
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
                        .createdBy(resultSet.getLong("created_by"))
                        .updatedBy(resultSet.getLong("updated_by"))
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
