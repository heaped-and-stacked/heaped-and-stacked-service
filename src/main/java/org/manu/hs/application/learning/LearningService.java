package org.manu.hs.application.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.application.learning.exception.AnswerAssessmentForbiddenException;
import org.manu.hs.application.learning.exception.AnswerNotFoundException;
import org.manu.hs.application.learning.exception.QuestionNotAvailableException;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.answer.AnswerRepository;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireNonNull;

/**
 * Answer validation and rules, building answers from commands, and persistence for learning.
 * Package-private: only LearningApplication uses it.
 * <p>
 * Never reads the current user: the caller's id is passed in.
 */
@Service
@RequiredArgsConstructor
class LearningService {

    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 10;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SubmitAnswerCommand command) {
        requireNonNull(command.getQuestionId(), "questionId");
        requireNonBlank(command.getAnswer(), "answer");
    }

    /** Both parts of a self-assessment are optional; given ones must be on the 1 to 10 scale. */
    void validate(AssessAnswerCommand command) {
        requireOnScale(command.getSelfGrade(), "selfGrade");
        requireOnScale(command.getPerceivedDifficulty(), "perceivedDifficulty");
    }

    private void requireOnScale(Integer value, String fieldName) {
        if (value != null && (value < MIN_SCALE || value > MAX_SCALE)) {
            throw new InvalidFieldValueException(
                    fieldName,
                    "must be between %d and %d".formatted(MIN_SCALE, MAX_SCALE));
        }
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data
    // ---------------------------------------------------------------------

    Optional<Question> findNextQuestion(Long userId, List<String> conceptTags, List<Long> skippedIds) {
        return questionRepository.findNextForLearning(userId, conceptTags, skippedIds);
    }

    int countUnansweredQuestions(Long userId, List<String> conceptTags) {
        return questionRepository.countUnansweredForLearning(userId, conceptTags);
    }

    Question findQuestionAvailableForLearningOrThrow(Long questionId) {
        return questionRepository.findAvailableForLearningById(questionId)
                .orElseThrow(() -> new QuestionNotAvailableException(questionId));
    }

    Answer findAnswerOrThrow(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new AnswerNotFoundException(id));
    }

    List<Answer> findAnswersBy(Long userId) {
        return answerRepository.findByUser(userId);
    }

    List<Answer> findAnswersBy(Long userId, Long questionId) {
        return answerRepository.findByUserAndQuestion(userId, questionId);
    }

    /** A self-assessment belongs to whoever gave the answer; not even admins may set it. */
    void requireOwnAnswer(Answer answer, Long userId) {
        if (!answer.getUserId().equals(userId)) {
            throw new AnswerAssessmentForbiddenException(answer.getId());
        }
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Answer create(SubmitAnswerCommand command, Question question, Long userId) {
        return answerRepository.insert(newAnswer(command, question, userId));
    }

    Answer assess(Long answerId, AssessAnswerCommand command) {
        return answerRepository.updateAssessment(answerId, command.getSelfGrade(), command.getPerceivedDifficulty());
    }

    /** Builds the answer to store, keeping the question as it is now. Pure: no repository access. */
    Answer newAnswer(SubmitAnswerCommand command, Question question, Long userId) {
        return Answer.builder()
                .userId(userId)
                .questionId(question.getId())
                .question(question)
                .answer(command.getAnswer())
                .build();
    }
}
