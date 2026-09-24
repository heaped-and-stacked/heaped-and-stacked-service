package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.learning.dto.LearningQuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.user.UserRole;
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
            (a benched question, one still waiting for approval, one only on a retired concept)
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
            Given two questions new to the learner, one of them liked by six other learners
            (up votes and praise, each learner counted once; a down vote doesn't count)
            When the learner asks for the next question many times
            Then both come up, in no fixed order, and the liked one comes up more often
            (weight 1 + ln(1 + likes): about 3 to 1 here)
            """)
    void getNextQuestionFavoursLikedQuestionsWithoutFixingTheOrder() {
        // Given
        insertConcept("java-core");
        Long liked = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long notLiked = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        for (long liker = 2001; liker <= 2007; liker++) {
            insertUser(liker, UserRole.MEMBER);
        }
        insertVote(liked, 2001L, Vote.UP);
        insertVote(liked, 2002L, Vote.UP);
        insertVote(liked, 2003L, Vote.UP);
        insertVote(liked, 2004L, Vote.UP);
        insertReport(liked, 2004L, ReportReason.PRAISE);
        insertReport(liked, 2005L, ReportReason.PRAISE);
        insertReport(liked, 2006L, ReportReason.PRAISE);
        insertVote(notLiked, 2007L, Vote.DOWN);
        actingAs(OTHER_USER);

        // When
        // Random by design: 6 likes give 2.95 to 1, so the liked one comes first about 75% of the
        // time. With 200 draws, it falls below 125 by chance about once in 25,000 runs; without
        // the weight (50%), it would reach 125 about once in 5,000.
        Map<Long, Integer> timesReturned = new HashMap<>();
        for (int draw = 0; draw < 200; draw++) {
            LearningQuestionResponse next = getNext("")
                    .expectStatus().isOk()
                    .expectBody(LearningQuestionResponse.class)
                    .returnResult()
                    .getResponseBody();
            assertNotNull(next);
            timesReturned.merge(next.getId(), 1, Integer::sum);
        }

        // Then
        int likedTimes = timesReturned.getOrDefault(liked, 0);
        int notLikedTimes = timesReturned.getOrDefault(notLiked, 0);
        assertTrue(notLikedTimes > 0, "the question nobody liked must come up sometimes");
        assertTrue(likedTimes >= 125, "the liked question must come up more often, but came up " + likedTimes + " of 200 times");
    }

    @Test
    @DisplayName("""
            Given a question on a gym concept and one on a member's own published concept
            When the learner asks for the next question without picking concepts (all muscles)
            Then only the gym's question comes up, counted alone: all muscles is the gym's pool
            """)
    void getNextQuestionForAllMusclesDrawsFromFeaturedConceptsOnly() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        Long gymQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long customQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        actingAs(OTHER_USER);

        // When / Then
        getNext("").expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(gymQuestion.intValue())
                .jsonPath("$.unansweredCount").isEqualTo(1);
        getNext("?skip=" + gymQuestion).expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given a question on a member's own published concept
            When the learner picks that concept
            Then the question is returned: community concepts are trained by picking them
            """)
    void getNextQuestionServesCustomConceptsWhenPicked() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        Long customQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?concepts=my-katas");

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(customQuestion.intValue())
                .jsonPath("$.unansweredCount").isEqualTo(1);
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
            Given a new question
            When the learner asks for the next question with an empty entry in the skip list
            Then the question is still returned: the empty entry is ignored, instead of making every
            question look skipped and ending the workout early
            """)
    void getNextQuestionIgnoresAnEmptySkipEntry() {
        // Given
        insertConcept("java-core");
        Long question = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?skip=&skip=" + (question + 1000));

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(question.intValue());
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
            and others that don't count (a benched one, one on another concept, one only someone else answered elsewhere)
            When the learner sends a GET request to the next question endpoint for that concept
            Then the response says how many questions on it the learner has never answered, the returned one included
            """)
    void getNextQuestionCountsTheQuestionsTheLearnerNeverAnswered() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long neverAnswered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
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

    @Test
    @DisplayName("""
            Given the only available question has three open reports, from three learners
            When a learner sends a GET request to the next question endpoint
            Then no content is returned: the question is held back until the reports are decided
            """)
    void getNextQuestionHoldsBackQuestionWithThreeOpenReports() {
        // Given
        insertConcept("java-core");
        Long reported = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(reported, AUTHOR);
        insertReport(reported, OTHER_USER);
        insertReport(reported, ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given the only available question has two open reports and a dismissed one
            When a learner sends a GET request to the next question endpoint
            Then it is still returned, as only open reports hold a question back
            """)
    void getNextQuestionStillServesQuestionWithFewerThanThreeOpenReports() {
        // Given
        insertConcept("java-core");
        Long reported = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(reported, AUTHOR);
        insertReport(reported, OTHER_USER);
        setReportStatus(insertReport(reported, ADMIN), "DISMISSED", AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(reported.intValue());
    }

    @Test
    @DisplayName("""
            Given the only available question has three open pieces of praise, from three learners
            When a learner sends a GET request to the next question endpoint
            Then it is still returned, as only problems hold a question back
            """)
    void getNextQuestionStillServesQuestionWithOpenPraise() {
        // Given
        insertConcept("java-core");
        Long praised = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(praised, AUTHOR, ReportReason.PRAISE);
        insertReport(praised, OTHER_USER, ReportReason.PRAISE);
        insertReport(praised, ADMIN, ReportReason.PRAISE);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(praised.intValue());
    }

    @Test
    @DisplayName("""
            Given two available questions, one of them answered by a member
            When a visitor who is not signed in sends a GET request to the next question endpoint
            Then a question is returned without its sample solution, and both count as unanswered,
            as a visitor has no history
            """)
    void getNextQuestionServesVisitorsAsIfEverythingIsNew() {
        // Given
        insertConcept("java-core");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        submitAnswer(answered, "Their answer");
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getNext("");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.sampleSolution").doesNotExist()
                .jsonPath("$.unansweredCount").isEqualTo(2)
                .jsonPath("$.myVote").isEmpty();
    }

    @Test
    @DisplayName("""
            Given two available questions on a concept, only one of them verified
            When a learner asks for the next question from the verified pool, skipping the verified one
            Then 204 is returned: the unverified question is left out
            """)
    void getNextQuestionLeavesOutUnverifiedQuestionsFromTheVerifiedPool() {
        // Given
        insertConcept("java-core");
        Long verified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionVerifiedBy(verified, ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=VERIFIED&skip=" + verified);

        // Then
        response.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given two available questions on a concept, only one of them verified
            When a learner asks for the next question from the verified pool
            Then the verified question is returned, marked as verified, and the count of new
            questions counts only verified ones: 1
            """)
    void getNextQuestionReturnsTheVerifiedQuestionAndCountsOnlyVerifiedOnes() {
        // Given
        insertConcept("java-core");
        Long verified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionVerifiedBy(verified, ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=VERIFIED");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(verified.intValue())
                .jsonPath("$.verified").isEqualTo(true)
                .jsonPath("$.unansweredCount").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given an available question that isn't verified yet
            When a learner asks for the next question without a pool, or from the pool of all
            Then it's returned both times, marked as not verified
            """)
    void getNextQuestionIncludesUnverifiedQuestionsByDefault() {
        // Given
        insertConcept("java-core");
        Long unverified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When / Then
        getNext("").expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(unverified.intValue())
                .jsonPath("$.verified").isEqualTo(false);
        getNext("?pool=ALL").expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(unverified.intValue());
    }

    @Test
    @DisplayName("""
            Given three questions on a gym concept: the oldest verified, the next one already answered
            by the staff member, the newest not
            When gym staff ask for the next question to verify
            Then the unanswered one is returned, as in any workout (not the oldest), and the count is
            what's left to verify, answered or not: 2
            """)
    void getNextQuestionToVerifyServesUnansweredFirstAndCountsWhatsLeft() {
        // Given
        insertConcept("java-core");
        Long verified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long answered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long unanswered = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionVerifiedBy(verified, ADMIN);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);
        submitAnswer(answered, "Their answer");

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=TO_VERIFY");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(unanswered.intValue())
                .jsonPath("$.verified").isEqualTo(false)
                .jsonPath("$.canVerify").isEqualTo(true)
                .jsonPath("$.unansweredCount").isEqualTo(2);
    }

    @Test
    @DisplayName("""
            Given two unverified questions on a gym concept
            When gym staff ask for the next question to verify, skipping one of them
            Then the other is returned: skipping works as in any workout
            """)
    void getNextQuestionToVerifyLeavesOutSkippedQuestions() {
        // Given
        insertConcept("java-core");
        Long skipped = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long other = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=TO_VERIFY&skip=" + skipped);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(other.intValue());
    }

    @Test
    @DisplayName("""
            Given unverified questions on a gym concept and on a member's own concept
            When that member asks for the next question to verify, on both concepts
            Then only the one on their own concept is returned: the gym's are staff's to check
            """)
    void getNextQuestionToVerifyServesAMemberOnlyTheirOwnConcepts() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long onOwnConcept = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=TO_VERIFY&concepts=java-core,my-katas");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(onOwnConcept.intValue())
                .jsonPath("$.unansweredCount").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given an unverified question on a gym concept
            When a visitor asks for the next question to verify
            Then 204 is returned: a visitor verifies nothing
            """)
    void getNextQuestionToVerifyServesVisitorsNothing() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getNext("?pool=TO_VERIFY");

        // Then
        response.expectStatus().isNoContent();
    }

    private RestTestClient.ResponseSpec getNext(String query) {
        return restClient
                .get()
                .uri("/learning/questions/next" + query)
                .exchange();
    }
}
