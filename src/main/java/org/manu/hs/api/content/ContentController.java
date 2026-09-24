package org.manu.hs.api.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.api.content.dto.CreateConceptRequest;
import org.manu.hs.api.content.dto.CreateQuestionRequest;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.api.content.dto.UpdateConceptRequest;
import org.manu.hs.api.content.dto.UpdateConceptStatusRequest;
import org.manu.hs.api.content.dto.UpdateQuestionRequest;
import org.manu.hs.api.content.dto.UpdateQuestionStatusRequest;
import org.manu.hs.application.content.ContentApplication;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentApplication applicationFacade;

    // ---------------------------------------------------------------------
    // Concepts
    // ---------------------------------------------------------------------

    @PostMapping("/concepts")
    @ResponseStatus(HttpStatus.CREATED)
    public ConceptResponse createConcept(@RequestBody CreateConceptRequest request) {
        final Concept created = applicationFacade.createConcept(toCommand(request));
        return ConceptResponse.toDto(created);
    }

    @DeleteMapping("/concepts/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConcept(@PathVariable String tag) {
        applicationFacade.deleteConcept(tag);
    }

    @PutMapping("/concepts/{tag}")
    @ResponseStatus(HttpStatus.OK)
    public ConceptResponse updateConcept(@RequestBody UpdateConceptRequest request, @PathVariable String tag) {
        final Concept updated = applicationFacade.updateConcept(tag, toCommand(request));
        return ConceptResponse.toDto(updated);
    }

    @PutMapping("/concepts/{tag}/status")
    @ResponseStatus(HttpStatus.OK)
    public ConceptResponse updateConceptStatus(@PathVariable String tag, @RequestBody UpdateConceptStatusRequest request) {
        final Concept updated = applicationFacade.updateConceptStatus(tag, request.getStatus());
        return ConceptResponse.toDto(updated);
    }

    @GetMapping("/concepts")
    @ResponseStatus(HttpStatus.OK)
    public List<ConceptResponse> getConcepts() {
        return applicationFacade.getConcepts().stream().map(ConceptResponse::toDto).toList();
    }

    @GetMapping("/concepts/review")
    @ResponseStatus(HttpStatus.OK)
    public List<ConceptResponse> getConceptsForReview(@RequestParam(required = false) ConceptStatus status) {
        return applicationFacade.getConceptsForReview(status).stream().map(ConceptResponse::toDto).toList();
    }

    // ---------------------------------------------------------------------
    // Questions
    // ---------------------------------------------------------------------

    @PostMapping("/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse submitQuestion(@RequestBody CreateQuestionRequest request) {
        final Question created = applicationFacade.submitQuestion(toCommand(request));
        return QuestionResponse.toDto(created);
    }

    @PutMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public QuestionResponse updateQuestion(@PathVariable Long id, @RequestBody UpdateQuestionRequest request) {
        final Question updated = applicationFacade.updateQuestion(id, toCommand(request));
        return QuestionResponse.toDto(updated);
    }

    @PutMapping("/questions/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public QuestionResponse updateQuestionStatus(@PathVariable Long id, @RequestBody UpdateQuestionStatusRequest request) {
        final Question updated = applicationFacade.updateQuestionStatus(id, request.getStatus());
        return QuestionResponse.toDto(updated);
    }

    @DeleteMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable Long id) {
        applicationFacade.deleteQuestion(id);
    }

    @GetMapping("/questions/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionResponse> getMySubmittedQuestions() {
        return applicationFacade.getMySubmittedQuestions().stream().map(QuestionResponse::toDto).toList();
    }

    @GetMapping("/questions/review")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionResponse> getQuestionsForReview(@RequestParam(required = false) QuestionStatus status) {
        return applicationFacade.getQuestionsForReview(status).stream().map(QuestionResponse::toDto).toList();
    }

    // ---------------------------------------------------------------------
    // Mapping: request DTOs to application commands
    // ---------------------------------------------------------------------

    private CreateConceptCommand toCommand(CreateConceptRequest request) {
        return CreateConceptCommand.builder()
                .tag(request.getTag())
                .name(request.getName())
                .description(request.getDescription())
                .externalReferences(request.getExternalReferences())
                .build();
    }

    private UpdateConceptCommand toCommand(UpdateConceptRequest request) {
        return UpdateConceptCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .externalReferences(request.getExternalReferences())
                .build();
    }

    private SubmitQuestionCommand toCommand(CreateQuestionRequest request) {
        return SubmitQuestionCommand.builder()
                .requirement(request.getRequirement())
                .sampleSolution(request.getSampleSolution())
                .type(request.getType())
                .difficultyScore(request.getDifficultyScore())
                .expectedDurationMinutes(request.getExpectedDurationMinutes())
                .source(request.getSource())
                .conceptTags(request.getConceptTags())
                .build();
    }

    private UpdateQuestionCommand toCommand(UpdateQuestionRequest request) {
        return UpdateQuestionCommand.builder()
                .requirement(request.getRequirement())
                .sampleSolution(request.getSampleSolution())
                .type(request.getType())
                .difficultyScore(request.getDifficultyScore())
                .expectedDurationMinutes(request.getExpectedDurationMinutes())
                .source(request.getSource())
                .conceptTags(request.getConceptTags())
                .build();
    }
}
