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
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportStatus;
import org.manu.hs.domain.question.VoteTotals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.manu.hs.application.common.Validations.requireNonNull;

/**
 * Entry point of the content feature: the only class the API layer uses.
 * <p>
 * Each use case checks the caller's permissions and runs the steps in the agreed order
 * (404, then 403, then 400, then 409). It is the only place that reads the current user;
 * ConceptService and QuestionService receive the caller's id and hold the validation,
 * rules, building and persistence.
 * <p>
 * Deletions, status changes, report decisions and review decisions are written to the "audit"
 * log: one line per action, ids only.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContentApplication {

    private static final Logger AUDIT = LoggerFactory.getLogger("audit");

    private final ConceptService conceptService;
    private final QuestionService questionService;
    private final CurrentUserProvider userProvider;

    // ---------------------------------------------------------------------
    // Concepts
    // ---------------------------------------------------------------------

    /** Whether the current user may add concepts and questions (staff always; members when open to them). */
    public boolean canContribute() {
        return conceptService.canContribute(userProvider.isStaff());
    }

    public Concept createConcept(CreateConceptCommand command) {
        conceptService.requireCanContribute(userProvider.isStaff());
        conceptService.validate(command);
        conceptService.requireTagAvailable(command.getTag());

        // Owned by their author. Published straight away, unless only staff publish concepts:
        // then a member's new concept waits as a draft for staff.
        final ConceptStatus status = conceptService.startingStatus(userProvider.isStaff());
        return conceptService.create(command, status, userProvider.userId());
    }

    public void deleteConcept(String tag) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeDeletedBy(existing, userProvider.userId(), userProvider.isStaff());
        conceptService.requireNotInUse(tag);

        conceptService.delete(tag);
        AUDIT.info("concept_deleted by={} concept={}", userProvider.userId(), tag);
    }

    public Concept updateConcept(String tag, UpdateConceptCommand command) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isStaff());
        conceptService.validate(command);

        return conceptService.update(existing, command, userProvider.userId());
    }

    /**
     * The owner publishes their concept (ACTIVE); staff may set any status. Checks run
     * 404, 403 (owner or staff), 400, 403 (owners only publish), 409.
     */
    public Concept updateConceptStatus(String tag, ConceptStatus newStatus) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isStaff());
        requireNonNull(newStatus, "status");
        conceptService.requireCanChangeStatusTo(newStatus, userProvider.isStaff());
        conceptService.requireCanMoveTo(tag, newStatus);

        final Concept updated = conceptService.updateStatus(tag, newStatus, userProvider.userId());
        AUDIT.info("concept_status_changed by={} concept={} status={}", userProvider.userId(), tag, newStatus);
        return updated;
    }

    public List<Concept> getConcepts() {
        return conceptService.findVisibleTo(userProvider.userId(), userProvider.isStaff());
    }

    // ---------------------------------------------------------------------
    // Questions
    // ---------------------------------------------------------------------

    public Question submitQuestion(SubmitQuestionCommand command) {
        conceptService.requireCanContribute(userProvider.isStaff());
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());
        // Questions are submitted straight away; drafts aren't offered yet.
        questionService.requireNoDraftConcepts(concepts);

        final List<String> awaitingApproval = questionService.tagsAwaitingApproval(
                concepts, userProvider.userId(), userProvider.isStaff());
        return questionService.create(command, QuestionStatus.SUBMITTED, userProvider.userId(), awaitingApproval);
    }

    public Question updateQuestion(Long id, UpdateQuestionCommand command) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isStaff());
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());

        if (existing.getStatus() == QuestionStatus.SUBMITTED) {
            questionService.requireNoDraftConcepts(concepts);
        }

        // Whether a new link waits depends on the question's author, unless staff edit it: they curate
        // every concept, so they would only approve the link themselves.
        final List<String> awaitingApproval = questionService.tagsAwaitingApproval(
                concepts, existing.getCreatedBy(), userProvider.isStaff());
        return questionService.update(existing, command, userProvider.userId(), awaitingApproval);
    }

    public Question updateQuestionStatus(Long id, QuestionStatus newStatus) {
        final Question existing = questionService.findByIdOrThrow(id);
        userProvider.requireStaff("change the status of a question");
        requireNonNull(newStatus, "status");

        if (newStatus == QuestionStatus.SUBMITTED) {
            questionService.requireNoDraftConcepts(conceptService.findByTags(existing.getConceptTags()));
        }

        final Question updated = questionService.updateStatus(id, newStatus, userProvider.userId());
        AUDIT.info("question_status_changed by={} question={} status={}", userProvider.userId(), id, newStatus);
        return updated;
    }

    public void deleteQuestion(Long id) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeDeletedBy(existing, userProvider.userId(), userProvider.isStaff());
        questionService.requireNotAnswered(id);

        questionService.delete(id);
        AUDIT.info("question_deleted by={} question={}", userProvider.userId(), id);
    }

    /** One question, whole, for whoever may change it (its author, or staff): to edit or preview it. */
    public Question getQuestion(Long id) {
        final Question existing = questionService.findByIdOrThrow(id);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isStaff());

        return existing;
    }

    public List<Question> getMySubmittedQuestions() {
        return questionService.findBySubmittedBy(userProvider.userId());
    }

    /** One page of Coach's Corner: every author's questions for staff, the caller's own otherwise. */
    public List<Question> getCuratedQuestions(String conceptTag, String titleSearch, int page, int size) {
        questionService.validatePage(page, size);
        return questionService.findCurated(curatedAuthor(), conceptTag, titleSearch, page, size);
    }

    /** How many questions getCuratedQuestions finds across every page. */
    public long countCuratedQuestions(String conceptTag, String titleSearch) {
        return questionService.countCurated(curatedAuthor(), conceptTag, titleSearch);
    }

    private Long curatedAuthor() {
        return userProvider.isStaff() ? null : userProvider.userId();
    }

    /** How many learners voted each question up and down; questions nobody voted on are left out. */
    public Map<Long, VoteTotals> getVoteTotals(List<Long> questionIds) {
        return questionService.countVotes(questionIds);
    }

    // ---------------------------------------------------------------------
    // Reviews: a concept's owner decides which contributed questions it accepts
    // ---------------------------------------------------------------------

    /** Questions waiting on the caller's own concepts; staff see those on every concept. */
    public List<ConceptLink> getReviews() {
        return questionService.findLinksAwaitingReview(userProvider.userId(), userProvider.isStaff());
    }

    /**
     * The concept's owner (or staff) accept or decline a question for that concept only.
     * An accepted question can still be declined later, for example a duplicate spotted later; a
     * decline is final (the concept drops off the question, see QuestionJdbcRepository).
     */
    public void reviewQuestion(Long questionId, String conceptTag, ConceptLinkStatus decision) {
        final Question question = questionService.findByIdOrThrow(questionId);
        final Concept concept = conceptService.findByTagOrThrow(conceptTag);
        conceptService.requireCanBeModifiedBy(concept, userProvider.userId(), userProvider.isStaff());
        // After the curator check: only a concept's curators learn which questions are linked to it.
        questionService.requireLinkedTo(question, conceptTag);
        questionService.validateReviewDecision(decision);

        questionService.decide(questionId, conceptTag, decision, userProvider.userId());
        AUDIT.info("link_decided by={} question={} concept={} status={}",
                userProvider.userId(), questionId, conceptTag, decision);
    }

    // ---------------------------------------------------------------------
    // Reports: learners flag questions; the author or a concept owner decides
    // ---------------------------------------------------------------------

    /**
     * Reports on the caller's questions and on questions linked to their concepts; staff see all.
     * Open ones (to decide), or the latest decided ones (the history).
     */
    public List<Report> getReports(boolean decided) {
        return decided
                ? questionService.findDecidedReports(userProvider.userId(), userProvider.isStaff())
                : questionService.findOpenReports(userProvider.userId(), userProvider.isStaff());
    }

    /**
     * Every report on one question, for whoever may change it (its author, or staff): the open ones
     * first, then the decided ones. Concept owners who didn't write the question decide its reports
     * through the reports list (getReports).
     */
    public List<Report> getQuestionReports(Long questionId) {
        final Question existing = questionService.findByIdOrThrow(questionId);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), userProvider.isStaff());

        return questionService.findReportsByQuestion(questionId);
    }

    /**
     * The question's author, a linked concept's owner or staff resolve the report (the question
     * was fixed) or dismisses it (nothing to fix). Decided reports no longer hold the question back.
     */
    public void resolveReport(Long reportId, ReportStatus decision) {
        final Report report = questionService.findReportOrThrow(reportId);
        final List<Concept> concepts = conceptService.findByTags(report.getQuestion().getConceptTags());
        questionService.requireCanDecideReport(report, concepts, userProvider.userId(), userProvider.isStaff());
        questionService.validateReportDecision(decision);

        questionService.decideReport(reportId, decision, userProvider.userId());
        AUDIT.info("report_decided by={} report={} question={} status={}",
                userProvider.userId(), reportId, report.getQuestion().getId(), decision);
    }
}