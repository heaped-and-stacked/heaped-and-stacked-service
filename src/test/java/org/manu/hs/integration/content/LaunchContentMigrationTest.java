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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The launch content (db/content/V2, generated from the muscle files) on a fresh database, the way
 * production first starts: the other integration tests run the schema only. Catches a generator
 * or constraint mismatch here instead of at the first production boot. No Spring: just Flyway,
 * with the same settings as application.properties.
 */
@Testcontainers
class LaunchContentMigrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    @Test
    @DisplayName("""
            Given a fresh database
            When Flyway runs the schema and the launch content, as production does at first start
            Then both migrations succeed, and the content is complete: 16 active muscles owned by the
            house account (which nobody can sign in as), and exercises that are all live, each
            accepted for at least one muscle
            """)
    void launchContentMigratesCleanlyOntoAFreshDatabase() {
        // Given
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        // When
        int applied = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration", "classpath:db/content")
                .placeholderReplacement(false)
                .load()
                .migrate()
                .migrationsExecuted;

        // Then
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        assertEquals(2, applied);
        assertEquals(1, count(jdbc, "SELECT count(*) FROM app_user WHERE display_name = 'Heaped & Stacked'"));
        assertEquals(0, count(jdbc, "SELECT count(*) FROM user_identity"));
        assertEquals(16, count(jdbc, "SELECT count(*) FROM concept WHERE concept_status = 'ACTIVE'"));
        assertEquals(16, count(jdbc, "SELECT count(*) FROM concept"));
        assertTrue(count(jdbc, "SELECT count(*) FROM question") > 900);
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
    }

    private static int count(JdbcTemplate jdbc, String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
