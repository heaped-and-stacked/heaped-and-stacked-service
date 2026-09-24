package org.manu.hs.domain.concept;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//public interface ConceptRepository {
//    Concept save(Concept concept);
//    boolean existsByTag(String tag);
//    Optional<Concept> findByTag(String tag);
//    List<Concept> findAll();
//    Concept update(Concept concept);
//    void deleteByTag(String tag);
//}
public interface ConceptRepository {
    Concept save(Concept concept, String createdBy);
    Concept update(Concept concept, String updatedBy);
    boolean existsByTag(String tag);
    Optional<Concept> findByTag(String tag);
    void deleteByTag(String tag);
    List<Concept> findAll();
    boolean isReferencedByAnyQuestion(String tag);
    /** The concepts among the given tags that exist; unknown tags are simply absent. */
    List<Concept> findByTags(Collection<String> tags);
}