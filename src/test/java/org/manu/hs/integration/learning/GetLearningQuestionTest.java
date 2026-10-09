package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * GET /learning/questions/{id}: one chosen question to train on, e.g. retried from past workouts.
 */
class GetLearningQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given an available question accepted on one concept and still waiting on another
            When a learner sends a GET request for it
            Then it is returned like the next question: only the concepts it is accepted for, and
            without its sample solution
            """)
    void getQuestionReturnsAnAvailableQuestionWithoutSolution() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "java-generics");
        setConceptLinkStatus(questionId, "java-generics", ConceptLinkStatus.PENDING);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId, "");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(questionId.intValue())
                .jsonPath("$.title").isEqualTo("Original title")
                .jsonPath("$.conceptTags.length()").isEqualTo(1)
                .jsonPath("$.conceptTags[0]").isEqualTo("java-core")
                .jsonPath("$.sampleSolution").doesNotExist();
    }

    @Test
    @DisplayName("""
            Given a learner who answered one of two questions on a concept, and a third question elsewhere
            When they send a GET request for the answered one
            Then the count of questions new to them is for that question's concepts: one left
            """)
    void getQuestionCountsUnansweredQuestionsForItsConcepts() {
        // Given
        insertConcept("java-core");
        insertConcept("databases");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "databases");
        actingAs(OTHER_USER);
        submitAnswer(answered, "My answer");

        // When
        RestTestClient.ResponseSpec response = getQuestion(answered, "");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.unansweredCount").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given the same three questions, one of them answered
            When the learner sends a GET request for the answered one, for the workout's concepts
            Then the count is for those concepts instead: two left
            """)
    void getQuestionCountsUnansweredQuestionsForTheGivenConcepts() {
        // Given
        insertConcept("java-core");
        insertConcept("databases");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "databases");
        actingAs(OTHER_USER);
        submitAnswer(answered, "My answer");

        // When
        RestTestClient.ResponseSpec response = getQuestion(answered, "?concepts=java-core,databases");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.unansweredCount").isEqualTo(2);
    }

    @Test
    @DisplayName("""
            Given a retired question, one only on a retired concept, and no question with id 999
            When a learner sends a GET request for each
            Then not found is returned for all three: none can be trained
            """)
    void getQuestionReturnsNotFoundWhenItCantBeTrained() {
        // Given
        insertConcept("java-core");
        insertConcept("java-applets", ConceptStatus.RETIRED);
        Long retired = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        Long onRetiredConcept = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-applets");
        actingAs(OTHER_USER);

        // When / Then
        getQuestion(retired, "").expectStatus().isNotFound();
        getQuestion(onRetiredConcept, "").expectStatus().isNotFound();
        getQuestion(999L, "").expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a question two learners voted up (the caller one of them) and one voted down
            When the caller sends a GET request for it
            Then it says how many voted it up and down, and the caller's own vote, so the rack shows
            the counts and which one is theirs
            """)
    void getQuestionCountsItsVotesAndShowsTheCallersOwn() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertVote(questionId, OTHER_USER, Vote.UP);
        insertVote(questionId, ADMIN, Vote.UP);
        insertVote(questionId, AUTHOR, Vote.DOWN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId, "");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.upVotes").isEqualTo(2)
                .jsonPath("$.downVotes").isEqualTo(1)
                .jsonPath("$.myVote").isEqualTo("UP");
    }

    @Test
    @DisplayName("""
            Given an available question
            When a visitor who isn't signed in sends a GET request for it
            Then unauthorized is returned: retrying is for members
            """)
    void getQuestionReturnsUnauthorizedForVisitors() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId, "");

        // Then
        response.expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec getQuestion(Long id, String query) {
        return restClient
                .get()
                .uri("/learning/questions/" + id + query)
                .exchange();
    }
}
