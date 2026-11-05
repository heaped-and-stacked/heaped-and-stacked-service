package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.content.command.CreateConceptCommand;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateConceptCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptWorkload;
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

    /** Whether the current user may add questions (staff always; members when open to them). */
    public boolean canAddQuestions() {
        return conceptService.canAddQuestions(userProvider.isStaff());
    }

    /** Whether the current user may add concepts (staff always; members when open to them). */
    public boolean canAddConcepts() {
        return conceptService.canAddConcepts(userProvider.isStaff());
    }

    public Concept createConcept(CreateConceptCommand command) {
        conceptService.requireCanAddConcepts(userProvider.isStaff());
        conceptService.validate(command);
        conceptService.requireTagAvailable(command.getTag());
        conceptService.requireRoomForAnotherConcept(userProvider.userId(), userProvider.isStaff());

        // Owned by their author and published straight away, as their own: it becomes
        // featured only when staff feature it.
        return conceptService.create(command, ConceptStatus.ACTIVE, userProvider.userId());
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
     * Its curators set any status; staff may also retire a member's own concept (moderation), or
     * bring it back. A featured concept stays published until it's unfeatured. Checks run
     * 404, 403 (curator or staff), 400, 403 (staff only retire others' own), 409.
     */
    public Concept updateConceptStatus(String tag, ConceptStatus newStatus) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        conceptService.requireCanChangeStatusOf(existing, userProvider.userId(), userProvider.isStaff());
        requireNonNull(newStatus, "status");
        conceptService.requireCanChangeStatusTo(existing, newStatus, userProvider.userId(), userProvider.isStaff());
        conceptService.requireCanMoveTo(tag, newStatus);

        final Concept updated = conceptService.updateStatus(tag, newStatus, userProvider.userId());
        AUDIT.info("concept_status_changed by={} concept={} status={}", userProvider.userId(), tag, newStatus);
        return updated;
    }

    /**
     * Staff take a member's published concept into the featured ones, or out again;
     * its owner stays its owner. House-owned concepts always stay featured. Checks run 404, 403
     * (staff), 400, 409 (published; not house-owned).
     */
    public Concept featureConcept(String tag, Boolean featured) {
        final Concept existing = conceptService.findByTagOrThrow(tag);
        userProvider.requireStaff("feature a concept");
        requireNonNull(featured, "featured");
        if (featured) {
            conceptService.requirePublished(existing);
        } else {
            conceptService.requireNotHouseOwned(existing);
        }

        final Concept updated = conceptService.updateFeatured(tag, featured, userProvider.userId());
        AUDIT.info("concept_featured by={} concept={} featured={}", userProvider.userId(), tag, featured);
        return updated;
    }

    public List<Concept> getConcepts() {
        return conceptService.findVisibleTo(userProvider.userId(), userProvider.isStaff());
    }

    // ---------------------------------------------------------------------
    // Questions
    // ---------------------------------------------------------------------

    public Question submitQuestion(SubmitQuestionCommand command) {
        conceptService.requireCanAddQuestions(userProvider.isStaff());
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
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), curatesAsStaff(existing));
        questionService.validate(command);

        final List<Concept> concepts = conceptService.findByTagsOrThrow(command.getConceptTags());

        if (existing.getStatus() == QuestionStatus.SUBMITTED) {
            questionService.requireNoDraftConcepts(concepts);
        }

        // Whether a new link waits depends on the question's author, unless staff edit it: they curate
        // every concept, so they would only approve the link themselves.
        final List<String> awaitingApproval = questionService.tagsAwaitingApproval(
                concepts, existing.getCreatedBy(), userProvider.isStaff());
        final Question saved = questionService.update(existing, command, userProvider.userId(), awaitingApproval);

        // The author asks again by picking a concept that declined the question.
        if (existing.getCreatedBy().equals(userProvider.userId())) {
            questionService.reopenDeclined(id, command.getConceptTags());
        }
        final Question updated = questionService.findByIdOrThrow(saved.getId());

        // An edit by someone who may verify the question keeps the check with the fix; anyone
        // else's edit (an author who doesn't curate it) means it's checked again.
        if (updated.isVerified() && !questionService.canVerify(id, userProvider.userId(), userProvider.isStaff())) {
            questionService.verify(id, false, userProvider.userId());
            AUDIT.info("question_verified by={} question={} verified=false (edited)", userProvider.userId(), id);
            return questionService.findByIdOrThrow(id);
        }
        return updated;
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

    /**
     * One question, whole, for whoever may change it (its author, or staff when it's on a featured concept), to
     * edit it; or verify it (a concept's owner), to review it before they do.
     */
    public Question getQuestion(Long id) {
        final Question existing = questionService.findByIdOrThrow(id);
        if (!questionService.canVerify(id, userProvider.userId(), userProvider.isStaff())) {
            questionService.requireCanBeModifiedBy(existing, userProvider.userId(), curatesAsStaff(existing));
        }

        return existing;
    }

    /**
     * One page of the questions the caller curates: their own, every one on a featured concept for staff,
     * and every one on a filtered concept the caller curates.
     */
    public List<Question> getCuratedQuestions(
            boolean asMember, String conceptTag, String titleSearch, Boolean verified, int page, int size) {
        questionService.validatePage(page, size);
        return questionService.findCurated(
                userProvider.userId(), staffUnlessAsMember(asMember), conceptTag, titleSearch, verified, page, size);
    }

    /** How many questions getCuratedQuestions finds across every page. */
    public long countCuratedQuestions(boolean asMember, String conceptTag, String titleSearch, Boolean verified) {
        return questionService.countCurated(
                userProvider.userId(), staffUnlessAsMember(asMember), conceptTag, titleSearch, verified);
    }

    /**
     * Staff see these lists as curators, unless they ask to see them as a member would
     * (asMember: the web app's reviewer mode off): then only what they wrote, like anyone else.
     */
    private boolean staffUnlessAsMember(boolean asMember) {
        return userProvider.isStaff() && !asMember;
    }

    /**
     * For each concept the caller curates (their own, and for staff the featured ones): its live
     * questions, how many are still to verify, and how many wait for approval.
     */
    public List<ConceptWorkload> getConceptWorkload() {
        return conceptService.findWorkload(userProvider.userId(), userProvider.isStaff());
    }

    /**
     * Staff curate a question (edit it, read its feedback) while it's on a featured concept: linked to
     * at least one featured concept. A question only on members' own concepts is its author's.
     */
    private boolean curatesAsStaff(Question question) {
        return userProvider.isStaff()
                && conceptService.findByTags(question.getConceptTags()).stream()
                        .anyMatch(concept -> concept.isFeatured() || concept.isHouseOwned());
    }

    /** How many learners voted each question up and down; questions nobody voted on are left out. */
    public Map<Long, VoteTotals> getVoteTotals(List<Long> questionIds) {
        return questionService.countVotes(questionIds);
    }

    // ---------------------------------------------------------------------
    // Reviews: a concept's owner decides which contributed questions it accepts
    // ---------------------------------------------------------------------

    /**
     * One page of the questions waiting on the caller's own concepts, and for staff on featured
     * ones too.
     */
    public List<ConceptLink> getReviews(int page, int size) {
        questionService.validatePage(page, size);
        return questionService.findLinksAwaitingReview(userProvider.userId(), userProvider.isStaff(), page, size);
    }

    /** How many questions getReviews finds across every page. */
    public long countReviews() {
        return questionService.countLinksAwaitingReview(userProvider.userId(), userProvider.isStaff());
    }

    /**
     * The concept's owner (or staff) accept or decline a question for that concept only.
     * An accepted question can still be declined later, for example a duplicate spotted later; a
     * decline stands (the concept drops off the question) until the author picks the concept again.
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

    /**
     * Whoever curates the question (staff for featured ones; otherwise the owners of its concepts)
     * marks it verified, checked for quality, or takes it back. Learners can train on verified
     * questions only. Checks run 404, 403, 400, 409 (accepted somewhere, to verify).
     */
    public void verifyQuestion(Long questionId, Boolean verified) {
        questionService.findByIdOrThrow(questionId);
        questionService.requireCanVerify(questionId, userProvider.userId(), userProvider.isStaff());
        requireNonNull(verified, "verified");
        if (verified) {
            questionService.requireAcceptedAnywhere(questionId);
        }

        questionService.verify(questionId, verified, userProvider.userId());
        AUDIT.info("question_verified by={} question={} verified={}", userProvider.userId(), questionId, verified);
    }

    // ---------------------------------------------------------------------
    // Reports: learners flag questions; the author or a concept owner decides
    // ---------------------------------------------------------------------

    /**
     * One page of the reports on the caller's questions and on questions linked to their concepts;
     * staff see all. Open ones (to decide), or the decided ones (the history).
     */
    public List<Report> getReports(boolean asMember, boolean decided, int page, int size) {
        questionService.validatePage(page, size);
        return questionService.findReports(userProvider.userId(), staffUnlessAsMember(asMember), decided, page, size);
    }

    /** How many reports getReports finds across every page. */
    public long countReports(boolean asMember, boolean decided) {
        return questionService.countReports(userProvider.userId(), staffUnlessAsMember(asMember), decided);
    }

    /**
     * Every report on one question, for whoever may change it (its author, or staff): the open ones
     * first, then the decided ones. Concept owners who didn't write the question decide its reports
     * through the reports list (getReports).
     */
    public List<Report> getQuestionReports(Long questionId) {
        final Question existing = questionService.findByIdOrThrow(questionId);
        questionService.requireCanBeModifiedBy(existing, userProvider.userId(), curatesAsStaff(existing));

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