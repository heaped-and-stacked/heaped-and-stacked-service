package org.manu.hs.integration.content;

import org.flywaydb.core.Flyway;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The launch content (seed/initial-content.sql, exported from a database by export-content.sql) on a fresh
 * database, the way a new environment starts: the schema migrations, then the seed, once. The
 * other integration tests run the schema only. Catches a generator or constraint mismatch here
 * instead of on a new server. No Spring: just Flyway, with the same settings as
 * application.properties, and the seed file as psql would run it.
 */
@Testcontainers
class LaunchContentSeedTest {

    private static final Path SEED = Path.of("seed", "initial-content.sql");

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    @Test
    @DisplayName("""
            Given a fresh database with the schema migrated
            When the launch seed runs, and then runs a second time
            Then the first run loads complete content: 16 published, featured muscles owned by the
            house account (which nobody can sign in as), and exercises that are all live, each
            accepted for at least one muscle; the second run is refused and changes nothing
            """)
    void launchSeedLoadsOnceOntoAFreshDatabase() throws Exception {
        // Given
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .placeholderReplacement(false)
                .load()
                .migrate();
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // When
        runSeed(dataSource);

        // Then
        assertEquals(1, count(jdbc, "SELECT count(*) FROM app_user WHERE display_name = 'Heaped & Stacked'"));
        assertEquals(0, count(jdbc, "SELECT count(*) FROM user_identity"));
        assertEquals(16, count(jdbc, "SELECT count(*) FROM concept WHERE concept_status = 'ACTIVE'"));
        assertEquals(16, count(jdbc, "SELECT count(*) FROM concept"));
        assertEquals(16, count(jdbc, "SELECT count(*) FROM concept WHERE featured"));
        // A floor, not the exact count: curation adds and cuts exercises, a broken seed loses most of them.
        int exercises = count(jdbc, "SELECT count(*) FROM question");
        assertTrue(exercises > 700);
        assertEquals(0, count(jdbc, "SELECT count(*) FROM question WHERE question_status <> 'SUBMITTED'"));
        assertEquals(0, count(jdbc, """
                SELECT count(*) FROM question q
                WHERE NOT EXISTS (SELECT 1 FROM question_concept qc WHERE qc.question_id = q.id)
                """));
        assertEquals(0, count(jdbc, "SELECT count(*) FROM question_concept WHERE link_status <> 'ACCEPTED'"));
        assertEquals(0, count(jdbc, """
                SELECT count(*) FROM question q JOIN app_user u ON u.id = q.created_by
                WHERE u.display_name <> 'Heaped & Stacked'
                """));

        // When / Then: a second run is refused, and nothing is loaded twice
        assertThrows(SQLException.class, () -> runSeed(dataSource));
        assertEquals(exercises, count(jdbc, "SELECT count(*) FROM question"));
    }

    /** The whole file as one script, as psql runs it: it brings its own transaction. */
    private static void runSeed(DriverManagerDataSource dataSource) throws Exception {
        String seed = Files.readString(SEED);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(seed);
        }
    }

    private static int count(JdbcTemplate jdbc, String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
