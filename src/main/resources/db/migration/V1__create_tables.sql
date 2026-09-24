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

-- ---------------------------------------------------------------------
-- Users
-- ---------------------------------------------------------------------

-- Who the person is. How they sign in lives in user_identity, so new login
-- providers can be added without changing this table. ("user" is a reserved word.)
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    display_name VARCHAR(200),
    avatar_url TEXT,
    user_role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    user_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT chk_app_user_role
        CHECK (user_role IN ('MEMBER', 'STAFF', 'ADMIN')),

    CONSTRAINT chk_app_user_status
        CHECK (user_status IN ('ACTIVE', 'BLOCKED', 'DELETED')),

    -- Soft delete: a DELETED user always has a deletion time, and only they do.
    CONSTRAINT chk_app_user_deleted_at
        CHECK ((user_status = 'DELETED') = (deleted_at IS NOT NULL))
);

CREATE TRIGGER trg_app_user_set_updated_at
    BEFORE UPDATE ON app_user
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- Backs the admin user list filtered by role or status.
CREATE INDEX idx_app_user_role
    ON app_user (user_role);

CREATE INDEX idx_app_user_status
    ON app_user (user_status);

-- How a person signs in: one row per login provider linked to the user.
CREATE TABLE user_identity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(30) NOT NULL,
    -- The provider's stable id for the account (GitHub: numeric id as text; Google: "sub").
    provider_user_id VARCHAR(200) NOT NULL,
    -- The provider's username; it can change, so it is refreshed on every login.
    provider_login VARCHAR(200),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at TIMESTAMPTZ,

    CONSTRAINT uk_user_identity_provider_account
        UNIQUE (provider, provider_user_id),

    CONSTRAINT chk_user_identity_provider
        CHECK (provider IN ('GITHUB')),

    CONSTRAINT chk_user_identity_provider_user_id_not_blank
        CHECK (btrim(provider_user_id) <> ''),

    -- Deleting an account removes its identities (soft delete keeps the app_user row).
    CONSTRAINT fk_user_identity_user
        FOREIGN KEY (user_id)
        REFERENCES app_user (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_identity_user_id
    ON user_identity (user_id);

-- Who may sign in while the environment is invite-only: provider usernames, matched in any case.
-- No rows: anyone may sign in. Bootstrap admins always may.
CREATE TABLE sign_in_invite (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(30) NOT NULL,
    provider_login VARCHAR(200) NOT NULL,
    -- Who added it; kept when that account is deleted.
    invited_by BIGINT,
    invited_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_sign_in_invite_provider
        CHECK (provider IN ('GITHUB')),

    CONSTRAINT chk_sign_in_invite_provider_login_not_blank
        CHECK (btrim(provider_login) <> ''),

    CONSTRAINT fk_sign_in_invite_invited_by
        FOREIGN KEY (invited_by)
        REFERENCES app_user (id)
        ON DELETE SET NULL
);

-- One invite per username, whatever its case.
CREATE UNIQUE INDEX uk_sign_in_invite_provider_login
    ON sign_in_invite (provider, lower(provider_login));

-- ---------------------------------------------------------------------
-- Content
-- ---------------------------------------------------------------------

CREATE TABLE concept (
    id BIGSERIAL PRIMARY KEY,
    tag VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    external_references JSONB NOT NULL DEFAULT '[]'::jsonb,
    concept_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    contribution_policy VARCHAR(20) NOT NULL DEFAULT 'AUTO_ACCEPT',
    -- Featured: trained when no concept is picked, curated by staff; otherwise a user's own
    -- concept, curated by its owner alone.
    featured BOOLEAN NOT NULL DEFAULT FALSE,

    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_concept_tag
        UNIQUE (tag),

    -- Owned by its author (created_by), who publishes it; admins may also unpublish or retire.
    CONSTRAINT chk_concept_status
        CHECK (concept_status IN ('DRAFT', 'ACTIVE', 'RETIRED')),

    -- Whether other users' questions are accepted for this concept straight away, or wait
    -- for the owner's approval.
    CONSTRAINT chk_concept_contribution_policy
        CHECK (contribution_policy IN ('AUTO_ACCEPT', 'OWNER_APPROVAL')),

    CONSTRAINT chk_concept_tag_kebab_case
        CHECK (tag ~ '^[a-z0-9]+(-[a-z0-9]+)*$'),

    CONSTRAINT chk_concept_name_not_blank
        CHECK (btrim(name) <> ''),

    CONSTRAINT chk_concept_description_not_blank
        CHECK (btrim(description) <> ''),

    CONSTRAINT chk_concept_description_length
        CHECK (char_length(description) <= 2000),

    -- Only a published concept is featured: unfeature it before unpublishing or retiring.
    CONSTRAINT chk_concept_featured_active
        CHECK (NOT featured OR concept_status = 'ACTIVE'),

    CONSTRAINT chk_concept_external_references_array
        CHECK (jsonb_typeof(external_references) = 'array'),

    CONSTRAINT chk_concept_external_references_count
        CHECK (jsonb_array_length(external_references) <= 20),

    -- Users are only soft deleted, so these references always stay valid.
    CONSTRAINT fk_concept_created_by
        FOREIGN KEY (created_by)
        REFERENCES app_user (id),

    CONSTRAINT fk_concept_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES app_user (id)
);

CREATE TRIGGER trg_concept_set_updated_at
    BEFORE UPDATE ON concept
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- Backs the learner's topic list (ACTIVE only).
CREATE INDEX idx_concept_status
    ON concept (concept_status);

CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    -- A short name for the question, shown above it and in lists.
    title VARCHAR(120) NOT NULL,
    requirement TEXT NOT NULL,
    sample_solution TEXT NOT NULL,
    difficulty_score INT NOT NULL,
    question_status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    source VARCHAR(200),

    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- Verified: checked by whoever curates the question (a clear question, a correct answer),
    -- whatever concepts it's on. Learners can choose to train on verified questions only.
    verified_by BIGINT,
    verified_at TIMESTAMPTZ,

    -- Verified by someone at some time, or not at all.
    CONSTRAINT chk_question_verified
        CHECK ((verified_by IS NULL) = (verified_at IS NULL)),

    CONSTRAINT chk_question_title_not_blank
        CHECK (btrim(title) <> ''),

    CONSTRAINT chk_question_requirement_not_blank
        CHECK (btrim(requirement) <> ''),

    CONSTRAINT chk_question_sample_solution_not_blank
        CHECK (btrim(sample_solution) <> ''),

    CONSTRAINT chk_question_requirement_length
        CHECK (char_length(requirement) <= 10000),

    CONSTRAINT chk_question_sample_solution_length
        CHECK (char_length(sample_solution) <= 10000),

    CONSTRAINT chk_question_status
        CHECK (question_status IN ('SUBMITTED', 'RETIRED')),

    CONSTRAINT chk_question_difficulty_score_range
        CHECK (difficulty_score BETWEEN 1 AND 10),

    -- Users are only soft deleted, so these references always stay valid.
    CONSTRAINT fk_question_created_by
        FOREIGN KEY (created_by)
        REFERENCES app_user (id),

    CONSTRAINT fk_question_verified_by
        FOREIGN KEY (verified_by)
        REFERENCES app_user (id),

    CONSTRAINT fk_question_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES app_user (id)
);

