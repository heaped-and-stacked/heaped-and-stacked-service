package org.manu.hs.application.content;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.common.exception.RequiredFieldMissingException;
import org.manu.hs.application.common.exception.StaffRequiredException;
import org.manu.hs.application.content.command.SubmitQuestionCommand;
import org.manu.hs.application.content.command.UpdateQuestionCommand;
import org.manu.hs.application.content.exception.QuestionNotAcceptedException;
import org.manu.hs.application.content.exception.QuestionVerificationForbiddenException;
import org.manu.hs.application.content.exception.ConceptLinkNotFoundException;
import org.manu.hs.application.content.exception.QuestionAnsweredException;
import org.manu.hs.application.content.exception.QuestionHasDraftConceptsException;
import org.manu.hs.application.content.exception.QuestionModificationForbiddenException;
import org.manu.hs.application.content.exception.QuestionNotFoundException;
import org.manu.hs.application.content.exception.ReportAlreadyDecidedException;
import org.manu.hs.application.content.exception.ReportNotFoundException;
import org.manu.hs.application.content.exception.ReportResolutionForbiddenException;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.question.ConceptLink;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionRepository;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportStatus;
import org.manu.hs.domain.question.VoteTotals;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.manu.hs.application.common.Validations.requireMaxLength;
import static org.manu.hs.application.common.Validations.requireMaxSize;
import static org.manu.hs.application.common.Validations.requireNoEmptyEntries;
import static org.manu.hs.application.common.Validations.requireNoNulCharacter;
import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireNonNull;
import static org.manu.hs.application.common.Validations.requireSingleLine;

/**
 * Question validation and rules, building questions from commands, and question
 * persistence. Package-private: only ContentApplication uses it.
 * <p>
 * Never reads the current user: the caller's id and decisions based on it (such as
 * the starting status) are passed in, which keeps every method easy to unit test.
 */
@Service
@RequiredArgsConstructor
class QuestionService {

