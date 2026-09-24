package org.manu.hs.application.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.ReportQuestionCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.question.QuestionPool;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.question.VoteTotals;
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
     * the longer ago the likelier. The skipped ones (already seen in this session) are left out; with concept tags, only
     * questions accepted for at least one of them are considered; the pool picks all, verified, or
     * those the learner may verify. Empty when none is left.
     */
    public Optional<Question> getNextQuestion(List<String> conceptTags, List<Long> skippedIds, QuestionPool pool) {
        return learningService.findNextQuestion(
                userProvider.userId(), userProvider.isStaff(), conceptTags, skippedIds, pool);
    }

    /** One chosen question to train on (e.g. retried from past answers), if learners can answer it. */
    public Question getQuestion(Long questionId) {
        return learningService.findQuestionAvailableForLearningOrThrow(questionId);
    }

    /**
     * How many questions (for these concept tags; none = all) this learner has never answered.
     * 0 means every one of them was answered at least once: only repeats are left. A visitor (not
     * signed in) has no answers, so for them every question counts as new. From the pool to
     * verify: how many are left to verify, answered or not.
     */
    public int countUnansweredQuestions(List<String> conceptTags, QuestionPool pool) {
        return learningService.countUnansweredQuestions(
                userProvider.userId(), userProvider.isStaff(), conceptTags, pool);
    }

    /** Whether the caller may verify the question: staff on a featured concept, otherwise its concepts' owners. */
    public boolean canVerify(Long questionId) {
        return learningService.canVerify(questionId, userProvider.userId(), userProvider.isStaff());
    }

    /** How many learners voted the question up and down: shown with it. */
    public VoteTotals getVoteTotals(Long questionId) {
        return learningService.countVotes(questionId);
    }

    /** The current learner's own vote on the question, so the learner sees which one is theirs. */
    public Optional<Vote> getMyVote(Long questionId) {
        return learningService.findVote(questionId, userProvider.userId());
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

    /**
     * Reports a question learners can answer as wrong or unsuitable. One open report per learner;
     * enough open reports hold the question back from learners until they are decided.
     */
    public void reportQuestion(Long questionId, ReportQuestionCommand command) {
        final Question question = learningService.findQuestionAvailableForLearningOrThrow(questionId);
        learningService.validate(command);
        learningService.requireNoOpenReport(questionId, userProvider.userId());

        learningService.report(command, question, userProvider.userId());
    }

    /**
     * The learner's own open report (feedback) on a question, so they can see, change or withdraw
     * it. Empty when they have none open. Only the learner's own: whoever decides reports uses
     * the content feature. Works for held-back questions too: the learner's report may be one of
     * those holding it back.
     */
    public Optional<Report> getMyOpenReport(Long questionId) {
        return learningService.findOpenReport(questionId, userProvider.userId());
    }

    /** Changes the reason and note of the learner's own open report on a question. */
    public Report updateMyOpenReport(Long questionId, ReportQuestionCommand command) {
        final Report report = learningService.findOpenReportOrThrow(questionId, userProvider.userId());
        learningService.validate(command);

        return learningService.change(report, command);
    }

    /** Withdraws the learner's own open report on a question; they may then send a new one. */
    public void withdrawMyOpenReport(Long questionId) {
        learningService.withdraw(learningService.findOpenReportOrThrow(questionId, userProvider.userId()));
    }

    /** The learner's vote on a question they can answer; voting again replaces it. */
    public void voteOnQuestion(Long questionId, Vote vote) {
        learningService.findQuestionAvailableForLearningOrThrow(questionId);
        learningService.validate(vote);

        learningService.vote(questionId, userProvider.userId(), vote);
    }

    /** Takes the learner's vote back; nothing happens if they had none. */
    public void clearVote(Long questionId) {
        learningService.clearVote(questionId, userProvider.userId());
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
