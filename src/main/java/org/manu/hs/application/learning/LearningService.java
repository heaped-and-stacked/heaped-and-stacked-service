package org.manu.hs.application.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.ReportQuestionCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.application.learning.exception.AnswerAssessmentForbiddenException;
import org.manu.hs.application.learning.exception.AnswerNotFoundException;
import org.manu.hs.application.learning.exception.OpenReportNotFoundException;
import org.manu.hs.application.learning.exception.QuestionNotAvailableException;
import org.manu.hs.application.learning.exception.ReportAlreadyOpenException;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.answer.AnswerRepository;
import org.manu.hs.domain.question.QuestionPool;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.question.VoteTotals;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.manu.hs.application.common.Validations.requireMaxLength;
import static org.manu.hs.application.common.Validations.requireNoInvisibleCharacters;
import static org.manu.hs.application.common.Validations.requireNoNulCharacter;
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
    private static final int MAX_ANSWER_LENGTH = 10_000;
    private static final int MAX_REPORT_NOTE_LENGTH = 2000;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SubmitAnswerCommand command) {
        requireNonNull(command.getQuestionId(), "questionId");
        requireNonBlank(command.getAnswer(), "answer");
        requireMaxLength(command.getAnswer(), MAX_ANSWER_LENGTH, "answer");
        requireNoNulCharacter(command.getAnswer(), "answer");
    }

    /** Both parts of a self-assessment are optional; given ones must be on the 1 to 10 scale. */
    void validate(AssessAnswerCommand command) {
        requireOnScale(command.getSelfGrade(), "selfGrade");
        requireOnScale(command.getPerceivedDifficulty(), "perceivedDifficulty");
    }

    /** A report needs a reason; the note is optional. */
    void validate(ReportQuestionCommand command) {
        requireNonNull(command.getReason(), "reason");
        requireMaxLength(command.getNote(), MAX_REPORT_NOTE_LENGTH, "note");
        requireNoInvisibleCharacters(command.getNote(), "note");
    }

    void validate(Vote vote) {
        requireNonNull(vote, "vote");
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

    Optional<Question> findNextQuestion(
            Long userId, boolean staff, List<String> conceptTags, List<Long> skippedIds, QuestionPool pool) {
        return questionRepository.findNextForLearning(userId, staff, conceptTags, skippedIds, pool);
    }

    int countUnansweredQuestions(Long userId, boolean staff, List<String> conceptTags, QuestionPool pool) {
        return questionRepository.countUnansweredForLearning(userId, staff, conceptTags, pool);
    }

    boolean canVerify(Long questionId, Long userId, boolean staff) {
        return userId != null && questionRepository.canVerify(questionId, userId, staff);
    }

    VoteTotals countVotes(Long questionId) {
        return questionRepository.countVotes(List.of(questionId)).getOrDefault(questionId, VoteTotals.NONE);
    }

    /** The learner's own vote on the question; none for a visitor (no user id). */
    Optional<Vote> findVote(Long questionId, Long userId) {
        return userId == null ? Optional.empty() : questionRepository.findVote(questionId, userId);
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

    Optional<Report> findOpenReport(Long questionId, Long userId) {
        return questionRepository.findOpenReport(questionId, userId);
    }

    /** The learner's open report on the question: only open ones can still be changed or withdrawn. */
    Report findOpenReportOrThrow(Long questionId, Long userId) {
        return findOpenReport(questionId, userId)
                .orElseThrow(() -> new OpenReportNotFoundException(questionId));
    }

    /** One open report per learner and question, until someone resolves or dismisses it. */
    void requireNoOpenReport(Long questionId, Long userId) {
        if (questionRepository.hasOpenReport(questionId, userId)) {
            throw new ReportAlreadyOpenException(questionId);
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

    Report report(ReportQuestionCommand command, Question question, Long userId) {
        return questionRepository.insertReport(Report.builder()
                .question(question)
                .reportedBy(userId)
                .reason(command.getReason())
                .note(command.getNote())
                .build());
    }

    /** Changes an open report; if it was decided in the meantime, there's no open report left to change. */
    Report change(Report report, ReportQuestionCommand command) {
        return questionRepository.updateOpenReportContent(report.getId(), command.getReason(), command.getNote())
                .orElseThrow(() -> new OpenReportNotFoundException(report.getQuestion().getId()));
    }

    /** Withdraws an open report; a decided one stays, as part of the question's history. */
    void withdraw(Report report) {
        if (!questionRepository.deleteOpenReport(report.getId())) {
            throw new OpenReportNotFoundException(report.getQuestion().getId());
        }
    }

    void vote(Long questionId, Long userId, Vote vote) {
        questionRepository.saveVote(questionId, userId, vote);
    }

    void clearVote(Long questionId, Long userId) {
        questionRepository.deleteVote(questionId, userId);
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
