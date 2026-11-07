# Database

PostgreSQL 16. The schema is Flyway's: `src/main/resources/db/migration/`. Every rule the API checks
is also a constraint there, where it can be.

## Tables

| Table | Holds |
| ----- | ----- |
| `app_user` | people: role, status, soft delete (`deleted_at`) |
| `user_identity` | how they sign in (GitHub id), one row per provider |
| `concept` | concepts: tag (the id), name, description, status, contribution policy |
| `question` | questions: title, requirement, sample solution, difficulty, status, source |
| `question_concept` | which concepts a question works, and whether each accepted it |
| `answer` | learners' answers, with a snapshot of the question as it was answered |
| `question_report` | learners' reports on questions: reason, note, open / resolved / dismissed |
| `question_vote` | learners' votes on questions, one per learner and question |
| `spring_session*` | sign-in sessions |

## Changing the schema

An applied migration is never edited. Every change is a new file, `V2__…sql`, then `V3__…sql`, and
it must work with the API version before it: add first, remove in a later release (expand, then
contract). That keeps rolling back the API safe, since the database itself only moves forward.

Add the constraint, the repository SQL and a test that the constraint holds.

Question texts quote `${...}` property syntax, so Flyway's placeholder replacement is off.

## Running migrations

- **Locally** the API runs them when it starts.
- **On a server** (`DB_MIGRATE_ON_START=false`) they're a release step of their own, run as the
  database's owner. The API connects as a user that can't change the schema.

`Dockerfile.migrations` builds the migrations image, from the same commit as the API. Its entry
point, `db/run.sh`, offers `migrate`, `info`, `validate`, `seed`, `export-seed` and `backup`.

## Content

Content isn't part of the schema: an environment's content lives in its database, and the app owns
it. A new environment starts from an export of an existing one (`export-seed`, then `seed`).
`seed/export-content.sql` defines what travels: concepts, questions and their verifications, and
nothing personal. `seed` refuses a database that already has content.
