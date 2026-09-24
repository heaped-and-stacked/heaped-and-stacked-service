package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * PUT /content/questions/{id}/verification: whoever curates the question marks it verified
 * (checked: a clear question, a correct answer), or takes it back. Verification belongs to the
 * question, whatever concepts it's on: staff check the gym's questions (on any featured concept),
 * and the owners of members' own concepts check the questions only on those.
 */
class VerifyQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question on two gym concepts
            When a staff member verifies it
            Then HTTP 204 is returned and the question is verified, once, by that staff member
            """)
    void verifyQuestionReturnsNoContentWhenStaffVerifiesAGymQuestion() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "java-generics");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = verify(questionId, true);

        // Then
        response.expectStatus().isNoContent();
        assertEquals(STAFF, findQuestionVerifiedBy(questionId));
    }

    @Test
    @DisplayName("""
            Given a question someone else wrote on a member's own concept
            When the concept's owner verifies it, and later takes it back
            Then HTTP 204 is returned both times: it's verified by the owner, then not verified
            """)
    void verifyQuestionLetsTheOwnerOfAMembersConceptVerifyAndTakeItBack() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "my-katas");
        actingAs(AUTHOR);

        // When / Then
        verify(questionId, true).expectStatus().isNoContent();
        assertEquals(AUTHOR, findQuestionVerifiedBy(questionId));
        verify(questionId, false).expectStatus().isNoContent();
        assertNull(findQuestionVerifiedBy(questionId));
    }

    @Test
    @DisplayName("""
            Given a gym question, and a question on both a gym concept and a member's own concept
            When a member tries to verify the first, and the member who owns that own concept the
            second
            Then HTTP 403 is returned for both, and neither is verified: a question on the gym's
            concepts is staff's to check
            """)
    void verifyQuestionReturnsForbiddenUnlessTheCallerCuratesIt() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        Long gymQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long sharedQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "my-katas");

        // When / Then
        actingAs(OTHER_USER);
        verify(gymQuestion, true).expectStatus().isForbidden();
        verify(sharedQuestion, true).expectStatus().isForbidden();

        assertNull(findQuestionVerifiedBy(gymQuestion));
        assertNull(findQuestionVerifiedBy(sharedQuestion));
    }

    @Test
    @DisplayName("""
            Given a question still waiting for approval on its only concept
            When the concept's owner tries to verify it
            Then HTTP 409 is returned: a question is verified once it's accepted somewhere
            """)
    void verifyQuestionReturnsConflictWhileNotAcceptedAnywhere() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = verify(questionId, true);

        // Then
        response.expectStatus().isEqualTo(409);
        assertNull(findQuestionVerifiedBy(questionId));
    }

    @Test
    @DisplayName("""
            Given no question with id 999, and a question
            When staff verify the first, and send no decision for the second
            Then HTTP 404 and HTTP 400 are returned
            """)
    void verifyQuestionReturnsNotFoundAndBadRequest() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When / Then
        verify(999L, true).expectStatus().isNotFound();
        restClient.put()
                .uri("/content/questions/{id}/verification", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("""
            Given a question
            When a visitor who isn't signed in tries to verify it
            Then HTTP 401 is returned
            """)
    void verifyQuestionReturnsUnauthorizedForVisitors() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAsVisitor();

        // When / Then
        verify(questionId, true).expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec verify(Long questionId, boolean verified) {
        return restClient
                .put()
                .uri("/content/questions/{id}/verification", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "verified": %s
                        }
                        """.formatted(verified))
                .exchange();
    }
}
