package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.AdminRequiredException;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.exception.ConceptAlreadyExistsException;
import org.manu.hs.application.content.exception.ConceptHasSubmittedQuestionsException;
import org.manu.hs.application.content.exception.ConceptInUseException;
import org.manu.hs.application.content.exception.ConceptModificationForbiddenException;
import org.manu.hs.application.content.exception.ConceptNotFoundException;
import org.manu.hs.application.content.exception.InvalidConceptTagException;
import org.manu.hs.application.content.exception.UnknownConceptTagException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.manu.hs.application.common.Validations.requireNonBlank;

/**
 * Concept validation and rules, building concepts from commands, and concept persistence.
 * Package-private: only ContentApplication uses it.
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

    Concept findByTagOrThrow(String tag) {
        return conceptRepository.findByTag(tag)
                .orElseThrow(() -> new ConceptNotFoundException(tag));
    }

    /**
     * A concept belongs to its author: the owner may change it in any status, as may any admin.
     */
    void requireCanBeModifiedBy(Concept concept, Long userId, boolean admin) {
        if (!admin && !isOwner(concept, userId)) {
            throw new ConceptModificationForbiddenException(concept.getTag());
        }
    }

    /**
     * Owners may delete their concept only while it is a DRAFT; once published, only admins may.
     */
    void requireCanBeDeletedBy(Concept concept, Long userId, boolean admin) {
        requireCanBeModifiedBy(concept, userId, admin);

        if (!admin && concept.getStatus() != ConceptStatus.DRAFT) {
            throw new AdminRequiredException("delete a published concept");
        }
    }

    /**
     * Owners may publish their concept (ACTIVE); unpublishing and retiring are for admins.
     * Whether the caller is the owner is checked separately, by requireCanBeModifiedBy.
     */
    void requireCanChangeStatusTo(ConceptStatus status, boolean admin) {
        if (!admin && status != ConceptStatus.ACTIVE) {
            throw new AdminRequiredException("unpublish or retire a concept");
        }
    }

    private static boolean isOwner(Concept concept, Long userId) {
        return concept.getCreatedBy().equals(userId);
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

    /**
     * A concept used by a SUBMITTED question may not go back to DRAFT; retiring it is allowed.
     */
    void requireCanMoveTo(String tag, ConceptStatus status) {
        if (status == ConceptStatus.DRAFT && conceptRepository.isReferencedBySubmittedQuestion(tag)) {
            throw new ConceptHasSubmittedQuestionsException(tag);
        }
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Concept create(CreateConceptCommand command, ConceptStatus status, Long userId) {
        return conceptRepository.save(newConcept(command, status), userId);
    }

    Concept update(Concept existing, UpdateConceptCommand command, Long userId) {
        return conceptRepository.update(updatedConcept(existing, command), userId);
    }

    Concept updateStatus(String tag, ConceptStatus status, Long updatedBy) {
        return conceptRepository.updateStatus(tag, status, updatedBy);
    }

    void delete(String tag) {
        conceptRepository.deleteByTag(tag);
    }

    /** Builds the concept to store from the command. Pure: no repository access. */
    Concept newConcept(CreateConceptCommand command, ConceptStatus status) {
        return Concept.builder()
                .tag(command.getTag())
                .name(command.getName())
                .description(command.getDescription())
                .externalReferences(List.copyOf(command.getExternalReferences()))
                .status(status)
                .contributionPolicy(command.getContributionPolicy() != null
                        ? command.getContributionPolicy()
                        : ContributionPolicy.AUTO_ACCEPT)
                .build();
    }

    /**
     * Builds the edited concept: content comes from the command; tag, status, owner and, unless
     * the command sets one, the contribution policy are kept. Pure: no repository access.
     */
    Concept updatedConcept(Concept existing, UpdateConceptCommand command) {
        return existing.toBuilder()
                .name(command.getName())
                .description(command.getDescription())
                .externalReferences(List.copyOf(command.getExternalReferences()))
                .contributionPolicy(command.getContributionPolicy() != null
                        ? command.getContributionPolicy()
                        : existing.getContributionPolicy())
                .build();
    }
}
