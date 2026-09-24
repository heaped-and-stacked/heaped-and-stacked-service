package org.manu.hs.application.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.question.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Entry point of the learning feature: the only class the API layer uses.
 * <p>
 * Learners get questions to answer, answer them, and may assess their own answers. Checks
 * run 404, then 403, then 400. It is the only place that reads the current user.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LearningApplication {

    private final LearningService learningService;
    private final CurrentUserProvider userProvider;

    /**
     * The next question for this learner: never answered ones first, then answered ones at random,
     * the longer ago the likelier. The skipped ones (already seen in this workout) are left out; with concept tags, only
     * questions accepted for at least one of them are considered. Empty when none is left.
     */
    public Optional<Question> getNextQuestion(List<String> conceptTags, List<Long> skippedIds) {
        return learningService.findNextQuestion(userProvider.userId(), conceptTags, skippedIds);
    }

    /**
     * How many questions (for these concept tags; none = all) this learner has never answered.
     * 0 means every one of them was answered at least once: only repeats are left.
     */
    public int countUnansweredQuestions(List<String> conceptTags) {
        return learningService.countUnansweredQuestions(userProvider.userId(), conceptTags);
    }

    /** Stores the answer together with the question as it is now, which later edits won't change. */
    public Answer submitAnswer(SubmitAnswerCommand command) {
        learningService.validate(command);
        final Question question = learningService.findQuestionAvailableForLearningOrThrow(command.getQuestionId());

        return learningService.create(command, question, userProvider.userId());
    }

    /** The learner's optional self-assessment of their own answer. */
    public Answer assessAnswer(Long answerId, AssessAnswerCommand command) {
        final Answer answer = learningService.findAnswerOrThrow(answerId);
        learningService.requireOwnAnswer(answer, userProvider.userId());
        learningService.validate(command);

        return learningService.assess(answerId, command);
    }

    /** The learner's own answers, newest first. */
    public List<Answer> getMyAnswers() {
        return learningService.findAnswersBy(userProvider.userId());
    }

    /** The learner's own answers to one question, newest first: their past attempts at it. */
    public List<Answer> getMyAnswers(Long questionId) {
        return learningService.findAnswersBy(userProvider.userId(), questionId);
    }
}
