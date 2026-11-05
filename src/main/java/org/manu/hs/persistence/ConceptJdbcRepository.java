package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptRepository;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ConceptWorkload;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConceptJdbcRepository implements ConceptRepository {

    // The house account: the user named "Heaped & Stacked" with no sign-in linked (the one
    // UserService.requireNotHouseAccount spares). Staff curate its concepts, featured or not.
    private static final String HOUSE_ACCOUNT = """
            (SELECT u.id FROM app_user u
             WHERE u.display_name = 'Heaped & Stacked'
               AND NOT EXISTS (SELECT 1 FROM user_identity i WHERE i.user_id = u.id))
            """;

    // Whether the concept is house-owned: owned by the house account, the user named
    // "Heaped & Stacked" with no sign-in linked (the one UserService.requireNotHouseAccount spares).
    private static final String HOUSE_OWNED = """
            EXISTS (SELECT 1 FROM app_user u
                    WHERE u.id = concept.created_by AND u.display_name = 'Heaped & Stacked'
                      AND NOT EXISTS (SELECT 1 FROM user_identity i WHERE i.user_id = u.id)) AS house_owned
            """;

    private static final String FIND_BY_TAG = """
            SELECT tag, name, description, external_references, concept_status, contribution_policy, featured,
                   created_by, updated_by, created_at, updated_at, """ + HOUSE_OWNED + """
            FROM concept
            WHERE tag = :tag
            """;

    private static final String FIND_ALL = """
            SELECT tag, name, description, external_references, concept_status, contribution_policy, featured,
                   created_by, updated_by, created_at, updated_at, """ + HOUSE_OWNED + """
            FROM concept
            ORDER BY tag
            """;

    private static final String FIND_BY_TAGS = """
            SELECT tag, name, description, external_references, concept_status, contribution_policy, featured,
                   created_by, updated_by, created_at, updated_at, """ + HOUSE_OWNED + """
            FROM concept
            WHERE tag IN (:tags)
            ORDER BY tag
            """;

    private static final String INSERT = """
            INSERT INTO concept (tag, name, description, external_references, concept_status,
                                 contribution_policy, created_by, updated_by)
            VALUES (:tag, :name, :description, CAST(:external_references AS jsonb), :concept_status,
                    :contribution_policy, :created_by, :created_by)
            RETURNING tag
            """;

    private static final String UPDATE = """
            UPDATE concept
            SET name = :name,
                description = :description,
                external_references = CAST(:external_references AS jsonb),
                contribution_policy = :contribution_policy,
                updated_by = :updated_by
            WHERE tag = :tag
            """;

    private static final String UPDATE_STATUS = """
            UPDATE concept
            SET concept_status = :status,
                updated_by = :updated_by
            WHERE tag = :tag
            """;

    private static final String UPDATE_FEATURED = """
            UPDATE concept
            SET featured = :featured,
                updated_by = :updated_by
            WHERE tag = :tag
            """;

    // Per concept the user curates: live accepted questions, the unverified ones among them, and
    // submitted questions waiting for approval. Concepts without questions count zeros.
    private static final String FIND_WORKLOAD = """
            SELECT c.tag,
                   count(q.id) FILTER (WHERE qc.link_status = 'ACCEPTED') AS questions,
                   count(q.id) FILTER (WHERE qc.link_status = 'ACCEPTED' AND q.verified_at IS NULL) AS to_verify,
                   count(q.id) FILTER (WHERE qc.link_status = 'PENDING') AS waiting
            FROM concept c
            LEFT JOIN question_concept qc ON qc.concept_tag = c.tag
            LEFT JOIN question q ON q.id = qc.question_id AND q.question_status = 'SUBMITTED'
            WHERE c.created_by = :user_id
               OR (:staff AND (c.featured OR c.created_by IN """ + HOUSE_ACCOUNT + """
                   ))
            GROUP BY c.tag
            ORDER BY c.tag
            """;

    private static final String DELETE = """
            DELETE FROM concept
            WHERE tag = :tag
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Concept save(Concept concept, Long createdBy) {
        String tag = jdbcTemplate.queryForObject(
                INSERT,
                insertParameters(concept, createdBy),
                String.class);

        return findByTag(tag)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Concept was inserted but could not be retrieved"));

    }

    @Override
    public boolean existsByTag(String tag) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM concept
                            WHERE tag = :tag
                        )
                        """,
                Map.of("tag", tag),
                Boolean.class);

        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<Concept> findByTag(String tag) {
        List<Concept> concepts = jdbcTemplate.query(
                FIND_BY_TAG,
                Map.of("tag", tag),
                this::mapConcept);

        return concepts.stream().findFirst();
    }

    @Override
    public List<Concept> findAll() {
        return jdbcTemplate.query(
                FIND_ALL,
                this::mapConcept);
    }

    @Override
    public Concept update(Concept concept, Long updatedBy) {
        jdbcTemplate.update(
                UPDATE,
                updateParameters(concept, updatedBy));
        return findByTag(concept.getTag())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Concept was updated but could not be retrieved"));
    }

    @Override
    public List<ConceptWorkload> findWorkload(Long userId, boolean staff) {
        return jdbcTemplate.query(
                FIND_WORKLOAD,
                new MapSqlParameterSource().addValue("user_id", userId).addValue("staff", staff),
                (resultSet, rowNum) -> ConceptWorkload.builder()
                        .tag(resultSet.getString("tag"))
                        .questions(resultSet.getInt("questions"))
                        .toVerify(resultSet.getInt("to_verify"))
                        .waiting(resultSet.getInt("waiting"))
                        .build());
    }

    @Override
    public long countOwnedBy(Long userId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM concept WHERE created_by = :user_id AND concept_status <> 'RETIRED'",
                Map.of("user_id", userId), Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public Concept updateFeatured(String tag, boolean featured, Long updatedBy) {
        jdbcTemplate.update(
                UPDATE_FEATURED,
                new MapSqlParameterSource()
                        .addValue("tag", tag)
                        .addValue("featured", featured)
                        .addValue("updated_by", updatedBy));

        return findByTag(tag)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Concept featured was updated but could not be retrieved"));
    }

    @Override
    public Concept updateStatus(String tag, ConceptStatus status, Long updatedBy) {
        jdbcTemplate.update(
                UPDATE_STATUS,
                new MapSqlParameterSource()
                        .addValue("tag", tag)
                        .addValue("status", status.name())
                        .addValue("updated_by", updatedBy));

        return findByTag(tag)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Concept status was updated but could not be retrieved"));
    }

    @Override
    public void deleteByTag(String tag) {
        jdbcTemplate.update(
                DELETE,
                Map.of("tag", tag));
    }

    @Override
    public boolean isReferencedByAnyQuestion(String tag) {
        Boolean referenced = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM question_concept
                            WHERE concept_tag = :tag
                        )
                        """,
                Map.of("tag", tag),
                Boolean.class);

        return Boolean.TRUE.equals(referenced);
    }

    @Override
    public boolean isReferencedBySubmittedQuestion(String tag) {
        Boolean referenced = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM question_concept qc
                            JOIN question q ON q.id = qc.question_id
                            WHERE qc.concept_tag = :tag
                              AND q.question_status = 'SUBMITTED'
                        )
                        """,
                Map.of("tag", tag),
                Boolean.class);

        return Boolean.TRUE.equals(referenced);
    }

    @Override
    public List<Concept> findByTags(Collection<String> tags) {
        // "IN ()" is invalid SQL, so an empty list is answered without a query.
        if (tags.isEmpty()) {
            return List.of();
        }

        return jdbcTemplate.query(
                FIND_BY_TAGS,
                Map.of("tags", tags),
                this::mapConcept);
    }

    private MapSqlParameterSource insertParameters(Concept concept, Long createdBy) {
        return new MapSqlParameterSource()
                .addValue("tag", concept.getTag())
                .addValue("name", concept.getName())
                .addValue("description", concept.getDescription())
                .addValue("external_references", serializeReferences(concept.getExternalReferences()))
                .addValue("concept_status", concept.getStatus().name())
                .addValue("contribution_policy", concept.getContributionPolicy().name())
                .addValue("created_by", createdBy);
    }

    private MapSqlParameterSource updateParameters(Concept concept, Long updatedBy) {
        return new MapSqlParameterSource()
                .addValue("tag", concept.getTag())
                .addValue("name", concept.getName())
                .addValue("description", concept.getDescription())
                .addValue("external_references", serializeReferences(concept.getExternalReferences()))
                .addValue("contribution_policy", concept.getContributionPolicy().name())
                .addValue("updated_by", updatedBy);
    }

    private String serializeReferences(List<String> references) {
        return objectMapper.writeValueAsString(references);
    }

    private Concept mapConcept(ResultSet resultSet, int rowNumber) throws SQLException {
        return Concept.builder()
                .tag(resultSet.getString("tag"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .externalReferences(deserializeReferences(resultSet.getString("external_references")))
                .status(ConceptStatus.valueOf(resultSet.getString("concept_status")))
                .contributionPolicy(ContributionPolicy.valueOf(resultSet.getString("contribution_policy")))
                .featured(resultSet.getBoolean("featured"))
                .houseOwned(resultSet.getBoolean("house_owned"))
                .createdBy(resultSet.getLong("created_by"))
                .updatedBy(resultSet.getLong("updated_by"))
                .createdAt(resultSet.getTimestamp("created_at").toInstant())
                .updatedAt(resultSet.getTimestamp("updated_at").toInstant())
                .build();
    }

    private List<String> deserializeReferences(String references) {
        return objectMapper.readValue(
                references,
                new TypeReference<>() {
                });

    }
}