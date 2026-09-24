package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.manu.hs.application.common.Validations.requireNonNull;

/**
 * Entry point of the content feature: the only class the API layer uses.
 * <p>
 * Each use case checks the caller's permissions and runs the steps in the agreed order
 * (404, then 403, then 400, then 409). It is the only place that reads the current user;
 * ConceptService and QuestionService receive the caller's id and hold the validation,
 * rules, building and persistence.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContentApplication {

    private final ConceptService conceptService;
    private final QuestionService questionService;
    private final CurrentUserProvider userProvider;

    // ---------------------------------------------------------------------
    // Concepts
    // ---------------------------------------------------------------------

    public Concept createConcept(CreateConceptCommand command) {
        conceptService.validate(command);
        conceptService.requireTagAvailable(command.getTag());

        final ConceptStatus status = userProvider.isAdmin() ? ConceptStatus.ACTIVE : ConceptStatus.DRAFT;
        return conceptService.create(command, status, userProvider.userId());
    }

    public void deleteConcept(String tag) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        conceptService.requireNotInUse(tag);

        conceptService.delete(tag);
    }

    public Concept updateConcept(String tag, UpdateConceptCommand command) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        conceptService.validate(command);

        return conceptService.update(tag, command, userProvider.userId());
    }

    public Concept updateConceptStatus(String tag, ConceptStatus newStatus) {
        conceptService.findByTagOrThrow(tag);
        userProvider.requireAdmin("change the status of a concept");
        requireNonNull(newStatus, "status");
        conceptService.requireCanMoveTo(tag, newStatus);

        return conceptService.updateStatus(tag, newStatus, userProvider.userId());
    }

    public List<Concept> getConcepts() {
        return conceptService.findAll();
    }

    public List<Concept> getConceptsForReview(ConceptStatus statusFilter) {
        userProvider.requireAdmin("view the concept review queue");

        final ConceptStatus status = statusFilter == null ? ConceptStatus.DRAFT : statusFilter;
        return conceptService.findByStatus(status);
    }

    // ---------------------------------------------------------------------
    // Questions
    // ---------------------------------------------------------------------

    public Question submitQuestion(SubmitQuestionCommand command) {
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());

        final QuestionStatus status = userProvider.isAdmin() ? QuestionStatus.ACTIVE : QuestionStatus.DRAFT;
        if (status == QuestionStatus.ACTIVE) {
            questionService.requireNoDraftConcepts(concepts);
        }

        return questionService.create(command, status, userProvider.userId());
    }

    public Question updateQuestion(Long id, UpdateQuestionCommand command) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());

        if (existing.getStatus() == QuestionStatus.ACTIVE) {
            questionService.requireNoDraftConcepts(concepts);
        }

        return questionService.update(existing, command, userProvider.userId());
    }

    public Question updateQuestionStatus(Long id, QuestionStatus newStatus) {
        final Question existing = questionService.findByIdOrThrow(id);
        userProvider.requireAdmin("change the status of a question");
        requireNonNull(newStatus, "status");

        if (newStatus == QuestionStatus.ACTIVE) {
            questionService.requireNoDraftConcepts(conceptService.findByTags(existing.getConceptTags()));
        }

        return questionService.updateStatus(id, newStatus, userProvider.userId());
    }

    public void deleteQuestion(Long id) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());

        questionService.delete(id);
    }

    public List<Question> getMySubmittedQuestions() {
        return questionService.findBySubmittedBy(userProvider.userId());
    }

    public List<Question> getQuestionsForReview(QuestionStatus statusFilter) {
        userProvider.requireAdmin("view the review queue");

        final QuestionStatus status = statusFilter == null ? QuestionStatus.DRAFT : statusFilter;
        return questionService.findByStatus(status);
    }
}