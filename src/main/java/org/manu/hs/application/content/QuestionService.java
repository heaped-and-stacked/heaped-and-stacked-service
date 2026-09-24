package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.AdminRequiredException;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.common.exception.RequiredFieldMissingException;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.application.content.exception.ConceptLinkNotFoundException;
import org.manu.hs.application.content.exception.QuestionAnsweredException;
import org.manu.hs.application.content.exception.QuestionHasDraftConceptsException;
import org.manu.hs.application.content.exception.QuestionModificationForbiddenException;
import org.manu.hs.application.content.exception.QuestionNotFoundException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireNonNull;

/**
 * Question validation and rules, building questions from commands, and question
 * persistence. Package-private: only ContentApplication uses it.
 * <p>
 * Never reads the current user: the caller's id and decisions based on it (such as
 * the starting status) are passed in, which keeps every method easy to unit test.
 */
@Service
@RequiredArgsConstructor
class QuestionService {

    private static final int MIN_DIFFICULTY_SCORE = 1;
    private static final int MAX_DIFFICULTY_SCORE = 10;
    private static final int MAX_TITLE_LENGTH = 120;

    private final QuestionRepository questionRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SubmitQuestionCommand command) {
        validateFields(
                command.getTitle(),
                command.getRequirement(),
                command.getSampleSolution(),
                command.getDifficultyScore(),
                command.getConceptTags());
    }

    void validate(UpdateQuestionCommand command) {
        validateFields(
                command.getTitle(),
                command.getRequirement(),
                command.getSampleSolution(),
                command.getDifficultyScore(),
                command.getConceptTags());
    }

    private void validateFields(
            String title,
            String requirement,
            String sampleSolution,
            Integer difficultyScore,
            List<String> conceptTags) {
        requireNonBlank(title, "title");
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new InvalidFieldValueException(
                    "title",
                    "must be at most %d characters".formatted(MAX_TITLE_LENGTH));
        }
        requireNonBlank(requirement, "requirement");
        requireNonBlank(sampleSolution, "sampleSolution");
        requireNonNull(difficultyScore, "difficultyScore");
        if (difficultyScore < MIN_DIFFICULTY_SCORE || difficultyScore > MAX_DIFFICULTY_SCORE) {
            throw new InvalidFieldValueException(
                    "difficultyScore",
                    "must be between %d and %d".formatted(MIN_DIFFICULTY_SCORE, MAX_DIFFICULTY_SCORE));
        }
        if (conceptTags == null || conceptTags.isEmpty()) {
            throw new RequiredFieldMissingException("conceptTags");
        }
    }

    /** A review either accepts or declines a question for a concept. */
    void validateReviewDecision(ConceptLinkStatus decision) {
        requireNonNull(decision, "status");
        if (decision == ConceptLinkStatus.PENDING) {
            throw new InvalidFieldValueException("status", "must be ACCEPTED or DECLINED");
        }
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data
    // ---------------------------------------------------------------------

    Question findByIdOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    List<Question> findBySubmittedBy(Long userId) {
        return questionRepository.findBySubmittedBy(userId);
    }

    /** What waits for a decision: on the caller's own concepts, or on every concept for admins. */
    List<ConceptLink> findLinksAwaitingReview(Long userId, boolean admin) {
        return admin
                ? questionRepository.findLinksAwaitingReview()
                : questionRepository.findLinksAwaitingReviewByConceptOwner(userId);
    }

    void requireLinkedTo(Question question, String conceptTag) {
        if (!question.getConceptTags().contains(conceptTag)) {
            throw new ConceptLinkNotFoundException(question.getId(), conceptTag);
        }
    }

    /**
     * The author may edit their question in any status (typos, better phrasing), as may any admin.
     */
    void requireCanBeModifiedBy(Question question, Long userId, boolean admin) {
        if (!admin && !question.getCreatedBy().equals(userId)) {
            throw new QuestionModificationForbiddenException(question.getId());
        }
    }

    /**
     * Authors may delete their question only while it is a DRAFT; once submitted, only admins may.
     */
    void requireCanBeDeletedBy(Question question, Long userId, boolean admin) {
        requireCanBeModifiedBy(question, userId, admin);

        if (!admin && question.getStatus() != QuestionStatus.DRAFT) {
            throw new AdminRequiredException("delete a submitted question");
        }
    }

    /** Answered questions are retired, not deleted, so learners keep their history. */
    void requireNotAnswered(Long id) {
        if (questionRepository.isReferencedByAnyAnswer(id)) {
            throw new QuestionAnsweredException(id);
        }
    }

    /**
     * A SUBMITTED question may not link a concept that is still a DRAFT.
     */
    void requireNoDraftConcepts(List<Concept> concepts) {
        final List<String> draftTags = concepts.stream()
                .filter(concept -> concept.getStatus() == ConceptStatus.DRAFT)
                .map(Concept::getTag)
                .toList();

        if (!draftTags.isEmpty()) {
            throw new QuestionHasDraftConceptsException(draftTags);
        }
    }

    /**
     * The concepts a question by this author has to wait on: those whose owner approves
     * contributions, unless the author is that owner. Pure: no repository access.
     */
    List<String> tagsAwaitingApproval(List<Concept> concepts, Long authorId) {
        return concepts.stream()
                .filter(concept -> concept.getContributionPolicy() == ContributionPolicy.OWNER_APPROVAL)
                .filter(concept -> !concept.getCreatedBy().equals(authorId))
                .map(Concept::getTag)
                .toList();
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Question create(SubmitQuestionCommand command, QuestionStatus status, Long userId, List<String> tagsAwaitingApproval) {
        return questionRepository.insert(newQuestion(command, status, userId), tagsAwaitingApproval);
    }

    Question update(Question existing, UpdateQuestionCommand command, Long userId, List<String> tagsAwaitingApproval) {
        return questionRepository.update(
                existing.getId(),
                updatedQuestion(existing, command, userId),
                tagsAwaitingApproval);
    }

    Question updateStatus(Long id, QuestionStatus status, Long updatedBy) {
        return questionRepository.updateStatus(id, status, updatedBy);
    }

    /** Records the concept owner's (or an admin's) decision on a question for that concept. */
    void decide(Long questionId, String conceptTag, ConceptLinkStatus decision, Long decidedBy) {
        questionRepository.updateLinkStatus(questionId, conceptTag, decision, decidedBy);
    }

    void delete(Long id) {
        questionRepository.deleteById(id);
    }

    /** Builds the question to store from the command. Pure: no repository access. */
    Question newQuestion(SubmitQuestionCommand command, QuestionStatus status, Long userId) {
        return Question.builder()
                .conceptTags(withoutDuplicates(command.getConceptTags()))
                .title(command.getTitle())
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .difficultyScore(command.getDifficultyScore())
                .status(status)
                .source(command.getSource())
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    /**
     * Builds the edited question: content comes from the command; id, status and author
     * are kept from the stored question. Pure: no repository access.
     */
    Question updatedQuestion(Question existing, UpdateQuestionCommand command, Long userId) {
        return existing.toBuilder()
                .conceptTags(withoutDuplicates(command.getConceptTags()))
                .title(command.getTitle())
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .difficultyScore(command.getDifficultyScore())
                .source(command.getSource())
                .updatedBy(userId)
                .build();
    }

    /** Duplicate tags are dropped silently, keeping the first occurrence and the order. */
    private List<String> withoutDuplicates(List<String> conceptTags) {
        return conceptTags.stream().distinct().toList();
    }
}
