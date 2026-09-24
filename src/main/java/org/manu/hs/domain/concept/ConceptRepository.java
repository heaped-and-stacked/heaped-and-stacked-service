package org.manu.hs.domain.concept;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConceptRepository {
    Concept save(Concept concept, Long createdBy);
    Concept update(Concept concept, Long updatedBy);
    Concept updateStatus(String tag, ConceptStatus status, Long updatedBy);
    boolean existsByTag(String tag);
    Optional<Concept> findByTag(String tag);
    void deleteByTag(String tag);
    List<Concept> findAll();
    /** The concepts in the given status, oldest first. */
    List<Concept> findByStatus(ConceptStatus status);
    boolean isReferencedByAnyQuestion(String tag);
    boolean isReferencedByActiveQuestion(String tag);
    /** The concepts among the given tags that exist; unknown tags are simply absent. */
    List<Concept> findByTags(Collection<String> tags);
}