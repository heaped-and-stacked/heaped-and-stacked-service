package org.manu.hs.api.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.api.learning.dto.AnswerResponse;
import org.manu.hs.api.learning.dto.AssessAnswerRequest;
import org.manu.hs.api.learning.dto.LearningQuestionResponse;
import org.manu.hs.api.learning.dto.MyReportResponse;
import org.manu.hs.api.learning.dto.ReportQuestionRequest;
import org.manu.hs.api.learning.dto.SubmitAnswerRequest;
import org.manu.hs.api.learning.dto.VoteRequest;
import org.manu.hs.application.learning.LearningApplication;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.ReportQuestionCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.domain.answer.Answer;
import org.manu.hs.domain.question.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Learning: questions to answer, answers, and the learner's self-assessment of them.
 * Every endpoint requires signing in, except getting the next question: visitors may try a
 * workout (SecurityConfig).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/learning")
public class LearningController {

    private final LearningApplication learningApplication;

    /**
     * One question to train on next. concepts: the picked concept tags (none = all); skip: ids
     * already seen in this workout. 204 when there is nothing left. Says how many questions for
     * these concepts the learner has never answered, so the UI can tell when a muscle is done.
     */
    @GetMapping("/questions/next")
    public ResponseEntity<LearningQuestionResponse> getNextQuestion(
            @RequestParam(required = false) List<String> concepts,
            @RequestParam(required = false) List<Long> skip) {
        List<String> conceptTags = concepts == null ? List.of() : concepts;
        // An empty entry (?skip=) binds as null, and one null in NOT IN (...) would match nothing.
        List<Long> skipped = skip == null ? List.of() : skip.stream().filter(Objects::nonNull).toList();
        return learningApplication.getNextQuestion(conceptTags, skipped)
                .map(question -> ResponseEntity.ok(LearningQuestionResponse.toDto(
                        question,
                        learningApplication.countUnansweredQuestions(conceptTags),
                        learningApplication.getVoteTotals(question.getId()),
                        learningApplication.getMyVote(question.getId()).orElse(null))))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * One chosen question to train on, e.g. retried from past workouts; 404 if learners can't
     * answer it. The count of new questions is for concepts (the workout's), or else its own.
     */
    @GetMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LearningQuestionResponse getQuestion(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> concepts) {
        final Question question = learningApplication.getQuestion(id);
        final List<String> conceptTags = concepts == null ? question.getConceptTags() : concepts;
        return LearningQuestionResponse.toDto(
                question,
                learningApplication.countUnansweredQuestions(conceptTags),
                learningApplication.getVoteTotals(question.getId()),
                learningApplication.getMyVote(question.getId()).orElse(null));
    }

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerResponse submitAnswer(@RequestBody SubmitAnswerRequest request) {
        return AnswerResponse.toDto(learningApplication.submitAnswer(toCommand(request)));
    }

    @PutMapping("/answers/{id}/assessment")
    @ResponseStatus(HttpStatus.OK)
    public AnswerResponse assessAnswer(@PathVariable Long id, @RequestBody AssessAnswerRequest request) {
        return AnswerResponse.toDto(learningApplication.assessAnswer(id, toCommand(request)));
    }

    /** Praises the question, or reports it as wrong, unclear, a duplicate, inappropriate or other; 409 if already reported. */
    @PostMapping("/questions/{id}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportQuestion(@PathVariable Long id, @RequestBody ReportQuestionRequest request) {
        learningApplication.reportQuestion(id, toCommand(request));
    }

    /** The learner's own open report on the question; 204 when they have none open. */
    @GetMapping("/questions/{id}/reports/mine")
    public ResponseEntity<MyReportResponse> getMyReport(@PathVariable Long id) {
        return learningApplication.getMyOpenReport(id)
                .map(report -> ResponseEntity.ok(MyReportResponse.toDto(report)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** Changes the reason and note of the learner's own open report; 404 when they have none open. */
    @PutMapping("/questions/{id}/reports/mine")
    @ResponseStatus(HttpStatus.OK)
    public MyReportResponse updateMyReport(@PathVariable Long id, @RequestBody ReportQuestionRequest request) {
        return MyReportResponse.toDto(learningApplication.updateMyOpenReport(id, toCommand(request)));
    }

    /** Withdraws the learner's own open report; 404 when they have none open. */
    @DeleteMapping("/questions/{id}/reports/mine")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawMyReport(@PathVariable Long id) {
        learningApplication.withdrawMyOpenReport(id);
    }

    @PutMapping("/questions/{id}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void voteOnQuestion(@PathVariable Long id, @RequestBody VoteRequest request) {
        learningApplication.voteOnQuestion(id, request.getVote());
    }

    @DeleteMapping("/questions/{id}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearVote(@PathVariable Long id) {
        learningApplication.clearVote(id);
    }

    /** The learner's answers, newest first; with questionId, only those to that question. */
    @GetMapping("/answers")
    @ResponseStatus(HttpStatus.OK)
    public List<AnswerResponse> getMyAnswers(@RequestParam(required = false) Long questionId) {
        final List<Answer> answers = questionId == null
                ? learningApplication.getMyAnswers()
                : learningApplication.getMyAnswers(questionId);
        return answers.stream().map(AnswerResponse::toDto).toList();
    }

    // ---------------------------------------------------------------------
    // Mapping: request DTOs to application commands
    // ---------------------------------------------------------------------

    private SubmitAnswerCommand toCommand(SubmitAnswerRequest request) {
        return SubmitAnswerCommand.builder()
                .questionId(request.getQuestionId())
                .answer(request.getAnswer())
                .build();
    }

    private ReportQuestionCommand toCommand(ReportQuestionRequest request) {
        return ReportQuestionCommand.builder()
                .reason(request.getReason())
                .note(request.getNote())
                .build();
    }

    private AssessAnswerCommand toCommand(AssessAnswerRequest request) {
        return AssessAnswerCommand.builder()
                .selfGrade(request.getSelfGrade())
                .perceivedDifficulty(request.getPerceivedDifficulty())
                .build();
    }
}
