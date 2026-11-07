# Security

What protects the API. The web app's own measures are in its
[documentation](https://github.com/heaped-and-stacked/heaped-and-stacked-ui/blob/master/docs/architecture.md).
To report a vulnerability, please use the contact address on the site rather than a public issue.

- **Sample solutions aren't leaked:** `GET /learning/questions/next` never includes one. It comes
  back only with the learner's own submitted answer, or to whoever may verify the question. Answers
  are returned only to their author.
- **Authorization on every write, in the services:** questions by their author or staff; concepts by
  their owner or staff; reviews by the concept's curators; verifying by whoever may verify it;
  reports decided by the author, a linked concept's owner, or staff; `/admin/**` for admins only;
  unknown paths denied. What the web app shows doesn't change any of this.
- **Sign-in:** GitHub OAuth2. The API never sees a password and asks GitHub for no email address.
- **Sessions:** an HttpOnly, Secure, SameSite=Lax cookie, stored in the database. A new session id at
  sign-in; every session of a user ends on block, role change or deletion; the last admin can't be
  removed; anonymous and refused requests create no session.
- **CSRF** tokens on every POST, PUT and DELETE. The session cookie travels with every browser
  request, so without a token any other site could make a signed-in visitor's browser post to the
  API.
- **CORS** allows only the web app's origin, with credentials.
- **Injection:** every query binds its parameters.
- **Limits:** every free-text field and list, in the API and the database; bodies over 256 KB refused
  (413); per address and minute, 10 sign-in starts, 60 writes and 300 reads (429); invisible
  characters refused in titles and names.
- **Errors** carry no stack traces or exception messages. Swagger is off unless
  `API_DOCS_ENABLED=true`, and actuator exposes health only.
- **Database rights:** the API's user can't change the schema; migrations run as the owner.
- **On purpose:** visitors may list concepts and train, so question texts are public. The rate limits
  matter for that.
