package org.manu.hs.application.content;

import org.manu.hs.application.common.Validations;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.exception.ConceptAlreadyExistsException;
import org.manu.hs.application.content.exception.ConceptFeaturedException;
import org.manu.hs.application.content.exception.ConceptHasSubmittedQuestionsException;
import org.manu.hs.application.content.exception.ConceptInUseException;
import org.manu.hs.application.content.exception.ConceptModificationForbiddenException;
import org.manu.hs.application.content.exception.ContributionsClosedException;
import org.manu.hs.application.content.exception.ConceptNotFoundException;
import org.manu.hs.application.content.exception.ConceptNotPublishedException;
import org.manu.hs.application.content.exception.ConceptOwnedByHouseException;
import org.manu.hs.application.content.exception.InvalidConceptTagException;
import org.manu.hs.application.content.exception.UnknownConceptTagException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ConceptWorkload;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.manu.hs.application.common.Validations.requireNoInvisibleCharacters;
import static org.manu.hs.application.common.Validations.requireMaxLength;
import static org.manu.hs.application.common.Validations.requireMaxSize;
import static org.manu.hs.application.common.Validations.requireNoEmptyEntries;
import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireSingleLine;

/**
 * Concept validation and rules, building concepts from commands, and concept persistence.
 * Package-private: only ContentApplication uses it.
 * <p>
 * Never reads the current user: the caller's id and decisions based on it (such as
 * the starting status) are passed in, which keeps every method easy to unit test.
 */
@Service
class ConceptService {

    private static final Pattern KEBAB_CASE_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
    private static final int MAX_TAG_LENGTH = 100;
    private static final int MAX_NAME_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 2_000;
    private static final int MAX_EXTERNAL_REFERENCES = 20;
    private static final int MAX_EXTERNAL_REFERENCE_LENGTH = 500;

    private final ConceptRepository conceptRepository;
    private final boolean membersAddQuestions;
    private final boolean membersAddConcepts;

    /**
     * Written by hand (not @RequiredArgsConstructor), so the settings it reads are visible here.
     *
     * @param membersAddQuestions whether members may add questions; when false, only staff do
     * @param membersAddConcepts   whether members may add concepts; when false, only staff do
     */
    ConceptService(
            ConceptRepository conceptRepository,
            @Value("${app.content.members-add-questions}") boolean membersAddQuestions,
            @Value("${app.content.members-add-concepts}") boolean membersAddConcepts) {
        this.conceptRepository = conceptRepository;
        this.membersAddQuestions = membersAddQuestions;
        this.membersAddConcepts = membersAddConcepts;
    }

    // ---------------------------------------------------------------------
    // Who may contribute
    // ---------------------------------------------------------------------

    /** Whether the caller may add questions: staff always, members when it's open to them. */
    boolean canAddQuestions(boolean staff) {
        return staff || membersAddQuestions;
    }

    /** Whether the caller may add concepts: staff always, members when it's open to them. */
    boolean canAddConcepts(boolean staff) {
        return staff || membersAddConcepts;
    }

    void requireCanAddQuestions(boolean staff) {
        if (!canAddQuestions(staff)) {
            throw new ContributionsClosedException("questions");
        }
    }

    void requireCanAddConcepts(boolean staff) {
        if (!canAddConcepts(staff)) {
            throw new ContributionsClosedException("concepts");
        }
    }

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(CreateConceptCommand command) {
        validateTag(command.getTag());
        validateFields(command.getName(), command.getDescription(), command.getExternalReferences());
    }

    void validate(UpdateConceptCommand command) {
        validateFields(command.getName(), command.getDescription(), command.getExternalReferences());
    }

    private void validateTag(String tag) {
        if (tag == null || tag.length() > MAX_TAG_LENGTH || !KEBAB_CASE_PATTERN.matcher(tag).matches()) {
            throw new InvalidConceptTagException(tag);
        }
    }