CREATE TRIGGER trg_question_set_updated_at
    BEFORE UPDATE ON question
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- Backs the list of the caller's own questions (GET /content/questions).
CREATE INDEX idx_question_created_by
    ON question (created_by);

-- Backs queries by status, such as the SUBMITTED questions shown to learners.
CREATE INDEX idx_question_status
    ON question (question_status);

CREATE TABLE question_concept (
    question_id BIGINT NOT NULL,
    concept_tag VARCHAR(100) NOT NULL,
    -- Whether the question is accepted for this concept; decided per concept, by its owner
    -- when the concept's contribution policy asks for approval.
    link_status VARCHAR(20) NOT NULL,
    decided_by BIGINT,
    decided_at TIMESTAMPTZ,

    CONSTRAINT pk_question_concept
        PRIMARY KEY (question_id, concept_tag),

    CONSTRAINT chk_question_concept_link_status
        CHECK (link_status IN ('PENDING', 'ACCEPTED', 'DECLINED')),

    -- A link still waiting for its owner has no decision yet.
    CONSTRAINT chk_question_concept_pending_undecided
        CHECK (link_status <> 'PENDING' OR (decided_by IS NULL AND decided_at IS NULL)),

    CONSTRAINT fk_question_concept_decided_by
        FOREIGN KEY (decided_by)
        REFERENCES app_user (id),

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

-- Also backs the review list: the questions waiting on a concept.
CREATE INDEX idx_question_concept_concept_tag
    ON question_concept (concept_tag, link_status);

-- ---------------------------------------------------------------------
-- Learning
-- ---------------------------------------------------------------------

CREATE TABLE answer (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT NOT NULL,
    -- The question exactly as it was answered: later edits must not change what the learner saw.
    question_snapshot JSONB NOT NULL,
    -- The learner's optional self-assessment, both on a 1 to 10 scale.
    self_grade SMALLINT,
    perceived_difficulty SMALLINT,
    answered_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_answer_text_not_blank
        CHECK (btrim(answer_text) <> ''),

    CONSTRAINT chk_answer_text_length
        CHECK (char_length(answer_text) <= 10000),

    CONSTRAINT chk_answer_self_grade_range
        CHECK (self_grade BETWEEN 1 AND 10),

    CONSTRAINT chk_answer_perceived_difficulty_range
        CHECK (perceived_difficulty BETWEEN 1 AND 10),

    -- Users are only soft deleted, so this reference always stays valid.
    CONSTRAINT fk_answer_user
        FOREIGN KEY (user_id)
        REFERENCES app_user (id),

    -- An answered question cannot be deleted, so learners keep their history: it is retired
    -- instead. The service checks this first to answer 409; RESTRICT guarantees it.
    CONSTRAINT fk_answer_question
        FOREIGN KEY (question_id)
        REFERENCES question (id)
        ON DELETE RESTRICT
);

-- Backs GET /learning/answers: a learner's answers, newest first.
CREATE INDEX idx_answer_user_answered_at
    ON answer (user_id, answered_at DESC);

-- Backs picking the next question: what this learner answered, how often and when last.
CREATE INDEX idx_answer_user_question
    ON answer (user_id, question_id, answered_at DESC);

-- Backs the "has answers" check before deleting a question (the index above starts with user_id).
CREATE INDEX idx_answer_question_id
    ON answer (question_id);

-- ---------------------------------------------------------------------
-- Feedback: learners report questions that are wrong or unsuitable, and vote on them
-- ---------------------------------------------------------------------

-- A learner's feedback on a question: praise, or a problem (wrong, unclear, a duplicate,
-- inappropriate). The question's author, the owner of one of its concepts, or an admin
-- resolves or dismisses it.
-- While a question has 3 or more open problem reports, it isn't served to learners; praise
-- never holds it back.
CREATE TABLE question_report (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    reason VARCHAR(20) NOT NULL,
    -- What's wrong, in the learner's words, or a suggested fix. Optional.
    note VARCHAR(2000),
    report_status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    resolved_by BIGINT,
    resolved_at TIMESTAMPTZ,
    reported_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_question_report_reason
        CHECK (reason IN ('PRAISE', 'WRONG', 'UNCLEAR', 'DUPLICATE', 'INAPPROPRIATE', 'OTHER')),

    CONSTRAINT chk_question_report_status
        CHECK (report_status IN ('OPEN', 'RESOLVED', 'DISMISSED')),

    -- An open report has no decision yet; a closed one always says who closed it and when.
    CONSTRAINT chk_question_report_open_undecided
        CHECK ((report_status = 'OPEN') = (resolved_by IS NULL)
           AND (report_status = 'OPEN') = (resolved_at IS NULL)),

    -- Deleting a question removes its reports.
    CONSTRAINT fk_question_report_question
        FOREIGN KEY (question_id)
        REFERENCES question (id)
        ON DELETE CASCADE,

    -- Users are only soft deleted, so these references always stay valid.
    CONSTRAINT fk_question_report_reported_by
        FOREIGN KEY (reported_by)
        REFERENCES app_user (id),

    CONSTRAINT fk_question_report_resolved_by
        FOREIGN KEY (resolved_by)
        REFERENCES app_user (id)
);

