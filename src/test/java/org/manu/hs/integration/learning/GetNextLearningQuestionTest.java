package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.learning.dto.LearningQuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GET /learning/questions/next
 */
class GetNextLearningQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given one available question among others that are not available
            (a draft, a retired question, one still waiting for approval, one only on a retired concept)
            When a learner sends a GET request to the next question endpoint
            Then the available question is returned, listing only the concepts it is accepted for,
            and without its sample solution
            """)
    void getNextQuestionReturnsOnlyAnAvailableQuestionWithoutSolution() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        insertConcept("java-applets", ConceptStatus.RETIRED);

        Long available = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "java-generics");
        setConceptLinkStatus(available, "java-generics", ConceptLinkStatus.PENDING);

        insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        insertQuestionAwaitingApproval(OTHER_USER, "java-generics");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-applets");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(available.intValue())
                .jsonPath("$.conceptTags.length()").isEqualTo(1)
                .jsonPath("$.conceptTags[0]").isEqualTo("java-core")
                .jsonPath("$.title").isEqualTo("Original title")
                .jsonPath("$.requirement").isEqualTo("Original requirement")
                .jsonPath("$.sampleSolution").doesNotExist();
    }

    @Test
    @DisplayName("""
            Given a question the learner already answered, and one only someone else answered
            When the learner sends a GET request to the next question endpoint
            Then the question new to the learner is returned: novelty comes first
            """)
    void getNextQuestionReturnsUnansweredQuestionFirst() {
        // Given
        insertConcept("java-core");
        Long answeredByMe = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long answeredBySomeoneElse = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);
        submitAnswer(answeredBySomeoneElse, "Their answer");
        actingAs(OTHER_USER);
        submitAnswer(answeredByMe, "My answer");

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(answeredBySomeoneElse.intValue());
    }

    @Test
    @DisplayName("""
            Given the learner has answered every available question, one 3 days ago and one today
            When the learner asks for the next question many times
            Then both come back for repetition, in no fixed order, and the one answered longer ago
            comes back more often (it is 4 times as likely: days since answered + 1)
            """)
    void getNextQuestionFavoursQuestionsAnsweredLongerAgoWithoutFixingTheOrder() {
        // Given
        insertConcept("java-core");
        Long answeredLongerAgo = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long answeredToday = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        Long olderAnswer = submitAnswer(answeredLongerAgo, "First try");
        submitAnswer(answeredToday, "Fresh answer");
        setAnsweredAt(olderAnswer, Instant.now().minus(3, ChronoUnit.DAYS));

        // When
        // Random by design: with 60 draws, failing by chance is about one in a million.
        Map<Long, Integer> timesReturned = new HashMap<>();
        for (int draw = 0; draw < 60; draw++) {
            LearningQuestionResponse next = getNext("")
                    .expectStatus().isOk()
                    .expectBody(LearningQuestionResponse.class)
                    .returnResult()
                    .getResponseBody();
            assertNotNull(next);
            timesReturned.merge(next.getId(), 1, Integer::sum);
        }

        // Then
        int longerAgo = timesReturned.getOrDefault(answeredLongerAgo, 0);
        int today = timesReturned.getOrDefault(answeredToday, 0);
        assertTrue(today > 0, "the question answered today must come up sometimes");
        assertTrue(longerAgo > today, "the question answered longer ago must come up more often");
    }

    @Test
    @DisplayName("""
            Given two new questions, one of which the learner already saw in this workout
            When the learner sends a GET request to the next question endpoint, skipping that one
            Then the other question is returned, so a workout never repeats a question
            """)
    void getNextQuestionLeavesOutSkippedQuestions() {
        // Given
        insertConcept("java-core");
        Long seenThisWorkout = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long other = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?skip=" + seenThisWorkout);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(other.intValue());
    }

    @Test
    @DisplayName("""
            Given questions on different concepts, one of them accepted for two concepts
            When the learner sends a GET request to the next question endpoint for one concept
            Then a question working that concept is returned, still listing all of its concepts
            """)
    void getNextQuestionReturnsOnlyQuestionsForTheRequestedConcepts() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long forGenerics = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "java-generics");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?concepts=java-generics");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(forGenerics.intValue())
                .jsonPath("$.conceptTags.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("""
            Given the only available question was already seen in this workout
            When the learner sends a GET request to the next question endpoint, skipping it
            Then no content is returned: the workout has run out of questions
            """)
    void getNextQuestionReturnsNoContentWhenNothingIsLeft() {
        // Given
        insertConcept("java-core");
        Long onlyQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?skip=" + onlyQuestion);

        // Then
        response.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given two available questions on a concept, one of them answered by the learner,
            and others that don't count (a draft, one on another concept, one only someone else answered elsewhere)
            When the learner sends a GET request to the next question endpoint for that concept
            Then the response says how many questions on it the learner has never answered, the returned one included
            """)
    void getNextQuestionCountsTheQuestionsTheLearnerNeverAnswered() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long neverAnswered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        Long onAnotherConcept = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-generics");
        actingAs(AUTHOR);
        submitAnswer(onAnotherConcept, "Their answer");
        actingAs(OTHER_USER);
        submitAnswer(answered, "My answer");

        // When
        RestTestClient.ResponseSpec response = getNext("?concepts=java-core");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(neverAnswered.intValue())
                .jsonPath("$.unansweredCount").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given the learner has answered every available question
            When the learner sends a GET request to the next question endpoint
            Then a question is still returned, as a repeat, and the response says none is left unanswered
            """)
    void getNextQuestionReportsNoUnansweredQuestionsOnceAllAreAnswered() {
        // Given
        insertConcept("java-core");
        Long onlyQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        submitAnswer(onlyQuestion, "My answer");

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(onlyQuestion.intValue())
                .jsonPath("$.unansweredCount").isEqualTo(0);
    }

    private RestTestClient.ResponseSpec getNext(String query) {
        return restClient
                .get()
                .uri("/learning/questions/next" + query)
                .exchange();
    }
}