    private void validateFields(String name, String description, List<String> externalReferences) {
        requireNonBlank(name, "name");
        requireMaxLength(name, MAX_NAME_LENGTH, "name");
        requireSingleLine(name, "name");
        requireNonBlank(description, "description");
        requireMaxLength(description, MAX_DESCRIPTION_LENGTH, "description");
        requireNoInvisibleCharacters(description, "description");
        requireMaxSize(externalReferences, MAX_EXTERNAL_REFERENCES, "externalReferences");
        requireNoEmptyEntries(externalReferences, "externalReferences");
        if (externalReferences != null && externalReferences.stream()
                .anyMatch(reference -> reference.length() > MAX_EXTERNAL_REFERENCE_LENGTH)) {
            throw new InvalidFieldValueException(
                    "externalReferences",
                    "must each be at most %d characters".formatted(MAX_EXTERNAL_REFERENCE_LENGTH));
        }
        if (externalReferences != null && !externalReferences.stream().allMatch(Validations::isSingleLine)) {
            throw new InvalidFieldValueException(
                    "externalReferences",
                    "must each be one line without invisible characters");
        }
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data
    // ---------------------------------------------------------------------

    /** Every concept, except drafts the caller doesn't curate: a draft is private to its owner (and staff). */
    List<Concept> findVisibleTo(Long userId, boolean staff) {
        return conceptRepository.findAll().stream()
                .filter(concept -> concept.getStatus() != ConceptStatus.DRAFT || concept.isCuratedBy(userId, staff))
                .toList();
    }

    /** What waits on each concept the user curates (theirs, or the featured ones for staff). */
    List<ConceptWorkload> findWorkload(Long userId, boolean staff) {
        return conceptRepository.findWorkload(userId, staff);
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
     * Whoever curates the concept (its owner, or staff) may change it in any status.
     */
    void requireCanBeModifiedBy(Concept concept, Long userId, boolean staff) {
        if (!concept.isCuratedBy(userId, staff)) {
            throw new ConceptModificationForbiddenException(concept.getTag());
        }
    }

    /**
     * Whoever curates the concept may delete it, in any status, once it's no longer featured.
     * Staff don't delete a member's own concept: they retire it if it must come down.
     */
    void requireCanBeDeletedBy(Concept concept, Long userId, boolean staff) {
        requireCanBeModifiedBy(concept, userId, staff);
        requireNotFeatured(concept);
    }

    /**
     * Whoever may touch the concept's status at all: its curators, and staff, who may take any
     * concept down (moderation).
     */
    void requireCanChangeStatusOf(Concept concept, Long userId, boolean staff) {
        if (!staff) {
            requireCanBeModifiedBy(concept, userId, false);
        }
    }

    /**
     * Its curators set any status; staff retire a member's own concept, or bring it back, and
     * nothing more. A featured concept stays published until it's unfeatured.
     */
    void requireCanChangeStatusTo(Concept concept, ConceptStatus status, Long userId, boolean staff) {
        if (!concept.isCuratedBy(userId, staff) && status == ConceptStatus.DRAFT) {
            throw new ConceptModificationForbiddenException(concept.getTag());
        }
        if (status != ConceptStatus.ACTIVE) {
            requireNotFeatured(concept);
        }
    }

    private void requireNotFeatured(Concept concept) {
        if (concept.isFeatured()) {
            throw new ConceptFeaturedException(concept.getTag());
        }
    }

    void requireNotHouseOwned(Concept concept) {
        if (concept.isHouseOwned()) {
            throw new ConceptOwnedByHouseException(concept.getTag());
        }
    }

    void requirePublished(Concept concept) {
        if (concept.getStatus() != ConceptStatus.ACTIVE) {
            throw new ConceptNotPublishedException(concept.getTag());
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

    Concept updateFeatured(String tag, boolean featured, Long updatedBy) {
        return conceptRepository.updateFeatured(tag, featured, updatedBy);
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