-- One open report per learner and question: a learner can't push a question out of rotation
-- on their own. Once it's closed, they may report it again.
CREATE UNIQUE INDEX uk_question_report_open_per_reporter
    ON question_report (question_id, reported_by)
    WHERE report_status = 'OPEN';

-- Backs counting a question's open reports when picking questions to serve.
CREATE INDEX idx_question_report_question_status
    ON question_report (question_id, report_status);

-- A learner's thumbs up or down on a question: one per learner and question, changeable.
-- Shown to the question's author. Ups count as likes, which make a question come up a little
-- more often when picking the next one (QuestionJdbcRepository, LIKES).
CREATE TABLE question_vote (
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    vote VARCHAR(10) NOT NULL,
    voted_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_question_vote
        PRIMARY KEY (question_id, user_id),

    CONSTRAINT chk_question_vote_vote
        CHECK (vote IN ('UP', 'DOWN')),

    -- Deleting a question removes its votes.
    CONSTRAINT fk_question_vote_question
        FOREIGN KEY (question_id)
        REFERENCES question (id)
        ON DELETE CASCADE,

    -- Users are only soft deleted, so this reference always stays valid.
    CONSTRAINT fk_question_vote_user
        FOREIGN KEY (user_id)
        REFERENCES app_user (id)
);

-- ---------------------------------------------------------------------
-- HTTP sessions (Spring Session JDBC)
-- ---------------------------------------------------------------------

-- Logins are kept in the database so they survive restarts and deploys.
-- Copied as-is from spring-session-jdbc 4.1.0 (org/springframework/session/jdbc/
-- schema-postgresql.sql); Flyway creates them, so Spring's own schema init stays off.
CREATE TABLE SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BYTEA NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID)
        REFERENCES SPRING_SESSION (PRIMARY_ID) ON DELETE CASCADE
);
