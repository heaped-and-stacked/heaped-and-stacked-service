package org.manu.hs.domain.question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    Question insert(Question question);
    Question update(Long id, Question question);
    Question updateStatus(Long id, QuestionStatus status, Long updatedBy);
    void deleteById(Long id);
    Optional<Question> findById(Long id);
    List<Question> findBySubmittedBy(Long userId);
    List<Question> findByStatus(QuestionStatus status);
}