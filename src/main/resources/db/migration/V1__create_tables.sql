-- The database is the last line of defence: every rule the service validates is
-- mirrored here as a constraint, so bad data can't sneak in through a bug, a
-- manual SQL session or a future second writer. The service still validates first
-- so callers get a readable 400 instead of a constraint-violation 500.

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE concept (
    id BIGSERIAL PRIMARY KEY,
    tag VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    external_references JSONB NOT NULL DEFAULT '[]'::jsonb,
    concept_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_concept_tag
        UNIQUE (tag),

    -- Same lifecycle as questions: members propose (DRAFT), admins publish or retire.
    CONSTRAINT chk_concept_status
        CHECK (concept_status IN ('DRAFT', 'ACTIVE', 'RETIRED')),

    CONSTRAINT chk_concept_tag_kebab_case
        CHECK (tag ~ '^[a-z0-9]+(-[a-z0-9]+)*$'),

    CONSTRAINT chk_concept_name_not_blank
        CHECK (btrim(name) <> ''),

    CONSTRAINT chk_concept_description_not_blank
        CHECK (btrim(description) <> ''),

    CONSTRAINT chk_concept_external_references_array
        CHECK (jsonb_typeof(external_references) = 'array'),

    CONSTRAINT chk_concept_created_by_not_blank
        CHECK (btrim(created_by) <> ''),

    CONSTRAINT chk_concept_updated_by_not_blank
        CHECK (btrim(updated_by) <> '')
);

CREATE TRIGGER trg_concept_set_updated_at
    BEFORE UPDATE ON concept
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- Backs the concept review queue and the learner's topic list (ACTIVE only).
CREATE INDEX idx_concept_status
    ON concept (concept_status);

CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    requirement TEXT NOT NULL,
    sample_solution TEXT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    difficulty_score INT NOT NULL,
    expected_duration_minutes INT NOT NULL,
    question_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    source VARCHAR(200),

    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_question_requirement_not_blank
        CHECK (btrim(requirement) <> ''),

    CONSTRAINT chk_question_sample_solution_not_blank
        CHECK (btrim(sample_solution) <> ''),

    CONSTRAINT chk_question_type
        CHECK (question_type IN (
            'DEFINITION',
            'GOTCHA',
            'CODE_ANALYSIS',
            'BUG_HUNT',
            'IMPLEMENTATION',
            'DESIGN'
        )),

    CONSTRAINT chk_question_status
        CHECK (question_status IN ('DRAFT', 'ACTIVE', 'RETIRED')),

    CONSTRAINT chk_question_difficulty_score_range
        CHECK (difficulty_score BETWEEN 1 AND 10),

    CONSTRAINT chk_question_expected_duration_positive
        CHECK (expected_duration_minutes > 0),

    CONSTRAINT chk_question_created_by_not_blank
        CHECK (btrim(created_by) <> ''),

    CONSTRAINT chk_question_updated_by_not_blank
        CHECK (btrim(updated_by) <> '')
);

CREATE TRIGGER trg_question_set_updated_at
    BEFORE UPDATE ON question
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- Backs GET /content/questions/mine.
CREATE INDEX idx_question_created_by
    ON question (created_by);

-- Backs GET /content/questions/review (filtered by status, DRAFT by default).
CREATE INDEX idx_question_status
    ON question (question_status);

CREATE TABLE question_concept (
    question_id BIGINT NOT NULL,
    concept_tag VARCHAR(100) NOT NULL,

    CONSTRAINT pk_question_concept
        PRIMARY KEY (question_id, concept_tag),

    -- Deleting a question removes its links.
    CONSTRAINT fk_question_concept_question
        FOREIGN KEY (question_id)
        REFERENCES question (id)
        ON DELETE CASCADE,

    -- A concept still used by a question cannot be deleted. The service checks this
    -- first to answer 409; RESTRICT guarantees it even if that check is bypassed.
    CONSTRAINT fk_question_concept_concept
        FOREIGN KEY (concept_tag)
        REFERENCES concept (tag)
        ON DELETE RESTRICT
);

CREATE INDEX idx_question_concept_concept_tag
    ON question_concept (concept_tag);
