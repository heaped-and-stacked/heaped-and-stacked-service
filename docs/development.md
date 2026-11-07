# Development

## Run it locally

You need Docker (for PostgreSQL and the tests), Java 21, Maven, and a GitHub OAuth App (GitHub →
Settings → Developer settings → OAuth Apps) whose callback URL is
`http://localhost:8080/login/oauth2/code/github`.

1. Start PostgreSQL 16:

   ```sh
   docker run -d --name heaped-and-stacked-db -p 5432:5432 \
     -e POSTGRES_DB=heaped_and_stacked -e POSTGRES_USER=heaped_and_stacked \
     -e POSTGRES_PASSWORD=local_dev_only postgres:16-alpine
   ```

2. Run `Application` from the IDE, or `mvn spring-boot:run`, with the settings below. Flyway creates
   the tables on startup.
3. Check it: `curl http://localhost:8080/actuator/health` answers `{"status":"UP"}`. With
   `API_DOCS_ENABLED=true`, http://localhost:8080/swagger-ui.html lists every endpoint.
4. Sign in through the web app, or at http://localhost:8080/oauth2/authorization/github. Put your
   own GitHub id in `BOOTSTRAP_ADMIN_IDS` to be admin from the first sign-in.

A fresh database has no content: as an admin, add concepts and questions in the web app.

To run the API in a container instead: `docker build -t heaped-and-stacked-service .`, then
`docker run -p 8080:8080` with the same settings. `DB_URL` then points at the database as the
container sees it, e.g. `host.docker.internal` instead of `localhost`.

## Settings

Everything comes from environment variables. `src/main/resources/application.properties` maps them
to Spring properties; app-specific ones start with `app.`.

| Variable | Local value | Deployed | What it is |
| -------- | ----------- | -------- | ---------- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/heaped_and_stacked` | with `?sslmode=require` | the database |
| `DB_USERNAME`, `DB_PASSWORD` | `heaped_and_stacked`, `local_dev_only` | secrets | its account |
| `DB_MIGRATE_ON_START` | unset (default `true`) | `false` | run migrations at startup |
| `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` | from your OAuth App | secrets, from a separate OAuth App | GitHub sign-in |
| `APP_UI_URL` | `http://localhost:5173` | `https://<domain>` | the web app's origin: the only one CORS allows, and where sign-in returns |
| `BOOTSTRAP_ADMIN_IDS` | e.g. `GITHUB:12345678` | your id | accounts made admin at every sign-in (your id: `https://api.github.com/users/<login>` → `id`) |
| `SESSION_COOKIE_SECURE` | `false` | unset (default `true`) | secure cookies, https only |
| `API_DOCS_ENABLED` | `true` | unset (default `false`) | Swagger UI and the OpenAPI spec |
| `MEMBERS_ADD_QUESTIONS` | `true` | `true` | whether members may add questions (default `false`) |
| `MEMBERS_ADD_CONCEPTS` | `true` | `true` | whether members may add concepts (default `false`) |
| `FORWARD_HEADERS_STRATEGY` | unset (default `none`) | `native` | read `X-Forwarded-*` from the reverse proxy; needed for real client addresses |
| `TRUSTED_PROXIES` | unset | only for a public proxy, such as a CDN | regular expression of public proxy addresses to skip, on top of private networks |
| `RATE_LIMIT_ENABLED` | unset (default `true`) | unset | per-client rate limits |
| `LOGGING_STRUCTURED_FORMAT_CONSOLE` | unset (plain text) | `ecs` | JSON log lines |

## Tests

`mvn test` runs about 450 tests in a few minutes. Testcontainers starts its own PostgreSQL, so
Docker must be running. `src/test/resources/application-test.properties` overrides some settings,
such as turning rate limits off.

- **Unit tests** cover the XService rules and the filters.
- **Integration tests** call the real endpoints over HTTP, with a real database: one test class per
  endpoint, plus journeys across several endpoints. Methods are named `xReturnsYWhenZ`, with a
  Given / When / Then `@DisplayName` and plain JUnit assertions.

## Common tasks

- **A new endpoint:** a failing integration test first. Then the controller in `api/<feature>`
  (request DTO → command), a method on `XApplication` (current user and checks in order), and the
  rule in `XService` with its unit test. Open or restrict it in `SecurityConfig`.
- **A schema change:** see [Database](database.md#changing-the-schema).
- **A new setting:** a property in `application.properties` read from an environment variable with a
  safe default (`app.x=${X:default}`), injected with `@Value`. Add it to the table above.
- **Make someone staff:** the web app's admin page, or `PUT /admin/users/{id}/role` with `MEMBER` or
  `STAFF`. Their sessions end, so the new role applies from their next sign-in.
- **Check dependencies:** `mvn versions:display-dependency-updates` shows what's newer.

## Troubleshooting

- **GitHub: "redirect_uri is not associated with this application":** the OAuth App's callback URL
  must be exactly `http://localhost:8080/login/oauth2/code/github`.
- **The browser says CORS:** the web app's origin must equal `APP_UI_URL` exactly: scheme, host and
  port, no trailing slash.
- **401 on every call:** no session. The cookie is `Secure` by default, so over plain http set
  `SESSION_COOKIE_SECURE=false`.
- **403 on a write:** usually a missing or stale CSRF token: fetch `GET /csrf` first and send it as
  `X-CSRF-TOKEN`. Otherwise the user really may not; the message says which.
- **429 while testing locally:** the rate limits apply on localhost too. Set
  `RATE_LIMIT_ENABLED=false` for a load test, never when deployed.
- **After sign-in, the user isn't admin:** check the id in `BOOTSTRAP_ADMIN_IDS` (the numeric GitHub
  id, prefixed `GITHUB:`), then sign out and in again.
- **Flyway: "Migration checksum mismatch":** an applied migration was edited. Put it back and make
  the change in a new one.
- **Tests fail at startup:** Docker isn't running.
