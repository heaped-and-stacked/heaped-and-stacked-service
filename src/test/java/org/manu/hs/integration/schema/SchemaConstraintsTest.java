package org.manu.hs.integration.schema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The database's own rules (V1's CHECK constraints), the last line of defence behind the
 * application's checks: each test writes a row the application never would, straight in SQL.
 */
class SchemaConstraintsTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @ParameterizedTest(name = "{0} with resolved_by {1} and resolved_at {2}")
    @CsvSource({
            "RESOLVED, set, missing",
            "DISMISSED, missing, set",
            "OPEN, set, missing",
            "OPEN, missing, set"})
    @DisplayName("""
            Given a report
            When it is stored with only one of "who decided" and "when", or a decision while open
            Then the database refuses it: a decided report always says who decided it and when,
            and an open one says neither
            """)
    void reportDecisionIsAllOrNothing(String status, String resolvedBy, String resolvedAt) {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");

        // When / Then
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("""
                        INSERT INTO question_report (question_id, reported_by, reason, report_status, resolved_by, resolved_at)
                        VALUES (?, ?, 'WRONG', ?, ?, %s)
                        """.formatted(resolvedAt.equals("set") ? "now()" : "NULL"),
                questionId, OTHER_USER, status, resolvedBy.equals("set") ? ADMIN : null));
    }
}
