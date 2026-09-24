package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/questions/{id}
 */
class UpdateQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller is the author of a question
            When a PUT request is sent to the questions endpoint with a valid payload and another concept
            Then the question is updated, its concept links replaced, its status kept, and HTTP 200 returned
            """)
    void updateQuestionReplacesConceptLinksAndKeepsTheStatus() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        String requestBody = """
                {
                    "title": "Updated title",
                    "requirement": "Updated requirement",
                    "sampleSolution": "Updated solution",
                    "difficultyScore": 4,
                    "source": "manu",
                    "conceptTags": ["java-generics"]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(questionId, requestBody);

        // Then
        QuestionResponse updatedQuestion = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedQuestion);
        assertEquals("Updated title", updatedQuestion.getTitle());
        assertEquals("Updated requirement", updatedQuestion.getRequirement());
        assertEquals(4, updatedQuestion.getDifficultyScore());
        assertEquals(List.of("java-generics"), updatedQuestion.getConceptTags());
        assertEquals(QuestionStatus.SUBMITTED, updatedQuestion.getStatus());
        assertEquals(AUTHOR, updatedQuestion.getCreatedBy());
        assertEquals(AUTHOR, updatedQuestion.getUpdatedBy());

        // Verify database entry: the old link is gone, the new one is there
        assertEquals(List.of("java-generics"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given the caller is an admin
            When a PUT request is sent to the questions endpoint for another author's submitted question
            Then the question is updated, keeps its author and status, and records the admin as updater
            """)
    void updateQuestionReturnsOkWhenAdminEditsAnyQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        QuestionResponse updatedQuestion = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedQuestion);
        assertEquals("Updated title", updatedQuestion.getTitle());
        assertEquals("Updated requirement", updatedQuestion.getRequirement());
        assertEquals(QuestionStatus.SUBMITTED, updatedQuestion.getStatus());
        assertEquals(AUTHOR, updatedQuestion.getCreatedBy());
        assertEquals(ADMIN, updatedQuestion.getUpdatedBy());
    }

    @Test
    @DisplayName("""
            Given a submitted question still waiting for approval on one concept
            When its author edits it and also links a new concept whose owner approves contributions
            Then the existing link keeps its state and the new link waits for its owner's approval
            """)
    void updateQuestionKeepsExistingLinksAndEvaluatesNewOnes() {
        // Given
        insertConcept("java-core");
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        insertConcept("java-generics");
        setContributionPolicy("java-generics", ContributionPolicy.OWNER_APPROVAL);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        setConceptLinkStatus(questionId, "java-core", ConceptLinkStatus.PENDING);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core", "java-generics"));

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(questionId, "java-core"));
        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(questionId, "java-generics"));
    }

    @Test
    @DisplayName("""
            Given a question accepted for one concept
            When its author edits it without changing its concepts
            Then the concept keeps its accepted state, so edits never re-open a decision
            """)
    void updateQuestionKeepsAcceptedLinkWhenConceptsUnchanged() {
        // Given
        insertConcept("java-core");
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(questionId, "java-core"));
    }

    @Test
    @DisplayName("""
            Given a question accepted for one concept and declined for another by that concept's owner
            When its author removes the declined concept in one edit and adds it back in the next
            Then the decline stands: a declined concept drops off the question for good, so edits
            can't undo an owner's decision
            """)
    void updateQuestionCantUndoADecline() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        setContributionPolicy("java-generics", ContributionPolicy.OWNER_APPROVAL);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core", "java-generics");
        setConceptLinkStatus(questionId, "java-generics", ConceptLinkStatus.DECLINED);
        actingAs(OTHER_USER);

        // When
        update(questionId, validQuestionJson("java-core")).expectStatus().isOk();
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core", "java-generics"));

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.conceptTags.length()").isEqualTo(1)
                .jsonPath("$.conceptTags[0]").isEqualTo("java-core");

        assertEquals(ConceptLinkStatus.DECLINED, findConceptLinkStatus(questionId, "java-generics"));
    }

    @Test
    @DisplayName("""
            Given an admin links a submitted question to a concept that is still a draft
            When a PUT request is sent to the questions endpoint
            Then a conflict is returned and the question is unchanged
            """)
    void updateQuestionReturnsConflictWhenSubmittedQuestionLinksDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core", "java-records"));

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("A question can't be submitted while these concepts are drafts: java-records");

        assertEquals("Original requirement", findQuestionById(questionId).getRequirement());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given the caller is not the author of a question and is not an admin
            When a PUT request is sent to the questions endpoint, even with an invalid payload
            Then a forbidden response is returned and the question is unchanged
            """)
    void updateQuestionReturnsForbiddenWhenNonAuthorEditsQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // Forbidden is reported before the payload is validated.
        String invalidRequestBody = """
                {
                    "sampleSolution": "No requirement"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(questionId, invalidRequestBody);

        // Then
        response.expectStatus().isForbidden();

        assertEquals("Original requirement", findQuestionById(questionId).getRequirement());
    }

    @Test
    @DisplayName("""
            Given the caller is the author of a question that is already submitted
            When a PUT request is sent to the questions endpoint with a valid payload
            Then the question is updated with HTTP 200 and stays submitted, as authors may fix
            typos and phrasing at any time
            """)
    void updateQuestionReturnsOkWhenAuthorEditsOwnSubmittedQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        response.expectStatus().isOk();

        Question dbRecord = findQuestionById(questionId);
        assertEquals("Updated requirement", dbRecord.getRequirement());
        assertEquals(QuestionStatus.SUBMITTED, dbRecord.getStatus());
    }

    @Test
    @DisplayName("""
            Given no question exists for the given id
            When a PUT request is sent to the questions endpoint, even with an invalid payload
            Then a not found response is returned
            """)
    void updateQuestionReturnsNotFoundWhenQuestionDoesNotExist() {
        // Given
        actingAs(OTHER_USER);

        // Not found is reported before permissions and payload are checked.
        String invalidRequestBody = """
                {
                    "sampleSolution": "No requirement"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(999_999L, invalidRequestBody);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given a payload with no concept tags
            When a PUT request is sent to the questions endpoint
            Then a bad request is returned and the question is unchanged
            """)
    void updateQuestionReturnsBadRequestWhenConceptTagsAreEmpty() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson());

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("conceptTags is required");

        Question unchanged = findQuestionById(questionId);
        assertEquals("Original requirement", unchanged.getRequirement());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given a submitted question by a member
            When gym staff send a PUT request to the questions endpoint for it
            Then the question is updated, keeps its author, and records staff as updater
            """)
    void updateQuestionReturnsOkWhenStaffEditsAnyQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        response.expectStatus().isOk();

        Question dbRecord = findQuestionById(questionId);
        assertEquals("Updated title", dbRecord.getTitle());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
        assertEquals(STAFF, dbRecord.getUpdatedBy());
    }

    @Test
    @DisplayName("""
            Given a member's question on their own concept only, outside the gym's pool
            When gym staff try to edit it
            Then HTTP 403 is returned: staff curate the exercises of the gym's pool, and a member's
            own concept is theirs to curate
            """)
    void updateQuestionReturnsForbiddenWhenStaffEditsAQuestionOutsideTheGymsPool() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = update(questionId, """
                {
                    "conceptTags": ["my-katas"],
                    "title": "Changed by staff",
                    "requirement": "Changed requirement",
                    "sampleSolution": "Changed solution",
                    "difficultyScore": 3
                }
                """);

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a verified question its author wrote on a member's own concept they don't own
            When the author edits it
            Then it's no longer verified: the concept's owner checks it again
            """)
    void updateQuestionClearsTheVerificationWhenTheAuthorCantVerify() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        setQuestionVerifiedBy(questionId, OTHER_USER);
        actingAs(AUTHOR);

        // When
        update(questionId, """
                {
                    "conceptTags": ["my-katas"],
                    "title": "Changed title",
                    "requirement": "Changed requirement",
                    "sampleSolution": "Changed solution",
                    "difficultyScore": 3
                }
                """).expectStatus().isOk();

        // Then
        assertNull(findQuestionVerifiedBy(questionId));
    }

    @Test
    @DisplayName("""
            Given a verified gym question
            When gym staff edit it
            Then it stays verified: whoever may verify it keeps the check with the fix
            """)
    void updateQuestionKeepsTheVerificationWhenTheEditorMayVerify() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionVerifiedBy(questionId, ADMIN);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        update(questionId, """
                {
                    "conceptTags": ["java-core"],
                    "title": "Fixed title",
                    "requirement": "Fixed requirement",
                    "sampleSolution": "Fixed solution",
                    "difficultyScore": 3
                }
                """).expectStatus().isOk();

        // Then
        assertEquals(ADMIN, findQuestionVerifiedBy(questionId));
    }

    private RestTestClient.ResponseSpec update(Long questionId, String requestBody) {
        return restClient
                .put()
                .uri("/content/questions/{id}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