    private static final int MIN_DIFFICULTY_SCORE = 1;
    private static final int MAX_DIFFICULTY_SCORE = 10;
    private static final int MAX_TITLE_LENGTH = 120;
    private static final int MAX_TEXT_LENGTH = 10_000;
    private static final int MAX_SOURCE_LENGTH = 200;
    private static final int MAX_CONCEPT_TAGS = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final QuestionRepository questionRepository;

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SubmitQuestionCommand command) {
        validateFields(
                command.getTitle(),
                command.getRequirement(),
                command.getSampleSolution(),
                command.getDifficultyScore(),
                command.getSource(),
                command.getConceptTags());
    }

    void validate(UpdateQuestionCommand command) {
        validateFields(
                command.getTitle(),
                command.getRequirement(),
                command.getSampleSolution(),
                command.getDifficultyScore(),
                command.getSource(),
                command.getConceptTags());
    }

    private void validateFields(
            String title,
            String requirement,
            String sampleSolution,
            Integer difficultyScore,
            String source,
            List<String> conceptTags) {
        requireNonBlank(title, "title");
        requireMaxLength(title, MAX_TITLE_LENGTH, "title");
        requireSingleLine(title, "title");
        requireNonBlank(requirement, "requirement");
        requireMaxLength(requirement, MAX_TEXT_LENGTH, "requirement");
        requireNoNulCharacter(requirement, "requirement");
        requireNonBlank(sampleSolution, "sampleSolution");
        requireMaxLength(sampleSolution, MAX_TEXT_LENGTH, "sampleSolution");
        requireNoNulCharacter(sampleSolution, "sampleSolution");
        requireMaxLength(source, MAX_SOURCE_LENGTH, "source");
        requireSingleLine(source, "source");
        requireNonNull(difficultyScore, "difficultyScore");
        if (difficultyScore < MIN_DIFFICULTY_SCORE || difficultyScore > MAX_DIFFICULTY_SCORE) {
            throw new InvalidFieldValueException(
                    "difficultyScore",
                    "must be between %d and %d".formatted(MIN_DIFFICULTY_SCORE, MAX_DIFFICULTY_SCORE));
        }
        if (conceptTags == null || conceptTags.isEmpty()) {
            throw new RequiredFieldMissingException("conceptTags");
        }
        requireMaxSize(conceptTags, MAX_CONCEPT_TAGS, "conceptTags");
        requireNoEmptyEntries(conceptTags, "conceptTags");
    }

    /** A review either accepts or declines a question for a concept. */
    void validateReviewDecision(ConceptLinkStatus decision) {
        requireNonNull(decision, "status");
        if (decision == ConceptLinkStatus.PENDING) {
            throw new InvalidFieldValueException("status", "must be ACCEPTED or DECLINED");
        }
    }

    /** A report is either resolved (the question was fixed) or dismissed (nothing to fix). */
    void validateReportDecision(ReportStatus decision) {
        requireNonNull(decision, "status");
        if (decision == ReportStatus.OPEN) {
            throw new InvalidFieldValueException("status", "must be RESOLVED or DISMISSED");
        }
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data
    // ---------------------------------------------------------------------

    Question findByIdOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    /** Pages are zero-based and hold 1 to 100 questions. */
    void validatePage(int page, int size) {
        if (page < 0) {
            throw new InvalidFieldValueException("page", "must be 0 or more");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidFieldValueException("size", "must be between 1 and %d".formatted(MAX_PAGE_SIZE));
        }
    }

    /** One page of the questions a user curates; a null author means every author's, blank filters are ignored. */
    List<Question> findCurated(
            Long userId, boolean staff, String conceptTag, String titleSearch, int page, int size) {
        return questionRepository.findCurated(
                userId, staff, filterOrNull(conceptTag), filterOrNull(titleSearch), size, (long) page * size);
    }

    long countCurated(Long userId, boolean staff, String conceptTag, String titleSearch) {
        return questionRepository.countCurated(
                userId, staff, filterOrNull(conceptTag), filterOrNull(titleSearch));
    }

    private static String filterOrNull(String filter) {
        return filter == null || filter.isBlank() ? null : filter.strip();
    }

    /** What waits for a decision: on the caller's own concepts, and on featured ones for staff. */
    List<ConceptLink> findLinksAwaitingReview(Long userId, boolean staff, int page, int size) {
        return questionRepository.findLinksAwaitingReview(userId, staff, size, (long) page * size);
    }

    long countLinksAwaitingReview(Long userId, boolean staff) {
        return questionRepository.countLinksAwaitingReview(userId, staff);
    }

    /** Open reports the caller can decide (on every question for staff), or the decided ones. */
    List<Report> findReports(Long userId, boolean staff, boolean decided, int page, int size) {
        final long offset = (long) page * size;
        return decided
                ? questionRepository.findDecidedReports(userId, staff, size, offset)
                : questionRepository.findOpenReports(userId, staff, size, offset);
    }

    long countReports(Long userId, boolean staff, boolean decided) {
        return decided
                ? questionRepository.countDecidedReports(userId, staff)
                : questionRepository.countOpenReports(userId, staff);
    }

    List<Report> findReportsByQuestion(Long questionId) {
        return questionRepository.findReportsByQuestion(questionId);
    }

    Report findReportOrThrow(Long id) {
        return questionRepository.findReportById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    /** Vote totals per question id; questions nobody voted on are left out. */
    Map<Long, VoteTotals> countVotes(List<Long> questionIds) {
        return questionRepository.countVotes(questionIds);
    }

    /**
     * Whoever can fix or vouch for the question decides its reports: its author, the owner of
     * any concept it is linked to (the question's concepts are passed in), or staff.
     */
    void requireCanDecideReport(Report report, List<Concept> concepts, Long userId, boolean staff) {
        final boolean curatesALinkedConcept = concepts.stream()
                .anyMatch(concept -> concept.isCuratedBy(userId, staff));

        if (!staff && !report.getQuestion().getCreatedBy().equals(userId) && !curatesALinkedConcept) {
            throw new ReportResolutionForbiddenException(report.getId());
        }
    }

    void requireLinkedTo(Question question, String conceptTag) {
        if (!question.getConceptTags().contains(conceptTag)) {
            throw new ConceptLinkNotFoundException(question.getId(), conceptTag);
        }
    }

    /**
     * The author may edit their question in any status (typos, better phrasing), as may staff.
     */
    void requireCanBeModifiedBy(Question question, Long userId, boolean staff) {
        if (!staff && !question.getCreatedBy().equals(userId)) {
            throw new QuestionModificationForbiddenException(question.getId());
        }
    }

    /**
     * Only staff delete questions (and only unanswered ones, see requireNotAnswered); authors ask staff.
     */
    void requireCanBeDeletedBy(Question question, Long userId, boolean staff) {
        requireCanBeModifiedBy(question, userId, staff);

        if (!staff) {
            throw new StaffRequiredException("delete a question");
        }
    }

    /** Answered questions are retired, not deleted, so learners keep their history. */
    void requireNotAnswered(Long id) {
        if (questionRepository.isReferencedByAnyAnswer(id)) {
            throw new QuestionAnsweredException(id);
        }
    }

    /**
     * A SUBMITTED question may not link a concept that is still a DRAFT.
     */
    void requireNoDraftConcepts(List<Concept> concepts) {
        final List<String> draftTags = concepts.stream()
                .filter(concept -> concept.getStatus() == ConceptStatus.DRAFT)
                .map(Concept::getTag)
                .toList();

        if (!draftTags.isEmpty()) {
            throw new QuestionHasDraftConceptsException(draftTags);
        }
    }

    /**
     * The concepts a question by this author has to wait on: those whose owner approves
     * contributions, unless the author curates the concept (its owner, or staff), who would
     * only approve it themselves. Pure: no repository access.
     */
    List<String> tagsAwaitingApproval(List<Concept> concepts, Long authorId, boolean staff) {
        return concepts.stream()
                .filter(concept -> concept.getContributionPolicy() == ContributionPolicy.OWNER_APPROVAL)
                .filter(concept -> !concept.isCuratedBy(authorId, staff))
                .map(Concept::getTag)
                .toList();
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    Question create(SubmitQuestionCommand command, QuestionStatus status, Long userId, List<String> tagsAwaitingApproval) {
        return questionRepository.insert(newQuestion(command, status, userId), tagsAwaitingApproval);
    }

    Question update(Question existing, UpdateQuestionCommand command, Long userId, List<String> tagsAwaitingApproval) {
        return questionRepository.update(
                existing.getId(),
                updatedQuestion(existing, command, userId),
                tagsAwaitingApproval);
    }

    Question updateStatus(Long id, QuestionStatus status, Long updatedBy) {
        return questionRepository.updateStatus(id, status, updatedBy);
    }

    /** Records the concept owner's (or staff's) decision on a question for that concept. */
    void decide(Long questionId, String conceptTag, ConceptLinkStatus decision, Long decidedBy) {
        questionRepository.updateLinkStatus(questionId, conceptTag, decision, decidedBy);
    }

    /** Verifies the question, or takes it back. */
    void verify(Long questionId, boolean verified, Long userId) {
        questionRepository.updateVerification(questionId, verified ? userId : null);
    }

    /** Whether the user curates the question: staff on a featured concept, otherwise its concepts' owners. */
    boolean canVerify(Long questionId, Long userId, boolean staff) {
        return userId != null && questionRepository.canVerify(questionId, userId, staff);
    }

    void requireCanVerify(Long questionId, Long userId, boolean staff) {
        if (!canVerify(questionId, userId, staff)) {
            throw new QuestionVerificationForbiddenException(questionId);
        }
    }

    /** A question is verified once some concept accepted it. */
    void requireAcceptedAnywhere(Long questionId) {
        if (!questionRepository.isAcceptedAnywhere(questionId)) {
            throw new QuestionNotAcceptedException(questionId);
        }
    }

    void delete(Long id) {
        questionRepository.deleteById(id);
    }

    /** Decides an open report; one that was already decided (maybe a moment ago) stays as it was. */
    Report decideReport(Long id, ReportStatus decision, Long decidedBy) {
        return questionRepository.decideOpenReport(id, decision, decidedBy)
                .orElseThrow(() -> new ReportAlreadyDecidedException(id));
    }

    /** Builds the question to store from the command. Pure: no repository access. */
    Question newQuestion(SubmitQuestionCommand command, QuestionStatus status, Long userId) {
        return Question.builder()
                .conceptTags(withoutDuplicates(command.getConceptTags()))
                .title(command.getTitle())
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .difficultyScore(command.getDifficultyScore())
                .status(status)
                .source(command.getSource())
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    /**
     * Builds the edited question: content comes from the command; id, status and author
     * are kept from the stored question. Pure: no repository access.
     */
    Question updatedQuestion(Question existing, UpdateQuestionCommand command, Long userId) {
        return existing.toBuilder()
                .conceptTags(withoutDuplicates(command.getConceptTags()))
                .title(command.getTitle())
                .requirement(command.getRequirement())
                .sampleSolution(command.getSampleSolution())
                .difficultyScore(command.getDifficultyScore())
                .source(command.getSource())
                .updatedBy(userId)
                .build();
    }

    /** Duplicate tags are dropped silently, keeping the first occurrence and the order. */
    private List<String> withoutDuplicates(List<String> conceptTags) {
        return conceptTags.stream().distinct().toList();
    }
}
