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
import org.manu.hs.application.content.exception.InvalidConceptTagException;
import org.manu.hs.application.content.exception.UnknownConceptTagException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final ConceptService conceptService = new ConceptService(conceptRepository, true, true);

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

    @Test
    @DisplayName("""
            Given a new concept whose tag is kebab-case but longer than 100 characters
            When it is validated
            Then it is refused
            """)
    void validateRejectsTooLongTag() {
        // Given
        CreateConceptCommand command = aCreateCommand().tag("a".repeat(101)).build();

        // When / Then
        assertThrows(InvalidConceptTagException.class, () -> conceptService.validate(command));
    }

    static Stream<Arguments> invalidFields() {
        return Stream.of(
                arguments("name is null", aCreateCommand().name(null).build(), "name is required"),
                arguments("name is blank", aCreateCommand().name("   ").build(), "name is required"),
                arguments("name is too long", aCreateCommand().name("x".repeat(201)).build(), "name must be at most 200 characters"),
                arguments("name has a control character", aCreateCommand().name("Java\u0007").build(), "name must be one line without invisible characters"),
                arguments("description is null", aCreateCommand().description(null).build(), "description is required"),
                arguments("description is too long", aCreateCommand().description("x".repeat(2_001)).build(), "description must be at most 2000 characters"),
                arguments("description hides a right-to-left override", aCreateCommand().description("Fine‮").build(), "description must not contain invisible characters"),
                arguments("too many externalReferences", aCreateCommand().externalReferences(Collections.nCopies(21, "https://dev.java/learn/")).build(), "externalReferences must have at most 20 entries"),
                arguments("an externalReference is too long", aCreateCommand().externalReferences(List.of("x".repeat(501))).build(), "externalReferences must each be at most 500 characters"),
                arguments("an externalReference has a line break", aCreateCommand().externalReferences(List.of("https://dev.java/\nlearn")).build(), "externalReferences must each be one line without invisible characters"),
                arguments("an externalReference is null", aCreateCommand().externalReferences(Arrays.asList("https://dev.java/learn/", null)).build(), "externalReferences must not contain empty entries"),
                arguments("an externalReference is blank", aCreateCommand().externalReferences(List.of(" ")).build(), "externalReferences must not contain empty entries"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidFields")
    @DisplayName("""
            Given a new concept that breaks one field rule
            When it is validated
            Then a validation error names the field and the rule
            """)
    void validateRejectsInvalidField(String description, CreateConceptCommand command, String expectedMessage) {
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

    @ParameterizedTest(name = "{0} (staff: {1}) on a concept featured: {2} -> curates: {3}")
    @CsvSource(textBlock = """
            # caller, staff, featured, curates
            1001,     false, false,    true
            1001,     false, true,     true
            1002,     false, false,    false
            1002,     false, true,     false
            1003,     true,  false,    false
            1003,     true,  true,     true
            """)
    @DisplayName("""
            Given a concept owned by 1001, featured (in the gym's pool) or the owner's own
            When it's asked who curates it
            Then its owner always does, and staff only while it's featured
            """)
    void isCuratedByTheOwnerAndByStaffWhileFeatured(Long caller, boolean staff, boolean featured, boolean curates) {
        // Given
        Concept concept = Concept.builder().tag("java-core").createdBy(1001L).featured(featured).build();

        // When / Then
        assertEquals(curates, concept.isCuratedBy(caller, staff));
    }

    @ParameterizedTest(name = "{0} (staff: {1}) on a {2} concept, featured: {3} -> allowed: {4}")
    @CsvSource(textBlock = """
            # caller, staff, status,  featured, allowed
            1001,     false, DRAFT,   false,    true
            1001,     false, ACTIVE,  false,    true
            1001,     false, RETIRED, false,    true
            1001,     false, ACTIVE,  true,     false
            1002,     false, ACTIVE,  false,    false
            1003,     true,  ACTIVE,  false,    false
            1003,     true,  ACTIVE,  true,     false
            """)
    @DisplayName("""
            Given a concept owned by 1001, in a given status, featured or not
            When a caller wants to delete it
            Then its owner may in any status once it's out of the gym's pool; nobody else may
            (staff retire a member's concept instead)
            """)
    void requireCanBeDeletedByAllowsTheOwnerOnceUnfeatured(
            Long caller, boolean staff, ConceptStatus status, boolean featured, boolean allowed) {
        // Given
        Concept concept = Concept.builder().tag("java-core").status(status).createdBy(1001L).featured(featured).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> conceptService.requireCanBeDeletedBy(concept, caller, staff));
        } else {
            assertThrows(RuntimeException.class,
                    () -> conceptService.requireCanBeDeletedBy(concept, caller, staff));
        }
    }

    @ParameterizedTest(name = "{0} (staff: {1}), featured: {2}, to {3} -> allowed: {4}")
    @CsvSource(textBlock = """
            # caller, staff, featured, target,  allowed
            1001,     false, false,    DRAFT,   true
            1001,     false, false,    RETIRED, true
            1001,     false, true,     RETIRED, false
            1001,     false, true,     ACTIVE,  true
            1003,     true,  false,    RETIRED, true
            1003,     true,  false,    ACTIVE,  true
            1003,     true,  false,    DRAFT,   false
            1003,     true,  true,     DRAFT,   false
            """)
    @DisplayName("""
            Given a published concept owned by 1001, featured or not
            When a caller who may touch its status sets a target status
            Then its owner sets any status; staff retire or restore a member's own concept and
            nothing more; and a featured concept stays published until it's unfeatured
            """)
    void requireCanChangeStatusToFollowsOwnershipAndTheGymsPool(
            Long caller, boolean staff, boolean featured, ConceptStatus target, boolean allowed) {
        // Given
        Concept concept = Concept.builder()
                .tag("java-core").status(ConceptStatus.ACTIVE).createdBy(1001L).featured(featured).build();

        // When / Then
        if (allowed) {
            assertDoesNotThrow(() -> conceptService.requireCanChangeStatusTo(concept, target, caller, staff));
        } else {
            assertThrows(RuntimeException.class,
                    () -> conceptService.requireCanChangeStatusTo(concept, target, caller, staff));
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
        assertEquals(ContributionPolicy.OWNER_APPROVAL, concept.getContributionPolicy());
    }

    @Test
    @DisplayName("""
            Given a new concept command that sets a contribution policy
            When the concept to store is built
            Then it carries that policy instead of the default
            """)
    void newConceptTakesContributionPolicyFromCommand() {
        // Given
        CreateConceptCommand command = aCreateCommand()
                .contributionPolicy(ContributionPolicy.OWNER_APPROVAL)
                .build();

        // When
        Concept concept = conceptService.newConcept(command, ConceptStatus.ACTIVE);

        // Then
        assertEquals(ContributionPolicy.OWNER_APPROVAL, concept.getContributionPolicy());
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
            Given an edit that leaves the contribution policy out
            When the edited concept is built
            Then it takes the new content and keeps the stored tag, status and policy
            """)
    void updatedConceptKeepsTagStatusAndPolicyWhenPolicyOmitted() {
        // Given
        Concept existing = aConcept("java-core").toBuilder()
                .contributionPolicy(ContributionPolicy.OWNER_APPROVAL)
                .build();
        UpdateConceptCommand command = UpdateConceptCommand.builder()
                .name("Advanced Java Core")
                .description("Revisited")
                .externalReferences(List.of())
                .build();

        // When
        Concept concept = conceptService.updatedConcept(existing, command);

        // Then
        assertEquals("java-core", concept.getTag());
        assertEquals("Advanced Java Core", concept.getName());
        assertEquals(ConceptStatus.ACTIVE, concept.getStatus());
        assertEquals(ContributionPolicy.OWNER_APPROVAL, concept.getContributionPolicy());
    }

    @Test
    @DisplayName("""
            Given an edit that sets a contribution policy
            When the edited concept is built
            Then it carries the new policy
            """)
    void updatedConceptTakesContributionPolicyFromCommandWhenGiven() {
        // Given
        Concept existing = aConcept("java-core");
        UpdateConceptCommand command = UpdateConceptCommand.builder()
                .name("Java Core")
                .description("Fundamentals")
                .externalReferences(List.of())
                .contributionPolicy(ContributionPolicy.OWNER_APPROVAL)
                .build();

        // When
        Concept concept = conceptService.updatedConcept(existing, command);

        // Then
        assertEquals(ContributionPolicy.OWNER_APPROVAL, concept.getContributionPolicy());
    }

    private static CreateConceptCommand.CreateConceptCommandBuilder aCreateCommand() {
        return CreateConceptCommand.builder()
                .tag("java-core")
                .name("Java Core")
                .description("Fundamentals of Java")
                .externalReferences(List.of("https://dev.java/learn/"));
    }

    private static Concept aConcept(String tag) {
        return Concept.builder()
                .tag(tag)
                .status(ConceptStatus.ACTIVE)
                .contributionPolicy(ContributionPolicy.AUTO_ACCEPT)
                .build();
    }
}
