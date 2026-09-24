package org.manu.hs.application.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.application.learning.exception.AnswerAssessmentForbiddenException;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.answer.AnswerRepository;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

/**
 * Unit test for LearningService: its validation, rules and builders, without Spring or a
 * database.
 */
class LearningServiceTest {

    private static final Long LEARNER = 1001L;

    private final LearningService learningService = new LearningService(
            mock(QuestionRepository.class),
            mock(AnswerRepository.class));

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    static Stream<Arguments> invalidAnswers() {
        return Stream.of(
                arguments("question id is missing",
                        SubmitAnswerCommand.builder().answer("It prints 1").build(),
                        "questionId is required"),
                arguments("answer is missing",
                        SubmitAnswerCommand.builder().questionId(42L).build(),
                        "answer is required"),
                arguments("answer is blank",
                        SubmitAnswerCommand.builder().questionId(42L).answer("   ").build(),
                        "answer is required"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidAnswers")
    @DisplayName("""
            Given an answer that is missing a required field
            When it is validated
            Then it is refused, naming the field
            """)
    void validateRejectsIncompleteAnswer(String description, SubmitAnswerCommand command, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> learningService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> invalidAssessments() {
        return Stream.of(
                arguments("self grade below 1", 0, null, "selfGrade must be between 1 and 10"),
                arguments("self grade above 10", 11, null, "selfGrade must be between 1 and 10"),
                arguments("perceived difficulty below 1", null, 0, "perceivedDifficulty must be between 1 and 10"),
                arguments("perceived difficulty above 10", null, 11, "perceivedDifficulty must be between 1 and 10"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidAssessments")
    @DisplayName("""
            Given a self-assessment with a value outside 1 to 10
            When it is validated
            Then it is refused, naming the field
            """)
    void validateRejectsAssessmentOutOfRange(String description, Integer selfGrade, Integer perceivedDifficulty, String expectedMessage) {
        // Given
        AssessAnswerCommand command = AssessAnswerCommand.builder()
                .selfGrade(selfGrade)
                .perceivedDifficulty(perceivedDifficulty)
                .build();

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> learningService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a self-assessment that leaves both values out
            When it is validated
            Then it is allowed, as both parts are optional
            """)
    void validateAllowsEmptyAssessment() {
        // Given
        AssessAnswerCommand command = AssessAnswerCommand.builder().build();

        // When / Then
        assertDoesNotThrow(() -> learningService.validate(command));
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given an answer given by someone else
            When a learner wants to assess it
            Then it is refused, as a self-assessment belongs to whoever answered
            """)
    void requireOwnAnswerRejectsSomeoneElsesAnswer() {
        // Given
        Answer answer = Answer.builder().id(7L).userId(1002L).build();

        // When / Then
        assertThrows(AnswerAssessmentForbiddenException.class, () -> learningService.requireOwnAnswer(answer, LEARNER));
    }

    @Test
    @DisplayName("""
            Given an answer given by the learner
            When they want to assess it
            Then it is allowed
            """)
    void requireOwnAnswerAllowsOwnAnswer() {
        // Given
        Answer answer = Answer.builder().id(7L).userId(LEARNER).build();

        // When / Then
        assertDoesNotThrow(() -> learningService.requireOwnAnswer(answer, LEARNER));
    }

    // ---------------------------------------------------------------------
    // Builders
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a learner's answer to a question
            When the answer to store is built
            Then it keeps the learner, the text and the question exactly as it was answered
            """)
    void newAnswerKeepsTheQuestionAsAnswered() {
        // Given
        Question question = Question.builder().id(42L).requirement("What does this print?").build();
        SubmitAnswerCommand command = SubmitAnswerCommand.builder().questionId(42L).answer("It prints 1").build();

        // When
        Answer answer = learningService.newAnswer(command, question, LEARNER);

        // Then
        assertEquals(LEARNER, answer.getUserId());
        assertEquals(42L, answer.getQuestionId());
        assertEquals("It prints 1", answer.getAnswer());
        assertSame(question, answer.getQuestion());
    }
}
