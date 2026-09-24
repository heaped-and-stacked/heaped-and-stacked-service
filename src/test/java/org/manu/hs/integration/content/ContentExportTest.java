package org.manu.hs.integration.content;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The content export (seed/export-content.sql): it reads an environment's database and writes the
 * seed a new environment starts from. Here: a source database with content and people, exported,
 * then loaded onto a fresh one, the way local → test and test → production go. No Spring: Flyway,
 * and the files as psql runs them.
 */
@Testcontainers
class ContentExportTest {

    private static final Path EXPORT = Path.of("seed", "export-content.sql");

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    private static DriverManagerDataSource source;
    private static DriverManagerDataSource target;

    @BeforeAll
    static void createDatabases() throws SQLException {
        try (Connection connection = postgres.createConnection("");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE source");
            statement.execute("CREATE DATABASE target");
        }
        source = migrated("source");
        target = migrated("target");
    }

    @Test
    @DisplayName("""
            Given a database with the gym's muscles and exercises, a member's featured muscle, a
            community muscle, a benched exercise, links waiting for approval, verifications by staff,
            and people's accounts, sets, votes and feedback
            When it's exported and the export runs on a fresh database, and then runs a second time
            Then the fresh database holds the featured muscles and their live exercises, in the same
            order, every one owned by the house account; a verified exercise is verified by the house
            account, at the same time; only accepted links on featured muscles travel; text comes
            through exactly; nothing personal, community or benched travels; the second run is refused
            """)
    void exportCarriesTheGymsContentAndNothingPersonal() throws Exception {
        // Given
        JdbcTemplate from = new JdbcTemplate(source);
        from.execute("""
                INSERT INTO app_user (id, display_name, user_role, user_status) VALUES
                    (1, 'Heaped & Stacked', 'STAFF', 'ACTIVE'),
                    (2, 'A curator', 'STAFF', 'ACTIVE'),
                    (3, 'A member', 'MEMBER', 'ACTIVE');
                INSERT INTO user_identity (user_id, provider, provider_user_id) VALUES (2, 'GITHUB', '42');
                INSERT INTO concept (tag, name, description, external_references, concept_status,
                                     contribution_policy, featured, created_by, updated_by) VALUES
                    ('java-core', 'Core Java', 'The language.', '["https://openjdk.org"]', 'ACTIVE', 'OWNER_APPROVAL', TRUE, 1, 1),
                    ('member-katas', 'Katas', 'A member''s, featured.', '[]', 'ACTIVE', 'AUTO_ACCEPT', TRUE, 3, 3),
                    ('my-katas', 'My katas', 'A member''s own.', '[]', 'ACTIVE', 'AUTO_ACCEPT', FALSE, 3, 3);
                INSERT INTO question (id, title, requirement, sample_solution, difficulty_score, question_status,
                                      source, created_by, updated_by, verified_by, verified_at) VALUES
                    (10, 'Verified', E'It''s $1 and $$ here,\\nwith ```code```', 'Answer', 4, 'SUBMITTED',
                         'A book', 1, 1, 2, '2026-10-20T10:00:00Z'),
                    (11, 'Unverified', 'Requirement', 'Answer', 3, 'SUBMITTED', NULL, 3, 3, NULL, NULL),
                    (12, 'Benched', 'Requirement', 'Answer', 3, 'RETIRED', NULL, 1, 1, NULL, NULL),
                    (13, 'Community only', 'Requirement', 'Answer', 3, 'SUBMITTED', NULL, 3, 3, NULL, NULL),
                    (14, 'Still waiting', 'Requirement', 'Answer', 3, 'SUBMITTED', NULL, 3, 3, NULL, NULL);
                INSERT INTO question_concept (question_id, concept_tag, link_status) VALUES
                    (10, 'java-core', 'ACCEPTED'),
                    (11, 'java-core', 'ACCEPTED'), (11, 'member-katas', 'PENDING'),
                    (12, 'java-core', 'ACCEPTED'),
                    (13, 'my-katas', 'ACCEPTED'),
                    (14, 'java-core', 'PENDING');
                """);
        from.update("""
                INSERT INTO answer (user_id, question_id, answer_text, question_snapshot)
                VALUES (3, 10, 'My answer', '{}'::jsonb)
                """);

        // When
        String export = exportFrom(source);
        runScript(target, export);

        // Then
        JdbcTemplate to = new JdbcTemplate(target);
        assertEquals(List.of("Heaped & Stacked"), to.queryForList("SELECT display_name FROM app_user", String.class));
        Long house = to.queryForObject("SELECT id FROM app_user", Long.class);
        assertEquals(0, count(to, "SELECT count(*) FROM user_identity"));
        assertEquals(List.of("java-core", "member-katas"),
                to.queryForList("SELECT tag FROM concept ORDER BY tag", String.class));
        assertEquals(0, count(to, "SELECT count(*) FROM concept WHERE NOT featured OR concept_status <> 'ACTIVE'"));
        assertEquals(0, count(to, "SELECT count(*) FROM concept WHERE created_by <> " + house));
        assertEquals("[\"https://openjdk.org\"]",
                to.queryForObject("SELECT external_references::text FROM concept WHERE tag = 'java-core'", String.class));
        assertEquals("OWNER_APPROVAL",
                to.queryForObject("SELECT contribution_policy FROM concept WHERE tag = 'java-core'", String.class));

        assertEquals(List.of("Verified", "Unverified"),
                to.queryForList("SELECT title FROM question ORDER BY id", String.class));
        assertEquals(0, count(to, "SELECT count(*) FROM question WHERE created_by <> " + house
                + " OR question_status <> 'SUBMITTED'"));
        assertEquals("It's $1 and $$ here,\nwith ```code```",
                to.queryForObject("SELECT requirement FROM question WHERE title = 'Verified'", String.class));
        assertEquals("A book", to.queryForObject("SELECT source FROM question WHERE title = 'Verified'", String.class));
        assertEquals(house, to.queryForObject("SELECT verified_by FROM question WHERE title = 'Verified'", Long.class));
        assertEquals("2026-10-20 10:00:00+00", to.queryForObject(
                "SELECT to_char(verified_at AT TIME ZONE 'UTC', 'YYYY-MM-DD HH24:MI:SS') || '+00' FROM question WHERE title = 'Verified'",
                String.class));
        assertNull(to.queryForObject("SELECT verified_by FROM question WHERE title = 'Unverified'", Long.class));
        assertEquals(List.of("Unverified:java-core", "Verified:java-core"), to.queryForList("""
                SELECT q.title || ':' || qc.concept_tag FROM question_concept qc
                JOIN question q ON q.id = qc.question_id
                WHERE qc.link_status = 'ACCEPTED'
                ORDER BY 1
                """, String.class));
        assertEquals(0, count(to, "SELECT count(*) FROM question_concept WHERE link_status <> 'ACCEPTED'"));
        assertEquals(0, count(to, "SELECT count(*) FROM answer"));

        // When / Then: a second run is refused, and nothing is loaded twice
        assertThrows(SQLException.class, () -> runScript(target, export));
        assertEquals(2, count(to, "SELECT count(*) FROM question"));
    }

    private static DriverManagerDataSource migrated(String database) {
        String url = postgres.getJdbcUrl().replaceFirst("/[^/?]+(\\?|$)", "/" + database + "$1");
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, postgres.getUsername(), postgres.getPassword());
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .placeholderReplacement(false)
                .load()
                .migrate();
        return dataSource;
    }

    /** The export's rows, one line each, as psql -At prints them. */
    private static String exportFrom(DriverManagerDataSource dataSource) throws Exception {
        List<String> lines = new JdbcTemplate(dataSource).queryForList(Files.readString(EXPORT), String.class);
        return String.join("\n", lines) + "\n";
    }

    /** The whole script at once, as psql runs it: it brings its own transaction. */
    private static void runScript(DriverManagerDataSource dataSource, String script) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(script);
        }
    }

    private static int count(JdbcTemplate jdbc, String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
