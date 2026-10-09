package org.manu.hs.domain.question;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionRepository {
    /**
     * Stores the question and links it to its concepts: PENDING for the tags awaiting approval,
     * ACCEPTED for the others.
     */
    Question insert(Question question, List<String> tagsAwaitingApproval);
    /**
     * Stores the edited question. Links to concepts it no longer has are removed; new links are
     * PENDING or ACCEPTED as on insert; links it already had keep their state.
     */
    Question update(Long id, Question question, List<String> tagsAwaitingApproval);
    Question updateStatus(Long id, QuestionStatus status, Long updatedBy);
    void deleteById(Long id);
    /** Whether any learner has answered the question; answered questions are retired, not deleted. */
    boolean isReferencedByAnyAnswer(Long id);
    Optional<Question> findById(Long id);
    List<Question> findBySubmittedBy(Long userId);
    /**
     * One page of the questions Coach's Corner lists, newest first. Each filter is optional (null):
     * the author, a concept the question is on (main or not), and part of the title, any case.
     */
    List<Question> findCurated(Long createdBy, String conceptTag, String titleSearch, int limit, long offset);
    /** How many questions match the same filters as findCurated, across every page. */
    long countCurated(Long createdBy, String conceptTag, String titleSearch);
    /** Links of submitted questions waiting for approval, on any concept; oldest question first. */
    List<ConceptLink> findLinksAwaitingReview();
    /** Links of submitted questions waiting for approval on the owner's concepts; oldest question first. */
    List<ConceptLink> findLinksAwaitingReviewByConceptOwner(Long ownerId);
    void updateLinkStatus(Long questionId, String conceptTag, ConceptLinkStatus status, Long decidedBy);
    /**
     * The question, if learners can answer it: submitted, and accepted for at least one active
     * concept. Its concept tags are only those concepts.
     */
    Optional<Question> findAvailableForLearningById(Long id);
    /**
     * One question learners can answer (as in findAvailableForLearningById), leaving out the
     * skipped ones and, with concept tags, those accepted for none of them. Questions the learner
     * never answered come first, in random order; then answered ones at random, weighted towards
     * those answered longest ago.
     */
    Optional<Question> findNextForLearning(Long userId, List<String> conceptTags, List<Long> skippedIds);
    /**
     * How many questions learners can answer (as in findNextForLearning, without skipping any)
     * this learner has never answered.
     */
    int countUnansweredForLearning(Long userId, List<String> conceptTags);

    // ---------------------------------------------------------------------
    // Feedback: reports and votes
    // ---------------------------------------------------------------------

    /** Stores an OPEN report on the report's question. */
    Report insertReport(Report report);
    Optional<Report> findReportById(Long id);
    /** Whether this learner already has an open report on the question. */
    boolean hasOpenReport(Long questionId, Long reportedBy);
    /** This learner's open report on the question, if any (one at most). */
    Optional<Report> findOpenReport(Long questionId, Long reportedBy);
    /** Changes what an open report says (its reason and note); empty when it's no longer open. */
    Optional<Report> updateOpenReportContent(Long id, ReportReason reason, String note);
    /** Removes an open report, as if it was never sent; false when it's no longer open. */
    boolean deleteOpenReport(Long id);
    /** Open reports on every question; oldest first. */
    List<Report> findOpenReports();
    /** Open reports on questions the user wrote or that are linked to a concept they own; oldest first. */
    List<Report> findOpenReportsVisibleTo(Long userId);
    /** The latest decided (resolved or dismissed) reports on every question; newest decision first. */
    List<Report> findDecidedReports(int limit);
    /** The same, on questions the user wrote or that are linked to a concept they own. */
    List<Report> findDecidedReportsVisibleTo(Long userId, int limit);
    /** Every report on one question: the open ones first, oldest first; then the decided ones, newest decision first. */
    List<Report> findReportsByQuestion(Long questionId);
    /**
     * Decides an open report (resolved or dismissed); empty when it was already decided. The check
     * is part of the update, so of two racing decisions only the first one counts.
     */
    Optional<Report> decideOpenReport(Long id, ReportStatus status, Long resolvedBy);
    /** Records the learner's vote, replacing an earlier one on the same question. */
    void saveVote(Long questionId, Long userId, Vote vote);
    void deleteVote(Long questionId, Long userId);
    /** This learner's vote on the question, if they voted. */
    Optional<Vote> findVote(Long questionId, Long userId);
    /** Vote totals for the given questions; questions without votes are left out. */
    Map<Long, VoteTotals> countVotes(List<Long> questionIds);
}