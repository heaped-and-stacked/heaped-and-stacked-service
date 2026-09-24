package org.manu.hs.application.content;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.exception.ConceptModificationForbiddenException;
import org.manu.hs.application.content.exception.InvalidConceptTagException;
import org.manu.hs.application.content.exception.UnknownConceptTagException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for ConceptService: its validation, rules and builders, without Spring or a
 * database. The repository is a Mockito mock, only needed where a lookup result matters.
 */
class ConceptServiceTest {

    private final ConceptRepository conceptRepository = mock(ConceptRepository.class);
    private final ConceptService conceptService = new ConceptService(conceptRepository);

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "tag \"{0}\"")
    @ValueSource(strings = {"java", "java-core", "java-8", "spring-boot-4"})
    @DisplayName("""
            Given a new concept whose tag is lowercase words joined by single hyphens
            When it is validated
            Then it is accepted
            """)
    void validateAcceptsKebabCaseTag(String tag) {
        // Given
        CreateConceptCommand command = aCreateCommand().tag(tag).build();

        // When / Then
        assertDoesNotThrow(() -> conceptService.validate(command));
    }

    @ParameterizedTest(name = "tag \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"Java Core!", "JavaCore", "java_core", "java core", "-java", "java-", "java--core"})
    @DisplayName("""
            Given a new concept whose tag is not kebab-case
            When it is validated
            Then it is refused
            """)
    void validateRejectsTagThatIsNotKebabCase(String tag) {
        // Given
        CreateConceptCommand command = aCreateCommand().tag(tag).build();

        // When / Then
        assertThrows(InvalidConceptTagException.class, () -> conceptService.validate(command));
    }

    static Stream<Arguments> missingFields() {
        return Stream.of(
                arguments("name is null", aCreateCommand().name(null).build(), "name is required"),
                arguments("name is blank", aCreateCommand().name("   ").build(), "name is required"),
                arguments("description is null", aCreateCommand().description(null).build(), "description is required"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingFields")
    @DisplayName("""
            Given a new concept missing a required field
            When it is validated
            Then a validation error names the field
            """)
    void validateRejectsMissingRequiredField(String description, CreateConceptCommand command, String expectedMessage) {
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> conceptService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a concept edit without a name
            When it is validated
            Then a validation error names the field
            """)
    void validateUpdateRejectsMissingName() {
        // Given
        UpdateConceptCommand command = UpdateConceptCommand.builder()
                .description("Fundamentals")
                .externalReferences(List.of())
                .build();

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> conceptService.validate(command));

        // Then
        assertEquals("name is required", exception.getMessage());
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given tags that all have a concept
            When the concepts are looked up
            Then they are returned
            """)
    void findByTagsOrThrowReturnsConceptsWhenAllExist() {
        // Given
        List<Concept> stored = List.of(aConcept("java-core"), aConcept("java-generics"));
        when(conceptRepository.findByTags(List.of("java-core", "java-generics"))).thenReturn(stored);

        // When
        List<Concept> concepts = conceptService.findByTagsOrThrow(List.of("java-core", "java-generics"));

        // Then
        assertEquals(stored, concepts);
    }

    @Test
    @DisplayName("""
            Given tags where some have no concept
            When the concepts are looked up
            Then the first unknown tag, in the order given, is reported
            """)
    void findByTagsOrThrowReportsFirstUnknownTag() {
        // Given
        List<String> tags = List.of("java-core", "ghost", "phantom");
        when(conceptRepository.findByTags(tags)).thenReturn(List.of(aConcept("java-core")));

        // When
        UnknownConceptTagException exception = assertThrows(
                UnknownConceptTagException.class,
                () -> conceptService.findByTagsOrThrow(tags));

        // Then
        assertEquals("Concept tag 'ghost' does not exist", exception.getMessage());
    }

    @ParameterizedTest(name = "{0} (admin: {1}) on a {2} concept -> allowed: {3}")
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
            Given a concept written by an author, in a given status
            When a caller wants to modify it
            Then only the author of a DRAFT, or an admin in any status, is allowed
            """)
    void requireCanBeModifiedByAllowsAuthorOfDraftOrAnyAdmin(Long caller, boolean admin, ConceptStatus status, boolean allowed) {
        // Given
        Concept concept = Concept.builder().tag("java-core").status(status).createdBy(1001L).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> conceptService.requireCanBeModifiedBy(concept, caller, admin));
        } else {
            assertThrows(ConceptModificationForbiddenException.class,
                    () -> conceptService.requireCanBeModifiedBy(concept, caller, admin));
        }
    }

    // ---------------------------------------------------------------------
    // Builders
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a new concept command and a starting status
            When the concept to store is built
            Then it carries the command's fields and the given status
            """)
    void newConceptCopiesCommandAndStatus() {
        // Given
        CreateConceptCommand command = aCreateCommand().build();

        // When
        Concept concept = conceptService.newConcept(command, ConceptStatus.DRAFT);

        // Then
        assertEquals("java-core", concept.getTag());
        assertEquals("Java Core", concept.getName());
        assertEquals("Fundamentals of Java", concept.getDescription());
        assertEquals(List.of("https://dev.java/learn/"), concept.getExternalReferences());
        assertEquals(ConceptStatus.DRAFT, concept.getStatus());
    }

    @Test
    @DisplayName("""
            Given a new concept command whose reference list is changed afterwards
            When the concept to store has been built
            Then the concept keeps its own copy of the references
            """)
    void newConceptKeepsItsOwnCopyOfReferences() {
        // Given
        List<String> references = new ArrayList<>(List.of("https://dev.java/learn/"));
        CreateConceptCommand command = aCreateCommand().externalReferences(references).build();

        // When
        Concept concept = conceptService.newConcept(command, ConceptStatus.DRAFT);
        references.add("https://added.later/");

        // Then
        assertEquals(List.of("https://dev.java/learn/"), concept.getExternalReferences());
    }

    @Test
    @DisplayName("""
            Given a concept edit
            When the edited concept is built
            Then it takes the tag from the path and carries no status, since an edit never changes it
            """)
    void updatedConceptUsesPathTagAndNoStatus() {
        // Given
        UpdateConceptCommand command = UpdateConceptCommand.builder()
                .name("Advanced Java Core")
                .description("Revisited")
                .externalReferences(List.of())
                .build();

        // When
        Concept concept = conceptService.updatedConcept("java-core", command);

        // Then
        assertEquals("java-core", concept.getTag());
        assertEquals("Advanced Java Core", concept.getName());
        assertNull(concept.getStatus());
    }

    private static CreateConceptCommand.CreateConceptCommandBuilder aCreateCommand() {
        return CreateConceptCommand.builder()
                .tag("java-core")
                .name("Java Core")
                .description("Fundamentals of Java")
                .externalReferences(List.of("https://dev.java/learn/"));
    }

    private static Concept aConcept(String tag) {
        return Concept.builder().tag(tag).status(ConceptStatus.ACTIVE).build();
    }
}
