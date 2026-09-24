package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.common.exception.RequiredFieldMissingException;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.application.content.exception.QuestionHasDraftConceptsException;
import org.manu.hs.application.content.exception.QuestionModificationForbiddenException;
import org.manu.hs.application.content.exception.QuestionNotFoundException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
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

    private final QuestionRepository questionRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SubmitQuestionCommand command) {
        validateFields(
                command.getRequirement(),
                command.getSampleSolution(),
                command.getType(),
                command.getDifficultyScore(),
                command.getExpectedDurationMinutes(),
                command.getConceptTags());
    }

    void validate(UpdateQuestionCommand command) {
        validateFields(
                command.getRequirement(),
                command.getSampleSolution(),
                command.getType(),
                command.getDifficultyScore(),
                command.getExpectedDurationMinutes(),
                command.getConceptTags());
    }

    private void validateFields(
            String requirement,
            String sampleSolution,
            QuestionType type,
            Integer difficultyScore,
            Integer expectedDurationMinutes,
            List<String> conceptTags) {
        requireNonBlank(requirement, "requirement");
        requireNonBlank(sampleSolution, "sampleSolution");
        requireNonNull(type, "type");
        requireNonNull(difficultyScore, "difficultyScore");
        if (difficultyScore < MIN_DIFFICULTY_SCORE || difficultyScore > MAX_DIFFICULTY_SCORE) {
            throw new InvalidFieldValueException(
                    "difficultyScore",
                    "must be between %d and %d".formatted(MIN_DIFFICULTY_SCORE, MAX_DIFFICULTY_SCORE));
        }
        requireNonNull(expectedDurationMinutes, "expectedDurationMinutes");
        if (expectedDurationMinutes <= 0) {
            throw new InvalidFieldValueException("expectedDurationMinutes", "must be greater than 0");
        }
        if (conceptTags == null || conceptTags.isEmpty()) {
            throw new RequiredFieldMissingException("conceptTags");
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

    List<Question> findByStatus(QuestionStatus status) {
        return questionRepository.findByStatus(status);
    }

    /**
     * Admins may modify any question in any status; authors only their own DRAFT questions.
     */
    void requireCanBeModifiedBy(Question question, Long userId, boolean admin) {
        final boolean authorOfDraft = question.getCreatedBy().equals(userId)
                && question.getStatus() == QuestionStatus.DRAFT;

        if (!admin && !authorOfDraft) {
            throw new QuestionModificationForbiddenException(question.getId());
        }
    }

    /**
     * An ACTIVE question may not link a concept that is still a DRAFT.
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

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Question create(SubmitQuestionCommand command, QuestionStatus status, Long userId) {
        return questionRepository.insert(newQuestion(command, status, userId));
    }

    Question update(Question existing, UpdateQuestionCommand command, Long userId) {
        return questionRepository.update(existing.getId(), updatedQuestion(existing, command, userId));
    }

    Question updateStatus(Long id, QuestionStatus status, Long updatedBy) {
        return questionRepository.updateStatus(id, status, updatedBy);
    }

    void delete(Long id) {
        questionRepository.deleteById(id);
    }

    /** Builds the question to store from the command. Pure: no repository access. */
    Question newQuestion(SubmitQuestionCommand command, QuestionStatus status, Long userId) {
        return Question.builder()
                .conceptTags(withoutDuplicates(command.getConceptTags()))
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .type(command.getType())
                .difficultyScore(command.getDifficultyScore())
                .expectedDurationMinutes(command.getExpectedDurationMinutes())
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
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .type(command.getType())
                .difficultyScore(command.getDifficultyScore())
                .expectedDurationMinutes(command.getExpectedDurationMinutes())
                .source(command.getSource())
                .updatedBy(userId)
                .build();
    }

    /** Duplicate tags are dropped silently, keeping the first occurrence and the order. */
    private List<String> withoutDuplicates(List<String> conceptTags) {
        return conceptTags.stream().distinct().toList();
    }
}
