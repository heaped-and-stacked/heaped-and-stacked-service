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
    /**
     * One page of the questions Coach's Corner lists, newest first: the user's own, and for staff
     * every question in the gym's pool (on a featured concept), and on a filtered concept the user
     * curates every question on it; or, with verified (false or true), the submitted questions
     * still to verify, or verified, on the concepts the user curates. Each filter is optional
     * (null): verified, a concept the question is on (main or not), and part of the title, any case.
     */
    List<Question> findCurated(
            Long userId, boolean staff, String conceptTag, String titleSearch, int limit, long offset);
    /** How many questions match the same filters as findCurated, across every page. */
    long countCurated(Long userId, boolean staff, String conceptTag, String titleSearch);
    /**
     * One page of the links of submitted questions waiting for approval on the user's own concepts,
     * and for staff on the gym's pool too. Oldest question first.
     */
    List<ConceptLink> findLinksAwaitingReview(Long userId, boolean staff, int limit, long offset);
    /** How many links findLinksAwaitingReview finds across every page. */
    long countLinksAwaitingReview(Long userId, boolean staff);
    void updateLinkStatus(Long questionId, String conceptTag, ConceptLinkStatus status, Long decidedBy);
    /** Verifies the question (verifiedBy), or takes the verification back (null). */
    void updateVerification(Long questionId, Long verifiedBy);
    /**
     * Whether the user may verify the question: staff when it's on any featured concept; otherwise
     * the owners of the concepts it's on (declined links don't count).
     */
    boolean canVerify(Long questionId, Long userId, boolean staff);
    /** Whether any concept accepted the question. */
    boolean isAcceptedAnywhere(Long questionId);
    /**
     * The question, if learners can answer it: submitted, and accepted for at least one active
     * concept. Its concept tags are only those concepts.
     */
    Optional<Question> findAvailableForLearningById(Long id);
    /**
     * One question learners can answer (as in findAvailableForLearningById), leaving out the
     * skipped ones and, with concept tags, those accepted for none of them. Questions the learner
     * never answered come first, in random order; then answered ones at random, weighted towards
     * those answered longest ago. From the pool to verify: oldest first. staff: whether the
     * learner is staff, for who may verify what.
     */
    Optional<Question> findNextForLearning(
            Long userId, boolean staff, List<String> conceptTags, List<Long> skippedIds, QuestionPool pool);
    /**
     * How many questions learners can answer (as in findNextForLearning, without skipping any)
     * this learner has never answered; from the pool to verify, how many are left, answered or not.
     */
    int countUnansweredForLearning(Long userId, boolean staff, List<String> conceptTags, QuestionPool pool);

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
    /**
     * One page of the open reports on questions the user wrote or that are linked to a concept they
     * own; on every question for staff. Oldest first.
     */
    List<Report> findOpenReports(Long userId, boolean staff, int limit, long offset);
    long countOpenReports(Long userId, boolean staff);
    /** The same for decided (resolved or dismissed) reports; newest decision first. */
    List<Report> findDecidedReports(Long userId, boolean staff, int limit, long offset);
    long countDecidedReports(Long userId, boolean staff);
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