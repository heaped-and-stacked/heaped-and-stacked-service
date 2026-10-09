package org.manu.hs.application.content;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.application.common.exception.base.ForbiddenException;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.application.content.exception.QuestionHasDraftConceptsException;
import org.manu.hs.application.content.exception.QuestionModificationForbiddenException;
import org.manu.hs.application.content.exception.ReportResolutionForbiddenException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportStatus;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
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

    @Test
    @DisplayName("""
            Given a question whose title has an emoji made of joined parts (a zero-width joiner)
            and whose text holds code with tabs and line breaks
            When it is validated
            Then it is accepted: the joiner is part of the emoji, and exercise texts may hold any code
            """)
    void validateAcceptsJoinedEmojiInTitleAndAnyCharactersInText() {
        assertDoesNotThrow(() -> questionService.validate(aSubmitCommand()
                .title("Debugging like a 👩‍💻")
                .requirement("for (;;) {\n\tif (x) break;\n}")
                .build()));
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
                arguments("title is null", aSubmitCommand().title(null).build(), "title is required"),
                arguments("title is blank", aSubmitCommand().title("   ").build(), "title is required"),
                arguments("title is too long", aSubmitCommand().title("x".repeat(121)).build(), "title must be at most 120 characters"),
                arguments("title has a line break", aSubmitCommand().title("Two\nlines").build(), "title must be one line without invisible characters"),
                arguments("title hides a right-to-left override", aSubmitCommand().title("Safe ‮txt.exe").build(), "title must be one line without invisible characters"),
                arguments("requirement is null", aSubmitCommand().requirement(null).build(), "requirement is required"),
                arguments("requirement is blank", aSubmitCommand().requirement("   ").build(), "requirement is required"),
                arguments("requirement is too long", aSubmitCommand().requirement("x".repeat(10_001)).build(), "requirement must be at most 10000 characters"),
                arguments("sampleSolution is null", aSubmitCommand().sampleSolution(null).build(), "sampleSolution is required"),
                arguments("sampleSolution is too long", aSubmitCommand().sampleSolution("x".repeat(10_001)).build(), "sampleSolution must be at most 10000 characters"),
                arguments("source is too long", aSubmitCommand().source("x".repeat(201)).build(), "source must be at most 200 characters"),
                arguments("source has a zero-width space", aSubmitCommand().source("manu​").build(), "source must be one line without invisible characters"),
                arguments("too many conceptTags", aSubmitCommand().conceptTags(IntStream.rangeClosed(1, 11).mapToObj(i -> "tag-" + i).toList()).build(), "conceptTags must have at most 10 entries"),
                arguments("difficultyScore is null", aSubmitCommand().difficultyScore(null).build(), "difficultyScore is required"),
                arguments("difficultyScore is 0", aSubmitCommand().difficultyScore(0).build(), "difficultyScore must be between 1 and 10"),
                arguments("difficultyScore is 11", aSubmitCommand().difficultyScore(11).build(), "difficultyScore must be between 1 and 10"),
                arguments("conceptTags is null", aSubmitCommand().conceptTags(null).build(), "conceptTags is required"),
                arguments("conceptTags is empty", aSubmitCommand().conceptTags(List.of()).build(), "conceptTags is required"),
                arguments("requirement holds a NUL character", aSubmitCommand().requirement("a\u0000b").build(), "requirement must not contain the NUL character"),
                arguments("sampleSolution holds a NUL character", aSubmitCommand().sampleSolution("a\u0000b").build(), "sampleSolution must not contain the NUL character"),
                arguments("a conceptTag is null", aSubmitCommand().conceptTags(Arrays.asList("java-core", null)).build(), "conceptTags must not contain empty entries"),
                arguments("a conceptTag is blank", aSubmitCommand().conceptTags(List.of("java-core", " ")).build(), "conceptTags must not contain empty entries"));
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

    @ParameterizedTest(name = "{0}")
    @CsvSource(textBlock = """
            # decision, expected message
            ,           status is required
            PENDING,    status must be ACCEPTED or DECLINED
            """)
    @DisplayName("""
            Given a review decision that is missing or isn't a decision
            When it is validated
            Then it is refused, as a review either accepts or declines
            """)
    void validateReviewDecisionRejectsAnythingButAcceptOrDecline(ConceptLinkStatus decision, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class,
                () -> questionService.validateReviewDecision(decision));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"ACCEPTED", "DECLINED"})
    @DisplayName("""
            Given a review that accepts or declines
            When it is validated
            Then it is allowed
            """)
    void validateReviewDecisionAllowsAcceptAndDecline(ConceptLinkStatus decision) {
        // When / Then
        assertDoesNotThrow(() -> questionService.validateReviewDecision(decision));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(textBlock = """
            # decision, expected message
            ,           status is required
            OPEN,       status must be RESOLVED or DISMISSED
            """)
    @DisplayName("""
            Given a report decision that is missing or isn't a decision
            When it is validated
            Then it is refused, as a report is either resolved or dismissed
            """)
    void validateReportDecisionRejectsAnythingButResolveOrDismiss(ReportStatus decision, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class,
                () -> questionService.validateReportDecision(decision));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"RESOLVED", "DISMISSED"})
    @DisplayName("""
            Given a report decision that resolves or dismisses
            When it is validated
            Then it is allowed
            """)
    void validateReportDecisionAllowsResolveAndDismiss(ReportStatus decision) {
        // When / Then
        assertDoesNotThrow(() -> questionService.validateReportDecision(decision));
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{0} (staff: {1}) -> allowed: {2}")
    @CsvSource(textBlock = """
            # caller,    staff, allowed
            1001,        false, true
            1004,        false, true
            1002,        false, false
            1003,        true,  true
            """)
    @DisplayName("""
            Given a report on a question by 1001, linked to a concept owned by 1004
            When a caller wants to resolve or dismiss it
            Then only the question's author, the concept's owner or staff may
            """)
    void requireCanDecideReportAllowsAuthorConceptOwnerOrStaff(Long caller, boolean staff, boolean allowed) {
        // Given
        Report report = Report.builder().id(7L).question(aQuestion().build()).build();
        List<Concept> concepts = List.of(aConcept("java-core", ConceptStatus.ACTIVE).toBuilder().createdBy(1004L).build());

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> questionService.requireCanDecideReport(report, concepts, caller, staff));
        } else {
            assertThrows(ReportResolutionForbiddenException.class,
                    () -> questionService.requireCanDecideReport(report, concepts, caller, staff));
        }
    }

    @ParameterizedTest(name = "{0} (staff: {1}) on a {2} question -> allowed: {3}")
    @CsvSource(textBlock = """
            # caller,    staff, status,    allowed
            1001,        false, DRAFT,     true
            1001,        false, SUBMITTED, true
            1001,        false, RETIRED,   true
            1002,        false, DRAFT,     false
            1002,        false, SUBMITTED, false
            1002,        false, RETIRED,   false
            1003,        true,  DRAFT,     true
            1003,        true,  SUBMITTED, true
            1003,        true,  RETIRED,   true
            """)
    @DisplayName("""
            Given a question written by an author, in a given status
            When a caller wants to edit it
            Then only the author or staff may, in any status
            """)
    void requireCanBeModifiedByAllowsAuthorOrAnyStaff(Long caller, boolean staff, QuestionStatus status, boolean allowed) {
        // Given
        Question question = aQuestion().status(status).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> questionService.requireCanBeModifiedBy(question, caller, staff));
        } else {
            assertThrows(QuestionModificationForbiddenException.class,
                    () -> questionService.requireCanBeModifiedBy(question, caller, staff));
        }
    }

    @ParameterizedTest(name = "{0} (staff: {1}) on a {2} question -> allowed: {3}")
    @CsvSource(textBlock = """
            # caller,    staff, status,    allowed
            1001,        false, DRAFT,     true
            1001,        false, SUBMITTED, false
            1001,        false, RETIRED,   false
            1002,        false, DRAFT,     false
            1002,        false, SUBMITTED, false
            1002,        false, RETIRED,   false
            1003,        true,  DRAFT,     true
            1003,        true,  SUBMITTED, true
            1003,        true,  RETIRED,   true
            """)
    @DisplayName("""
            Given a question written by an author, in a given status
            When a caller wants to delete it
            Then only the author of a DRAFT, or staff in any status, may
            """)
    void requireCanBeDeletedByAllowsAuthorOfDraftOrAnyStaff(Long caller, boolean staff, QuestionStatus status, boolean allowed) {
        // Given
        Question question = aQuestion().status(status).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> questionService.requireCanBeDeletedBy(question, caller, staff));
        } else {
            assertThrows(ForbiddenException.class,
                    () -> questionService.requireCanBeDeletedBy(question, caller, staff));
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
        assertEquals("A question can't be submitted while these concepts are drafts: java-records, java-modules",
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

    @ParameterizedTest(name = "{0} concept owned by 1001, question by {1} (staff: {2}) -> awaits approval: {3}")
    @CsvSource(textBlock = """
            # policy,        author, staff, awaits
            AUTO_ACCEPT,     1002,   false, false
            AUTO_ACCEPT,     1001,   false, false
            OWNER_APPROVAL,  1002,   false, true
            OWNER_APPROVAL,  1001,   false, false
            OWNER_APPROVAL,  1002,   true,  false
            """)
    @DisplayName("""
            Given a concept with a contribution policy, and a question's author
            When the question is linked to the concept
            Then it waits for approval only if the owner approves contributions and the author doesn't curate the concept
            """)
    void tagsAwaitingApprovalFollowsPolicyAndOwnership(ContributionPolicy policy, Long author, boolean staff, boolean awaits) {
        // Given
        Concept concept = aConcept("java-core", ConceptStatus.ACTIVE).toBuilder()
                .contributionPolicy(policy)
                .createdBy(1001L)
                .build();

        // When
        List<String> awaiting = questionService.tagsAwaitingApproval(List.of(concept), author, staff);

        // Then
        assertEquals(awaits ? List.of("java-core") : List.of(), awaiting);
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
        assertEquals("Equality of objects", question.getTitle());
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
        Question existing = aQuestion().status(QuestionStatus.SUBMITTED).build();
        UpdateQuestionCommand command = anUpdateCommand()
                .title("Updated title")
                .requirement("Updated requirement")
                .conceptTags(List.of("java-core", "java-core"))
                .build();

        // When
        Question question = questionService.updatedQuestion(existing, command, 1003L);

        // Then
        assertEquals("Updated title", question.getTitle());
        assertEquals("Updated requirement", question.getRequirement());
        assertEquals(List.of("java-core"), question.getConceptTags());
        assertEquals(1003L, question.getUpdatedBy());
        assertEquals(42L, question.getId());
        assertEquals(QuestionStatus.SUBMITTED, question.getStatus());
        assertEquals(AUTHOR, question.getCreatedBy());
        assertEquals(CREATED, question.getCreatedAt());
    }

    private static SubmitQuestionCommand.SubmitQuestionCommandBuilder aSubmitCommand() {
        return SubmitQuestionCommand.builder()
                .title("Equality of objects")
                .requirement("Explain == vs equals")
                .sampleSolution("Reference vs logical equality")
                .difficultyScore(3)
                .source("manu")
                .conceptTags(List.of("java-core"));
    }

    private static UpdateQuestionCommand.UpdateQuestionCommandBuilder anUpdateCommand() {
        return UpdateQuestionCommand.builder()
                .title("Equality of objects")
                .requirement("Explain == vs equals")
                .sampleSolution("Reference vs logical equality")
                .difficultyScore(3)
                .source("manu")
                .conceptTags(List.of("java-core"));
    }

    private static Question.QuestionBuilder aQuestion() {
        return Question.builder()
                .id(42L)
                .title("Original title")
                .requirement("Original requirement")
                .sampleSolution("Original solution")
                .difficultyScore(2)
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
