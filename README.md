# Heaped & Stacked API

The backend of Heaped & Stacked: a Spring Boot service that serves the web app in
`../heaped-and-stacked-ui`. The project as a whole, and how to run everything with Docker, is
described in the README at the root of the project (`../README.md`).

## Stack

Java 21, Spring Boot 4.1, plain JDBC (`NamedParameterJdbcTemplate`, no ORM), Flyway,
PostgreSQL 16, Lombok, springdoc.

- **Sign-in:** GitHub OAuth2.
- **Session:** an HTTP-only cookie, stored in the database (Spring Session JDBC).
- **CSRF:** state-changing requests need the token from `GET /csrf` in the `X-CSRF-TOKEN`
  header.
- **CORS:** only the UI's origin (`APP_UI_URL`) may call the API.

## Run it from the IDE

Start the database with `./dev.sh db` (from the project root), then run `Application` with:

| Variable | Value |
| -------- | ----- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/heaped_and_stacked` |
| `DB_USERNAME` / `DB_PASSWORD` | `heaped_and_stacked` / `local_dev_only` |
| `APP_UI_URL` | `http://localhost:5173` |
| `API_DOCS_ENABLED` | `true` (Swagger UI at http://localhost:8080/swagger-ui.html, needed by the UI's `npm run api:types`) |
| `SESSION_COOKIE_SECURE` | `false` |
| `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, `BOOTSTRAP_ADMIN_IDS` | from the project's `.env` |

## Code layout

Layers, per feature (`content`, `learning`, `user`):

```
api/           controllers and request/response DTOs; map requests to commands, nothing else
application/   XApplication: the only class controllers call; reads the current user, runs the
               checks in order (404 → 403 → 400/409), then acts
               XService: package-private validation, rules and persistence calls; never reads
               the current user, so it's easy to unit test
domain/        the model (Lombok classes, no records) and repository interfaces
persistence/   JDBC repositories: SQL as constants, mapped field by field
security/      GitHub sign-in, sessions, CSRF, who may call what (SecurityConfig)
```

## Endpoints

| Endpoint | Who |
| -------- | --- |
| `GET /csrf` | anyone |
| `GET/PUT/DELETE /users/me` | signed in |
| `GET /content/concepts`, `POST/PUT/DELETE /content/concepts[/{tag}]`, `PUT …/status` | signed in (owner rules) |
| `POST/PUT/DELETE /content/questions[/{id}]`, `GET /content/questions/mine`, `PUT …/status` | signed in (author rules) |
| `GET /content/reviews`, `PUT /content/questions/{id}/concepts/{tag}/review` | concept owners, admins |
| `GET /learning/questions/next?concepts=&skip=` | signed in |
| `POST /learning/answers`, `GET /learning/answers[?questionId=]`, `PUT /learning/answers/{id}/assessment` | signed in |
| `/admin/users/**` | admins |

## Rules

**Concepts:**
- A concept belongs to its author and is live when created.
- The owner chooses "anyone can add exercises" or "I approve new exercises".

**Questions:**
- A question has a required title (at most 120 characters).
- It's live when submitted, on each concept once accepted there.
- Authors can edit any time. Authors can delete only their drafts.
- Answered questions are never deleted, only retired.

**Learning:** the API picks each learner's next question:
1. Questions the learner never answered come first.
2. Then answered ones, at random, weighted by how long ago (days + 1). A month-old one is
   about 31 times as likely as one answered today.

A workout sends the questions it has shown as `skip`, so it never repeats one. Each next question
comes with `unansweredCount`: how many questions for the requested concepts the learner has never
answered (skipped or not), this one included if it's new. 0 means only repeats are left.

## The database

One migration, `src/main/resources/db/migration/V1__create_tables.sql`, run by Flyway when the
API starts. Every rule the API checks is also a constraint there.

| Table | Holds |
| ----- | ----- |
| `app_user` | people: role, status, soft delete (`deleted_at`) |
| `user_identity` | how they sign in (GitHub id), one row per provider |
| `concept` | muscles: tag (the id), name, description, status, contribution policy |
| `question` | exercises: title, requirement, model answer, difficulty, status, source |
| `question_concept` | which concepts a question works, and whether each accepted it |
| `answer` | learners' answers, with a snapshot of the question as it was answered |
| `spring_session*` | sign-in sessions |

**From the first real content on, V1 is frozen.** Every schema change is a new `V2__…sql`
(`V3__…`) migration that updates the data in place. Editing V1 afterwards means
`./dev.sh reset-db`, which deletes everything, so take `./dev.sh backup` before anything risky.

## Tests

`mvn test`. Testcontainers starts its own PostgreSQL, so Docker must be running.

- **Unit tests** cover the XService rules.
- **Integration tests** call the real endpoints, one test class per endpoint. Methods are
  named `xReturnsYWhenZ`, with a Given / When / Then `@DisplayName`.
