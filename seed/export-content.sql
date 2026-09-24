-- Exports an environment's content as the seed a new environment starts from (initial-content.sql):
--   psql -At -v ON_ERROR_STOP=1 -f export-content.sql > initial-content.sql
-- One query, read-only: it prints the seed's lines and changes nothing.
--
-- What travels: the gym's muscles (featured, published) and their live exercises, in the order
-- they were added, each with its accepted links to those muscles; whether it's verified, and when.
-- In the new environment the house account owns all of it and is its verifier: the people who
-- wrote and checked it don't exist there.
-- What stays behind: everything personal (accounts, sign-ins, sets, votes, feedback, sessions),
-- community muscles, benched exercises, and links still waiting for a decision or declined.
WITH gym AS (
    SELECT tag, name, description, external_references, contribution_policy
    FROM concept
    WHERE featured AND concept_status = 'ACTIVE'
),
live AS (
    SELECT q.id, q.title, q.requirement, q.sample_solution, q.difficulty_score, q.source, q.verified_at,
           array_agg(qc.concept_tag::text ORDER BY qc.concept_tag) AS tags
    FROM question q
    JOIN question_concept qc ON qc.question_id = q.id AND qc.link_status = 'ACCEPTED'
    JOIN gym ON gym.tag = qc.concept_tag
    WHERE q.question_status = 'SUBMITTED'
    GROUP BY q.id
),
lines (part, ord, line) AS (
    SELECT 0, 0::bigint, $header$-- The one-time seed of a new environment, exported from another one's database by
-- seed/export-content.sql (don't edit by hand). Run once, after the schema migrations, on an
-- empty database:
--   psql -v ON_ERROR_STOP=1 -f initial-content.sql
-- It refuses a database that already has content, and loads everything or nothing.
BEGIN;
DO $guard$
BEGIN
    IF EXISTS (SELECT 1 FROM concept) OR EXISTS (SELECT 1 FROM app_user WHERE display_name = 'Heaped & Stacked') THEN
        RAISE EXCEPTION 'This database already has content: the seed is only for a new, empty one';
    END IF;
END
$guard$;

-- Owns the gym's muscles and exercises, and is their verifier. Nobody can sign in as it: it has
-- no GitHub identity. Staff curate its content like anyone else's.
INSERT INTO app_user (display_name, user_role, user_status) VALUES ('Heaped & Stacked', 'STAFF', 'ACTIVE');
CREATE TEMPORARY TABLE house ON COMMIT DROP AS
    SELECT id FROM app_user WHERE display_name = 'Heaped & Stacked';
$header$
    UNION ALL
    SELECT 0, 1, format('-- %s muscles, %s exercises (%s verified).',
                        (SELECT count(*) FROM gym), (SELECT count(*) FROM live),
                        (SELECT count(*) FROM live WHERE verified_at IS NOT NULL))
    UNION ALL
    SELECT 1, row_number() OVER (ORDER BY tag), format(
        'INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, featured, created_by, updated_by) '
            || 'SELECT %L, %L, %L, %L::jsonb, ''ACTIVE'', %L, TRUE, id, id FROM house;',
        tag, name, description, external_references::text, contribution_policy)
    FROM gym
    UNION ALL
    SELECT 2, id, format($insert$WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by, verified_by, verified_at)
    SELECT %L, %L, %L, %s, 'SUBMITTED', %L, house.id, house.id, %s, %L
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(%L::text[]) AS tag;$insert$,
        title, requirement, sample_solution, difficulty_score, source,
        CASE WHEN verified_at IS NULL THEN 'NULL' ELSE 'house.id' END, verified_at, tags)
    FROM live
    UNION ALL
    SELECT 3, 0, 'COMMIT;'
)
SELECT line FROM lines ORDER BY part, ord;
