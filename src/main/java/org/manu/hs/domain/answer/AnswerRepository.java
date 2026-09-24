package org.manu.hs.domain.answer;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository {
    Answer insert(Answer answer);
    Optional<Answer> findById(Long id);
    /** The learner's answers, newest first. */
    List<Answer> findByUser(Long userId);
    /** The learner's answers to one question, newest first: their past attempts at it. */
    List<Answer> findByUserAndQuestion(Long userId, Long questionId);
    Answer updateAssessment(Long id, Integer selfGrade, Integer perceivedDifficulty);
}
