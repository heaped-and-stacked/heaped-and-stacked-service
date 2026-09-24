package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.ConceptLinkStatus;
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

        // Concepts are published straight away and owned by their author; no review needed.
        return conceptService.create(command, ConceptStatus.ACTIVE, userProvider.userId());
    }

    public void deleteConcept(String tag) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeDeletedBy(existing, userProvider.userId(), userProvider.isAdmin());
        conceptService.requireNotInUse(tag);

        conceptService.delete(tag);
    }

    public Concept updateConcept(String tag, UpdateConceptCommand command) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        conceptService.validate(command);

        return conceptService.update(existing, command, userProvider.userId());
    }

    /**
     * The owner publishes their concept (ACTIVE); admins may set any status. Checks run
     * 404, 403 (owner or admin), 400, 403 (owners only publish), 409.
     */
    public Concept updateConceptStatus(String tag, ConceptStatus newStatus) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        requireNonNull(newStatus, "status");
        conceptService.requireCanChangeStatusTo(newStatus, userProvider.isAdmin());
        conceptService.requireCanMoveTo(tag, newStatus);

        return conceptService.updateStatus(tag, newStatus, userProvider.userId());
    }

    public List<Concept> getConcepts() {
        return conceptService.findAll();
    }

    // ---------------------------------------------------------------------
    // Questions
    // ---------------------------------------------------------------------

    public Question submitQuestion(SubmitQuestionCommand command) {
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());
        // Questions are submitted straight away; drafts aren't offered yet.
        questionService.requireNoDraftConcepts(concepts);

        final List<String> awaitingApproval = questionService.tagsAwaitingApproval(concepts, userProvider.userId());
        return questionService.create(command, QuestionStatus.SUBMITTED, userProvider.userId(), awaitingApproval);
    }

    public Question updateQuestion(Long id, UpdateQuestionCommand command) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isAdmin());
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());

        if (existing.getStatus() == QuestionStatus.SUBMITTED) {
            questionService.requireNoDraftConcepts(concepts);
        }

        // Whether a new link waits depends on the question's author, not on who edits it.
        final List<String> awaitingApproval = questionService.tagsAwaitingApproval(concepts, existing.getCreatedBy());
        return questionService.update(existing, command, userProvider.userId(), awaitingApproval);
    }

    public Question updateQuestionStatus(Long id, QuestionStatus newStatus) {
        final Question existing = questionService.findByIdOrThrow(id);
        userProvider.requireAdmin("change the status of a question");
        requireNonNull(newStatus, "status");

        if (newStatus == QuestionStatus.SUBMITTED) {
            questionService.requireNoDraftConcepts(conceptService.findByTags(existing.getConceptTags()));
        }

        return questionService.updateStatus(id, newStatus, userProvider.userId());
    }

    public void deleteQuestion(Long id) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeDeletedBy(existing, userProvider.userId(), userProvider.isAdmin());
        questionService.requireNotAnswered(id);

        questionService.delete(id);
    }

    public List<Question> getMySubmittedQuestions() {
        return questionService.findBySubmittedBy(userProvider.userId());
    }

    // ---------------------------------------------------------------------
    // Reviews: a concept's owner decides which contributed questions it accepts
    // ---------------------------------------------------------------------

    /** Questions waiting on the caller's own concepts; admins see those on every concept. */
    public List<ConceptLink> getReviews() {
        return questionService.findLinksAwaitingReview(userProvider.userId(), userProvider.isAdmin());
    }

    /**
     * The concept's owner (or an admin) accepts or declines a question for that concept only.
     * A decision can be changed later, for example to decline a duplicate that was accepted.
     */
    public void reviewQuestion(Long questionId, String conceptTag, ConceptLinkStatus decision) {
        final Question question = questionService.findByIdOrThrow(questionId);
        questionService.requireLinkedTo(question, conceptTag);
        final Concept concept = conceptService.findByTagOrThrow(conceptTag);
        conceptService.requireCanBeModifiedBy(concept, userProvider.userId(), userProvider.isAdmin());
        questionService.validateReviewDecision(decision);

        questionService.decide(questionId, conceptTag, decision, userProvider.userId());
    }
}