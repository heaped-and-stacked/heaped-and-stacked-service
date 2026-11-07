# API

Who may call what. For request and response shapes, run the API with `API_DOCS_ENABLED=true` and
open `/swagger-ui.html`; the spec is at `/v3/api-docs`. The rules behind these are in
[Business rules](business-rules.md).

State-changing requests need the token from `GET /csrf` in the `X-CSRF-TOKEN` header.

## Sign-in and account

| Endpoint | Who |
| -------- | --- |
| `GET /oauth2/authorization/github`, `/login/oauth2/code/github` | anyone (the sign-in round trip) |
| `GET /csrf` | signed in (only they write; a token for anyone would store a session per visitor) |
| `POST /logout` | signed in |
| `GET`, `PUT`, `DELETE /users/me` | signed in |

## Training

| Endpoint | Who |
| -------- | --- |
| `GET /learning/questions/next?concepts=&skip=&pool=` | anyone (a visitor has no history, so everything is new). `pool`: `VERIFIED`, `ALL` (default) or `TO_VERIFY` (the unverified ones the caller may verify) |
| `GET /learning/questions/{id}?concepts=` | signed in (one chosen question, e.g. a retry; 404 when it can't be trained) |
| `POST /learning/answers`, `GET /learning/answers[?questionId=]` | signed in |
| `PUT /learning/answers/{id}/assessment` | signed in |
| `POST /learning/questions/{id}/reports` | signed in |
| `GET`, `PUT`, `DELETE /learning/questions/{id}/reports/mine` | signed in |
| `PUT`, `DELETE /learning/questions/{id}/vote` | signed in |

## Content

| Endpoint | Who |
| -------- | --- |
| `GET /content/concepts` | anyone (drafts only for those who curate them) |
| `GET /content/concepts/workload` | signed in (for each concept they curate: its questions, how many to verify, how many waiting) |
| `POST /content/concepts`, `PUT`, `DELETE /content/concepts/{tag}`, `PUT …/status` | signed in (owner rules) |
| `PUT /content/concepts/{tag}/featured` | staff |
| `GET /content/questions?concept=&search=&verified=&asMember=&page=&size=` | signed in (their own; staff see all, unless `asMember=true`) |
| `GET /content/questions/{id}` | the author, staff, whoever may verify it |
| `POST /content/questions`, `PUT`, `DELETE /content/questions/{id}`, `PUT …/status` | signed in (author rules; deleting and `…/status` are staff only) |
| `PUT /content/questions/{id}/verification` | whoever may verify it |
| `GET /content/reviews?page=&size=`, `PUT /content/questions/{id}/concepts/{tag}/review` | concept owners, staff |
| `GET /content/questions/{id}/reports` | the author, staff (open first, then decided) |
| `GET /content/reports?decided=&asMember=&page=&size=`, `PUT /content/reports/{id}` | question authors, concept owners, staff |

## Administration

All admins only.

| Endpoint | What |
| -------- | ---- |
| `GET /admin/overview` | users, this week's activity, open reports, questions awaiting review |
| `GET /admin/invites`, `POST /admin/invites`, `DELETE /admin/invites/{id}` | the invite list |
| `GET /admin/users`, `PUT /admin/users/{id}/role`, `PUT /admin/users/{id}/status` | roles and blocking; the house account can't be changed |
| `DELETE /admin/users/{id}[?eraseActivity=true]` | deleting an account; the house account can't be deleted |

## Health

| Endpoint | Who |
| -------- | --- |
| `GET /actuator/health`, `/liveness`, `/readiness` | anyone (`UP` or `DOWN` only) |

Any other path is refused.
