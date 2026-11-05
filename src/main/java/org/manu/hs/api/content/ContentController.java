package org.manu.hs.api.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.api.content.dto.ConceptWorkloadResponse;
import org.manu.hs.api.content.dto.FeaturedRequest;
import org.manu.hs.api.content.dto.CreateConceptRequest;
import org.manu.hs.api.content.dto.CreateQuestionRequest;
import org.manu.hs.api.content.dto.QuestionPageResponse;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.api.content.dto.ReportPageResponse;
import org.manu.hs.api.content.dto.ReportResponse;
import org.manu.hs.api.content.dto.ResolveReportRequest;
import org.manu.hs.api.content.dto.ReviewDecisionRequest;
import org.manu.hs.api.content.dto.ReviewPageResponse;
import org.manu.hs.api.content.dto.ReviewResponse;
import org.manu.hs.api.content.dto.UpdateConceptRequest;
import org.manu.hs.api.content.dto.UpdateConceptStatusRequest;
import org.manu.hs.api.content.dto.UpdateQuestionRequest;
import org.manu.hs.api.content.dto.UpdateQuestionStatusRequest;
import org.manu.hs.api.content.dto.VerificationRequest;
import org.manu.hs.application.content.ContentApplication;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.VoteTotals;
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
import java.util.Map;

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

    /** Staff feature a published concept or unfeature it. */
    @PutMapping("/concepts/{tag}/featured")
    @ResponseStatus(HttpStatus.OK)
    public ConceptResponse featureConcept(@PathVariable String tag, @RequestBody FeaturedRequest request) {
        final Concept updated = applicationFacade.featureConcept(tag, request.getFeatured());
        return ConceptResponse.toDto(updated);
    }

    /** Open to visitors. Drafts are left out unless the caller curates them (their owner, or staff). */
    @GetMapping("/concepts")
    @ResponseStatus(HttpStatus.OK)
    public List<ConceptResponse> getConcepts() {
        return applicationFacade.getConcepts().stream().map(ConceptResponse::toDto).toList();
    }

    /** For each concept the caller curates: its questions, how many to verify, how many waiting. */
    @GetMapping("/concepts/workload")
    @ResponseStatus(HttpStatus.OK)
    public List<ConceptWorkloadResponse> getConceptWorkload() {
        return applicationFacade.getConceptWorkload().stream().map(ConceptWorkloadResponse::toDto).toList();
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

    @GetMapping("/questions")
    @ResponseStatus(HttpStatus.OK)
    public QuestionPageResponse getQuestions(
            @RequestParam(required = false) String concept,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "false") boolean asMember,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        final List<Question> questions = applicationFacade.getCuratedQuestions(asMember, concept, search, verified, page, size);
        final long totalItems = applicationFacade.countCuratedQuestions(asMember, concept, search, verified);
        final Map<Long, VoteTotals> votes = applicationFacade.getVoteTotals(
                questions.stream().map(Question::getId).toList());
        return QuestionPageResponse.builder()
                .items(questions.stream()
                        .map(question -> QuestionResponse.toDto(question, votes.getOrDefault(question.getId(), VoteTotals.NONE)))
                        .toList())
                .page(page)
                .size(size)
                .totalItems(totalItems)
                .totalPages((int) ((totalItems + size - 1) / size))
                .build();
    }

    @GetMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public QuestionResponse getQuestion(@PathVariable Long id) {
        final Question question = applicationFacade.getQuestion(id);
        final VoteTotals votes = applicationFacade.getVoteTotals(List.of(id)).getOrDefault(id, VoteTotals.NONE);
        return QuestionResponse.toDto(question, votes);
    }

    // ---------------------------------------------------------------------
    // Reviews
    // ---------------------------------------------------------------------

    /** Questions waiting for approval, oldest first. */
    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    public ReviewPageResponse getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        final List<ConceptLink> reviews = applicationFacade.getReviews(page, size);
        final long totalItems = applicationFacade.countReviews();
        return ReviewPageResponse.builder()
                .items(reviews.stream().map(ReviewResponse::toDto).toList())
                .page(page)
                .size(size)
                .totalItems(totalItems)
                .totalPages((int) ((totalItems + size - 1) / size))
                .build();
    }

    @PutMapping("/questions/{id}/concepts/{tag}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewQuestion(
            @PathVariable Long id,
            @PathVariable String tag,
            @RequestBody ReviewDecisionRequest request) {
        applicationFacade.reviewQuestion(id, tag, request.getStatus());
    }

    /** Verifies the question (checked for quality), or takes it back. */
    @PutMapping("/questions/{id}/verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyQuestion(@PathVariable Long id, @RequestBody VerificationRequest request) {
        applicationFacade.verifyQuestion(id, request.getVerified());
    }

    // ---------------------------------------------------------------------
    // Reports
    // ---------------------------------------------------------------------

    /**
     * Open reports the caller can decide, oldest first; with decided=true, the decided ones
     * (resolved or dismissed), newest decision first.
     */
    @GetMapping("/reports")
    @ResponseStatus(HttpStatus.OK)
    public ReportPageResponse getReports(
            @RequestParam(defaultValue = "false") boolean decided,
            @RequestParam(defaultValue = "false") boolean asMember,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        final List<Report> reports = applicationFacade.getReports(asMember, decided, page, size);
        final long totalItems = applicationFacade.countReports(asMember, decided);
        return ReportPageResponse.builder()
                .items(reports.stream().map(ReportResponse::toDto).toList())
                .page(page)
                .size(size)
                .totalItems(totalItems)
                .totalPages((int) ((totalItems + size - 1) / size))
                .build();
    }

    /**
     * Every report on one question, for whoever may change it (its author, or staff): open ones
     * first, oldest first; then decided ones, newest decision first.
     */
    @GetMapping("/questions/{id}/reports")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportResponse> getQuestionReports(@PathVariable Long id) {
        return applicationFacade.getQuestionReports(id).stream().map(ReportResponse::toDto).toList();
    }

    @PutMapping("/reports/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resolveReport(@PathVariable Long id, @RequestBody ResolveReportRequest request) {
        applicationFacade.resolveReport(id, request.getStatus());
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
                .contributionPolicy(request.getContributionPolicy())
                .build();
    }

    private UpdateConceptCommand toCommand(UpdateConceptRequest request) {
        return UpdateConceptCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .externalReferences(request.getExternalReferences())
                .contributionPolicy(request.getContributionPolicy())
                .build();
    }

    private SubmitQuestionCommand toCommand(CreateQuestionRequest request) {
        return SubmitQuestionCommand.builder()
                .title(request.getTitle())
                .requirement(request.getRequirement())
                .sampleSolution(request.getSampleSolution())
                .difficultyScore(request.getDifficultyScore())
                .source(request.getSource())
                .conceptTags(request.getConceptTags())
                .build();
    }

    private UpdateQuestionCommand toCommand(UpdateQuestionRequest request) {
        return UpdateQuestionCommand.builder()
                .title(request.getTitle())
                .requirement(request.getRequirement())
                .sampleSolution(request.getSampleSolution())
                .difficultyScore(request.getDifficultyScore())
                .source(request.getSource())
                .conceptTags(request.getConceptTags())
                .build();
    }
}
