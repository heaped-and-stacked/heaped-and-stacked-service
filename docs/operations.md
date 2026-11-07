# Operations

How to find out what happened, and how versions are built.

## Finding out what happened

- **Health:** `GET /actuator/health` says `UP` or `DOWN`, the database included, and nothing else.
  `/actuator/health/liveness` is for restarts: it never asks the database, so a database hiccup
  doesn't restart a healthy API. `/actuator/health/readiness` is for traffic, with the database.
  Answers are cached for 5 seconds.
- **Version:** at startup the API logs `Heaped & Stacked API <version> (built <time>) is ready`.
- **Request id:** every response carries `X-Request-Id`, and every log line of that request carries
  it. The web app shows it in error messages as "ref …". When someone reports an error, search the
  logs for that id.
- **Audit log:** sign-ins and refused ones, role and status changes, account deletions, deleted or
  retired content, and report and review decisions each write one line to the `audit` logger, ids
  only, e.g. `role_changed by=1003 user=1001 role=STAFF`.
- **JSON logs:** `LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs` writes one JSON object per line, with the
  request id as a field.
- **What's never logged:** content, answers, names or other personal text; only ids.

## Builds

`.github/workflows/ci.yml` runs every test on each push and pull request. Pull requests also build
the Docker image. On pushes to `master` and `release/**` branches and on version tags, once the tests pass, it publishes two
images to GitHub Container Registry, the API and the migrations:

| Tag | Meaning |
| --- | ------- |
| `sha-<commit>` | every publish: what a deploy points at, and what a rollback goes back to |
| `latest` | the newest commit on `master` |
| `1.0.0` | a version tag |

The API reports the same version at startup: `v1.0.0` becomes `1.0.0`, anything else `sha-<commit>`.

## Releasing a version

1. Tag the commit: `git tag v1.0.0 && git push --tags`.
2. Wait for CI, then deploy that tag.
3. Publish the changes on the
   [Releases page](https://github.com/heaped-and-stacked/heaped-and-stacked-service/releases).

**Rolling back** means deploying the previous image. That works while the database is still
compatible with it, so a schema change and the code depending on it are released apart: first add,
later remove.

**Dependencies** are updated by hand, now and then. GitHub's security alerts flag a vulnerable one.
