# Heaped & Stacked API

The backend of Heaped & Stacked, a gym for programmers' brains: learners pick **concepts**
(topics, e.g. Java concurrency) and answer **questions** (open questions with a model answer),
writing their own answer first and comparing afterwards. Members can also write questions and
concepts; the people who own a concept decide what goes live on it.

This repository is a Spring Boot REST API. Its only client is the Heaped & Stacked web app, which
lives in its own repository and calls this API from the browser. The web app speaks of muscles
(concepts), exercises (questions) and sets (answers); this API uses the plain words.

- **Status:** pre-release. Until the first production deploy, schema changes still go into
  `V1`, and the gym's content into `V2` (see [The database](#the-database)).
- **Start here:** [Run it locally](#run-it-locally), then [How a request flows](#how-a-request-flows)
  and [Code layout](#code-layout).

## Contents

- [Stack](#stack)
- [Run it locally](#run-it-locally)
- [Configuration](#configuration)
- [How a request flows](#how-a-request-flows)
- [Code layout](#code-layout)
- [Endpoints](#endpoints)
- [Rules](#rules)
- [The database](#the-database)
- [Tests](#tests)
- [Operations: finding out what happened](#operations-finding-out-what-happened)
- [CI and releases](#ci-and-releases)
- [Common tasks](#common-tasks)
- [Troubleshooting](#troubleshooting)

## Stack

Java 21, Spring Boot 4.1, plain JDBC (`NamedParameterJdbcTemplate`, no ORM), Flyway,
PostgreSQL 16, Lombok, springdoc, Spring Boot Actuator (health only). Tests: JUnit 5,
Testcontainers, `RestTestClient` against a real server.

- **Sign-in:** GitHub OAuth2. The API never sees a password and asks GitHub for no email.
- **Session:** an HTTP-only cookie, stored in the database (Spring Session JDBC), so restarts and
  several instances keep people signed in. Anonymous requests never create a session.
- **CSRF:** state-changing requests need the token from `GET /csrf` in the `X-CSRF-TOKEN`
  header. The session cookie travels with every browser request, so without this token any other
  site could make a signed-in visitor's browser post to the API.
- **CORS:** only the UI's origin (`APP_UI_URL`) may call the API.

## Run it locally

You need Docker (for PostgreSQL and the tests), Java 21 and Maven, and a GitHub OAuth App
(GitHub > Settings > Developer settings > OAuth Apps) whose callback URL is
`http://localhost:8080/login/oauth2/code/github`.

1. Start PostgreSQL 16:

   ```sh
   docker run -d --name heaped-and-stacked-db -p 5432:5432 \
     -e POSTGRES_DB=heaped_and_stacked -e POSTGRES_USER=heaped_and_stacked \
     -e POSTGRES_PASSWORD=local_dev_only postgres:16-alpine
   ```

2. Run `Application` from the IDE (or `mvn spring-boot:run`) with the configuration below.
   Flyway creates the tables on startup.
3. Check it: `curl http://localhost:8080/actuator/health` answers `{"status":"UP"}`. With
   `API_DOCS_ENABLED=true`, http://localhost:8080/swagger-ui.html lists every endpoint.
4. Sign in through the web app (or open http://localhost:8080/oauth2/authorization/github). Put
   your own GitHub id in `BOOTSTRAP_ADMIN_IDS` to be admin from the first sign-in.

A fresh database gets the gym's content (16 concepts, 956 questions) from the `V2` migration on
first startup, owned by the house account "Heaped & Stacked".

To run the API itself in a container instead: `docker build -t heaped-and-stacked-service .`
(`--build-arg APP_VERSION=1.0.0` sets the version it reports), then
`docker run -p 8080:8080` with the same variables (`DB_URL` then points at the database as the
container sees it, e.g. `host.docker.internal` instead of `localhost`).

## Configuration

Everything comes from environment variables:

| Variable | Local value | Production | What it is |
| -------- | ----------- | ---------- | ---------- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/heaped_and_stacked` | the managed database, with `?sslmode=require` | the database |
| `DB_USERNAME` / `DB_PASSWORD` | `heaped_and_stacked` / `local_dev_only` | secrets | its account |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | from your local OAuth App | from a separate production OAuth App (secrets) | GitHub sign-in |
| `APP_UI_URL` | `http://localhost:5173` | `https://<domain>` | the web app's origin: the only one CORS allows, and where sign-in returns |
| `BOOTSTRAP_ADMIN_IDS` | e.g. `GITHUB:12345678` | your id | accounts made admin at every sign-in (your id: `https://api.github.com/users/<login>` → `id`); optional |
| `SESSION_COOKIE_SECURE` | `false` | unset (default `true`) | secure cookies (https only) |
| `API_DOCS_ENABLED` | `true` | unset (default `false`) | Swagger UI at `/swagger-ui.html` and the spec at `/v3/api-docs` |
| `MEMBERS_PUBLISH_MUSCLES` | `false` | `false` at launch | whether members publish their own concepts (see [Rules](#rules); default `false`) |
| `MEMBERS_CONTRIBUTE` | `false` | `false` at launch | whether members may add concepts and questions at all (see [Rules](#rules); default `false`: staff only) |
| `FORWARD_HEADERS_STRATEGY` | `none` | `native` | read `X-Forwarded-*` from the reverse proxy, the right way: client-written entries are ignored (default `none`) |
| `TRUSTED_PROXIES` | (none) | (only for a public proxy such as a CDN) | regular expression of public proxy addresses to skip, on top of private networks |
| `RATE_LIMIT_ENABLED` | `true` | unset (default `true`) | per-client rate limits |
| `LOGGING_STRUCTURED_FORMAT_CONSOLE` | unset (plain text) | `ecs` | JSON log lines, which hosting platforms search well |

`src/main/resources/application.properties` maps these to Spring properties; app-specific ones
start with `app.`. Tests override some in `src/test/resources/application-test.properties`
(e.g. rate limits off).

## How a request flows

```
browser
  → RequestIdFilter          gives the request an id: X-Request-Id header, MDC, every log line
  → RequestSizeLimitFilter   bodies over 256 KB → 413, before they're read
  → RateLimitFilter          too many requests from this address → 429 + Retry-After
  → Spring Security          CORS, session cookie, CSRF, who may call what (SecurityConfig)
  → controller (api/)        maps the request to a command; nothing else
  → XApplication             reads the current user; checks 404 → 403 → 400 → 409; acts
  → XService                 validation, rules, persistence calls
  → repository               SQL
```

Errors are exceptions, translated in one place, `GlobalExceptionHandler`:
`NotFoundException` → 404, `ForbiddenException` → 403, `ValidationException` → 400,
`ConflictException` → 409. Every error body is `{"message": "…"}`. Anything unexpected is logged
once with its stack trace and the request id, and the client gets only
`{"message": "Something went wrong", "requestId": "…"}`: no stack traces leave the API.

## Code layout

Everything is in `org.manu.hs`, by layer first, then by feature (`content`, `learning`, `user`):

```
api/           controllers and request/response DTOs; map requests to commands, nothing else;
               also the filters and GlobalExceptionHandler
application/   XApplication: the only class controllers call; reads the current user, runs the
               checks in order (404 → 403 → 400/409), then acts
               XService: package-private validation, rules and persistence calls; never reads
               the current user, so it's easy to unit test
               command/ (inputs), exception/ (per feature; common/exception has the base types)
domain/        the model (Lombok classes, no records) and repository interfaces
persistence/   JDBC repositories: SQL as constants, mapped field by field
security/      GitHub sign-in, sessions, CSRF, who may call what (SecurityConfig),
               CurrentUserProvider
Application    the entry point; StartupLogger logs the version once the API is ready
```

**Conventions:**
- No Java records; Lombok classes instead.
- The check order is always 404 (does it exist, for this user?) → 403 (may they?) → 400 (is the
  input valid?) → 409 (does it clash with the current state?).
- A rule lives in one place: the XService, or the domain class it's about (e.g.
  `Concept.isCuratedBy`). Every rule the API checks is also a database constraint where it can be.
- New behaviour is written test first: a failing test, then the code that makes it pass.

## Endpoints

| Endpoint | Who |
| -------- | --- |
| `GET /csrf` | signed-in users (only they write; a token for anyone would store a session for every visitor) |
| `GET /actuator/health`, `/liveness`, `/readiness` | anyone (`UP` or `DOWN` only, for healthchecks, platform probes and uptime monitors) |
| `GET /oauth2/authorization/github`, `/login/oauth2/code/github` | anyone (the sign-in round trip) |
| `POST /logout` | signed in |
| `GET/PUT/DELETE /users/me` | signed in |
| `GET /content/concepts` | anyone (drafts only for their owner and staff) |
| `POST/PUT/DELETE /content/concepts[/{tag}]`, `PUT …/status` | signed in (owner rules) |
| `POST/PUT/DELETE /content/questions[/{id}]`, `GET /content/questions/mine`, `PUT …/status` | signed in (author rules) |
| `GET /content/questions?muscle=&search=&page=&size=` | signed in (their own; staff see all) |
| `GET /content/questions/{id}` | the author, staff (to edit or preview it) |
| `GET /content/reviews`, `PUT /content/questions/{id}/concepts/{tag}/review` | concept owners, staff |
| `GET /content/questions/{id}/reports` | the author, staff (all its reports: open first, then decided) |
| `GET /content/reports[?decided=true]`, `PUT /content/reports/{id}` | question authors, concept owners, staff (open feedback; with `decided=true`, the latest 100 decided) |
| `GET /learning/questions/next?concepts=&skip=` | anyone (a visitor has no history: everything is new) |
| `GET /learning/questions/{id}?concepts=` | signed in (one chosen exercise, e.g. a retry from past workouts; 404 when it can't be trained) |
| `POST /learning/answers`, `GET /learning/answers[?questionId=]`, `PUT /learning/answers/{id}/assessment` | signed in |
| `POST /learning/questions/{id}/reports`, `GET/PUT/DELETE …/reports/mine`, `PUT/DELETE /learning/questions/{id}/vote` | signed in |
| `GET /admin/overview` | admins (users, this week's new users, active users and answers, open reports, questions awaiting review) |
| `/admin/users/**` (list, role, status; `DELETE /admin/users/{id}[?eraseActivity=true]`) | admins; the house account can't be deleted, blocked or have its role changed |

Request and response shapes: Swagger UI (`API_DOCS_ENABLED=true`).

## Rules

**Roles:** `MEMBER` curates their own content. `STAFF` curate every concept and question:
reviews, reports, edits, retiring, deleting what nobody uses. `ADMIN` are staff who also manage
users (roles, blocking). Content rules ask `CurrentUserProvider.isStaff()`, user management asks
`isAdmin()`. Who curates a concept is decided in one place, `Concept.isCuratedBy`.

**Users:**
- An account is created at the first GitHub sign-in (`app_user` plus a `user_identity`).
- Accounts in `BOOTSTRAP_ADMIN_IDS` become admin at every sign-in, so an admin can't lock
  everyone out.
- A role or status change ends all the person's sessions (`UserSessions`): a blocked user is out
  at once and can't sign in again; a new role applies from the next sign-in.
- Deleting an account is a soft delete: name, picture and the GitHub link go at once, and the
  person's sessions end; answers and contributions stay, attached to a nameless account.

**Contributing:** with `app.content.members-contribute` (env `MEMBERS_CONTRIBUTE`) false, the default,
only staff add concepts and questions; a member who tries gets 403, and `GET /users/me` says
`canContribute: false`, so the web app hides its ADD buttons. Members still edit what they already
wrote. With it true, members contribute as described below.

**Concepts:**
- A concept belongs to its author. Staff's concepts are live when created; members' are too
  when `app.content.members-publish-concepts` (env `MEMBERS_PUBLISH_MUSCLES`) is true, otherwise
  they wait as drafts until staff publish them (the default).
- Drafts are listed only to their owner and staff.
- The owner chooses "anyone can add exercises" or "I approve new exercises". A question waits
  for approval only when its author doesn't curate the concept: the owner's and staff's own
  questions are accepted straight away.
- The owner (or staff) accept or decline each question for their concept. A decline is final:
  the concept drops off the question, and the author can't add it back.

**Text fields:** every free-text field has a maximum length. Short single-line fields (titles,
names, sources, display names, reference links) refuse line breaks, control characters and
invisible formatting characters (zero-width spaces, direction overrides); descriptions and report
notes refuse the invisible ones but allow line breaks. Question texts and answers may hold any
code. The API stores text as given; the web app shows it as text, never as HTML.

**Questions:**
- A question has a required title (at most 120 characters).
- It's live when submitted, on each concept once accepted there.
- Authors can edit any time. Authors can delete only their drafts.
- Answered questions are never deleted, only retired.
- Coach's Corner lists them a page at a time (`GET /content/questions`, pages of 1 to 100,
  20 by default), newest first: members their own, staff everyone's. Filters: a concept (any of
  the question's, not only its main one) and part of the title, in any case.

**Learning:** the API picks each learner's next question:
1. Questions the learner never answered come first.
2. Then answered ones.

Within each group the pick is random but weighted, so nothing is ever certain:
- **Age:** days since the learner last answered it + 1 (1 for a new one). A month-old one is
  about 31 times as likely as one answered today.
- **Likes:** 1 + ln(1 + likes), where likes are learners who voted it up or praised it, each
  counted once. A nudge that flattens out: 1 like 1.7, 5 likes 2.8, 20 likes 4.0.

The two weights multiply.

A workout sends the questions it has shown as `skip`, so it never repeats one. Each next question
comes with `unansweredCount`: how many questions for the requested concepts the learner has never
answered (skipped or not), this one included if it's new. 0 means only repeats are left.

**Feedback:** learners rate the questions they can answer.
- **Votes:** up or down, one per learner, which they can change or take back. Authors see the
  totals on their own questions (`upVotes`, `downVotes` in `questions/mine`). Up votes count as
  likes (see Learning); down votes don't change how often a question is served.
- **Reports** (feedback in the web app): praise ("keep it, more like this"), or a problem: wrong,
  unclear, duplicate, inappropriate or other. Each with an optional note (at most 2000 characters,
  enough for a suggested fix). Each learner has at most one open report per question, which they
  can see, change or withdraw (`…/reports/mine`) until it's decided; then they may send a new one.
- **Holding back:** a question with 3 open problem reports is no longer served until some are
  decided. Praise never holds a question back, and counts as a like.
- **Deciding:** the question's author, the owner of any concept it's linked to, or staff mark
  a report resolved (fixed, or praise noted) or dismissed (nothing to fix). Reports never say who
  sent them. A decision is final: deciding again, or changing or withdrawing a decided report,
  is refused (409, or 404 for "no open feedback").

**Limits:**
- **Request size:** bodies over 256 KB are refused with 413 before they're read; the largest valid
  request stays under 100 KB.
- **Rate limits:** per client address and minute: 10 sign-in starts, 60 writes, 300 reads; health
  checks are free. Over the limit: 429 with `Retry-After`. In memory, per instance. Behind a proxy
  it needs `FORWARD_HEADERS_STRATEGY=native`, or every client shares the proxy's address.

## The database

Two migrations, run by Flyway when the API starts:

- `db/migration/V1__create_tables.sql`: the schema. Every rule the API checks is also a
  constraint there.
- `db/content/V2__initial_content.sql`: the gym's content at launch: the house account
  "Heaped & Stacked" (staff, no GitHub identity) that owns it, the 16 concepts and the 956
  questions. **Generated** from the muscle files in the workspace's `content/` folder
  (`perl content/tools/build-migration.pl`); don't edit it by hand. The tests leave `db/content`
  out (`application-test.properties`), so they start from empty tables. Exercise texts quote
  `${...}`, so Flyway's placeholder replacement is off.

| Table | Holds |
| ----- | ----- |
| `app_user` | people: role, status, soft delete (`deleted_at`) |
| `user_identity` | how they sign in (GitHub id), one row per provider |
| `concept` | concepts: tag (the id), name, description, status, contribution policy |
| `question` | questions: title, requirement, model answer, difficulty, status, source |
| `question_concept` | which concepts a question works, and whether each accepted it |
| `answer` | learners' answers, with a snapshot of the question as it was answered |
| `question_report` | learners' reports on questions: reason, note, open / resolved / dismissed |
| `question_vote` | learners' votes on questions, one per learner and question |
| `spring_session*` | sign-in sessions |

**Until the first release, schema changes go into V1 and content changes into V2** (regenerated).
Editing either changes its checksum, so a local database has to be recreated afterwards: back it up (`pg_dump`), drop it, let Flyway
recreate the tables on startup, then load the data back. **From the first release on, both are
frozen:** every schema or content change is a new `V3__…sql` (`V4__…`) migration that updates the
data in place. Never edit a migration that has run in production.

## Tests

`mvn test` (about 450 tests, a few minutes). Testcontainers starts its own PostgreSQL, so Docker
must be running.

- **Unit tests** cover the XService rules and the filters.
- **Integration tests** call the real endpoints over HTTP, one test class per endpoint, with a
  real database. Methods are named `xReturnsYWhenZ`, with a Given / When / Then `@DisplayName`,
  and plain JUnit assertions.

## Operations: finding out what happened

- **Health:** `GET /actuator/health` says `UP` or `DOWN` (the database included), nothing else;
  point uptime monitors at it. The platform's probes: `/actuator/health/liveness` for restarts (no
  database, so a database hiccup doesn't restart a healthy API) and `/actuator/health/readiness`
  for traffic (with the database). Answers are cached for 5 seconds.
- **Version:** at startup the API logs `Heaped & Stacked API <version> (built <time>) is ready`,
  from Maven's build info. Search for it to know what's running.
- **Request id:** every response carries `X-Request-Id`, and every log line of that request
  carries it. The web app shows it in error messages as "ref …". When someone reports an error,
  search the logs for that id.
- **Audit log:** sign-ins (and refused ones), role and status changes, account deletions, deleted
  or retired content, report decisions and review decisions (accept or decline) each write one
  line to the `audit` logger, ids only, e.g. `role_changed by=1003 user=1001 role=STAFF`. Search
  for `audit` and the user's id.
- **JSON logs:** `LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs` writes one JSON object per line, with the
  request id as a field.
- **What's never logged:** content, answers, names or other personal text; only ids.

## CI and releases

`.github/workflows/ci.yml` runs every test on each push and pull request (pull requests also
build the Docker image). On pushes to `master` and on version tags (`v1.0.0`), once the tests
pass, it publishes the image to GitHub Container Registry as
`ghcr.io/<owner>/heaped-and-stacked-service`, tagged:

| Tag | Meaning |
| --- | ------- |
| `sha-<commit>` | every publish: what a deploy points at, and what a rollback goes back to |
| `latest` | the newest commit on `master` |
| `1.0.0` | a version tag |

The API logs the same version at startup (`v1.0.0` → `1.0.0`, otherwise `sha-<commit>`).
Packages start private: the hosting platform then needs a pull token, or the package is made
public. Dependabot proposes dependency updates weekly.

- **Release:** tag the commit (`git tag v1.0.0 && git push --tags`), wait for CI, deploy that tag.
- **Rollback:** deploy the previous `sha-…` image. This only works while the database is still
  compatible with it, so a schema change and the code depending on it are best released apart
  (first add, later remove).

## Common tasks

- **A new endpoint:** a failing integration test first (its own test class, or the endpoint's
  existing one). Then the controller in `api/<feature>` (request DTO → command), a method on
  `XApplication` (current user and checks in order) and the rule in `XService` with its unit test.
  Open or restrict it in `SecurityConfig`.
- **A schema change:** before the first release, edit `V1__create_tables.sql` and recreate the
  local database; afterwards, a new `V3__…` migration. Add the constraint, the repository SQL
  and a test that the constraint holds.
- **A new setting:** a property in `application.properties` read from an environment variable
  with a safe default (`app.x=${X:default}`), injected with `@Value`. Add it to
  [Configuration](#configuration).
- **Make someone staff or admin:** the web app's front desk (admins), or `PUT /admin/users/{id}/role`. Their sessions end, so the
  new role applies from their next sign-in.
- **Check dependencies:** Dependabot alerts on GitHub; `mvn org.owasp:dependency-check-maven:check`
  for the libraries, and Trivy for the image.

## Troubleshooting

- **Flyway: "Migration checksum mismatch":** V1 or V2 was edited (or regenerated) after it ran. Back up, drop and
  recreate the local database (see [The database](#the-database)).
- **GitHub: "redirect_uri is not associated with this application":** the OAuth App's callback
  URL must be exactly `http://localhost:8080/login/oauth2/code/github` (in production,
  `https://api.<domain>/login/oauth2/code/github`).
- **The browser says CORS:** the web app's origin must equal `APP_UI_URL` exactly (scheme, host
  and port, no trailing slash).
- **401 on every call:** no session. The cookie is `Secure` by default, so over plain http set
  `SESSION_COOKIE_SECURE=false`.
- **403 on a write:** usually a missing or stale CSRF token: fetch `GET /csrf` first and send it
  as `X-CSRF-TOKEN`. Otherwise the user really may not (the body's message says which).
- **429 while testing locally:** the rate limits apply on localhost too; set
  `RATE_LIMIT_ENABLED=false` for a load test, never in production.
- **After sign-in, the user isn't admin:** check the id in `BOOTSTRAP_ADMIN_IDS` (it's the numeric
  GitHub id, prefixed `GITHUB:`), then sign out and in again.
- **Tests fail at startup:** Docker isn't running (Testcontainers needs it).
