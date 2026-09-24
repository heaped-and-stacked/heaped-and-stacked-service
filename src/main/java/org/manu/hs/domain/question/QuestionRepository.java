package org.manu.hs.domain.question;

import java.util.List;
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
}