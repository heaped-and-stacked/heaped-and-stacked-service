package org.manu.hs.application.content;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.application.content.exception.QuestionHasDraftConceptsException;
import org.manu.hs.application.content.exception.QuestionModificationForbiddenException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

/**
 * Unit test for QuestionService: its validation, rules and builders, without Spring or
 * a database. None of the methods tested here touch the repository.
 */
class QuestionServiceTest {

    private static final Long AUTHOR = 1001L;
    private static final Instant CREATED = Instant.parse("2026-01-01T10:00:00Z");

    private final QuestionService questionService = new QuestionService(mock(QuestionRepository.class));

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a complete question, with no source
            When it is validated
            Then it is accepted, as the source is optional
            """)
    void validateAcceptsValidCommandWithoutSource() {
        assertDoesNotThrow(() -> questionService.validate(aSubmitCommand().source(null).build()));
    }

    @ParameterizedTest(name = "difficulty {0}")
    @ValueSource(ints = {1, 10})
    @DisplayName("""
            Given a question with a difficulty at the limits
            When it is validated
            Then it is accepted, as 1 and 10 are both allowed
            """)
    void validateAcceptsDifficultyAtTheLimits(int difficulty) {
        assertDoesNotThrow(() -> questionService.validate(aSubmitCommand().difficultyScore(difficulty).build()));
    }

    static Stream<Arguments> invalidCommands() {
        return Stream.of(
                arguments("requirement is null", aSubmitCommand().requirement(null).build(), "requirement is required"),
                arguments("requirement is blank", aSubmitCommand().requirement("   ").build(), "requirement is required"),
                arguments("sampleSolution is null", aSubmitCommand().sampleSolution(null).build(), "sampleSolution is required"),
                arguments("type is null", aSubmitCommand().type(null).build(), "type is required"),
                arguments("difficultyScore is null", aSubmitCommand().difficultyScore(null).build(), "difficultyScore is required"),
                arguments("difficultyScore is 0", aSubmitCommand().difficultyScore(0).build(), "difficultyScore must be between 1 and 10"),
                arguments("difficultyScore is 11", aSubmitCommand().difficultyScore(11).build(), "difficultyScore must be between 1 and 10"),
                arguments("expectedDurationMinutes is null", aSubmitCommand().expectedDurationMinutes(null).build(), "expectedDurationMinutes is required"),
                arguments("expectedDurationMinutes is 0", aSubmitCommand().expectedDurationMinutes(0).build(), "expectedDurationMinutes must be greater than 0"),
                arguments("conceptTags is null", aSubmitCommand().conceptTags(null).build(), "conceptTags is required"),
                arguments("conceptTags is empty", aSubmitCommand().conceptTags(List.of()).build(), "conceptTags is required"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCommands")
    @DisplayName("""
            Given a question that breaks one field rule
            When it is validated
            Then a validation error names the field and the rule
            """)
    void validateRejectsInvalidCommand(String description, SubmitQuestionCommand command, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> questionService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a question edit that breaks a field rule
            When it is validated
            Then it is refused by the same rules as a new question
            """)
    void validateUpdateAppliesTheSameRules() {
        // Given
        UpdateQuestionCommand command = anUpdateCommand().difficultyScore(11).build();

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> questionService.validate(command));

        // Then
        assertEquals("difficultyScore must be between 1 and 10", exception.getMessage());
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{0} (admin: {1}) on a {2} question -> allowed: {3}")
    @CsvSource(textBlock = """
            # caller,    admin, status,  allowed
            1001,        false, DRAFT,   true
            1001,        false, ACTIVE,  false
            1001,        false, RETIRED, false
            1002,        false, DRAFT,   false
            1002,        false, ACTIVE,  false
            1002,        false, RETIRED, false
            1003,        true,  DRAFT,   true
            1003,        true,  ACTIVE,  true
            1003,        true,  RETIRED, true
            """)
    @DisplayName("""
            Given a question written by an author, in a given status
            When a caller wants to modify it
            Then only the author of a DRAFT, or an admin in any status, is allowed
            """)
    void requireCanBeModifiedByAllowsAuthorOfDraftOrAnyAdmin(Long caller, boolean admin, QuestionStatus status, boolean allowed) {
        // Given
        Question question = aQuestion().status(status).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> questionService.requireCanBeModifiedBy(question, caller, admin));
        } else {
            assertThrows(QuestionModificationForbiddenException.class,
                    () -> questionService.requireCanBeModifiedBy(question, caller, admin));
        }
    }

    @Test
    @DisplayName("""
            Given linked concepts where some are still drafts
            When a question requires approved concepts
            Then it is refused, naming only the draft concepts
            """)
    void requireNoDraftConceptsNamesTheDraftConcepts() {
        // Given
        List<Concept> concepts = List.of(
                aConcept("java-core", ConceptStatus.ACTIVE),
                aConcept("java-records", ConceptStatus.DRAFT),
                aConcept("java-modules", ConceptStatus.DRAFT));

        // When
        QuestionHasDraftConceptsException exception = assertThrows(
                QuestionHasDraftConceptsException.class,
                () -> questionService.requireNoDraftConcepts(concepts));

        // Then
        assertEquals("A question can't be active while these concepts are drafts: java-records, java-modules",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given linked concepts that are active or retired
            When a question requires approved concepts
            Then it is allowed, as only drafts block
            """)
    void requireNoDraftConceptsAllowsActiveAndRetiredConcepts() {
        // Given
        List<Concept> concepts = List.of(
                aConcept("java-core", ConceptStatus.ACTIVE),
                aConcept("java-applets", ConceptStatus.RETIRED));

        // When / Then
        assertDoesNotThrow(() -> questionService.requireNoDraftConcepts(concepts));
    }

    // ---------------------------------------------------------------------
    // Builders
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a new question listing some concept tags twice
            When the question to store is built
            Then duplicates are dropped keeping the first occurrence, and the caller is author and updater
            """)
    void newQuestionDropsDuplicateTagsAndRecordsTheAuthor() {
        // Given
        SubmitQuestionCommand command = aSubmitCommand()
                .conceptTags(List.of("java-generics", "java-core", "java-generics"))
                .build();

        // When
        Question question = questionService.newQuestion(command, QuestionStatus.DRAFT, AUTHOR);

        // Then
        assertEquals(List.of("java-generics", "java-core"), question.getConceptTags());
        assertEquals(QuestionStatus.DRAFT, question.getStatus());
        assertEquals(AUTHOR, question.getCreatedBy());
        assertEquals(AUTHOR, question.getUpdatedBy());
        assertEquals("Explain == vs equals", question.getRequirement());
    }

    @Test
    @DisplayName("""
            Given a stored question and an edit by an admin
            When the edited question is built
            Then content comes from the edit, while id, status, author and creation time are kept
            """)
    void updatedQuestionKeepsIdentityStatusAndAuthor() {
        // Given
        Question existing = aQuestion().status(QuestionStatus.ACTIVE).build();
        UpdateQuestionCommand command = anUpdateCommand()
                .requirement("Updated requirement")
                .type(QuestionType.GOTCHA)
                .conceptTags(List.of("java-core", "java-core"))
                .build();

        // When
        Question question = questionService.updatedQuestion(existing, command, 1003L);

        // Then
        assertEquals("Updated requirement", question.getRequirement());
        assertEquals(QuestionType.GOTCHA, question.getType());
        assertEquals(List.of("java-core"), question.getConceptTags());
        assertEquals(1003L, question.getUpdatedBy());
        assertEquals(42L, question.getId());
        assertEquals(QuestionStatus.ACTIVE, question.getStatus());
        assertEquals(AUTHOR, question.getCreatedBy());
        assertEquals(CREATED, question.getCreatedAt());
    }

    private static SubmitQuestionCommand.SubmitQuestionCommandBuilder aSubmitCommand() {
        return SubmitQuestionCommand.builder()
                .requirement("Explain == vs equals")
                .sampleSolution("Reference vs logical equality")
                .type(QuestionType.DEFINITION)
                .difficultyScore(3)
                .expectedDurationMinutes(5)
                .source("manu")
                .conceptTags(List.of("java-core"));
    }

    private static UpdateQuestionCommand.UpdateQuestionCommandBuilder anUpdateCommand() {
        return UpdateQuestionCommand.builder()
                .requirement("Explain == vs equals")
                .sampleSolution("Reference vs logical equality")
                .type(QuestionType.DEFINITION)
                .difficultyScore(3)
                .expectedDurationMinutes(5)
                .source("manu")
                .conceptTags(List.of("java-core"));
    }

    private static Question.QuestionBuilder aQuestion() {
        return Question.builder()
                .id(42L)
                .requirement("Original requirement")
                .sampleSolution("Original solution")
                .type(QuestionType.DEFINITION)
                .difficultyScore(2)
                .expectedDurationMinutes(5)
                .status(QuestionStatus.DRAFT)
                .conceptTags(List.of("java-core"))
                .createdBy(AUTHOR)
                .updatedBy(AUTHOR)
                .createdAt(CREATED);
    }

    private static Concept aConcept(String tag, ConceptStatus status) {
        return Concept.builder().tag(tag).status(status).build();
    }
}
