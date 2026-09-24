package org.manu.hs.api.learning;

import lombok.RequiredArgsConstructor;
import org.manu.hs.api.learning.dto.AnswerResponse;
import org.manu.hs.api.learning.dto.AssessAnswerRequest;
import org.manu.hs.api.learning.dto.LearningQuestionResponse;
import org.manu.hs.api.learning.dto.SubmitAnswerRequest;
import org.manu.hs.application.learning.LearningApplication;
import org.manu.hs.application.learning.command.AssessAnswerCommand;
import org.manu.hs.application.learning.command.SubmitAnswerCommand;
import org.manu.hs.domain.answer.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * Learning: questions to answer, answers, and the learner's self-assessment of them.
 * Every endpoint requires signing in (SecurityConfig).
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
        return learningApplication.getNextQuestion(conceptTags, skip == null ? List.of() : skip)
                .map(question -> ResponseEntity.ok(LearningQuestionResponse.toDto(
                        question,
                        learningApplication.countUnansweredQuestions(conceptTags))))
                .orElseGet(() -> ResponseEntity.noContent().build());
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

    private AssessAnswerCommand toCommand(AssessAnswerRequest request) {
        return AssessAnswerCommand.builder()
                .selfGrade(request.getSelfGrade())
                .perceivedDifficulty(request.getPerceivedDifficulty())
                .build();
    }
}
