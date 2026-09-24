package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.exception.ConceptAlreadyExistsException;
import org.manu.hs.application.content.exception.ConceptInUseException;
import org.manu.hs.application.content.exception.ConceptNotFoundException;
import org.manu.hs.application.content.exception.InvalidConceptTagException;
import org.manu.hs.application.content.exception.UnknownConceptTagException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.manu.hs.application.common.Validations.requireNonBlank;

/**
 * Concept rules that need stored data, building concepts from commands, and concept
 * persistence. Package-private: only ContentApplication uses it.
 * <p>
 * Never reads the current user: the caller's id and decisions based on it (such as
 * the starting status) are passed in, which keeps every method easy to unit test.
 */
@Service
@RequiredArgsConstructor
class ConceptService {

    private static final Pattern KEBAB_CASE_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    private final ConceptRepository conceptRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(CreateConceptCommand command) {
        validateTag(command.getTag());
        validateFields(command.getName(), command.getDescription());
    }

    void validate(UpdateConceptCommand command) {
        validateFields(command.getName(), command.getDescription());
    }

    private void validateTag(String tag) {
        if (tag == null || !KEBAB_CASE_PATTERN.matcher(tag).matches()) {
            throw new InvalidConceptTagException(tag);
        }
    }

    private void validateFields(String name, String description) {
        requireNonBlank(name, "name");
        requireNonBlank(description, "description");
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data
    // ---------------------------------------------------------------------

    List<Concept> findAll() {
        return conceptRepository.findAll();
    }

    /** The concepts for the given tags, as stored; tags without a concept are left out. */
    List<Concept> findByTags(List<String> tags) {
        return conceptRepository.findByTags(tags);
    }

    /** The concepts for the given tags; fails if any tag has no concept. */
    List<Concept> findByTagsOrThrow(List<String> tags) {
        final List<Concept> concepts = conceptRepository.findByTags(tags);
        final Set<String> foundTags = concepts.stream().map(Concept::getTag).collect(Collectors.toSet());

        tags.stream()
                .filter(tag -> !foundTags.contains(tag))
                .findFirst()
                .ifPresent(tag -> {
                    throw new UnknownConceptTagException(tag);
                });

        return concepts;
    }

    void requireExists(String tag) {
        if (!conceptRepository.existsByTag(tag)) {
            throw new ConceptNotFoundException(tag);
        }
    }

    void requireTagAvailable(String tag) {
        if (conceptRepository.existsByTag(tag)) {
            throw new ConceptAlreadyExistsException(tag);
        }
    }

    void requireNotInUse(String tag) {
        if (conceptRepository.isReferencedByAnyQuestion(tag)) {
            throw new ConceptInUseException(tag);
        }
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Concept create(CreateConceptCommand command, ConceptStatus status, String userId) {
        return conceptRepository.save(newConcept(command, status), userId);
    }

    Concept update(String tag, UpdateConceptCommand command, String userId) {
        return conceptRepository.update(updatedConcept(tag, command), userId);
    }

    /** Builds the concept to store from the command. Pure: no repository access. */
    Concept newConcept(CreateConceptCommand command, ConceptStatus status) {
        return Concept.builder()
                .tag(command.getTag())
                .name(command.getName())
                .description(command.getDescription())
                .externalReferences(List.copyOf(command.getExternalReferences()))
                .status(status)
                .build();
    }

    /** Builds the edited concept from the command. Pure: no repository access. */
    Concept updatedConcept(String tag, UpdateConceptCommand command) {
        return Concept.builder()
                .tag(tag)
                .name(command.getName())
                .description(command.getDescription())
                .externalReferences(List.copyOf(command.getExternalReferences()))
                .build();
    }

    void delete(String tag) {
        conceptRepository.deleteByTag(tag);
    }
}
