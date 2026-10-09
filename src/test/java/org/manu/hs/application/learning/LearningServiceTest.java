package org.manu.hs.application.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.ReportQuestionCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.application.learning.exception.AnswerAssessmentForbiddenException;
import org.manu.hs.application.learning.exception.OpenReportNotFoundException;
import org.manu.hs.application.learning.exception.ReportAlreadyOpenException;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.answer.AnswerRepository;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.question.Vote;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for LearningService: its validation, rules and builders, without Spring or a
 * database.
 */
class LearningServiceTest {

    private static final Long LEARNER = 1001L;

    private final QuestionRepository questionRepository = mock(QuestionRepository.class);

    private final LearningService learningService = new LearningService(
            questionRepository,
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
                        "answer is required"),
                arguments("answer is too long",
                        SubmitAnswerCommand.builder().questionId(42L).answer("x".repeat(10_001)).build(),
                        "answer must be at most 10000 characters"),
                arguments("answer holds a NUL character",
                        SubmitAnswerCommand.builder().questionId(42L).answer("It prints\u0000 1").build(),
                        "answer must not contain the NUL character"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidAnswers")
    @DisplayName("""
            Given an answer that breaks a field rule
            When it is validated
            Then it is refused, naming the field and the rule
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

    @Test
    @DisplayName("""
            Given a report whose note suggests a rewrite of 2000 characters
            When it is validated
            Then it is allowed, as a suggested fix may need the room
            """)
    void validateAllowsReportNoteOfMaxLength() {
        // Given
        ReportQuestionCommand command = ReportQuestionCommand.builder()
                .reason(ReportReason.UNCLEAR)
                .note("x".repeat(2000))
                .build();

        // When / Then
        assertDoesNotThrow(() -> learningService.validate(command));
    }

    static Stream<Arguments> invalidReports() {
        return Stream.of(
                arguments("reason is missing",
                        ReportQuestionCommand.builder().note("The model answer is wrong").build(),
                        "reason is required"),
                arguments("note is too long",
                        ReportQuestionCommand.builder().reason(ReportReason.OTHER).note("x".repeat(2001)).build(),
                        "note must be at most 2000 characters"),
                arguments("note hides a left-to-right isolate",
                        ReportQuestionCommand.builder().reason(ReportReason.OTHER).note("Wrong ⁦answer").build(),
                        "note must not contain invisible characters"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidReports")
    @DisplayName("""
            Given a report that breaks a field rule
            When it is validated
            Then it is refused, naming the field and the rule
            """)
    void validateRejectsInvalidReport(String description, ReportQuestionCommand command, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> learningService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a report with a reason and no note
            When it is validated
            Then it is allowed, as the note is optional
            """)
    void validateAllowsReportWithoutNote() {
        // Given
        ReportQuestionCommand command = ReportQuestionCommand.builder().reason(ReportReason.UNCLEAR).build();

        // When / Then
        assertDoesNotThrow(() -> learningService.validate(command));
    }

    @Test
    @DisplayName("""
            Given a report whose note spans two lines and uses a tab
            When it is validated
            Then it is allowed: only invisible characters are refused in notes, not line breaks
            """)
    void validateAllowsMultiLineNote() {
        // Given
        ReportQuestionCommand command = ReportQuestionCommand.builder()
                .reason(ReportReason.WRONG)
                .note("Step 2 is wrong:\n\tHashMap isn't ordered.")
                .build();

        // When / Then
        assertDoesNotThrow(() -> learningService.validate(command));
    }

    @Test
    @DisplayName("""
            Given a vote that is neither up nor down
            When it is validated
            Then it is refused, naming the field
            """)
    void validateRejectsMissingVote() {
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> learningService.validate((Vote) null));

        // Then
        assertEquals("vote is required", exception.getMessage());
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a learner who already has an open report on a question
            When they report it again
            Then it is refused, as one open report per learner is enough
            """)
    void requireNoOpenReportRejectsSecondReport() {
        // Given
        when(questionRepository.hasOpenReport(42L, LEARNER)).thenReturn(true);

        // When / Then
        assertThrows(ReportAlreadyOpenException.class, () -> learningService.requireNoOpenReport(42L, LEARNER));
    }

    @Test
    @DisplayName("""
            Given a learner with no open report on a question
            When they report it
            Then it is allowed
            """)
    void requireNoOpenReportAllowsFirstReport() {
        // Given
        when(questionRepository.hasOpenReport(42L, LEARNER)).thenReturn(false);

        // When / Then
        assertDoesNotThrow(() -> learningService.requireNoOpenReport(42L, LEARNER));
    }

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

    @Test
    @DisplayName("""
            Given a learner's open report that staff decide just before the learner's change lands
            When the change is saved
            Then it is refused as "no open feedback", and the decided report is left as decided
            """)
    void changeIsRefusedWhenTheReportWasDecidedMeanwhile() {
        // Given
        Report report = Report.builder().id(7L).question(Question.builder().id(42L).build()).build();
        when(questionRepository.updateOpenReportContent(7L, ReportReason.WRONG, "Still wrong")).thenReturn(Optional.empty());
        ReportQuestionCommand command = ReportQuestionCommand.builder().reason(ReportReason.WRONG).note("Still wrong").build();

        // When / Then
        assertThrows(OpenReportNotFoundException.class, () -> learningService.change(report, command));
    }

    @Test
    @DisplayName("""
            Given a learner's open report that staff decide just before the learner withdraws it
            When the withdrawal is saved
            Then it is refused as "no open feedback": a decided report stays in the question's history
            """)
    void withdrawIsRefusedWhenTheReportWasDecidedMeanwhile() {
        // Given
        Report report = Report.builder().id(7L).question(Question.builder().id(42L).build()).build();
        when(questionRepository.deleteOpenReport(7L)).thenReturn(false);

        // When / Then
        assertThrows(OpenReportNotFoundException.class, () -> learningService.withdraw(report));
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
