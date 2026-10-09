package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * POST /content/questions
 */
class SubmitQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member and a valid payload with at least one existing concept tag
            When a POST request is sent to the questions endpoint
            Then the question is created as SUBMITTED and returned with HTTP 201
            """)
    void submitQuestionReturnsCreatedWhenPayloadIsValid() {
        // Given
        insertConcept("java-core");
        actingAs(AUTHOR);

        String requestBody = """
                {
                    "title": "Equality of objects",
                    "requirement": "Explain the difference between == and .equals() for objects",
                    "sampleSolution": "== compares references, .equals() compares logical equality",
                    "difficultyScore": 2,
                    "source": "manu",
                    "conceptTags": ["java-core"]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = submit(requestBody);

        // Then
        QuestionResponse createdQuestion = response
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdQuestion);
        assertNotNull(createdQuestion.getId());
        assertEquals(
                "Explain the difference between == and .equals() for objects",
                createdQuestion.getRequirement());
        assertEquals("Equality of objects", createdQuestion.getTitle());
        assertEquals(QuestionStatus.SUBMITTED, createdQuestion.getStatus());
        assertEquals(List.of("java-core"), createdQuestion.getConceptTags());
        assertEquals(AUTHOR, createdQuestion.getCreatedBy());
        assertEquals(AUTHOR, createdQuestion.getUpdatedBy());
        assertNotNull(createdQuestion.getCreatedAt());
        assertEquals(createdQuestion.getCreatedAt(), createdQuestion.getUpdatedAt());

        // Verify database entry
        Question dbRecord = findQuestionById(createdQuestion.getId());
        assertEquals("Equality of objects", dbRecord.getTitle());
        assertEquals(QuestionStatus.SUBMITTED, dbRecord.getStatus());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(createdQuestion.getId()));
    }

    @Test
    @DisplayName("""
            Given a member submitting a question linked to a concept that is still a draft
            When a POST request is sent to the questions endpoint
            Then a conflict is returned, since submitted questions only link published concepts,
            and nothing is persisted
            """)
    void submitQuestionReturnsConflictWhenLinkingDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = submit(validQuestionJson("java-core", "java-records"));

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("A question can't be submitted while these concepts are drafts: java-records");

        assertEquals(0, countQuestions());
    }

    @Test
    @DisplayName("""
            Given a concept that accepts contributed questions automatically
            When another member submits a question for it
            Then the question is accepted for that concept straight away
            """)
    void submitQuestionAcceptsLinkWhenConceptAcceptsAutomatically() {
        // Given
        insertConcept("java-core");
        actingAs(OTHER_USER);

        // When
        QuestionResponse createdQuestion = submit(validQuestionJson("java-core"))
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(createdQuestion);
        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(createdQuestion.getId(), "java-core"));
    }

    @Test
    @DisplayName("""
            Given one concept whose owner approves contributed questions and one that accepts them
            When another member submits a question linked to both
            Then the question waits for approval on the first concept only, and is accepted on the second
            """)
    void submitQuestionLeavesLinkPendingOnlyWhereOwnerApproves() {
        // Given
        insertConcept("java-core");
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        insertConcept("java-generics");
        actingAs(OTHER_USER);

        // When
        QuestionResponse createdQuestion = submit(validQuestionJson("java-core", "java-generics"))
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(createdQuestion);
        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(createdQuestion.getId(), "java-core"));
        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(createdQuestion.getId(), "java-generics"));
    }

    @Test
    @DisplayName("""
            Given a concept whose owner approves contributed questions
            When a staff member who isn't its owner submits a question for it
            Then the question is accepted straight away, as staff curate every concept
            """)
    void submitQuestionAcceptsLinkWhenAuthorIsStaff() {
        // Given
        insertConcept("java-core");
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        QuestionResponse createdQuestion = submit(validQuestionJson("java-core"))
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(createdQuestion);
        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(createdQuestion.getId(), "java-core"));
    }

    @Test
    @DisplayName("""
            Given a concept whose owner approves contributed questions
            When the owner submits a question for it
            Then the question is accepted straight away, as owners don't approve their own questions
            """)
    void submitQuestionAcceptsLinkWhenAuthorOwnsConcept() {
        // Given
        insertConcept("java-core");
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        actingAs(AUTHOR);

        // When
        QuestionResponse createdQuestion = submit(validQuestionJson("java-core"))
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertNotNull(createdQuestion);
        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(createdQuestion.getId(), "java-core"));
    }

    @Test
    @DisplayName("""
            Given a payload listing the same concept tag more than once
            When a POST request is sent to the questions endpoint
            Then the duplicates are dropped, keeping the first occurrence
            """)
    void submitQuestionDropsDuplicateConceptTags() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response =
                submit(validQuestionJson("java-generics", "java-core", "java-generics"));

        // Then
        QuestionResponse createdQuestion = response
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdQuestion);
        assertEquals(List.of("java-core", "java-generics"), findConceptTagsForQuestion(createdQuestion.getId()));
    }

    @Test
    @DisplayName("""
            Given a payload that also sets fields owned by the server (status, createdBy)
            When a POST request is sent to the questions endpoint
            Then those fields are ignored: the question is SUBMITTED and owned by the caller
            """)
    void submitQuestionIgnoresServerOwnedFields() {
        // Given
        insertConcept("java-core");
        actingAs(AUTHOR);

        String requestBody = """
                {
                    "title": "Equality of objects",
                    "requirement": "Explain the difference between == and .equals() for objects",
                    "sampleSolution": "== compares references, .equals() compares logical equality",
                    "difficultyScore": 2,
                    "conceptTags": ["java-core"],
                    "status": "RETIRED",
                    "createdBy": "someone-else"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = submit(requestBody);

        // Then
        response
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("SUBMITTED")
                .jsonPath("$.createdBy").isEqualTo(AUTHOR);
    }

    /**
     * A few representative cases: every rule is listed in QuestionServiceTest. These prove
     * that a broken rule reaches the client as a 400 with its message.
     */
    static Stream<Arguments> invalidPayloads() {
        String valid = validQuestionJson("java-core");
        return Stream.of(
                arguments("title is missing",
                        valid.replace("\"title\": \"Updated title\",", ""),
                        "title is required"),
                arguments("requirement is missing",
                        valid.replace("\"requirement\": \"Updated requirement\",", ""),
                        "requirement is required"),
                // A missing number must stay "missing" through JSON binding, not become 0.
                arguments("difficultyScore is missing",
                        valid.replace("\"difficultyScore\": 4,", ""),
                        "difficultyScore is required"),
                arguments("difficultyScore is 11",
                        valid.replace("\"difficultyScore\": 4", "\"difficultyScore\": 11"),
                        "difficultyScore must be between 1 and 10"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPayloads")
    @DisplayName("""
            Given a payload that breaks one field rule
            When a POST request is sent to the questions endpoint
            Then a bad request naming the field is returned and nothing is persisted
            """)
    void submitQuestionReturnsBadRequestWhenAFieldIsInvalid(String description, String requestBody, String expectedMessage) {
        // Given
        insertConcept("java-core");

        // When
        RestTestClient.ResponseSpec response = submit(requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(expectedMessage);

        assertEquals(0, countQuestions());
    }

    @Test
    @DisplayName("""
            Given a payload referencing a concept tag that does not exist
            When a POST request is sent to the questions endpoint
            Then a bad request is returned and nothing is persisted
            """)
    void submitQuestionReturnsBadRequestWhenConceptTagDoesNotExist() {
        // When
        RestTestClient.ResponseSpec response = submit(validQuestionJson("does-not-exist"));

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept tag 'does-not-exist' does not exist");

        assertEquals(0, countQuestions());
    }

    private RestTestClient.ResponseSpec submit(String requestBody) {
        return restClient
                .post()
                .uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }

    @Nested
    @TestPropertySource(properties = "app.content.members-contribute=false")
    class WhenOnlyStaffContribute {

        @Test
        @DisplayName("""
                Given contributions are open to staff only
                When a member submits a question
                Then a forbidden response is returned and nothing is stored
                """)
        void submitQuestionReturnsForbiddenForAMember() {
            // Given
            insertConcept("java-core");
            actingAs(AUTHOR);

            // When
            RestTestClient.ResponseSpec response = submit(validQuestionJson("java-core"));

            // Then
            response
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Only staff may add concepts and questions for now");

            assertEquals(0, countQuestions());
        }

        @Test
        @DisplayName("""
                Given contributions are open to staff only
                When staff submit a question
                Then it is created
                """)
        void submitQuestionStillWorksForStaff() {
            // Given
            insertConcept("java-core");
            insertUser(STAFF, UserRole.STAFF);
            actingAs(STAFF);

            // When / Then
            submit(validQuestionJson("java-core")).expectStatus().isCreated();
        }
    }
}
