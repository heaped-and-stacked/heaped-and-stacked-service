# Architecture

A Spring Boot REST API with a single client, the web app, which calls it from the browser.
Everything is in `org.manu.hs`, by layer first, then by feature (`content`, `learning`, `user`).

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

## Code layout

```
api/           controllers and request/response DTOs; map requests to commands, nothing else;
               also the filters and GlobalExceptionHandler
application/   XApplication: the only class controllers call; reads the current user, runs the
               checks in order, then acts
               XService: package-private validation, rules and persistence calls; never reads
               the current user, so it's easy to unit test
               command/ (inputs), exception/ (per feature; common/exception has the base types)
domain/        the model (Lombok classes, no records) and repository interfaces
persistence/   JDBC repositories: SQL as constants, mapped field by field
security/      GitHub sign-in, sessions, CSRF, who may call what (SecurityConfig),
               CurrentUserProvider
Application    the entry point; StartupLogger logs the version once the API is ready
```

## Errors

Errors are exceptions, translated in one place, `GlobalExceptionHandler`:

| Exception | Status |
| --------- | ------ |
| `NotFoundException` | 404 |
| `ForbiddenException` | 403 |
| `ValidationException` | 400 |
| `ConflictException` | 409 |

Every error body is `{"message": "…"}`. Anything unexpected is logged once with its stack trace and
the request id, and the client gets only `{"message": "Something went wrong", "requestId": "…"}`.

## Vocabulary

The web app speaks of muscles (concepts), exercises (questions) and sets (answers). Identifiers,
the API contract, settings, messages and comments use only the neutral words: concept, question,
answer, report, featured, house-owned, curate, retired. The full mapping is in the web app's
[CLAUDE.md](https://github.com/heaped-and-stacked/heaped-and-stacked-ui/blob/master/CLAUDE.md).

## Conventions

- No Java records; Lombok classes instead.
- Imports name each class: no wildcards, none unused. In IntelliJ: Code Style → Java → Imports,
  with both "class count to use import with \*" settings at 999.
- The check order is always 404 (does it exist, for this user?) → 403 (may they?) → 400 (is the
  input valid?) → 409 (does it clash with the current state?).
- A rule lives in one place: the XService, or the domain class it's about (e.g.
  `Concept.isCuratedBy`). Every rule the API checks is also a database constraint where it can be.
- New behaviour is written test first: a failing test, then the code that makes it pass.
