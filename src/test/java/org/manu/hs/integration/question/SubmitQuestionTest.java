package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            Then the question is created in DRAFT status and returned with HTTP 201
            """)
    void submitQuestionReturnsCreatedWhenPayloadIsValid() {
        // Given
        insertConcept("java-core");
        actingAs(AUTHOR, false);

        String requestBody = """
                {
                    "requirement": "Explain the difference between == and .equals() for objects",
                    "sampleSolution": "== compares references, .equals() compares logical equality",
                    "type": "DEFINITION",
                    "difficultyScore": 2,
                    "expectedDurationMinutes": 1,
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
        assertEquals(QuestionType.DEFINITION, createdQuestion.getType());
        assertEquals(QuestionStatus.DRAFT, createdQuestion.getStatus());
        assertEquals(List.of("java-core"), createdQuestion.getConceptTags());
        assertEquals(AUTHOR, createdQuestion.getCreatedBy());
        assertEquals(AUTHOR, createdQuestion.getUpdatedBy());
        assertNotNull(createdQuestion.getCreatedAt());
        assertEquals(createdQuestion.getCreatedAt(), createdQuestion.getUpdatedAt());

        // Verify database entry
        Question dbRecord = findQuestionById(createdQuestion.getId());
        assertEquals(QuestionStatus.DRAFT, dbRecord.getStatus());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(createdQuestion.getId()));
    }

    @Test
    @DisplayName("""
            Given an admin and a valid payload
            When a POST request is sent to the questions endpoint
            Then the question is created directly as ACTIVE
            """)
    void submitQuestionReturnsActiveQuestionWhenCallerIsAdmin() {
        // Given
        insertConcept("java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = submit(validQuestionJson("java-core"));

        // Then
        QuestionResponse createdQuestion = response
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdQuestion);
        assertEquals(QuestionStatus.ACTIVE, createdQuestion.getStatus());
        assertEquals(QuestionStatus.ACTIVE, findQuestionById(createdQuestion.getId()).getStatus());
    }

    @Test
    @DisplayName("""
            Given a member proposing a question for a concept that is still a draft
            When a POST request is sent to the questions endpoint
            Then the question is created as a DRAFT, waiting for review together with the concept
            """)
    void submitQuestionReturnsCreatedWhenMemberLinksDraftConcept() {
        // Given
        insertConcept("java-records", ConceptStatus.DRAFT);
        actingAs(AUTHOR, false);

        // When
        RestTestClient.ResponseSpec response = submit(validQuestionJson("java-records"));

        // Then
        response
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("DRAFT");
    }

    @Test
    @DisplayName("""
            Given an admin submitting a question linked to a concept that is still a draft
            When a POST request is sent to the questions endpoint
            Then a conflict is returned, since the question would be active, and nothing is persisted
            """)
    void submitQuestionReturnsConflictWhenAdminLinksDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = submit(validQuestionJson("java-core", "java-records"));

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("A question can't be active while these concepts are drafts: java-records");

        assertEquals(0, countQuestions());
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
        actingAs(AUTHOR, false);

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
            Then those fields are ignored: the question is a DRAFT owned by the caller
            """)
    void submitQuestionIgnoresServerOwnedFields() {
        // Given
        insertConcept("java-core");
        actingAs(AUTHOR, false);

        String requestBody = """
                {
                    "requirement": "Explain the difference between == and .equals() for objects",
                    "sampleSolution": "== compares references, .equals() compares logical equality",
                    "type": "DEFINITION",
                    "difficultyScore": 2,
                    "expectedDurationMinutes": 1,
                    "conceptTags": ["java-core"],
                    "status": "ACTIVE",
                    "createdBy": "someone-else"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = submit(requestBody);

        // Then
        response
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("DRAFT")
                .jsonPath("$.createdBy").isEqualTo(AUTHOR);
    }

    static Stream<Arguments> invalidPayloads() {
        String valid = validQuestionJson("java-core");
        return Stream.of(
                arguments("requirement is missing",
                        valid.replace("\"requirement\": \"Updated requirement\",", ""),
                        "requirement is required"),
                arguments("requirement is blank",
                        valid.replace("\"Updated requirement\"", "\"   \""),
                        "requirement is required"),
                arguments("sampleSolution is missing",
                        valid.replace("\"sampleSolution\": \"Updated solution\",", ""),
                        "sampleSolution is required"),
                arguments("type is missing",
                        valid.replace("\"type\": \"GOTCHA\",", ""),
                        "type is required"),
                arguments("difficultyScore is missing",
                        valid.replace("\"difficultyScore\": 4,", ""),
                        "difficultyScore is required"),
                arguments("difficultyScore is 0",
                        valid.replace("\"difficultyScore\": 4", "\"difficultyScore\": 0"),
                        "difficultyScore must be between 1 and 10"),
                arguments("difficultyScore is 11",
                        valid.replace("\"difficultyScore\": 4", "\"difficultyScore\": 11"),
                        "difficultyScore must be between 1 and 10"),
                arguments("expectedDurationMinutes is missing",
                        valid.replace("\"expectedDurationMinutes\": 8,", ""),
                        "expectedDurationMinutes is required"),
                arguments("expectedDurationMinutes is 0",
                        valid.replace("\"expectedDurationMinutes\": 8", "\"expectedDurationMinutes\": 0"),
                        "expectedDurationMinutes must be greater than 0"),
                arguments("conceptTags is empty",
                        validQuestionJson(),
                        "conceptTags is required"));
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

    @Test
    @DisplayName("""
            Given a payload with an unknown question type
            When a POST request is sent to the questions endpoint
            Then a bad request is returned with a message
            """)
    void submitQuestionReturnsBadRequestWhenTypeIsUnknown() {
        // Given
        insertConcept("java-core");

        String requestBody = validQuestionJson("java-core").replace("\"GOTCHA\"", "\"RIDDLE\"");

        // When
        RestTestClient.ResponseSpec response = submit(requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    private RestTestClient.ResponseSpec submit(String requestBody) {
        return restClient
                .post()
                .uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
