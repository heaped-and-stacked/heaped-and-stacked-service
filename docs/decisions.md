# Decisions

Why the API is built the way it is, and what each choice costs.

## Hand-written SQL instead of JPA or Hibernate

The queries are a large part of the logic. Picking the next question is a single weighted-random
query, and filtering, paging and review states are shaped closely to the tables. With
`NamedParameterJdbcTemplate`, every statement is visible in the repository that runs it, and nothing
loads lazily or runs unseen. The domain model stays free of persistence annotations.

**The cost:** rows are mapped field by field, by hand, so a new column means touching its repository.

## Sessions in a cookie, not tokens

The only client is a browser, so an HTTP-only cookie keeps the session out of reach of page scripts.
Sessions live in the database (Spring Session JDBC), which means they survive restarts, and a blocked
user's sessions can be ended immediately. Self-contained tokens couldn't be taken back that easily.

**The cost:** state-changing requests need a CSRF token, and each signed-in request reads the
session from the database.

## GitHub as the only sign-in

The audience is programmers, who nearly all have an account. The app stores no passwords and asks
GitHub for no email address, so there's far less personal data to protect.

**The cost:** someone without GitHub can try questions but can't save anything.

## Rules in the code and in the database

The services give clear error messages; constraints guarantee the rule, even under concurrency or
if a bug slips through. Uniqueness is the clearest case: only the database can decide it safely.

**The cost:** a rule is stated twice, and changing it needs a migration.

## Layers with one entry point per feature

Controllers call only `XApplication`, which reads the current user and runs the checks in a fixed
order (404 → 403 → 400 → 409). Services take everything as parameters and never read the current
user, so the rules can be unit tested without a security context.

## Forward-only migrations, run apart from the API

A migration runs as its own release step, as the database's owner. The API connects as a user that
can't change the schema, so a bug in the API can't either. Migrations are never rolled back: each
one works with the API version before it (expand, then contract), so the API alone can be rolled
back at any time.

## One instance, rate limits in memory

Rate limits count per client address in the API's memory. That's right for a single instance and
needs no extra service. With several instances, each would count on its own, and the counters would
have to move to a shared store.

## Leaving the database alone

The database may scale to zero when idle, and is billed for the time it's awake. So an idle API
stays quiet: the connection pool empties, expired sessions are removed once a day, and health checks
that ask the database run rarely.

**The cost:** the first request after a quiet spell waits for the database to wake.

## Soft delete for accounts

Deleting an account removes the name, picture and GitHub link at once, and ends the sessions.
Answers and contributions stay, attached to a nameless account, so concepts and questions that
others rely on don't disappear.

## One word for each thing, gym words stay in the web app

The gym theme is a matter of presentation. The API uses neutral words, so it reads like any other
API, and the theme can change without touching the backend.
