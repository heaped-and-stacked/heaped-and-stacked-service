# Heaped & Stacked: API

### Try the app at [heapedstacked.dev](https://heapedstacked.dev)

Heaped & Stacked is an online learning tool, generally focused on programming and specifically on
Java and Spring. The goal is to bring questions as plain and simple as possible, like you would get
them from a colleague or hear them in an interview. The expected format is free text with
self-evaluation, allowing one to track progress on certain topics and to compare how well an answer
stands next to their previous ones, or next to a sample one provided by the app.

It does not intend to be a teaching authority, but rather focuses on bringing users the right
questions and documentation references. It even asks users to do their own research and not to rely
on what they see here.

It does not intend to be a Codewars-style app. There are questions in the bank asking how code
snippets behave, but nothing that a user submits is compiled or executed on the server.

Lastly, the app leaves room to grow through use. Users may create custom topics with their own
questions, and even contribute to existing topics by adding new questions to the pool.

This repository is the backend, a Spring Boot REST API. The web app is in
[heaped-and-stacked-ui](https://github.com/heaped-and-stacked/heaped-and-stacked-ui).

## What it does

The API works with **concepts** (topics), **questions** and **answers**. Version 1 covers:

- **Training:** pick concepts and get one question at a time, with the ones you haven't answered
  first. Visitors can try it without an account.
- **Your history:** every answer is kept, with how hard it felt and how you graded yourself.
- **Writing content:** members add questions and their own concepts. Each concept's owner decides
  what goes live on it.
- **Keeping quality up:** questions are verified by the people who curate them, and learners can
  vote and report problems. A question with too many open problems stops being served.
- **Accounts:** sign in with GitHub, roles for members, staff and admins, and deleting your account
  whenever you like.

## Technology

| Technology | Where it fits |
| ---------- | ------------- |
| **Java 21, Spring Boot 4** | The REST API: a plain layered application. |
| **Spring JDBC, no JPA or Hibernate** | Every query is hand-written SQL. Picking the next question is one weighted-random query, so the SQL is the logic, and it stays in plain sight instead of behind an ORM. |
| **PostgreSQL 16** | All the data. Rules the API checks are also database constraints wherever they can be. |
| **Flyway** | Versioned schema migrations, which only ever move forward. |
| **Spring Security, GitHub OAuth2** | Sign-in for an audience that already has GitHub: the app stores no passwords. |
| **Spring Session JDBC** | Sessions live in the database, so they survive restarts and can be ended at once when someone is blocked. |
| **springdoc (OpenAPI)** | Describes the API as an OpenAPI spec, so its contract is always published. |
| **JUnit 5, Testcontainers** | About 450 tests. Integration tests call real endpoints over HTTP, against a real PostgreSQL. |
| **Docker, GitHub Actions** | Every push is tested, and a passing build publishes the images. |

## How it's organised

A request passes through four layers, each with one job:

```
api/            HTTP: controllers, request and response objects, filters
application/    what the app does: who may do it, the rules, in which order
domain/         the model and the repository interfaces
persistence/    SQL
```

The [architecture guide](docs/architecture.md) follows a request through them.

## Run it locally

You need Docker, Java 21, Maven and a GitHub OAuth App. The
[development guide](docs/development.md) has the full steps, the settings and how to run the tests.

## Documentation

Detailed documentation lives in [docs/](docs/README.md):

- [Architecture](docs/architecture.md): how a request flows, and the conventions in the code
- [Decisions](docs/decisions.md): why it's built this way
- [Business rules](docs/business-rules.md): roles, ownership, reviews, how questions are picked
- [API](docs/api.md): the endpoints and who may call them
- [Database](docs/database.md): the tables and how the schema changes
- [Development](docs/development.md): running, configuring and testing it
- [Operations](docs/operations.md): health, logs, builds and releases
- [Security](docs/security.md): what protects the API

## Releases

Changes between versions are published on the
[Releases page](https://github.com/heaped-and-stacked/heaped-and-stacked-service/releases).

## Found a bug?

Please [open an issue](https://github.com/heaped-and-stacked/heaped-and-stacked-service/issues).

## Licence

© 2026 Haprean Manuel. All rights reserved. The code is public to read and learn from; it isn't
licensed for reuse.
