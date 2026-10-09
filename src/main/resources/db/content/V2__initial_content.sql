-- The gym's content at launch: the house account that owns it, the muscles and the exercises.
-- GENERATED from the muscle files by content/tools/build-migration.pl: don't edit it by hand.
-- Run by LaunchContentMigrationTest on a fresh database; the other tests use db/migration only.

-- Owns the muscles and exercises the gym starts with. Nobody can sign in as it: it has no
-- GitHub identity. Staff curate its content like anyone else's.
INSERT INTO app_user (display_name, user_role, user_status) VALUES ('Heaped & Stacked', 'STAFF', 'ACTIVE');
CREATE TEMPORARY TABLE house ON COMMIT DROP AS
    SELECT id FROM app_user WHERE display_name = 'Heaped & Stacked';

INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'databases', 'Databases & SQL', 'Relational databases independent of any framework: transactions and isolation, locking, SQL querying, data modelling, indexes and query plans, pagination, schema migrations, scaling, connection pools, and when a NoSQL store fits better.

Covers: isolation and MVCC · locking and race-free patterns · SQL querying · NULL · data modelling · indexes · query plans · pagination · schema migrations · scaling · connection pools · SQL vs NoSQL · PostgreSQL specifics · SQL injection', '["https://www.postgresql.org/docs/current/transaction-iso.html", "https://www.postgresql.org/docs/current/indexes.html", "https://www.postgresql.org/docs/current/using-explain.html", "https://use-the-index-luke.com/"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'devops', 'DevOps & Production', 'Getting services into production and keeping them healthy: Docker and Kubernetes, CI/CD, deployment strategies, zero-downtime releases, logging, metrics, tracing, SLOs and incident handling.

Covers: Docker · Kubernetes · twelve-factor apps · CI/CD · deployment strategies · zero-downtime releases · build tools · cloud basics · logging · metrics · tracing · SLOs and alerting · incident debugging · postmortems', '["https://12factor.net/", "https://kubernetes.io/docs/concepts/", "https://opentelemetry.io/docs/concepts/observability-primer/", "https://sre.google/sre-book/service-level-objectives/"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'distributed-systems', 'Distributed Systems', 'The theory behind every distributed design, independent of any technology: partial failure, CAP and consistency models, sagas, idempotency, time and ordering, replication and consensus, and distributed locks.

Covers: partial failure · CAP and consistency models · distributed transactions and sagas · idempotency · time and ordering · replication and consensus · distributed locks · consistent hashing and load balancing', '["https://learn.microsoft.com/en-us/azure/architecture/patterns/saga", "https://raft.github.io/", "https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html", "https://en.wikipedia.org/wiki/CAP_theorem"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'java-concurrency', 'Concurrency & Multithreading', 'Correct and efficient multi-threaded Java: the memory model, locks and atomics, executors and CompletableFuture, concurrent collections, virtual threads, and concurrency inside Spring applications.

Covers: threads and executors · the Java Memory Model · locking · atomics · concurrent collections · coordination · CompletableFuture · virtual threads · ThreadLocal and context · concurrency in Spring apps', '["https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html", "https://openjdk.org/jeps/444"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'java-core', 'Core Java', 'The Java language and its core APIs at interview depth: the object contract, immutability, generics, collections, exceptions, functional style and modern Java (records, sealed types, pattern matching).

Covers: the object contract · immutability · numbers and boxing · strings · generics · collections · functional style and streams · exceptions · modern Java · designing Java APIs', '["https://dev.java/learn/", "https://docs.oracle.com/javase/specs/jls/se21/html/index.html", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'java-performance', 'JVM & Performance Tuning', 'How the JVM runs your code and how to make it fast and stable: class loading, the JIT, memory areas, garbage collectors, tuning in containers, memory leaks, profiling and diagnostics.

Covers: class loading · the JIT · memory areas · garbage collectors · tuning and containers · memory leaks and OOMs · diagnostics · performance method · startup and footprint', '["https://docs.oracle.com/en/java/javase/21/gctuning/", "https://docs.oracle.com/en/java/javase/21/troubleshoot/", "https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'messaging', 'Messaging & Event Streaming', 'Asynchronous communication between services with Kafka and RabbitMQ: delivery guarantees, ordering, failure handling, the outbox pattern, event design and schemas, event sourcing and CQRS.

Covers: why messaging · Kafka''s model · RabbitMQ''s model · delivery guarantees · ordering · failure handling and dead letters · dual writes and the outbox · event design and schemas · event-driven patterns · Spring for Kafka', '["https://kafka.apache.org/intro", "https://kafka.apache.org/documentation/#semantics", "https://www.rabbitmq.com/tutorials/amqp-concepts", "https://learn.microsoft.com/en-us/azure/architecture/patterns/event-sourcing"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'microservices', 'Microservices & Resilience', 'Architecting systems out of services and keeping them up: boundaries and data ownership, communication, gateways, migrating from a monolith, and the resilience patterns that stop failures from spreading (timeouts, retries, circuit breakers, bulkheads, rate limiting).

Covers: monolith vs microservices · service boundaries · data ownership · communication · gateways · discovery · migrating · cross-cutting concerns · timeouts · retries · circuit breakers · bulkheads · rate limiting · load shedding · health checks · testing for failure', '["https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/microservices", "https://learn.microsoft.com/en-us/azure/architecture/patterns/", "https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'software-design', 'Software Design & Architecture', 'Code and structures that stay easy to change: SOLID, design patterns, refactoring, layered and hexagonal architecture, domain-driven design and designing for testability.

Covers: SOLID · design patterns · clean code and refactoring · application architecture · tactical DDD · modules · anti-patterns · designing for testability', '["https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/", "https://refactoring.guru/design-patterns", "https://learn.microsoft.com/en-us/azure/architecture/microservices/model/tactical-ddd"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-boot', 'Spring Boot', 'Spring Boot''s conventions and how to control them: auto-configuration, starters, externalized configuration, Actuator, the embedded server, packaging and building your own starters.

Covers: auto-configuration · starters · externalized configuration · startup and shutdown · Actuator · the embedded server · packaging · custom starters · developer experience', '["https://docs.spring.io/spring-boot/reference/using/auto-configuration.html", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-core', 'Spring Framework Internals', 'Spring Framework internals: the IoC container and dependency injection, bean lifecycle and scopes, proxies and AOP, events, and the environment, properties and conditions behind configuration.

Covers: IoC and dependency injection · the context and configuration · resolving dependencies · bean lifecycle · scopes · proxies and AOP · proxy-based annotations · events · environment and conditions', '["https://docs.spring.io/spring-framework/reference/core/beans/introduction.html", "https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html", "https://docs.spring.io/spring-framework/reference/core/aop.html"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-data', 'Spring Data & JPA', 'Persisting objects with JPA, Hibernate and Spring Data, and running them in transactions with Spring: the persistence context, mapping, fetching and N+1, repositories, @Transactional, propagation and transaction boundaries.

Covers: the persistence context · mapping · identity · fetching and N+1 · lazy loading · repositories · caches · batching · optimistic locking · @Transactional · rollback rules · propagation · programmatic transactions · transaction boundaries · after-commit side effects', '["https://docs.spring.io/spring-data/jpa/reference/", "https://docs.spring.io/spring-framework/reference/data-access/transaction.html", "https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-security', 'Application Security with Spring', 'Securing Spring applications: authentication and authorization, Spring Security''s architecture, passwords, OAuth2 and OpenID Connect, JWTs, browser threats, the OWASP Top 10 and secrets.

Covers: authentication and authorization · Spring Security''s architecture · passwords · OAuth2 and OpenID Connect · JWT · authorization in practice · browser security · the OWASP Top 10 · secrets · service-to-service security', '["https://docs.spring.io/spring-security/reference/servlet/architecture.html", "https://owasp.org/www-project-top-ten/", "https://www.rfc-editor.org/rfc/rfc9700"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-testing', 'Testing Java & Spring Apps', 'Tests that give confidence at a sensible cost: the test pyramid, JUnit and Mockito, Spring''s test slices, Testcontainers, contract tests, TDD, and fighting flaky and slow suites.

Covers: strategy and the pyramid · JUnit · test doubles and Mockito · Spring''s test context and slices · Testcontainers · contract tests · TDD · non-functional tests · flaky and slow tests', '["https://docs.spring.io/spring-boot/reference/testing/index.html", "https://docs.junit.org/current/user-guide/", "https://java.testcontainers.org/"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'spring-web', 'REST APIs with Spring', 'Designing and building HTTP APIs with Spring: HTTP semantics, resource design, Spring MVC internals, validation, error handling, JSON, API evolution, idempotency and HTTP clients.

Covers: HTTP semantics · resource design · Spring MVC internals · validation · error handling · JSON with Jackson · API evolution · ETags and idempotency · HTTP clients · MVC vs WebFlux', '["https://www.rfc-editor.org/rfc/rfc9110", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html", "https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;
INSERT INTO concept (tag, name, description, external_references, concept_status, contribution_policy, created_by, updated_by) SELECT 'system-design', 'System Design & Scalability', 'Designing whole systems from requirements to bottlenecks: estimates, scaling services and data, caching, asynchronous processing, storage choices, availability, and classic interview designs.

Covers: the method · estimates · scaling services and data · load balancing · asynchronous processing · storage choices · availability · classic designs · caching layers and strategies · invalidation · Redis · HTTP caching', '["https://github.com/donnemartin/system-design-primer", "https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching", "https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/data-store-overview"]'::jsonb, 'ACTIVE', 'OWNER_APPROVAL', id, id FROM house;

WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: ACID$hs$, $hs$What do the four letters of ACID stand for?$hs$, $hs$- Atomicity: all of a transaction's changes are applied, or none are.
- Consistency: a transaction takes the database from one valid state to another; constraints hold.
- Isolation: concurrent transactions don't see each other's uncommitted changes.
- Durability: once a commit is confirmed, its changes survive a crash.$hs$, 1, 'SUBMITTED', $hs$PostgreSQL docs, Transactions (tutorial) https://www.postgresql.org/docs/current/tutorial-transactions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: a transfer between two accounts$hs$, $hs$The database crashes right after the first `UPDATE`. What state are the accounts in after restart, and which ACID property guarantees it?

```sql
BEGIN;
UPDATE account SET balance = balance - 100.00 WHERE id = 1;
UPDATE account SET balance = balance + 100.00 WHERE id = 2;
COMMIT;
```$hs$, $hs$Both balances are as they were before the transfer: the transaction never committed, so its first update is rolled back during recovery.

Atomicity guarantees it: the two updates are applied together or not at all, so money is never debited without being credited.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Transactions (tutorial) https://www.postgresql.org/docs/current/tutorial-transactions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: the four standard levels$hs$, $hs$Name the four isolation levels of the SQL standard, from weakest to strongest.$hs$, $hs$1. Read Uncommitted
2. Read Committed
3. Repeatable Read
4. Serializable

Each level allows fewer read phenomena than the one before; Serializable makes concurrent transactions behave as if they ran one after another.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: dirty, non-repeatable and phantom reads$hs$, $hs$What are a dirty read, a non-repeatable read and a phantom read?$hs$, $hs$- Dirty read: a transaction reads changes another transaction has made but not yet committed.
- Non-repeatable read: a transaction reads the same row twice and gets different values, because another transaction updated it and committed in between.
- Phantom read: a transaction runs the same query twice and gets a different set of rows, because another transaction inserted or deleted matching rows and committed in between.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: the defaults of PostgreSQL and MySQL$hs$, $hs$Which isolation level does PostgreSQL use by default, and which does MySQL's InnoDB use? Which read phenomena does PostgreSQL's default still allow?$hs$, $hs$- PostgreSQL: Read Committed. Each statement sees the data committed before it started, so dirty reads are impossible, but non-repeatable reads and phantom reads can happen within a transaction.
- MySQL InnoDB: Repeatable Read.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: MVCC$hs$, $hs$What is multi-version concurrency control (MVCC), and what does it give PostgreSQL?$hs$, $hs$Every update writes a new version of the row instead of overwriting it, and each transaction (or statement) reads a consistent snapshot: the versions committed when it started.

As a result, reading never blocks writing and writing never blocks reading. The old versions are cleaned up later by `VACUUM`.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Introduction to Concurrency Control (MVCC) https://www.postgresql.org/docs/current/mvcc-intro.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: a lost update on a balance$hs$, $hs$Two transactions at Read Committed run this at the same time for account 1, whose balance is 100. Each deposits 10. What is the final balance, and how do you fix it?

```java
BigDecimal balance = jdbc.queryForObject("SELECT balance FROM account WHERE id = 1", BigDecimal.class);
jdbc.update("UPDATE account SET balance = ? WHERE id = 1", balance.add(TEN));
```$hs$, $hs$Usually 110, not 120: both read 100, both write 110. The second write overwrites the first without having seen it: a lost update.

```java
jdbc.update("UPDATE account SET balance = balance + 10 WHERE id = 1");   // read and write in one statement
```

If the logic needs the value first: `SELECT ... FOR UPDATE` to lock the row before reading it, or optimistic locking with a version column.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: write skew$hs$, $hs$A couple's two linked accounts may together never go below zero. Two withdrawals run concurrently at Repeatable Read, one from each account: each checks that the combined balance covers it, then updates only its own account. Both succeed, and the combined balance ends up negative. What is this anomaly called, and which isolation level prevents it?$hs$, $hs$Write skew: two transactions read the same data, each makes a decision based on it, and they write to different rows, so neither conflicts with the other's write, but together they break the rule.

Only Serializable prevents it: PostgreSQL detects the dangerous pattern and aborts one transaction with a serialization failure, which must be retried. At lower levels, lock the rows the check depends on (`SELECT ... FOR UPDATE` on both accounts).$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation (Serializable) https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: Repeatable Read and a concurrent update$hs$, $hs$Two Repeatable Read transactions in PostgreSQL both update the same account row. The first commits. What happens to the second?$hs$, $hs$Its update waits for the first transaction, then fails with `ERROR: could not serialize access due to concurrent update`, because the row changed after its snapshot was taken. The application must retry the whole transaction.

So at Repeatable Read, PostgreSQL turns a lost update into an error instead of silently losing data.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation (Repeatable Read) https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: what Serializable asks of the application$hs$, $hs$Two transactions run at `SERIALIZABLE` in PostgreSQL, and together they would produce a result no serial order could. What happens to one of them, and what must the application therefore always do around a serializable transaction?$hs$, $hs$PostgreSQL aborts one of them with a serialization failure (SQLSTATE `40001`, "could not serialize access due to read/write dependencies among transactions"), at a statement or at commit.

The application must catch that error and retry the whole transaction from the start, re-reading everything: any transaction may fail this way, even read-only ones. Under contention retries become frequent and throughput drops, which is why most systems run at Read Committed and use Serializable only where an invariant depends on what was read.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation (Serializable Isolation Level) https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Isolation: two admins demote each other$hs$, $hs$The rule is "there is always at least one active admin". There are exactly two admins, A and B, and at the same moment A demotes B and B demotes A, on PostgreSQL at READ COMMITTED. What is the end state, why doesn't REPEATABLE READ prevent it, and how do you fix it?

```java
@Transactional
public void demote(long userId) {
    if (users.countActiveAdmins() <= 1) {        // SELECT count(*) FROM app_user WHERE role = 'ADMIN' AND status = 'ACTIVE'
        throw new LastAdminException();
    }
    users.updateRole(userId, Role.MEMBER);       // UPDATE app_user SET role = 'MEMBER' WHERE id = ?
}
```$hs$, $hs$Both transactions count 2 admins, each demotes a different row, and both commit: there are no admins left. This anomaly is **write skew**: two transactions read the same data, then each updates a different row, so no write conflicts with the other, yet together they break an invariant that each checked.

REPEATABLE READ (snapshot isolation) doesn't help: each transaction still sees a snapshot in which the other's demotion is invisible, and the updates touch different rows.

The fix is to lock the rows the decision depends on before counting them:

```sql
SELECT id FROM app_user WHERE role = 'ADMIN' AND status = 'ACTIVE' FOR UPDATE;   -- count the ids in Java
```

(`FOR UPDATE` can't be combined with `count(*)`.) The second transaction waits for the first; when it resumes, READ COMMITTED re-checks the locked rows, the demoted admin no longer matches, so it counts 1 and refuses. Alternatives: the SERIALIZABLE isolation level, retrying on serialization failures (SQLSTATE 40001), or an advisory lock around changes to admin rights.$hs$, 8, 'SUBMITTED', $hs$PostgreSQL docs, Explicit Locking (row-level locks) https://www.postgresql.org/docs/current/explicit-locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: SELECT ... FOR UPDATE$hs$, $hs$What does `SELECT ... FOR UPDATE` do to the rows it returns?$hs$, $hs$It locks them until the transaction ends. Other transactions that try to update, delete or lock those rows (including with `FOR UPDATE`) wait until then. Plain reads are not blocked.

```sql
BEGIN;
SELECT balance FROM account WHERE id = 1 FOR UPDATE;   -- account 1 is locked until COMMIT
UPDATE account SET balance = balance - 50 WHERE id = 1;
COMMIT;
```$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Explicit Locking (row-level locks) https://www.postgresql.org/docs/current/explicit-locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: a withdrawal that can't overdraw$hs$, $hs$Withdraw 50 from account 1 only if the balance is at least 50, safely under concurrent withdrawals, in a single statement. How does the application know whether it worked?$hs$, $hs$```sql
UPDATE account
SET balance = balance - 50
WHERE id = 1 AND balance >= 50;
```

The check and the update happen atomically on the locked row: a concurrent withdrawal waits, then re-checks the condition against the new balance. The affected row count tells the result: 1 means withdrawn, 0 means insufficient funds (or no such account).$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, UPDATE https://www.postgresql.org/docs/current/sql-update.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: deadlocks between transfers$hs$, $hs$Transfer A moves money from account 1 to account 2 while transfer B moves money from 2 to 1; each updates the debited account first. Sometimes one of them fails with `deadlock detected`. Why, and how do you prevent it?$hs$, $hs$A locks row 1 and waits for row 2; B locks row 2 and waits for row 1. PostgreSQL detects the cycle and aborts one transaction, which the application must retry.

Prevent it by locking the rows in the same order in every transaction, e.g. by account id, before updating them:

```sql
BEGIN;
SELECT id FROM account WHERE id IN (1, 2) ORDER BY id FOR UPDATE;   -- always lowest id first
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;
```

Short transactions reduce deadlocks further.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Explicit Locking (deadlocks) https://www.postgresql.org/docs/current/explicit-locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: a job queue with SKIP LOCKED$hs$, $hs$Several worker instances process pending payments from a `payment` table in PostgreSQL. Each payment must go to exactly one worker, and workers must not wait for each other. Write the query a worker uses to claim 10 payments. Is the clause you use standard SQL?$hs$, $hs$```sql
BEGIN;
SELECT id FROM payment
WHERE status = 'PENDING'
ORDER BY created_at
LIMIT 10
FOR UPDATE SKIP LOCKED;          -- rows locked by another worker are skipped, not waited for
UPDATE payment SET status = 'PROCESSING' WHERE id IN (...);
COMMIT;
```

Each worker locks a different set of rows, so no payment is claimed twice and no worker blocks.

`SKIP LOCKED` isn't standard SQL, but it is widely supported: PostgreSQL (9.5+), MySQL (8.0+) and Oracle have it, and SQL Server gets the same effect with the `READPAST` table hint. Standard SQL has only `FOR UPDATE`, where a second worker waits for the first. Without `SKIP LOCKED`, claim rows optimistically instead: `UPDATE payment SET status = 'PROCESSING', worker = ? WHERE id = ? AND status = 'PENDING'`, and treat an update count of 0 as "another worker got it first".$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, SELECT (the locking clause, SKIP LOCKED) https://www.postgresql.org/docs/current/sql-select.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Constraints: duplicate transfer requests$hs$, $hs$Each transfer request carries a client-generated `request_id`. To avoid executing a retried request twice, the code checks `SELECT 1 FROM transfer WHERE request_id = ?` and inserts only if nothing is found. Why can duplicates still appear, and what is the fix?$hs$, $hs$Check-then-insert is a race: two copies of the request can both run the check before either inserts, and both insert.

Let the database enforce it with a unique constraint, and treat a violation as "already done":

```sql
ALTER TABLE transfer ADD CONSTRAINT uk_transfer_request UNIQUE (request_id);

INSERT INTO transfer (request_id, from_account, to_account, amount)
VALUES (?, ?, ?, ?)
ON CONFLICT (request_id) DO NOTHING;   -- 0 rows inserted: a duplicate, return the original result
```$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Constraints (unique constraints) https://www.postgresql.org/docs/current/ddl-constraints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: an upsert$hs$, $hs$A `daily_volume(account_id, day, total)` table sums each account's transfers per day. Add an amount to today's total, creating the row if it doesn't exist yet, atomically, in one PostgreSQL statement.$hs$, $hs$```sql
INSERT INTO daily_volume (account_id, day, total)
VALUES (:accountId, current_date, :amount)
ON CONFLICT (account_id, day)
DO UPDATE SET total = daily_volume.total + EXCLUDED.total;
```

`ON CONFLICT` needs a unique constraint or primary key on `(account_id, day)`. `EXCLUDED` refers to the row that was proposed for insertion.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, INSERT (ON CONFLICT) https://www.postgresql.org/docs/current/sql-insert.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: a block undone by a sign-in$hs$, $hs$Every change to a user goes through one repository method that writes the whole row back. An admin blocks a member at the same moment as the member signs in, and a minute later the member is signed in and ACTIVE again. How can that happen at PostgreSQL's default isolation level, and how do you prevent it?

```java
@Transactional
public User signIn(long id) {
    User user = users.findById(id).orElseThrow();          // SELECT * FROM app_user WHERE id = ?
    if (user.getStatus() != Status.ACTIVE) throw new AccountBlockedException(id);
    return users.update(user.withLastLoginAt(Instant.now()));
}

@Transactional
public User block(long id) {
    User user = users.findById(id).orElseThrow();
    return users.update(user.withStatus(Status.BLOCKED));
}

// users.update: UPDATE app_user SET display_name = ?, role = ?, status = ?, last_login_at = ? WHERE id = ?
```$hs$, $hs$This is a **lost update**: two transactions read the same row, and the later write overwrites the earlier one with values based on a stale read.

1. The sign-in reads the row (status ACTIVE) and passes its check.
2. The block reads the row, writes BLOCKED and commits.
3. The sign-in writes its whole copy back, including `status = ACTIVE`, and commits. At READ COMMITTED its `UPDATE` waits for the block's row lock, then overwrites the committed row: the block is gone.

Two standard fixes:
- **Optimistic locking:** add a version column (or compare `updated_at`) and write `UPDATE ... WHERE id = ? AND version = ?`. If no row matches, someone changed it meanwhile: retry or answer 409. With JPA this is `@Version`.
- **Targeted, guarded updates:** each use case writes only its own columns and puts its precondition in the statement, e.g. `UPDATE app_user SET last_login_at = now() WHERE id = ? AND status = 'ACTIVE'`, then checks the affected-row count.

(`SELECT ... FOR UPDATE` when reading also works, at the cost of holding the row lock for the whole transaction.)$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation (Read Committed) https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: withdrawing feedback that was just decided$hs$, $hs$Members may withdraw their feedback on an exercise only while it is still OPEN; once a coach has resolved it, it stays as history. A coach resolves the feedback at the same moment as the member withdraws it. Is "only while OPEN" really enforced on PostgreSQL at READ COMMITTED, and what is the smallest fix that needs no explicit lock?

```java
@Transactional
public void withdraw(long exerciseId, long memberId) {
    Feedback feedback = feedbacks.findOpen(exerciseId, memberId)   // ... WHERE status = 'OPEN'
            .orElseThrow(NoOpenFeedbackException::new);
    feedbacks.delete(feedback.getId());                          // DELETE FROM feedback WHERE id = ?
}

// The coach, concurrently: UPDATE feedback SET status = 'RESOLVED', resolved_by = ? WHERE id = ?
```$hs$, $hs$No. The check and the write are separate statements, a **check-then-act** race: if the coach's update commits between them, `DELETE ... WHERE id = ?` still matches the row, and a resolved piece of feedback disappears from the history.

The fix is to put the condition into the write itself and check how many rows it affected:

```java
int deleted = jdbc.update("DELETE FROM feedback WHERE id = ? AND status = 'OPEN'", feedback.getId());
if (deleted == 0) throw new NoOpenFeedbackException();   // decided in the meantime
```

This is race-free without `FOR UPDATE`: at READ COMMITTED, a write that has waited for another transaction's row lock re-evaluates its `WHERE` clause against the newly committed version of the row. It sees RESOLVED and skips the row. The first check can stay, for a friendly message in the common case.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Transaction Isolation (Read Committed) https://www.postgresql.org/docs/current/transaction-iso.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: INNER JOIN vs LEFT JOIN$hs$, $hs$Which customers does each query return, if some customers have no account?

```sql
SELECT c.name, a.iban FROM customer c JOIN account a ON a.customer_id = c.id;
SELECT c.name, a.iban FROM customer c LEFT JOIN account a ON a.customer_id = c.id;
```$hs$, $hs$```sql
SELECT c.name, a.iban FROM customer c JOIN account a ON a.customer_id = c.id;
-- only customers with at least one account, one row per account

SELECT c.name, a.iban FROM customer c LEFT JOIN account a ON a.customer_id = c.id;
-- every customer; those without an account appear once, with NULL for iban
```$hs$, 1, 'SUBMITTED', $hs$PostgreSQL docs, Joins Between Tables https://www.postgresql.org/docs/current/tutorial-join.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: WHERE vs HAVING$hs$, $hs$This query lists accounts that made more than 10 transfers in 2026. What does `WHERE` filter, and what does `HAVING` filter?

```sql
SELECT account_id, count(*) AS transfers
FROM transfer
WHERE created_at >= DATE '2026-01-01'
GROUP BY account_id
HAVING count(*) > 10;
```$hs$, $hs$`WHERE` filters rows before grouping; `HAVING` filters groups after aggregation.

```sql
SELECT account_id, count(*) AS transfers
FROM transfer
WHERE created_at >= DATE '2026-01-01'   -- rows: only transfers in 2026
GROUP BY account_id
HAVING count(*) > 10;                   -- groups: only accounts with more than 10 of them
```

`WHERE` can't use aggregates, since the groups don't exist yet.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Aggregate Functions https://www.postgresql.org/docs/current/tutorial-agg.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: a LEFT JOIN that loses rows$hs$, $hs$This query should list every account with its number of transfers in 2026, including accounts with none. Accounts without transfers are missing. Why, and how do you fix it?

```sql
SELECT a.id, count(t.id) AS transfers_2026
FROM account a
LEFT JOIN transfer t ON t.account_id = a.id
WHERE t.created_at >= DATE '2026-01-01'
GROUP BY a.id;
```$hs$, $hs$A `LEFT JOIN` keeps every row of the left table and fills the right table's columns with `NULL` where no row matches. The `WHERE` clause is applied after the join, so it filters those rows out again: for an account without transfers, `t.created_at` is `NULL`, and `NULL >= DATE '2026-01-01'` evaluates to unknown, not true. The outer join effectively becomes an inner join.

A condition on the right-hand table belongs in the `ON` clause, where it restricts which rows are joined rather than which rows are returned:

```sql
SELECT a.id, count(t.id) AS transfers_2026
FROM account a
LEFT JOIN transfer t ON t.account_id = a.id
                    AND t.created_at >= DATE '2026-01-01'   -- filters the joined rows, not the result
GROUP BY a.id;
```

`count(t.id)` counts only non-NULL values, so accounts without transfers get 0.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Joins Between Tables https://www.postgresql.org/docs/current/tutorial-join.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: accounts without any transaction$hs$, $hs$Find the accounts that have never had a transaction.$hs$, $hs$```sql
SELECT a.id
FROM account a
WHERE NOT EXISTS (
    SELECT 1 FROM ledger_entry e WHERE e.account_id = a.id
);
```

An anti join: equivalently, `LEFT JOIN ledger_entry e ON e.account_id = a.id WHERE e.id IS NULL`. Prefer `NOT EXISTS` over `NOT IN`, which returns no rows at all if the subquery contains a `NULL`.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Subquery Expressions (EXISTS) https://www.postgresql.org/docs/current/functions-subquery.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: the latest transaction per account$hs$, $hs$Return, for each account, its most recent ledger entry.$hs$, $hs$```sql
SELECT account_id, id, amount, created_at
FROM (
    SELECT e.*, row_number() OVER (PARTITION BY account_id ORDER BY created_at DESC, id DESC) AS rn
    FROM ledger_entry e
) ranked
WHERE rn = 1;
```

In PostgreSQL, `SELECT DISTINCT ON (account_id) ... ORDER BY account_id, created_at DESC` does the same more briefly. An index on `(account_id, created_at)` supports both.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Window Functions https://www.postgresql.org/docs/current/tutorial-window.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: a running balance$hs$, $hs$The ledger stores only money movements, not balances: each entry has a signed `amount` (+ for money in, − for money out). Write the query for account 42's statement: every entry in order, each with the account's balance after it.

```sql
CREATE TABLE ledger_entry (
    id         BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount     NUMERIC(19, 4) NOT NULL,   -- signed: + credit, − debit
    created_at TIMESTAMPTZ NOT NULL
);
```$hs$, $hs$```sql
SELECT created_at, amount,
       sum(amount) OVER (ORDER BY created_at, id) AS balance_after
FROM ledger_entry
WHERE account_id = 42
ORDER BY created_at, id;
```

A window `sum` with an `ORDER BY` is a running total: for each row it adds up all entries up to and including that row, so it gives the balance after each entry. `id` breaks ties between entries with the same timestamp, so the order is deterministic. Window functions are standard SQL (PostgreSQL, Oracle, SQL Server and MySQL 8 all have them).$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Window Functions https://www.postgresql.org/docs/current/tutorial-window.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: a CTE vs a subquery$hs$, $hs$What does a common table expression (`WITH ...`) offer over a subquery, and is it slower in PostgreSQL?$hs$, $hs$A CTE names an intermediate result, so a complex query reads as a series of steps, and a CTE can reference itself (`WITH RECURSIVE`), which a subquery can't.

Not usually: since PostgreSQL 12, a simple CTE referenced once is inlined into the main query and optimised like a subquery. (`MATERIALIZED` forces it to be computed separately.)$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, WITH Queries (Common Table Expressions) https://www.postgresql.org/docs/current/queries-with.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: count(*) vs count(column)$hs$, $hs$The `customer` table has 10 rows; 3 customers have no email (`email` is `NULL`). In standard SQL, which every relational database follows here, what does this query return?

```sql
SELECT count(*), count(email) FROM customer;
```$hs$, $hs$`10` and `7`.

- `count(*)` counts rows, whatever their values.
- `count(expression)` counts the rows for which the expression is not `NULL`.

This is defined by the SQL standard, so PostgreSQL, MySQL, Oracle and SQL Server behave the same way. The same rule applies to the other aggregate functions: `sum`, `avg`, `min` and `max` ignore `NULL` values.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Aggregate Functions https://www.postgresql.org/docs/current/functions-aggregate.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: comparing with = NULL$hs$, $hs$Why does this return no rows, although some accounts have no closing date?

```sql
SELECT * FROM account WHERE closed_at = NULL;
```$hs$, $hs$Any comparison with `NULL` yields `NULL` (unknown), never true, so no row passes the `WHERE`.

```sql
SELECT * FROM account WHERE closed_at IS NULL;
```$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Comparison Functions and Operators https://www.postgresql.org/docs/current/functions-comparison.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: NOT IN with a NULL in the subquery$hs$, $hs$This query returns no rows, although some customers have no account. Why?

```sql
SELECT * FROM customer WHERE id NOT IN (SELECT customer_id FROM account);
```$hs$, $hs$At least one `account.customer_id` is `NULL`. `id NOT IN (1, 2, NULL)` means `id <> 1 AND id <> 2 AND id <> NULL`, and `id <> NULL` is unknown, so the whole condition is never true, for any customer.

```sql
SELECT * FROM customer c
WHERE NOT EXISTS (SELECT 1 FROM account a WHERE a.customer_id = c.id);   -- not affected by NULLs
```$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Subquery Expressions (NOT IN) https://www.postgresql.org/docs/current/functions-subquery.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: monthly totals per account$hs$, $hs$Return, for account 42, the total of its ledger entries per calendar month of 2026, oldest month first.

```sql
CREATE TABLE ledger_entry (
    id         BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount     NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
```$hs$, $hs$```sql
SELECT date_trunc('month', created_at) AS month,
       sum(amount)                    AS total
FROM ledger_entry
WHERE account_id = 42
  AND created_at >= '2026-01-01' AND created_at < '2027-01-01'
GROUP BY month
ORDER BY month;
```

`date_trunc('month', ...)` maps every timestamp to the first instant of its month, so grouping by it gives one row per month. The half-open range on `created_at` (rather than a function on the column) keeps an index on `(account_id, created_at)` usable.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Date/Time Functions (date_trunc) https://www.postgresql.org/docs/current/functions-datetime.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: the top three athletes per gym$hs$, $hs$For each gym, return the three athletes with the highest total weight lifted, with their totals. If two athletes tie for third place, return both.

```sql
CREATE TABLE workout_set (
    athlete_id BIGINT NOT NULL,
    gym_id     BIGINT NOT NULL,
    weight     NUMERIC(6, 2) NOT NULL,
    reps       INT NOT NULL
);
```

The total weight of a set is `weight * reps`.$hs$, $hs$```sql
SELECT gym_id, athlete_id, total
FROM (
    SELECT gym_id, athlete_id, sum(weight * reps) AS total,
           rank() OVER (PARTITION BY gym_id ORDER BY sum(weight * reps) DESC) AS place
    FROM workout_set
    GROUP BY gym_id, athlete_id
) ranked
WHERE place <= 3
ORDER BY gym_id, place;
```

- The window function runs after `GROUP BY`, so it can rank the sums.
- `rank()` gives tied rows the same place, so a tie for third returns both; `row_number()` would cut one of them off.
- A window function can't be used in `WHERE` directly, hence the subquery.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Window Functions https://www.postgresql.org/docs/current/tutorial-window.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: transfers submitted twice$hs$, $hs$Find suspected duplicate transfers: a transfer with the same `from_account`, `to_account` and `amount` as the previous transfer between those accounts, created less than one minute after it.

```sql
CREATE TABLE transfer (
    id           BIGINT PRIMARY KEY,
    from_account BIGINT NOT NULL,
    to_account   BIGINT NOT NULL,
    amount       NUMERIC(19, 4) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL
);
```$hs$, $hs$```sql
SELECT id, from_account, to_account, amount, created_at
FROM (
    SELECT t.*,
           lag(amount)     OVER pair AS previous_amount,
           lag(created_at) OVER pair AS previous_at
    FROM transfer t
    WINDOW pair AS (PARTITION BY from_account, to_account ORDER BY created_at, id)
) ordered
WHERE amount = previous_amount
  AND created_at - previous_at < interval '1 minute';
```

`lag` reads the previous row of the same partition, so each transfer is compared with the one before it between the same two accounts; the first transfer of a pair has no previous row (`NULL`) and is never reported.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Window Functions (lag) https://www.postgresql.org/docs/current/functions-window.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SQL: filtering by one tag, returning them all$hs$, $hs$The search "exercises tagged `core`" must list each matching exercise with all of its tags. What is wrong with this query's result, and how do you fix it?

```sql
SELECT e.id, e.name, array_agg(t.tag ORDER BY t.tag) AS tags
FROM exercise e
LEFT JOIN exercise_tag t ON t.exercise_id = e.id
WHERE t.tag = 'core'
GROUP BY e.id, e.name
ORDER BY e.id;
```$hs$, $hs$`WHERE` filters the joined rows before they are grouped, so only the `core` row survives for each exercise: `tags` is always `{core}`. (A condition on the right-hand table in `WHERE` also turns the `LEFT JOIN` into an inner join.)

Filter the exercises, not the joined rows, so the aggregate still sees every tag:

```sql
SELECT e.id, e.name, array_agg(t.tag ORDER BY t.tag) AS tags
FROM exercise e
LEFT JOIN exercise_tag t ON t.exercise_id = e.id
WHERE EXISTS (SELECT 1 FROM exercise_tag f WHERE f.exercise_id = e.id AND f.tag = 'core')
GROUP BY e.id, e.name
ORDER BY e.id;
```

`HAVING bool_or(t.tag = 'core')` gives the same result, filtering after grouping.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Table Expressions (WHERE, GROUP BY, HAVING) https://www.postgresql.org/docs/current/queries-table-expressions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: primary key vs foreign key$hs$, $hs$In this schema, what do the primary key and the foreign key each guarantee?

```sql
CREATE TABLE customer (id BIGINT PRIMARY KEY, name TEXT NOT NULL);
CREATE TABLE account (
    id          BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer (id)
);
```$hs$, $hs$- A **primary key** (`customer.id`, `account.id`) uniquely identifies each row of its table: its value is unique and never `NULL`.
- A **foreign key** (`account.customer_id`) enforces referential integrity: every value must match an existing row of the referenced table. Inserting an account for a missing customer fails, and so does deleting a customer who still has accounts, unless the foreign key specifies an action such as `ON DELETE CASCADE`.$hs$, 1, 'SUBMITTED', $hs$PostgreSQL docs, Constraints https://www.postgresql.org/docs/current/ddl-constraints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: storing money$hs$, $hs$Which column type should hold an account balance in PostgreSQL, and why not `double precision`?$hs$, $hs$`numeric` with a fixed scale, e.g. `numeric(19, 4)`, which stores decimal values exactly; or a `bigint` of minor units (cents).

`double precision` is binary floating point: values like 0.10 can't be stored exactly, so sums drift by fractions of a cent, which is unacceptable for money.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Numeric Types (arbitrary precision numbers) https://www.postgresql.org/docs/current/datatype-numeric.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Constraints: why they belong in the database$hs$, $hs$The application already validates that a balance is never negative and that every account has a customer. Why also declare `CHECK (balance >= 0)` and the foreign key in the database?$hs$, $hs$- They hold for every writer: other services, scripts, migrations and manual fixes, not just one code path.
- They hold under concurrency: two application checks can pass at the same time and together break the rule; the database checks each write atomically.

Application validation still matters, for clear error messages; the constraint is the last line of defence.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Constraints https://www.postgresql.org/docs/current/ddl-constraints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: the problem normalisation solves$hs$, $hs$Each row of this table stores one transfer, with the customer's name and email copied into it. What problem does this cause, and how does normalisation fix it?

```sql
CREATE TABLE transfer (
    id             BIGINT PRIMARY KEY,
    customer_name  TEXT,
    customer_email TEXT,
    amount         NUMERIC(19, 4)
);
```$hs$, $hs$Redundancy: the customer's details are repeated in every transfer. Changing an email means updating many rows, and missing one leaves the data inconsistent (an update anomaly).

Normalisation stores each fact once, and refers to it by key:

```sql
CREATE TABLE customer (id BIGINT PRIMARY KEY, name TEXT, email TEXT);
CREATE TABLE transfer (
    id          BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customer (id),   -- the details live once, in customer
    amount      NUMERIC(19, 4)
);
```$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Database normalization (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Database_normalization$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: 1NF, 2NF and 3NF$hs$, $hs$State in one sentence each what first, second and third normal form require.$hs$, $hs$- 1NF: every column holds a single atomic value; no repeating groups (no list of phone numbers in one column).
- 2NF: 1NF, and no non-key column depends on only part of a composite primary key.
- 3NF: 2NF, and no non-key column depends on another non-key column (e.g. `branch_city` determined by `branch_id` belongs in a branch table).$hs$, 3, 'SUBMITTED', $hs$Wikipedia, Database normalization (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Database_normalization$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: surrogate vs natural keys$hs$, $hs$Should the `account` table use the IBAN as its primary key, or a generated `id`?$hs$, $hs$A generated surrogate `id` as the primary key, plus a unique constraint on the IBAN.

A natural key such as the IBAN carries meaning, so it can change (an account migrated to another bank) or be formatted inconsistently, and every foreign key would have to change with it. A surrogate key never changes and is compact; the unique constraint still prevents duplicate IBANs.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Constraints https://www.postgresql.org/docs/current/ddl-constraints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: UUIDs vs sequential ids$hs$, $hs$What are the pros and cons of random UUIDs (version 4) as primary keys instead of sequential `bigint` ids?$hs$, $hs$Pros: any service can generate them without asking the database, they're unique across systems (useful when merging or sharding), and they don't reveal how many rows exist.

Cons: they're twice the size of a `bigint`, and random values insert into random places of the B-tree index, which fragments it and hurts cache efficiency. Time-ordered UUIDs (version 7) keep the benefits and insert in order.$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, UUID Type https://www.postgresql.org/docs/current/datatype-uuid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Constraints: one open report per member$hs$, $hs$Members can report a problem with an exercise. Each member may have at most one OPEN report per exercise, but may report it again after a coach has closed the earlier report, and closed reports are kept as history. A plain `UNIQUE (exercise_id, member_id)` forbids reporting again. How do you enforce the rule in the database itself?$hs$, $hs$With a **partial unique index**: a unique index built only over the rows that satisfy a predicate.

```sql
CREATE UNIQUE INDEX uq_report_open_per_member
    ON report (exercise_id, member_id)
    WHERE status = 'OPEN';
```

Uniqueness then applies to open reports only; closed reports for the same pair are not in the index and may repeat. The database enforces the rule even when two requests arrive at the same instant: both may pass an application-level "already reported?" check, but the second insert fails with a unique violation (SQLSTATE 23505), which the application translates into 409 Conflict.

(`UNIQUE (exercise_id, member_id, status)` would be wrong too: it forbids two closed reports.)$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Partial Indexes https://www.postgresql.org/docs/current/indexes-partial.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$PostgreSQL: exporting changes by updated_at$hs$, $hs$A trigger sets `updated_at = now()` on every update of `exercise`. Every minute, a job copies the changes into a search index and then remembers the largest `updated_at` it has seen. Some updates never reach the index. Why?

```sql
SELECT * FROM exercise WHERE updated_at > :lastSeen ORDER BY updated_at;
-- afterwards: lastSeen = max(updated_at) of this batch
```$hs$, $hs$In PostgreSQL, `now()` returns the **start time of the current transaction**, but a row only becomes visible to other transactions when that transaction **commits**.

A transaction that starts at 10:00:00 and commits at 10:00:30 stamps its rows 10:00:00. If the job runs at 10:00:10 and already sees a row stamped 10:00:05 from a shorter transaction, `lastSeen` moves past 10:00:00, and the long transaction's row, committed later with an older stamp, is never selected.

`clock_timestamp()` narrows the gap but still isn't the commit time. Reliable options:
- re-read with an overlap window (`updated_at > :lastSeen - interval '5 minutes'`) and write to the index idempotently;
- or capture changes from the database's log (change data capture, e.g. Debezium) or a transactional outbox table instead of timestamps.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Current Date/Time (now() is the transaction's start time) https://www.postgresql.org/docs/current/functions-datetime.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: deleting an exercise while it is being answered$hs$, $hs$Exercises that have answers must never be deleted. `answer.exercise_id` references `exercise (id)` with `ON DELETE RESTRICT`. On PostgreSQL at READ COMMITTED, an editor deletes exercise 42: the check has already returned false when a member's answer to exercise 42 is inserted, just before the `DELETE`. What happens to each transaction?

```java
@Transactional
public void delete(long id) {
    if (answers.existByExercise(id)) throw new ExerciseAnsweredException(id);   // 409
    exercises.deleteById(id);                                                  // DELETE FROM exercise WHERE id = ?
}
```$hs$, $hs$The foreign key keeps the data consistent; one side fails with an error instead.

1. Inserting the answer checks the foreign key, which locks exercise 42 in **FOR KEY SHARE** mode until the answer's transaction ends.
2. The `DELETE` needs a stronger lock on that row, so it **waits** for the answer's transaction.
3. The answer commits. The `DELETE` proceeds, and the `RESTRICT` check now finds the committed answer: the delete fails with a foreign-key violation (SQLSTATE 23503), and the exercise stays.

So the application's check-then-delete is racy, but the constraint is the real guard. Translate the violation into the same 409 the check gives (in Spring, a `DataIntegrityViolationException`), instead of letting it surface as a 500.

(An edit of the exercise's title would not block the answer: it takes `FOR NO KEY UPDATE`, which is compatible with `FOR KEY SHARE`.)$hs$, 8, 'SUBMITTED', $hs$PostgreSQL docs, Explicit Locking (FOR KEY SHARE) https://www.postgresql.org/docs/current/explicit-locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: how a B-tree index finds rows$hs$, $hs$How does a B-tree index on `account(iban)` find the row for one IBAN, compared with no index?$hs$, $hs$The index keeps the IBANs sorted in a balanced tree, each entry pointing to its row. A lookup walks from the root down to the matching leaf in a few steps (logarithmic in the table size), then reads that one row.

Without an index, the database must read every row of the table (a sequential scan). A B-tree also supports range conditions and `ORDER BY` on its columns.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Index Types (B-Tree) https://www.postgresql.org/docs/current/indexes-types.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: what they cost$hs$, $hs$An index on `ledger_entry(account_id)` speeds up loading an account's entries. What does the index cost?$hs$, $hs$- Slower writes: every `INSERT` and `DELETE`, and every `UPDATE` of `account_id`, must also update the index.
- Disk space and memory for the index itself.
- Maintenance: indexes bloat and need upkeep.

So index the columns that queries actually filter, join or sort on.$hs$, 1, 'SUBMITTED', $hs$PostgreSQL docs, Indexes https://www.postgresql.org/docs/current/indexes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: a composite index and its column order$hs$, $hs$There's an index on `ledger_entry(account_id, created_at)`. Which of these queries can use it efficiently?

```sql
-- 1
SELECT * FROM ledger_entry WHERE account_id = 42;
-- 2
SELECT * FROM ledger_entry WHERE account_id = 42 AND created_at >= DATE '2026-09-01' ORDER BY created_at;
-- 3
SELECT * FROM ledger_entry WHERE created_at >= DATE '2026-09-01';
```$hs$, $hs$1 and 2. A B-tree on `(account_id, created_at)` is sorted by `account_id` first, then by `created_at` within each account, so it serves conditions on the leading column, and on both (query 2 even gets its rows already sorted).

Query 3 constrains only the second column, so it generally can't use the index efficiently. (PostgreSQL 18 can sometimes skip-scan it when `account_id` has few distinct values, which isn't the case here.) Put the column used for equality first.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Multicolumn Indexes https://www.postgresql.org/docs/current/indexes-multicolumn.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: a function on the indexed column$hs$, $hs$There's an index on `customer(email)`, but this query scans the whole table. Why, and how do you fix it?

```sql
SELECT * FROM customer WHERE lower(email) = 'ana@bank.example';
```$hs$, $hs$The index stores the values of `email`, not of `lower(email)`, so it can't be used for a condition on the expression.

```sql
CREATE INDEX idx_customer_email_lower ON customer (lower(email));   -- an expression index
```

Alternatively, store emails already normalised to lower case, and query `email` directly.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Indexes on Expressions https://www.postgresql.org/docs/current/indexes-expressional.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: a leading wildcard$hs$, $hs$Why can't `WHERE name LIKE '%son'` use a normal B-tree index on `name`, while `LIKE 'And%'` can?$hs$, $hs$A B-tree is sorted by the value from its first character on, so it can find everything starting with "And" (a range of the sorted values). A leading wildcard gives no start to look for: matching rows are spread all over the index.

For substring searches, use a trigram index (`pg_trgm` with GIN) or full-text search. (`LIKE 'And%'` needs the `C` collation or a `text_pattern_ops` index.)$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Index Types https://www.postgresql.org/docs/current/indexes-types.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: covering indexes and index-only scans$hs$, $hs$The statement page runs `SELECT created_at, amount FROM ledger_entry WHERE account_id = ? ORDER BY created_at DESC LIMIT 50` constantly. Which index lets PostgreSQL answer it from the index alone?$hs$, $hs$```sql
CREATE INDEX idx_ledger_statement ON ledger_entry (account_id, created_at DESC) INCLUDE (amount);
```

A covering index: it contains every column the query needs, `account_id` and `created_at` as keys (for the filter and the order) and `amount` as an included payload column. PostgreSQL can then do an index-only scan, without visiting the table (as long as the pages are marked all-visible by `VACUUM`).$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Index-Only Scans and Covering Indexes https://www.postgresql.org/docs/current/indexes-index-only-scans.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Indexes: why the planner ignores one$hs$, $hs$There's an index on `ledger_entry(currency)`, and 90% of the entries are in EUR. `EXPLAIN` shows a sequential scan for `WHERE currency = 'EUR'`, but an index scan for `WHERE currency = 'CHF'`. Why does the planner skip the index for EUR?$hs$, $hs$Low selectivity. The planner knows from the column statistics that EUR matches 90% of the rows. Fetching them through the index means a random page read per row, which costs more than reading the whole table sequentially; for the rare CHF, the few index lookups are cheaper.

- An index helps when a condition matches a small fraction of the rows.
- The planner's choice is only as good as its statistics: after large changes, `ANALYZE` refreshes them.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Statistics Used by the Planner https://www.postgresql.org/docs/current/planner-stats.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Query performance: reading a slow statement's plan$hs$, $hs$Since `ledger_entry` grew to 500 million rows, the statement page takes 4 seconds. This is its plan. What is the problem, and which index fixes it?

```sql
EXPLAIN ANALYZE
SELECT created_at, amount FROM ledger_entry
WHERE account_id = 42 ORDER BY created_at DESC LIMIT 50;

Limit  (actual time=4012.310..4012.322 rows=50 loops=1)
  ->  Sort  (actual time=4012.308..4012.315 rows=50 loops=1)
        Sort Key: created_at DESC
        Sort Method: top-N heapsort  Memory: 32kB
        ->  Seq Scan on ledger_entry  (actual time=0.031..3980.127 rows=4120 loops=1)
              Filter: (account_id = 42)
              Rows Removed by Filter: 499995880
```$hs$, $hs$A sequential scan reads all 500 million rows to find the account's 4,120 entries (499,995,880 are removed by the filter), and then sorts them to keep the newest 50. Nearly all the time is the scan.

```sql
CREATE INDEX CONCURRENTLY ledger_entry_account_created
    ON ledger_entry (account_id, created_at DESC) INCLUDE (amount);
```

The index finds the account's entries directly, already in `created_at DESC` order, so the plan becomes an index scan that stops after 50 entries, with no sort. `INCLUDE (amount)` lets it answer from the index alone.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Using EXPLAIN https://www.postgresql.org/docs/current/using-explain.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Query performance: EXPLAIN ANALYZE$hs$, $hs$What is the difference between `EXPLAIN` and `EXPLAIN ANALYZE` in PostgreSQL?$hs$, $hs$`EXPLAIN` shows the plan the planner chose (scans, joins, sorts) with estimated costs and row counts, without running the query. `EXPLAIN ANALYZE` also runs it, and adds the actual time and rows of each step.

Since `ANALYZE` really executes the statement, wrap an `UPDATE` or `DELETE` in a transaction and roll it back.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, Using EXPLAIN https://www.postgresql.org/docs/current/using-explain.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Query performance: why a large OFFSET is slow$hs$, $hs$Page 5000 of an account statement is loaded with `ORDER BY created_at DESC OFFSET 100000 LIMIT 20`. Why is it slow?$hs$, $hs$The database can't jump to row 100,000: it must produce the first 100,020 rows in order and throw away 100,000 of them. The deeper the page, the more work, even with an index on the sort column.$hs$, 4, 'SUBMITTED', $hs$Use The Index, Luke: Fetching the next page https://use-the-index-luke.com/sql/partial-results/fetch-next-page$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Query performance: keyset pagination for a statement$hs$, $hs$Rewrite the statement query so the next page doesn't need `OFFSET`. The last entry on the current page had `created_at` T and `id` 981.$hs$, $hs$```sql
SELECT id, created_at, amount
FROM ledger_entry
WHERE account_id = :accountId
  AND (created_at, id) < (:T, 981)          -- continue right after the last row seen
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

With an index on `(account_id, created_at, id)`, every page is a short index range scan, however deep. `id` makes the order unique, so no entry is skipped or repeated. The trade-off: no jumping to an arbitrary page number.$hs$, 5, 'SUBMITTED', $hs$Use The Index, Luke: Fetching the next page https://use-the-index-luke.com/sql/partial-results/fetch-next-page$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migrations: what Flyway and Liquibase do$hs$, $hs$What do schema migration tools such as Flyway and Liquibase do?$hs$, $hs$They keep the database schema under version control as an ordered series of migration scripts. On startup (or in a deployment step) they apply the scripts not yet applied, in order, and record each one in a history table, so every environment ends up with the same schema.$hs$, 2, 'SUBMITTED', $hs$Flyway documentation https://documentation.red-gate.com/fd$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migrations: never edit an applied migration$hs$, $hs$`V3__add_iban.sql` has already run in production. A developer changes it to fix a column type. What happens, and what should they do instead?$hs$, $hs$Databases that already applied V3 won't run it again, so their schema differs from new environments that run the edited version. Flyway also stores each script's checksum and fails validation at the next start, because V3 no longer matches.

Add a new migration (`V4__change_iban_type.sql`) that makes the change.$hs$, 4, 'SUBMITTED', $hs$Flyway documentation https://documentation.red-gate.com/fd$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migrations: building an index on a busy table$hs$, $hs$Why is a plain `CREATE INDEX` on the busy `ledger_entry` table dangerous in production, and how do you build the index safely in PostgreSQL?$hs$, $hs$A plain `CREATE INDEX` blocks all inserts, updates and deletes on the table until the index is built, which can take minutes on a large table.

```sql
CREATE INDEX CONCURRENTLY idx_ledger_account ON ledger_entry (account_id, created_at);
```

It builds without blocking writes, but takes longer, can't run inside a transaction block, and if it fails it leaves an `INVALID` index that must be dropped and rebuilt.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, CREATE INDEX (building indexes concurrently) https://www.postgresql.org/docs/current/sql-createindex.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migrations: renaming a column without downtime$hs$, $hs$The `account.owner` column must be renamed to `holder`. The service runs as several instances, updated one at a time in a rolling deployment. Why does a migration with a plain `ALTER TABLE ... RENAME COLUMN` break, and in which steps do you do it instead?$hs$, $hs$During a rolling deployment, old and new versions of the service run at the same time against one database. After the rename, the old instances still use `owner` and fail.

Do it in steps (expand, migrate, contract), each a separate release:
1. Expand: add `holder`. The new version writes both columns, and reads `holder`, falling back to `owner`.
2. Migrate: backfill `holder` from `owner`, in batches.
3. Switch: release a version that only uses `holder`.
4. Contract: once no running version uses `owner`, drop it.$hs$, 7, 'SUBMITTED', $hs$Martin Fowler, ParallelChange https://martinfowler.com/bliki/ParallelChange.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: vertical scaling, read replicas and sharding$hs$, $hs$What do vertical scaling, read replicas and sharding each do to scale a database?$hs$, $hs$- Vertical scaling: a bigger machine (CPU, memory, faster disks).
- Read replicas: copies of the database that receive the primary's changes and serve reads; writes still go to the one primary.
- Sharding: splitting the data across several independent databases by a key (e.g. account id), each owning its part, so writes scale too.$hs$, 2, 'SUBMITTED', $hs$PostgreSQL docs, High Availability, Load Balancing, and Replication https://www.postgresql.org/docs/current/high-availability.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: reading your own writes from replicas$hs$, $hs$A customer makes a transfer and immediately opens their transaction history, but the transfer isn't there. The history is read from a replica. What's happening, and how do you fix it?$hs$, $hs$Replication lag: the replica receives the primary's changes asynchronously, a little later, and hadn't applied the transfer yet.

Read-your-own-writes: route a user's reads to the primary for a short while after they write (or for data they just changed), or have the replica wait until it has replayed the write's position before answering.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, High Availability, Load Balancing, and Replication https://www.postgresql.org/docs/current/high-availability.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: partitioning vs sharding$hs$, $hs$In relational databases in general, what is the difference between partitioning a table and sharding a database?$hs$, $hs$- **Partitioning** divides one logical table into smaller physical pieces, the partitions, by the value of a partition key (for example, a ledger split into one partition per month). All partitions stay on the same database server. Queries that filter on the key read only the relevant partitions (partition pruning), and old data can be removed by dropping a whole partition. It improves the manageability and speed of large tables, but adds no capacity.
- **Sharding** distributes the rows of a data set over several independent database servers, the shards, by the value of a shard key (for example, customers by region). Each shard holds a subset of the data, so writes, storage and load scale with the number of servers. The cost is complexity: queries, joins and transactions that span shards become difficult, and rebalancing data between shards is costly.

In short: partitioning organises data within one server; sharding spreads it across servers. (PostgreSQL, for example, supports partitioning natively, while sharding needs an extension or the application.)$hs$, 5, 'SUBMITTED', $hs$PostgreSQL docs, Table Partitioning https://www.postgresql.org/docs/current/ddl-partitioning.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Connection pools: why use one$hs$, $hs$Why does a Spring Boot application talk to the database through a connection pool such as HikariCP?$hs$, $hs$Opening a database connection is slow (network handshake, authentication, a new server process in PostgreSQL). A pool keeps a bounded set of open connections and lends them out for each transaction, then takes them back.

The bound also protects the database: it never receives more connections than it was sized for.$hs$, 3, 'SUBMITTED', $hs$HikariCP: About Pool Sizing https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Connection pools: why a bigger pool can be slower$hs$, $hs$Requests time out waiting for a connection, so a team raises the pool from 10 to 200 connections per instance, with 10 instances. Why can that make things worse?$hs$, $hs$- The database can only run as many queries in parallel as it has CPU cores and disk capacity; more concurrent queries mean more contention and context switching, so each gets slower.
- 10 instances × 200 can exceed the database's connection limit, and each PostgreSQL connection is a process using memory.

The waits usually mean connections are held too long: slow queries, long transactions, or remote calls made inside a transaction. Fix those; small pools are usually faster.$hs$, 6, 'SUBMITTED', $hs$HikariCP: About Pool Sizing https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: choosing the shard key for a payments platform$hs$, $hs$A payments platform serves thousands of merchants and is sharded by merchant id. Why is the merchant id a good shard key here, and what problem do the few very large merchants cause?$hs$, $hs$All of a merchant's data lives on one shard, so its queries and transactions never cross shards, and merchants spread over the shards evenly by number.

The very large merchants are hot spots: each puts its whole load on a single shard, which the other merchants on that shard feel. Give them dedicated shards, or split them further with a second key (merchant id plus account id).$hs$, 7, 'SUBMITTED', $hs$Azure Architecture Center, Sharding pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/sharding$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'system-design', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: why not accept writes for one account in two regions$hs$, $hs$To make transfers fast worldwide, a bank considers letting both its European and its US database accept writes for every account, replicating to each other asynchronously (multi-leader). Why is that unsafe for account balances?$hs$, $hs$Two debits on the same account can happen in both regions at once. Each region checks the balance it has, finds enough money, and commits; only afterwards does replication bring the two writes together. The account is now overdrawn, and no automatic conflict resolution can fix it: last-write-wins keeps one balance and silently drops the other debit, creating money.

Give each account one home region whose primary accepts its writes, and route transfers there; other regions get replicas for reads and for disaster recovery.$hs$, 8, 'SUBMITTED', $hs$Designing Data-Intensive Applications (Martin Kleppmann), chapter 5: multi-leader replication and write conflicts$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Data stores: four kinds of NoSQL databases$hs$, $hs$Name a typical use for a document store, a key-value store, a wide-column store and a graph database.$hs$, $hs$- Document (MongoDB): records read and written as a whole, such as a customer profile with nested preferences.
- Key-value (Redis, DynamoDB): fast lookups by key at scale, such as sessions or cached exchange rates.
- Wide-column (Cassandra): huge, write-heavy, time-ordered data, such as card transaction events.
- Graph (Neo4j): highly connected data, such as detecting fraud rings through shared devices and accounts.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Understand data store models (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/data-store-overview$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$PostgreSQL: VACUUM and long transactions$hs$, $hs$What does `VACUUM` do in PostgreSQL, and why does a transaction left open for hours make the `account` table grow?$hs$, $hs$Because of MVCC, every update leaves the old row version behind. `VACUUM` (run automatically by autovacuum) reclaims the versions no transaction can still see, so their space can be reused.

A transaction open for hours may still need versions from when it started, so vacuum can't remove any version newer than that. Frequently updated tables, such as balances, fill with dead versions and bloat.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Routine Vacuuming https://www.postgresql.org/docs/current/routine-vacuuming.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Security: SQL injection$hs$, $hs$Why is this query dangerous, and how do you write it safely?

```java
String sql = "SELECT * FROM account WHERE iban = '" + iban + "'";
```$hs$, $hs$The input becomes part of the SQL text, so input such as `' OR '1'='1` changes the query itself: here, returning every account (SQL injection).

```java
jdbc.query("SELECT * FROM account WHERE iban = ?", mapper, iban);   // a bound parameter: always data, never SQL
```

Also give the application's database user only the privileges it needs.$hs$, 3, 'SUBMITTED', $hs$OWASP SQL Injection Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: double-entry bookkeeping$hs$, $hs$What does double-entry bookkeeping mean for how a bank's ledger records a transfer of 100 from account A to account B?$hs$, $hs$Every transfer is recorded as at least two entries that sum to zero: a debit of 100 on A and a credit of 100 on B, written together in one transaction.

```sql
INSERT INTO ledger_entry (transfer_id, account_id, amount) VALUES (:t, :a, -100.00), (:t, :b, 100.00);
```

Money is never created or lost: for every transfer, and for the whole ledger, the entries add up to zero, which makes errors detectable.$hs$, 3, 'SUBMITTED', $hs$Wikipedia, Double-entry bookkeeping (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Double-entry_bookkeeping$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Schema design: timestamp vs timestamptz$hs$, $hs$A global bank records when each transfer happened. Should the column be `timestamp` or `timestamptz` in PostgreSQL, and what does each store?$hs$, $hs$`timestamptz` (timestamp with time zone): it stores an absolute instant (internally in UTC). Values written with a zone offset are converted to UTC, and are shown in the session's `TimeZone` when read, so "when it happened" is unambiguous across regions.

`timestamp` (without time zone) stores only a date and wall-clock time, with no zone: 2026-10-06 18:00 in Bucharest and in New York would look the same.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Date/Time Types https://www.postgresql.org/docs/current/datatype-datetime.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auditing: recording every balance change$hs$, $hs$Every change to `account.balance` must be recorded in `account_audit`: the account, the old and new balance, when, and the database user. Write the trigger, so no code path can skip it.

```sql
CREATE TABLE account_audit (
    account_id  BIGINT NOT NULL,
    old_balance NUMERIC(19, 4) NOT NULL,
    new_balance NUMERIC(19, 4) NOT NULL,
    changed_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_by  TEXT NOT NULL DEFAULT current_user
);
```$hs$, $hs$```sql
CREATE FUNCTION audit_balance() RETURNS trigger AS $$
BEGIN
    INSERT INTO account_audit (account_id, old_balance, new_balance)
    VALUES (OLD.id, OLD.balance, NEW.balance);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER account_balance_audit
    AFTER UPDATE OF balance ON account
    FOR EACH ROW
    WHEN (OLD.balance IS DISTINCT FROM NEW.balance)
    EXECUTE FUNCTION audit_balance();
```

- `AFTER UPDATE OF balance ... FOR EACH ROW` fires once per changed row; the `WHEN` clause skips updates that leave the balance as it was.
- The audit row is written in the same transaction, so it exists exactly when the change is committed.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Trigger Functions (PL/pgSQL) https://www.postgresql.org/docs/current/plpgsql-trigger.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: the transfer transaction$hs$, $hs$Write the transaction that moves `:amount` from account `:from` to account `:to` for the client request `:request_id`. It must never overdraw, never deadlock with a transfer in the opposite direction, and do nothing the second time the same request arrives.

```sql
CREATE TABLE account (
    id      BIGINT PRIMARY KEY,
    balance NUMERIC(19, 4) NOT NULL CHECK (balance >= 0)
);
CREATE TABLE transfer (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_id   UUID UNIQUE NOT NULL,
    from_account BIGINT NOT NULL REFERENCES account (id),
    to_account   BIGINT NOT NULL REFERENCES account (id),
    amount       NUMERIC(19, 4) NOT NULL CHECK (amount > 0)
);
CREATE TABLE ledger_entry (
    transfer_id BIGINT NOT NULL REFERENCES transfer (id),
    account_id  BIGINT NOT NULL REFERENCES account (id),
    amount      NUMERIC(19, 4) NOT NULL
);
```$hs$, $hs$```sql
BEGIN;

INSERT INTO transfer (request_id, from_account, to_account, amount)
VALUES (:request_id, :from, :to, :amount)
ON CONFLICT (request_id) DO NOTHING
RETURNING id;                    -- no row returned: a retry, so ROLLBACK and report the original

SELECT id FROM account WHERE id IN (:from, :to)
ORDER BY id FOR UPDATE;          -- lock both rows, always in id order: no deadlock

UPDATE account SET balance = balance - :amount
WHERE id = :from AND balance >= :amount;   -- 0 rows updated: insufficient funds, ROLLBACK

UPDATE account SET balance = balance + :amount WHERE id = :to;

INSERT INTO ledger_entry (transfer_id, account_id, amount)
VALUES (:transfer_id, :from, -:amount), (:transfer_id, :to, :amount);

COMMIT;
```

- The unique `request_id` makes a retry a no-op; a concurrent duplicate waits for the first insert and then inserts nothing.
- The conditional `UPDATE` and the `CHECK` keep the balance from going negative.
- The two ledger entries sum to zero, so the ledger always balances.$hs$, 8, 'SUBMITTED', $hs$PostgreSQL docs, INSERT (ON CONFLICT) https://www.postgresql.org/docs/current/sql-insert.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: why an append-only ledger$hs$, $hs$Why do banks record money movements as an append-only ledger of entries, instead of only updating a `balance` column?$hs$, $hs$- History and audit: every movement is a permanent record of what happened and why; a balance column only shows the latest state.
- Correctness can be checked: the balance must equal the sum of the account's entries, and every transfer's entries must sum to zero. A stored balance becomes a cache that can be reconciled against the ledger.
- Corrections are new entries (a reversal), never edits, so nothing is lost.

The stored balance stays for fast reads and for checking funds; the ledger is the source of truth.$hs$, 6, 'SUBMITTED', $hs$Martin Fowler, Accounting Patterns https://martinfowler.com/eaaDev/AccountingNarrative.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: balances that don't match the ledger$hs$, $hs$Write the reconciliation query: every account whose stored `balance` differs from the sum of its ledger entries, with both values. Accounts without any entries must be checked too (their balance should be 0).

```sql
account      (id BIGINT PRIMARY KEY, balance NUMERIC(19, 4) NOT NULL)
ledger_entry (account_id BIGINT NOT NULL, amount NUMERIC(19, 4) NOT NULL)
```$hs$, $hs$```sql
SELECT a.id, a.balance, coalesce(l.total, 0) AS ledger_total
FROM account a
LEFT JOIN (
    SELECT account_id, sum(amount) AS total
    FROM ledger_entry
    GROUP BY account_id
) l ON l.account_id = a.id
WHERE a.balance <> coalesce(l.total, 0);
```

- Aggregating first, then joining, sums each account's entries once; joining first and summing would multiply rows if more tables were joined.
- `LEFT JOIN` keeps accounts without entries; `coalesce` turns their missing total into 0, so a non-zero balance without entries is reported.$hs$, 6, 'SUBMITTED', $hs$PostgreSQL docs, Table Expressions (subqueries in FROM) https://www.postgresql.org/docs/current/queries-table-expressions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: a hot merchant account$hs$, $hs$A large merchant's account receives thousands of payments per second. Each payment runs `UPDATE account SET balance = balance + :amount WHERE id = :merchant` in its own transaction, and payment latency explodes. Why, and how does splitting the account into sub-accounts help?$hs$, $hs$Every one of those updates needs the row lock on the same row, held until its transaction commits. The payments are therefore serialised: each waits for the one before it, so throughput is capped at one transaction at a time on that row, and the lock queue grows.

Split the account into N sub-accounts (buckets) and credit a random or round-robin one: N rows can be updated in parallel, so N payments proceed at once. The merchant's balance is the sum of its buckets. (Alternatively, insert credit entries without touching a balance row, and aggregate them periodically.)$hs$, 8, 'SUBMITTED', $hs$Designing Data-Intensive Applications (Martin Kleppmann), chapter 6: skewed workloads and relieving hot spots$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: a transfer across two shards$hs$, $hs$Accounts are sharded across 16 databases by account id. A transfer's two accounts live on different shards, so no single local transaction covers both. How does a saga move the money, and how does it stay correct when the credit on the second shard fails?$hs$, $hs$A saga is a sequence of local transactions, each committed on its own shard:

1. On A's shard: debit A and record the transfer as "in flight" (a suspense entry), in one local transaction. Publish "debited" reliably, with a transactional outbox.
2. On B's shard: credit B, identified by the transfer id, so a redelivered message can't credit twice. Publish "credited".
3. On A's shard: mark the transfer completed.

If the credit is impossible (B closed), the saga runs a compensating transaction on A's shard: refund A and mark the transfer failed. In between, the money sits in the suspense entry, so the sum of all entries across shards still reconciles to zero.$hs$, 9, 'SUBMITTED', $hs$Microservices.io, Pattern: Saga https://microservices.io/patterns/data/saga.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: posting daily interest to 50 million accounts$hs$, $hs$Every night, interest must be posted to 50 million savings accounts. A developer writes one `UPDATE` that changes all 50 million balances in a single transaction. What goes wrong, and how do you make a rerun after a failure safe?$hs$, $hs$One giant transaction holds the row lock on every account until it ends, blocking transfers on them; it writes a huge amount of WAL; it leaves 50 million dead row versions for vacuum; and if it fails at 90%, everything rolls back and must start over.

Process in chunks instead (e.g. 10,000 accounts by id range), each its own short transaction. Make each chunk idempotent: record the posting per account and day under a unique constraint, so a rerun skips the accounts already done and continues where the failure happened.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Routine Vacuuming https://www.postgresql.org/docs/current/routine-vacuuming.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: a container vs a virtual machine$hs$, $hs$What is the difference between a container and a virtual machine?$hs$, $hs$- Container: isolated processes that share the host's operating system kernel, packaged with their own filesystem and dependencies. Lightweight: starts in seconds or less, small overhead.
- Virtual machine: a full guest operating system with its own kernel, running on a hypervisor. Stronger isolation, but heavier: more memory, slower to start.$hs$, 1, 'SUBMITTED', $hs$Docker documentation, Building best practices https://docs.docker.com/build/building/best-practices/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: an image vs a container$hs$, $hs$What is the difference between a container image and a container?$hs$, $hs$- Image: an immutable, layered template: a filesystem with the application and its dependencies, plus metadata such as the start command. Built once, stored in a registry.
- Container: a running (or stopped) instance of an image, with its own writable layer, process and network. Many containers can run from one image.$hs$, 1, 'SUBMITTED', $hs$Docker documentation, Building best practices https://docs.docker.com/build/building/best-practices/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: Docker layer caching and instruction order$hs$, $hs$Why does the order of instructions in a Dockerfile matter for build speed?$hs$, $hs$Each instruction produces a layer, and Docker reuses cached layers as long as the instruction and its inputs are unchanged. Once one layer changes, every layer after it is rebuilt.

So put what rarely changes first (the base image, system packages, dependencies) and what changes on every commit last (the application code). A one-line code change then rebuilds only the last layers.$hs$, 3, 'SUBMITTED', $hs$Docker documentation, Building best practices (leverage the build cache) https://docs.docker.com/build/building/best-practices/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: a Dockerfile for a Spring Boot service$hs$, $hs$Write a good Dockerfile for a Spring Boot application, `application.jar`, built beforehand.$hs$, $hs$```dockerfile
FROM eclipse-temurin:21-jre AS builder
WORKDIR /builder
COPY target/application.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
RUN useradd --system app
USER app
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "application.jar"]
```

- Two stages: the extraction step isn't in the final image.
- Layers from rarely to often changing: dependencies first, so a code change rebuilds only the last layer.
- A JRE base image, a non-root user, heap sized from the container's memory, no secrets in the image.

Alternatively, `mvn spring-boot:build-image` builds a layered image with Cloud Native Buildpacks, without a Dockerfile.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Dockerfiles https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kubernetes: pods, deployments and services$hs$, $hs$In Kubernetes, what are a pod, a deployment and a service?$hs$, $hs$- Pod: the smallest unit Kubernetes runs: one or more containers that share a network address and storage, scheduled together on one node.
- Deployment: declares how many identical pods of a version should run, keeps that number running, and rolls out new versions gradually.
- Service: a stable name and virtual address for a set of pods, load-balancing traffic over the ready ones.$hs$, 1, 'SUBMITTED', $hs$Kubernetes documentation, Deployments https://kubernetes.io/docs/concepts/workloads/controllers/deployment/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kubernetes: config maps and secrets$hs$, $hs$What is the difference between a ConfigMap and a Secret in Kubernetes? Is a Secret encrypted?$hs$, $hs$Both supply configuration to pods, as environment variables or mounted files.

- ConfigMap: for non-sensitive settings (URLs, feature switches).
- Secret: for sensitive values (passwords, keys), with separate access control.

By default, a Secret's values are only base64-encoded and stored unencrypted in etcd: anyone with API access to it can read it. Enable encryption at rest, restrict access with RBAC, or use an external secrets manager.$hs$, 2, 'SUBMITTED', $hs$Kubernetes documentation, Secrets https://kubernetes.io/docs/concepts/configuration/secret/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kubernetes: resource requests and limits$hs$, $hs$What is the difference between resource requests and limits for a container in Kubernetes, and what happens when a container exceeds each limit?$hs$, $hs$- Requests: what the scheduler reserves for the container; a pod is placed only on a node with that much free. Guaranteed capacity.
- Limits: the maximum the container may use.

Exceeding the memory limit: the container is killed (`OOMKilled`) and restarted.
Exceeding the CPU limit: the container isn't killed but throttled: it's paused until the next period, which shows up as latency.$hs$, 3, 'SUBMITTED', $hs$Kubernetes documentation, Resource Management for Pods and Containers https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: twelve-factor configuration in the environment$hs$, $hs$Name five principles of the twelve-factor app, and explain "config in the environment".$hs$, $hs$Examples:
- one codebase, many deploys
- explicit dependencies
- config in the environment
- backing services as attached resources
- separate build, release and run
- stateless processes
- disposability (fast start, graceful shutdown)
- dev/prod parity
- logs as event streams.

Config in the environment: everything that differs between environments (URLs, credentials, limits) is supplied at runtime, not compiled into the artifact, so the same build runs everywhere.$hs$, 2, 'SUBMITTED', $hs$The Twelve-Factor App https://12factor.net/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: build once, deploy many$hs$, $hs$What does the continuous delivery principle "build once, deploy many" mean, and why does it matter?$hs$, $hs$"Build once, deploy many" means that a release artifact (a JAR or a container image, identified by its version or commit) is built exactly once, and that same artifact is promoted unchanged through every environment: test, staging and production. Only the configuration differs between environments, supplied from outside the artifact.

It matters because it guarantees that what was tested is exactly what runs in production. Rebuilding for each environment can produce a different artifact (other dependency versions, another compiler, a changed build script), so production would run something that was never tested.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, ContinuousDelivery https://martinfowler.com/bliki/ContinuousDelivery.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: CI, continuous delivery, continuous deployment$hs$, $hs$What is the difference between continuous integration, continuous delivery and continuous deployment?$hs$, $hs$- Continuous integration: everyone merges small changes into the main branch frequently (at least daily), and each merge is built and tested automatically.
- Continuous delivery: every change that passes the pipeline is releasable; deploying to production is a routine, push-button decision.
- Continuous deployment: every change that passes the pipeline is deployed to production automatically, with no manual step.$hs$, 1, 'SUBMITTED', $hs$Martin Fowler, Continuous Integration https://martinfowler.com/articles/continuousIntegration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: the stages of a pipeline$hs$, $hs$What stages should the pipeline of a Spring Boot service have, from commit to production?$hs$, $hs$1. Compile and run unit tests (fast feedback first).
2. Integration and contract tests (Testcontainers).
3. Static analysis, and dependency and image vulnerability scanning.
4. Build the image once, tag it with the commit, push it to the registry.
5. Deploy to a test or staging environment; run smoke tests.
6. Deploy to production (automatically or with an approval), progressively.
7. Post-deploy checks of health and key metrics, with automatic rollback.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, Continuous Integration https://martinfowler.com/articles/continuousIntegration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Releases: rolling, blue-green and canary$hs$, $hs$Compare rolling, blue-green and canary deployments.$hs$, $hs$- Rolling: instances are replaced with the new version a few at a time. No extra capacity needed, but both versions serve traffic during the rollout, and rolling back is another rollout.
- Blue-green: the new version is deployed as a complete second environment; traffic switches all at once, and back just as fast if needed. Needs double capacity during the switch.
- Canary: a small share of traffic (say 5%) goes to the new version first; it's widened step by step while its metrics stay healthy, or rolled back affecting few users.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, CanaryRelease https://martinfowler.com/bliki/CanaryRelease.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Releases: feature flags$hs$, $hs$What are feature flags for, and what is their hidden cost?$hs$, $hs$They separate deploying code from releasing a feature: the code ships switched off, and the feature is turned on at runtime, for some users or a percentage, then for everyone. They also act as kill switches for features that misbehave.

The cost: every flag doubles the code paths, combinations go untested, and old flags pile up as dead code. Remove each release flag once the feature is fully rolled out.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, Feature Toggles https://martinfowler.com/articles/feature-toggles.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Releases: two versions running at once$hs$, $hs$During a rolling deployment, old and new versions of a service run side by side. What does that require?$hs$, $hs$Compatibility in both directions for everything the two versions share:
- the database schema: the new schema must work with the old code (and the old schema with the new code, for rollback)
- the API: clients may hit either version
- events: consumers may be on either version, and in-flight messages come from both
- cached data and sessions.

Changes are therefore made in steps: expand, migrate, contract.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, ParallelChange https://martinfowler.com/bliki/ParallelChange.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Releases: a new required field$hs$, $hs$A release must add a `purpose` column to the `transfer` table and a `purpose` field to the `TransferBooked` event, both eventually required. Sequence the changes for zero downtime.$hs$, $hs$1. Expand: add the column as nullable; update consumers to accept events with or without the field.
2. Deploy producers that write the column and include the field in the event.
3. Backfill existing rows with a sensible value.
4. Contract: once all running versions write it, make the column `NOT NULL`, and let consumers require the field.

Each step is a separate release that works with the version before and after it.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, ParallelChange https://martinfowler.com/bliki/ParallelChange.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'databases', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: Maven vs Gradle$hs$, $hs$Compare Maven and Gradle.$hs$, $hs$- Maven: declarative XML (`pom.xml`) with a fixed lifecycle (`compile`, `test`, `package`…) and plugins. Uniform across projects and simple to read, but rigid for unusual builds.
- Gradle: builds scripted in Kotlin or Groovy, with incremental builds and a build cache. Faster for large, multi-module builds and very flexible, but more to learn, and builds can become complicated code.$hs$, 2, 'SUBMITTED', $hs$Gradle User Manual https://docs.gradle.org/current/userguide/userguide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: conflicting dependency versions$hs$, $hs$At runtime, a service fails with `NoSuchMethodError` from a Jackson class, although it compiles. What's the likely cause, and how do you manage it?$hs$, $hs$Two dependencies bring different versions of Jackson transitively, and the build picked one (Maven's "nearest wins") that lacks a method the other library calls.

- Inspect the tree: `mvn dependency:tree -Dincludes=com.fasterxml.jackson.core` (or `tools.jackson.core` for Jackson 3).
- Manage versions centrally: import a BOM (Spring Boot's dependency management already aligns Jackson), and pin versions in `dependencyManagement` rather than per dependency.
- Exclude the conflicting transitive dependency where needed, and use the Maven Enforcer plugin's dependency convergence rule to catch conflicts in the build.$hs$, 4, 'SUBMITTED', $hs$Apache Maven, Introduction to the Dependency Mechanism https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Releases: a migration that queues behind a long transaction$hs$, $hs$A migration runs `ALTER TABLE transfer ADD COLUMN purpose TEXT`, a change that's instant on its own. Yet during the deploy every request touching `transfer` hung for two minutes. A reporting query had been running on the table at the time. What happened, and which setting makes migrations fail fast instead?$hs$, $hs$`ALTER TABLE` needs an exclusive lock on the table. It had to wait for the long reporting query to finish, and while it waited in the lock queue, every new query on `transfer`, even plain reads, queued behind it. A brief change became a two-minute outage.

```sql
SET lock_timeout = '5s';   -- in the migration, before the ALTER
ALTER TABLE transfer ADD COLUMN purpose TEXT;
```

If the lock isn't granted within 5 seconds, the migration fails instead of blocking everyone, and can be retried at a quieter moment.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Client Connection Defaults (lock_timeout) https://www.postgresql.org/docs/current/runtime-config-client.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Observability: logs, metrics and traces$hs$, $hs$Observability is commonly built on three kinds of telemetry: logs, metrics and traces. What is each one, and what is it best for?$hs$, $hs$- **Logs** are timestamped records of individual events, with their details: what happened to this transfer, and with which error. They are best for investigating a specific occurrence.
- **Metrics** are numeric measurements aggregated over time, such as request rate, error rate, latency percentiles or connection pool usage. They are cheap to store and query, so they suit dashboards and alerts.
- **Traces** record the path of a single request through the system, as a tree of timed operations across services. They show where the time went and which call failed.

Used together: metrics show that something is wrong, traces show where, and logs show why.$hs$, 2, 'SUBMITTED', $hs$OpenTelemetry, Observability primer (CC BY 4.0) https://opentelemetry.io/docs/concepts/observability-primer/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Observability: monitoring vs observability$hs$, $hs$What is the difference between monitoring and observability?$hs$, $hs$- Monitoring: watching predefined signals for known failure conditions: dashboards and alerts for problems you anticipated.
- Observability: the ability to understand the system's internal state from its outputs, including asking new questions about problems nobody anticipated, e.g. "why are only transfers to one bank slow since noon?"

Observability needs rich, correlated telemetry: high-detail traces and structured logs, not just a few metrics.$hs$, 2, 'SUBMITTED', $hs$OpenTelemetry, Observability primer (CC BY 4.0) https://opentelemetry.io/docs/concepts/observability-primer/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Logging: structured logging$hs$, $hs$What is structured logging, and how do you enable it in Spring Boot?$hs$, $hs$Writing each log event as machine-readable key-value data, usually JSON, instead of free text: timestamp, level, logger, message, trace id, and fields such as `transferId`. Log systems can then filter and aggregate on the fields reliably.

Spring Boot supports it built in:

```properties
logging.structured.format.console=ecs
```

(`ecs`, `logstash` and `gelf` are supported formats.)$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Logging (Structured Logging) https://docs.spring.io/spring-boot/reference/features/logging.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Logging: correlation ids$hs$, $hs$What is a correlation (or trace) id in logs, and how does it get onto every log line of a request?$hs$, $hs$An id shared by everything belonging to one request, so all its log lines, across services, can be found with one search.

- It arrives with the request (the W3C `traceparent` header) or is created at the edge.
- Within a service, the tracing instrumentation puts it into the logging MDC, and the log pattern or structured format includes it in every line.
- It's passed on in the headers of outgoing calls and messages.

With Micrometer Tracing in Spring Boot, the trace and span ids are added to the logs automatically.$hs$, 3, 'SUBMITTED', $hs$W3C Trace Context https://www.w3.org/TR/trace-context/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Logging: choosing the level$hs$, $hs$Which log level fits each of these events in a payment service, and why?

1. A transfer couldn't be booked: the ledger service was unreachable, even after three retries.
2. The FX rate provider timed out once, and the retry succeeded.
3. The nightly interest job finished: 2,140,512 accounts in 14 minutes.
4. The SQL parameters of every fee lookup.$hs$, $hs$1. **ERROR**: an operation failed for good, and someone may have to act on it.
2. **WARN**: something went wrong but was handled; worth noticing if it keeps happening.
3. **INFO**: a significant business or lifecycle event, with its outcome.
4. **DEBUG**: detail for troubleshooting, off in production by default.

The rule behind it: the level says how urgently a person should look. If ERROR is used for handled problems, alerts on errors fire for nothing, and people stop reading them.$hs$, 3, 'SUBMITTED', $hs$SLF4J user manual https://www.slf4j.org/manual.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: counters, gauges and timers$hs$, $hs$What do a counter, a gauge and a timer (or histogram) measure? Give an example of each for a payment service.$hs$, $hs$- Counter: a value that only increases. Number of payments processed, failed payments.
- Gauge: a current value that goes up and down. Payments waiting in the queue, active database connections.
- Timer / histogram: the distribution of durations or sizes, giving count, total and percentiles. Payment processing time.$hs$, 2, 'SUBMITTED', $hs$Micrometer documentation https://docs.micrometer.io/micrometer/reference/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: the RED and USE methods$hs$, $hs$What are the RED and USE methods?$hs$, $hs$Two checklists of what to measure:
- RED, for services (per endpoint): Rate (requests per second), Errors (failed requests), Duration (latency distribution).
- USE, for resources (CPU, memory, disks, connection and thread pools): Utilisation (how busy), Saturation (how much work is waiting), Errors.

RED shows what users experience; USE shows which resource is the bottleneck.$hs$, 3, 'SUBMITTED', $hs$Brendan Gregg, The USE Method https://www.brendangregg.com/usemethod.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: high-cardinality tags$hs$, $hs$A developer adds the customer id as a tag on the payment latency metric, "to see latency per customer". Why is that a bad idea?$hs$, $hs$Every distinct tag value creates a separate time series. With millions of customers, the metric becomes millions of series: memory and storage in the metrics system explode, queries slow down, and costs soar; the system may drop data or fall over.

Keep tags to a small, bounded set of values (endpoint, status, payment type). For per-customer detail use traces and logs, which are made for high-cardinality data.$hs$, 5, 'SUBMITTED', $hs$Prometheus documentation, Metric and label naming https://prometheus.io/docs/practices/naming/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: averaging percentiles$hs$, $hs$Each of 10 instances reports its own p99 latency. Why is the average of these ten values not the p99 of the service, and how do you compute the real one?$hs$, $hs$Percentiles don't aggregate: the p99 of the combined requests depends on the whole distribution, not on each instance's p99. One slow instance with a p99 of 2 s and nine at 100 ms average to 290 ms, while the real p99 may be much higher (or lower), depending on traffic shares.

Have each instance publish a histogram (counts per latency bucket), add the buckets across instances, and compute the percentile from the combined histogram (e.g. Prometheus's `histogram_quantile` over summed buckets).$hs$, 6, 'SUBMITTED', $hs$Prometheus documentation, Histograms and summaries https://prometheus.io/docs/practices/histograms/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Tracing: traces and spans$hs$, $hs$A `POST /checkout` request reaches the order service, which calls the payment service; the payment service queries its database and then calls an external fraud check. With distributed tracing (for example OpenTelemetry), what are the trace and the spans of this request, and how are they related?$hs$, $hs$- A **trace** is the complete record of one request's path through the system, identified by a single trace id that every service passes on (in the `traceparent` header). Here: everything that happened for this one checkout.
- A **span** is one timed operation within the trace, with a name, a start time, a duration, attributes and a status. Here: the order service handling `POST /checkout`, the call to the payment service, the database query, and the call to the fraud check.

Spans form a tree: each span except the first (the root span) has a parent span. The database query and the fraud check are children of the payment service's span, which is a child of the order service's span. A tracing tool shows this tree on a timeline, so the slowest or failing operation stands out.$hs$, 2, 'SUBMITTED', $hs$OpenTelemetry docs, Traces (CC BY 4.0) https://opentelemetry.io/docs/concepts/signals/traces/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Tracing: OpenTelemetry$hs$, $hs$What is OpenTelemetry, and how does a Spring Boot 4 service export traces with it?$hs$, $hs$A vendor-neutral standard, with APIs, SDKs and a protocol (OTLP), for producing and exporting traces, metrics and logs to any backend (Jaeger, Grafana Tempo, commercial tools), plus the Collector to receive, process and forward them.

In Spring Boot 4: add `spring-boot-starter-opentelemetry` and configure the OTLP endpoint with the `management.opentelemetry.tracing.export.otlp.*` properties. Micrometer Tracing instruments HTTP, JDBC and messaging, and propagates the context. By default Boot samples 10% of requests (`management.tracing.sampling.probability`).$hs$, 3, 'SUBMITTED', $hs$OpenTelemetry, What is OpenTelemetry? (CC BY 4.0) https://opentelemetry.io/docs/what-is-opentelemetry/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: from a Spring Boot app to Grafana$hs$, $hs$How do a Spring Boot service's metrics reach a Grafana dashboard with Prometheus?$hs$, $hs$1. The service records metrics with Micrometer (Boot instruments HTTP, JVM, pools, caches automatically).
2. With the Prometheus registry on the classpath, Actuator exposes them at `/actuator/prometheus` (included in the exposed endpoints).
3. Prometheus scrapes that endpoint from every instance every few seconds, and stores the time series.
4. Grafana queries Prometheus (PromQL) for the dashboards; alerts are evaluated on the same data.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Metrics https://docs.spring.io/spring-boot/reference/actuator/metrics.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Incidents: dashboards green, customers complaining$hs$, $hs$During an incident, every dashboard was green while customers complained that payments failed: the failing requests never reached the payment service (a broken DNS entry for the mobile app). Which two kinds of monitoring would have shown it?$hs$, $hs$Monitoring from the customer's side, not from inside the services:

- Synthetic checks: a probe outside your network runs a real journey (e.g. a small test payment through the public address) every minute, and alerts when it fails.
- Client telemetry: the mobile app reports its own failed requests, and SLIs are measured at the edge (the load balancer or gateway) rather than in the service.

Server-side metrics only see requests that arrive; a request that never arrives is invisible to them.$hs$, 6, 'SUBMITTED', $hs$Google SRE book, Monitoring Distributed Systems https://sre.google/sre-book/monitoring-distributed-systems/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Metrics: Prometheus and a latency histogram from Spring Boot$hs$, $hs$A Spring Boot service exposes its metrics to Prometheus with Micrometer. How does Prometheus collect them, and why should request latency be recorded as a histogram rather than as an average?$hs$, $hs$**Prometheus pulls.** It scrapes each instance's metrics endpoint over HTTP at a fixed interval, here `/actuator/prometheus` (added by `micrometer-registry-prometheus`), and stores the values as time series. It finds the instances through service discovery, e.g. from Kubernetes.

An **average** hides the slow requests users actually feel, and averages from several instances can't be combined into a percentile. A **histogram** counts requests in latency buckets; Prometheus can then compute any percentile across all instances and over any time window (with `histogram_quantile`), which is what latency SLOs and alerts need. In Spring Boot, turn it on for HTTP requests with `management.metrics.distribution.percentiles-histogram.http.server.requests=true`.$hs$, 5, 'SUBMITTED', $hs$Prometheus docs, Overview (pull model) https://prometheus.io/docs/introduction/overview/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Incidents: the first steps$hs$, $hs$An alert fires: the error rate of the payments API jumped from 0.1% to 5%. What are your first steps?$hs$, $hs$1. Assess impact and scope: which endpoints, instances, regions, customers; since when.
2. Check what changed around that time: deploys, configuration, feature flags, traffic, a dependency's status.
3. Mitigate first: roll back the deploy, disable the flag, scale, fail over. Restoring service comes before understanding it.
4. Then find the cause: error logs and traces of failing requests, dependency dashboards.
5. Communicate status throughout, and follow up with a postmortem.$hs$, 4, 'SUBMITTED', $hs$Google SRE book, Effective Troubleshooting https://sre.google/sre-book/effective-troubleshooting/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Incidents: high latency with normal CPU and memory$hs$, $hs$The transfer API's latency is high, but CPU and memory look normal. What might be saturated, and how do you check?$hs$, $hs$Something the requests wait for, rather than compute:
- the database connection pool (requests waiting for a connection): pool metrics for pending threads
- the request thread pool or an executor's queue
- locks: in the database (lock waits) or in the JVM (thread dumps showing `BLOCKED` threads)
- a slow downstream service: traces and client metrics
- GC pauses: GC logs
- disk or network I/O.

Traces of slow requests show which wait dominates.$hs$, 5, 'SUBMITTED', $hs$Brendan Gregg, The USE Method https://www.brendangregg.com/usemethod.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Incidents: errors for only some users$hs$, $hs$Only some customers see errors, and only some of the time. How do you narrow it down?$hs$, $hs$Segment the failing requests by every dimension you have and look for the one that separates them from successful ones:
- instance or pod, node, availability zone, region
- application version (a canary), feature flag state
- endpoint, client type and app version
- customer attributes (country, product, account type), specific data (a malformed record)
- the cache node or database shard involved.

Traces and structured logs make this a query. A common answer: one bad instance or node, a canary, or data that only some customers have.$hs$, 6, 'SUBMITTED', $hs$Google SRE book, Effective Troubleshooting https://sre.google/sre-book/effective-troubleshooting/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Incidents: backups, RPO and RTO$hs$, $hs$What are RPO and RTO? How does PostgreSQL's point-in-time recovery relate to them?$hs$, $hs$- RPO (recovery point objective): how much data you can afford to lose, as time: "at most 1 minute of transactions".
- RTO (recovery time objective): how long the service may be down while recovering: "back within 30 minutes".

Point-in-time recovery restores a base backup and replays the archived WAL up to a chosen moment. It gives a small RPO (as small as the WAL archiving delay) and also undoes mistakes ("restore to just before the bad migration"). The RTO depends on the backup size and the WAL to replay, which only regular restore drills measure.$hs$, 4, 'SUBMITTED', $hs$PostgreSQL docs, Continuous Archiving and Point-in-Time Recovery https://www.postgresql.org/docs/current/continuous-archiving.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['devops', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Distributed systems: what makes them different$hs$, $hs$What makes a system of several services fundamentally different from a single process?$hs$, $hs$- Partial failure: one part can fail while the others keep running, and they may not even know.
- An unreliable network: messages can be lost, delayed, duplicated or reordered, with unpredictable latency.
- No shared memory and no shared clock: each node knows only what it has received, and clocks disagree.

So every remote call can fail in ways a local method call can't, and the design must expect it.$hs$, 1, 'SUBMITTED', $hs$Wikipedia, Fallacies of distributed computing (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Distributed systems: the fallacies of distributed computing$hs$, $hs$Name the fallacies of distributed computing: the false assumptions developers new to distributed systems tend to make.$hs$, $hs$1. The network is reliable.
2. Latency is zero.
3. Bandwidth is infinite.
4. The network is secure.
5. Topology doesn't change.
6. There is one administrator.
7. Transport cost is zero.
8. The network is homogeneous.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Fallacies of distributed computing (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Distributed systems: what a timed-out call tells you$hs$, $hs$The transfer service calls the ledger service to book a transfer, and the call times out. What do you know about whether the booking happened?$hs$, $hs$Nothing. The request may never have arrived, it may have been booked with only the response lost, or it may still be running.

So the caller must be able to find out (query the booking by its transfer id) or retry safely, which requires the booking to be idempotent: a retry with the same transfer id must not book twice.$hs$, 3, 'SUBMITTED', $hs$AWS Builders' Library, Making retries safe with idempotent APIs https://aws.amazon.com/builders-library/making-retries-safe-with-idempotent-APIs/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Overload: shedding load instead of queueing it$hs$, $hs$During a sale, the booking service gets three times the traffic it can handle. Its request queue grows, every request waits longer, and soon all of them time out, even those it could have served. What is load shedding, and why does it keep the service useful?$hs$, $hs$Load shedding: when the service is at capacity, it rejects the excess requests at once (`503`, or `429` with `Retry-After`) instead of queueing them.

Requests in a long queue wait until their clients have already timed out, so the service spends its capacity on answers nobody receives, and its throughput of useful work drops to zero. Rejecting early keeps the queue short: the requests it accepts are served in time, and rejected clients can back off and retry. Shedding the less important requests first (e.g. browsing before payments) keeps the critical ones working.$hs$, 5, 'SUBMITTED', $hs$Google SRE Book, Handling Overload https://sre.google/sre-book/handling-overload/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Consistency: the CAP theorem$hs$, $hs$What does the CAP theorem say?$hs$, $hs$The CAP theorem states that a replicated data store cannot guarantee both consistency and availability while a network partition is occurring. The three properties:
- **Consistency:** every read returns the most recent write, or an error.
- **Availability:** every request receives a non-error response, which may be stale.
- **Partition tolerance:** the system keeps operating even though messages between its nodes are lost or delayed.

Partitions cannot be ruled out in a real network, so the popular "pick 2 of 3" is misleading: the actual choice is how the system behaves during a partition. A CP system refuses some requests, e.g. a bank balance, where an error is better than a double spend. An AP system answers with possibly stale data, e.g. a product catalogue. When there is no partition, a system can provide both consistency and availability.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, CAP theorem (CC BY-SA 4.0) https://en.wikipedia.org/wiki/CAP_theorem$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Consistency: what PACELC adds to CAP$hs$, $hs$What does the PACELC principle add to the CAP theorem?$hs$, $hs$CAP only describes behaviour during a partition. PACELC adds: Else, when there's no partition, the system still trades Latency against Consistency.

E.g. waiting for a replica in another region to confirm every write (synchronous replication) keeps replicas consistent but makes every write slower; replicating asynchronously is fast but replicas can be stale.$hs$, 4, 'SUBMITTED', $hs$Wikipedia, PACELC design principle (CC BY-SA 4.0) https://en.wikipedia.org/wiki/PACELC_design_principle$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Consistency: strong vs eventual$hs$, $hs$What is the difference between strong consistency and eventual consistency?$hs$, $hs$- Strong consistency: once a write completes, every subsequent read, from anywhere, sees it. The system behaves like a single copy.
- Eventual consistency: replicas may return stale data for a while, but if no new writes happen, they all converge to the same value.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Eventual consistency (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Eventual_consistency$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Consistency: read-your-writes and monotonic reads$hs$, $hs$A banking app reads from replicas. What do the guarantees read-your-writes and monotonic reads mean there, and what goes wrong without them?$hs$, $hs$- Read-your-writes: after a customer changes something, their own next reads always show the change. Without it, a customer saves a new beneficiary and doesn't see it in the list, because the read hit a lagging replica.
- Monotonic reads: once a customer has seen a version of the data, they never see an older one. Without it, a transaction appears, then disappears on refresh (served by a staler replica), then reappears.

Both are weaker than strong consistency, and often enough for a good user experience; e.g. read from the primary for a while after a user's write, or pin a user to one replica.$hs$, 5, 'SUBMITTED', $hs$Wikipedia, Consistency model (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Consistency_model$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Consistency: what a bank can afford to be eventual$hs$, $hs$In a retail bank's systems, which data needs strong consistency, and which can be eventually consistent? Give three examples of each.$hs$, $hs$Strong consistency, where a stale read causes real damage:
- the available balance used to authorise a payment or withdrawal
- daily and per-transaction limits
- revoked permissions or a blocked card.

Eventually consistent, where a few seconds of delay are harmless:
- the transaction history view and statements
- notifications and analytics, fraud reports after the fact
- search indexes, e.g. over payees or transactions.$hs$, 5, 'SUBMITTED', $hs$Wikipedia, Eventual consistency (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Eventual_consistency$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$2PC: two-phase commit$hs$, $hs$How does two-phase commit work?$hs$, $hs$A coordinator makes a transaction atomic across several participants (e.g. two databases):

1. Prepare (voting) phase: the coordinator asks every participant to prepare. Each one makes the changes durable but not final, and votes yes (it can commit) or no.
2. Commit phase: if all voted yes, the coordinator tells all to commit; if any voted no or didn't answer, it tells all to abort.

Either all participants commit, or none does.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Two-phase commit protocol (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Two-phase_commit_protocol$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$2PC: why a participant blocks$hs$, $hs$In two-phase commit, a participant has voted "yes" in the prepare phase, and then the coordinator crashes. Why is the participant stuck?$hs$, $hs$By voting yes, the participant promised it can commit whatever happens, so it may neither commit nor abort on its own: only the coordinator knows whether every participant voted yes. It must keep its transaction open, holding its locks, until the coordinator recovers and tells it the decision.

That's why two-phase commit is called a blocking protocol.$hs$, 3, 'SUBMITTED', $hs$Wikipedia, Two-phase commit protocol (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Two-phase_commit_protocol$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$2PC: why microservices avoid it$hs$, $hs$Why is two-phase commit rarely used across microservices?$hs$, $hs$- Blocking: participants hold locks until the coordinator decides; a slow or failed coordinator or participant stalls the others.
- Availability: a transaction can only complete when every participant is up, so services' availability is coupled.
- Latency: extra round trips and longer-held locks on every transaction.
- Support: message brokers like Kafka, HTTP APIs and many cloud services can't take part in an XA transaction.

Microservices use sagas with local transactions instead.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Saga distributed transactions pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/saga$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sagas: what a saga is$hs$, $hs$What is a saga?$hs$, $hs$A way to run a business transaction that spans services without a distributed transaction: a sequence of local transactions, one per service, each committed on its own and triggering the next step.

If a step fails, the steps already completed are undone by compensating transactions: business actions that semantically reverse them, e.g. a refund for a charge, since a committed transaction can't be rolled back.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Saga distributed transactions pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/saga$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sagas: choreography vs orchestration$hs$, $hs$What is the difference between a choreographed and an orchestrated saga? When would you pick each?$hs$, $hs$- Choreography: each service listens for the previous step's event and publishes its own. No central coordinator, but the flow is implicit, spread over services, and hard to follow and change as steps are added.
- Orchestration: an orchestrator tells each service what to do (commands), waits for replies, and keeps the saga's state, including compensation. The flow is explicit in one place, at the cost of a central component.

Choreography for simple flows of a few steps; orchestration for longer flows with branching and compensations, such as payments.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Saga distributed transactions pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/saga$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sagas: a transfer between two services$hs$, $hs$A transfer moves money from a current account (accounts service) to a savings wallet (wallet service). Write the saga's steps and compensations, including what happens if the credit fails.$hs$, $hs$1. Transfer service: create the transfer as `PENDING`, with a unique transfer id.
2. Accounts service: debit the current account (idempotent by transfer id). If it fails (insufficient funds): mark the transfer `REJECTED`, done.
3. Wallet service: credit the wallet (idempotent by transfer id).
4. Transfer service: mark the transfer `COMPLETED`.

If the credit fails permanently (wallet closed): compensate by crediting the current account back (a refund booking, idempotent by transfer id), and mark the transfer `FAILED`.

Every step can be retried safely; a transfer stuck in `PENDING` is picked up by a timeout job that retries or compensates.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Compensating Transaction pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/compensating-transaction$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sagas: no isolation$hs$, $hs$Sagas have no isolation between their steps. What anomalies can that cause, and how are they handled?$hs$, $hs$Each step commits on its own, so other transactions see intermediate states:
- Dirty reads: another process uses data that a later compensation will undo, e.g. money debited for a transfer that's then refunded.
- Lost updates: two sagas overwrite each other's changes.

Countermeasures:
- Semantic locks: mark records as pending (`PENDING` transfer, reserved amount), so others treat them carefully.
- Commutative updates: debits and credits as entries that can be applied in any order.
- Order the steps: do the steps most likely to fail first, and the ones that can't be compensated (sending money to another bank, an email) last.
- Re-read values before acting on them.$hs$, 6, 'SUBMITTED', $hs$microservices.io, Pattern: Saga https://microservices.io/patterns/data/saga.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: what it means$hs$, $hs$What does it mean for an operation to be idempotent? Give an idempotent and a non-idempotent example from banking.$hs$, $hs$Performing it several times has the same effect as performing it once.

- Idempotent: "set the card's status to blocked", "book transfer t-81 (if not already booked)".
- Not idempotent: "add 50 to the balance", "create a new transfer". Each repetition changes the state again.$hs$, 1, 'SUBMITTED', $hs$Stripe API reference, Idempotent requests https://docs.stripe.com/api/idempotent_requests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: why exactly-once delivery isn't possible$hs$, $hs$Why can't a network guarantee that a message is delivered exactly once, and what do systems do instead?$hs$, $hs$When no acknowledgement comes back, the sender can't tell whether the message was lost or only the acknowledgement was. It must either resend (risking a duplicate: at-least-once) or not (risking a loss: at-most-once).

Systems therefore deliver at least once and make the processing idempotent, which gives exactly-once effects even though messages may arrive more than once.$hs$, 3, 'SUBMITTED', $hs$AWS Builders' Library, Making retries safe with idempotent APIs https://aws.amazon.com/builders-library/making-retries-safe-with-idempotent-APIs/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: four techniques$hs$, $hs$Name four techniques to make an operation idempotent.$hs$, $hs$- An idempotency key chosen by the client, stored with the result: a repeated request returns the stored result instead of acting again.
- A unique constraint on a business key (e.g. one booking per transfer id), so a duplicate insert fails and is treated as "already done".
- Naturally idempotent operations: set a value or upsert instead of incrementing.
- Conditional updates with a version or state check: `UPDATE transfer SET status = 'BOOKED' WHERE id = ? AND status = 'PENDING'`.$hs$, 4, 'SUBMITTED', $hs$AWS Builders' Library, Making retries safe with idempotent APIs https://aws.amazon.com/builders-library/making-retries-safe-with-idempotent-APIs/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: two identical requests at the same moment$hs$, $hs$A client retries `POST /payments` with the same idempotency key while the first request is still running. Both requests run this code. What happens, and how do you fix it?

```java
public PaymentResult pay(String idempotencyKey, PaymentRequest request) {
    Optional<PaymentResult> previous = idempotencyKeys.findResult(idempotencyKey);
    if (previous.isPresent()) {
        return previous.get();
    }
    PaymentResult result = paymentProvider.charge(request);
    idempotencyKeys.save(idempotencyKey, result);
    return result;
}
```$hs$, $hs$Both requests check before either has saved the key, both find nothing, and both charge: the customer pays twice. Check-then-act on shared state is a race.

Claim the key first, atomically, and charge only if this request won the claim:

```java
public PaymentResult pay(String idempotencyKey, PaymentRequest request) {
    if (!idempotencyKeys.claim(idempotencyKey)) {   // INSERT ... status 'IN_PROGRESS', unique key; false if it exists
        return idempotencyKeys.findResult(idempotencyKey)
                .orElseThrow(() -> new ConflictException("payment still in progress"));   // answered with 409
    }
    PaymentResult result = paymentProvider.charge(request);
    idempotencyKeys.complete(idempotencyKey, result);
    return result;
}
```$hs$, 6, 'SUBMITTED', $hs$Stripe API reference, Idempotent requests https://docs.stripe.com/api/idempotent_requests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: retries with exponential backoff and jitter$hs$, $hs$Why should retries use exponential backoff with jitter, rather than retrying immediately or at a fixed interval?$hs$, $hs$- Immediate or fixed-interval retries multiply the load on a dependency that's already struggling, and can keep it from recovering (a retry storm).
- Exponential backoff waits longer after each failure (e.g. 100 ms, 200 ms, 400 ms…), giving the dependency room to recover.
- Jitter adds randomness to each wait, so the many clients that failed at the same moment don't all retry at the same moment again.

Also cap the number of retries, retry only idempotent operations, and only on errors that may be transient (timeouts, 503), not on a 400.$hs$, 4, 'SUBMITTED', $hs$AWS Builders' Library, Timeouts, retries, and backoff with jitter https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Idempotency: webhooks arriving twice and out of order$hs$, $hs$A payment provider's webhooks sometimes arrive twice, and sometimes out of order ("payment succeeded" before "payment created"). How do you process them correctly?$hs$, $hs$- Duplicates: store each event's id under a unique constraint, and skip the ones already seen.
- Order: don't rely on arrival order. Either treat the webhook only as a signal and fetch the payment's current state from the provider's API, or apply only forward transitions of a state machine (`CREATED → SUCCEEDED`, never back), creating the payment record if it doesn't exist yet.

Also verify each webhook's signature, answer `2xx` quickly and process asynchronously, and reconcile periodically against the provider for webhooks that never arrived.$hs$, 7, 'SUBMITTED', $hs$Stripe documentation, Webhooks https://docs.stripe.com/webhooks$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Ordering: events by timestamp across machines$hs$, $hs$Why can't you reliably order events from different machines by their wall-clock timestamps?$hs$, $hs$Each machine has its own clock. Clocks drift, and are corrected by NTP, sometimes jumping, even backwards. Two machines' clocks commonly disagree by milliseconds, sometimes far more.

So an event stamped later on one machine may actually have happened before an event on another: a credit can appear to come before the debit that caused it. Order within one entity must come from one source, e.g. a sequence number assigned by the entity's owner.$hs$, 3, 'SUBMITTED', $hs$Wikipedia, Lamport timestamp (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Lamport_timestamp$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Ordering: one account's events across services$hs$, $hs$The accounts service publishes events for each account, and several services apply them. How do you make sure each service applies an account's events in order, and notices when it doesn't?$hs$, $hs$- The owner (the accounts service) assigns each event a sequence number per account: 1, 2, 3…
- Events of an account are delivered in order: the account id as the partition key.
- Each consumer stores the last sequence applied per account:
  - the next number: apply it
  - an older or equal one: a duplicate, skip it
  - a gap: wait, buffer, or fetch the missing state from the owner.$hs$, 5, 'SUBMITTED', $hs$Wikipedia, Lamport timestamp (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Lamport_timestamp$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Replication: leader-follower, multi-leader, leaderless$hs$, $hs$Compare leader-follower, multi-leader and leaderless replication.$hs$, $hs$- Leader-follower (single leader): one node accepts writes and streams them to followers, which serve reads. Simple, no write conflicts; the leader is a bottleneck and needs failover. PostgreSQL works this way.
- Multi-leader: several nodes (e.g. one per region) accept writes and replicate to each other. Local writes everywhere, but concurrent writes to the same data conflict and must be resolved.
- Leaderless: clients write to and read from several replicas, using quorums. Highly available; consistency depends on the quorum settings. Dynamo-style stores such as Cassandra and Riak.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Consistency model (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Consistency_model$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Replication: synchronous vs asynchronous$hs$, $hs$What is the difference between synchronous and asynchronous replication, and what does it mean for a bank's database when the primary fails?$hs$, $hs$- **Asynchronous replication:** the primary confirms a commit as soon as it is durable locally; replicas apply the change shortly afterwards. Commits are fast, but if the primary fails, the commits not yet replicated are lost when a replica takes over.
- **Synchronous replication:** the primary confirms a commit only after at least one replica has also made it durable. No confirmed transaction is lost on failover, but every commit waits for the replica, and the primary stops accepting commits if no synchronous replica is reachable.

For booked transactions, losing a confirmed commit is unacceptable, so a bank replicates synchronously to at least one replica, often in another availability zone, and accepts the extra latency.$hs$, 3, 'SUBMITTED', $hs$PostgreSQL docs, Log-Shipping Standby Servers (synchronous replication) https://www.postgresql.org/docs/current/warm-standby.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: a scheduled job on several instances$hs$, $hs$The interest service runs on 4 instances. A `@Scheduled` method books the daily interest at midnight. What happens, and how do you make it run once?$hs$, $hs$Spring's `@Scheduled` runs on every instance, so interest is booked 4 times.

Make only one instance run it, with a lock in a shared place:
- ShedLock: `@SchedulerLock(name = "dailyInterest", lockAtMostFor = "30m")` takes a lock row in the database (or Redis); the other instances skip the run.
- Or a database advisory lock or a `SELECT ... FOR UPDATE` on a job row.

Make the booking itself idempotent too (one interest booking per account and day, under a unique constraint), so a rerun after a crash can't double-book.$hs$, 4, 'SUBMITTED', $hs$ShedLock https://github.com/lukas-krecan/ShedLock$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: why a distributed lock's lease isn't enough$hs$, $hs$A process holds a distributed lock with a 30-second expiry. Why can two processes still act as the lock holder at the same time, and what are fencing tokens?$hs$, $hs$The holder can pause (a long GC pause, a stalled VM, a slow network) for longer than the expiry. The lock expires, another process acquires it and starts working, and then the first process resumes, still believing it holds the lock, and writes too.

Fencing tokens: every time the lock is granted, the lock service returns a number that increases with each grant. The holder sends it with each write, and the storage rejects writes carrying a token lower than one it has already seen. The paused process's late writes are refused.$hs$, 6, 'SUBMITTED', $hs$Martin Kleppmann, How to do distributed locking https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Partitioning: load-balancing algorithms$hs$, $hs$Compare round robin, least connections and hash-based load balancing.$hs$, $hs$- Round robin: each request goes to the next instance in turn. Simple; fine when requests cost about the same.
- Least connections: each request goes to the instance with the fewest active requests. Better when request costs vary.
- Hash-based (e.g. consistent hashing on a key): the same key always goes to the same instance. Useful when an instance holds state or a cache for that key.$hs$, 2, 'SUBMITTED', $hs$Wikipedia, Consistent hashing (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Consistent_hashing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Partitioning: consistent hashing$hs$, $hs$Why is `hash(key) % numberOfNodes` a poor way to spread cache keys over nodes, and how does consistent hashing fix it?$hs$, $hs$When the number of nodes changes (from 4 to 5), almost every key maps to a different node, so nearly the whole cache misses at once and the database gets the full load.

Consistent hashing places both nodes and keys on a ring of hash values; each key belongs to the next node clockwise. Adding or removing a node moves only the keys of its neighbouring segment, about 1/N of them. Virtual nodes (many positions per node) spread the load evenly.$hs$, 4, 'SUBMITTED', $hs$Wikipedia, Consistent hashing (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Consistent_hashing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Partitioning: a counter that takes a million increments per second$hs$, $hs$A viral workout video gets a million "likes" per second, all incrementing one counter key. Why can't a single key take that load, and how does a sharded counter fix it?$hs$, $hs$Every increment of one key goes to the same node, and to the same row or record on it: a single hot spot, whatever the size of the cluster, with every write contending for the same lock or memory location.

A sharded counter splits the key into N sub-counters (`likes:video42:0` … `likes:video42:N-1`). Each increment goes to a random sub-counter, so the load spreads over N keys, and N nodes. The total is the sum of the sub-counters, read rarely, or cached for a few seconds, which is fine for an approximate count.$hs$, 7, 'SUBMITTED', $hs$Designing Data-Intensive Applications (Martin Kleppmann), chapter 6: skewed workloads and relieving hot spots$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['distributed-systems', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: process vs thread$hs$, $hs$What is the difference between a process and a thread?$hs$, $hs$A process is a running program with its own memory and resources, isolated from other processes. A thread is a path of execution inside a process.

- Threads of one process share its heap; each has its own stack.
- Threads are cheaper to create and switch than processes, but shared memory needs synchronisation.$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: start() vs run()$hs$, $hs$What is the difference between these two calls?

```java
Thread muscle = new Thread(() -> workOut());
muscle.start();   // or: muscle.run();
```$hs$, $hs$```java
muscle.start();   // starts a new thread, which then calls run(): workOut() runs concurrently
muscle.run();     // an ordinary method call: workOut() runs on the current thread, no new thread
```

Calling `start()` a second time on the same `Thread` throws `IllegalThreadStateException`.$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: what join() does$hs$, $hs$What does `muscle.join()` do, called from the main thread?$hs$, $hs$The main thread waits until the `muscle` thread has finished. Afterwards, everything `muscle` did is visible to the main thread: the end of a thread happens-before `join()` returns.

`join(Duration)` (or `join(millis)`) waits at most that long.$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread (join) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: the thread states$hs$, $hs$Name the states a Java thread can be in, as defined by the enum `Thread.State`.$hs$, $hs$`Thread.State` defines six states:
- `NEW`: the thread has been created but not yet started.
- `RUNNABLE`: the thread is executing in the JVM, or ready to execute and waiting for a CPU.
- `BLOCKED`: the thread is waiting to acquire a monitor lock, to enter a `synchronized` block or method that another thread holds.
- `WAITING`: the thread is waiting indefinitely for another thread to act, e.g. in `Object.wait()`, `Thread.join()` or `LockSupport.park()`.
- `TIMED_WAITING`: the thread is waiting for at most a given time, e.g. in `Thread.sleep(...)`, `wait(timeout)` or `join(timeout)`.
- `TERMINATED`: the thread has finished executing.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread.State https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.State.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: sleep() vs wait()$hs$, $hs$What is the difference between `Thread.sleep(1000)` and `lock.wait(1000)`?$hs$, $hs$- `Thread.sleep`: pauses the current thread for the given time. It keeps every lock it holds.
- `wait`: must be called while holding the object's monitor (inside `synchronized (lock)`), and releases that monitor while waiting. It ends when another thread calls `notify`/`notifyAll` on the object, when the timeout expires, or on interruption, and the thread then re-acquires the monitor.

`sleep` is for pausing; `wait` is for waiting on a condition another thread will signal.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Object (wait) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: daemon threads$hs$, $hs$What is a daemon thread, and what happens to it when the program's last non-daemon thread ends?$hs$, $hs$A daemon thread is a background thread that does not prevent the JVM from exiting. The JVM exits when all remaining threads are daemon threads; at that moment the daemon threads are abandoned wherever they are, without running their `finally` blocks.

A thread is made a daemon with `thread.setDaemon(true)`, which must be called before `start()`. Virtual threads are always daemon threads.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread (daemon threads) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: Runnable vs Callable$hs$, $hs$What is the difference between `Runnable` and `Callable`?$hs$, $hs$`Callable<V>.call()` returns a result and may throw a checked exception; `Runnable.run()` returns nothing and can't throw checked exceptions.

Submitted to an `ExecutorService`, a `Callable`'s result (or exception) comes back through the `Future`.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.Callable https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Callable.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread safety: what makes a class thread-safe$hs$, $hs$What does it mean for a class to be thread-safe, and what are the three basic ways to make it so?$hs$, $hs$A class is thread-safe when it behaves correctly when used by several threads at the same time, without the callers having to add synchronisation.

1. Don't share state: keep data confined to one thread (local variables).
2. Don't change shared state: make it immutable.
3. Synchronise access to shared mutable state: locks, atomics or concurrent collections.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread safety: a race condition$hs$, $hs$Several threads call `book` at the same time, and a slot sometimes ends up booked by two athletes, one silently replacing the other. What is the race condition, and how do you fix it?

```java
private final Map<String, Athlete> bookings = new ConcurrentHashMap<>();

void book(String slot, Athlete athlete) {
    if (!bookings.containsKey(slot)) {
        bookings.put(slot, athlete);
    }
}
```$hs$, $hs$It's a check-then-act: two threads can both see the slot as free before either puts, and the second `put` overwrites the first. Each call is thread-safe, but the pair isn't atomic.

```java
private final Map<String, Athlete> bookings = new ConcurrentHashMap<>();

void book(String slot, Athlete athlete) {
    bookings.putIfAbsent(slot, athlete);   // check and put in one atomic step
}
```$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: visibility vs atomicity$hs$, $hs$What is the difference between visibility and atomicity in concurrent code?$hs$, $hs$- Visibility: whether a value written by one thread is seen by other threads. Without synchronisation, another thread may keep seeing an old value.
- Atomicity: whether an operation happens as one indivisible step that no other thread can observe half-done or get in between. `balance += amount` is a read and a write: not atomic.

`volatile` gives visibility, but not atomicity of compound actions; a lock or an atomic class gives both.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks (§17.4 memory model) https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: what volatile guarantees$hs$, $hs$What does declaring a field `volatile` guarantee, and what does it not guarantee?$hs$, $hs$**Guaranteed: visibility and ordering.** In the Java Memory Model, a write to a volatile field happens-before every subsequent read of that field. A reading thread therefore sees the latest value, together with everything the writing thread did before the write.

**Not guaranteed: atomicity of compound actions.** `count++` on a volatile field is still a read followed by a separate write, so concurrent increments can be lost. Compound updates need an atomic variable such as `AtomicInteger`, or a lock.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks (§17.4 memory model) https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: happens-before$hs$, $hs$What is the happens-before relation in the Java Memory Model? Name three rules that create it.$hs$, $hs$If action A happens-before action B, then A's effects are guaranteed visible to B. Without such a relation between a write and a read in different threads, the reader may see a stale value.

Rules that create it:
- Unlocking a monitor happens-before every later lock of the same monitor.
- A write to a `volatile` field happens-before every later read of that field.
- `thread.start()` happens-before every action of the started thread.
- Every action of a thread happens-before another thread returns from `join()` on it.$hs$, 3, 'SUBMITTED', $hs$Java Language Specification, §17.4.5 Happens-before Order https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: safe publication$hs$, $hs$What does it mean to publish an object safely to other threads? Name three ways to do it.$hs$, $hs$Making a newly created object, together with the state its constructor set, visible to other threads, so they never see it half-initialised.

Ways to do it:
- Store the reference in a `volatile` field (or an `AtomicReference`).
- Store it in a field guarded by a lock that the readers also take.
- Put it in a thread-safe collection, such as a `ConcurrentHashMap` or `BlockingQueue`.
- Initialise it in a static initialiser.
- Make the object's fields `final`: other threads see final fields correctly initialised, as long as the constructor doesn't leak `this`.$hs$, 6, 'SUBMITTED', $hs$Aleksey Shipilëv, Java Memory Model Pragmatics https://shipilev.net/blog/2014/jmm-pragmatics/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread safety: lost updates in a shared rep counter$hs$, $hs$Two `Muscle` threads each call `rep()` 100,000 times on the same `RepCounter`. The program usually prints less than 200000. Why are reps lost, and how do you fix `RepCounter`?

```java
class RepCounter {
    private int reps = 0;

    void rep() { reps++; }

    int reps() { return reps; }
}

RepCounter counter = new RepCounter();
Runnable muscle = () -> { for (int i = 0; i < 100_000; i++) counter.rep(); };
Thread left = new Thread(muscle);
Thread right = new Thread(muscle);
left.start(); right.start();
left.join(); right.join();
System.out.println(counter.reps());   // usually less than 200000
```$hs$, $hs$`reps++` is not atomic: it reads `reps`, adds 1 and writes the result back. Two threads can read the same value and both write value + 1, so one rep is lost.

```java
class RepCounter {
    private final AtomicInteger reps = new AtomicInteger();   // atomic read-modify-write

    void rep() { reps.incrementAndGet(); }

    int reps() { return reps.get(); }
}
```

- Making `reps` `volatile` would not help: it makes writes visible, but the read and the write are still two separate steps.
- `synchronized` on both methods also works. `LongAdder` scales better when many threads count and the total is read rarely.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, package java.util.concurrent.atomic https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: a stop flag without volatile$hs$, $hs$A `Muscle` thread works out until another thread calls `rest()`. Can `run()` keep working out forever, even after `rest()` has been called? Explain why, and fix the class.

```java
class Muscle implements Runnable {
    private boolean tired = false;

    void rest() { tired = true; }   // called from another thread

    @Override
    public void run() {
        while (!tired) {
            workOut();
        }
    }
}
```$hs$, $hs$Yes. Nothing creates a happens-before relation between the write in `rest()` and the reads in `run()`, so the Java Memory Model doesn't guarantee that the `Muscle` thread ever sees `tired = true`. The JIT compiler may read the field once and turn the loop into `while (true)`.

```java
class Muscle implements Runnable {
    private volatile boolean tired = false;   // a volatile write happens-before every later read

    void rest() { tired = true; }

    @Override
    public void run() {
        while (!tired) {
            workOut();
        }
    }
}
```

`volatile` is enough here because the flag is only written and read, never read-modify-written. Interruption (`thread.interrupt()` and `isInterrupted()`) is the standard alternative, and also wakes a thread blocked in `sleep` or `wait`.$hs$, 5, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks (§17.4 memory model) https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: double-checked locking needs volatile$hs$, $hs$Thread A and thread B both call `rates().eurTo("USD")` at about the same time, on a fresh `RateService`. Thread A gets there first and builds the table. Thread B can crash with a `NullPointerException` inside `eurTo`, even though `rates()` never returns `null` to it. How, and what one-word change fixes it?

```java
class RateTable {
    private Map<String, BigDecimal> rates;   // not final

    RateTable() {
        rates = loadRates();                 // never returns null
    }

    BigDecimal eurTo(String currency) {
        return rates.get(currency);
    }
}

class RateService {
    private RateTable table;                 // not volatile

    RateTable rates() {
        if (table == null) {                 // first check: no lock
            synchronized (this) {
                if (table == null) {         // second check: under the lock
                    table = new RateTable();
                }
            }
        }
        return table;
    }
}
```$hs$, $hs$Without a happens-before relationship, thread B can observe the reference to the new `RateTable` before it observes the writes made by its constructor: `table` is not null, yet the object's `rates` field still reads as `null`, so `rates.get(...)` throws.

1. `table = new RateTable()` consists of several writes: the constructor's write to `rates`, then the write of the reference into `table`.
2. Thread B's first check reads `table` without acquiring the lock. Only acquiring the same lock would create a happens-before edge from thread A's writes to thread B's reads; without one, the compiler and the CPU may make those writes visible in any order.
3. Thread B can therefore see `table` set, skip the `synchronized` block, and read `rates` before the constructor's write is visible to it.

The fix is one word:

```java
private volatile RateTable table;
```

A write to a volatile field happens-before every subsequent read of that field, so everything thread A did before publishing the reference (running the constructor) is visible to any thread that reads the non-null `table`.

(Declaring `RateTable.rates` final would also protect that field, because final fields set in a constructor are visible to every thread that sees the object. Only `volatile` fixes the pattern itself.)$hs$, 7, 'SUBMITTED', $hs$The "Double-Checked Locking is Broken" Declaration (Pugh et al.) https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: final fields and unsafe publication$hs$, $hs$Thread 1 creates a `Membership` and stores it in the plain (not `volatile`) field `shared`; thread 2 reads it. Thread 2 can print `null`. How, and which one-word change to `Membership` prevents it?

```java
class Membership {
    private String level;

    Membership(String level) { this.level = level; }

    String level() { return level; }
}

// thread 1
shared = new Membership("GOLD");

// thread 2
Membership m = shared;
if (m != null) System.out.println(m.level());
```$hs$, $hs$Nothing orders thread 1's writes for thread 2: it can see the write of `shared` before the write of `level` inside the constructor, so it reads the field's default value, `null`.

```java
private final String level;   // final: guaranteed visible once the constructor has finished
```

The memory model guarantees that a thread which sees a reference to an object after its constructor finished also sees the correctly set values of its `final` fields, as long as `this` didn't escape during construction. `shared` itself is still published unsafely; immutable objects with `final` fields tolerate that.$hs$, 7, 'SUBMITTED', $hs$Java Language Specification, §17.5 final Field Semantics https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testing: a concurrency test that passes on a broken class$hs$, $hs$`RepCounter.rep()` does `reps++` without any synchronisation, yet this test passes every time. Why does it miss the race, and how do you make the race likely to show?

```java
@Test
void countsEveryRep() throws InterruptedException {
    RepCounter counter = new RepCounter();
    Thread left = new Thread(() -> { for (int i = 0; i < 100; i++) counter.rep(); });
    Thread right = new Thread(() -> { for (int i = 0; i < 100; i++) counter.rep(); });
    left.start();
    right.start();
    left.join();
    right.join();
    assertEquals(200, counter.reps());
}
```$hs$, $hs$The loops hardly ever overlap: 100 increments take microseconds, so `left` has usually finished before `right` has even started running. No overlap, no race.

Make the threads run at the same moment, for longer:

```java
CountDownLatch startGate = new CountDownLatch(1);
Runnable reps = () -> {
    try {
        startGate.await();                           // every thread waits here...
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
    }
    for (int i = 0; i < 100_000; i++) counter.rep();
};
// start 8 threads running reps, then:
startGate.countDown();                               // ...and all start together
```

- More threads and many more iterations, repeated several times.
- Even then a green test proves little: a race shows by chance. For memory-model bugs, use a tool built for them, such as OpenJDK's jcstress.$hs$, 6, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CountDownLatch https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: what synchronized guarantees$hs$, $hs$What two guarantees does a `synchronized` block give threads that synchronise on the same object?$hs$, $hs$1. Mutual exclusion: only one thread at a time can hold the object's monitor and run the block.
2. Visibility: releasing the monitor happens-before every later acquisition of it, so the next thread sees all changes made inside the block.

Both hold only between threads that use the same lock object.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 17: Threads and Locks (§17.1 synchronization) https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: static synchronized vs synchronized$hs$, $hs$Which lock does each method take? Can one thread run `deposit` while another runs `nextAccountNumber`?

```java
class Account {
    synchronized void deposit(long cents) { ... }
    static synchronized long nextAccountNumber() { ... }
}
```$hs$, $hs$```java
class Account {
    synchronized void deposit(long cents) { ... }            // locks this Account instance
    static synchronized long nextAccountNumber() { ... }     // locks the Account.class object
}
```

Yes: they use different locks, so they don't exclude each other.$hs$, 3, 'SUBMITTED', $hs$Java Language Specification, §8.4.3.6 synchronized Methods https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: synchronized vs ReentrantLock$hs$, $hs$What can a `ReentrantLock` do that a `synchronized` block can't, and what does it cost you?$hs$, $hs$It adds:
- `tryLock()`, optionally with a timeout: give up instead of waiting forever.
- `lockInterruptibly()`: waiting for the lock can be interrupted.
- An optional fair ordering of waiting threads.
- Several `Condition`s per lock.

The cost: you must release it yourself, always in `finally`:

```java
lock.lock();
try {
    account.withdraw(cents);
} finally {
    lock.unlock();
}
```$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.ReentrantLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Deadlock: the four conditions$hs$, $hs$What is a deadlock, and which four conditions must all hold for one to occur?$hs$, $hs$Two or more threads each wait for a lock another one holds, so none of them can ever continue.

1. Mutual exclusion: a lock is held by one thread at a time.
2. Hold and wait: a thread holds one lock while waiting for another.
3. No preemption: a lock can't be taken away from its holder.
4. Circular wait: a cycle of threads, each waiting for the next.

Breaking any one of them prevents deadlock; in practice, circular wait (by always taking locks in the same order).$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.ReentrantLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Deadlock: transfers between two accounts$hs$, $hs$Thread 1 calls `transfer(alice, bob, 100)` while thread 2 calls `transfer(bob, alice, 50)`, and both threads sometimes hang forever. Why, and how do you fix `transfer`?

```java
void transfer(Account from, Account to, long cents) {
    synchronized (from) {
        synchronized (to) {
            from.withdraw(cents);
            to.deposit(cents);
        }
    }
}
```$hs$, $hs$This is a deadlock: two threads each hold a lock the other needs, and each waits for the other forever. Thread 1 locks `alice` and waits for `bob`; thread 2 locks `bob` and waits for `alice`. The cause is a circular wait, which arises because the two threads acquire the same locks in opposite orders.

The standard fix is a global lock order: every thread acquires the locks in the same order, here by account id, so a circular wait cannot form.

```java
void transfer(Account from, Account to, long cents) {
    Account first = from.id() < to.id() ? from : to;   // every thread locks the lower id first
    Account second = (first == from) ? to : from;
    synchronized (first) {
        synchronized (second) {
            from.withdraw(cents);
            to.deposit(cents);
        }
    }
}
```

An alternative is `ReentrantLock.tryLock` with a timeout on both locks: a thread that cannot get the second lock releases the first and retries.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.ReentrantLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: ReadWriteLock and StampedLock$hs$, $hs$An exchange-rate table is read thousands of times per second and updated once a minute. Which lock fits better than `synchronized`, and why?$hs$, $hs$A `ReentrantReadWriteLock`: any number of readers can hold the read lock at the same time; only writers need exclusive access. With `synchronized`, readers would block each other for nothing.

A `StampedLock` goes further with optimistic reads: read without locking, then `validate(stamp)` and retry under a read lock only if a write happened in between. (It isn't reentrant.) Neither helps when writes are frequent.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.StampedLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/StampedLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: why wait() is called in a loop$hs$, $hs$Why must `wait()` be called inside a `while` loop that checks the condition, and not an `if`?

```java
synchronized (queue) {
    while (queue.isEmpty()) {
        queue.wait();
    }
    return queue.remove();
}
```$hs$, $hs$A thread can return from `wait()` while the condition is still false:
- spurious wakeups are allowed: `wait` may return without any `notify`
- another thread may have woken first and taken the element before this thread re-acquired the monitor.

So the condition must be checked again after every wake-up. (Calling `wait` without holding the monitor throws `IllegalMonitorStateException`.) In new code, a `BlockingQueue` does all this for you.$hs$, 6, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Object (wait) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Deadlock: deadlock vs livelock vs starvation$hs$, $hs$What is the difference between a deadlock, a livelock and starvation?$hs$, $hs$- Deadlock: threads are blocked forever, each waiting for another.
- Livelock: threads keep running and reacting to each other (e.g. both backing off and retrying in step), but none makes progress.
- Starvation: one thread never gets the resource it needs, because others always get it first.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, package java.util.concurrent https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: synchronizing on a boxed field$hs$, $hs$Why doesn't this lock protect `pending`?

```java
private Integer pending = 0;

void addPending() {
    synchronized (pending) {
        pending++;
    }
}
```$hs$, $hs$`pending++` assigns a new `Integer` object to the field, so the next thread locks a different object than the one the current thread holds. On top of that, small `Integer` values are cached and shared, so unrelated code may lock the very same object.

```java
private final Object lock = new Object();   // a dedicated, final lock object
private int pending = 0;

void addPending() {
    synchronized (lock) {
        pending++;
    }
}
```

Or simply an `AtomicInteger`.$hs$, 6, 'SUBMITTED', $hs$Java Language Specification, §5.1.7 Boxing Conversion https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: synchronized around a @Transactional method$hs$, $hs$To stop double bookings, `book` is `synchronized`. Two concurrent requests for the last spot in a class still both succeed, on a single instance. Why?

```java
@Service
class BookingService {

    @Transactional
    public synchronized void book(long classId, long athleteId) {
        int taken = bookings.countByClassId(classId);
        if (taken >= capacity(classId)) throw new ClassFullException();
        bookings.save(new Booking(classId, athleteId));
    }
}
```$hs$, $hs$The lock ends before the transaction does. `@Transactional` works through a proxy: the proxy starts the transaction, calls `book` (which takes and releases the lock), and only then commits. The second request can take the lock after the first released it but before the first committed, so its count doesn't see the new booking yet.

Let the database decide instead, inside the transaction:
- lock the class row first (`SELECT ... FOR UPDATE`, or `@Lock(PESSIMISTIC_WRITE)` on the repository method), then count and insert
- or claim the spot atomically: `UPDATE gym_class SET taken = taken + 1 WHERE id = ? AND taken < capacity`, and book only if one row was updated.

A JVM lock also protects nothing once a second instance runs.$hs$, 7, 'SUBMITTED', $hs$Spring Framework docs, Declarative transaction implementation https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-decl-explained.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: a thread-safe account with transfers$hs$, $hs$In an in-memory trading simulator, many threads move money between `Account`s at the same time. A withdrawal on one account can use an `AtomicLong` balance with a compare-and-set loop. Why does that not make a transfer between two accounts safe, and how do you write `transfer` so it is atomic, never overdraws and can't deadlock?$hs$, $hs$A transfer changes two balances, and two separate atomics can't change together: another thread can see the money gone from one account and not yet in the other, and the overdraft check and the debit aren't one step across both.

Lock both accounts, always in the same global order (by account id), and check and move the money while holding both:

```java
void transfer(Account from, Account to, long cents) {
    Account first = from.id < to.id ? from : to;
    Account second = first == from ? to : from;
    first.lock.lock();
    try {
        second.lock.lock();
        try {
            if (from.balance < cents) throw new InsufficientFundsException();
            from.balance -= cents;   // a plain long, guarded by the account's lock
            to.balance += cents;
        } finally {
            second.lock.unlock();
        }
    } finally {
        first.lock.unlock();
    }
}
```

The fixed order removes the circular wait, so two opposite transfers can't deadlock.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.ReentrantLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Deadlock: a service that freezes with idle CPU$hs$, $hs$Under load, the payment service stops responding while its CPU usage is near zero. What do you capture to find out what its threads are doing, how does it tell you whether there's a deadlock, and how do you fix what you find?$hs$, $hs$Low CPU with no progress means the threads are waiting rather than working. The diagnostic tool is a **thread dump**: a snapshot of every thread's state and stack trace, including the locks each one holds and waits for. Take several, a few seconds apart, with `jcmd <pid> Thread.print` (or `jstack <pid>`). It is safe on a production JVM: it pauses the application only briefly, and is the standard first step in a production incident.

Reading the dumps:
- **Deadlock:** the JVM detects lock cycles itself and reports them at the end of the dump, as "Found one Java-level deadlock", naming the threads and the lock each holds and waits for.
- **Otherwise,** threads stuck at the same place across dumps show what they wait for: many threads `BLOCKED` on one monitor (lock contention), `WAITING` in the connection pool's `getConnection` (an exhausted pool), or in a socket read (a slow downstream call without a timeout).

The fix follows the cause:
- Deadlock: acquire locks in one global order, or use `tryLock` with a timeout.
- Contention: hold the lock for less time, or narrow what it guards.
- Exhausted pool: find what holds connections too long (a slow query, a remote call inside a transaction), and set a pool acquisition timeout.
- Slow downstream: set connect and read timeouts on the client, and add a circuit breaker.$hs$, 7, 'SUBMITTED', $hs$The jcmd Command (Java SE 21) https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: a total that's wrong while transfers run$hs$, $hs$Transfers between accounts are correct: each locks both accounts in id order. Yet this report, run while transfers happen, sometimes returns a total that's off by exactly one transfer's amount. No money is lost. Why?

```java
long totalBalance(List<Account> accounts) {
    long total = 0;
    for (Account account : accounts) {
        account.lock.lock();
        try {
            total += account.balance;
        } finally {
            account.lock.unlock();
        }
    }
    return total;
}
```$hs$, $hs$Each balance is read safely, but the balances are read at different moments, so the total is not a consistent snapshot. A transfer that runs between two reads can move money from an account the loop has not reached yet into one it has already counted; that amount is missed. In the opposite direction, it is counted twice. The computed total never existed at any single point in time.

A consistent total requires every balance from the same moment:
- acquire all the accounts' locks, in id order, before reading, and release them afterwards (this pauses transfers while the report runs);
- or read from a snapshot: in a database, a single transaction at `REPEATABLE READ` sees every row as of the transaction's start.$hs$, 8, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.ReentrantLock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Deadlock: reading it in a thread dump$hs$, $hs$The banking service stopped answering requests, with the CPU near zero. `jcmd <pid> Thread.print` ends with this. Which two code paths deadlock, and how do you fix it?

```
Found one Java-level deadlock:
=============================
"statement-job":
  waiting to lock monitor 0x00007f3a2c004e58 (object 0x000000071f2b3a10, a com.bank.Account),
  which is held by "http-nio-8080-exec-12"

"http-nio-8080-exec-12":
  waiting to lock monitor 0x00007f3a2c007f28 (object 0x000000071f2b3c48, a com.bank.Ledger),
  which is held by "statement-job"

Java stack information for the threads listed above:
===================================================
"statement-job":
	at com.bank.Account.balance(Account.java:42)
	- waiting to lock <0x000000071f2b3a10> (a com.bank.Account)
	at com.bank.StatementService.closeMonth(StatementService.java:31)
	- locked <0x000000071f2b3c48> (a com.bank.Ledger)

"http-nio-8080-exec-12":
	at com.bank.Ledger.record(Ledger.java:18)
	- waiting to lock <0x000000071f2b3c48> (a com.bank.Ledger)
	at com.bank.Account.withdraw(Account.java:57)
	- locked <0x000000071f2b3a10> (a com.bank.Account)
	at com.bank.TransferService.transfer(TransferService.java:24)

Found 1 deadlock.
```$hs$, $hs$Follow the "waiting to lock … which is held by" lines: each thread holds the lock the other waits for.

- The request thread is in `Account.withdraw`, which is `synchronized` on the account (`locked <…3a10>`), and calls `Ledger.record`, which needs the ledger's lock: account, then ledger.
- The month-end job is in `StatementService.closeMonth`, which holds the ledger's lock (`locked <…3c48>`), and calls `Account.balance`, which needs the account's lock: ledger, then account.

Two code paths take the same two locks in opposite orders: a circular wait.

The fix: one lock order everywhere, or better, don't call into another object while holding a lock. Record the ledger entry after `withdraw` has released the account's lock (an "open call"), and have `closeMonth` read the balances before taking the ledger's lock. The JVM finds such cycles for both `synchronized` and `java.util.concurrent` locks; the deadlocked threads show as `BLOCKED` (or `WAITING` for a `ReentrantLock`).$hs$, 6, 'SUBMITTED', $hs$Java Troubleshooting Guide (Java 21), Troubleshoot Process Hangs and Loops https://docs.oracle.com/en/java/javase/21/troubleshoot/troubleshoot-process-hangs-and-loops.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locks: a ReadWriteLock around an LRU cache$hs$, $hs$An LRU (least recently used) cache holds a fixed number of entries; when it's full, it evicts the entry that was used longest ago. This one holds training plans. Reads outnumber writes 100 to 1, so it lets readers share a read lock. Under load it returns wrong plans, and once a thread spun at 100% CPU inside `get`. Why?

```java
class PlanCache {
    // accessOrder = true: iteration runs from the least to the most recently used entry
    private final Map<Long, Plan> plans = new LinkedHashMap<>(16, 0.75f, true) {
        // Called after every put: returning true evicts the eldest (least recently used) entry.
        // size() is the map's own entry count.
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Plan> eldest) {
            return size() > 1_000;
        }
    };
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    Plan get(long id) {
        lock.readLock().lock();
        try {
            return plans.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    void put(long id, Plan plan) {
        lock.writeLock().lock();
        try {
            plans.put(id, plan);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```$hs$, $hs$**The cause: in access order, `get` changes the map.**

A `LinkedHashMap` keeps its entries in a doubly linked list next to the hash table. With `accessOrder = true`, that list records how recently each entry was used: every `get` of an existing key unlinks the entry and relinks it at the tail, as the most recently used. The `LinkedHashMap` Javadoc counts this as a structural modification, the same kind of change as a `put`.

**Why the read lock doesn't protect it:**
- A read lock is shared: any number of threads may hold it at once. It is only safe around code that changes nothing.
- Here several threads call `get` at the same time, and each one rewrites the `before` / `after` pointers of the same list. `LinkedHashMap` isn't thread-safe, so these interleaved pointer updates corrupt the list.

**The symptoms:**
- Wrong plans: entries get lost from the list or evicted in the wrong order, and the hash table and the list stop agreeing.
- 100% CPU: a corrupted link can point back to an earlier node, forming a cycle; the next thread that walks the list loops forever.

**The fix:**
- Every access changes the order, so every access needs the exclusive lock: take the write lock in `get` too, or make both methods `synchronized`. Reads are then serialised, which is the price of exact LRU order.
- In production, use a concurrent cache such as Caffeine: it records reads in buffers and applies them to an approximate LRU order later, so reads don't have to lock.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.LinkedHashMap https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Atomics: how AtomicInteger works$hs$, $hs$How can `AtomicInteger.incrementAndGet()` be thread-safe without a lock?$hs$, $hs$It uses compare-and-set (CAS), an atomic CPU instruction: read the current value, compute the new one, and write it only if the value is still the one read. If another thread changed it in between, the write fails and the loop retries with the new value.

No thread ever blocks, which makes it fast under low and moderate contention.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.atomic.AtomicInteger https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Atomics: AtomicLong vs LongAdder$hs$, $hs$A counter of processed transfers is incremented by 64 threads and read once a second for metrics. Why is `LongAdder` better here than `AtomicLong`?$hs$, $hs$With many threads incrementing one `AtomicLong`, they keep failing each other's compare-and-set and retrying. A `LongAdder` spreads the increments over several internal cells, so threads rarely collide, and `sum()` adds the cells up when read.

`AtomicLong` remains the choice when you need an exact value on every read or operations like `compareAndSet`.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.atomic.LongAdder https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/LongAdder.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: a lost update with get and put$hs$, $hs$Many threads call `recordTransfer` for the same account, and the counts come out too low, even though the map is a `ConcurrentHashMap`. Why, and how do you fix it?

```java
private final Map<String, Integer> transfersPerAccount = new ConcurrentHashMap<>();

void recordTransfer(String accountId) {
    Integer current = transfersPerAccount.getOrDefault(accountId, 0);
    transfersPerAccount.put(accountId, current + 1);
}
```$hs$, $hs$Each call to the map is atomic, but the read and the write are two separate calls. Two threads can read the same count and both write count + 1, so one update is lost.

```java
private final Map<String, Integer> transfersPerAccount = new ConcurrentHashMap<>();

void recordTransfer(String accountId) {
    transfersPerAccount.merge(accountId, 1, Integer::sum);   // read, add and write as one atomic step
}
```$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ConcurrentHashMap https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: ConcurrentHashMap vs a synchronized map$hs$, $hs$What is the difference between `ConcurrentHashMap` and `Collections.synchronizedMap(new HashMap<>())`?$hs$, $hs$- The synchronized wrapper puts one lock around the whole map: only one thread at a time can use it, and iterating still requires locking the map yourself.
- `ConcurrentHashMap` lets many threads read and write at once (reads don't lock; writes lock only the affected bin), and offers atomic compound operations such as `putIfAbsent`, `computeIfAbsent` and `merge`.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ConcurrentHashMap https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: ConcurrentHashMap nulls and iteration$hs$, $hs$Can a `ConcurrentHashMap` hold `null` keys or values? What does its iterator do when another thread changes the map during iteration?$hs$, $hs$- No `null` keys or values: `get` returning `null` must unambiguously mean "absent", since another thread could change the map between `containsKey` and `get`.
- Its iterators are weakly consistent: they never throw `ConcurrentModificationException`, and may or may not reflect changes made after the iteration started.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ConcurrentHashMap https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: when CopyOnWriteArrayList fits$hs$, $hs$When is `CopyOnWriteArrayList` a good choice, and when is it a bad one?$hs$, $hs$Good: many reads and iterations, very few writes, such as a list of event listeners. Reads take no lock, and every iteration sees a stable snapshot.

Bad: frequent writes or large lists. Every add or remove copies the whole underlying array.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CopyOnWriteArrayList https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: a producer-consumer handoff$hs$, $hs$One thread reads payment files and produces payments; four threads send them to the bank. Hand the payments over between the threads, so the reader can't run far ahead of the senders.$hs$, $hs$A bounded `BlockingQueue`:

```java
BlockingQueue<Payment> queue = new ArrayBlockingQueue<>(1_000);

// reader thread
queue.put(payment);              // blocks while the queue is full: backpressure

// each sender thread
Payment payment = queue.take();  // blocks while the queue is empty
send(payment);
```

The bound keeps memory in check: when the senders fall behind, the reader waits.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.BlockingQueue https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: CountDownLatch vs CyclicBarrier$hs$, $hs$What is the difference between a `CountDownLatch` and a `CyclicBarrier`?$hs$, $hs$- `CountDownLatch`: one or more threads wait until a count, decremented by other threads with `countDown()`, reaches zero. It's used once: e.g. the main thread waits until 5 workers have loaded their data.
- `CyclicBarrier`: a fixed group of threads wait for each other at a common point, then all continue together. It resets and can be reused, e.g. for each round of a simulation.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CountDownLatch https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: limiting concurrent calls with a Semaphore$hs$, $hs$The payment provider allows at most 5 concurrent calls. Make `charge` respect that limit, whatever the number of calling threads.

```java
Receipt charge(Payment payment) {
    return provider.charge(payment);
}
```$hs$, $hs$```java
private final Semaphore permits = new Semaphore(5);

Receipt charge(Payment payment) throws InterruptedException {
    permits.acquire();                    // waits while 5 calls are in progress
    try {
        return provider.charge(payment);
    } finally {
        permits.release();                // always give the permit back
    }
}
```

`tryAcquire(timeout, unit)` lets the caller give up instead of waiting indefinitely.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.Semaphore https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: a plain HashMap shared between threads$hs$, $hs$Several threads put entries into the same plain `HashMap` at the same time. What can happen?$hs$, $hs$The map's behaviour is undefined, because `HashMap` isn't synchronised:

- Lost entries: two threads write to the same bucket, and one write overwrites the other.
- A corrupted structure: a resize running concurrently with puts can lose or mislink entries; in old JDKs (Java 7) it could even create a cycle that made `get` loop forever.
- A wrong `size()`, and readers seeing partly updated state.

Use `ConcurrentHashMap`.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.HashMap (not synchronized) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Concurrent collections: loading each key only once$hs$, $hs$Build an in-memory cache for exchange rates that loads a missing rate from a slow service. Concurrent requests for the same currency must trigger only one load, and requests for other currencies must not wait for it.$hs$, $hs$Cache the future, not the value. The first caller for a currency creates a `CompletableFuture` that performs the load; every later caller for that currency receives the same future and waits on it, so each rate is loaded once.

```java
private final ConcurrentHashMap<String, CompletableFuture<Rate>> rates = new ConcurrentHashMap<>();

CompletableFuture<Rate> rate(String currency) {
    CompletableFuture<Rate> future = rates.computeIfAbsent(currency,
            c -> CompletableFuture.supplyAsync(() -> rateService.load(c), executor));   // starts the load, doesn't wait
    future.whenComplete((rate, error) -> {
        if (error != null) rates.remove(currency, future);   // don't cache failures
    });
    return future;
}
```

- `computeIfAbsent` holds the map's lock only while it creates the future; the slow load runs outside the lock, so other currencies are not blocked.
- A production cache also needs a size bound and expiry; in practice, Caffeine's `AsyncLoadingCache` provides all of this.$hs$, 8, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ConcurrentHashMap (computeIfAbsent) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Atomics: a side effect inside updateAndGet$hs$, $hs$Under heavy load, the audit log sometimes records the same withdrawal twice, although the balance is right. Why?

```java
long withdraw(long cents) {
    return balance.updateAndGet(current -> {
        audit.log("withdraw " + cents);
        return current - cents;
    });
}
```$hs$, $hs$`updateAndGet` is a compare-and-set loop: it applies the function to the current value and tries to store the result; if another thread changed the balance in between, it applies the function again to the new value. Under contention the function runs more than once, and so does the log call. That's why the function must be free of side effects.

```java
long withdraw(long cents) {
    long after = balance.addAndGet(-cents);   // or updateAndGet with a pure function
    audit.log("withdraw " + cents);           // outside: runs exactly once
    return after;
}
```$hs$, 6, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.atomic.AtomicLong (updateAndGet) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicLong.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Coordination: a bounded buffer with Lock and Conditions$hs$, $hs$Without using a `BlockingQueue`, write a bounded buffer of payments shared by one reader thread and four sender threads: `put` waits while the buffer is full, `take` waits while it's empty. Use a `ReentrantLock`.

```java
class PaymentBuffer {
    PaymentBuffer(int capacity) { ... }
    void put(Payment payment) throws InterruptedException { ... }
    Payment take() throws InterruptedException { ... }
}
```$hs$, $hs$One lock, two conditions: senders wait on "not empty", the reader on "not full".

```java
class PaymentBuffer {
    private final Payment[] items;
    private int head, tail, count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    PaymentBuffer(int capacity) {
        items = new Payment[capacity];
    }

    void put(Payment payment) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[tail] = payment;
            tail = (tail + 1) % items.length;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    Payment take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Payment payment = items[head];
            items[head] = null;   // let the payment be garbage collected
            head = (head + 1) % items.length;
            count--;
            notFull.signal();
            return payment;
        } finally {
            lock.unlock();
        }
    }
}
```

- `await` in a `while`, not an `if`: a thread can wake up spuriously, or another thread can take the slot first, so the condition must be checked again.
- Two conditions, so `signal` wakes a thread that can go on. With one condition (or `wait`/`notify`), a sender could wake another sender, while the reader that could proceed sleeps on; there you'd need `signalAll`.
- `unlock` in `finally`, so an exception can't leave the lock held.

In production, use `ArrayBlockingQueue`: it's built exactly this way.$hs$, 6, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.locks.Condition https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/Condition.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: a rate limit per client on the bank API$hs$, $hs$The bank API allows each client app 10 calls per second, with bursts of up to 10. Write `boolean tryAcquire(String clientId)` for a service where many threads call it at once, without a background thread to refill anything. What else do you have to take care of?$hs$, $hs$A token bucket per client, refilled lazily from the time elapsed since its last use:

```java
class ClientRateLimiter {
    private static final double CAPACITY = 10;
    private static final double TOKENS_PER_NANO = 10 / 1e9;   // 10 per second
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    boolean tryAcquire(String clientId) {
        return buckets.computeIfAbsent(clientId, id -> new Bucket(System.nanoTime())).tryTake();
    }

    private static final class Bucket {
        private double tokens = CAPACITY;
        private long refilledAt;

        Bucket(long now) {
            refilledAt = now;
        }

        synchronized boolean tryTake() {
            long now = System.nanoTime();
            tokens = Math.min(CAPACITY, tokens + (now - refilledAt) * TOKENS_PER_NANO);
            refilledAt = now;
            if (tokens < 1) {
                return false;
            }
            tokens--;
            return true;
        }
    }
}
```

- Refill and take must happen atomically, or two threads both see the last token. A lock per bucket keeps different clients from waiting on each other; `computeIfAbsent` creates each bucket exactly once.
- `System.nanoTime()`, not `currentTimeMillis()`: it never jumps when the wall clock is adjusted.
- The map keeps every client ever seen. Evict idle buckets, e.g. a cache with expire-after-access, or a sweep that drops buckets idle long enough to be full again (dropping them changes nothing).
- With several instances, each one limits on its own: a shared limit needs a shared store, such as Redis.

Libraries such as Bucket4j or Resilience4j's `RateLimiter` do this for you.$hs$, 8, 'SUBMITTED', $hs$Token bucket (Wikipedia) https://en.wikipedia.org/wiki/Token_bucket$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: why use a thread pool$hs$, $hs$Why submit tasks to an `ExecutorService` thread pool instead of starting a new platform `Thread` for each task?$hs$, $hs$- Reuse: platform threads are expensive to create; a pool reuses a fixed set.
- A bound on concurrency: at most N tasks run at once; the rest wait in a queue instead of exhausting memory and CPU.
- Results and errors: `submit` returns a `Future` with the task's result or exception.
- Lifecycle: `shutdown()` stops the pool cleanly.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ExecutorService https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: how ThreadPoolExecutor handles a new task$hs$, $hs$A `ThreadPoolExecutor` has a core size of 4, a maximum size of 8 and a queue of capacity 100. In what order does it start threads, queue tasks and reject them as tasks keep arriving?$hs$, $hs$1. Fewer than 4 threads running: start a new thread for the task.
2. 4 threads busy: put the task in the queue.
3. Queue full (100 waiting): start extra threads, up to 8.
4. 8 threads busy and the queue full: reject the task with the rejection policy (by default, `AbortPolicy` throws `RejectedExecutionException`).

The pool only grows beyond its core size once the queue is full.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ThreadPoolExecutor https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: the danger of Executors.newFixedThreadPool in a server$hs$, $hs$What can go wrong in a busy server with `Executors.newFixedThreadPool(10)`, and with `Executors.newCachedThreadPool()`?$hs$, $hs$- `newFixedThreadPool(10)` has an unbounded queue: when tasks arrive faster than 10 threads can finish them, the queue grows until the service runs out of memory, and latency grows long before that.
- `newCachedThreadPool()` creates a new thread whenever none is idle, without limit: under load it can create thousands of threads.

Configure a `ThreadPoolExecutor` with a bounded queue and an explicit rejection policy instead.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.Executors https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: sizing a thread pool$hs$, $hs$How many threads should a pool have for CPU-bound work, and how many for tasks that spend most of their time waiting on a database?$hs$, $hs$- CPU-bound: about the number of CPU cores (`Runtime.getRuntime().availableProcessors()`). More threads only add context switching.
- Mostly waiting: more threads than cores, roughly cores × (1 + wait time / compute time), but never more than the downstream resource allows: no point in 200 threads for a connection pool of 20.

For tasks that block on I/O, virtual threads remove the need for sizing, but the downstream limit still applies.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ThreadPoolExecutor https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: shutdown() vs shutdownNow()$hs$, $hs$What is the difference between `shutdown()` and `shutdownNow()` on an `ExecutorService`?$hs$, $hs$- `shutdown()`: accepts no new tasks, but lets running and queued tasks finish.
- `shutdownNow()`: also interrupts running tasks, and returns the queued tasks that never started.

Neither waits: call `awaitTermination(timeout, unit)` to wait for the pool to finish.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ExecutorService https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Threads: handling InterruptedException$hs$, $hs$What's wrong with this, and what should the `catch` block do?

```java
try {
    Thread.sleep(retryDelay);
} catch (InterruptedException e) {
    // ignore
}
```$hs$, $hs$Interruption is how other code (e.g. `shutdownNow()`) asks this thread to stop. Catching the exception clears the thread's interrupt flag, so swallowing it hides the request, and the thread keeps working.

```java
try {
    Thread.sleep(retryDelay);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // restore the flag for the code above
    return;                               // and stop what this method was doing
}
```

Even better, let the method declare `throws InterruptedException` and propagate it.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Thread (interrupt) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: a program that never exits$hs$, $hs$This program prints `done`, but the JVM never exits. Why, and how do you fix it?

```java
public static void main(String[] args) {
    ExecutorService pool = Executors.newFixedThreadPool(4);
    pool.submit(() -> System.out.println("done"));
}
```$hs$, $hs$The pool's worker threads are non-daemon threads that stay alive, waiting for more tasks, and the JVM only exits once every non-daemon thread has ended. Shut the pool down:

```java
try (ExecutorService pool = Executors.newFixedThreadPool(4)) {   // close(): shutdown, then wait for the tasks
    pool.submit(() -> System.out.println("done"));
}
```

`ExecutorService` is `AutoCloseable` since Java 19; before that, call `pool.shutdown()` when no more tasks will come.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ExecutorService (close) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: an exception lost through submit()$hs$, $hs$`settle` throws an `IllegalStateException`, but nothing is logged anywhere. Where did the exception go?

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
pool.submit(() -> settle(payment));
```$hs$, $hs$It was captured in the `Future` that `submit` returned. `submit` wraps the task in a `FutureTask`, which catches any exception the task throws and stores it as the task's outcome. The exception is rethrown only by `future.get()`, wrapped in an `ExecutionException`. Here nobody keeps or checks the future, so the exception is silently lost.

```java
Future<?> settled = pool.submit(() -> settle(payment));
settled.get();   // throws ExecutionException, with the IllegalStateException as its cause
```

With `execute` instead of `submit`, the exception propagates out of the task and the worker thread's uncaught-exception handler reports it. Alternatively, catch and log the exception inside the task.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ThreadPoolExecutor (afterExecute) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: the four rejection policies$hs$, $hs$A `ThreadPoolExecutor` runs its maximum number of threads and its bounded queue is full. What happens to the next submitted task under each of the four built-in rejection policies?$hs$, $hs$- `AbortPolicy`, the default: throws `RejectedExecutionException` to the caller.
- `CallerRunsPolicy`: runs the task on the submitting thread, which slows the producer down: simple backpressure.
- `DiscardPolicy`: drops the task silently.
- `DiscardOldestPolicy`: drops the oldest queued task and tries to submit this one again.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ThreadPoolExecutor (Rejected tasks) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: two limits on calls to a bank API$hs$, $hs$Each payment is sent on its own virtual thread. The bank allows at most 20 concurrent calls and at most 50 calls per second. Which mechanism enforces each limit, and why can't one of them enforce both?

```java
void send(Payment payment) {
    bank.send(payment);   // a blocking HTTP call
}
```$hs$, $hs$A `Semaphore(20)` bounds how many calls are in flight; a rate limiter (a token bucket, e.g. Resilience4j's `RateLimiter`) bounds how many start per second.

```java
private final Semaphore inFlight = new Semaphore(20);

void send(Payment payment) throws InterruptedException {
    inFlight.acquire();
    try {
        rateLimiter.waitForPermit();   // at most 50 starts per second
        bank.send(payment);
    } finally {
        inFlight.release();
    }
}
```

- A semaphore says nothing about time: with fast calls, 20 permits can serve far more than 50 calls per second.
- A rate limiter says nothing about duration: with slow calls, 50 starts per second pile up into hundreds of calls in flight.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.Semaphore https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Executors: a pool that deadlocks on its own subtasks$hs$, $hs$Each report submits two subtasks to the same pool and waits for them. With one report at a time it works; when two reports are requested at the same moment, both hang forever. Why?

```java
ExecutorService pool = Executors.newFixedThreadPool(2);

Future<Report> report(long athleteId) {
    return pool.submit(() -> {
        Future<Workouts> workouts = pool.submit(() -> loadWorkouts(athleteId));
        Future<Records> records = pool.submit(() -> loadRecords(athleteId));
        return new Report(workouts.get(), records.get());
    });
}
```$hs$, $hs$A thread-starvation deadlock. The two reports take both of the pool's threads, and each blocks in `get()`, waiting for subtasks that sit in the queue behind them. No thread is free to run a subtask, so nothing ever finishes.

- Don't block a pool's thread on work submitted to the same pool: run the subtasks on a separate executor.
- Or don't block at all: compose with `CompletableFuture` (`supplyAsync` for both, then `thenCombine`).
- With virtual threads (`newVirtualThreadPerTaskExecutor`) there's no fixed number of threads to run out of.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ExecutorService https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: Future vs CompletableFuture$hs$, $hs$What can a `CompletableFuture` do that a plain `Future` can't?$hs$, $hs$A plain `Future` can only be checked (`isDone`) or waited on (`get`). A `CompletableFuture` can also:
- be composed: run further steps when it completes (`thenApply`, `thenCompose`, `thenCombine`) without blocking
- handle errors inside the chain (`exceptionally`, `handle`)
- be completed manually (`complete`, `completeExceptionally`) and given a timeout (`orTimeout`).$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: thenApply vs thenCompose vs thenCombine$hs$, $hs$`findAccount` and `loadRates` return `CompletableFuture`s. Which method fits each line?

```java
CompletableFuture<Account> account = findAccount(id);
account.???(acc -> acc.balance());                                   // 1: get the balance
account.???(acc -> loadStatement(acc));                              // 2: loadStatement returns a CompletableFuture
account.???(loadRates(), (acc, rates) -> convert(acc.balance(), rates));  // 3: combine two independent futures
```$hs$, $hs$```java
account.thenApply(acc -> acc.balance());                       // 1: transforms the result (like map)
account.thenCompose(acc -> loadStatement(acc));                // 2: chains another async step, flattened (like flatMap)
account.thenCombine(loadRates(), (acc, rates) -> convert(acc.balance(), rates));  // 3: waits for both
```

With `thenApply` in line 2, the result would be a nested `CompletableFuture<CompletableFuture<Statement>>`.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: supplyAsync without an executor$hs$, $hs$On which threads does `CompletableFuture.supplyAsync(() -> callBankApi())` run, and why is that a problem in a server?$hs$, $hs$On the shared `ForkJoinPool.commonPool()`, which has about as many threads as CPU cores, and is also used by parallel streams and every other library calling `supplyAsync` without an executor.

A blocking HTTP call occupies one of its few threads for the whole call, so a few slow calls starve everything else using the pool. Pass an executor suited to blocking I/O:

```java
CompletableFuture.supplyAsync(() -> callBankApi(), ioExecutor);
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture (supplyAsync) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: three calls in parallel with a timeout$hs$, $hs$An account overview needs three blocking calls: the account, its cards and its last 20 transactions. Write `overview` with `CompletableFuture`: run the three calls in parallel on `executor` and combine the results with `new Overview(account, cards, transactions)`. If the whole overview takes longer than 2 seconds, `overview` must throw an exception caused by a `TimeoutException`.

```java
private final AccountService accountService;   // Account find(long accountId)
private final CardService cardService;         // List<Card> cards(long accountId)
private final Ledger ledger;                   // List<Transaction> last(long accountId, int count)
private final Executor executor;               // runs the blocking calls

Overview overview(long accountId) {
    // ...
}
```$hs$, $hs$```java
Overview overview(long accountId) {
    CompletableFuture<Account> account =
            CompletableFuture.supplyAsync(() -> accountService.find(accountId), executor);
    CompletableFuture<List<Card>> cards =
            CompletableFuture.supplyAsync(() -> cardService.cards(accountId), executor);
    CompletableFuture<List<Transaction>> transactions =
            CompletableFuture.supplyAsync(() -> ledger.last(accountId, 20), executor);

    return CompletableFuture.allOf(account, cards, transactions)
            .thenApply(done -> new Overview(account.join(), cards.join(), transactions.join()))
            .orTimeout(2, TimeUnit.SECONDS)
            .join();
}
```

- `supplyAsync(..., executor)` starts each call on the executor straight away, so the three run in parallel.
- `allOf` completes once all three have completed. Inside `thenApply`, every future is already done, so `join()` returns immediately instead of blocking.
- `orTimeout` completes the combined future exceptionally with a `TimeoutException` after 2 seconds. The final `join()` then throws a `CompletionException` caused by it. If one of the calls fails first, it throws a `CompletionException` caused by that failure instead.
- The timeout doesn't stop the calls: they keep running and keep their executor threads busy, even after `overview` has thrown. Cancelling a `CompletableFuture` doesn't interrupt its task either. To really bound the work, give each call its own timeout as well, such as a query timeout or an HTTP client timeout.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture (allOf, orTimeout) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: how exceptions flow through a chain$hs$, $hs$`findAccount` throws an exception. What happens to the following stages, and what does `join()` throw?

```java
Money balance = supplyAsync(() -> findAccount(id))
        .thenApply(Account::balance)
        .thenApply(this::toEuros)
        .exceptionally(e -> Money.ZERO)
        .join();
```$hs$, $hs$```java
Money balance = supplyAsync(() -> findAccount(id))   // completes exceptionally
        .thenApply(Account::balance)                // skipped
        .thenApply(this::toEuros)                   // skipped
        .exceptionally(e -> Money.ZERO)             // runs: recovers with ZERO (e is a CompletionException)
        .join();                                    // returns ZERO; nothing is thrown
```

Without the `exceptionally`, `join()` would throw a `CompletionException` wrapping the original exception (`get()` an `ExecutionException`).$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: a chain that silently never finishes$hs$, $hs$A `CompletableFuture` chain that sends payment confirmations sometimes sends nothing, and nothing is logged. Nobody calls `join()` or `get()` on it. Why is the failure invisible, and how do you make it visible?$hs$, $hs$A stage threw an exception, so the future completed exceptionally and every following stage was skipped. `CompletableFuture` doesn't log failures: the exception stays inside the future until someone asks for the result or handles it, and nobody does.

- Handle errors at the end of every chain: `whenComplete((result, error) -> { if (error != null) log.error("...", error); })`, or `exceptionally`.
- Add a timeout (`orTimeout`), so a call that hangs fails instead of waiting forever.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Futures: a timeout that doesn't stop the work$hs$, $hs$When the bank API slows down to 30 seconds per call, this method fails after 2 seconds, as intended. But the pool's 20 threads stay busy and new requests pile up behind them. Why doesn't the timeout free them, and how do you fix it?

```java
CompletableFuture<Rate> eur = CompletableFuture.supplyAsync(() -> bankApi.rate("EUR"), pool);
CompletableFuture<Rate> usd = CompletableFuture.supplyAsync(() -> bankApi.rate("USD"), pool);

CompletableFuture.allOf(eur, usd)
        .orTimeout(2, TimeUnit.SECONDS)
        .join();
```$hs$, $hs$`orTimeout` only completes the future it's called on (the `allOf` one) with a `TimeoutException`. It doesn't touch `eur` and `usd`, and nothing reaches the threads running `bankApi.rate`: a `CompletableFuture` doesn't know which thread computes it. Even `eur.cancel(true)` wouldn't help; its Javadoc says `mayInterruptIfRunning` "has no effect in this implementation because interrupts are not used to control processing". So each call runs on for its full 30 seconds, holding a pool thread and an HTTP connection.

Fix it where the waiting happens: give the HTTP client a timeout (connect and read, e.g. 2 seconds), so the call itself fails and frees its thread. Alternatively, submit the calls to an `ExecutorService` and cancel the returned `Future`s with `cancel(true)`, which interrupts the threads; that helps only if the blocking call reacts to interrupts.$hs$, 7, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.CompletableFuture (cancel, orTimeout) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: what they are$hs$, $hs$What are virtual threads in Java 21, and which kind of work do they suit?$hs$, $hs$Virtual threads are lightweight threads managed by the JVM rather than the operating system. They run on a small pool of platform (carrier) threads; when a virtual thread blocks on I/O, it's unmounted and its carrier runs another one.

They suit many concurrent tasks that spend most of their time waiting, such as one thread per request in a server. They don't make CPU-bound work faster.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> fetchStatement(accountId));
}
```$hs$, 2, 'SUBMITTED', $hs$JEP 444: Virtual Threads https://openjdk.org/jeps/444$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: when they don't help$hs$, $hs$A service switches to virtual threads, but its throughput doesn't improve. Name two kinds of bottleneck virtual threads can't remove.$hs$, $hs$- CPU-bound work: virtual threads don't add CPU; they only stop threads from being wasted while waiting.
- A limited downstream resource: if every request needs one of 20 database connections, the 21st waits for a connection, however many threads exist.$hs$, 3, 'SUBMITTED', $hs$JEP 444: Virtual Threads https://openjdk.org/jeps/444$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: don't pool them$hs$, $hs$Should you put virtual threads in a pool of 50 to limit how many calls hit the bank's API at once? If not, how do you limit it?$hs$, $hs$No. Virtual threads are cheap to create and are meant to be one per task (`Executors.newVirtualThreadPerTaskExecutor()`); pooling them brings back the costs pools were invented to avoid, for no benefit.

Limit the resource, not the threads: a `Semaphore(50)` around the API call.$hs$, 4, 'SUBMITTED', $hs$JEP 444: Virtual Threads https://openjdk.org/jeps/444$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: pinning$hs$, $hs$What is pinning of a virtual thread, and what causes it?$hs$, $hs$A pinned virtual thread can't unmount from its carrier thread while it blocks, so the carrier is blocked too. With many pinned threads, all carriers are blocked and nothing else runs.

It happens during native calls (and foreign functions), and, up to Java 23, while blocking inside a `synchronized` block or method. Java 24 removed the `synchronized` case (JEP 491); on Java 21, prefer `ReentrantLock` around blocking calls.$hs$, 6, 'SUBMITTED', $hs$JEP 491: Synchronize Virtual Threads without Pinning https://openjdk.org/jeps/491$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread-local state: what ThreadLocal is for$hs$, $hs$What is a `ThreadLocal` used for? Give two examples from a Spring application.$hs$, $hs$It holds a separate value for each thread, without passing it through every method call.

Examples: Spring Security's `SecurityContextHolder` (the current user), Spring's transaction synchronisation (the current transaction and connection), and the logging MDC (a request's trace id).$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.lang.ThreadLocal https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread-local state: ThreadLocal in a thread pool$hs$, $hs$This servlet filter stores the signed-in customer in a `ThreadLocal`, which the audit log reads. Now and then, the audit log attributes an anonymous request (a public exchange-rate lookup) to a real customer. Why, and how do you fix the filter?

```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    Customer customer = signedInCustomer(request);   // null for anonymous requests
    if (customer != null) {
        CURRENT_CUSTOMER.set(customer);
    }
    chain.doFilter(request, response);
}
```$hs$, $hs$Tomcat runs requests on a pool of reused threads, and nothing ever removes the value. When an anonymous request runs on a thread that earlier served a signed-in customer, the filter doesn't set anything, so the audit log reads that earlier customer.

```java
try {
    if (customer != null) {
        CURRENT_CUSTOMER.set(customer);
    }
    chain.doFilter(request, response);
} finally {
    CURRENT_CUSTOMER.remove();   // the next request on this thread starts empty
}
```

Every `set` on a pooled thread needs a `remove` in a `finally`; leftovers also keep their objects from being garbage-collected.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.lang.ThreadLocal https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: blocking inside synchronized on Java 21$hs$, $hs$On Java 21, a service handles every request on a virtual thread. Under load, throughput collapses to a few requests at a time, although the bank API answers quickly. Why, and how do you fix this method?

```java
synchronized Rate rate(String currency) {
    Rate rate = cache.get(currency);
    if (rate == null) {
        rate = bankApi.fetch(currency);   // a blocking HTTP call
        cache.put(currency, rate);
    }
    return rate;
}
```$hs$, $hs$Pinning: on Java 21 to 23, a virtual thread that blocks inside `synchronized` can't unmount from its carrier thread. The carriers (about one per core) end up blocked in `bankApi.fetch` or waiting for the monitor, so few requests make progress, whatever the number of virtual threads.

- Use a `ReentrantLock` instead of `synchronized`: virtual threads blocked on it unmount.
- Better, don't hold a lock during I/O at all: a `ConcurrentHashMap` with a per-key load, so other currencies don't wait.
- From Java 24 (JEP 491), `synchronized` no longer pins, though holding a lock during a slow call still serialises it.$hs$, 7, 'SUBMITTED', $hs$JEP 491: Synchronize Virtual Threads without Pinning https://openjdk.org/jeps/491$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: connection timeouts after the switch$hs$, $hs$After enabling virtual threads in a Spring MVC service, throughput rises at first, then requests start failing with "Connection is not available, request timed out after 30000ms". Why, and how do you fix it?$hs$, $hs$The bottleneck moved to the database. With Tomcat's 200 platform threads, at most 200 requests ran at once; with virtual threads, thousands do, and they all compete for the HikariCP pool (10 connections by default). Requests wait longer than `connectionTimeout` (30 seconds by default) and fail.

Limit concurrency where the scarce resource is, rather than raising the pool to match: a semaphore or bulkhead around the database work, sized to what the database can handle, and short transactions so connections are returned quickly.$hs$, 7, 'SUBMITTED', $hs$HikariCP, configuration (maximumPoolSize, connectionTimeout) https://github.com/brettwooldridge/HikariCP$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Thread-local state: ThreadLocal vs scoped values$hs$, $hs$Java 25 finalised scoped values (`ScopedValue`). Name the two properties of a scoped value binding that a `ThreadLocal` doesn't have.$hs$, $hs$- **A bounded lifetime:** the value is bound only for the scope of one call (`ScopedValue.where(KEY, value).run(...)`) and is gone when it returns; a `ThreadLocal` lives until someone removes it.
- **Immutability:** code in the scope can read the value but not change it; a `ThreadLocal` can be set again, from anywhere.

That makes them cheap and leak-free with millions of short-lived virtual threads.$hs$, 6, 'SUBMITTED', $hs$JEP 506: Scoped Values https://openjdk.org/jeps/506$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Virtual threads: the problem structured concurrency addresses$hs$, $hs$A method starts two subtasks with an `ExecutorService` and waits for both. One fails right away. What happens to the other, and what does structured concurrency (`StructuredTaskScope`, a preview API) change about it?$hs$, $hs$With a plain executor, nothing relates the subtasks to each other or to the method that started them. The other subtask keeps running to completion, although its result is no longer needed, and it can even outlive the method.

Structured concurrency treats a group of subtasks as a single unit of work within a scope, whose lifetime is bounded by a code block:
- by default, the failure of one subtask cancels the others;
- the scope cannot be closed before all its subtasks have finished, so none outlives the method;
- thread dumps show the subtasks as children of the task that started them.$hs$, 5, 'SUBMITTED', $hs$JEP 505: Structured Concurrency (Fifth Preview) https://openjdk.org/jeps/505$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring: concurrent requests and a singleton controller$hs$, $hs$100 requests for `GET /accounts/{id}` arrive at the same time. How many `AccountController` instances handle them, and on how many threads?$hs$, $hs$One `AccountController` instance handles all of them: controllers are singleton beans by default, so the application context holds exactly one instance, shared by every request.

The requests run concurrently, each on its own thread from the embedded web server's request-processing pool (Tomcat's default maximum is 200 threads), or each on its own virtual thread when `spring.threads.virtual.enabled=true`.

Because one instance serves many threads at once, controllers and services must be stateless: they must not keep request-specific data in fields.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Task Execution and Scheduling https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-core', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring: request state in a singleton field$hs$, $hs$Customers sometimes see another customer's statement. Why?

```java
@Service
class StatementService {
    private Customer currentCustomer;

    Statement statementFor(Customer customer) {
        this.currentCustomer = customer;
        return buildStatement();   // reads currentCustomer
    }
}
```$hs$, $hs$`StatementService` is a singleton shared by all request threads. When two requests overlap, the second overwrites `currentCustomer` before the first finishes `buildStatement()`, which then builds the second customer's statement.

```java
@Service
class StatementService {
    Statement statementFor(Customer customer) {
        return buildStatement(customer);   // pass request data as parameters; no shared field
    }
}
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Bean Scopes https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring: how @Async and @Scheduled run by default$hs$, $hs$In a Spring Boot application with default settings, which threads run `@Async` methods and `@Scheduled` methods?$hs$, $hs$- `@Async`: Boot's auto-configured `ThreadPoolTaskExecutor`, with 8 core threads and an unbounded queue (or virtual threads when `spring.threads.virtual.enabled=true`). `@EnableAsync` is still required.
- `@Scheduled`: a scheduler with a single thread, so a slow job delays every other scheduled job. Raise `spring.task.scheduling.pool.size` if jobs must run in parallel.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Task Execution and Scheduling https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'spring-core', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory model: lazy singleton initialisation without synchronized$hs$, $hs$Create the expensive `RateTable` lazily, on first use, thread-safely and without `synchronized` or `volatile`.$hs$, $hs$The holder-class idiom: the nested class is initialised only when `get()` first touches it, and the JVM guarantees that class initialisation happens once and is visible to all threads.

```java
final class RateTables {
    private static final class Holder {
        static final RateTable INSTANCE = loadRateTable();
    }

    static RateTable get() {
        return Holder.INSTANCE;
    }
}
```$hs$, 5, 'SUBMITTED', $hs$Java Language Specification, §12.4.2 Detailed Initialization Procedure https://docs.oracle.com/javase/specs/jls/se21/html/jls-12.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring: the lost trace id with @Async$hs$, $hs$Log lines written inside an `@Async` method are missing the request's trace id, which every other log line has. Why, and how do you fix it?$hs$, $hs$The trace id lives in the logging MDC, which is thread-local. `@Async` runs the method on another thread from the task executor, where the MDC is empty.

Copy the context to the task's thread: register a `TaskDecorator` on the executor that captures the MDC (`MDC.getCopyOfContextMap()`) when the task is submitted and sets it around the task's run. With Micrometer Tracing, its context-propagation support does this for you.$hs$, 6, 'SUBMITTED', $hs$Spring Boot docs, Task Execution and Scheduling https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring: a scheduled job that runs on every instance$hs$, $hs$A `@Scheduled` job posts the daily interest. After scaling the service to three instances, every customer gets interest three times. Why, and how does a lock such as ShedLock make the job run once per schedule?$hs$, $hs$`@Scheduled` runs in every instance's own scheduler, so the job runs once per instance.

ShedLock keeps a lock row per job in a shared store, usually a database table. When the schedule fires, each instance tries to take the lock with an atomic update; only the one that succeeds runs the job, the others skip it. The lock also has a `lockAtMostFor` time, so a crashed instance can't hold it forever.

Make the job idempotent anyway (record that the day's interest was paid), so a retry can't pay twice.$hs$, 6, 'SUBMITTED', $hs$ShedLock https://github.com/lukas-krecan/ShedLock$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Parallel streams: collecting into an ArrayList$hs$, $hs$Sometimes weights are missing from `heavy`, and sometimes the stream throws an `ArrayIndexOutOfBoundsException`. Why, and how do you fix it?

```java
List<Integer> heavy = new ArrayList<>();
workoutSets.parallelStream()
        .filter(set -> set.weight() > 100)
        .forEach(set -> heavy.add(set.weight()));
```$hs$, $hs$A parallel `forEach` runs on several threads at once, and `ArrayList` isn't thread-safe: concurrent `add`s overwrite each other's slots, and one can write past the array while another is growing it.

Don't change shared state from a stream; let the stream collect the result:

```java
List<Integer> heavy = workoutSets.parallelStream()
        .filter(set -> set.weight() > 100)
        .map(WorkoutSet::weight)
        .toList();   // each thread collects its part, the parts are merged safely
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, package java.util.stream (Side-effects) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency', 'java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Parallel streams: blocking calls in the common pool$hs$, $hs$Sending 1,000 payments this way is slow, and while it runs, `CompletableFuture.supplyAsync` calls elsewhere in the service stall too. Why?

```java
List<Receipt> receipts = payments.parallelStream()
        .map(bankApi::send)   // a blocking HTTP call, about 200 ms
        .toList();
```$hs$, $hs$Parallel streams run on the JVM-wide common `ForkJoinPool`, which has about as many threads as the machine has cores. The blocking calls occupy all of them: only a handful of payments are in flight at a time, and everything else that uses the common pool, such as `supplyAsync` without an executor or other parallel streams, waits for a free thread.

Parallel streams are for CPU-bound work. For blocking calls, use your own executor, e.g. virtual threads:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<Receipt>> sent = payments.stream()
            .map(payment -> executor.submit(() -> bankApi.send(payment)))
            .toList();
    // collect each future's result
}
```$hs$, 6, 'SUBMITTED', $hs$Java SE 21 API, java.util.concurrent.ForkJoinPool (commonPool) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Object contract: == vs equals()$hs$, $hs$For two object references `a` and `b`, what does `a == b` compare, and what does `a.equals(b)` compare?$hs$, $hs$- `a == b` compares **identity**: it is `true` only when both references point to the same object.
- `a.equals(b)` compares **logical equality**, as defined by the class.

```java
String a = new String("squat");
String b = new String("squat");
System.out.println(a == b);        // false: two distinct objects
System.out.println(a.equals(b));   // true: the same characters
```

`Object.equals` itself falls back to identity; value classes such as `String`, `Integer` and records override it to compare their contents.$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Object (equals) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Object contract: equals and hashCode$hs$, $hs$If two objects are equal according to `equals`, what must be true of their `hashCode` values?$hs$, $hs$Their hash codes must be equal. The reverse isn't required: unequal objects may share a hash code.

That's why a class that overrides `equals` must also override `hashCode`; otherwise hash-based collections such as `HashMap` and `HashSet` can't find equal objects.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Object (hashCode) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Object contract: equals without hashCode in a HashSet$hs$, $hs$`Athlete` overrides `equals` to compare emails, but not `hashCode`. What does the last line print, and why?

```java
class Athlete {
    private final String email;
    Athlete(String email) { this.email = email; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Athlete a && a.email.equals(email);
    }
}

Set<Athlete> members = new HashSet<>();
members.add(new Athlete("ana@gym.example"));
System.out.println(members.contains(new Athlete("ana@gym.example")));
```$hs$, $hs$Almost always `false`. `HashSet` first picks a bucket by `hashCode`, and the inherited `Object.hashCode` differs for the two objects, so it looks in the wrong bucket and never calls `equals`.

```java
@Override
public int hashCode() {
    return email.hashCode();   // equal athletes now have equal hash codes
}
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Object (hashCode) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Object contract: changing a key inside a HashMap$hs$, $hs$An `Athlete` is used as a key in a `HashMap`. After it's been put in the map, the field its `hashCode` uses changes. What happens when you call `map.get(athlete)` with that same object?$hs$, $hs$It returns `null`, although the entry is still in the map. The entry was stored in the bucket for the old hash code; `get` now computes the new hash code and looks in a different bucket.

The entry is effectively lost: it can't be found or removed by key. Map keys must be immutable, at least in the fields `equals` and `hashCode` use.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.Map (mutable keys) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Object contract: equals and a subclass that adds a field$hs$, $hs$`Plate` compares its weight in `equals`. `ColouredPlate extends Plate` adds a colour, and overrides `equals` to compare the colour too. Which rule of the `equals` contract does this break, and what's the usual fix?$hs$, $hs$Symmetry: `plate.equals(colouredPlate)` is true (`Plate.equals` only compares weights), but `colouredPlate.equals(plate)` is false (a `Plate` has no colour). Trying to fix that by ignoring the colour against plain plates breaks transitivity instead.

You can't extend an instantiable class, add a value component, and keep `equals` correct. Use composition: `ColouredPlate` has a `Plate` field and a colour, and doesn't extend `Plate`.$hs$, 6, 'SUBMITTED', $hs$Effective Java, 3rd edition (Joshua Bloch), Item 10: Obey the general contract when overriding equals$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Immutability: the rules of an immutable class$hs$, $hs$Which rules must a Java class follow for its instances to be immutable?$hs$, $hs$1. Every field is `private` and `final`.
2. No method changes the object's state (no setters).
3. The class can't be subclassed: it's `final`, or has only private constructors.
4. Mutable parts are never shared: mutable arguments are copied in the constructor (e.g. `List.copyOf`), and internal mutable objects are never returned.$hs$, 2, 'SUBMITTED', $hs$JEP 395: Records https://openjdk.org/jeps/395$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Immutability: why prefer immutable objects$hs$, $hs$Give three advantages of immutable objects.$hs$, $hs$- Thread-safe without locks: they can be shared freely between threads.
- Safe as `HashMap` keys and `Set` elements: their hash code never changes.
- Simpler to reason about: no code can change them behind your back, so they can be shared and cached without defensive copies.$hs$, 2, 'SUBMITTED', $hs$JEP 395: Records https://openjdk.org/jeps/395$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Immutability: a class that leaks its list$hs$, $hs$Is `Team` immutable? If not, fix it.

```java
public final class Team {
    private final List<String> athletes;

    public Team(List<String> athletes) { this.athletes = athletes; }

    public List<String> athletes() { return athletes; }
}
```$hs$, $hs$No. The field is final, but the list itself can change: the caller who passed it in, and anyone calling `athletes()`, can add or remove athletes.

```java
public final class Team {
    private final List<String> athletes;

    public Team(List<String> athletes) { this.athletes = List.copyOf(athletes); }   // copy in, unmodifiable

    public List<String> athletes() { return athletes; }   // safe: List.copyOf is unmodifiable
}
```$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.List (unmodifiable lists) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Records: what the compiler generates$hs$, $hs$What does the compiler generate for this record?

```java
record PersonalBest(String exercise, int kilos) {}
```$hs$, $hs$```java
record PersonalBest(String exercise, int kilos) {}
// generates:
// - private final fields exercise and kilos
// - a canonical constructor PersonalBest(String exercise, int kilos)
// - accessors exercise() and kilos() (no "get" prefix)
// - equals and hashCode, comparing all components
// - toString: PersonalBest[exercise=squat, kilos=140]
```

The record class is implicitly `final` and extends `java.lang.Record`.$hs$, 2, 'SUBMITTED', $hs$JEP 395: Records https://openjdk.org/jeps/395$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Records: is a record immutable?$hs$, $hs$Can the contents of a `Workout` change after it's created?

```java
record Workout(String name, List<String> exercises) {}
```$hs$, $hs$Yes. A record is only shallowly immutable: its fields are final, but the `List` they point to can still be changed, by the caller who passed it in or through `exercises()`.

```java
record Workout(String name, List<String> exercises) {
    Workout {
        exercises = List.copyOf(exercises);   // compact constructor: store an unmodifiable copy
    }
}
```$hs$, 3, 'SUBMITTED', $hs$JEP 395: Records https://openjdk.org/jeps/395$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Records: validating components$hs$, $hs$Make sure a `RepRange` can never be created with `min` greater than `max`.

```java
record RepRange(int min, int max) {}
```$hs$, $hs$Validate in a compact canonical constructor; it runs before the fields are assigned.

```java
record RepRange(int min, int max) {
    RepRange {
        if (min > max) {
            throw new IllegalArgumentException("min %d > max %d".formatted(min, max));
        }
    }
}
```$hs$, 4, 'SUBMITTED', $hs$JEP 395: Records https://openjdk.org/jeps/395$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Language: final on a variable, a method and a class$hs$, $hs$What does `final` mean on a variable, on a method and on a class?$hs$, $hs$- Variable: it can be assigned only once. If it refers to an object, the object itself can still change.
- Method: it can't be overridden in a subclass.
- Class: it can't be extended.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 8: Classes (final classes, methods and fields) https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: int vs Integer$hs$, $hs$What is the difference between `int` and `Integer`?$hs$, $hs$`int` is a primitive value; `Integer` is an object that wraps an `int`.

- An `Integer` can be `null`; an `int` can't.
- Generics and collections need objects: `List<Integer>`, never `List<int>`.
- An `Integer` costs more memory, and converting between the two (boxing and unboxing) costs time.$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Integer https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Integer.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: comparing Integer objects with ==$hs$, $hs$What does this code print with default JVM settings?

```java
Integer reps = 127, target = 127;
Integer heavyReps = 128, heavyTarget = 128;
System.out.println(reps == target);
System.out.println(heavyReps == heavyTarget);
System.out.println(heavyReps.equals(heavyTarget));
```$hs$, $hs$```
true
false
true
```

- Boxing must return the same object for `int` values from -128 to 127, so `reps` and `target` are one cached object.
- 128 is outside that range: two objects, and `==` compares references.
- `equals` compares the values.$hs$, 4, 'SUBMITTED', $hs$Java Language Specification, §5.1.7 Boxing Conversion https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: unboxing a missing map value$hs$, $hs$Why does the second line throw a `NullPointerException`?

```java
Map<String, Integer> repsPerExercise = new HashMap<>();
int squats = repsPerExercise.get("squat");
```$hs$, $hs$`get` returns `null` for a missing key, and assigning it to an `int` unboxes it: unboxing `null` throws `NullPointerException`.

```java
int squats = repsPerExercise.getOrDefault("squat", 0);   // 0 when missing
```$hs$, 4, 'SUBMITTED', $hs$Java Language Specification, §5.1.8 Unboxing Conversion https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: 0.1 + 0.2 == 0.3$hs$, $hs$What does `System.out.println(0.1 + 0.2 == 0.3);` print, and how should a membership fee be stored instead of a `double`?$hs$, $hs$`false`. A `double` is a binary floating-point number, which can't represent 0.1, 0.2 or 0.3 exactly; the sum is 0.30000000000000004.

Store money as a `BigDecimal` (created from a string or with `BigDecimal.valueOf`), or as a `long` number of cents.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.math.BigDecimal https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: BigDecimal equals vs compareTo$hs$, $hs$What do these two lines print?

```java
System.out.println(new BigDecimal("1.0").equals(new BigDecimal("1.00")));
System.out.println(new BigDecimal("1.0").compareTo(new BigDecimal("1.00")));
```$hs$, $hs$```
false
0
```

`equals` compares the value and the scale (1 vs 2 decimal places), so 1.0 and 1.00 aren't equal. `compareTo` compares only the numeric value. Use `compareTo(...) == 0` for numeric equality.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.math.BigDecimal (equals, compareTo) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: Integer.MAX_VALUE + 1$hs$, $hs$What does `System.out.println(Integer.MAX_VALUE + 1);` print, and how do you make such an overflow fail instead?$hs$, $hs$`-2147483648`: `int` arithmetic overflows silently and wraps around to `Integer.MIN_VALUE`.

`Math.addExact(Integer.MAX_VALUE, 1)` throws an `ArithmeticException` on overflow instead (likewise `multiplyExact`, `subtractExact`).$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Math (addExact) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: new BigDecimal(0.1)$hs$, $hs$What do these two lines print, and which way of creating a `BigDecimal` should a membership fee use?

```java
System.out.println(new BigDecimal(0.1));
System.out.println(BigDecimal.valueOf(0.1));
```$hs$, $hs$```
0.1000000000000000055511151231257827021181583404541015625
0.1
```

`0.1` can't be stored exactly as a `double`; `new BigDecimal(double)` converts the exact binary value the double holds. `BigDecimal.valueOf(double)` goes through the double's shortest string form, `"0.1"`.

For money, never start from a `double`: use `new BigDecimal("0.10")`, or `BigDecimal.valueOf(10, 2)` for a value in cents.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.math.BigDecimal (BigDecimal(double), valueOf(double)) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Numbers: comparing Long ids with ==$hs$, $hs$The tests use athlete ids 1, 2 and 3, and pass. In production, athletes with ids in the thousands sometimes can't edit their own workouts. Why?

```java
public class Workout {
    private Long athleteId;   // from a BIGINT NOT NULL column

    public boolean isOwnedBy(Long currentAthleteId) {
        return athleteId == currentAthleteId;
    }
}
```$hs$, $hs$`==` on two `Long` objects compares **references**, not values. `Long.valueOf`, which autoboxing uses, returns cached objects only for values from -128 to 127, so two small ids that are equal are also the same object, and the tests pass by luck. Larger ids are usually distinct objects with the same value, so `==` returns `false`.

Compare values instead:

```java
return athleteId.equals(currentAthleteId);   // athleteId is never null (NOT NULL column)
```

`Objects.equals(a, b)` is the null-safe alternative. Test ids of 128 or more (e.g. 1001) would have exposed the bug.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.lang.Long (valueOf caches -128 to 127) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Long.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strings: why String is immutable$hs$, $hs$Why are `String` objects immutable in Java?$hs$, $hs$- Sharing: string literals with the same content share one object from the string pool, which is only safe if no one can change it.
- Hashing: the hash code can be computed once and cached, which makes strings fast map keys.
- Thread safety: strings can be shared between threads without synchronisation.
- Security: a validated value, such as a file path or URL, can't be changed afterwards.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, §3.10.5 String Literals https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strings: the string pool$hs$, $hs$What does this print, and why?

```java
String a = "deadlift";
String b = "deadlift";
String c = new String("deadlift");
System.out.println(a == b);
System.out.println(a == c);
```$hs$, $hs$```
true
false
```

- String literals are interned: every literal with the same content refers to the same pooled `String` object, so `a` and `b` are one object.
- `new String(...)` always creates a new object, so `c` is a different object.$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, §3.10.5 String Literals https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strings: String vs StringBuilder vs StringBuffer$hs$, $hs$What is the difference between `String`, `StringBuilder` and `StringBuffer`?$hs$, $hs$- `String`: immutable; every change creates a new string.
- `StringBuilder`: a mutable sequence of characters, for building a string in steps.
- `StringBuffer`: like `StringBuilder`, but its methods are synchronized. It's rarely needed; use `StringBuilder`.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.lang.StringBuilder https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringBuilder.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strings: concatenation in a loop$hs$, $hs$This method is very slow for 100,000 exercises. Why, and how do you fix it?

```java
String summary(List<String> exercises) {
    String result = "";
    for (String exercise : exercises) {
        result += exercise + ", ";
    }
    return result;
}
```$hs$, $hs$`String` is immutable, so each `+=` creates a new string and copies all the characters built so far into it. The total amount of copying therefore grows quadratically with the number of exercises: O(n²).

A `StringBuilder` appends into one growing buffer, which makes the whole loop linear; `String.join` does the same internally:

```java
String summary(List<String> exercises) {
    return String.join(", ", exercises);   // or append to one StringBuilder in the loop
}
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.lang.StringBuilder https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringBuilder.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Generics: what problem they solve$hs$, $hs$What problem do generics solve?$hs$, $hs$They make types and methods type-safe for the types they work with. A `List<Workout>` only accepts workouts, so a mistake is a compile error instead of a `ClassCastException` at runtime, and no casts are needed when reading.$hs$, 1, 'SUBMITTED', $hs$dev.java, Generics https://dev.java/learn/generics/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Generics: type erasure$hs$, $hs$What is type erasure in Java generics?$hs$, $hs$The compiler uses generic type information to check types, then erases it: type parameters are replaced by their bound (`Object` when unbounded), and casts are inserted where values come out. At runtime, `List<Workout>` and `List<Exercise>` are the same class, `List`.

Java chose erasure so generic code stays compatible with code written before generics existed (Java 5).$hs$, 2, 'SUBMITTED', $hs$dev.java, Generics https://dev.java/learn/generics/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Generics: invariance vs array covariance$hs$, $hs$Which of these lines compile, and what happens when the third one runs?

```java
List<Object> objects = new ArrayList<String>();
Object[] array = new String[1];
array[0] = 42;
```$hs$, $hs$```java
List<Object> objects = new ArrayList<String>();   // compile error: generics are invariant
Object[] array = new String[1];                   // compiles: arrays are covariant
array[0] = 42;                                    // compiles, but throws ArrayStoreException at runtime
```

A `List<String>` is not a `List<Object>`, because otherwise anything could be added to it. Arrays allow it, and pay with a runtime check on every store.$hs$, 4, 'SUBMITTED', $hs$Java Language Specification, chapter 4: Types (subtyping) https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Generics: PECS$hs$, $hs$You can read `Number`s from a `List<? extends Number>`, but can't add to it; you can add `Integer`s to a `List<? super Integer>`, but only read `Object`s from it. Why, and what does PECS mean?$hs$, $hs$- `List<? extends Number>` could be a `List<Double>`: adding an `Integer` would be unsafe, but everything in it is a `Number`.
- `List<? super Integer>` could be a `List<Object>`: adding an `Integer` is always safe, but what you read could be any `Object`.

PECS: Producer `extends`, Consumer `super`. A parameter you read from uses `extends`; one you write to uses `super`:

```java
static <T> void copy(List<? super T> target, List<? extends T> source) { ... }
```$hs$, 6, 'SUBMITTED', $hs$dev.java, Generics (wildcards) https://dev.java/learn/generics/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: ArrayList vs LinkedList$hs$, $hs$What is the difference between `ArrayList` and `LinkedList`, and which do you use by default?$hs$, $hs$- `ArrayList` keeps elements in an array: access by index is O(1), and its compact memory is cache-friendly. Inserting in the middle shifts elements.
- `LinkedList` links nodes: inserting at a known node is O(1), but access by index is O(n), and each element costs much more memory.

Use `ArrayList` by default (and `ArrayDeque` for queues); it's faster for almost all workloads.$hs$, 2, 'SUBMITTED', $hs$dev.java, The Collections Framework https://dev.java/learn/api/collections-and-streams/collections-framework/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: how a HashMap stores and finds entries$hs$, $hs$How does `HashMap.get(key)` find an entry?$hs$, $hs$1. It computes the key's `hashCode` and uses it to pick a bucket in its internal array.
2. Within that bucket, it compares the key with each entry's key using `equals`.

A bucket holds a short linked list of entries (turned into a balanced tree when it grows long). When the map is fuller than its load factor (0.75 by default), the array doubles and the entries are redistributed.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, java.util.HashMap https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: HashMap vs LinkedHashMap vs TreeMap$hs$, $hs$In which order does each of `HashMap`, `LinkedHashMap` and `TreeMap` return its keys, and what does `get` cost in each?$hs$, $hs$- `HashMap`: no guaranteed order; `get` is O(1).
- `LinkedHashMap`: insertion order (or access order, if constructed so); `get` is O(1).
- `TreeMap`: sorted by key (natural order or a `Comparator`); `get` is O(log n).$hs$, 3, 'SUBMITTED', $hs$dev.java, The Collections Framework https://dev.java/learn/api/collections-and-streams/collections-framework/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: removing inside a for-each loop$hs$, $hs$What happens when this runs, and how do you remove the exercise correctly?

```java
List<String> exercises = new ArrayList<>(List.of("squat", "bench", "deadlift"));
for (String exercise : exercises) {
    if (exercise.equals("squat")) exercises.remove(exercise);
}
```$hs$, $hs$It throws `ConcurrentModificationException`. The for-each loop uses the list's iterator, which is fail-fast: it detects that the list was changed outside the iterator and throws on the next step.

```java
exercises.removeIf(exercise -> exercise.equals("squat"));   // or Iterator.remove() in an explicit loop
```$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.ArrayList (fail-fast iterators) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: List.of vs Arrays.asList vs unmodifiableList$hs$, $hs$What can and can't you do with the lists returned by `List.of(...)`, `Arrays.asList(...)` and `Collections.unmodifiableList(...)`?$hs$, $hs$- `List.of(...)`: unmodifiable. Every change throws `UnsupportedOperationException`, and it rejects `null` elements.
- `Arrays.asList(...)`: a fixed-size view of the array. `set` works (and changes the array); `add` and `remove` throw.
- `Collections.unmodifiableList(list)`: a read-only view. You can't change it through the view, but changes to the original list show through.$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.List (unmodifiable lists) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: Comparable vs Comparator$hs$, $hs$What is the difference between `Comparable` and `Comparator`?$hs$, $hs$- `Comparable`: implemented by the class itself (`compareTo`), defining its one natural order, e.g. `String` alphabetically.
- `Comparator`: a separate object defining an order from outside, so a class can be sorted in many ways:

```java
athletes.sort(Comparator.comparing(Athlete::name).thenComparingInt(Athlete::personalBest));
```$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.Comparator https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: an LRU cache with LinkedHashMap$hs$, $hs$Implement a cache of the 100 most recently used workout plans, evicting the least recently used one, with only the JDK.$hs$, $hs$A `LinkedHashMap` in access order keeps entries from least to most recently used; overriding `removeEldestEntry` evicts the oldest when the cache grows too big.

```java
class PlanCache extends LinkedHashMap<Long, Plan> {
    PlanCache() {
        super(16, 0.75f, true);   // true = access order: get() moves an entry to the end
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Plan> eldest) {
        return size() > 100;      // evict the least recently used entry
    }
}
```

It isn't thread-safe: wrap it with `Collections.synchronizedMap`, or use a caching library such as Caffeine.$hs$, 5, 'SUBMITTED', $hs$Java SE 21 API, java.util.LinkedHashMap (removeEldestEntry) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedHashMap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Collections: why HashMap turns a bucket into a tree$hs$, $hs$Since Java 8, a `HashMap` bucket that holds many colliding entries is converted from a linked list into a balanced tree. When does that happen, and which problem does it solve?$hs$, $hs$When a single bucket reaches 8 entries and the table has at least 64 buckets (a smaller table is resized instead). It turns back into a list when it shrinks to 6.

It protects against many keys with the same hash, accidental or deliberate: in a list, a lookup in that bucket costs O(n); in a tree it costs O(log n). An attacker sending thousands of request parameters with colliding `String` hashes could otherwise make every lookup crawl (a denial of service).$hs$, 7, 'SUBMITTED', $hs$JEP 180: Handle Frequent HashMap Collisions with Balanced Trees https://openjdk.org/jeps/180$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Streams: the four core functional interfaces$hs$, $hs$What is a functional interface? Name the four core ones in `java.util.function` and their methods.$hs$, $hs$An interface with exactly one abstract method, so a lambda or method reference can implement it.

- `Function<T, R>`: `R apply(T t)`
- `Supplier<T>`: `T get()`
- `Consumer<T>`: `void accept(T t)`
- `Predicate<T>`: `boolean test(T t)`$hs$, 1, 'SUBMITTED', $hs$Java SE 21 API, package java.util.function https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Streams: lazy evaluation$hs$, $hs$What does it mean that Java streams are lazy?$hs$, $hs$Intermediate operations (`filter`, `map`) only describe the pipeline; nothing runs until a terminal operation (`collect`, `forEach`, `count`) is called. Then each element flows through all the stages in turn, and a short-circuiting operation such as `findFirst` can stop early without processing the rest.$hs$, 3, 'SUBMITTED', $hs$dev.java, Streams https://dev.java/learn/api/collections-and-streams/streams/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Streams: map vs flatMap$hs$, $hs$Each `Workout` has a `List<String> exercises()`. Which of `map` and `flatMap` gives you one `Stream<String>` of all the exercises, and what does the other one give?

```java
workouts.stream().map(Workout::exercises);
workouts.stream().flatMap(workout -> workout.exercises().stream());
```$hs$, $hs$`flatMap` gives a `Stream<String>` of all the exercises: it turns each workout into a stream and flattens those streams into one.

`map` turns each workout into exactly one element, its list, so it gives a `Stream<List<String>>`.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, package java.util.stream https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Streams: Collectors.toMap with duplicate keys$hs$, $hs$Two athletes are from the same city. What happens, and how do you fix it?

```java
Map<String, String> athleteByCity = athletes.stream()
        .collect(Collectors.toMap(Athlete::city, Athlete::name));
```$hs$, $hs$It throws `IllegalStateException` (duplicate key). Either say how to merge two values for one key, or group:

```java
Map<String, String> athleteByCity = athletes.stream()
        .collect(Collectors.toMap(Athlete::city, Athlete::name, (first, second) -> first));

Map<String, List<Athlete>> athletesByCity = athletes.stream()
        .collect(Collectors.groupingBy(Athlete::city));
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.stream.Collectors (toMap) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Optional: what it is meant for$hs$, $hs$According to its API documentation, what is `Optional` primarily intended for?$hs$, $hs$As a method return type, where there's a clear need to represent "no result" and returning `null` would likely cause errors:

```java
Optional<Athlete> findByName(String name);
```

It's not meant for fields, method parameters or collections. A variable of type `Optional` must never itself be `null`.$hs$, 2, 'SUBMITTED', $hs$Java SE 21 API, java.util.Optional (API note) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Optional: orElse vs orElseGet$hs$, $hs$`loadDefaultPlan()` is an expensive database call. When does each line call it?

```java
Plan a = findPlan(athleteId).orElse(loadDefaultPlan());
Plan b = findPlan(athleteId).orElseGet(() -> loadDefaultPlan());
```$hs$, $hs$```java
Plan a = findPlan(athleteId).orElse(loadDefaultPlan());           // always: the argument is evaluated first
Plan b = findPlan(athleteId).orElseGet(() -> loadDefaultPlan());  // only when no plan was found
```$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.util.Optional (orElse, orElseGet) https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Exceptions: checked vs unchecked$hs$, $hs$What distinguishes a checked exception from an unchecked exception in Java?$hs$, $hs$A checked exception must be caught or declared in the method's `throws` clause; the compiler enforces it. An unchecked exception doesn't have to be.

- Checked: subclasses of `Exception` other than `RuntimeException`, e.g. `IOException`.
- Unchecked: `RuntimeException` and its subclasses (e.g. `IllegalArgumentException`), and `Error`s.$hs$, 2, 'SUBMITTED', $hs$dev.java, Exceptions https://dev.java/learn/exceptions/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Exceptions: try-with-resources$hs$, $hs$What does try-with-resources guarantee here, and in which order are the resources closed?

```java
try (Connection connection = dataSource.getConnection();
     PreparedStatement statement = connection.prepareStatement(sql)) {
    ...
}
```$hs$, $hs$Every resource declared in the header is closed when the block ends, normally or with an exception, in reverse order of declaration: `statement` first, then `connection`.

If the block throws and `close()` throws too, the exception from `close()` is added to the first one as a suppressed exception (`getSuppressed()`), instead of replacing it.$hs$, 3, 'SUBMITTED', $hs$Java Language Specification, §14.20.3 try-with-resources https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Exceptions: return in finally$hs$, $hs$What does `sets()` return?

```java
int sets() {
    try {
        return 3;
    } finally {
        return 5;
    }
}
```$hs$, $hs$`5`.

A `finally` block always executes when control leaves the `try` block. If the `finally` block itself completes abruptly, here with `return 5`, that outcome replaces the outcome of the `try` block, so the pending `return 3` is discarded.

For the same reason, a `return` in `finally` would also discard an exception thrown in `try`. Never return from a `finally` block.$hs$, 5, 'SUBMITTED', $hs$Java Language Specification, §14.20.2 Execution of try-finally https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Exceptions: swallowing an exception$hs$, $hs$What's wrong with this error handling?

```java
try {
    importWorkouts(file);
} catch (Exception e) {
    log.error("import failed");
}
```$hs$, $hs$- It catches every exception, programming errors included, and the code continues as if the import worked.
- The log message drops the exception, so the stack trace and cause are lost.

```java
try {
    importWorkouts(file);
} catch (IOException e) {
    throw new WorkoutImportException("Can't import " + file, e);   // a specific exception, cause kept
}
```$hs$, 4, 'SUBMITTED', $hs$dev.java, Exceptions https://dev.java/learn/exceptions/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Language: Java passes by value$hs$, $hs$What does this print?

```java
static void rename(Athlete athlete) {
    athlete.setName("Ana");
    athlete = new Athlete("Ben");
}

Athlete athlete = new Athlete("Carla");
rename(athlete);
System.out.println(athlete.getName());
```$hs$, $hs$`Ana`. Java always passes arguments by value; for an object, the value passed is a copy of the reference.

```java
static void rename(Athlete athlete) {
    athlete.setName("Ana");          // changes the caller's object through the copied reference
    athlete = new Athlete("Ben");    // only reassigns the method's own copy; the caller's variable is unchanged
}
```$hs$, 3, 'SUBMITTED', $hs$Java Language Specification, §15.12.4 Run-Time Evaluation of Method Invocation https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Language: static nested vs inner class$hs$, $hs$What is the difference between a `static` nested class and an inner (non-static) class?$hs$, $hs$An inner class instance holds a hidden reference to an instance of its enclosing class: it can use that instance's fields, and can only be created through one (`gym.new Locker()`). A static nested class has no such reference and is created on its own (`new Gym.Locker()`).

Prefer `static` unless you need the enclosing instance: the hidden reference costs memory and can keep the outer object from being garbage collected.$hs$, 3, 'SUBMITTED', $hs$dev.java, Nested Classes https://dev.java/learn/classes-objects/nested-classes/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Modern Java: what sealed means$hs$, $hs$What does `sealed` mean on this interface?

```java
sealed interface Exercise permits Squat, Plank {}
```$hs$, $hs$Only the classes listed in `permits`, `Squat` and `Plank`, may implement `Exercise`. Each of them must itself be `final`, `sealed` or `non-sealed`.

The compiler therefore knows every possible subtype, which lets a `switch` over an `Exercise` be checked for completeness.$hs$, 2, 'SUBMITTED', $hs$JEP 409: Sealed Classes https://openjdk.org/jeps/409$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Modern Java: an exhaustive switch over a sealed type$hs$, $hs$Complete `calories` with a `switch` expression, without a `default` branch.

```java
sealed interface Exercise permits Squat, Plank {}
record Squat(int reps) implements Exercise {}
record Plank(int seconds) implements Exercise {}

int calories(Exercise exercise) {
    // ?
}
```$hs$, $hs$```java
int calories(Exercise exercise) {
    return switch (exercise) {
        case Squat squat -> squat.reps() / 2;
        case Plank plank -> plank.seconds() / 10;
    };   // exhaustive: Exercise is sealed, so no default is needed
}
```

Adding a new `Exercise` subtype makes this switch a compile error until it's handled.$hs$, 4, 'SUBMITTED', $hs$JEP 441: Pattern Matching for switch https://openjdk.org/jeps/441$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Modern Java: var$hs$, $hs$Where can you use `var` in Java, and what type does `workouts` get here?

```java
var workouts = new ArrayList<Workout>();
```$hs$, $hs$`workouts` gets the type `ArrayList<Workout>`, inferred from the initializer, and it's still statically typed.

`var` works only for local variables with an initializer (including `for` loop variables, try-with-resources and lambda parameters), never for fields, method parameters or return types.$hs$, 3, 'SUBMITTED', $hs$JEP 286: Local-Variable Type Inference https://openjdk.org/jeps/286$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Modern Java: the long-term support releases$hs$, $hs$Which Java releases since Java 11 are long-term support (LTS) releases, and how often does a new one come out now?$hs$, $hs$Java 17 (2021), Java 21 (2023) and Java 25 (2025).

A feature release ships every six months, and every fourth one, every two years, is an LTS release. Production systems usually stay on LTS versions.$hs$, 2, 'SUBMITTED', $hs$Oracle Java SE Support Roadmap https://www.oracle.com/java/technologies/java-se-support-roadmap.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Modern Java: two interfaces with the same default method$hs$, $hs$Does `Workout` compile? If not, make it use `Timed`'s label.

```java
interface Tracked { default String label() { return "tracked"; } }
interface Timed   { default String label() { return "timed"; } }

class Workout implements Tracked, Timed { }
```$hs$, $hs$No: `Workout` inherits two unrelated default methods with the same signature, and the compiler won't pick one. The class must override the method, and may delegate to one of them with `Interface.super`:

```java
class Workout implements Tracked, Timed {
    @Override
    public String label() {
        return Timed.super.label();   // "timed"
    }
}
```$hs$, 4, 'SUBMITTED', $hs$Java Language Specification, §8.4.8.4 Inheriting Methods with Override-Equivalent Signatures https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: static factory methods$hs$, $hs$What advantages does a static factory method such as `Workout.of(...)` have over a constructor?$hs$, $hs$- It has a name, which can say what it does (`Duration.ofMinutes`).
- It doesn't have to create a new object each time: it can return a cached instance (`Boolean.valueOf`).
- It can return a subtype, or a hidden implementation class (`List.of` returns an internal unmodifiable list).$hs$, 3, 'SUBMITTED', $hs$Effective Java, 3rd edition (Joshua Bloch), Item 1: Consider static factory methods instead of constructors$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: many optional parameters$hs$, $hs$A `TrainingPlan` has 2 required and 8 optional parameters. How should callers create one?$hs$, $hs$With a builder: required values in the builder's constructor, optional ones through named methods, and validation in `build()`.

```java
TrainingPlan plan = TrainingPlan.builder("Ana", Level.BEGINNER)
        .daysPerWeek(3)
        .includeCardio(true)
        .build();
```

It reads better than a constructor with 10 parameters (telescoping constructors), and unlike setters it can produce an immutable object.$hs$, 3, 'SUBMITTED', $hs$Effective Java, 3rd edition (Joshua Bloch), Item 2: Consider a builder when faced with many constructor parameters$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: an empty collection instead of null$hs$, $hs$`findWorkouts(athleteId)` has nothing to return. Why should it return an empty list rather than `null`?$hs$, $hs$Callers can use an empty list directly (loop over it, stream it, check `isEmpty()`) without a null check. Returning `null` forces every caller to remember the check, and one that forgets throws `NullPointerException`, often far from the cause.

```java
return List.of();
```$hs$, 3, 'SUBMITTED', $hs$Effective Java, 3rd edition (Joshua Bloch), Item 54: Return empty collections or arrays, not nulls$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: abstract class vs interface$hs$, $hs$Interfaces can have default methods. What can an abstract class still do that an interface can't?$hs$, $hs$- Hold state: instance fields (an interface can only have constants).
- Have constructors, and protected or package-private members.

In return, a class can extend only one abstract class but implement many interfaces. Use an interface to define a type; an abstract class to share state and implementation between closely related classes.$hs$, 4, 'SUBMITTED', $hs$dev.java, Interfaces https://dev.java/learn/interfaces/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: why composition is safer than inheritance$hs$, $hs$This set should count every exercise ever added. After `addAll(List.of("squat", "bench"))`, `added` is 4, not 2. Why, and how does composition avoid it?

```java
class CountingSet extends HashSet<String> {
    int added = 0;

    @Override public boolean add(String e) { added++; return super.add(e); }

    @Override public boolean addAll(Collection<? extends String> c) {
        added += c.size();
        return super.addAll(c);
    }
}
```$hs$, $hs$`HashSet.addAll` is implemented by calling `add` for each element, which is overridden, so every element is counted twice. The subclass depends on an implementation detail of its parent it can't see.

```java
class CountingSet {                       // wraps a Set instead of extending HashSet
    private final Set<String> set = new HashSet<>();
    int added = 0;

    boolean add(String e) { added++; return set.add(e); }

    boolean addAll(Collection<? extends String> c) {
        added += c.size();
        return set.addAll(c);              // the wrapped set calls its own add, never ours
    }
}
```

The wrapper relies only on the `Set` contract, not on how `HashSet` implements it.$hs$, 6, 'SUBMITTED', $hs$Effective Java, 3rd edition (Joshua Bloch), Item 18: Favor composition over inheritance$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Language: overloading vs overriding$hs$, $hs$What is the difference between overloading and overriding a method?$hs$, $hs$- Overloading: several methods in one class share a name but differ in their parameter lists. The compiler picks one at compile time, from the argument types.
- Overriding: a subclass redefines an inherited instance method with the same signature. The version that runs is picked at runtime, from the object's actual class.

```java
void log(Workout workout) { ... }
void log(Workout workout, Athlete athlete) { ... }   // overloads log

@Override
public String toString() { ... }                    // overrides Object.toString
```$hs$, 2, 'SUBMITTED', $hs$Java Language Specification, chapter 8: Classes (§8.4.8 inheritance, overriding, and hiding; §8.4.9 overloading) https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Language: static methods are hidden, not overridden$hs$, $hs$What does this print?

```java
class Exercise {
    static String kind() { return "exercise"; }
    String name() { return "exercise"; }
}

class Squat extends Exercise {
    static String kind() { return "squat"; }
    @Override String name() { return "squat"; }
}

Exercise exercise = new Squat();
System.out.println(exercise.kind());
System.out.println(exercise.name());
```$hs$, $hs$```
exercise
squat
```

- `kind()` is static: `Squat.kind()` hides `Exercise.kind()` instead of overriding it, and the call is chosen at compile time from the variable's declared type, `Exercise`.
- `name()` is an instance method: it's overridden, and the call is chosen at runtime from the object's class, `Squat`.$hs$, 4, 'SUBMITTED', $hs$Java Language Specification, §8.4.8.2 Hiding (by Class Methods) https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Dates and times: Instant vs LocalDateTime vs ZonedDateTime$hs$, $hs$Which `java.time` type should store the moment a workout was logged, and what do `LocalDateTime` and `ZonedDateTime` represent instead?$hs$, $hs$`Instant`: a point on the timeline in UTC, the same everywhere, which is right for "when it happened".

- `LocalDateTime`: a date and time without a time zone, e.g. "6 October, 18:00", which isn't a fixed moment until a zone is added.
- `ZonedDateTime`: a date and time in a specific time zone, e.g. for showing the time to an athlete in Bucharest, or scheduling a class there.$hs$, 3, 'SUBMITTED', $hs$Java SE 21 API, package java.time https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JVM: JDK vs JRE vs JVM$hs$, $hs$What is the difference between the JDK, the JRE and the JVM?$hs$, $hs$- JVM: the virtual machine that loads and runs bytecode, and manages memory and threads.
- JRE: the JVM plus the standard class libraries, enough to run Java programs.
- JDK: the JRE plus development tools: the compiler `javac`, `jar`, `jcmd`, `jlink` and others.

Since Java 11, vendors ship only JDKs; a minimal runtime image for an application can be built with `jlink`.$hs$, 1, 'SUBMITTED', $hs$JVM Specification, Chapter 2 The Structure of the Java Virtual Machine https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JVM: bytecode and portability$hs$, $hs$What is bytecode, and why does it make Java programs portable?$hs$, $hs$Bytecode is the platform-independent instruction set that `javac` compiles Java source into, stored in `.class` files.

The same `.class` files run on any operating system and processor that has a JVM: the JVM interprets the bytecode and compiles the frequently used parts to native machine code for that platform.$hs$, 1, 'SUBMITTED', $hs$JVM Specification, Chapter 2 The Structure of the Java Virtual Machine https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JVM: class loading and parent delegation$hs$, $hs$How are classes loaded in the JVM, and what is the parent delegation model?$hs$, $hs$Classes are loaded lazily, the first time they're needed, by class loaders arranged in a hierarchy: the bootstrap loader (core JDK classes), the platform loader, and the application (system) loader for the classpath, plus custom loaders (application servers, plugins).

Parent delegation: a class loader first asks its parent to load the class, and loads it itself only if the parent can't. So core classes like `java.lang.String` always come from the bootstrap loader and can't be replaced by a class of the same name on the classpath.$hs$, 3, 'SUBMITTED', $hs$JVM Specification, Chapter 5 Loading, Linking, and Initializing https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JVM: ClassNotFoundException vs NoClassDefFoundError$hs$, $hs$What is the difference between a `ClassNotFoundException` and a `NoClassDefFoundError`?$hs$, $hs$- `ClassNotFoundException`: a checked exception, thrown when code asks for a class by name at runtime (`Class.forName("...")`, `loadClass`) and no class loader finds it. Typical: a missing JDBC driver.
- `NoClassDefFoundError`: an error, thrown when a class that was present at compile time can't be found or used at runtime: a JAR missing from the runtime classpath, a dependency version conflict, or a class whose static initialisation failed earlier ("Could not initialize class").$hs$, 4, 'SUBMITTED', $hs$JVM Specification, Chapter 5 Loading, Linking, and Initializing https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JIT: what it does$hs$, $hs$What does the JVM's just-in-time (JIT) compiler do?$hs$, $hs$It compiles frequently executed bytecode ("hot" methods and loops) into optimised native machine code while the program runs.

Because it compiles at runtime, it can use profiling information from the running program: which branches are taken, which classes actually appear at a call site. With that it inlines methods, removes unnecessary checks and optimises for the real workload.$hs$, 2, 'SUBMITTED', $hs$OpenJDK, HotSpot Group https://openjdk.org/groups/hotspot/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JIT: tiered compilation and warm-up$hs$, $hs$What is tiered compilation, and why is a freshly started Java service slower for its first requests?$hs$, $hs$Code passes through tiers as it gets hotter:

1. The interpreter runs bytecode directly, collecting profiles.
2. C1, a fast compiler, compiles warm methods with light optimisation.
3. C2 recompiles the hottest methods with aggressive optimisations based on the profiles.

Until the hot paths are compiled by C2, the service runs slower: this is the warm-up. Class loading, empty caches and connection pools opening add to it.$hs$, 3, 'SUBMITTED', $hs$OpenJDK, HotSpot Group https://openjdk.org/groups/hotspot/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory: heap vs stack$hs$, $hs$What is the difference between the heap and the stack in the JVM?$hs$, $hs$- Heap: holds all objects and arrays. Shared by all threads, and managed by the garbage collector.
- Stack: one per thread. Holds a frame for each method call in progress, with its local variables (primitives and references to objects) and partial results. A frame is removed when its method returns.

In `Athlete a = new Athlete()` inside a method, the `Athlete` object is on the heap; the reference `a` is in the method's stack frame.$hs$, 1, 'SUBMITTED', $hs$JVM Specification, §2.5 Run-Time Data Areas https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory: metaspace$hs$, $hs$What is metaspace?$hs$, $hs$The area of native memory (outside the heap) where the JVM stores class metadata: the structure of loaded classes and methods. It replaced the permanent generation (PermGen) in Java 8.

It grows with the number of loaded classes, and is unbounded by default; `-XX:MaxMetaspaceSize` caps it. A steadily growing metaspace often means a class loader leak, e.g. classes generated over and over.$hs$, 2, 'SUBMITTED', $hs$JVM Specification, §2.5 Run-Time Data Areas https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory: unbounded recursion$hs$, $hs$What happens when this method is called, and which memory runs out?

```java
int countSets(Workout workout) {
    return 1 + countSets(workout);
}
```$hs$, $hs$It throws a `StackOverflowError`.

Each call adds a frame to the thread's stack, and the recursion never ends, so the thread's stack (about 1 MB by default on 64-bit Linux, set with `-Xss`) runs out. The heap isn't involved.$hs$, 2, 'SUBMITTED', $hs$JVM Specification, §2.5.2 Java Virtual Machine Stacks https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$GC: which objects are garbage$hs$, $hs$How does the garbage collector decide which objects can be removed?$hs$, $hs$An object is garbage when it can't be reached from any GC root: local variables on thread stacks, static fields, and JNI references. The collector follows references from the roots and marks everything it reaches; everything else is reclaimed.

Reachability, not reference counting: two objects referring only to each other, but unreachable from the roots, are collected.$hs$, 1, 'SUBMITTED', $hs$HotSpot Garbage Collection Tuning Guide (Java 21) https://docs.oracle.com/en/java/javase/21/gctuning/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$GC: the generational hypothesis$hs$, $hs$What is the generational hypothesis, and how do garbage collectors use it?$hs$, $hs$Most objects die young: request objects, DTOs and temporary strings become garbage within milliseconds; few objects live long.

So the heap is split into a young generation, where new objects are allocated and which is collected often and cheaply (only the few survivors are copied), and an old generation for objects that survived several collections, collected less often.$hs$, 2, 'SUBMITTED', $hs$HotSpot Garbage Collection Tuning Guide (Java 21) https://docs.oracle.com/en/java/javase/21/gctuning/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$GC: stop-the-world pauses$hs$, $hs$What is a stop-the-world pause, and why does it matter for an API?$hs$, $hs$A phase of garbage collection during which all application threads are stopped, so the collector can work on the heap safely.

Every request in progress waits for the pause, so pause lengths add directly to response times, and long or frequent pauses show up in the high percentiles (p99) of latency.$hs$, 2, 'SUBMITTED', $hs$HotSpot Garbage Collection Tuning Guide (Java 21) https://docs.oracle.com/en/java/javase/21/gctuning/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$GC: G1 vs ZGC vs Parallel$hs$, $hs$Compare the G1, ZGC and Parallel garbage collectors. When would you choose each?$hs$, $hs$- G1: the default for server workloads. Balances throughput and pause times by collecting the heap in regions, aiming at a pause-time goal (200 ms by default). A good default for most services.
- ZGC: does almost all its work concurrently with the application, keeping pauses around a millisecond or below regardless of heap size, at some cost in throughput and memory. For latency-sensitive services, especially with large heaps. Generational by default since JDK 23.
- Parallel: maximises throughput with multiple threads, accepting longer stop-the-world pauses. For batch jobs where total time matters, not individual pauses.$hs$, 3, 'SUBMITTED', $hs$JEP 474 ZGC: Generational Mode by Default https://openjdk.org/jeps/474$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$GC: a collector for a latency-sensitive API$hs$, $hs$A payments API with a 16 GB heap has a p99 latency target of 50 ms, and GC pauses of up to 300 ms show up in its logs. How do you choose and validate the collector?$hs$, $hs$1. Check the current G1 setup: heap sizing, the pause goal (`-XX:MaxGCPauseMillis`), the allocation rate, and what kind of pauses dominate (young, mixed, full).
2. If tuning G1 can't keep pauses well inside the target, switch to ZGC (`-XX:+UseZGC`, generational), whose pauses stay around a millisecond regardless of heap size; budget some extra CPU and memory for it.
3. Validate under realistic load: GC logs and p99/p999 latency before and after, plus throughput and CPU.

Reducing allocation in the hot paths helps whichever collector is used.$hs$, 5, 'SUBMITTED', $hs$JEP 474 ZGC: Generational Mode by Default https://openjdk.org/jeps/474$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory: -Xms and -Xmx$hs$, $hs$What do the JVM options `-Xms` and `-Xmx` set?$hs$, $hs$- `-Xms`: the initial heap size.
- `-Xmx`: the maximum heap size. When the live objects need more than this, the JVM throws `OutOfMemoryError: Java heap space`.

E.g. `java -Xms1g -Xmx1g -jar app.jar` starts with a 1 GB heap that never grows or shrinks.$hs$, 1, 'SUBMITTED', $hs$The java Command (Java 21) https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: the default maximum heap$hs$, $hs$A Java 21 service runs in a container with a 2 GB memory limit and no heap options. What is its maximum heap size, and how should you set it?$hs$, $hs$About 512 MB. The JVM is container-aware and, by default, uses 25% of the memory it detects for the maximum heap (`-XX:MaxRAMPercentage=25`).

That usually wastes most of the container's memory. Set it as a share of the limit, leaving room for the non-heap memory, e.g. `-XX:MaxRAMPercentage=70`, or an explicit `-Xmx`.$hs$, 3, 'SUBMITTED', $hs$The java Command, -XX:MaxRAMPercentage https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Containers: killed by the kernel with a healthy heap$hs$, $hs$A pod with a 2 GB memory limit runs a JVM with `-Xmx1900m`. Kubernetes keeps killing it (`OOMKilled`), but the heap never comes near its maximum and there's no `OutOfMemoryError`. Why?$hs$, $hs$The container's limit covers the whole process, not only the heap. Metaspace, thread stacks, the code cache, direct buffers and GC structures need memory on top of the 1900 MB heap. When the process as a whole exceeds 2 GB, the kernel kills it, without any Java error.

Size the heap as a share of the limit (e.g. `-XX:MaxRAMPercentage=70`), and check the real non-heap usage with Native Memory Tracking (`jcmd <pid> VM.native_memory`).$hs$, 5, 'SUBMITTED', $hs$Java docs, Native Memory Tracking https://docs.oracle.com/en/java/javase/21/vm/native-memory-tracking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Performance: boxing in a hot loop$hs$, $hs$`reps` holds the repetitions of every set an athlete logged this year. Why is this loop far slower than it needs to be, and what's the fix?

```java
int[] reps = loadRepsPerSet();   // e.g. 10_000_000 sets
Long totalReps = 0L;
for (int i = 0; i < reps.length; i++) {
    totalReps += reps[i];
}
```$hs$, $hs$`totalReps` is declared as `Long`, a wrapper object, not as the primitive `long`. Every `+=` therefore unboxes the current value, adds, and **autoboxes** the result into a new `Long` object (only values from -128 to 127 are cached). The loop creates millions of short-lived objects, which costs allocation and garbage-collection time.

Use the primitive type:

```java
long totalReps = 0L;
for (int i = 0; i < reps.length; i++) {
    totalReps += reps[i];
}
```$hs$, 3, 'SUBMITTED', $hs$The Java Tutorials, Autoboxing and Unboxing https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Leaks: how a garbage-collected program leaks memory$hs$, $hs$How can a Java program leak memory, if the garbage collector frees unused objects?$hs$, $hs$The GC frees only unreachable objects. A leak is when objects that are no longer needed stay reachable:

- Static collections or caches that only grow.
- Listeners or callbacks registered and never removed.
- `ThreadLocal` values left on pooled threads.
- Class loaders kept alive after a redeploy, with all their classes.
- Unclosed resources holding buffers.$hs$, 2, 'SUBMITTED', $hs$Java Troubleshooting Guide, Troubleshoot Memory Leaks https://docs.oracle.com/en/java/javase/21/troubleshoot/troubleshooting-memory-leaks.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Leaks: an in-memory session map$hs$, $hs$The gym's check-in service crashes with `OutOfMemoryError: Java heap space` after about a week of running. What's the leak, and how do you fix it?

```java
@Service
public class CheckInService {

    private static final Map<String, WorkoutSession> sessions = new ConcurrentHashMap<>();

    public WorkoutSession checkIn(Athlete athlete) {
        WorkoutSession session = new WorkoutSession(athlete, Instant.now());
        sessions.put(UUID.randomUUID().toString(), session);
        return session;
    }
}
```$hs$, $hs$Every check-in adds an entry under a new random key, and nothing ever removes one. The static map keeps every session reachable for the life of the application, so the heap fills up.

Remove entries when sessions end, and bound what's left: e.g. a cache with a size limit and expiry, such as Caffeine:

```java
private final Cache<String, WorkoutSession> sessions = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofHours(4))
        .build();
```

Or keep sessions in the database instead of the heap.$hs$, 3, 'SUBMITTED', $hs$Java Troubleshooting Guide, Troubleshoot Memory Leaks https://docs.oracle.com/en/java/javase/21/troubleshoot/troubleshooting-memory-leaks.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Leaks: what the OutOfMemoryError messages mean$hs$, $hs$What does each of these `OutOfMemoryError` messages mean: "Java heap space", "Metaspace", "unable to create native thread", "Direct buffer memory"?$hs$, $hs$- "Java heap space": the heap is full of live objects; an allocation couldn't be satisfied even after a full GC. A leak, or a heap too small for the data.
- "Metaspace": too much class metadata, typically a class loader leak or endless generated classes.
- "unable to create native thread": the operating system or container refused a new thread (thread count limit or memory for its stack). Usually a thread leak or unbounded thread creation.
- "Direct buffer memory": the off-heap memory for direct `ByteBuffer`s is used up (`-XX:MaxDirectMemorySize`), e.g. by a networking library.$hs$, 3, 'SUBMITTED', $hs$Java Troubleshooting Guide, Understand the OutOfMemoryError Exception https://docs.oracle.com/en/java/javase/21/troubleshoot/troubleshooting-memory-leaks.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Memory: strong, soft, weak and phantom references$hs$, $hs$What is the difference between strong, soft, weak and phantom references in Java?$hs$, $hs$- Strong: an ordinary reference. The object stays alive as long as it's reachable this way.
- Soft (`SoftReference`): cleared only when the JVM needs memory, before an `OutOfMemoryError`. Used for memory-sensitive caches.
- Weak (`WeakReference`): cleared at the next GC once no strong reference remains. Used by `WeakHashMap`, for metadata attached to objects owned elsewhere.
- Phantom (`PhantomReference`): `get()` always returns `null`; it's enqueued after the object is collected, to run cleanup actions (what `java.lang.ref.Cleaner` builds on).$hs$, 4, 'SUBMITTED', $hs$Java SE 21 API, java.lang.ref package https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Leaks: finding the cause$hs$, $hs$A service's heap usage after each GC grows steadily over days until it crashes. How do you find the leak?$hs$, $hs$1. Make sure you get evidence: run with `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=...`, or take dumps yourself with `jcmd <pid> GC.heap_dump`, ideally two some hours apart.
2. Open the dump in Eclipse MAT: the dominator tree and the "leak suspects" report show which objects retain the most memory.
3. Follow the path to the GC roots to find what keeps them alive: a static map, a cache, a listener list.
4. Comparing two dumps shows which object counts grow.

Then fix the reference that keeps them, and confirm with the heap-after-GC metric over time.$hs$, 5, 'SUBMITTED', $hs$Eclipse Memory Analyzer (MAT) https://eclipse.dev/mat/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Diagnostics: thread dumps$hs$, $hs$What is a thread dump, and what do you look for in one?$hs$, $hs$A snapshot of every thread in the JVM: its name, state (`RUNNABLE`, `BLOCKED`, `WAITING`…) and current stack trace.

Look for:
- Deadlocks (the JVM reports them at the end).
- Many threads `BLOCKED` on the same lock, and which thread owns it.
- Many threads waiting in the same place, e.g. for a database connection or an HTTP response.
- What busy threads are actually doing.

Several dumps a few seconds apart show whether threads are stuck or moving.$hs$, 2, 'SUBMITTED', $hs$The jcmd Command (Java 21) https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Diagnostics: GC logs$hs$, $hs$How do you enable GC logging on a modern JVM (Java 9 and later), and what do the logs tell you?$hs$, $hs$GC logging is enabled when the JVM starts, with the unified logging option `-Xlog`:

```
java -Xlog:gc*:file=gc.log:time,uptime,level,tags -jar payments.jar
```

The log records every garbage collection: its type (young, mixed or full), its pause time, and the heap occupancy before and after. From it you can derive how often and how long the application pauses, the allocation rate, and the live heap that remains after each collection; a live heap that keeps growing points to a memory leak. Tools such as GCeasy and JDK Mission Control visualise these logs.$hs$, 3, 'SUBMITTED', $hs$The java Command, Enable Logging with the JVM Unified Logging Framework https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Diagnostics: one instance at 100% CPU$hs$, $hs$One instance of the payment service runs at 100% CPU while the others are fine. How do you find the responsible code?$hs$, $hs$1. Check whether it's the GC: GC logs or metrics showing constant collections (then it's a memory problem, see the heap).
2. Otherwise, profile it: a CPU profile with async-profiler or JFR for a minute, viewed as a flame graph, shows which methods use the CPU.
3. Without a profiler: find the busiest OS threads (`top -H -p <pid>`), convert their ids to hex, and find them by `nid` in a thread dump, taken a few times.

Typical findings: an infinite loop, catastrophic regex backtracking, a hot spin, or a huge collection processed per request.$hs$, 6, 'SUBMITTED', $hs$Brendan Gregg, Flame Graphs https://www.brendangregg.com/flamegraphs.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Performance: latency vs throughput$hs$, $hs$What is the difference between latency and throughput?$hs$, $hs$- Latency: how long a single operation takes, e.g. a transfer request answered in 80 ms.
- Throughput: how many operations complete per unit of time, e.g. 2,000 transfers per second.

They can trade off: batching raises throughput but makes individual operations wait; more concurrency raises throughput until contention starts increasing latency.$hs$, 2, 'SUBMITTED', $hs$Brendan Gregg, The USE Method https://www.brendangregg.com/usemethod.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Performance: percentiles vs averages$hs$, $hs$Why measure latency with percentiles (p95, p99) rather than the average?$hs$, $hs$The average hides the slow tail: 98 requests at 20 ms and 2 at 3 seconds average about 80 ms, which describes nobody's experience.

The p99 says 1% of requests are slower than this. A page or a user session makes many calls, so most users run into the p99 regularly. Targets and alerts are therefore set on high percentiles.$hs$, 3, 'SUBMITTED', $hs$Brendan Gregg, The USE Method https://www.brendangregg.com/usemethod.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Performance: measure first$hs$, $hs$A service is "too slow". What does "measure first" mean in practice?$hs$, $hs$1. Define the problem with numbers: which operation, which percentile, how slow vs the target, since when.
2. Measure where the time goes: traces (time per downstream call), profiles (CPU, allocations, locks), and resource metrics (CPU, memory, connection pool, GC).
3. Form one hypothesis from the data and change one thing.
4. Measure again under the same load to confirm the effect.

Optimising by guessing usually speeds up code that wasn't the bottleneck.$hs$, 4, 'SUBMITTED', $hs$Brendan Gregg, The USE Method https://www.brendangregg.com/usemethod.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Messaging: what a message broker solves$hs$, $hs$What problems does a message broker solve between services?$hs$, $hs$- Decoupling in time: the producer doesn't wait for the consumer, and the consumer can be down or slow; messages wait in the broker.
- Load levelling: bursts are queued and processed at the pace consumers can handle.
- Fan-out: several consumers react to the same message without the producer knowing them.
- Independent scaling: more consumers can share the work.$hs$, 1, 'SUBMITTED', $hs$Azure Architecture Center, Asynchronous messaging options (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/messaging$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Messaging: a queue vs a log$hs$, $hs$What is the difference between a message queue (e.g. RabbitMQ) and a log (e.g. Kafka)?$hs$, $hs$- Queue: each message is delivered to one consumer and removed once that consumer acknowledges it. Competing consumers share the work.
- Log: messages are appended and kept for a retention period, whether consumed or not. Each consumer group tracks its own position (offset), so several groups each read everything, and a group can rewind and replay.$hs$, 2, 'SUBMITTED', $hs$Apache Kafka documentation, Introduction https://kafka.apache.org/43/getting-started/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Messaging: commands vs events$hs$, $hs$What is the difference between a command message and an event message? Give a banking example of each.$hs$, $hs$- Command: asks one specific receiver to do something, and may be rejected. Named in the imperative: `BookTransfer`, `SendStatement`.
- Event: announces a fact that has already happened, for any number of interested listeners, who can't reject it. Named in the past tense: `TransferBooked`, `AccountClosed`.

The producer of an event doesn't know or care who reacts to it.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, What do you mean by "Event-Driven"? https://martinfowler.com/articles/201701-event-driven.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Messaging: a synchronous call or a message$hs$, $hs$Accepting a transfer needs the balance check from the account service; after booking, statements and notifications must be updated. Which of these calls should be synchronous and which a message, and what decides it?$hs$, $hs$Whether the caller needs the answer to continue. The balance check is synchronous: the transfer can't be accepted or refused without it, and the customer is waiting for that answer.

Updating statements and sending notifications are messages: they can happen a moment later, the transfer service doesn't need their result, and it stays available while those services are down.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Asynchronous messaging options (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/messaging$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: topics, partitions and offsets$hs$, $hs$In Kafka, what are a topic, a partition and an offset?$hs$, $hs$- Topic: a named stream of records, such as `transfers`.
- Partition: a topic is split into partitions, each an ordered, append-only log, stored and replicated on brokers. Partitions let a topic be spread over brokers and read in parallel.
- Offset: the position of a record within its partition. Consumers track which offset they've processed up to.$hs$, 1, 'SUBMITTED', $hs$Apache Kafka documentation, Introduction https://kafka.apache.org/43/getting-started/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: consumer groups$hs$, $hs$What is a Kafka consumer group?$hs$, $hs$A set of consumers that share a group id and divide a topic's partitions among them: each partition is read by exactly one consumer of the group at a time, so the group processes the topic in parallel.

Different groups are independent: the fraud service and the statement service each receive every record of `transfers`, each tracking its own offsets.$hs$, 2, 'SUBMITTED', $hs$Apache Kafka documentation, Introduction https://kafka.apache.org/43/getting-started/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: more consumers than partitions$hs$, $hs$The `transfers` topic has 6 partitions, and the consumer group of the fraud service runs 8 instances. How many of them process records?$hs$, $hs$6. Each partition is assigned to one consumer of the group, so 2 instances stay idle (standbys that take over if another fails).

A consumer group's parallelism is limited by the number of partitions; to process with more consumers, the topic needs more partitions.$hs$, 3, 'SUBMITTED', $hs$Apache Kafka documentation, Introduction https://kafka.apache.org/43/getting-started/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: which partition a record goes to$hs$, $hs$What decides which partition of a topic a record is written to?$hs$, $hs$- With a key: a hash of the key, modulo the number of partitions. All records with the same key (e.g. the account id) go to the same partition, so they stay in order.
- Without a key: the producer spreads records over partitions, filling a batch for one partition before moving on (sticky partitioning).
- A custom partitioner can override both.$hs$, 3, 'SUBMITTED', $hs$Apache Kafka documentation, Design (The Producer) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: retention and log compaction$hs$, $hs$How long does Kafka keep records, and what is log compaction?$hs$, $hs$Records are kept per topic for a retention time (7 days by default) or up to a size, whether they've been consumed or not, then deleted.

Log compaction instead keeps at least the latest record for every key and removes older records with the same key. The topic becomes a changelog of current state, e.g. the latest customer address per customer id; a record with a null value (a tombstone) deletes the key.$hs$, 3, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Log Compaction) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: acks=all and min.insync.replicas$hs$, $hs$What do the producer setting `acks=all` and the topic setting `min.insync.replicas=2` guarantee together, for a topic with replication factor 3?$hs$, $hs$- `acks=all`: the leader acknowledges a write only after all in-sync replicas have it.
- `min.insync.replicas=2`: a write is accepted only if at least 2 replicas (the leader included) are in sync; otherwise the producer gets an error instead of a weakly replicated write.

Together, every acknowledged record exists on at least 2 brokers, so it survives the loss of one broker. `acks=all` is the producer default in current Kafka versions; `min.insync.replicas` defaults to 1 and must be raised.$hs$, 4, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Replication) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: adding partitions to a topic$hs$, $hs$The `transfers` topic, keyed by account id, is increased from 6 to 12 partitions. What can go wrong?$hs$, $hs$The partition for a key is hash(key) modulo the partition count, so after the change many accounts map to a different partition. New records for an account go to the new partition while older ones may still wait in the old one, so for a while an account's records can be processed out of order.

Plan partition counts generously up front. If they must change, do it at a quiet time, letting consumers drain first, or migrate to a new topic.$hs$, 5, 'SUBMITTED', $hs$Apache Kafka documentation, Design (The Producer) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: a consumer lagging further and further behind$hs$, $hs$The consumer group of the statement service falls further and further behind the `transfers` topic: its lag grows every hour. What can cause this, and what can you do about it?$hs$, $hs$Lag grows when records are produced faster than the group consumes them.

- Slow processing per record (a slow database or HTTP call): make it faster, or batch the writes.
- Too few consumers: add consumers, up to the number of partitions; beyond that, the topic needs more partitions.
- Frequent rebalances: a consumer that takes longer than `max.poll.interval.ms` (5 minutes by default) between polls is removed from the group. Process smaller batches (`max.poll.records`) or speed up processing.
- One hot partition: a key that carries most of the traffic. Choose a key that spreads the load.

Monitor lag per partition, to tell a general slowdown from one hot partition.$hs$, 6, 'SUBMITTED', $hs$Apache Kafka documentation, Consumer Configs https://kafka.apache.org/43/configuration/consumer-configs/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: records processed again and again$hs$, $hs$A consumer fetches 500 records per poll and calls a slow API for each. Every few minutes the same records are processed again, by another instance, and the logs show rebalances. Why?$hs$, $hs$Processing the batch takes longer than `max.poll.interval.ms` (5 minutes by default). The consumer doesn't poll in time, so the group considers it failed and rebalances: its partitions go to another instance, which starts from the last committed offset and processes the same records again. Meanwhile, the first consumer can't commit anymore.

Fixes: fewer records per poll (`max.poll.records`), faster processing (parallel or batched calls), or a longer `max.poll.interval.ms`. Processing must be idempotent either way.$hs$, 6, 'SUBMITTED', $hs$Apache Kafka documentation, Consumer Configs (max.poll.interval.ms) https://kafka.apache.org/43/configuration/consumer-configs/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$RabbitMQ: exchanges, queues and bindings$hs$, $hs$In RabbitMQ, what are exchanges, queues and bindings?$hs$, $hs$- Exchange: where producers publish messages. It doesn't store them; it routes them.
- Queue: stores messages until consumers receive and acknowledge them.
- Binding: a rule connecting an exchange to a queue, often with a routing key or pattern, that decides which messages reach the queue.

Producers never publish to a queue directly (the default exchange routes by queue name).$hs$, 2, 'SUBMITTED', $hs$RabbitMQ, AMQP 0-9-1 Model Explained https://www.rabbitmq.com/tutorials/amqp-concepts$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$RabbitMQ: direct, topic and fanout exchanges$hs$, $hs$What is the difference between RabbitMQ's direct, topic and fanout exchanges?$hs$, $hs$- Direct: routes a message to the queues whose binding key equals its routing key exactly, e.g. `statement.monthly`.
- Topic: routes by pattern on dot-separated routing keys: `*` matches one word, `#` zero or more, e.g. `transfer.*.failed` or `transfer.#`.
- Fanout: ignores the routing key and copies the message to every bound queue.$hs$, 3, 'SUBMITTED', $hs$RabbitMQ, AMQP 0-9-1 Model Explained https://www.rabbitmq.com/tutorials/amqp-concepts$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Messaging: Kafka or RabbitMQ$hs$, $hs$When would you choose Kafka, and when RabbitMQ?$hs$, $hs$- Kafka: high-throughput event streams, several independent consumers of the same events, replay of history, ordering per key, retention as a source for new services or analytics.
- RabbitMQ: task queues with work distributed among competing consumers, flexible routing, per-message acknowledgement, redelivery and priorities, request-reply, lower volumes.

Recent Kafka versions add share groups for queue-like consumption, but the models remain different: Kafka is a log, RabbitMQ a router of messages.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Asynchronous messaging options (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/messaging$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: at-most-once, at-least-once, exactly-once$hs$, $hs$What do at-most-once, at-least-once and exactly-once delivery mean?$hs$, $hs$- At most once: a message is never processed twice, but may be lost.
- At least once: a message is never lost, but may be processed more than once.
- Exactly once: neither lost nor duplicated. End to end, it's achieved by at-least-once delivery plus idempotent processing (or transactions within one system), not by the network alone.$hs$, 2, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Message Delivery Semantics) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: committing Kafka offsets before or after processing$hs$, $hs$A Kafka consumer processes each `TransferBooked` event and only then commits its offset. Which delivery guarantee does this give, and what must the processing be to make it safe? What changes if it commits first?$hs$, $hs$At-least-once: no event is lost, but an event can be processed twice. If the consumer crashes after processing but before committing, the event is delivered again after the restart.

So the processing must be idempotent: handling the same event twice has the same effect as once, e.g. by storing each processed event id under a unique constraint and skipping ids already seen.

Committing first gives at-most-once: if it crashes after the commit, before processing, the event is skipped and lost.$hs$, 3, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Message Delivery Semantics) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: an idempotent consumer$hs$, $hs$What is an idempotent consumer, and name three ways to make one.$hs$, $hs$A consumer for which processing the same message twice has the same effect as processing it once, so redeliveries are harmless.

- Record processed message ids in the database, under a unique constraint, in the same transaction as the effect; skip messages already recorded.
- Use operations that are naturally idempotent: upserts and "set" instead of increments ("status = BOOKED", not "count + 1").
- Check the current state before acting: ignore a "TransferBooked" for a transfer that's already booked.$hs$, 3, 'SUBMITTED', $hs$microservices.io, Pattern: Idempotent Consumer https://microservices.io/patterns/communication-style/idempotent-consumer.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: making a payment consumer idempotent$hs$, $hs$This consumer credits an account for each `PaymentReceived` event. After a redelivery, a customer is credited twice. Make it idempotent.

```java
@KafkaListener(topics = "payments-received")
@Transactional
public void on(PaymentReceived event) {
    Account account = accounts.findById(event.accountId()).orElseThrow();
    account.credit(event.amount());
}
```$hs$, $hs$Record the event's unique id in the same transaction as the credit, protected by a unique constraint, and skip events already recorded:

```java
@KafkaListener(topics = "payments-received")
@Transactional
public void on(PaymentReceived event) {
    if (!processedEvents.markProcessed(event.eventId())) {   // INSERT ... ON CONFLICT DO NOTHING, false if it existed
        return;                                             // already handled: acknowledge and skip
    }
    Account account = accounts.findById(event.accountId()).orElseThrow();
    account.credit(event.amount());
}
```

Both the marker and the credit commit together, or neither does, so a redelivery after a crash is processed exactly once.$hs$, 5, 'SUBMITTED', $hs$microservices.io, Pattern: Idempotent Consumer https://microservices.io/patterns/communication-style/idempotent-consumer.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: what Kafka's exactly-once semantics covers$hs$, $hs$Kafka advertises "exactly-once semantics". What does it actually cover, and what doesn't it?$hs$, $hs$Covered:
- Idempotent producers: retries don't write duplicates to a partition.
- Transactions: a consume-process-produce cycle within Kafka (read from one topic, write to others, and commit the consumer offsets) happens atomically, and consumers with `isolation.level=read_committed` see only committed results.

Not covered: side effects outside Kafka, such as writing to a database, calling a payment API or sending an email. Those can still happen twice after a failure, so they need idempotency of their own.$hs$, 6, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Using Transactions) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Delivery: a card charged twice after a crash$hs$, $hs$A Kafka consumer charges the card through an external payment provider, then crashes before its offset is committed. What happens after the restart, and how do you make it safe?$hs$, $hs$The record is delivered again, and the card is charged a second time: the charge happened outside Kafka, so neither the offset commit nor Kafka transactions can undo it.

- Pass an idempotency key (the event id) to the provider, so it returns the first charge's result instead of charging again.
- Record the charge's outcome with the event id in your database, and check it before calling.$hs$, 7, 'SUBMITTED', $hs$Apache Kafka documentation, Design (Message Delivery Semantics) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Kafka: transactions for read-process-write$hs$, $hs$A Spring Kafka listener reads `transfer-booked`, computes the fee, and publishes `fee-charged`. After a crash, a transfer can be charged twice, because the output was published but the input's offset wasn't committed. How do Kafka transactions make the output and the offset commit atomic, and what must consumers of `fee-charged` set?$hs$, $hs$Enable transactions on the producer; the listener container then runs each record in a Kafka transaction:

```properties
spring.kafka.producer.transaction-id-prefix=fees-tx-
```

```java
@KafkaListener(topics = "transfer-booked")
void onTransfer(TransferBooked event) {
    kafkaTemplate.send("fee-charged", feeFor(event));   // part of the transaction
}
```

The container sends the consumed offset to the same transaction, so the `fee-charged` record and the offset commit become visible together, or not at all after a crash.

Consumers of `fee-charged` must set `isolation.level=read_committed`, or they also read records from aborted transactions. This only covers effects inside Kafka; a database write in the listener isn't part of it.$hs$, 7, 'SUBMITTED', $hs$Spring for Apache Kafka, Transactions https://docs.spring.io/spring-kafka/reference/kafka/transactions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Ordering: Kafka's ordering guarantees$hs$, $hs$What ordering does Kafka guarantee?$hs$, $hs$Records are ordered within one partition: consumers read them in the order they were written. There's no ordering across partitions of a topic.

So whatever must stay in order needs to share a partition, i.e. the same key.$hs$, 2, 'SUBMITTED', $hs$Apache Kafka documentation, Introduction https://kafka.apache.org/43/getting-started/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Ordering: one account's events in order$hs$, $hs$All events of an account (`AccountOpened`, `MoneyDeposited`, `MoneyWithdrawn`, `AccountClosed`) must be processed in order. How do you achieve that with Kafka?$hs$, $hs$- Produce all of them to the same topic with the account id as the key, so they land in the same partition, in order.
- Process each partition sequentially: don't hand records of one partition to parallel threads (or parallelise only across keys).
- On the producer, keep idempotence on (the default), so retries can't reorder records.

Different accounts are still processed in parallel, across partitions.$hs$, 4, 'SUBMITTED', $hs$Apache Kafka documentation, Design (The Producer) https://kafka.apache.org/43/design/design/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Ordering: retrying a failed record later$hs$, $hs$To avoid blocking a partition, a consumer sends a failed `MoneyWithdrawn` event to a retry topic and carries on with the next records. What happens to ordering, and when is that acceptable?$hs$, $hs$Later events of the same account (e.g. `AccountClosed`) are processed before the retried event, so the account's events are applied out of order.

Acceptable when events of a key are independent or the consumer tolerates any order. When order per key matters: retry in place (blocking the partition), or, when one record is parked, park all later records of the same key too until it's resolved.$hs$, 6, 'SUBMITTED', $hs$Spring for Apache Kafka, Non-Blocking Retries https://docs.spring.io/spring-kafka/reference/retrytopic.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Failure handling: poison messages and dead-letter topics$hs$, $hs$What is a poison message, and what is a dead-letter queue (or topic) for?$hs$, $hs$- Poison message: a message that fails every time it's processed (invalid data, a bug), so retrying it forever blocks everything behind it.
- Dead-letter queue or topic: where messages go after their retries are exhausted. The consumer moves on, and the failed messages can be inspected, fixed and replayed later.$hs$, 2, 'SUBMITTED', $hs$Spring for Apache Kafka, Handling Exceptions https://docs.spring.io/spring-kafka/reference/kafka/annotation-error-handling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring Kafka: retries with backoff, then a dead-letter topic$hs$, $hs$Configure the Spring Kafka listener of the statement service to retry a failed record 3 times, one second apart, then publish it to a dead-letter topic. Validation failures should go to the dead-letter topic without retries.$hs$, $hs$```java
@Bean
DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);   // to "<topic>-dlt"
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    handler.addNotRetryableExceptions(InvalidStatementRequestException.class);
    return handler;
}
```

Spring Boot applies a `CommonErrorHandler` bean to the auto-configured listener container factory. Without configuration, the `DefaultErrorHandler` retries 9 times with no delay and then only logs the failure.$hs$, 5, 'SUBMITTED', $hs$Spring for Apache Kafka, Handling Exceptions (DefaultErrorHandler) https://docs.spring.io/spring-kafka/reference/kafka/annotation-error-handling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Failure handling: blocking vs non-blocking retries$hs$, $hs$Compare blocking retries in the listener with non-blocking retries through retry topics (`@RetryableTopic` in Spring Kafka).$hs$, $hs$- Blocking: the consumer retries the record in place, with backoff, before moving on. Ordering is kept, but the whole partition stalls while it retries.
- Non-blocking: the failed record is published to a retry topic (one or more, with increasing delays) and the consumer carries on. Throughput is kept, but later records of the same key overtake the retried one.

Blocking for short retries or where order per key matters; non-blocking for longer outages of a dependency when order doesn't matter.$hs$, 5, 'SUBMITTED', $hs$Spring for Apache Kafka, Non-Blocking Retries https://docs.spring.io/spring-kafka/reference/retrytopic.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Outbox: the dual-write problem$hs$, $hs$What is the dual-write problem?$hs$, $hs$A service changes its database and publishes a message about the change: two separate systems, with no shared transaction. One write can succeed while the other fails, or the process can crash in between, so the database and the published events disagree: an event for a change that was rolled back, or a change whose event is never sent.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Transactional Outbox pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/databases/guide/transactional-outbox-cosmos$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Outbox: publishing a transfer event reliably$hs$, $hs$`bookTransfer` saves the transfer and publishes a `TransferBooked` event to Kafka. Describe what can go wrong, and change the code so the event is published if and only if the transfer is saved.

```java
@Transactional
public void bookTransfer(Transfer transfer) {
    transferRepository.save(transfer);
    kafkaTemplate.send("transfers", transfer.accountId().toString(), new TransferBooked(transfer.id()));
}
```$hs$, $hs$The database and Kafka are two systems, and `@Transactional` covers only the database:
- The event is sent, then the commit fails: consumers react to a transfer that doesn't exist.
- The commit succeeds, but the send fails or the process crashes in between: the event is lost.

Transactional outbox: write the event to an `outbox` table in the same database transaction, and let a separate relay publish it.

```java
@Transactional
public void bookTransfer(Transfer transfer) {
    transferRepository.save(transfer);
    outboxRepository.save(OutboxEvent.of("TransferBooked", transfer.accountId(), transfer.id()));   // same transaction
}
```

- The relay reads new outbox rows (by polling, or by change data capture such as Debezium), publishes them, and marks them sent.
- Delivery is at-least-once, so consumers must be idempotent.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Transactional Outbox pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/databases/guide/transactional-outbox-cosmos$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Outbox: polling vs change data capture$hs$, $hs$The outbox relay can poll the outbox table or use change data capture (CDC) with Debezium. Compare the two.$hs$, $hs$- Polling: a scheduled job selects unsent rows (`FOR UPDATE SKIP LOCKED` with several instances), publishes them, and marks or deletes them. Simple, no extra infrastructure; but it adds query load and latency (the polling interval), and the table needs cleaning.
- CDC: Debezium reads the database's transaction log (the PostgreSQL WAL) and publishes each inserted outbox row to Kafka. Low latency, no polling queries, order follows the commit order; but it requires running and operating Kafka Connect and Debezium, and database configuration for logical replication.$hs$, 5, 'SUBMITTED', $hs$Debezium documentation, Outbox Event Router https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Outbox: why "publish after commit" isn't enough$hs$, $hs$Instead of an outbox, a developer publishes the event in a `@TransactionalEventListener(phase = AFTER_COMMIT)`. Why doesn't that solve the dual-write problem?$hs$, $hs$It fixes one half: no event is published for a transaction that rolls back.

But if the process crashes, is redeployed, or Kafka is unavailable right after the commit, the event is never published, and nothing records that it's still owed. The database change is permanent; the event is lost.

The outbox stores the obligation to publish in the same transaction as the change, so a relay can always retry it.$hs$, 5, 'SUBMITTED', $hs$microservices.io, Pattern: Transactional outbox https://microservices.io/patterns/data/transactional-outbox.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Outbox: the inbox pattern on the consumer side$hs$, $hs$What is the inbox pattern on the consumer side?$hs$, $hs$The consumer stores each incoming message's id (and often the message itself) in an inbox table in its own database, in the same transaction as the processing, or before processing it.

- Duplicates are detected by the id and skipped, which makes the consumer idempotent.
- With the message stored, it can be acknowledged to the broker right away and processed (and retried) from the database, independently of the broker.$hs$, 4, 'SUBMITTED', $hs$microservices.io, Pattern: Idempotent Consumer https://microservices.io/patterns/communication-style/idempotent-consumer.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: notification vs event-carried state transfer$hs$, $hs$Compare event notification with event-carried state transfer, using a changed customer address as the example.$hs$, $hs$- Event notification: `CustomerAddressChanged {customerId: 42}`. Consumers that need the new address call the customer service. Small events, but consumers depend on that service being available, and it gets the load.
- Event-carried state transfer: the event includes the new address. Consumers keep their own copy and never call back: more decoupled and resilient, but events are bigger and the data is duplicated (and eventually consistent) across services.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, What do you mean by "Event-Driven"? https://martinfowler.com/articles/201701-event-driven.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: what every event should carry$hs$, $hs$Besides its business data, what should every event in a banking system carry?$hs$, $hs$- A unique event id, for deduplication.
- The event type and its schema version.
- The time it happened.
- The id of the entity it's about (e.g. the account), and a sequence number or version per entity, to detect gaps and reordering.
- The producer (source).
- A correlation or trace id, to follow a flow across services.

CloudEvents standardises such an envelope.$hs$, 4, 'SUBMITTED', $hs$CloudEvents specification https://cloudevents.io/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: evolving event schemas safely$hs$, $hs$Which changes to the `TransferBooked` event are safe for existing consumers, and how do you make a breaking change?$hs$, $hs$Safe (backward and forward compatible): adding an optional field with a default. Consumers that don't know it ignore it.

Breaking: removing or renaming a field, changing its type or meaning, making an optional field required.

For a breaking change, publish a new version of the event (a new type or topic) alongside the old one, migrate consumers, then stop the old one. A schema registry with a compatibility mode checks every new schema version before it's used.$hs$, 5, 'SUBMITTED', $hs$Confluent documentation, Schema Evolution and Compatibility https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: event sourcing$hs$, $hs$What is event sourcing?$hs$, $hs$Instead of storing only the current state, the system stores every change as an event in an append-only store, and the events are the source of truth. The current state is derived by replaying them: an account's balance is the result of its `MoneyDeposited` and `MoneyWithdrawn` events.

It gives a complete history and audit trail, and state can be rebuilt as of any point in time.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Event Sourcing pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/event-sourcing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: CQRS$hs$, $hs$What is CQRS?$hs$, $hs$Command Query Responsibility Segregation: separate models for changing data and for reading it.

- The write side handles commands and enforces the business rules.
- The read side has models shaped for the queries (e.g. a denormalised transaction history), often in a separate store, updated from the write side's events.

Each side can be optimised and scaled on its own; the price is that read models are eventually consistent.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, CQRS pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/cqrs$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: a read model that lags behind$hs$, $hs$After making a transfer, a customer opens the transaction history, which is a read model updated from events, and the transfer isn't there yet. How do you handle this?$hs$, $hs$Accept the eventual consistency and design for it:

- Return the created transfer from the command, and show it in the UI immediately (as "pending").
- Read-your-writes: for the user who just wrote, read from the write side, or wait until the read model has processed at least the version returned by the command.
- Show processing states instead of pretending it's final.
- Monitor and alert on the projection's lag.$hs$, 6, 'SUBMITTED', $hs$Azure Architecture Center, CQRS pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/cqrs$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring Kafka: consuming a topic$hs$, $hs$How do you consume the `transfers` topic in a Spring Boot service?$hs$, $hs$Add `spring-kafka`, configure the brokers and deserializers in `spring.kafka.*` properties, and annotate a bean method:

```java
@Component
class StatementUpdater {

    @KafkaListener(topics = "transfers", groupId = "statement-service")
    void on(TransferBooked event) {
        statements.add(event);
    }
}
```

Spring Boot configures the consumer factory and the listener container, which polls Kafka, calls the method for each record and commits the offsets.$hs$, 2, 'SUBMITTED', $hs$Spring for Apache Kafka, @KafkaListener Annotation https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/listener-annotation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring Kafka: when offsets are committed$hs$, $hs$When does a Spring Kafka listener container commit offsets by default, and what delivery guarantee follows?$hs$, $hs$Spring Kafka turns off Kafka's own auto-commit (`enable.auto.commit=false`) and commits itself, by default in `BATCH` mode: after the listener has processed all the records returned by a poll.

So processing is at-least-once: if the instance crashes during a batch, the records since the last commit are delivered again, and the listener must be idempotent.$hs$, 4, 'SUBMITTED', $hs$Spring for Apache Kafka, Message Listener Containers (Committing Offsets) https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/message-listener-container.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring Kafka: concurrency$hs$, $hs$How do you make one instance of a Spring Kafka listener process several partitions in parallel, and what limits it?$hs$, $hs$Set the container's concurrency, e.g. `@KafkaListener(topics = "transfers", concurrency = "4")` (or `spring.kafka.listener.concurrency`). The container then runs 4 consumers, each on its own thread with its own partitions.

The total number of consumers in the group, across all instances, is useful only up to the number of partitions; extra ones stay idle. Order is preserved per partition, because each partition is still handled by one thread.$hs$, 5, 'SUBMITTED', $hs$Spring for Apache Kafka, Message Listener Containers https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/message-listener-container.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['messaging', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Microservices: what they are$hs$, $hs$What is a microservice architecture?$hs$, $hs$A microservice architecture structures an application as a collection of small, autonomous services, each of which:
- implements a single business capability (accounts, payments, cards);
- owns its data, which other services reach only through its API;
- is deployed independently of the others;
- communicates with the others over the network, through APIs (HTTP, gRPC) or messaging.

Each service is usually owned by one team, which can change, release and scale it on its own.$hs$, 1, 'SUBMITTED', $hs$Azure Architecture Center, Microservices architecture style (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/microservices$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Microservices: the modular monolith$hs$, $hs$A team wants firm boundaries between the parts of its application, without the operational cost of running separate services. How is the architecture known as a modular monolith defined, and what does it keep from each side?$hs$, $hs$A modular monolith is a single deployable application that is internally divided into modules with explicit boundaries. Each module exposes a small public API, hides its internals and, ideally, owns its own tables; modules communicate only through those APIs or through in-process events.

- From the monolith it keeps operational simplicity: one deployment, in-process calls, and a single database transaction where one is needed.
- From microservices it keeps the boundaries: code is organised by business capability, so a module can later be extracted into a service if it needs to scale or be released independently.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, MonolithFirst https://martinfowler.com/bliki/MonolithFirst.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Microservices: benefits and costs$hs$, $hs$What do microservices give you compared to a monolith, and what do they cost?$hs$, $hs$Benefits:
- independent deployment and scaling per service
- team autonomy: each team owns and releases its services
- fault isolation: one failing service needn't take down everything
- freedom of technology per service.

Costs:
- network calls: latency, partial failure, timeouts and retries
- distributed data: no transactions across services, eventual consistency
- operations: many deployments, monitoring, tracing, configuration
- harder testing and debugging across services.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, Microservices https://martinfowler.com/articles/microservices.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Microservices: the distributed monolith$hs$, $hs$What is a "distributed monolith", and how do you recognise one?$hs$, $hs$A system split into services that still behave like one application: all the costs of microservices without their independence.

Signs:
- services have to be deployed together, in a certain order
- several services read and write the same database tables
- long synchronous call chains, so one service down breaks every request
- a change to one feature regularly touches several services
- shared domain libraries that force everyone to upgrade at once.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, Microservices https://martinfowler.com/articles/microservices.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Microservices: a small startup$hs$, $hs$A team of six developers is starting a new digital bank and wants to build it as microservices from day one. What do you advise, and why?$hs$, $hs$Start with a well-modularised monolith.

- The domain boundaries aren't known yet; boundaries drawn too early are expensive to move once they're network APIs with separate data.
- Six people can't benefit from team autonomy, but pay the full operational cost: many pipelines, distributed tracing, data consistency across services.
- A modular monolith with enforced boundaries keeps the option open.

Extract a service when there's a concrete reason: a part with very different scaling or availability needs, or a growing organisation that needs independent deploys.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, MonolithFirst https://martinfowler.com/bliki/MonolithFirst.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Boundaries: bounded contexts as service boundaries$hs$, $hs$To the gym's training team an "athlete" has workouts and personal bests; to billing, an "athlete" has a membership and payment details. In domain-driven design, how should a training service and a billing service model the athlete?$hs$, $hs$Separately: training and billing are two bounded contexts, and each has its own `Athlete` model, with only what that context needs. Inside a bounded context every term has one precise meaning; across contexts, the same word can mean different things.

The two models share an identity (the athlete id), not one class. A single shared `Athlete` with every field would couple both services to each other's changes.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, BoundedContext https://martinfowler.com/bliki/BoundedContext.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Boundaries: finding good service boundaries$hs$, $hs$How do you find good boundaries between services?$hs$, $hs$- Around business capabilities and bounded contexts (accounts, payments, cards, lending), not technical layers.
- High cohesion inside, loose coupling between: most changes should stay inside one service.
- Clear data ownership: each piece of data has one owning service.
- Few synchronous calls needed to serve a request.
- Team ownership: one team can own and evolve the service on its own.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Identify microservice boundaries (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/microservice-boundaries$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Boundaries: splitting by technical layer$hs$, $hs$A team splits its application into a "validation service", a "business logic service" and a "data access service". What's wrong with that?$hs$, $hs$Every feature crosses all three services: each request makes chained network calls, every change touches and redeploys all three, and no team can deliver a feature alone. The services are tightly coupled and not independently useful.

Split by business capability instead, with each service containing its own validation, logic and data access.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Identify microservice boundaries (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/microservice-boundaries$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Data: a database per service$hs$, $hs$Why should each microservice own its data, rather than all services sharing one database?$hs$, $hs$A shared database couples the services through the schema:
- one team can't change a table without checking every service that reads it
- services can't be deployed, migrated or scaled independently
- the dependencies are invisible (no API documents them)
- one service's heavy queries slow down the others.

With data ownership, other services get data only through the owner's API or events, which the owner can keep stable while changing its internals.$hs$, 2, 'SUBMITTED', $hs$microservices.io, Pattern: Database per service https://microservices.io/patterns/data/database-per-service.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Data: needing another service's data$hs$, $hs$The payments service needs the customer's name and address, which the customer service owns. What are the options, and what fits a payment?$hs$, $hs$- Call the customer service when needed: always current, but payments now depend on its availability and latency.
- Keep a local copy, updated from `CustomerChanged` events: no runtime dependency, but eventually consistent.
- Copy what's needed into the payment when it's made: a snapshot.

For a payment, the snapshot is usually right: the payment must record the name and address as they were when it was made, not as they are today.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Data considerations for microservices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/design/data-considerations$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Data: queries across services$hs$, $hs$A screen needs "the customer's payments, with the merchant name and the dispute status", owned by three services. How can you answer such a query?$hs$, $hs$- API composition: a composer (a BFF, gateway or service) calls the three services and joins the results in memory. Simple and current; but slower, dependent on all three, and hard for large or filtered result sets.
- A read model (CQRS view): a service subscribes to the events of all three and maintains a denormalised table shaped for this screen. Fast and independent at query time; eventually consistent, and more to build and operate.

Composition for simple, small queries; a read model for heavy or frequently used ones.$hs$, 5, 'SUBMITTED', $hs$microservices.io, Pattern: API Composition https://microservices.io/patterns/data/api-composition.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Boundaries: two services that always change together$hs$, $hs$The limits service and the payments service change together in almost every release, and are always deployed together. What does that say about the boundary between them, and how do you fix it?$hs$, $hs$It indicates a misplaced boundary: one business concept is split across two services, so they are tightly coupled. The system pays the cost of a network boundary without gaining independent releases, a small-scale distributed monolith.

The fix is to move the boundary so that the shared concept has one owner:
- Identify the concept that keeps crossing the boundary; the change history of both services shows it.
- Give that concept to one service, and let the other use it only through a small, stable, versioned API.
- If the two services turn out to be a single business capability, merge them into one service.$hs$, 6, 'SUBMITTED', $hs$Azure Architecture Center, Identify microservice boundaries (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/microservice-boundaries$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: synchronous vs asynchronous$hs$, $hs$Compare synchronous (REST, gRPC) and asynchronous (events, messages) communication between services.$hs$, $hs$- Synchronous: simple to write and follow, and the caller gets an answer immediately. But the caller depends on the callee being up and fast at that moment: availability and latency are coupled.
- Asynchronous: the sender continues without waiting; the receiver can be down or slow, and bursts are absorbed by the broker. But results are eventually consistent, and flows are harder to follow and debug.

Synchronous when the caller needs the answer to continue; asynchronous for everything that can happen afterwards.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Interservice communication (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/design/interservice-communication$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: REST vs gRPC$hs$, $hs$When would you choose gRPC over REST with JSON for calls between services?$hs$, $hs$gRPC: binary Protocol Buffers over HTTP/2, with a contract (`.proto`) from which typed clients and servers are generated, and streaming support. Faster and smaller messages, strict contracts.

REST with JSON: universal, human-readable, easy to debug with any tool, works with HTTP caching, and is natural for public and browser-facing APIs.

gRPC fits internal, high-volume calls between services; REST fits public APIs and simpler internal ones.$hs$, 3, 'SUBMITTED', $hs$gRPC, Introduction to gRPC https://grpc.io/docs/what-is-grpc/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: a long synchronous chain$hs$, $hs$For every payment, the payments service calls the limits service, which calls the customer service, which calls the risk service, all synchronously. Each has 99.9% availability. What problems follow?$hs$, $hs$- Availability multiplies down: all four must be up, so roughly 0.999⁴ ≈ 99.6%, about 35 hours of failure a year instead of 9.
- Latencies add up along the chain, and the slowest call dominates the tail.
- Failures cascade: a slow risk service holds threads in all three callers.
- The services are coupled: none works without the others.

Shorten the chain: call in parallel where possible, keep local copies of the data needed (from events), or move work that doesn't need an immediate answer to asynchronous processing.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Interservice communication (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/design/interservice-communication$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: N+1 calls over HTTP$hs$, $hs$What's the problem with this code, and how do you fix it?

```java
List<StatementLine> lines = transactions.stream()
        .map(t -> new StatementLine(t, merchantClient.getMerchant(t.merchantId())))   // HTTP call
        .toList();
```$hs$, $hs$One HTTP call per transaction: an N+1 problem over the network. A statement with 300 transactions makes 300 calls, so latency and load on the merchant service grow with the list.

- Fetch in one call with a batch endpoint: `merchantClient.getMerchants(distinctIds)`, then look up from a map.
- Cache merchants, which rarely change.
- Or store the merchant name with the transaction when it's booked, so the statement needs no call at all.$hs$, 4, 'SUBMITTED', $hs$microservices.io, Pattern: API Composition https://microservices.io/patterns/data/api-composition.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: a fan-out of seven calls$hs$, $hs$Opening the account overview calls seven services in parallel, and needs all seven answers. Each service succeeds 99.5% of the time, independently. Roughly how often does the overview fail, and what changes that?$hs$, $hs$About 3.5% of the time: the overview succeeds only if all seven succeed, 0.995⁷ ≈ 0.965. Failure rates add up across a fan-out, so a page is much less reliable than any of its parts.

- Make most parts optional: show the overview with a placeholder when a non-essential part (spending insights, offers) fails, so only the essential calls count.
- Give each call a timeout, and a retry where it's idempotent and safe.
- Cache slowly changing parts, so they don't need a live call.$hs$, 6, 'SUBMITTED', $hs$Google SRE Book, Availability Table https://sre.google/sre-book/availability-table/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Gateways: what an API gateway does$hs$, $hs$What does an API gateway do in a microservice system?$hs$, $hs$It's the single entry point for external clients, in front of the services:
- routing requests to the right service
- authentication (validating tokens), TLS termination
- rate limiting and quotas
- sometimes aggregating several services' responses, or translating protocols
- cross-cutting logging and metrics.

Clients don't need to know the internal services and their locations.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, API gateways (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/design/gateway$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Gateways: backends for frontends$hs$, $hs$What is a backend for frontend (BFF), and when is it useful?$hs$, $hs$A separate backend per kind of client (web, mobile, partner API), owned with that client, that shapes and aggregates the underlying services' APIs for it: the mobile BFF returns exactly what the app's account overview needs, in one call.

Useful when clients need very different data or interaction patterns, so one general-purpose API would be a compromise for all of them. For browsers it's also where tokens can be kept server-side.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Backends for Frontends pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/backends-for-frontends$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Gateways: what a service mesh adds$hs$, $hs$What does a service mesh such as Istio add to a microservice system, and what does it cost?$hs$, $hs$A proxy next to each service (a sidecar, or a node-level proxy) handles service-to-service traffic, configured centrally:
- mutual TLS between services, and policies on who may call whom
- retries, timeouts, circuit breaking and traffic shifting (canary releases)
- uniform metrics and traces for all traffic.

All of it without code changes in the services. The costs: another complex system to run and understand, extra latency and resources per call, and harder debugging when the mesh misbehaves.$hs$, 5, 'SUBMITTED', $hs$Istio, What is Istio? https://istio.io/latest/docs/overview/what-is-istio/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Gateways: service discovery$hs$, $hs$What is service discovery, and how does it work on Kubernetes?$hs$, $hs$Finding the network addresses of a service's healthy instances at runtime, since instances come and go with scaling and deploys.

On Kubernetes, a `Service` gives a group of pods one stable name and virtual IP: other services call `http://payments`, cluster DNS resolves it, and traffic is spread over the ready pods. Outside Kubernetes, a registry such as Consul or Eureka plays this role.$hs$, 2, 'SUBMITTED', $hs$Kubernetes documentation, Service https://kubernetes.io/docs/concepts/services-networking/service/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migration: the strangler fig pattern$hs$, $hs$A bank wants to replace its legacy monolith with new services, without a risky big-bang rewrite. What is the strangler fig pattern, and how does it achieve this?$hs$, $hs$The strangler fig pattern replaces a legacy system incrementally, by gradually building new functionality around it until the old system can be retired.

1. A facade, typically a proxy or API gateway, is placed in front of the legacy system, and all requests go through it.
2. Pieces of functionality are rebuilt one at a time in new services, and the facade routes the matching requests to them.
3. The legacy system handles less and less, until nothing is routed to it and it is switched off.

Each step is small and can be released and rolled back on its own, and the system stays in service throughout the migration.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Strangler Fig pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/strangler-fig$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Migration: extracting a service from a shared database$hs$, $hs$You're extracting "cards" from a banking monolith whose code reads the card tables directly in many places. List the steps.$hs$, $hs$1. Inside the monolith, make cards a module: a public API, and replace all direct reads of card tables from other code with calls to that API.
2. Build the cards service with its own database.
3. Migrate and synchronise the data (an initial copy, then change data capture or dual writes from the module) while the monolith still owns it.
4. Route traffic through the facade: reads first, comparing results, then writes.
5. Switch ownership: the service becomes the source of truth; the monolith's module calls the service.
6. Remove the old code and tables.$hs$, 6, 'SUBMITTED', $hs$Azure Architecture Center, Strangler Fig pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/strangler-fig$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: trace context across service boundaries$hs$, $hs$The payments service calls the fraud service over HTTP. How does the fraud service's work end up in the same trace as the payment request?$hs$, $hs$Through context propagation: the payments service sends the trace context, meaning the trace id and the id of its current span, with the request, usually in the W3C `traceparent` HTTP header. The fraud service reads it and starts its spans as children of that span, under the same trace id.

Instrumentation such as the OpenTelemetry agent or Micrometer Tracing adds and reads the header automatically for common HTTP clients and servers.$hs$, 3, 'SUBMITTED', $hs$OpenTelemetry docs, Context propagation (CC BY 4.0) https://opentelemetry.io/docs/concepts/context-propagation/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Communication: evolving an API without lockstep deploys$hs$, $hs$How do you change the API between two services without having to deploy them together?$hs$, $hs$- Only backward-compatible changes in place: add optional fields and new endpoints; never remove, rename or change the meaning of fields that consumers use.
- Tolerant readers: consumers ignore fields they don't know.
- Expand, then contract: add the new form, migrate consumers, then remove the old one in a later release.
- A new version (endpoint or event type) for truly breaking changes, run alongside the old one for a while.
- Consumer-driven contract tests in the provider's pipeline, so a breaking change fails before release.$hs$, 5, 'SUBMITTED', $hs$Pact documentation https://docs.pact.io/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Boundaries: shared code between services$hs$, $hs$Every service copies the same 2,000 lines of "common" code, including the `Account` and `Customer` classes. Is a shared library the fix?$hs$, $hs$Partly:
- Infrastructure code (logging setup, security configuration, tracing, error format) as a shared library is fine: it's generic and versioned.
- Shared domain classes are the problem: every service then depends on one model, a change forces all services to upgrade together, and the bounded contexts disappear.

Let each service keep its own model of `Account` and `Customer` with only what it needs, communicating through APIs and events. A bit of duplicated domain code is cheaper than coupling.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, Microservices https://martinfowler.com/articles/microservices.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Resilience: cascading failures$hs$, $hs$What is a cascading failure?$hs$, $hs$A failure that spreads: one service becomes slow or fails, its callers' threads and connections pile up waiting for it until they can't serve anything either, then their callers do the same, until a large part of the system is down because of one component.$hs$, 1, 'SUBMITTED', $hs$Azure Architecture Center, Circuit Breaker pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Resilience: slow is worse than down$hs$, $hs$Why is a dependency that has become very slow often worse for its callers than one that's completely down?$hs$, $hs$A dependency that's down fails fast: connections are refused at once, and callers can move on or fall back.

A slow one makes every caller wait: each request holds a thread, a connection and memory until its timeout (or forever, without one). The caller's pools fill up, and it stops serving even requests that don't need the slow dependency.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Circuit Breaker pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Timeouts: choosing a timeout$hs$, $hs$How do you choose the timeout for a call from the payments service to the fraud service?$hs$, $hs$- From the fraud service's measured latency: somewhat above its p99 (or p99.9), so normal slow calls succeed but hanging ones are cut off.
- Within your own latency budget: the payment request has its own timeout, and all downstream calls must fit inside it.
- Never infinite (the default in some clients), and always shorter than your caller's timeout.

Revisit it when the dependency's latency changes.$hs$, 3, 'SUBMITTED', $hs$AWS Builders' Library, Timeouts, retries, and backoff with jitter https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Timeouts: a downstream timeout longer than your caller's$hs$, $hs$The mobile app gives up on `POST /payments` after 10 seconds. The payments service calls the card network with a 30-second timeout. What goes wrong?$hs$, $hs$- After 10 seconds nobody is waiting anymore, but the service keeps working on the request for up to 20 more seconds, holding a thread and a connection.
- The payment may still succeed after the app has shown an error, and if the app retries, it can pay twice unless the call is idempotent.

Make each hop's timeout shorter than its caller's (a deadline budget, ideally propagated down the chain), and make payment requests idempotent.$hs$, 5, 'SUBMITTED', $hs$AWS Builders' Library, Timeouts, retries, and backoff with jitter https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Retries: what to retry$hs$, $hs$Which failures of a downstream call should be retried, and which shouldn't?$hs$, $hs$Retry transient failures, which may succeed a moment later:
- timeouts, connection resets, `503 Service Unavailable`, `429` (after the indicated wait)
- deadlocks and optimistic locking conflicts in the database.

Don't retry failures that will happen again in the same way: `400` validation errors, `401`/`403`, `404`, business rejections (insufficient funds). And retry only idempotent operations.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Retry pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/retry$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Retries: retries in Spring$hs$, $hs$With Spring Framework 7, retry the call to the exchange rate provider up to 4 times on `RateProviderUnavailableException`, starting at 100 ms and doubling the delay, with some jitter.$hs$, $hs$```java
@Retryable(includes = RateProviderUnavailableException.class,
           maxRetries = 4, delay = 100, multiplier = 2, jitter = 10, maxDelay = 1000)
public BigDecimal rate(Currency from, Currency to) {
    return rateClient.fetch(from, to);
}
```

Enable it with `@EnableResilientMethods` on a configuration class. Without `includes`, every exception is retried (3 times, 1 second apart, by default). It works through a proxy, so self-invocation isn't retried. Resilience4j's `@Retry` offers the same, plus circuit breakers and bulkheads.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Resilience Features https://docs.spring.io/spring-framework/reference/core/resilience.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Circuit breakers: a slow dependency$hs$, $hs$The booking service calls the leaderboard service on every request. When the leaderboard becomes slow, every booking thread waits up to 30 seconds and the booking service stops responding too. How would a circuit breaker on that call have prevented this? Describe its three states.$hs$, $hs$A circuit breaker stops calling a dependency that keeps failing, so the caller fails fast instead of tying up its threads.

- Closed: calls pass through and failures (errors, timeouts) are counted. When the failure rate crosses a threshold, it opens.
- Open: calls fail immediately without touching the network, and the caller uses a fallback (bookings work without the leaderboard). After a wait time, it moves to half-open.
- Half-open: a few trial calls go through. If they succeed it closes again; if not, it opens again.

A much shorter timeout on the call is the first fix either way. In Java, Resilience4j provides circuit breakers.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Circuit Breaker pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Circuit breakers: configuring Resilience4j$hs$, $hs$Which settings do you configure on a Resilience4j circuit breaker, and how do you choose them?$hs$, $hs$- Failure rate threshold (e.g. 50%) and slow call rate threshold, with the duration that counts as slow.
- The sliding window: count-based (last 100 calls) or time-based (last 60 seconds), and the minimum number of calls before the rates are evaluated.
- Wait duration in the open state before trying again.
- Number of calls permitted in the half-open state.
- Which exceptions count as failures (record) and which don't (ignore).

Choose them from the dependency's normal error rate and latency, so normal noise doesn't open the circuit, and test them by simulating failures.$hs$, 4, 'SUBMITTED', $hs$Resilience4j documentation, CircuitBreaker https://resilience4j.readme.io/docs/circuitbreaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Circuit breakers: what happens while it's open$hs$, $hs$What should the caller do while a circuit breaker is open? Give examples of good and bad fallbacks in a banking app.$hs$, $hs$Either a fallback that's genuinely acceptable, or a fast, clear error.

Good:
- spending insights unavailable: hide the section, the rest of the app works
- exchange rates: show the last cached rate, marked with its time
- a non-urgent action: accept it and queue it for later.

Bad: any fallback that fakes a result for money, e.g. assuming the fraud check passed, or showing a balance of 0. Then fail clearly instead.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Circuit Breaker pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Bulkheads: isolating resources$hs$, $hs$What is the bulkhead pattern?$hs$, $hs$Isolating resources per dependency or per kind of work, like the watertight compartments of a ship: separate thread pools, concurrency limits or connection pools. When one dependency becomes slow, it can exhaust only its own compartment, and the rest of the service keeps working.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Bulkhead pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/bulkhead$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Overload: rate-limiting algorithms$hs$, $hs$Compare the token bucket, fixed window and sliding window rate-limiting algorithms.$hs$, $hs$- Token bucket: a bucket holds up to N tokens and refills at a steady rate; each request takes one. Allows bursts up to the bucket size while enforcing the average rate.
- Fixed window: count requests per interval (per minute) and reject above the limit. Simple, but allows up to twice the limit around a window boundary.
- Sliding window: counts over the last N seconds (exactly from a log, or approximated from two windows), smoothing out the boundary problem.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Rate Limiting pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/rate-limiting-pattern$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Overload: what to return when rate-limited$hs$, $hs$What should an API return to a client that has exceeded its rate limit?$hs$, $hs$`429 Too Many Requests`, with a `Retry-After` header saying how long to wait, and a body explaining the limit. Many APIs also send headers with the limit, the remaining requests and the reset time on every response, so clients can slow down before they hit it.$hs$, 2, 'SUBMITTED', $hs$RFC 6585 Additional HTTP Status Codes, §4 429 Too Many Requests https://www.rfc-editor.org/rfc/rfc6585$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Overload: backpressure and load shedding$hs$, $hs$What are backpressure and load shedding?$hs$, $hs$- Backpressure: a consumer that can't keep up makes its producers slow down, e.g. through bounded queues that block or reject when full, instead of buffering without limit until memory runs out.
- Load shedding: an overloaded service deliberately rejects some requests early and cheaply (e.g. `503`), lowest priority first, so it can keep serving the rest within its latency targets, instead of becoming slow for everyone.$hs$, 4, 'SUBMITTED', $hs$AWS Builders' Library, Using load shedding to avoid overload https://aws.amazon.com/builders-library/using-load-shedding-to-avoid-overload/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Overload: graceful degradation$hs$, $hs$The spending-insights service of a banking app is down. Describe graceful degradation for the app's home screen.$hs$, $hs$The core keeps working: balances, transactions and payments load and work normally, since they never wait on the insights service (short timeout, circuit breaker).

The insights section is hidden, or shows the last cached insights with their date, or a short "temporarily unavailable" note. Optional features must never block the core flow.$hs$, 5, 'SUBMITTED', $hs$AWS Builders' Library, Using load shedding to avoid overload https://aws.amazon.com/builders-library/using-load-shedding-to-avoid-overload/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: the five principles$hs$, $hs$What does each letter of SOLID stand for? Give a one-line meaning for each.$hs$, $hs$- S, single responsibility: a class should have one reason to change.
- O, open/closed: open for extension, closed for modification; add behaviour by adding code, not editing it.
- L, Liskov substitution: a subtype must be usable wherever its base type is expected, without surprises.
- I, interface segregation: clients shouldn't depend on methods they don't use; prefer small interfaces.
- D, dependency inversion: high-level policy depends on abstractions, not on low-level details.$hs$, 1, 'SUBMITTED', $hs$Wikipedia, SOLID (CC BY-SA 4.0) https://en.wikipedia.org/wiki/SOLID$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: single responsibility, beyond "does one thing"$hs$, $hs$A `StatementService` calculates the interest on an account and also renders the monthly statement as a PDF. Which principle does it break, and why does it matter?$hs$, $hs$Single responsibility: a module should have one reason to change, i.e. serve one group of people or one concern. Interest rules change when the bank's products change; the PDF layout changes when marketing redesigns it.

In one class, a layout change risks breaking interest calculation, both are tested and deployed together, and two teams edit the same file. Split it into an interest calculator and a statement renderer.$hs$, 2, 'SUBMITTED', $hs$Robert C. Martin, The Single Responsibility Principle https://blog.cleancoder.com/uncle-bob/2014/05/08/SingleReponsibilityPrinciple.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: replacing a switch with strategies$hs$, $hs$Every new payment type means editing this method. Apply the open/closed principle.

```java
public Fee fee(Payment payment) {
    switch (payment.type()) {
        case SEPA: return Fee.of("0.00");
        case INSTANT: return Fee.of("0.50");
        case SWIFT: return Fee.of("15.00").plus(payment.amount().percent("0.1"));
        default: throw new IllegalArgumentException(payment.type().toString());
    }
}
```$hs$, $hs$A strategy per payment type, behind one interface:

```java
public interface FeePolicy {
    PaymentType type();
    Fee fee(Payment payment);
}

@Component
class SwiftFeePolicy implements FeePolicy {
    public PaymentType type() { return PaymentType.SWIFT; }
    public Fee fee(Payment payment) { return Fee.of("15.00").plus(payment.amount().percent("0.1")); }
}

@Service
class FeeCalculator {
    private final Map<PaymentType, FeePolicy> policies;

    FeeCalculator(List<FeePolicy> policies) {
        this.policies = policies.stream().collect(toMap(FeePolicy::type, p -> p));
    }

    public Fee fee(Payment payment) {
        return policies.get(payment.type()).fee(payment);
    }
}
```

A new payment type is a new class; `FeeCalculator` doesn't change. Spring injects all `FeePolicy` beans.$hs$, 3, 'SUBMITTED', $hs$Refactoring.Guru, Strategy https://refactoring.guru/design-patterns/strategy$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: why a square isn't a rectangle$hs$, $hs$Which principle does `Square extends Rectangle` violate, and how does this code show it?

```java
void stretch(Rectangle r) {
    r.setWidth(10);
    r.setHeight(5);
    assert r.area() == 50;
}
```$hs$, $hs$The Liskov substitution principle. `Rectangle`'s contract says setting the height leaves the width unchanged. A `Square` must keep both sides equal, so `setHeight(5)` also changes the width, and `stretch(new Square())` ends with an area of 25.

A subtype must honour its base type's behaviour, not just its method signatures. Here, `Square` shouldn't extend a mutable `Rectangle`; both can implement a common `Shape` with `area()`, or be immutable.$hs$, 4, 'SUBMITTED', $hs$Wikipedia, Liskov substitution principle (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Liskov_substitution_principle$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: dependency inversion$hs$, $hs$What is the dependency inversion principle? Apply it to a `TransferService` that sends notifications by email through an SMTP client.$hs$, $hs$High-level policy shouldn't depend on low-level details; both depend on an abstraction, owned by the high-level side.

Instead of `TransferService` using the SMTP client directly, the transfer module defines what it needs:

```java
public interface CustomerNotifier {
    void transferCompleted(Transfer transfer);
}
```

An `EmailCustomerNotifier` in the infrastructure layer implements it with SMTP. `TransferService` depends only on `CustomerNotifier`, so the channel can change (SMS, push) or be faked in tests without touching it. Spring's dependency injection wires in the implementation.$hs$, 3, 'SUBMITTED', $hs$Robert C. Martin, The Clean Architecture https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$SOLID: an interface with 25 methods$hs$, $hs$An `AccountRepository` interface has 25 methods, but most of its 15 callers use one or two. What principle is violated, and what do you do?$hs$, $hs$Interface segregation: clients shouldn't depend on methods they don't use. Every caller is coupled to all 25 methods: changes ripple, and test doubles must deal with methods nobody calls.

Split it into small, client-focused interfaces, e.g. `AccountLookup` (`findById`, `findByIban`) for most callers and `AccountAdministration` for the few that manage accounts. One class can implement several of them.$hs$, 4, 'SUBMITTED', $hs$Wikipedia, SOLID (CC BY-SA 4.0) https://en.wikipedia.org/wiki/SOLID$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: what a design pattern is$hs$, $hs$What is a design pattern?$hs$, $hs$A named, reusable solution to a problem that recurs in software design, described with the context where it applies and its trade-offs. Not code to copy, but a template to adapt.

The names are a shared vocabulary: "put a decorator around the client" says a lot in a few words.$hs$, 1, 'SUBMITTED', $hs$Refactoring.Guru, Design Patterns https://refactoring.guru/design-patterns$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: strategy vs template method$hs$, $hs$Both strategy and template method let part of an algorithm vary. What's the difference?$hs$, $hs$- Strategy: the varying part is a separate object passed in (composition). It can be swapped at runtime, and combined freely.
- Template method: a base class defines the algorithm's steps, and subclasses override some of them (inheritance). The variation is fixed at compile time, and the subclass is tied to its base class.

Strategy is usually preferred: composition over inheritance.$hs$, 2, 'SUBMITTED', $hs$Refactoring.Guru, Template Method https://refactoring.guru/design-patterns/template-method$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: where Spring uses them$hs$, $hs$Name where Spring uses each of these patterns: proxy, template method, factory, observer, adapter, chain of responsibility.$hs$, $hs$- Proxy: AOP proxies for `@Transactional`, `@Cacheable`, `@Async`.
- Template method: `JdbcTemplate`, `TransactionTemplate` (fixed steps around your callback).
- Factory: `BeanFactory` and `FactoryBean`.
- Observer: application events with `@EventListener`.
- Adapter: `HandlerAdapter`, letting the `DispatcherServlet` call different kinds of handlers.
- Chain of responsibility: servlet filters and Spring Security's filter chain.$hs$, 3, 'SUBMITTED', $hs$Refactoring.Guru, Design Patterns https://refactoring.guru/design-patterns$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: decorator vs proxy$hs$, $hs$Decorators and proxies both wrap an object behind the same interface. What's the difference?$hs$, $hs$- Decorator: adds behaviour to the object (logging, caching, metrics), and several can be stacked; the client usually builds the chain.
- Proxy: controls access to the object (lazy loading, security checks, a remote call, transactions), usually without the client knowing it isn't talking to the real object.

The structure is the same; the intent differs.$hs$, 3, 'SUBMITTED', $hs$Refactoring.Guru, Proxy https://refactoring.guru/design-patterns/proxy$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: adding caching without changing a class$hs$, $hs$Add caching around this exchange rate provider without changing `HttpExchangeRates`.

```java
public interface ExchangeRates {
    BigDecimal rate(Currency from, Currency to);
}

class HttpExchangeRates implements ExchangeRates { ... }
```$hs$, $hs$A decorator that implements the same interface and delegates to the real one:

```java
class CachingExchangeRates implements ExchangeRates {

    private final ExchangeRates delegate;
    private final Cache<String, BigDecimal> cache;

    CachingExchangeRates(ExchangeRates delegate, Cache<String, BigDecimal> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public BigDecimal rate(Currency from, Currency to) {
        return cache.get(from + "-" + to, key -> delegate.rate(from, to));
    }
}
```

Callers keep depending on `ExchangeRates`; the configuration wires `new CachingExchangeRates(new HttpExchangeRates(...), cache)`. A metrics decorator can wrap it in turn.$hs$, 4, 'SUBMITTED', $hs$Refactoring.Guru, Decorator https://refactoring.guru/design-patterns/decorator$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: factory method, abstract factory, builder$hs$, $hs$What is the difference between a factory method, an abstract factory and a builder?$hs$, $hs$- Factory method: a method that decides which concrete object to create and returns it behind an interface, e.g. `PaymentProcessor.forType(type)`.
- Abstract factory: an object that creates a family of related objects that belong together, e.g. one factory per payment network creating its client, its message parser and its validator.
- Builder: constructs one complex object step by step, with readable optional parts, e.g. `Transfer.builder().from(a).to(b).amount(x).reference("rent").build()`.$hs$, 3, 'SUBMITTED', $hs$Refactoring.Guru, Builder https://refactoring.guru/design-patterns/builder$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Patterns: integrating three payment providers$hs$, $hs$Card payments go through one of three providers with completely different APIs. How do you keep the rest of the code independent of them?$hs$, $hs$Define one interface in your own terms, and write an adapter per provider:

- `CardPaymentGateway` with `charge(ChargeRequest)` returning your own `ChargeResult`.
- `StripeGateway`, `AdyenGateway`… each implement it, translating your models to the provider's API and its responses and errors back.
- A strategy or factory picks the adapter (by merchant, country, or configuration).

The domain never sees a provider's classes, and adding or replacing a provider touches one adapter.$hs$, 4, 'SUBMITTED', $hs$Refactoring.Guru, Adapter https://refactoring.guru/design-patterns/adapter$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Clean code: coupling and cohesion$hs$, $hs$What are coupling and cohesion, and which do you want high or low?$hs$, $hs$- Coupling: how much one module depends on others' internals. Keep it low, so a change in one module doesn't ripple through others.
- Cohesion: how closely the parts of one module belong together. Keep it high, so each module has a clear purpose and related changes happen in one place.$hs$, 1, 'SUBMITTED', $hs$Wikipedia, Coupling (computer programming) (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Coupling_%28computer_programming%29$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Refactoring: code smells$hs$, $hs$Name five code smells and what each points to.$hs$, $hs$- Long method: does several things; extract smaller methods.
- Large class: too many responsibilities.
- Duplicated code: the same logic must be changed in several places.
- Feature envy: a method uses another object's data more than its own; it belongs there.
- Primitive obsession: plain strings and numbers instead of small types (`Iban`, `Money`).
- Shotgun surgery: one change requires edits in many classes.
- Long parameter list: a missing object.$hs$, 2, 'SUBMITTED', $hs$Refactoring.Guru, Code Smells https://refactoring.guru/refactoring/smells$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Refactoring: what it is and what makes it safe$hs$, $hs$What is refactoring, and what makes it safe?$hs$, $hs$Changing the internal structure of code to make it easier to understand and change, without changing its observable behaviour.

Safe when done in small steps (each one keeps everything working), with tests that pin the behaviour, run after every step, and kept separate from feature changes, so a broken test points at the last small step.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, Definition Of Refactoring https://martinfowler.com/bliki/DefinitionOfRefactoring.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Clean code: tell, don't ask$hs$, $hs$Rewrite this service code following "tell, don't ask".

```java
if (account.getStatus() == AccountStatus.ACTIVE
        && account.getBalance().compareTo(amount) >= 0) {
    account.setBalance(account.getBalance().subtract(amount));
} else {
    throw new WithdrawalRejectedException();
}
```$hs$, $hs$Tell the account what to do, and let it protect its own rules:

```java
account.withdraw(amount);
```

```java
public void withdraw(BigDecimal amount) {
    if (status != AccountStatus.ACTIVE || balance.compareTo(amount) < 0) {
        throw new WithdrawalRejectedException();
    }
    balance = balance.subtract(amount);
}
```

The rule lives in one place with the data, `setBalance` can disappear, and no caller can forget the check.$hs$, 4, 'SUBMITTED', $hs$Martin Fowler, TellDontAsk https://martinfowler.com/bliki/TellDontAsk.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Clean code: the anemic domain model$hs$, $hs$What is an anemic domain model, and what's wrong with it?$hs$, $hs$Domain objects that only hold data (fields, getters and setters), while all the behaviour lives in services that read and set those fields.

- The business rules are scattered over services, and duplicated.
- Nothing protects the invariants: any code can set any field (a negative balance, a closed account receiving transfers).
- It looks object-oriented but is procedural code with data structures.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, AnemicDomainModel https://martinfowler.com/bliki/AnemicDomainModel.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Clean code: the god service$hs$, $hs$What is a god class, and how do you recognise one in a Spring service?$hs$, $hs$A god class is a class that knows too much or does too much: it concentrates a large share of the application's logic and responsibilities, violating the single responsibility principle. It is an extreme form of the Large Class code smell.

Signs of one in a Spring service:
- thousands of lines, and a constructor with ten or more dependencies;
- methods for unrelated use cases (accounts, cards, statements) in one class;
- almost every change, and many merge conflicts, touch it.

The remedy is to split it by use case or domain concept, and to move behaviour into the domain objects it belongs to.$hs$, 3, 'SUBMITTED', $hs$Refactoring.Guru, Code Smells (Large Class) https://refactoring.guru/refactoring/smells$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Refactoring: a 4,000-line service without tests$hs$, $hs$A 4,000-line `TransferService` has grown over five years, has almost no tests, and every change breaks something. Before restructuring any of it, what do you write first, and what does it check?$hs$, $hs$Characterization tests: tests that record what the code does today, not what it should do. Call it with a range of realistic inputs, capture the outputs and side effects (results, saved rows, published events), and assert exactly those, quirks included.

They make every later refactoring safe: a red test means behaviour changed, whether intended or not. Then restructure in small, test-protected steps (extract method, extract class), alongside feature work rather than in one big rewrite.$hs$, 6, 'SUBMITTED', $hs$Martin Fowler, Refactoring (Building Tests) https://refactoring.com/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: checked or unchecked for insufficient funds$hs$, $hs$`account.withdraw(amount)` must signal "insufficient funds" to its callers in a Spring service. Should `InsufficientFundsException` be checked or unchecked, and how should the REST layer turn it into a response?$hs$, $hs$Unchecked, extending a common domain base class (e.g. `DomainException extends RuntimeException`). Most callers can't do anything about it except let it pass up, so a checked exception would only add `throws` clauses through every layer, or tempt people to catch and swallow it. Unchecked exceptions also roll back Spring transactions by default; checked ones don't.

The REST layer maps it in one place, a `@RestControllerAdvice`, to `422` (or `409`) with a Problem Details body. Business code never deals with HTTP.$hs$, 5, 'SUBMITTED', $hs$Java Tutorials, Unchecked Exceptions: The Controversy https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: layered vs hexagonal$hs$, $hs$What is the difference between a layered architecture and a hexagonal (ports and adapters) architecture?$hs$, $hs$- Layered: controllers → services → repositories, each layer calling the one below. The business logic often ends up depending on the persistence layer and its technology.
- Hexagonal: the domain and application logic sit in the centre and define ports, interfaces for what they offer and what they need (e.g. `LoadAccount`). Adapters outside implement or call them: a REST controller, a JPA repository, a Kafka publisher. The core depends on nothing technical.$hs$, 2, 'SUBMITTED', $hs$Alistair Cockburn, Hexagonal Architecture https://alistair.cockburn.us/hexagonal-architecture/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: the dependency rule$hs$, $hs$What is the dependency rule of clean architecture?$hs$, $hs$Source code dependencies point only inward, towards the business rules. Inner circles (entities, use cases) know nothing about outer ones (controllers, databases, frameworks, the web).

So the domain has no imports of Spring MVC, JPA or Kafka classes; when it needs something from outside, it defines an interface that an outer layer implements.$hs$, 3, 'SUBMITTED', $hs$Robert C. Martin, The Clean Architecture https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: DTOs, domain objects and JPA entities$hs$, $hs$In a hexagonal Spring application, where do the request DTO, the domain `Account` and the JPA entity live, and who maps between them?$hs$, $hs$- Request and response DTOs: in the web adapter, which maps them to and from commands and domain objects.
- The domain `Account`: in the core, with the business rules, free of framework annotations.
- The JPA entity: in the persistence adapter, which maps it to and from the domain `Account` when implementing the port.

Each adapter owns its mapping. A common pragmatic shortcut is to use the JPA entity as the domain object, accepting the persistence annotations in the core.$hs$, 4, 'SUBMITTED', $hs$Alistair Cockburn, Hexagonal Architecture https://alistair.cockburn.us/hexagonal-architecture/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: where transactions, validation and security go$hs$, $hs$In a layered Spring service, where should the transaction boundary, input validation, business rules and authorization checks live?$hs$, $hs$- Transactions: on the application service methods, one per use case (`@Transactional` on `TransferService.transfer`), not in controllers or repositories.
- Input validation (format, required fields): at the edge, on the request DTOs (`@Valid`).
- Business rules and invariants (sufficient funds, limits): in the domain objects, enforced whatever the caller.
- Authorization: coarse checks at the edge (authenticated, scopes), object-level checks (is this the customer's account?) in the application service or the query.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Using tactical DDD to design microservices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/tactical-ddd$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: packages by layer vs by feature$hs$, $hs$Compare packaging a Spring application by layer with packaging it by feature.$hs$, $hs$- By layer: `controller`, `service`, `repository` packages, each holding classes of every feature. Everything must be public to be used across packages, and one feature's code is spread over all of them.
- By feature: `accounts`, `transfers`, `cards`, each holding its controller, service and repository. Related code changes together in one place, and internals can be package-private, so other features can only use the public API.

By feature scales better as the application grows.$hs$, 2, 'SUBMITTED', $hs$Spring Modulith reference https://docs.spring.io/spring-modulith/reference/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API design: what a semantic version bump promises$hs$, $hs$Your team publishes a Java client library used by other teams, versioned as MAJOR.MINOR.PATCH. What does each kind of bump promise its users, and which one does removing a public method need?$hs$, $hs$- PATCH (2.4.1 → 2.4.2): backward-compatible bug fixes only.
- MINOR (2.4.2 → 2.5.0): new, backward-compatible functionality; existing code keeps compiling and working.
- MAJOR (2.5.0 → 3.0.0): incompatible changes; users may have to change their code.

Removing a public method breaks code that calls it, so it needs a MAJOR version, ideally after deprecating the method in an earlier MINOR release.$hs$, 3, 'SUBMITTED', $hs$Semantic Versioning 2.0.0 https://semver.org/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: entities vs value objects$hs$, $hs$What is the difference between an entity and a value object? Give two banking examples of each.$hs$, $hs$- Entity: has an identity that stays the same while its attributes change. Two entities with the same data are still different. An account, a customer.
- Value object: defined only by its values, with no identity, and immutable; two with the same values are equal. Money (amount and currency), an IBAN, an address.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, ValueObject https://martinfowler.com/bliki/ValueObject.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: aggregates and their root$hs$, $hs$What is an aggregate in domain-driven design, and what is its root?$hs$, $hs$A cluster of objects treated as one unit for changes, within which the invariants are always kept, e.g. an `Account` with its standing orders.

The root (`Account`) is the only object that outside code references and calls; all changes go through it, so it can enforce the rules ("total standing orders can't exceed the limit"). An aggregate is loaded and saved as a whole, in one transaction.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, DDD_Aggregate https://martinfowler.com/bliki/DDD_Aggregate.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: referencing another aggregate$hs$, $hs$Should the `Account` aggregate hold a reference to the `Customer` entity, or only the customer's id? Why?$hs$, $hs$Only the id (`CustomerId`).

- Each aggregate is a consistency boundary, changed in its own transaction; an object reference invites changing the customer through the account.
- Loading an account doesn't drag in the customer and everything the customer references.
- The aggregates can later live in different modules or services, where object references are impossible anyway.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Using tactical DDD to design microservices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/tactical-ddd$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: domain events$hs$, $hs$What is a domain event? How can a Spring Data aggregate publish one?$hs$, $hs$An object describing something meaningful that happened in the domain, named in the past tense: `AccountOverdrawn`, `TransferCompleted`. Other parts of the system react to it without the aggregate knowing them.

In Spring Data, the aggregate collects events (extending `AbstractAggregateRoot` and calling `registerEvent(...)`, or with a `@DomainEvents` method), and the repository publishes them as application events when the aggregate is saved through it. Listeners handle them, e.g. with `@TransactionalEventListener` after the commit.$hs$, 4, 'SUBMITTED', $hs$Spring Data Commons, Publishing Events from Aggregate Roots https://docs.spring.io/spring-data/commons/reference/repositories/core-domain-events.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: domain services$hs$, $hs$What is a domain service, and when do you need one? Give a banking example.$hs$, $hs$Stateless domain logic that doesn't naturally belong to a single entity or value object, expressed in the domain's language.

E.g. a transfer between two accounts: it involves both, and putting it in either `Account` would be arbitrary. A `TransferService` in the domain withdraws from one and deposits to the other, each account enforcing its own rules.

Unlike an application service, it holds business logic, not orchestration of transactions, security or persistence.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Using tactical DDD to design microservices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/microservices/model/tactical-ddd$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: how big an aggregate should be$hs$, $hs$A team models `Customer` as an aggregate containing all their accounts, and each account all its transactions. What problems will they hit, and what's the rule of thumb?$hs$, $hs$- Every change locks or version-checks the whole customer: two payments from different accounts conflict.
- Loading a customer loads years of transactions.
- Transactions grow large and slow.

Rule of thumb: make aggregates as small as the invariants that must hold immediately allow, and change one aggregate per transaction. Here: `Customer`, `Account` and `Transaction` (or ledger entry) as separate aggregates, referencing each other by id; rules across them are kept by domain services or eventual consistency.$hs$, 6, 'SUBMITTED', $hs$Martin Fowler, DDD_Aggregate https://martinfowler.com/bliki/DDD_Aggregate.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: a decline undone by removing and re-adding a tag$hs$, $hs$Exercises are tagged with muscles. Each tag link has a status, PENDING, ACCEPTED or DECLINED, decided by the muscle's owner. When an author edits an exercise's tags, the repository runs the SQL below, and the test "editing an exercise never re-opens a decision" passes. Can an author still get their exercise back onto a muscle whose owner declined it?

```sql
DELETE FROM exercise_muscle WHERE exercise_id = :id AND muscle NOT IN (:newMuscles);

INSERT INTO exercise_muscle (exercise_id, muscle, status)
VALUES (:id, :muscle, :initialStatus)        -- ACCEPTED on muscles open to everyone
ON CONFLICT (exercise_id, muscle) DO NOTHING; -- existing links keep their decision
```$hs$, $hs$Yes, in two edits. The first edit removes the muscle, which deletes the link row and, with it, the DECLINED decision. The second edit adds the muscle back, which inserts a fresh link with its initial status, ACCEPTED on an open muscle. `ON CONFLICT DO NOTHING` protects a decision only within a single edit.

The decision is state that belongs to the relationship, so it must outlive the author's edits:
- never delete decided links: keep DECLINED rows, and leave them out of the exercise's visible tags;
- or record declines separately, and check them when a link is created.

The general lesson: "replace the whole association" strategies (delete and re-insert, or JPA's `orphanRemoval` with a replaced collection) destroy any state stored on the association.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, DDD_Aggregate https://martinfowler.com/bliki/DDD_Aggregate.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$DDD: snapshotting an entity that keeps changing$hs$, $hs$Athletes answer exercises that coaches keep editing, and each athlete's history must show every answer next to the exercise exactly as it was answered. A developer stores `objectMapper.writeValueAsString(exercise)` in an `answer.exercise_snapshot` JSONB column. What goes wrong a year and several refactorings later, and what's better?$hs$, $hs$Storing a snapshot is the right idea: a plain `exercise_id` reference alone would show today's version of an edited exercise, rewriting history. The problem is **serializing the domain class directly**, which couples the stored JSON to the class's current shape: renaming, removing or retyping a field later makes old rows fail to deserialize, or silently lose data.

Better options:
- Write the snapshot through a dedicated, versioned format (an explicit field-by-field map, or a snapshot DTO with a `version` field) and read it tolerantly.
- Or store immutable versions of the exercise in their own table (`exercise_version`), referenced by the answer. This avoids duplicating the text in every answer, at the cost of versioning every edit.$hs$, 6, 'SUBMITTED', $hs$Martin Fowler, DDD_Aggregate https://martinfowler.com/bliki/DDD_Aggregate.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testability: what makes code hard to test$hs$, $hs$What design choices make code hard to unit-test?$hs$, $hs$- Hidden dependencies: `new` of collaborators inside methods, static calls, global singletons.
- Direct use of time and randomness (`LocalDate.now()`, `Math.random()`).
- Business logic mixed with I/O (database, HTTP, files) in the same method.
- Large classes with many dependencies and responsibilities.
- Deep inheritance hierarchies where behaviour comes from base classes.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, The Practical Test Pyramid https://martinfowler.com/articles/practical-test-pyramid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testability: separating the rule from the I/O$hs$, $hs$Refactor this method so the loyalty discount rule can be unit-tested without HTTP or a clock.

```java
public BigDecimal membershipPrice(long athleteId) {
    AthleteProfile profile = profileClient.fetch(athleteId);               // HTTP call
    long years = ChronoUnit.YEARS.between(profile.memberSince(), LocalDate.now());
    BigDecimal discount = years >= 5 ? new BigDecimal("0.20")
            : years >= 2 ? new BigDecimal("0.10") : BigDecimal.ZERO;
    return BASE_PRICE.subtract(BASE_PRICE.multiply(discount));
}
```$hs$, $hs$Extract the rule into a pure function of its inputs, and keep the I/O and the clock in the orchestrating method:

```java
static BigDecimal priceFor(LocalDate memberSince, LocalDate today) {
    long years = ChronoUnit.YEARS.between(memberSince, today);
    BigDecimal discount = years >= 5 ? new BigDecimal("0.20")
            : years >= 2 ? new BigDecimal("0.10") : BigDecimal.ZERO;
    return BASE_PRICE.subtract(BASE_PRICE.multiply(discount));
}

public BigDecimal membershipPrice(long athleteId) {
    AthleteProfile profile = profileClient.fetch(athleteId);
    return priceFor(profile.memberSince(), LocalDate.now(clock));   // injected Clock
}
```

The rule is now tested with plain dates at every boundary; only the thin orchestration needs a stubbed client.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, The Practical Test Pyramid https://martinfowler.com/articles/practical-test-pyramid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['software-design', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: the three annotations of @SpringBootApplication$hs$, $hs$Which three annotations does `@SpringBootApplication` combine?$hs$, $hs$1. `@SpringBootConfiguration`: the class is a configuration class (a `@Configuration`).
2. `@EnableAutoConfiguration`: switches on auto-configuration.
3. `@ComponentScan`: scans the class's package, and every package below it, for components.$hs$, 1, 'SUBMITTED', $hs$Spring Boot docs, Using the @SpringBootApplication Annotation https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: what it is$hs$, $hs$What is Spring Boot's auto-configuration?$hs$, $hs$Configuration classes shipped with Spring Boot that register beans for you, a `DataSource`, Jackson's `ObjectMapper`, an embedded web server, when their conditions hold: e.g. the library is on the classpath, the needed properties are set, and you haven't defined such a bean yourself.$hs$, 1, 'SUBMITTED', $hs$Spring Boot docs, Auto-configuration https://docs.spring.io/spring-boot/reference/using/auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: how Boot finds the classes$hs$, $hs$How does Spring Boot find the auto-configuration classes to evaluate?$hs$, $hs$`@EnableAutoConfiguration` loads the class names listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in every jar on the classpath, then evaluates each class's conditions. (Before Spring Boot 2.7 they were listed in `spring.factories`.)

Auto-configuration classes are not found by component scanning.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Creating Your Own Auto-configuration https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: replacing an auto-configured bean$hs$, $hs$Spring Boot auto-configures a `DataSource`. You declare your own `DataSource` bean. Why does Boot then not create its own?$hs$, $hs$Auto-configuration is conditional. The auto-configured `DataSource` is guarded by `@ConditionalOnMissingBean`, so it's only created when no bean of that type exists yet. Your bean exists, so the auto-configuration backs off.

Starting the application with `--debug` prints which conditions matched and which didn't.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Auto-configuration https://docs.spring.io/spring-boot/reference/using/auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: switching one off$hs$, $hs$How do you switch off one auto-configuration, e.g. `DataSourceAutoConfiguration`?$hs$, $hs$```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
```

or with a property, which needs no code change:

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Auto-configuration (disabling specific auto-configuration classes) https://docs.spring.io/spring-boot/reference/using/auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: the common conditions$hs$, $hs$What does each of these conditions check: `@ConditionalOnClass`, `@ConditionalOnBean`, `@ConditionalOnMissingBean` and `@ConditionalOnProperty`?$hs$, $hs$- `@ConditionalOnClass`: a class is on the classpath (the library is present).
- `@ConditionalOnBean`: a bean of a type or name already exists.
- `@ConditionalOnMissingBean`: no such bean exists yet, so the user hasn't defined their own.
- `@ConditionalOnProperty`: a property is set, optionally to a specific value (e.g. a feature switch).$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Creating Your Own Auto-configuration (condition annotations) https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: "Failed to configure a DataSource"$hs$, $hs$After adding `spring-boot-starter-data-jpa`, a service that has no database yet fails at startup with "Failed to configure a DataSource: 'url' attribute is not specified". Why, and what are the fixes?$hs$, $hs$The starter put JPA and a connection pool on the classpath, so the `DataSource` auto-configuration activated, and it needs a database URL to create the pool.

Fixes: configure the database (`spring.datasource.url` and credentials); add an embedded database (H2) for local work; or, if the dependency isn't needed yet, remove it or exclude the auto-configuration.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Auto-configuration https://docs.spring.io/spring-boot/reference/using/auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Dependencies: what a starter is$hs$, $hs$What is a Spring Boot starter?$hs$, $hs$A single dependency that brings in everything needed for one purpose, in versions known to work together. For example, `spring-boot-starter-web` brings in Spring MVC, Jackson and embedded Tomcat.

Spring Boot's dependency management sets the versions, so you don't specify them yourself.$hs$, 1, 'SUBMITTED', $hs$Spring Boot docs, Build Systems (starters) https://docs.spring.io/spring-boot/reference/using/build-systems.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Dependencies: why no versions$hs$, $hs$Why can a Spring Boot project declare most dependencies without a version?$hs$, $hs$Spring Boot's dependency management, the `spring-boot-dependencies` BOM, imported through `spring-boot-starter-parent` or the Gradle plugin, fixes a version for each library it supports. The versions are tested together for that Boot release, so upgrading Boot upgrades them consistently.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Build Systems (dependency management) https://docs.spring.io/spring-boot/reference/using/build-systems.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Dependencies: mixing versions$hs$, $hs$What can go wrong when a project forces a newer Spring Framework or Hibernate version than its Spring Boot version manages?$hs$, $hs$Spring Boot manages a curated set of dependency versions, and its auto-configuration is compiled and tested against exactly those versions. Overriding one breaks that guarantee: the auto-configuration may call classes or methods that changed, which fails at startup or at runtime (`NoSuchMethodError`, `ClassNotFoundException`), or the application may behave subtly differently.

The supported way to get newer libraries is to upgrade Spring Boot itself.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Build Systems (dependency management) https://docs.spring.io/spring-boot/reference/using/build-systems.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: where it comes from$hs$, $hs$Name the main places a Spring Boot application reads its configuration from.$hs$, $hs$- `application.properties` or `application.yml`, inside the jar and next to it (e.g. in a `config/` directory)
- profile-specific files, `application-{profile}.yml`
- operating system environment variables
- Java system properties (`-Dkey=value`)
- command-line arguments (`--key=value`)

All are combined into the `Environment`, with a defined precedence.$hs$, 1, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: property precedence$hs$, $hs$`application.properties` contains `server.port=8080`, and the application is started with the environment variable `SERVER_PORT=9090`. Which port does it listen on?$hs$, $hs$9090. OS environment variables take precedence over `application.properties`, and relaxed binding maps `SERVER_PORT` to `server.port`.

A command-line argument (`--server.port=7070`) would win over both.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: @Value vs @ConfigurationProperties$hs$, $hs$When do you use `@Value`, and when `@ConfigurationProperties`?$hs$, $hs$- `@Value("${...}")` injects a single value into one field or parameter, and supports SpEL expressions.
- `@ConfigurationProperties("prefix")` binds a whole group of properties into a typed object, with relaxed binding, validation, and IDE metadata (auto-completion).

Prefer `@ConfigurationProperties` for anything beyond one or two values: the settings live in one class, typed and validated.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration (@ConfigurationProperties vs @Value) https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: @ConfigurationProperties$hs$, $hs$What does `@ConfigurationProperties(prefix = "gym")` on this class do?

```java
@ConfigurationProperties(prefix = "gym")
public class GymProperties {
    private int maxAthletes;
    // getter and setter
}
```$hs$, $hs$It binds every property starting with `gym.` to the matching field, type-converted: `gym.max-athletes=40` sets `maxAthletes` to 40 (relaxed binding also accepts `gym.maxAthletes` or `GYM_MAXATHLETES`).

The class must be registered, with `@EnableConfigurationProperties(GymProperties.class)` or `@ConfigurationPropertiesScan`. Add `@Validated` to check the values at startup.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration (type-safe configuration properties) https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: validated properties$hs$, $hs$Bind `payments.timeout=5s` and `payments.retries=3` into a typed object, so that a negative number of retries stops the application at startup.$hs$, $hs$Use a `@ConfigurationProperties` class annotated with `@Validated`, with Bean Validation constraints on its fields:

```java
@ConfigurationProperties("payments")
@Validated
public record PaymentProperties(
        @NotNull Duration timeout,   // "5s" is converted to a Duration
        @Min(0) int retries) {
}
```

Register it with `@ConfigurationPropertiesScan` (or `@EnableConfigurationProperties(PaymentProperties.class)`) and inject `PaymentProperties` where needed. Spring Boot validates the bound values at startup, and an invalid value stops the application with a message naming the property.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration (@ConfigurationProperties validation) https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: profiles$hs$, $hs$What does activating the profile `prod` change in a Spring Boot application?$hs$, $hs$- `application-prod.properties` (or `.yml`) is loaded on top of `application.properties`; its values override the defaults.
- Beans annotated `@Profile("prod")` are registered; beans for other profiles are not.

Activate it with `spring.profiles.active=prod`, e.g. the environment variable `SPRING_PROFILES_ACTIVE=prod`.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Profiles https://docs.spring.io/spring-boot/reference/features/profiles.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: where secrets come from$hs$, $hs$Why shouldn't the database password be in `application.yml`, and where should it come from instead?$hs$, $hs$`application.yml` is committed to git and packaged into the jar and container image, so everyone with access to the code or image has the secret, and rotating it needs a rebuild.

Provide secrets at runtime from the platform: environment variables or mounted files (e.g. Kubernetes secrets, read with `spring.config.import=configtree:`), or a secrets manager (Vault, a cloud secret store). And never log them.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Externalized Configuration https://docs.spring.io/spring-boot/reference/features/external-config.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'devops', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: two DataSources in one service$hs$, $hs$The transfer service must write to its own database and read reference data from a second one. What happens to Spring Boot's `DataSource` auto-configuration, and what do you have to define yourself?$hs$, $hs$As soon as you define a `DataSource` bean, the auto-configured one backs off, and so does everything built on it unless you provide it.

Define both `DataSource`s, each bound to its own properties (e.g. `app.datasource.main.*` and `app.datasource.reference.*`), and mark the main one `@Primary`, so components without a qualifier use it. With JPA on both, each needs its own `EntityManagerFactory` and transaction manager, with the repositories split by package. Often simpler: JPA on the main database, and a `JdbcClient` for the read-only reference data.$hs$, 7, 'SUBMITTED', $hs$Spring Boot docs, How-to: Data Access (configure two DataSources) https://docs.spring.io/spring-boot/how-to/data-access.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Lifecycle: CommandLineRunner vs ApplicationRunner$hs$, $hs$What is the difference between a `CommandLineRunner` and an `ApplicationRunner` bean?$hs$, $hs$Both run once, after the application context has started. A `CommandLineRunner` receives the raw `String[]` arguments; an `ApplicationRunner` receives `ApplicationArguments`, already parsed into options (`--key=value`) and non-option arguments.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, SpringApplication (using the ApplicationRunner or CommandLineRunner) https://docs.spring.io/spring-boot/reference/features/spring-application.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Lifecycle: graceful shutdown$hs$, $hs$What happens when a Spring Boot web application receives a shutdown signal (SIGTERM) while requests are in progress?$hs$, $hs$Graceful shutdown is enabled by default: the web server stops accepting new requests, and in-flight requests get a grace period to finish, up to `spring.lifecycle.timeout-per-shutdown-phase` (30 seconds by default). Then the application context closes: `@PreDestroy` methods run, and pools and connections are closed.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Graceful Shutdown https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Lifecycle: requests dropped during a rolling deploy$hs$, $hs$Graceful shutdown is on, yet every rolling deploy on Kubernetes drops a few requests with connection errors. Why do requests still arrive at a pod that is shutting down, and what does a `preStop` hook with a short sleep change?$hs$, $hs$Kubernetes sends SIGTERM and removes the pod from the Service's endpoints at the same time, and the removal takes a moment to reach every node and load balancer. Meanwhile the application has started shutting down and stops accepting new connections, so the requests still routed to it fail.

A `preStop` sleep of a few seconds delays the SIGTERM: the pod keeps serving normally until it's out of every load balancer, and only then shuts down gracefully. `terminationGracePeriodSeconds` must cover the sleep plus `spring.lifecycle.timeout-per-shutdown-phase`.$hs$, 7, 'SUBMITTED', $hs$Spring Boot docs, Graceful Shutdown https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Lifecycle: a service killed by its liveness probe$hs$, $hs$A Boot service takes 90 seconds to start on Kubernetes, and its liveness probe kills it before it's ready, over and over. Which probe stops this restart loop, and how does it work with the liveness probe?$hs$, $hs$A startup probe. While it hasn't succeeded yet, Kubernetes doesn't run the liveness and readiness probes at all; once it succeeds, they take over.

```yaml
startupProbe:
  httpGet: { path: /actuator/health/liveness, port: 8080 }
  periodSeconds: 10
  failureThreshold: 18   # up to 180 seconds to start
```

The liveness probe can then stay strict, catching a hung application quickly, instead of being loosened for the slow start.$hs$, 6, 'SUBMITTED', $hs$Kubernetes docs, Configure Liveness, Readiness and Startup Probes (CC BY 4.0) https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Actuator: what it is$hs$, $hs$What is Spring Boot Actuator?$hs$, $hs$A module that adds production-ready features to an application: endpoints to monitor and manage it, such as `health`, `metrics`, `info`, `loggers` and `threaddump`.

Over HTTP, only `/actuator/health` is exposed by default; others are exposed with `management.endpoints.web.exposure.include`.$hs$, 1, 'SUBMITTED', $hs$Spring Boot docs, Actuator Endpoints https://docs.spring.io/spring-boot/reference/actuator/endpoints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Actuator: what's exposed by default$hs$, $hs$Which Actuator endpoints does a Spring Boot application expose over HTTP by default, and why so few?$hs$, $hs$Only `health`. Other endpoints can reveal internals (`env`, `configprops`, `beans`) or change the running application (`loggers`), so they must be opted into explicitly:

```properties
management.endpoints.web.exposure.include=health,info,metrics
```$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Actuator Endpoints (exposing endpoints) https://docs.spring.io/spring-boot/reference/actuator/endpoints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Actuator: how health is decided$hs$, $hs$How does `/actuator/health` decide whether the application is `UP`?$hs$, $hs$It runs every `HealthIndicator` (database, disk space, message broker, and custom ones) and aggregates their statuses: if any is `DOWN`, the overall status is `DOWN`. The HTTP status follows (503 for `DOWN`).

Details per indicator are shown only when `management.endpoint.health.show-details` allows it.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Actuator Endpoints (health information) https://docs.spring.io/spring-boot/reference/actuator/endpoints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Actuator: liveness vs readiness$hs$, $hs$What is the difference between the liveness and the readiness health groups that Spring Boot provides for Kubernetes?$hs$, $hs$- Liveness (`/actuator/health/liveness`): is the application's internal state healthy? If it fails, the platform restarts the container.
- Readiness (`/actuator/health/readiness`): can the application take traffic right now? If it fails, the platform stops sending it requests, without restarting it (e.g. during startup, or while a dependency is unavailable).$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Actuator Endpoints (Kubernetes probes) https://docs.spring.io/spring-boot/reference/actuator/endpoints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Actuator: a custom health indicator$hs$, $hs$Write a health indicator that reports the payment provider as down when its status API doesn't answer.$hs$, $hs$```java
@Component
class PaymentProviderHealthIndicator implements HealthIndicator {
    private final PaymentProviderClient client;

    PaymentProviderHealthIndicator(PaymentProviderClient client) { this.client = client; }

    @Override
    public Health health() {
        try {
            client.ping(Duration.ofSeconds(1));   // cheap, with a short timeout
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).withDetail("provider", "card-network").build();
        }
    }
}
```

It appears as `paymentProvider` in `/actuator/health`. Decide which health group it belongs to: probably readiness, never liveness.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Actuator Endpoints (writing custom HealthIndicators) https://docs.spring.io/spring-boot/reference/actuator/endpoints.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Observability: counting failed transfers per reason$hs$, $hs$Count failed transfers per reason (`insufficient_funds`, `account_closed`, ...), so the team can see them at `/actuator/metrics/transfers.failed` and graph them in Prometheus. Write the code in `TransferService`.$hs$, $hs$```java
@Service
class TransferService {

    private final MeterRegistry meters;

    TransferService(MeterRegistry meters) {   // auto-configured by Spring Boot
        this.meters = meters;
    }

    void recordFailure(String reason) {
        meters.counter("transfers.failed", "reason", reason).increment();
    }
}
```

One counter per tag value: Prometheus shows `transfers_failed_total{reason="insufficient_funds"}`. Tags must have a small, fixed set of values; never an account id, which would create a time series per account.$hs$, 5, 'SUBMITTED', $hs$Micrometer docs, Counters https://docs.micrometer.io/micrometer/reference/concepts/counters.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Web: the embedded server$hs$, $hs$Why doesn't a Spring Boot web application need to be deployed to an external Tomcat?$hs$, $hs$The web starter embeds the server (Tomcat by default; Jetty is an alternative, and Netty for WebFlux). The application starts the server itself from `main`, so the jar is the whole application: `java -jar app.jar`.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Servlet Web Applications https://docs.spring.io/spring-boot/reference/web/servlet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Web: Tomcat's limits$hs$, $hs$How many requests does a Spring Boot application on Tomcat process concurrently with default settings, and what happens to more?$hs$, $hs$Up to 200 at once: Tomcat's request thread pool (`server.tomcat.threads.max`). Further connections are accepted and wait for a free thread, up to `server.tomcat.max-connections` (8192); beyond that, new connections wait in the operating system's accept queue (`server.tomcat.accept-count`, 100), and are then refused.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Common Application Properties (server.tomcat) https://docs.spring.io/spring-boot/appendix/application-properties/index.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Web: virtual threads in Spring Boot$hs$, $hs$What changes in a Spring Boot web application when you set `spring.threads.virtual.enabled=true`?$hs$, $hs$Tomcat handles each request on a virtual thread instead of a pooled platform thread, and Boot's auto-configured task executor (for `@Async`) and scheduler use virtual threads too. Requests blocked on I/O no longer occupy scarce platform threads.

The limits move downstream: the database connection pool and other services must still cap concurrency.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Task Execution and Scheduling https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Packaging: the executable jar$hs$, $hs$What's inside a Spring Boot executable jar, and how does `java -jar` start it?$hs$, $hs$Your classes, all dependency jars nested inside (`BOOT-INF/lib`), and Spring Boot's launcher. The jar's manifest names the launcher (`JarLauncher`) as the main class: it builds a class loader that reads the nested jars, then calls your application's `main`.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, The Executable Jar Format https://docs.spring.io/spring-boot/specification/executable-jar/index.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Web: 2,000 requests for 200 threads$hs$, $hs$A Spring Boot service runs on Tomcat with default settings. Each `GET /athletes/{id}/stats` request takes about 500 ms, most of it in two database queries through the default HikariCP pool. A promotion makes 2,000 requests arrive at once. What do the clients experience, and why doesn't raising `server.tomcat.threads.max` to 2,000 fix it?$hs$, $hs$With default settings, Tomcat processes at most 200 requests concurrently (`server.tomcat.threads.max`). It accepts up to 8,192 connections (`server.tomcat.max-connections`), so all 2,000 are accepted, but 1,800 wait for a free thread. The requests are served in waves: the last ones wait several seconds, and clients with a shorter timeout give up and may retry, adding more load.

Raising the thread count does not help, because the real bottleneck is the database connection pool: HikariCP allows 10 connections by default. Every request needs a connection, so at most 10 requests make progress at a time, and the other threads only wait for a connection; more threads mean more waiting, more memory, and connection-acquisition timeouts.

The throughput is set by the slowest shared resource. The options:
- Make each request cheaper: fewer or faster queries, or a cache for the stats.
- Size the connection pool deliberately for what the database can handle, and scale out with more instances if needed.
- Fail fast instead of queueing indefinitely: rate limiting or load shedding (429 or 503 with `Retry-After`), and short timeouts.
- Virtual threads (`spring.threads.virtual.enabled=true`) remove the thread limit, but not the connection limit.$hs$, 7, 'SUBMITTED', $hs$Spring Boot docs, Common Application Properties (server.tomcat) https://docs.spring.io/spring-boot/appendix/application-properties/index.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot', 'spring-web', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Auto-configuration: writing your own$hs$, $hs$A shared library should give every bank service an `AuditClient` bean automatically, while letting a service define its own instead. Write the auto-configuration, and say how Spring Boot finds it.$hs$, $hs$```java
@AutoConfiguration
@ConditionalOnClass(AuditClient.class)
public class AuditClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean   // backs off when the service defines its own AuditClient
    AuditClient auditClient(AuditProperties properties) {
        return new AuditClient(properties.url());
    }
}
```

Spring Boot finds it through the file `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in the library's jar, which lists the class's fully qualified name.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Creating Your Own Auto-configuration https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$IoC: dependency injection$hs$, $hs$What is dependency injection in Spring?$hs$, $hs$An object declares the dependencies it needs, and the Spring container supplies them when it creates the object, instead of the object creating or looking them up itself. It's Spring's form of inversion of control.

```java
@Service
class TransferService {
    private final AccountRepository accounts;

    TransferService(AccountRepository accounts) {   // supplied by the container
        this.accounts = accounts;
    }
}
```$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Introduction to the IoC Container and Beans https://docs.spring.io/spring-framework/reference/core/beans/introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$IoC: what a bean is$hs$, $hs$What is a bean in Spring?$hs$, $hs$An object that is instantiated, assembled and managed by the Spring IoC container. An object you create yourself with `new` is not a bean.

It's usually declared with `@Component` (or `@Service`, `@Repository`, `@Controller`) for component scanning, or with a `@Bean` method in a `@Configuration` class.$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Introduction to the IoC Container and Beans https://docs.spring.io/spring-framework/reference/core/beans/introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$IoC: the ApplicationContext$hs$, $hs$What is Spring's `ApplicationContext`?$hs$, $hs$The interface of the Spring IoC container: it creates, configures and wires the application's beans from its configuration, and manages their lifecycle.

It extends the basic `BeanFactory` with application features: publishing events, resolving messages (i18n), loading resources, and access to the `Environment` (profiles and properties).$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Additional Capabilities of the ApplicationContext https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: constructor, setter or field$hs$, $hs$Spring can inject dependencies through the constructor, through setters or directly into fields. Which should you use for required dependencies, and why?$hs$, $hs$Constructor injection, which the Spring documentation recommends for mandatory dependencies:
- The fields can be `final`, so the object is fully initialised and immutable from the moment it exists.
- A missing dependency fails at startup, instead of causing a `NullPointerException` later.
- Tests create the object with `new` and pass test doubles, without Spring.
- A constructor with too many parameters makes a class with too many responsibilities obvious.

Setter injection suits optional dependencies. Field injection hides the dependencies and needs Spring or reflection to set them in tests.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Dependency Injection https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: a single constructor without @Autowired$hs$, $hs$Why does Spring inject `AccountRepository` here, although the constructor has no `@Autowired`?

```java
@Service
class StatementService {
    private final AccountRepository accounts;

    StatementService(AccountRepository accounts) { this.accounts = accounts; }
}
```$hs$, $hs$When a bean class has a single constructor, Spring uses it for injection automatically (since Spring 4.3). `@Autowired` is only needed to tell Spring which constructor to use when there are several.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Dependency Injection https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: what it gives your tests$hs$, $hs$How does constructor injection make `TransferService` easy to unit test?$hs$, $hs$The test builds the service itself, passing fakes or mocks through the constructor: no Spring container, no reflection, and the test runs in milliseconds.

```java
TransferService service = new TransferService(new InMemoryAccountRepository(), mock(Notifier.class));
```

The collaborators are visible in the constructor, so the test can't forget one.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Dependency Injection https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$IoC: an object created with new$hs$, $hs$`StatementService` is a `@Service` with an `@Autowired AccountRepository accounts` field. Why does this throw a `NullPointerException` inside `forMonth`?

```java
@RestController
class StatementController {
    @GetMapping("/statements/{month}")
    Statement statement(@PathVariable YearMonth month) {
        return new StatementService().forMonth(month);
    }
}
```$hs$, $hs$`new StatementService()` creates a plain object that Spring knows nothing about, so nothing injects its `accounts` field: it stays `null`.

```java
@RestController
class StatementController {
    private final StatementService statements;   // the bean, injected by Spring

    StatementController(StatementService statements) { this.statements = statements; }

    @GetMapping("/statements/{month}")
    Statement statement(@PathVariable YearMonth month) {
        return statements.forMonth(month);
    }
}
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Introduction to the IoC Container and Beans https://docs.spring.io/spring-framework/reference/core/beans/introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$IoC: BeanFactory vs ApplicationContext$hs$, $hs$What is the difference between a `BeanFactory` and an `ApplicationContext`?$hs$, $hs$`BeanFactory` is the basic container: it creates and wires beans, lazily, when they're requested. `ApplicationContext` extends it: it creates singletons eagerly at startup, registers post-processors automatically, and adds events, message resolution, resource loading and the `Environment`.

Applications use an `ApplicationContext`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Additional Capabilities of the ApplicationContext https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: @Component vs @Bean$hs$, $hs$When do you declare a bean with `@Component` on the class, and when with a `@Bean` method?$hs$, $hs$- `@Component` (or a stereotype): for your own classes, which component scanning finds.
- `@Bean` method in a `@Configuration` class: for classes you can't annotate (from libraries, e.g. a `Clock` or an HTTP client), for objects that need construction logic, or for several beans of the same class with different settings.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Basic Concepts: @Bean and @Configuration https://docs.spring.io/spring-framework/reference/core/beans/java/basic-concepts.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: what @Repository adds$hs$, $hs$`@Service` and `@Repository` are both specialisations of `@Component`. What does `@Repository` add that `@Service` doesn't?$hs$, $hs$Exception translation: exceptions thrown by a `@Repository` bean are translated into Spring's `DataAccessException` hierarchy, so callers handle the same exceptions whatever the persistence technology. (Spring Boot registers the `PersistenceExceptionTranslationPostProcessor` that does it.)

`@Service` only documents the class's role; Spring treats it exactly like `@Component`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Classpath Scanning and Managed Components https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: how component scanning works$hs$, $hs$How does component scanning decide which classes become beans?$hs$, $hs$It searches the base packages (by default, the package of the class annotated with `@ComponentScan`, or `@SpringBootApplication`, and every package below it) for classes annotated with `@Component` or an annotation built on it (`@Service`, `@Repository`, `@Controller`, `@Configuration`), and registers a bean definition for each.

Classes outside those packages aren't found, however they're annotated.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Classpath Scanning and Managed Components https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: calling a @Bean method from another$hs$, $hs$Do `transferService` and `statementService` share one `RateTable` instance? What changes with `@Configuration(proxyBeanMethods = false)`?

```java
@Configuration
class BankConfig {
    @Bean RateTable rateTable() { return new RateTable(); }
    @Bean TransferService transferService() { return new TransferService(rateTable()); }
    @Bean StatementService statementService() { return new StatementService(rateTable()); }
}
```$hs$, $hs$Yes. Spring subclasses a `@Configuration` class with CGLIB, and the subclass intercepts calls to `rateTable()`, returning the container's singleton instead of running the method again.

With `proxyBeanMethods = false` ("lite" mode) there's no interception: each call to `rateTable()` is a plain Java call that creates a new `RateTable`, not managed by Spring. In lite mode, take dependencies as method parameters instead:

```java
@Bean TransferService transferService(RateTable rateTable) { return new TransferService(rateTable); }
```$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Basic Concepts: @Bean and @Configuration https://docs.spring.io/spring-framework/reference/core/beans/java/basic-concepts.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Validation: rejecting invalid arguments before a service method runs$hs$, $hs$Make Spring reject calls to `transfer` with a missing account id or an amount that isn't positive, before the method body runs, using Bean Validation annotations. What does a caller get for an invalid call?

```java
@Service
class TransferService {
    public void transfer(Long fromAccountId, BigDecimal amount) { ... }
}
```$hs$, $hs$```java
@Service
@Validated   // the class's methods get validated through a proxy
class TransferService {
    public void transfer(@NotNull Long fromAccountId, @Positive BigDecimal amount) { ... }
}
```

With a Bean Validation provider on the classpath (`spring-boot-starter-validation`), Spring Boot registers the method-validation post-processor. An invalid call throws a `ConstraintViolationException` listing each violated constraint, and the method body never runs.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Java Bean Validation (Method Validation) https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: @Primary vs @Qualifier$hs$, $hs$What is the difference between `@Primary` and `@Qualifier` when several beans match a type?$hs$, $hs$- `@Primary` on a bean makes it the default choice wherever its type is injected without further detail.
- `@Qualifier("name")` at the injection point selects a specific bean.

When both apply, the qualifier wins: it's the more specific choice.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Fine-tuning Annotation-based Autowiring with @Primary https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-primary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: two beans of the same type$hs$, $hs$Two beans implement `PaymentGateway`. A constructor asks for one `PaymentGateway`. What happens, and how do you tell Spring which one to inject?$hs$, $hs$Spring matches by type, finds two candidates and fails at startup with a `NoUniqueBeanDefinitionException`, unless something picks one:

- `@Primary` on one implementation makes it the default.
- `@Qualifier("stripeGateway")` on the constructor parameter selects a bean by name.
- As a fallback, Spring matches the parameter name against the bean names.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Fine-tuning Annotation-based Autowiring with @Primary https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-primary.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: all implementations of an interface$hs$, $hs$A `FraudChecker` must run every `FraudRule` bean, in a defined order. How do you inject them?$hs$, $hs$```java
@Component
class FraudChecker {
    private final List<FraudRule> rules;   // every FraudRule bean

    FraudChecker(List<FraudRule> rules) { this.rules = rules; }
}
```

The list is sorted by `@Order` (or the `Ordered` interface) on the rule beans. A `Map<String, FraudRule>` gives the beans keyed by their names instead.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Using @Autowired (collections) https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: an optional dependency$hs$, $hs$A `MetricsPublisher` bean exists only in some environments. How can `TransferService` use it when present, without failing when it's absent?$hs$, $hs$```java
TransferService(ObjectProvider<MetricsPublisher> metrics) {
    this.metrics = metrics;
}

void transfer(...) {
    ...
    metrics.ifAvailable(publisher -> publisher.recordTransfer());   // does nothing when absent
}
```

`Optional<MetricsPublisher>` as a parameter, or `@Autowired(required = false)` on a setter, also work. `ObjectProvider` also resolves lazily.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Using @Autowired https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: a circular dependency$hs$, $hs$`AccountService` needs `TransferService` in its constructor, and `TransferService` needs `AccountService` in its constructor. What happens at startup?$hs$, $hs$It fails with a `BeanCurrentlyInCreationException`: to create either bean, Spring must first create the other. Spring Boot also refuses circular references through fields and setters by default (`spring.main.allow-circular-references=false`).

It points to a design problem: extract what both need into a third bean, or decouple them with events.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Dependency Injection (circular dependencies) https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Injection: breaking a circular dependency$hs$, $hs$`AccountService` calls `TransferService` to move money, and `TransferService` calls `AccountService` to check account status. Startup fails with a circular dependency. Why does `@Lazy` on one side only hide the problem, and which change to the design removes the cycle?$hs$, $hs$With constructor injection, neither bean can be created first, because each needs the other already constructed; Spring detects the cycle and fails at startup. `@Lazy` injects a proxy instead of the real bean, which lets startup succeed, but both classes still depend on each other: the tangled responsibilities remain.

A circular dependency is a design problem, solved by removing one direction of the dependency:
- Extract what both need into a third bean (e.g. `AccountStatusChecker`), on which both depend and which depends on neither.
- Or decouple one direction with events: `TransferService` publishes `TransferCompleted`, and `AccountService` listens for it instead of being called.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Dependency Injection (circular dependencies) https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Bean lifecycle: the order of the callbacks$hs$, $hs$In which order does Spring call these for a singleton bean: the constructor, field injection, `@PostConstruct`, `afterPropertiesSet()` and the `@Bean(initMethod = ...)` method?$hs$, $hs$1. The constructor (with constructor injection).
2. Field and setter injection.
3. The `@PostConstruct` method.
4. `InitializingBean.afterPropertiesSet()`.
5. The custom `initMethod`.

After that, `BeanPostProcessor`s may wrap the bean, e.g. in a proxy.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Customizing the Nature of a Bean https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Bean lifecycle: when @PostConstruct runs$hs$, $hs$When does Spring call a bean's `@PostConstruct` method?$hs$, $hs$Once, after the bean has been created and all its dependencies have been injected, before the bean is handed out for use. For a singleton, that's during the context's startup.

```java
@Component
class RateCache {
    @Autowired RateRepository rates;

    @PostConstruct
    void load() {            // rates is already injected here; in the constructor it isn't
        cache = rates.findAll();
    }
}
```$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Using @PostConstruct and @PreDestroy https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/postconstruct-and-predestroy-annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Bean lifecycle: BeanPostProcessor vs BeanFactoryPostProcessor$hs$, $hs$What is the difference between a `BeanFactoryPostProcessor` and a `BeanPostProcessor`?$hs$, $hs$- A `BeanFactoryPostProcessor` works on bean *definitions*, before any bean is created: e.g. resolving `${...}` placeholders in configuration.
- A `BeanPostProcessor` works on bean *instances*, before and after each one is initialised: e.g. wrapping a bean in a proxy for `@Transactional`.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Container Extension Points https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Bean lifecycle: eager singletons and @Lazy$hs$, $hs$Are singleton beans created at startup or on first use? What does `@Lazy` change, and what's the trade-off?$hs$, $hs$At startup: the `ApplicationContext` creates all singletons eagerly, so misconfiguration (a missing bean, a bad property) fails the start instead of a request later.

`@Lazy` delays a bean's creation until it's first used. Startup gets faster, but errors in that bean only show up at runtime, and the first request pays the creation cost.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Lazy-initialized Beans https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-lazy-init.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scopes: the default scope$hs$, $hs$Which scope does a Spring bean have by default, and what does it mean?$hs$, $hs$Singleton: the container creates one instance of the bean and injects that same instance everywhere.

It's one instance per container, not per JVM. Since all threads share it, a singleton should be stateless or thread-safe.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Bean Scopes https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scopes: Spring singleton vs the Singleton pattern$hs$, $hs$How does a Spring singleton bean differ from the classic Singleton design pattern?$hs$, $hs$- Spring singleton: one instance per container and per bean definition. Two bean definitions of the same class give two instances; the class itself is ordinary and can be created with `new` in tests.
- Singleton pattern: the class itself enforces exactly one instance per class loader (private constructor, static accessor), which makes it hard to replace in tests.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Bean Scopes (singleton) https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scopes: a prototype injected into a singleton$hs$, $hs$How many `TransferReceipt` objects exist after `issuer.next()` is called three times?

```java
@Component @Scope("prototype")
class TransferReceipt { }

@Component
class ReceiptIssuer {
    private final TransferReceipt receipt;
    ReceiptIssuer(TransferReceipt receipt) { this.receipt = receipt; }
    TransferReceipt next() { return receipt; }
}
```$hs$, $hs$One. The prototype is created once, when the singleton `ReceiptIssuer` is created and injected; every call to `next()` returns that same instance.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Bean Scopes (singleton beans with prototype-bean dependencies) https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scopes: why singletons must be stateless$hs$, $hs$Why must singleton beans such as services and controllers be stateless, or thread-safe?$hs$, $hs$One instance serves every request, and requests run concurrently on different threads. A mutable field is therefore shared by all of them: one request's value can be overwritten by another's mid-way (a race), leaking data between users.

Keep request data in local variables and parameters; shared state that must exist needs thread-safe types (atomics, concurrent collections).$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Bean Scopes (singleton) https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: what it's for$hs$, $hs$What does Spring use aspect-oriented programming (AOP) for?$hs$, $hs$To apply cross-cutting concerns, behaviour needed in many places, without writing it into the business code: transactions (`@Transactional`), security (`@PreAuthorize`), caching (`@Cacheable`), retries, metrics.

Spring applies them by wrapping beans in proxies that run the extra behaviour around the method call.$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Aspect Oriented Programming with Spring https://docs.spring.io/spring-framework/reference/core/aop.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: aspect, advice, pointcut and join point$hs$, $hs$In AOP, what are an aspect, an advice, a pointcut and a join point?$hs$, $hs$- Join point: a point in the program where behaviour can be added. In Spring AOP, always a method execution.
- Pointcut: an expression selecting join points, e.g. "every method annotated `@Audited`".
- Advice: the code to run at the selected join points: before, after, or around the method.
- Aspect: a module combining pointcuts and advice, e.g. an auditing aspect.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, AOP Concepts https://docs.spring.io/spring-framework/reference/core/aop/introduction-defn.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: JDK dynamic proxies vs CGLIB proxies$hs$, $hs$What is the difference between a JDK dynamic proxy and a CGLIB proxy, and which does Spring Boot use by default?$hs$, $hs$- A JDK dynamic proxy implements the bean's interfaces: it can only be used through those interfaces, and only interface methods are intercepted.
- A CGLIB proxy is a generated subclass of the bean's class: it works without interfaces, but can't proxy `final` classes or intercept `final` or `private` methods.

Spring Boot uses CGLIB by default (`spring.aop.proxy-target-class=true`).$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Proxying Mechanisms https://docs.spring.io/spring-framework/reference/core/aop/proxying.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: what a CGLIB proxy can't intercept$hs$, $hs$Spring Boot applies `@Transactional` through CGLIB proxies by default. In which of these cases is a transaction not started?

```java
@Service
public class TransferService {
    @Transactional public void transfer(TransferRequest r) { ... }               // 1
    @Transactional public final void reverse(long id) { ... }                    // 2
    @Transactional private void settle(long id) { ... }                          // 3
    public void transferAll(List<TransferRequest> all) { all.forEach(this::transfer); }  // 4: calls transfer
}
```$hs$, $hs$In cases 2, 3 and 4.

A CGLIB proxy (CGLIB: Code Generation Library, repackaged inside `spring-core`) is a subclass of the bean generated at runtime. It overrides the bean's methods to start and end the transaction around them, so it can intercept only calls that arrive through the proxy, to methods a subclass can override.

```java
@Service
public class TransferService {
    @Transactional public void transfer(TransferRequest r) { ... }               // 1: works, when called from another bean
    @Transactional public final void reverse(long id) { ... }                    // 2: no: a final method can't be overridden
    @Transactional private void settle(long id) { ... }                          // 3: no: a private method can't be overridden
    public void transferAll(List<TransferRequest> all) { all.forEach(this::transfer); }  // 4: no: a call on this bypasses the proxy
}
```$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Proxying Mechanisms https://docs.spring.io/spring-framework/reference/core/aop/proxying.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: Spring AOP vs AspectJ$hs$, $hs$What is the difference between Spring AOP and AspectJ?$hs$, $hs$- Spring AOP is proxy-based: it only advises method calls on Spring beans that go through the proxy, so not self-invocations, private methods or objects created with `new`.
- AspectJ weaves the advice into the bytecode, at compile time or class-load time, so it can advise any join point: self-calls, private methods, constructors, field access, even non-Spring objects.

Spring AOP is enough for most applications and needs no special build or agent.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Aspect Oriented Programming with Spring https://docs.spring.io/spring-framework/reference/core/aop.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: what @Around can do$hs$, $hs$What can an `@Around` advice do that `@Before` and `@After` advice can't?$hs$, $hs$It controls the call itself, through `ProceedingJoinPoint.proceed()`:
- decide whether the method runs at all (e.g. return a cached value instead)
- run code before and after it, e.g. measure its duration
- change the arguments passed and the value returned
- catch, retry or translate the exceptions it throws.

```java
@Around("@annotation(Timed)")
Object time(ProceedingJoinPoint call) throws Throwable {
    long start = System.nanoTime();
    try {
        return call.proceed();
    } finally {
        metrics.record(call.getSignature().toShortString(), System.nanoTime() - start);
    }
}
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Declaring Advice (around advice) https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/advice.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: an aspect that doesn't fire$hs$, $hs$A `@Timed` aspect measures calls from the controller to `StatementService.build`, but not the calls `build` makes to `this.loadEntries`, which is also annotated `@Timed`. Why?$hs$, $hs$Self-invocation: `this.loadEntries()` calls the target object directly, not the proxy that Spring put in front of it, so no advice runs. Only calls that come in through the proxy, from other beans, are advised.

Move `loadEntries` into another bean, or use AspectJ weaving if self-calls must be advised.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Proxying Mechanisms (understanding AOP proxies) https://docs.spring.io/spring-framework/reference/core/aop/proxying.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Proxies: what @Transactional, @Async, @Cacheable and @PreAuthorize share$hs$, $hs$What do `@Transactional`, `@Async`, `@Cacheable` and `@PreAuthorize` have in common in how they work, and which limits do they therefore share?$hs$, $hs$Each is implemented by a proxy around the bean, which runs its logic around the method call.

So they share the same limits: they don't apply to self-invocation (`this.method()`), to `private` or `final` methods, or to objects that aren't Spring beans.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Proxying Mechanisms https://docs.spring.io/spring-framework/reference/core/aop/proxying.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Proxies: an @Async method that runs synchronously$hs$, $hs$An `@Async` method still runs on the caller's thread. Name two likely reasons.$hs$, $hs$`@Async` works through a proxy that hands the call to an executor; any call that does not go through that proxy runs synchronously.
- `@EnableAsync` is missing from the configuration, so no async proxy is created at all.
- The method is called on `this` from within the same bean (self-invocation), which bypasses the proxy.

A `private` method, or an object that Spring does not manage, has the same effect.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Task Execution and Scheduling (@Async) https://docs.spring.io/spring-framework/reference/integration/scheduling.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Proxies: @Transactional and @Cacheable on one method$hs$, $hs$A method is annotated with both `@Transactional` and `@Cacheable`. In which order do they apply, why does it matter, and how do you control it?$hs$, $hs$Both are advisors on the same proxy, applied in the order of their `order` value. It decides, e.g., whether a cache hit still opens a transaction (and takes a database connection) or returns before one is started.

Set the order explicitly: `@EnableCaching(order = ...)` and `@EnableTransactionManagement(order = ...)`, a lower value meaning the advice runs outside (first). Usually the cache should be outside the transaction.$hs$, 7, 'SUBMITTED', $hs$Spring Framework docs, Cache Abstraction https://docs.spring.io/spring-framework/reference/integration/cache.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$AOP: retrying on optimistic locking failures with an aspect$hs$, $hs$Write the advice for `@RetryOnConflict`, which retries a service method up to three times on optimistic locking failures. Where must its advice sit relative to `@Transactional`, and why?$hs$, $hs$Outside the transaction: each attempt must run in a new transaction that reloads current data. Inside, a retry would reuse the failed transaction, which is marked for rollback and holds stale entities.

```java
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
class RetryOnConflictAspect {
    @Around("@annotation(RetryOnConflict)")
    Object retry(ProceedingJoinPoint call) throws Throwable {
        for (int attempt = 1; ; attempt++) {
            try {
                return call.proceed();                  // each proceed() starts a new transaction
            } catch (OptimisticLockingFailureException e) {
                if (attempt == 3) throw e;
                Thread.sleep(50L * attempt);            // a little backoff
            }
        }
    }
}
```

Retry only that specific exception, beware of non-idempotent side effects inside the method (emails, remote calls), and remember self-invocation bypasses the aspect.$hs$, 8, 'SUBMITTED', $hs$Spring Framework docs, Declaring Advice (advice ordering) https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/advice.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: application events$hs$, $hs$What are Spring application events, and why use them?$hs$, $hs$A bean publishes an event object through `ApplicationEventPublisher.publishEvent(...)`, and any bean with an `@EventListener` method for that event type reacts to it.

The publisher doesn't know who reacts: `TransferService` publishes `TransferCompleted`, and notifications, statistics and fraud checks can each listen to it without `TransferService` depending on them.$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Additional Capabilities of the ApplicationContext (events) https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: how @EventListener is called$hs$, $hs$By default, is an `@EventListener` method called synchronously or asynchronously? In which thread and which transaction does it run?$hs$, $hs$Synchronously: `publishEvent` calls each listener directly, in the publisher's thread and inside the publisher's transaction, and returns when they've all finished. An exception thrown by a listener propagates to the publisher.

Annotating the listener with `@Async` (with async enabled) makes it run on another thread.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Additional Capabilities of the ApplicationContext (events) https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: @EventListener vs @TransactionalEventListener$hs$, $hs$What is the difference between an `@EventListener` and a `@TransactionalEventListener` for a `TransferCompleted` event published inside a transaction?$hs$, $hs$- `@EventListener` runs immediately, when the event is published, inside the transaction, even if the transaction later rolls back.
- `@TransactionalEventListener` runs at a phase of the transaction, by default after commit, so it only reacts to transfers that were really saved. On rollback it doesn't run.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Transaction-bound Events https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Events: a @TransactionalEventListener that never runs$hs$, $hs$A `@TransactionalEventListener` never runs. The event is published from a method without `@Transactional`. Why?$hs$, $hs$There's no transaction, so there's no commit to wait for, and the event is dropped. Either publish it from inside a transaction, or let the listener run without one with `@TransactionalEventListener(fallbackExecution = true)`.$hs$, 6, 'SUBMITTED', $hs$Spring Framework docs, Transaction-bound Events https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: beans per environment with profiles$hs$, $hs$A fake payment gateway must be registered everywhere except in production. How do you declare it?$hs$, $hs$```java
@Component
@Profile("!prod")      // registered only when the profile "prod" is not active
class FakePaymentGateway implements PaymentGateway { ... }
```

Profiles are activated with `spring.profiles.active`, e.g. the environment variable `SPRING_PROFILES_ACTIVE=prod`.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Environment Abstraction (bean definition profiles) https://docs.spring.io/spring-framework/reference/core/beans/environment.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Configuration: @Conditional$hs$, $hs$What does `@Conditional` do, and how does Spring Boot's auto-configuration use it?$hs$, $hs$`@Conditional(SomeCondition.class)` on a `@Bean` method or a configuration class registers it only if the `Condition` matches at startup.

Spring Boot's auto-configuration is built from such conditions: `@ConditionalOnClass` (a class is on the classpath), `@ConditionalOnMissingBean` (you haven't defined one yourself), `@ConditionalOnProperty` (a property is set). `@Profile` is itself implemented as a condition.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Composing Java-based Configurations (@Conditional) https://docs.spring.io/spring-framework/reference/core/beans/java/composing-configuration-classes.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-core', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence: JPA, Hibernate and Spring Data JPA$hs$, $hs$How do JPA, Hibernate and Spring Data JPA relate to each other?$hs$, $hs$- JPA (Jakarta Persistence) is the specification: the standard API for object-relational mapping (`@Entity`, `EntityManager`, JPQL).
- Hibernate ORM implements that specification; it's Spring Boot's default.
- Spring Data JPA builds on JPA: it generates repository implementations from interfaces, using the `EntityManager` underneath.$hs$, 1, 'SUBMITTED', $hs$Spring Data JPA reference documentation https://docs.spring.io/spring-data/jpa/reference/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence: JPA vs plain JDBC$hs$, $hs$What does JPA give you that plain JDBC (`JdbcClient`, `JdbcTemplate`) doesn't, and what does JDBC give you in return?$hs$, $hs$- JPA maps object graphs to tables, tracks changes to loaded entities and writes them automatically, and loads relations on demand. Good for rich domain models with many writes. The SQL is generated, so it's less visible.
- JDBC runs exactly the SQL you write: full control, predictable performance, every database feature. You map rows yourself.

Many services use both: JPA for writing the domain, plain SQL for reporting and complex reads.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Data Access with JDBC https://docs.spring.io/spring-framework/reference/data-access/jdbc.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence: plain JDBC vs jOOQ vs JPA$hs$, $hs$A Java service can talk to PostgreSQL through plain JDBC (e.g. `JdbcTemplate`), through jOOQ, or through JPA with Hibernate. What is each one, and what does each trade off?$hs$, $hs$- **Plain JDBC** (`JdbcTemplate`, `JdbcClient`): the application writes SQL as strings and maps rows by hand. It gives full control and no hidden queries, but no compile-time checking: a typo in a column name fails at runtime.
- **jOOQ:** a library that generates Java classes from the database schema and offers a type-safe SQL DSL. The application still writes SQL, including vendor features such as window functions and `ON CONFLICT`, but the compiler checks tables, columns and types.
- **JPA with Hibernate:** an object-relational mapper. The application works with entities, and Hibernate generates the SQL, tracks changes (dirty checking) and manages relationships. It means less code for CRUD-heavy domains, at the cost of less control over the SQL, and pitfalls such as N+1 queries and lazy loading.

SQL-centric services that want predictable queries tend to choose jOOQ or plain JDBC; JPA fits rich entity models with mostly simple queries.$hs$, 4, 'SUBMITTED', $hs$jOOQ, The jOOQ User Manual https://www.jooq.org/doc/latest/manual/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: what it is$hs$, $hs$What is the JPA persistence context?$hs$, $hs$The set of entity instances an `EntityManager` currently manages, with at most one instance per database row (id).

- It tracks changes to managed entities (dirty checking) and writes them to the database on flush, at the latest at commit, without an explicit `save`.
- In Spring, it usually lives as long as the transaction.$hs$, 2, 'SUBMITTED', $hs$Hibernate ORM User Guide (persistence contexts) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: the entity states$hs$, $hs$Name the four states a JPA entity instance can be in.$hs$, $hs$- New (transient): created with `new`, not yet associated with a persistence context or the database.
- Managed: associated with a persistence context; its changes are tracked and written on flush.
- Detached: was managed, but its persistence context has closed (or it was evicted); changes are no longer tracked.
- Removed: managed and scheduled for deletion at flush.$hs$, 2, 'SUBMITTED', $hs$Hibernate ORM User Guide (entity states) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: dirty checking$hs$, $hs$Is the new balance written to the database, although `save` is never called?

```java
@Transactional
public void applyFee(long accountId, BigDecimal fee) {
    Account account = accountRepository.findById(accountId).orElseThrow();
    account.setBalance(account.getBalance().subtract(fee));
}
```$hs$, $hs$Yes. `account` is a managed entity. At flush (at the latest at commit), Hibernate compares every managed entity with the snapshot it took when loading it (dirty checking), and writes an `UPDATE` for each one that changed.

`save` is only needed for new entities, or detached ones.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (flushing) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: loading the same row twice$hs$, $hs$Inside one `@Transactional` method, what does this print, and how many `SELECT`s run?

```java
Account a = accountRepository.findById(1L).orElseThrow();
Account b = accountRepository.findById(1L).orElseThrow();
System.out.println(a == b);
```$hs$, $hs$`true`, and one `SELECT`. The persistence context holds at most one instance per row: the second `findById` finds account 1 already managed, and returns the same instance without querying.$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (persistence contexts) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: persist vs merge$hs$, $hs$What is the difference between `entityManager.persist(account)` and `entityManager.merge(account)`?$hs$, $hs$- `persist` makes a new instance managed; it's inserted at flush. The instance you passed is now the managed one.
- `merge` copies the state of a detached (or new) instance onto a managed instance, loading it from the database if needed, and returns that managed instance. The instance you passed stays detached.

```java
Account managed = entityManager.merge(detached);   // keep working with managed, not detached
```$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (merge) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: save() with an assigned id$hs$, $hs$`Account` ids are assigned by the application (an IBAN-based string), and there's no `@Version` field. Why does every `accountRepository.save(newAccount)` run a `SELECT` before the `INSERT`?$hs$, $hs$Spring Data decides whether an entity is new by looking at its version field, or else its id: an entity with a non-null id counts as not new. So `save` calls `merge` instead of `persist`, and `merge` first loads the row to copy the state onto.

Fixes: implement `Persistable<String>` with your own `isNew()`, or add a `@Version` field (null while new).$hs$, 5, 'SUBMITTED', $hs$Spring Data JPA docs, Persisting Entities (entity state detection) https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: when Hibernate flushes$hs$, $hs$When does Hibernate write pending changes (flush) to the database during a transaction?$hs$, $hs$In the default flush mode (`AUTO`):
- at commit
- before running a query whose results the pending changes could affect, so the query sees them
- whenever `flush()` is called explicitly.

Flushing sends the SQL; it doesn't commit. A rollback still undoes it.$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (flushing) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Persistence context: running out of memory in a batch job$hs$, $hs$A job updates 200,000 accounts in one transaction and runs out of memory. Why, and how do you fix it?

```java
@Transactional
public void recalculateAll() {
    for (Account account : accountRepository.findAll()) {
        account.recalculateLimits();
    }
}
```$hs$, $hs$Every loaded entity stays managed in the persistence context until the transaction ends, together with its snapshot for dirty checking: 200,000 of each.

Process in chunks, each flushed and cleared (or each in its own transaction):

```java
Pageable page = PageRequest.of(0, 1_000, Sort.by("id"));
Slice<Account> chunk;
do {
    chunk = accountRepository.findAll(page);
    chunk.forEach(Account::recalculateLimits);
    entityManager.flush();     // write this chunk
    entityManager.clear();     // let the GC reclaim it
    page = chunk.nextPageable();
} while (chunk.hasNext());
```

For pure bulk updates, a set-based SQL `UPDATE` is often better still.$hs$, 6, 'SUBMITTED', $hs$Hibernate ORM User Guide (batching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Relationships: the owning side$hs$, $hs$`Account` has `@OneToMany(mappedBy = "account") List<Card> cards`, and `Card` has `@ManyToOne Account account`. Which side is the owning side, and why does it matter?$hs$, $hs$`Card.account` is the owning side: it maps the foreign key column (`card.account_id`); the side with `mappedBy` is the inverse side.

Only changes to the owning side are written to the database. Adding a card to `account.getCards()` without setting `card.setAccount(account)` stores nothing.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (bidirectional associations) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Relationships: forgetting the owning side$hs$, $hs$After this runs, the new card is saved, but its `account_id` column is `NULL`. Why, and how do you prevent it?

```java
Card card = new Card("1234");
account.getCards().add(card);
cardRepository.save(card);
```$hs$, $hs$Only the owning side, `card.account`, is written, and it was never set. Keep both sides in sync in one helper method on the entity:

```java
public void addCard(Card card) {
    cards.add(card);
    card.setAccount(this);   // the owning side: this sets account_id
}
```$hs$, 5, 'SUBMITTED', $hs$Hibernate ORM User Guide (bidirectional associations) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Relationships: the default fetch types$hs$, $hs$What are the default fetch types of `@ManyToOne`, `@OneToOne`, `@OneToMany` and `@ManyToMany` in JPA?$hs$, $hs$- `@ManyToOne` and `@OneToOne`: `EAGER`.
- `@OneToMany` and `@ManyToMany`: `LAZY`.

Most teams declare every relationship `LAZY` explicitly, and fetch what each use case needs.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (fetching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Relationships: cascade vs orphanRemoval$hs$, $hs$What is the difference between `cascade = CascadeType.ALL` and `orphanRemoval = true` on `@OneToMany List<Card> cards`?$hs$, $hs$- Cascading passes operations from the parent to its children: persisting, merging or removing the account also persists, merges or removes its cards.
- Orphan removal deletes a card when it's removed from the `cards` collection, even though the account itself stays.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (cascading) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mapping: a value object as an embeddable$hs$, $hs$Map an amount with its currency inside `Transfer`, as one `Money` value object stored in two columns of the `transfer` table.$hs$, $hs$```java
@Embeddable
public record Money(BigDecimal amount, String currency) {}

@Entity
public class Transfer {
    @Id Long id;

    @Embedded
    private Money money;   // stored as the columns amount and currency in transfer
}
```

`@AttributeOverride` renames the columns when an entity embeds `Money` twice (e.g. `fee` and `total`).$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (embeddable types) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mapping: inheritance strategies$hs$, $hs$`SavingsAccount` and `CheckingAccount` extend `Account`. How do the inheritance strategies `SINGLE_TABLE`, `JOINED` and `TABLE_PER_CLASS` store them?$hs$, $hs$- `SINGLE_TABLE` (the default): one `account` table with a discriminator column and the columns of all subclasses, nullable. Fast, no joins.
- `JOINED`: an `account` table for the shared columns, plus a table per subclass joined by id. Normalised, but every load needs a join.
- `TABLE_PER_CLASS`: one complete table per concrete class. A query on `Account` needs a `UNION` over all tables.

`@MappedSuperclass` is different: it only shares mapped fields, and `Account` can't be queried polymorphically.$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (inheritance) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mapping: IDENTITY vs SEQUENCE ids$hs$, $hs$Why does `GenerationType.SEQUENCE` allow Hibernate to batch inserts, while `GenerationType.IDENTITY` doesn't?$hs$, $hs$- `IDENTITY`: the database assigns the id during the `INSERT`, so Hibernate must run each insert immediately to learn the id. Inserts can't be grouped into JDBC batches.
- `SEQUENCE`: Hibernate gets ids from a database sequence before inserting, and fetches them in blocks (the pooled optimizer, 50 by default), so it can delay the inserts and send them in batches.$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (identifiers, generated values) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mapping: equals and hashCode on a generated id$hs$, $hs$`Card` uses its generated `id` in `equals` and `hashCode`. A new card is added to a `HashSet<Card>`, then saved. Why can't the set find it afterwards?$hs$, $hs$The id is `null` until the entity is persisted. The card went into the bucket for the hash of a `null` id; after saving, its `hashCode` uses the new id, so the set looks in another bucket.

Use a natural, immutable business key (e.g. the card number) for equality, or compare by id in `equals` with a constant `hashCode()` (e.g. `getClass().hashCode()`).$hs$, 6, 'SUBMITTED', $hs$Hibernate ORM User Guide (equals and hashCode) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'java-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mapping: Lombok @Data on entities$hs$, $hs$Why shouldn't a JPA entity be annotated with Lombok's `@Data`?$hs$, $hs$The generated `equals`, `hashCode` and `toString` use every field:
- They touch lazy relations, which triggers queries, or `LazyInitializationException` outside a transaction.
- On bidirectional relations, `toString` and `hashCode` call each other endlessly (`StackOverflowError`).
- `hashCode` changes when the generated id or any field changes, breaking hash-based collections.

Use `@Getter`/`@Setter` and write `equals`/`hashCode` deliberately.$hs$, 5, 'SUBMITTED', $hs$Hibernate ORM User Guide (equals and hashCode) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: the N+1 query problem$hs$, $hs$`cards` is a lazy `@OneToMany` of `Account`. How many SQL queries does this run for 50 accounts, and how do you bring it down to one?

```java
List<Account> accounts = accountRepository.findAll();
for (Account account : accounts) {
    System.out.println(account.getIban() + ": " + account.getCards().size());
}
```$hs$, $hs$51: one for the accounts, then one per account when its lazy `cards` collection is first used. This is the N+1 query problem.

```java
@Query("select distinct a from Account a left join fetch a.cards")
List<Account> findAllWithCards();   // accounts and their cards in one query
```

Alternatives: `@EntityGraph(attributePaths = "cards")` on a repository method, or batch fetching (`hibernate.default_batch_fetch_size`), which loads the collections in a few `IN (...)` queries.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (fetching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: catching N+1 before production$hs$, $hs$How can a test make an N+1 regression fail the build? Write one for `accountService.overview()`, which should run exactly 2 SQL statements, using Hibernate's statistics.$hs$, $hs$```java
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class AccountOverviewQueriesTest {

    @Autowired EntityManagerFactory entityManagerFactory;
    @Autowired AccountService accountService;

    @Test
    void overviewRunsTwoStatements() {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        accountService.overview();

        assertEquals(2, statistics.getPrepareStatementCount());
    }
}
```

Hibernate counts every statement it prepares. If someone later adds a lazy relation that the overview touches per account, the count grows with the number of accounts and the test fails. A datasource proxy that counts queries does the same without Hibernate.$hs$, 5, 'SUBMITTED', $hs$Hibernate ORM User Guide (statistics) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: a DTO projection for a list screen$hs$, $hs$The accounts list shows only the IBAN, the holder's name and the balance. How do you query it efficiently with Spring Data JPA?$hs$, $hs$Select only those columns into a projection instead of loading entities:

```java
public record AccountRow(String iban, String holderName, BigDecimal balance) {}

@Query("select new com.bank.AccountRow(a.iban, a.holder.name, a.balance) from Account a")
List<AccountRow> findRows();
```

No entities are created or tracked (no snapshots, no dirty checking), the join to `holder` happens in the one query, and only three columns travel over the network. Interface-based projections work too.$hs$, 5, 'SUBMITTED', $hs$Spring Data JPA docs, Projections https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: LazyInitializationException$hs$, $hs$This method throws `LazyInitializationException` on the marked line. Why, and how do you fix it?

```java
public int countWorkouts(long athleteId) {
    Athlete athlete = athleteService.find(athleteId);   // @Transactional inside the service
    return athlete.getWorkouts().size();                 // throws here
}
```$hs$, $hs$`workouts` is a lazy association: Hibernate loads it the first time it's accessed, which needs an open persistence context. The transaction in `find` has ended, so the persistence context is closed when `size()` is called.

```java
// in the repository: fetch the workouts with the athlete, inside the transaction
@Query("select a from Athlete a left join fetch a.workouts where a.id = :id")
Optional<Athlete> findWithWorkouts(@Param("id") long id);
```

Alternatives: do the work inside the transactional service method, or return a DTO with what's needed.$hs$, 3, 'SUBMITTED', $hs$Hibernate ORM User Guide (fetching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: open-session-in-view$hs$, $hs$What is open-session-in-view (open EntityManager in view), and what does Spring Boot do with it by default?$hs$, $hs$The persistence context stays open for the whole web request, including while the controller's result is turned into JSON, so lazy relations can still be loaded there.

Spring Boot enables it by default (`spring.jpa.open-in-view=true`) and logs a warning at startup. Many teams turn it off: it hides N+1 queries in the web layer and holds a database connection for the whole request.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, SQL Databases (open EntityManager in view) https://docs.spring.io/spring-boot/reference/data/sql.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: why FetchType.EAGER isn't the fix$hs$, $hs$A developer fixes a `LazyInitializationException` by changing `Account.cards` to `FetchType.EAGER`. Why is that a poor fix?$hs$, $hs$- The cards are now loaded every time an account is loaded, by every use case, including those that never need them.
- It doesn't remove N+1: for queries returning many accounts, Hibernate often loads each account's cards with a separate query.
- It can't be turned off per query.

Keep the relation lazy, and fetch it where it's needed: a fetch join, an entity graph, or a DTO projection.$hs$, 5, 'SUBMITTED', $hs$Hibernate ORM User Guide (fetching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: JOIN FETCH with pagination$hs$, $hs$To avoid N+1, a developer adds `join fetch a.cards` to a paged query (`Pageable`, 20 accounts per page). It works in tests; in production the page takes seconds and memory spikes. Why, and how do you fix it?$hs$, $hs$A fetch join on a collection returns one row per card, so the database can't apply "20 accounts" as a `LIMIT`. Hibernate loads every matching row and paginates in memory, logging a warning ("firstResult/maxResults specified with collection fetch; applying in memory"). On test data that's a few rows; in production it's all of them.

Page the account ids first (`select a.id ... order by ... limit 20`), then load those accounts with their cards in a second query; or keep the collection lazy and use batch fetching.$hs$, 7, 'SUBMITTED', $hs$Hibernate ORM User Guide, Fetching https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#fetching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: two collections in one fetch join$hs$, $hs$A query fetch-joins two `List` collections of `Account`: `cards` and `standingOrders`. What happens?$hs$, $hs$Hibernate throws `MultipleBagFetchException`: it can't fetch two "bags" (`List`s without an order column, which may hold duplicates) in one query. Switching them to `Set`s makes the error go away, but the result is a Cartesian product: cards × standing orders rows per account.

Fetch one collection per query, or use batch fetching for the second one.$hs$, 6, 'SUBMITTED', $hs$Hibernate ORM User Guide, Fetching https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#fetching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: batch fetching for lazy collections$hs$, $hs$The overview loads 50 accounts and, for each one, its lazy `cards` and `standingOrders`: 101 queries per request. Which Hibernate setting brings this down to 3 queries without changing any query or mapping, and how does it do it?$hs$, $hs$```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=50
```

When the first account's lazy `cards` are touched, Hibernate loads the cards of up to 50 accounts in the persistence context at once, in one query by their ids, instead of only that account's. The same happens for `standingOrders`: 1 query for the accounts, 1 for all their cards, 1 for all their standing orders. `@BatchSize` does the same for a single association.$hs$, 5, 'SUBMITTED', $hs$Hibernate ORM User Guide, Batch fetching https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#fetching-batch$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: an entity graph on a repository method$hs$, $hs$`Account.cards` is a lazy `@OneToMany`. Write the repository method that loads one account by its IBAN together with its cards, in one query, without writing JPQL.$hs$, $hs$```java
interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = "cards")
    Optional<Account> findByIban(String iban);
}
```

The derived query finds the account by IBAN; the entity graph makes Hibernate fetch `cards` in the same query, with a left join, for this method only. The mapping stays lazy for every other use case.$hs$, 4, 'SUBMITTED', $hs$Spring Data JPA docs, JPA Query Methods (Using @EntityGraph) https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Fetching: why teams disable open-session-in-view$hs$, $hs$Why do many teams set `spring.jpa.open-in-view=false`, and what do they have to do instead?$hs$, $hs$- It hides N+1 queries: lazy relations quietly load while the controller's result is serialised, outside any review.
- It holds a database connection for the whole request, including JSON rendering, which shrinks the effective pool.

Instead, the service fetches everything a use case needs inside its transaction (fetch joins, entity graphs) and returns DTOs, not entities.$hs$, 6, 'SUBMITTED', $hs$Spring Boot docs, Data access (spring.jpa.open-in-view) https://docs.spring.io/spring-boot/how-to/data-access.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: what JpaRepository gives you$hs$, $hs$What do you get by declaring `interface AccountRepository extends JpaRepository<Account, Long> {}`?$hs$, $hs$An implementation generated at runtime by Spring Data, with:
- CRUD methods: `save`, `findById`, `findAll`, `deleteById`, `count`, ...
- paging and sorting: `findAll(Pageable)`, `findAll(Sort)`
- the ability to add query methods derived from their names, or written with `@Query`.$hs$, 1, 'SUBMITTED', $hs$Spring Data JPA docs, Core concepts https://docs.spring.io/spring-data/jpa/reference/repositories/core-concepts.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: derived query methods$hs$, $hs$Nobody writes an implementation or a query for this repository method. How does Spring Data JPA run it?

```java
List<Workout> findByAthleteIdAndDoneOnAfter(Long athleteId, LocalDate date);
```$hs$, $hs$Spring Data derives the query from the method name. It parses the name into a subject (`find...By`) and a predicate built from entity properties, keywords and operators: `AthleteId` and `DoneOn` are properties of `Workout`, `And` combines the conditions, and `After` means "greater than". The parameters are bound in order.

```java
List<Workout> findByAthleteIdAndDoneOnAfter(Long athleteId, LocalDate date);
// select w from Workout w where w.athlete.id = ?1 and w.doneOn > ?2
```

The query is created when the repository is initialised, so a name that refers to an unknown property makes the application fail at startup.$hs$, 1, 'SUBMITTED', $hs$Spring Data JPA docs, Defining Query Methods https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: Page vs Slice$hs$, $hs$What is the difference between a repository method returning `Page<Transfer>` and one returning `Slice<Transfer>`?$hs$, $hs$A `Page` knows the total number of elements and pages, so Spring Data runs an extra `count` query for it. A `Slice` only knows whether a next slice exists (it fetches one row more than requested), which avoids the count query: cheaper on big tables, enough for "load more" or infinite scrolling.$hs$, 3, 'SUBMITTED', $hs$Spring Data JPA docs, Defining Query Methods (paging) https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: JPQL vs native queries$hs$, $hs$When would you use `@Query(nativeQuery = true)` instead of a JPQL `@Query`, and what do you give up?$hs$, $hs$Use native SQL for database features JPQL doesn't support: window functions, `DISTINCT ON`, `jsonb` operators, upserts, recursive queries.

You give up portability across databases, and the automatic mapping of entities and relations: results map to entities only when the columns match exactly, otherwise to projections.$hs$, 4, 'SUBMITTED', $hs$Spring Data JPA docs, JPA Query Methods (@Query) https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: a bulk update and stale entities$hs$, $hs$When a customer reports a stolen wallet, all their accounts are frozen, and the frozen accounts are returned to show on the support screen. The screen still shows them as `ACTIVE`, although the rows in the database are `FROZEN`. Why, and how do you write the method cleanly?

```java
@Transactional
public List<Account> freezeAccounts(long customerId) {
    List<Account> accounts = accountRepository.findByCustomerId(customerId);
    accountRepository.freezeByCustomerId(customerId);   // @Modifying @Query("update Account a set a.status = 'FROZEN' where ...")
    return accounts;
}
```$hs$, $hs$A JPQL bulk `update` goes straight to the database and bypasses the persistence context: the `Account` entities loaded before it keep their old state, so the method returns them as `ACTIVE`.

The accounts are loaded anyway, so change them and let dirty checking write them: one code path, and no stale state.

```java
@Transactional
public List<Account> freezeAccounts(long customerId) {
    List<Account> accounts = accountRepository.findByCustomerId(customerId);
    accounts.forEach(Account::freeze);   // written at commit
    return accounts;
}
```

A bulk update is for many rows you don't need in memory. If you do use one next to loaded entities, `@Modifying(clearAutomatically = true)` clears the persistence context afterwards, and you load them again.$hs$, 5, 'SUBMITTED', $hs$Spring Data JPA docs, JPA Query Methods (Modifying Queries) https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: dynamic filters with Specifications$hs$, $hs$A transaction search has six optional filters (account, date range, minimum amount, currency, status, counterparty). How do you build the query without writing a repository method for every combination?$hs$, $hs$Combine `Specification`s (the JPA Criteria API) for the filters that are present:

```java
interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {}

Specification<Transaction> spec = (root, query, cb) -> cb.conjunction();   // matches everything
if (filter.currency() != null) {
    spec = spec.and((root, query, cb) -> cb.equal(root.get("currency"), filter.currency()));
}
if (filter.minAmount() != null) {
    spec = spec.and((root, query, cb) -> cb.ge(root.get("amount"), filter.minAmount()));
}
Page<Transaction> page = transactionRepository.findAll(spec, pageable);
```

Querydsl or query by example are alternatives. Never concatenate query strings.$hs$, 5, 'SUBMITTED', $hs$Spring Data JPA docs, Specifications https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: auditing$hs$, $hs$How do you have `createdAt`, `createdBy` and `lastModifiedAt` filled in automatically on every entity?$hs$, $hs$Spring Data auditing:

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @CreatedDate Instant createdAt;
    @CreatedBy String createdBy;           // from an AuditorAware<String> bean
    @LastModifiedDate Instant lastModifiedAt;
}
```

Enable it with `@EnableJpaAuditing`, and provide an `AuditorAware` bean that returns the current user, e.g. from Spring Security's context.$hs$, 3, 'SUBMITTED', $hs$Spring Data JPA docs, Auditing https://docs.spring.io/spring-data/jpa/reference/auditing.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: first-level vs second-level cache$hs$, $hs$What is the difference between Hibernate's first-level cache and its second-level cache?$hs$, $hs$- First-level cache: the persistence context itself, one per `EntityManager` (usually per transaction). Always on.
- Second-level cache: shared across all `EntityManager`s of the application, holding entity data between transactions. Optional, and needs a cache provider to be configured.$hs$, 2, 'SUBMITTED', $hs$Hibernate ORM User Guide (caching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Batching: JDBC batching$hs$, $hs$What does `spring.jpa.properties.hibernate.jdbc.batch_size=50` change when 10,000 new ledger entries are saved in one transaction?$hs$, $hs$Hibernate groups the `INSERT` statements into JDBC batches of 50, so 10,000 inserts take about 200 round trips to the database instead of 10,000.

It needs ids that don't require an immediate insert (sequence ids, not `IDENTITY`), and `hibernate.order_inserts=true` helps when several entity types are mixed.$hs$, 4, 'SUBMITTED', $hs$Hibernate ORM User Guide (batching) https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Repositories: findAll() on 10 million rows$hs$, $hs$A nightly job calls `transactionRepository.findAll()` on a table with 10 million rows. What happens, and what should it do instead?$hs$, $hs$It loads all 10 million rows into memory as managed entities, each with a snapshot for dirty checking, and runs out of memory, or crawls under garbage collection.

Process in chunks instead: keyset pages by id (`where id > :lastId order by id limit 1000`), each chunk in its own transaction or with `entityManager.clear()` between chunks. Or let the database do the work in set-based SQL.$hs$, 6, 'SUBMITTED', $hs$Spring Data JPA docs, JPA Query Methods (Streaming Query Results) https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: how @Version works$hs$, $hs$`Account` has a `@Version Long version` field. Which SQL does Hibernate run when a changed account is flushed, and how does it detect a concurrent change?$hs$, $hs$```sql
UPDATE account SET balance = ?, version = 8 WHERE id = ? AND version = 7
```

It writes the new version and requires the old one in the `WHERE` clause. If another transaction updated the row in the meantime, its version is no longer 7, no row matches, and Hibernate throws an optimistic locking exception instead of overwriting the other change.$hs$, 3, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: the optimistic locking exception$hs$, $hs$Two requests update the same versioned account concurrently. Which exception reaches the service layer for the loser, and when is it thrown?$hs$, $hs$JPA's `OptimisticLockException`, translated by Spring into `ObjectOptimisticLockingFailureException` (a subclass of `OptimisticLockingFailureException`).

It's thrown when the update is flushed, usually at commit, not when `setBalance` is called.$hs$, 4, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: retrying an optimistic locking failure$hs$, $hs$Retry `applyInterest` up to three times when an optimistic locking failure occurs. Where must the retry happen, and why?$hs$, $hs$Outside the transaction: each attempt must be a new transaction that reloads the account with its current version. Retrying inside the failed transaction can't succeed, since it's marked for rollback and holds stale state.

```java
@Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3)
public void applyInterestWithRetry(long accountId) {
    interestService.applyInterest(accountId);   // another bean: @Transactional starts fresh each time
}
```

Without Spring Retry: a loop around a call to the transactional method on another bean, or a `TransactionTemplate` inside a loop.$hs$, 6, 'SUBMITTED', $hs$Spring Retry https://github.com/spring-projects/spring-retry$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: pessimistic locks with Spring Data$hs$, $hs$Write a repository method that loads an account and locks its row until the end of the transaction, so a concurrent transfer has to wait.$hs$, $hs$```java
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);   // SELECT ... FOR UPDATE
}
```

It must be called inside a transaction; the lock is released at commit or rollback. To lock two accounts without deadlocks, always lock the lower id first.$hs$, 5, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: optimistic vs pessimistic$hs$, $hs$Two coaches edit the same `Workout` at the same time, and one coach's change silently overwrites the other's (a lost update). How does optimistic locking prevent this in Spring Data JPA, and how does pessimistic locking?$hs$, $hs$Optimistic locking detects the conflict when writing; pessimistic locking prevents it by locking the row when reading.

Optimistic: a `@Version` column. Each update checks and increments it, and fails if another transaction changed the row since it was read. Spring then throws `ObjectOptimisticLockingFailureException`.

```java
@Entity
class Workout {
    @Id Long id;
    @Version Long version;
}
```

Pessimistic: read the row with `SELECT ... FOR UPDATE`. Other writers wait until the transaction ends.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select w from Workout w where w.id = :id")
Optional<Workout> findForUpdate(@Param("id") Long id);
```

- Optimistic suits rare conflicts and edits that span user think-time.
- Pessimistic suits frequent conflicts where retrying is expensive.$hs$, 4, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: a flash sale$hs$, $hs$10,000 users try to buy the last 100 discounted gym memberships at the same moment. The remaining count is a column of one `offer` row. Should the purchase use optimistic locking on that row, or a pessimistic lock?$hs$, $hs$Neither works well on one hot row. With optimistic locking, almost every transaction fails on the version check and retries: a retry storm. A pessimistic lock serialises them, holding the lock for each whole transaction.

Use a single atomic conditional update, which holds the row lock only for that statement:

```sql
UPDATE offer SET remaining = remaining - 1 WHERE id = :id AND remaining > 0;   -- 1 row: sold; 0 rows: sold out
```$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, UPDATE https://www.postgresql.org/docs/current/sql-update.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Locking: optimistic locking across two HTTP requests$hs$, $hs$`Workout` has a `@Version` field. Two coaches open the same workout's edit form, both change it, and both press Save; the second save silently overwrites the first. Why doesn't optimistic locking catch it here?

```java
@PutMapping("/workouts/{id}")
@Transactional
public void update(@PathVariable long id, @RequestBody WorkoutForm form) {
    Workout workout = workouts.findById(id).orElseThrow();
    workout.setName(form.name());
    workout.setExercises(form.exercises());
}
```$hs$, $hs$The version is checked against what this transaction loaded, and each save loads the workout fresh, at its current version. The second coach's request reads the version the first save just wrote, so its update matches and succeeds. The version the coach saw when opening the form is never compared.

Send the version to the client with the form and back with the save, and compare it before changing anything:

```java
if (workout.getVersion() != form.version()) {
    throw new ObjectOptimisticLockingFailureException(Workout.class, id);   // answered as 409 Conflict
}
```

In a REST API the same idea is an `ETag` holding the version, and an `If-Match` header on the update (412 if it doesn't match).$hs$, 7, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: Spring's transaction abstraction$hs$, $hs$What does Spring's transaction abstraction give you?$hs$, $hs$One programming model, `@Transactional` or `TransactionTemplate`, over different transactional resources (JDBC, JPA, JMS and reactive drivers). A `PlatformTransactionManager` implementation does the work for the resource in use, so business code doesn't change when the persistence technology does.$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Transaction Management https://docs.spring.io/spring-framework/reference/data-access/transaction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: where @Transactional goes$hs$, $hs$In a controller → service → repository application, where should `@Transactional` be placed for a money transfer?$hs$, $hs$On the service method that implements the use case, `TransferService.transfer(...)`, because that's the unit of work: debiting, crediting and recording the transfer must succeed or fail together.

Not on the controller (it shouldn't know about transactions), and not only on the repositories (each call would get its own transaction, so a failure in the middle leaves half a transfer).$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: how @Transactional works$hs$, $hs$What happens, step by step, when another bean calls a `@Transactional` method of `TransferService`?$hs$, $hs$1. The caller holds a proxy of `TransferService`, not the object itself; the call goes to the proxy.
2. The proxy's transaction interceptor asks the `PlatformTransactionManager` to start a transaction (or join an existing one), which binds a connection to the current thread.
3. The proxy calls the real method; repositories called inside use the thread's connection.
4. When the method returns, the interceptor commits; if it throws an unchecked exception, it rolls back.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Declarative Transaction Implementation https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-decl-explained.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: one transaction across repositories$hs$, $hs$`TransferService.transfer` calls `accountRepository` and `transferRepository`. How do both end up in the same database transaction?$hs$, $hs$The transaction manager binds the transaction's resource (the JDBC connection, or the `EntityManager`) to the current thread when the transaction starts. Every data access call on that thread looks the resource up there (through `TransactionSynchronizationManager`) and uses it, so both repositories work on the same connection and transaction.

That's also why the transaction doesn't extend to other threads.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Strategy https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: transaction managers$hs$, $hs$What is the difference between `JdbcTransactionManager` (formerly `DataSourceTransactionManager`) and `JpaTransactionManager`?$hs$, $hs$- `JdbcTransactionManager` manages a transaction on a JDBC connection from a `DataSource`: for `JdbcTemplate` and `JdbcClient`.
- `JpaTransactionManager` manages a JPA `EntityManager` and its underlying connection, and also lets JDBC code on the same `DataSource` join the transaction.

Spring Boot configures the right one for the starters on the classpath.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Strategy https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: where @Transactional doesn't apply$hs$, $hs$In which situations is a method's `@Transactional` annotation silently ignored with Spring's default proxy mode?$hs$, $hs$- The method is called on `this` from another method of the same bean (self-invocation): the call doesn't go through the proxy.
- The method is `private` (or `final`, with class-based proxies): the proxy can't override it.
- The object isn't a Spring bean (created with `new`).
- The call happens during initialisation, e.g. from `@PostConstruct`, before the proxy is fully in place.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: @Transactional and self-invocation$hs$, $hs$`importAll` should save each transfer in its own transaction, so one bad transfer doesn't undo the others. Instead, a failing transfer leaves its ledger entries half saved. Why, and how do you fix it?

```java
@Service
class TransferImportService {
    public void importAll(List<TransferDto> batch) {
        for (TransferDto dto : batch) {
            try {
                importOne(dto);
            } catch (RuntimeException e) {
                log.warn("Skipping transfer {}", dto.id(), e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importOne(TransferDto dto) {
        Transfer transfer = transfers.save(toTransfer(dto));
        entries.saveAll(toEntries(transfer, dto));   // may throw
    }
}
```$hs$, $hs$`importOne` is called on `this`, not through the Spring proxy, so its `@Transactional` is never applied. Each repository call then runs in its own transaction, and a failing `saveAll` can't roll back the transfer already saved.

Move `importOne` into another bean, so the call goes through that bean's proxy:

```java
@Service
class TransferImportService {
    private final TransferImporter importer;   // another bean

    public void importAll(List<TransferDto> batch) {
        for (TransferDto dto : batch) {
            try {
                importer.importOne(dto);       // through the proxy: a new transaction each time
            } catch (RuntimeException e) {
                log.warn("Skipping transfer {}", dto.id(), e);
            }
        }
    }
}

@Service
class TransferImporter {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importOne(TransferDto dto) { ... }
}
```$hs$, 6, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: readOnly = true$hs$, $hs$A service method only reads data, for example a customer's orders, with Spring Data JPA. What does annotating it with `@Transactional(readOnly = true)` do?$hs$, $hs$It declares the transaction as read-only, a hint that the transaction manager passes on to the underlying resources:
- Hibernate skips dirty checking and flushing for the session, which saves work when many entities are loaded.
- The JDBC connection is marked read-only, which some databases and drivers optimise, and which a routing data source can use to send the query to a replica.

It is an optimisation hint, not a security mechanism: it does not reliably prevent writes.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: isolation and timeout$hs$, $hs$A `@Transactional(isolation = Isolation.SERIALIZABLE, timeout = 5)` method is called from another method that already runs in a Read Committed transaction. Which isolation level and timeout apply?$hs$, $hs$The outer transaction's: Read Committed and its timeout. With the default propagation `REQUIRED`, the method joins the existing transaction, and `isolation` and `timeout` are only applied when a new transaction is started; when joining, they're ignored.

(The transaction manager can be set to reject such mismatches with `validateExistingTransaction`.)$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional (settings) https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: a 40-minute report$hs$, $hs$A monthly regulatory report runs as one `@Transactional` method for 40 minutes. Since it was introduced, the database grows faster and other queries slow down while the report runs. Why?$hs$, $hs$The transaction keeps its snapshot for 40 minutes, so vacuum can't remove any row version that became dead after it started: busy tables such as `account` bloat, and their queries slow down. The report also holds a connection the whole time.

Read in chunks, each in its own short transaction, run the report against a read replica, or build it from data aggregated during the month.$hs$, 7, 'SUBMITTED', $hs$PostgreSQL docs, Routine Vacuuming https://www.postgresql.org/docs/current/routine-vacuuming.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: an after-commit email that gets lost$hs$, $hs$An after-commit `@TransactionalEventListener` sends the transfer receipt by email. Sometimes the receipt never arrives. Why, and what's more reliable?$hs$, $hs$The listener runs in memory after the commit. If the mail server is down, the listener throws and nothing retries; if the process crashes between the commit and the listener, the event is gone.

For guaranteed delivery, write the intent ("send receipt for transfer 42") to an outbox table in the same transaction as the transfer, and have a separate process send it, retrying until it succeeds.$hs$, 6, 'SUBMITTED', $hs$Microservices.io, Pattern: Transactional outbox https://microservices.io/patterns/data/transactional-outbox.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: the transfer service in Spring$hs$, $hs$Write the Spring service method that transfers money between two accounts in one database: it must never overdraw, never deadlock with an opposite transfer, and never execute the same request twice. `accounts.findByIdForUpdate` locks the row (`PESSIMISTIC_WRITE`), and `transfer.request_id` is unique.$hs$, $hs$```java
@Transactional
public TransferResult transfer(TransferRequest request) {
    Optional<Transfer> done = transfers.findByRequestId(request.requestId());
    if (done.isPresent()) return TransferResult.of(done.get());          // a retry: return the original

    long firstId = Math.min(request.from(), request.to());
    long secondId = Math.max(request.from(), request.to());
    Account first = accounts.findByIdForUpdate(firstId).orElseThrow();  // lock in id order: no deadlock
    Account second = accounts.findByIdForUpdate(secondId).orElseThrow();
    Account from = first.getId() == request.from() ? first : second;
    Account to = from == first ? second : first;

    from.debit(request.amount());                                       // throws if insufficient funds
    to.credit(request.amount());
    Transfer transfer = transfers.save(Transfer.of(request));
    ledger.saveAll(List.of(Entry.debit(transfer, from), Entry.credit(transfer, to)));
    return TransferResult.of(transfer);
}
```

- One transaction: both balances, the transfer and its ledger entries commit together.
- Two concurrent copies of the same request both pass the first check; the unique `request_id` makes the second fail at insert, and its retry then finds the original.
- No remote calls inside the transaction.$hs$, 8, 'SUBMITTED', $hs$Spring Data JPA docs, Locking https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: a page and its total from two snapshots$hs$, $hs$A paged list runs two queries inside one `@Transactional(readOnly = true)` method on PostgreSQL with default settings. While athletes keep logging workouts, users sometimes see a total that doesn't match the pages. Why, given that both queries run in one transaction?

```java
@Transactional(readOnly = true)
public WorkoutPage page(int page, int size) {
    List<Workout> items = workoutRepository.findPage(page, size);   // ... ORDER BY id DESC LIMIT ? OFFSET ?
    long total = workoutRepository.count();                         // SELECT count(*) FROM workout
    return new WorkoutPage(items, total);
}
```$hs$, $hs$PostgreSQL's default isolation level is **READ COMMITTED**, at which each statement sees a new snapshot of the data: everything committed before that statement started. Sharing a transaction doesn't give the two queries the same view, so a workout committed between them is counted in the total but missing from the page.

Two fixes:
- Run the method at REPEATABLE READ, where every statement of the transaction sees one snapshot, taken at its first query: `@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)`.
- Or get both from one statement, e.g. `count(*) OVER ()` next to the page's columns (though a page past the end then returns no rows, and so no total).$hs$, 6, 'SUBMITTED', $hs$Spring Framework docs, Using @Transactional (isolation) https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Rollback: which exceptions roll back$hs$, $hs$By default, which exceptions thrown from a `@Transactional` method make Spring roll the transaction back?$hs$, $hs$Unchecked exceptions: `RuntimeException` and its subclasses, and `Error`. A checked exception does not roll back by default; the transaction commits.

`rollbackFor` changes that: `@Transactional(rollbackFor = IOException.class)`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Rolling Back a Declarative Transaction https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Rollback: a checked exception commits$hs$, $hs$`archive.store` throws an `IOException`. Is the account closed in the database afterwards?

```java
@Transactional
public void closeAccount(long id) throws IOException {
    accountRepository.markClosed(id);
    archive.store(statementFor(id));   // throws IOException
}
```$hs$, $hs$Yes. `IOException` is a checked exception, and by default Spring commits when a checked exception leaves the method: the account is closed although archiving failed.

```java
@Transactional(rollbackFor = IOException.class)
public void closeAccount(long id) throws IOException { ... }
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Rolling Back a Declarative Transaction https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Rollback: catching the exception inside the method$hs$, $hs$`fees.charge` throws a `RuntimeException`, which is caught. Is the transfer committed?

```java
@Transactional
public void transfer(TransferRequest request) {
    transfers.save(toTransfer(request));
    try {
        fees.charge(request);          // a plain object, not a transactional bean; throws
    } catch (RuntimeException e) {
        log.warn("Fee not charged", e);
    }
}
```$hs$, $hs$Yes. The exception never leaves the transactional method, so Spring sees a normal return and commits: the transfer is saved without its fee.

Catching an exception is a business decision to carry on; if the transfer must not happen without the fee, let the exception propagate.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Rolling Back a Declarative Transaction https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Rollback: an inner exception caught by the outer method$hs$, $hs$`TransferService.transfer` is `@Transactional` and calls `FeeService.charge`, another bean's `@Transactional` method (propagation `REQUIRED`), which throws a `RuntimeException`. `transfer` catches it and returns normally. What happens at the end of `transfer`?$hs$, $hs$The commit fails with `UnexpectedRollbackException`, and nothing is saved.

`charge` joined the outer transaction. When the exception left `charge` through its proxy, the proxy marked the shared transaction rollback-only. `transfer` caught the exception, but the transaction is still marked, so Spring rolls back instead of committing, and throws `UnexpectedRollbackException` to say so.

If the fee may fail independently, run `charge` with `REQUIRES_NEW`; otherwise let the exception propagate.$hs$, 7, 'SUBMITTED', $hs$Spring Framework docs, Transaction Propagation (Understanding PROPAGATION_REQUIRED) https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Propagation: what REQUIRED means$hs$, $hs$What does the default propagation, `Propagation.REQUIRED`, mean?$hs$, $hs$`Propagation.REQUIRED` means that the method must run within a transaction: it joins the transaction already active on the current thread, if there is one, and otherwise starts a new one. When it joins, the caller and the method share one transaction, so they commit or roll back together.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Transaction Propagation https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Propagation: SUPPORTS, NOT_SUPPORTED, MANDATORY and NEVER$hs$, $hs$What do the propagation settings `SUPPORTS`, `NOT_SUPPORTED`, `MANDATORY` and `NEVER` do?$hs$, $hs$- `SUPPORTS`: run in the current transaction if there is one; otherwise run without one.
- `NOT_SUPPORTED`: suspend any current transaction and run without one.
- `MANDATORY`: require a current transaction; throw an exception if there's none.
- `NEVER`: require that there's no transaction; throw an exception if there is one.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Propagation https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: TransactionTemplate$hs$, $hs$What is `TransactionTemplate`?$hs$, $hs$A helper that runs a piece of code inside a transaction, without an annotation or a proxy:

```java
transactionTemplate.executeWithoutResult(status -> {
    accounts.debit(from, amount);
    accounts.credit(to, amount);
});   // commits when the callback returns, rolls back if it throws
```

Its settings (propagation, isolation, timeout, read-only) are configured on the template.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Programmatic Transaction Management https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: when programmatic transactions fit better$hs$, $hs$`pay` calls a slow card provider, then saves the result. Only the saving must be transactional: the provider call must not hold a database connection or a transaction open. Write `pay` with `TransactionTemplate`, and say why splitting it into a second `@Transactional` method of the same class doesn't work.

```java
public void pay(Card card, Money amount) {
    PaymentResult result = provider.charge(card, amount);   // slow: 1 to 3 seconds
    payments.save(Payment.from(result));
    ledger.record(result);
}
```$hs$, $hs$```java
public void pay(Card card, Money amount) {
    PaymentResult result = provider.charge(card, amount);   // outside any transaction
    transactionTemplate.executeWithoutResult(status -> {     // the transaction covers only the writes
        payments.save(Payment.from(result));
        ledger.record(result);
    });
}
```

A second `@Transactional` method called from `pay` is a self-invocation: the call doesn't go through the proxy, so no transaction starts. `TransactionTemplate` needs no proxy, and draws the boundary exactly around the writes.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Programmatic Transaction Management https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: why they should be short$hs$, $hs$Why should database transactions be kept short?$hs$, $hs$- A transaction holds a connection from the pool for its whole duration; long ones exhaust the pool.
- It holds its row locks until it ends, so other transactions wait (on a busy account, everyone waits).
- In PostgreSQL, an old open transaction stops vacuum from cleaning up dead rows, so tables bloat.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Management https://docs.spring.io/spring-framework/reference/data-access/transaction.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: running code after commit$hs$, $hs$After a transfer commits, a confirmation must be sent, but never for a transfer that rolled back. How do you run code only after a successful commit?$hs$, $hs$Publish an event inside the transaction, and handle it with a transactional event listener, which by default runs after commit:

```java
@Transactional
public void transfer(TransferRequest request) {
    ...
    events.publishEvent(new TransferCompleted(transferId));
}

@TransactionalEventListener   // phase = AFTER_COMMIT by default; not called on rollback
void sendConfirmation(TransferCompleted event) { ... }
```$hs$, 3, 'SUBMITTED', $hs$Spring Framework API, TransactionalEventListener https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/event/TransactionalEventListener.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: async work inside a transaction$hs$, $hs$Inside a `@Transactional` method, `CompletableFuture.runAsync(() -> auditRepository.save(entry))` is called. Is that save part of the method's transaction?$hs$, $hs$No. The transaction is bound to the thread that started it; the async code runs on another thread, outside it, in its own transaction (or none). It may even run before the method's transaction commits, and it's not rolled back if the method fails.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Transaction Strategy https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: an @Async method that can't find the row$hs$, $hs$Right after saving a transfer, the service calls an `@Async` method that loads the transfer by id to send a receipt. Sometimes it finds nothing. Why, and how do you fix it?$hs$, $hs$The `@Async` method runs on another thread, in its own transaction, and can start before the caller's transaction has committed: the new transfer isn't visible to it yet.

Trigger the async work after the commit: from a `@TransactionalEventListener` (after commit), possibly itself `@Async`, instead of calling it inside the transaction.$hs$, 5, 'SUBMITTED', $hs$Spring Framework API, TransactionalEventListener https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/event/TransactionalEventListener.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: XA distributed transactions$hs$, $hs$What is a distributed (XA) transaction?$hs$, $hs$One transaction spanning several transactional resources, such as two databases, or a database and a message broker, that commit or roll back together. A JTA transaction manager coordinates them with two-phase commit: every resource first prepares, then all commit.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Strategy (JTA) https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Transactions: what a @Transactional test can hide$hs$, $hs$Integration tests annotated `@Transactional` roll back at the end of each test. What kinds of bugs can that hide?$hs$, $hs$- Nothing is committed, and often not even flushed, so constraint violations and SQL errors that occur at flush or commit never show up.
- `@TransactionalEventListener`s (after commit) never run.
- Lazy loading always works, because the whole test runs inside one open transaction, unlike production code, where the transaction ends with the service method.

Flush explicitly in such tests, or test against committed data and clean up afterwards.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Transaction Management in the TestContext Framework https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data', 'spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Propagation: REQUIRES_NEW vs REQUIRED$hs$, $hs$`finishWorkout` runs in a transaction and calls `AuditLog.record`, which uses `Propagation.REQUIRES_NEW`. Then `leaderboard.update` throws a `RuntimeException`. After `finishWorkout` fails, which of its two writes remains in the database?

```java
@Service
class WorkoutService {
    @Transactional
    public void finishWorkout(long workoutId) {
        workouts.markFinished(workoutId);
        auditLog.record("workout " + workoutId + " finished");
        leaderboard.update(workoutId);   // throws a RuntimeException
    }
}

@Service
class AuditLog {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String event) {
        auditEntries.save(new AuditEntry(event));
    }
}
```$hs$, $hs$Only the audit entry. The workout is not marked finished.

```java
@Transactional
public void finishWorkout(long workoutId) {
    workouts.markFinished(workoutId);                        // rolled back with finishWorkout's transaction
    auditLog.record("workout " + workoutId + " finished");  // committed in its own transaction
    leaderboard.update(workoutId);                           // throws: finishWorkout's transaction rolls back
}
```

- `REQUIRES_NEW` suspends the caller's transaction and runs `record` in a new, independent one, which commits when `record` returns. The later rollback can't undo it.
- With `REQUIRED`, `record` joins `finishWorkout`'s transaction, so the audit entry is rolled back together with the workout.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Transaction Propagation https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Propagation: REQUIRES_NEW and an exhausted pool$hs$, $hs$Under load, a service whose `@Transactional` method calls a `REQUIRES_NEW` method freezes, with every request waiting for a database connection. Why?$hs$, $hs$Each request holds one connection for its outer transaction and needs a second one for the `REQUIRES_NEW` transaction. With a pool of 10 and 10 concurrent requests, every request holds one connection and waits for a second that nobody will release: a pool deadlock, until the connection timeout fires.

Avoid new nested transactions on busy paths, or size the pool for two connections per concurrent request.$hs$, 7, 'SUBMITTED', $hs$HikariCP wiki, About Pool Sizing https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Security basics: authentication vs authorization$hs$, $hs$What is the difference between authentication and authorization?$hs$, $hs$- Authentication verifies who the caller is: a customer signing in, a service presenting a token.
- Authorization decides what that caller may do: whether this customer may see account 42 or make this transfer.

Authentication comes first; authorization is checked on every action.$hs$, 1, 'SUBMITTED', $hs$OWASP Authorization Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sessions: server-side sessions vs JWT access tokens$hs$, $hs$Compare server-side sessions with self-contained tokens such as JWTs for keeping a user signed in.$hs$, $hs$- Server-side session: the server stores the session state and the browser holds only an opaque session id in a cookie. Easy to revoke (delete the session), but every instance needs the session store (sticky sessions or shared storage such as Redis).
- Self-contained token: signed claims (who, roles, expiry) that any instance can verify without a lookup. Scales easily, but can't be revoked before it expires, so it must be short-lived.$hs$, 2, 'SUBMITTED', $hs$OWASP Session Management Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Security basics: least privilege in a Spring service$hs$, $hs$What is the principle of least privilege? Give three ways to apply it in a Spring Boot payment service.$hs$, $hs$The principle of least privilege states that every user, service and process should be granted only the access it needs to perform its function, and nothing more. It limits the damage a mistake or a compromised account can do.

Applied to a Spring Boot payment service:
- Fine-grained authorities, checked with Spring Security: a support agent may view payments but not approve them.
- A database user with only the privileges the service needs: no DDL rights, and read-only users for reporting.
- Tokens with minimal scopes, and admin and Actuator endpoints not exposed publicly.$hs$, 3, 'SUBMITTED', $hs$OWASP Authorization Cheat Sheet, Enforce Least Privileges (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: how Spring Security plugs into Spring MVC$hs$, $hs$Where does Spring Security sit in a Spring MVC application, and through which components does a request pass?$hs$, $hs$In front of the `DispatcherServlet`, as a servlet filter:

1. `DelegatingFilterProxy`, a standard servlet filter, hands the request to a Spring bean,
2. the `FilterChainProxy`, which picks the matching `SecurityFilterChain`,
3. whose security filters (authentication, CSRF, exception translation, authorization…) run in order.

Only if they let the request through does it reach the `DispatcherServlet` and your controller.$hs$, 2, 'SUBMITTED', $hs$Spring Security docs, Architecture https://docs.spring.io/spring-security/reference/servlet/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: an unauthenticated request to a protected endpoint$hs$, $hs$A request without credentials reaches `GET /accounts/42`, which requires authentication. Walk through what Spring Security does.$hs$, $hs$1. The authentication filters find no credentials, so the request continues as anonymous.
2. The `AuthorizationFilter` denies access and throws an `AccessDeniedException`.
3. The `ExceptionTranslationFilter` catches it; since the user isn't authenticated, it starts authentication: it calls the `AuthenticationEntryPoint`.
4. The entry point answers: `401` with a `WWW-Authenticate` header for an API, or a redirect to the login page for a web app.

The controller is never called.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, Architecture (Handling Security Exceptions) https://docs.spring.io/spring-security/reference/servlet/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: SecurityContext and SecurityContextHolder$hs$, $hs$What are the `SecurityContext` and the `SecurityContextHolder`?$hs$, $hs$- `SecurityContext`: holds the current `Authentication`: the principal (who), the credentials and the granted authorities.
- `SecurityContextHolder`: where Spring Security stores the context. By default in a `ThreadLocal`, so any code on the request's thread can read the current user:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
```$hs$, 2, 'SUBMITTED', $hs$Spring Security docs, Servlet Authentication Architecture https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: AuthenticationManager, providers and UserDetailsService$hs$, $hs$What do `AuthenticationManager`, `AuthenticationProvider` and `UserDetailsService` each do during a username and password login?$hs$, $hs$- `AuthenticationManager` (usually a `ProviderManager`): the entry point; it delegates to the providers until one can authenticate this kind of credential.
- `AuthenticationProvider`: authenticates one kind of credential. For username and password, the `DaoAuthenticationProvider`.
- `UserDetailsService`: loads the user (stored password hash, authorities, account status) by username. The provider then checks the password with the `PasswordEncoder`.

On success, the resulting `Authentication` is stored in the `SecurityContext`.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, Servlet Authentication Architecture https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: security with only the Boot starter$hs$, $hs$You add `spring-boot-starter-security` to a web application and configure nothing else. What happens?$hs$, $hs$- Every endpoint requires authentication, Actuator included.
- An in-memory user named `user` is created, with a random password printed in the startup log (for development only).
- Login works with a form login page or HTTP Basic, depending on the request.
- Security defaults are on: CSRF protection, security headers, session fixation protection.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Spring Security https://docs.spring.io/spring-boot/reference/web/spring-security.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: a SecurityFilterChain for a payments API$hs$, $hs$Write the security configuration for a payments API: `/actuator/health` is public, `/admin/**` requires the role `ADMIN`, every other request must be authenticated, and requests carry JWT access tokens.$hs$, $hs$```java
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

A `SecurityFilterChain` bean built with the lambda DSL; `WebSecurityConfigurerAdapter` no longer exists. Specific rules first, `anyRequest()` last.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, Authorize HttpServletRequests https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Authorization: a broad rule declared first$hs$, $hs$With this configuration, any signed-in athlete can call `DELETE /api/admin/athletes/7`. Why, and how do you fix it?

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/**").authenticated()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().denyAll());
```$hs$, $hs$Rules are checked in the order they are declared, and only the first match applies. `/api/admin/athletes/7` already matches `/api/**`, so only `authenticated()` is checked; the `ADMIN` rule is never reached.

Declare the specific rule first:

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/**").authenticated()
        .anyRequest().denyAll());
```$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, Authorize HttpServletRequests https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: different security for the API and the admin console$hs$, $hs$One application serves `/api/**`, called with JWT access tokens, and `/admin/**`, an internal console with form login. How do you give each its own security configuration?$hs$, $hs$Two `SecurityFilterChain` beans, each limited with `securityMatcher` and ordered with `@Order`. The `FilterChainProxy` uses the first chain whose matcher fits the request.

```java
@Bean @Order(1)
SecurityFilterChain api(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
}

@Bean @Order(2)
SecurityFilterChain admin(HttpSecurity http) throws Exception {
    http.securityMatcher("/admin/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
        .formLogin(Customizer.withDefaults());
    return http.build();
}
```$hs$, 5, 'SUBMITTED', $hs$Spring Security docs, Architecture (SecurityFilterChain) https://docs.spring.io/spring-security/reference/servlet/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Architecture: getting the current user in a controller$hs$, $hs$In a resource server that accepts JWTs, how does the `GET /me/accounts` controller method get the current customer's id without touching `SecurityContextHolder`?$hs$, $hs$Let Spring MVC inject it:

```java
@GetMapping("/me/accounts")
List<AccountDto> myAccounts(@AuthenticationPrincipal Jwt jwt) {
    return accounts.findByCustomer(jwt.getSubject());
}
```

Alternatively, declare an `Authentication` (or `JwtAuthenticationToken`) parameter. The customer id comes from the verified token, never from a request parameter.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, OAuth 2.0 Resource Server JWT https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Passwords: how to store them$hs$, $hs$How must an application store users' passwords?$hs$, $hs$Hashed with a slow, salted, adaptive password-hashing algorithm: Argon2id, scrypt or bcrypt (PBKDF2 where FIPS compliance is required).

Never in plain text, never encrypted (encryption can be reversed), and never with fast hashes like MD5 or SHA-256.$hs$, 2, 'SUBMITTED', $hs$OWASP Password Storage Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Passwords: why not SHA-256$hs$, $hs$An athlete's password is stored like this. Why is it unsafe, and how should a Spring application store it instead?

```java
String stored = DigestUtils.sha256Hex(rawPassword);
athleteRepository.save(new Athlete(email, stored));
```$hs$, $hs$SHA-256 is designed to be fast, so a leaked hash can be brute-forced at billions of guesses per second. There's also no salt, so equal passwords get equal hashes, and precomputed tables work.

Use a slow, salted password-hashing function through Spring Security's `PasswordEncoder`:

```java
String stored = passwordEncoder.encode(rawPassword);   // e.g. "{bcrypt}$2a$10$..."
athleteRepository.save(new Athlete(email, stored));

boolean ok = passwordEncoder.matches(rawPassword, stored);   // at sign-in
```$hs$, 4, 'SUBMITTED', $hs$OWASP Password Storage Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Passwords: the bcrypt work factor$hs$, $hs$What does the strength argument of `new BCryptPasswordEncoder(12)` control, and how do you choose it?$hs$, $hs$The work factor: the number of hashing rounds is 2 to the power of the strength, so each step up doubles the time to compute one hash, for the attacker as well as for you. The default is 10.

Choose the highest value your servers can afford at login: Spring Security's documentation suggests tuning it so that verifying a password takes about one second on your system. Increase it over time as hardware gets faster.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, Password Storage (BCryptPasswordEncoder) https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Login: the error message for a failed sign-in$hs$, $hs$A login endpoint answers "No account with this email" or "Wrong password", depending on what failed. What's the problem with these messages, and what should it answer instead?$hs$, $hs$The two messages enable **user enumeration**: an attacker can find out which email addresses have accounts, and then target those accounts with password guessing or phishing.

The endpoint should give the same generic answer in both cases, for example "Invalid email or password", with the same status code and, ideally, a similar response time. The same rule applies to password reset ("If an account exists, we've sent an email") and to registration.$hs$, 3, 'SUBMITTED', $hs$OWASP Authentication Cheat Sheet, Authentication and Error Messages (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Login: defending against credential stuffing$hs$, $hs$Attackers are trying email and password pairs leaked from other sites against the bank's login endpoint. Which defences do you put in place?$hs$, $hs$- Multi-factor authentication: the most effective defence, since a correct password alone is no longer enough.
- Rate limiting per account and per IP address, with increasing delays or temporary lockouts.
- Rejecting passwords known from breaches, at registration and password change.
- Generic error messages, so attackers can't tell which accounts exist.
- Monitoring and alerting on failed-login spikes, and bot detection where appropriate.$hs$, 5, 'SUBMITTED', $hs$OWASP Credential Stuffing Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Credential_Stuffing_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sessions: session fixation$hs$, $hs$What is a session fixation attack, and what does Spring Security do against it by default?$hs$, $hs$The attacker obtains a session id from the site, gets the victim's browser to use it (e.g. through a crafted link), and waits for the victim to sign in. If the session id stays the same after login, the attacker's copy is now an authenticated session.

Spring Security changes the session id when the user authenticates (`changeSessionId`, the default on current servlet containers), so the id the attacker knows becomes worthless.$hs$, 3, 'SUBMITTED', $hs$Spring Security docs, Authentication Persistence and Session Management (session fixation) https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Sessions: signing a user out everywhere$hs$, $hs$Sessions live in Spring Session JDBC for 14 days, and the principal stored at sign-in holds the user's role. When an admin blocks someone or changes their role, all of that person's sessions must end at once, on every device. A developer deletes them inside the `@Transactional` service method. Name two problems with this, and how to do it right.

```java
@Transactional
public void block(long userId) {
    users.updateStatus(userId, Status.BLOCKED);
    sessions.findByPrincipalName(String.valueOf(userId)).keySet()
            .forEach(sessions::deleteById);
}
```$hs$, $hs$`FindByIndexNameSessionRepository` finds a user's sessions by an index on the principal name, so this works only if `Authentication.getName()` returns the stable user id used here. Two problems remain:

1. **It runs before the commit.** If the transaction rolls back, the user was signed out for nothing. Worse, a sign-in running concurrently still reads the old, uncommitted status and can create a fresh session after the deletion. End the sessions **after commit**: after the transactional call returns, or in a `@TransactionalEventListener(phase = AFTER_COMMIT)`.
2. **The current request's session can't be deleted underneath it.** When users act on themselves (deleting their own account), Spring Session writes the request's session back when the request ends. That session must be ended with `request.getSession().invalidate()` instead.

Even then, a sign-in that read the user just before the commit can finish just after the deletion. Closing that gap needs a check on each request, such as comparing a per-user security version stored in the session.$hs$, 7, 'SUBMITTED', $hs$Spring Session docs, Find by Username https://docs.spring.io/spring-session/reference/guides/boot-findbyusername.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: OAuth 2.0 vs OpenID Connect$hs$, $hs$What is OAuth 2.0 for, and what does OpenID Connect add to it?$hs$, $hs$- **OAuth 2.0** is an authorization framework for delegated access: a client application obtains an access token that allows it to call an API on a user's behalf, without ever seeing the user's password. It does not define how to authenticate the user.
- **OpenID Connect** is an identity layer on top of OAuth 2.0 that adds authentication: alongside the access token, the client receives an ID token, a signed JWT stating who the user is and how and when they signed in.$hs$, 1, 'SUBMITTED', $hs$OpenID Connect Core 1.0 https://openid.net/specs/openid-connect-core-1_0.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: the four roles$hs$, $hs$Name OAuth 2.0's four roles, using a budgeting app that reads a customer's bank transactions as the example.$hs$, $hs$- Resource owner: the customer, who owns the transactions and grants access.
- Client: the budgeting app, which wants to read them.
- Authorization server: the bank's identity provider, which authenticates the customer, asks for consent and issues tokens.
- Resource server: the bank's transactions API, which accepts the access token.$hs$, 2, 'SUBMITTED', $hs$RFC 6749 The OAuth 2.0 Authorization Framework, §1.1 Roles https://www.rfc-editor.org/rfc/rfc6749$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: the authorization code flow with PKCE$hs$, $hs$Walk through the OAuth 2.0 authorization code flow with PKCE, as used by a mobile banking app.$hs$, $hs$1. The app generates a random `code_verifier` and derives the `code_challenge` from it (SHA-256).
2. It sends the user's browser to the authorization server with its client id, the requested scopes, a redirect URI and the `code_challenge`.
3. The user signs in and consents; the authorization server redirects back to the app with a short-lived authorization code.
4. The app exchanges the code, together with the `code_verifier`, for tokens on a direct (back-channel) request.
5. The server checks that the verifier matches the challenge, then returns the access token (and refresh token, and ID token with OpenID Connect).$hs$, 3, 'SUBMITTED', $hs$RFC 7636 Proof Key for Code Exchange https://www.rfc-editor.org/rfc/rfc7636$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: what PKCE protects against$hs$, $hs$What attack does PKCE prevent in the authorization code flow?$hs$, $hs$PKCE (Proof Key for Code Exchange, pronounced "pixy") prevents **authorization code interception**: an attacker who steals the authorization code on its way back to the client, e.g. a malicious app registered for the same redirect URI on a phone, cannot exchange it for tokens.

The client creates a random secret, the `code_verifier`, and sends only its hash, the `code_challenge`, with the authorization request. The token request must include the `code_verifier` itself, which only the client that started the flow knows.

PKCE is essential for public clients (mobile apps, SPAs), which cannot keep a client secret, and is recommended for all clients.$hs$, 4, 'SUBMITTED', $hs$RFC 7636 Proof Key for Code Exchange, §1 Introduction https://www.rfc-editor.org/rfc/rfc7636$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: choosing a flow$hs$, $hs$Which OAuth 2.0 grant fits each client: a single-page web app, a mobile banking app, and a nightly batch job that calls the statements API?$hs$, $hs$- Single-page app: authorization code with PKCE (ideally through a backend-for-frontend that keeps the tokens).
- Mobile app: authorization code with PKCE, using the system browser.
- Nightly batch job: client credentials. No user is involved; the job authenticates as itself.$hs$, 3, 'SUBMITTED', $hs$RFC 9700 Best Current Practice for OAuth 2.0 Security https://www.rfc-editor.org/rfc/rfc9700$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: why the implicit and password grants are discouraged$hs$, $hs$Why does the OAuth 2.0 security best practice tell clients not to use the implicit grant or the resource owner password credentials grant?$hs$, $hs$- Implicit grant (should not be used): the access token comes back in the redirect URL, where it can leak through browser history, referrer headers and logs, and it can be injected or replayed. Authorization code with PKCE replaces it.
- Password grant (must not be used): the client handles the user's password directly, which teaches users to type passwords into third-party apps and doesn't work with multi-factor authentication or federated sign-in.$hs$, 4, 'SUBMITTED', $hs$RFC 9700 Best Current Practice for OAuth 2.0 Security, §2.1.2 and §2.4 https://www.rfc-editor.org/rfc/rfc9700$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: accepting JWT access tokens in a resource server$hs$, $hs$The bank's identity provider is at `https://idp.bank.example`. What do you add to a Spring Boot service so it accepts the provider's JWT access tokens, and what does Spring then check on each token?$hs$, $hs$The `spring-boot-starter-oauth2-resource-server` dependency, and:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://idp.bank.example
          audiences: payments-api
```

Spring discovers the provider's signing keys (JWKS) from its metadata. On each request it verifies the token's signature with those keys, checks `exp` and `nbf` (with 60 seconds of clock skew), the `iss` issuer and the audience, and maps the scopes to authorities.$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, OAuth 2.0 Resource Server JWT https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: scopes become authorities in a resource server$hs$, $hs$The access token contains `"scope": "transfers:write"`, yet this rule rejects the request with `403`. Why, and what's the fix?

```java
.requestMatchers(HttpMethod.POST, "/transfers").hasAuthority("transfers:write")
```$hs$, $hs$By default, the resource server maps each scope to an authority with the prefix `SCOPE_`, so the authority is `SCOPE_transfers:write`.

```java
.requestMatchers(HttpMethod.POST, "/transfers").hasAuthority("SCOPE_transfers:write")
```

Alternatively, change the mapping with a `JwtAuthenticationConverter` (or Boot's `authority-prefix` and `authorities-claim-name` properties).$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, OAuth 2.0 Resource Server JWT (Extracting Authorities) https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: access token vs ID token$hs$, $hs$A user signs in to the bank's web app through the bank's identity provider (OpenID Connect), and the app then calls the accounts API. The identity provider gives the app an ID token and an access token. What is each one for, and which one should the accounts API accept?$hs$, $hs$Both are issued by the identity provider (the authorization server). They differ in their audience, the `aud` claim: who the token is meant for.

- **ID token:** its audience is the web app itself. It tells the app who signed in, and when and how, so the app can start a session and show the user's name. The app reads it; it doesn't send it anywhere.
- **Access token:** its audience is the API. The app sends it with each call (`Authorization: Bearer ...`), and it carries what the app may do there (scopes).

The accounts API accepts only access tokens whose `aud` is the accounts API. An ID token sent to an API is a misuse: it proves who signed in to the app, not that the app may call this API.$hs$, 3, 'SUBMITTED', $hs$OpenID Connect Core 1.0, §2 ID Token https://openid.net/specs/openid-connect-core-1_0.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: refresh tokens$hs$, $hs$How do refresh tokens work, and why is a short-lived access token with a refresh token safer than a long-lived access token?$hs$, $hs$The client receives a short-lived access token (minutes) for API calls, and a longer-lived refresh token, which it sends only to the authorization server to get new access tokens.

- A stolen access token is useful only until it expires.
- The refresh token never travels to the APIs, and the authorization server can revoke it (e.g. at logout, or when the user's account is blocked).
- With refresh token rotation, each use returns a new refresh token; if an old one is reused, the server detects the theft and revokes the whole chain.$hs$, 5, 'SUBMITTED', $hs$RFC 9700 Best Current Practice for OAuth 2.0 Security, §4.14 Refresh Token Protection https://www.rfc-editor.org/rfc/rfc9700$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: where a SPA keeps its tokens$hs$, $hs$A banking SPA receives access and refresh tokens. Why shouldn't it store them in `localStorage`, and what's the safer pattern?$hs$, $hs$Any script running on the page can read `localStorage`, so a single XSS flaw, or a compromised third-party script, steals the tokens, which then work from anywhere.

Safer: a backend-for-frontend (BFF). A small server-side component runs the OAuth flow, keeps the tokens on the server, and gives the browser only an `HttpOnly`, `Secure`, `SameSite` session cookie. The BFF attaches the access token when it forwards API calls. The cookie then needs CSRF protection.$hs$, 6, 'SUBMITTED', $hs$IETF draft, OAuth 2.0 for Browser-Based Applications https://datatracker.ietf.org/doc/draft-ietf-oauth-browser-based-apps/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: what oauth2Login() does after "Log in with Google"$hs$, $hs$A Spring Boot web app is configured with `oauth2Login()` and Google as the provider. Walk through what happens from the click on "Log in with Google" until the athlete is signed in.$hs$, $hs$1. Spring Security redirects the browser to Google's authorization endpoint, with the client id, the scopes `openid profile email`, the redirect URI and a `state` value.
2. The athlete signs in at Google and consents.
3. Google redirects back to `/login/oauth2/code/google` with an authorization code; Spring checks `state`.
4. Spring exchanges the code for tokens in a back-channel call, authenticated with the client secret.
5. It validates the ID token (signature, issuer, audience, expiry, nonce) and loads the user info.
6. It creates an `OidcUser` as the principal and stores the authentication in the session, under a new session id.

The app should map Google's stable subject (`sub`), not the email, to its own account.$hs$, 6, 'SUBMITTED', $hs$Spring Security docs, OAuth 2.0 Login https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$OAuth2: GitHub rejects the redirect_uri behind a proxy$hs$, $hs$A Spring Boot app using `oauth2Login()` works locally. In production it runs behind a load balancer that terminates TLS and forwards plain HTTP to port 8080, adding `X-Forwarded-Proto: https` and `X-Forwarded-Host`. Sign-in now fails at GitHub with "The redirect_uri is not associated with this application". The registered callback is `https://api.example.com/login/oauth2/code/github`. Why, and what's the right fix?$hs$, $hs$Spring Security builds the redirect URI from the incoming request, using the template `{baseUrl}/login/oauth2/code/{registrationId}`. Behind the proxy, the application sees the request as `http://<internal-host>:8080`, so it sends that as the `redirect_uri`, which doesn't match the registered callback.

The fix is to make the application honour the forwarded headers, so that the scheme, host and port reflect what the client used:

```properties
server.forward-headers-strategy=native
```

`native` uses Tomcat's `RemoteIpValve`, which trusts the headers only from known proxy addresses. Do this only when the proxy is the only way in, since otherwise clients could fake the headers. Hard-coding the `redirect-uri` would fix this one URL, but leave other absolute URLs and `request.isSecure()` wrong.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Running Behind a Front-end Proxy Server https://docs.spring.io/spring-boot/how-to/webserver.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-boot']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JWT: its three parts$hs$, $hs$What is a JSON Web Token (JWT) made of?$hs$, $hs$Three base64url-encoded parts separated by dots: `header.payload.signature`.

- Header: the token type and the signing algorithm, e.g. `RS256`, and the key id `kid`.
- Payload: the claims, e.g. `sub` (the user), `iss`, `aud`, `exp`, `scope`.
- Signature: over the header and payload, so any change to them is detected.$hs$, 1, 'SUBMITTED', $hs$RFC 7519 JSON Web Token https://www.rfc-editor.org/rfc/rfc7519$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JWT: is the payload secret?$hs$, $hs$A developer puts the customer's IBAN and date of birth in the JWT access token's claims, since "the token is signed". What's wrong?$hs$, $hs$Signed doesn't mean encrypted. The payload is only base64url-encoded: anyone holding the token (the browser, logs, proxies) can decode and read it. The signature only guarantees it wasn't changed.

Keep personal and sensitive data out of the token; put identifiers and authorities in it, and let the API look up the rest.$hs$, 2, 'SUBMITTED', $hs$RFC 7519 JSON Web Token https://www.rfc-editor.org/rfc/rfc7519$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JWT: what a resource server must check$hs$, $hs$What must an API check on an incoming JWT access token before trusting it?$hs$, $hs$- The signature, with a key from the trusted issuer, and an allowed algorithm (never `none`, never chosen by the token).
- `exp` (not expired) and `nbf` (already valid).
- `iss`: issued by the expected identity provider.
- `aud`: intended for this API.
- Then authorization: the scopes or roles required for the operation.$hs$, 3, 'SUBMITTED', $hs$RFC 7519 JSON Web Token, §7.2 Validating a JWT https://www.rfc-editor.org/rfc/rfc7519$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JWT: revoking an access token$hs$, $hs$A customer reports a stolen phone. Their JWT access token is valid for another 50 minutes. Can you revoke it, and how do you design for this case?$hs$, $hs$Not directly: the API validates the token locally from its signature and expiry, without asking anyone.

- Keep access tokens short-lived (minutes) and revoke the refresh token, so no new access tokens are issued.
- If immediate revocation is required, keep a deny-list of revoked token ids (`jti`) or users, checked on each request (at the cost of a lookup).
- Or use opaque tokens with introspection for the sensitive APIs.$hs$, 5, 'SUBMITTED', $hs$OWASP JSON Web Token Cheat Sheet, Token Explicit Revocation (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JWT: self-contained JWTs vs opaque tokens$hs$, $hs$Compare self-contained JWT access tokens with opaque tokens checked through introspection.$hs$, $hs$- JWT: validated locally with the issuer's public keys: fast, no call to the authorization server, works when it's down. But it stays valid until it expires, and its claims are readable by whoever holds it.
- Opaque token: a random reference; the API calls the authorization server's introspection endpoint to learn whether it's active and what it allows. Revocation is immediate and nothing leaks, but every request (or cache miss) costs a call and depends on the authorization server.

A common compromise: short-lived JWTs, with introspection only for the most sensitive operations.$hs$, 6, 'SUBMITTED', $hs$Spring Security docs, OAuth 2.0 Resource Server Opaque Token https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/opaque-token.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Authorization: hasRole vs hasAuthority$hs$, $hs$What is the difference between `hasRole("ADMIN")` and `hasAuthority("ADMIN")`?$hs$, $hs$Both check the user's `GrantedAuthority`s. A role is simply an authority with the `ROLE_` prefix.

- `hasRole("ADMIN")` checks for the authority `ROLE_ADMIN`.
- `hasAuthority("ADMIN")` checks for exactly `ADMIN`.$hs$, 2, 'SUBMITTED', $hs$Spring Security docs, Authorize HttpServletRequests https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Authorization: the owner or an admin with method security$hs$, $hs$Only the account's owner or a user with the role `ADMIN` may close an account. Complete the method with Spring Security's method security, given an `accountSecurity` bean with `isOwner(long accountId, Authentication auth)`.

```java
public void closeAccount(long accountId) { ... }
```$hs$, $hs$```java
@PreAuthorize("hasRole('ADMIN') or @accountSecurity.isOwner(#accountId, authentication)")
public void closeAccount(long accountId) { ... }
```

Method security must be switched on with `@EnableMethodSecurity` on a configuration class; Spring Boot doesn't activate it by default. `#accountId` refers to the parameter, `@accountSecurity` to the bean.$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, Method Security https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Authorization: a @PreAuthorize that's ignored$hs$, $hs$`@EnableMethodSecurity` is on, yet any user can approve a payment through `submit()`. Why?

```java
@Service
public class PaymentService {

    public void submit(long paymentId) {
        approve(paymentId);
    }

    @PreAuthorize("hasRole('APPROVER')")
    public void approve(long paymentId) { ... }
}
```$hs$, $hs$Method security works through a Spring proxy around the bean. `submit()` calls `approve()` on `this`, the object itself, so the call never passes through the proxy, and the check doesn't run.

```java
@Service
public class PaymentService {

    @PreAuthorize("hasRole('APPROVER')")
    public void submit(long paymentId) {
        approve(paymentId);
    }

    void approve(long paymentId) { ... }
}
```

Put the check on the entry point that's called from outside, or move `approve()` to another bean.$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, Method Security https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Access control: insecure direct object references$hs$, $hs$What is an insecure direct object reference (IDOR)? Give a banking example.$hs$, $hs$An endpoint that uses an identifier from the request to fetch an object, checks that the caller is authenticated, but not that they may access that object.

Example: a signed-in customer calls `GET /accounts/1043/statements` instead of their own `/accounts/1042/statements`, and receives someone else's statements. It's a form of broken access control, the top risk in the OWASP Top 10.$hs$, 3, 'SUBMITTED', $hs$OWASP Insecure Direct Object Reference Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Access control: fixing an IDOR in the statements endpoint$hs$, $hs$Fix this endpoint so customers can only read statements of their own accounts.

```java
@GetMapping("/accounts/{accountId}/statements")
List<StatementDto> statements(@PathVariable long accountId) {
    return statementService.findByAccount(accountId);
}
```$hs$, $hs$```java
@GetMapping("/accounts/{accountId}/statements")
List<StatementDto> statements(@PathVariable long accountId, @AuthenticationPrincipal Jwt jwt) {
    return statementService.findByAccount(accountId, jwt.getSubject());
}
```

In the service, the ownership is part of the query, so another customer's account simply isn't found:

```sql
SELECT s.* FROM statement s JOIN account a ON a.id = s.account_id
WHERE a.id = :accountId AND a.owner_id = :customerId
```

Answer `404` when the account isn't the caller's, so its existence isn't revealed. The check belongs in the service (or query), so every caller of it is protected, not just this controller.$hs$, 5, 'SUBMITTED', $hs$OWASP Authorization Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: what CSRF is$hs$, $hs$What is cross-site request forgery (CSRF)?$hs$, $hs$An attack where a malicious site makes the victim's browser send a request to a site where the victim is signed in, e.g. a hidden form that posts a transfer to the bank. The browser attaches the bank's cookies automatically, so the request runs with the victim's identity, without their knowledge.$hs$, 2, 'SUBMITTED', $hs$OWASP Cross-Site Request Forgery Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: CSRF with session cookies vs bearer tokens$hs$, $hs$Why can a CSRF attack succeed against an API authenticated with a session cookie, but not against one that only accepts a bearer token in the `Authorization` header?$hs$, $hs$The browser attaches cookies automatically to every request to their site, including requests triggered by another site, so a forged cookie-authenticated request arrives looking legitimate.

A bearer token isn't attached automatically: the client's own code must add the `Authorization` header, and the attacker's page can't read the token to do so.

So cookie-based APIs need CSRF protection (on by default in Spring Security); stateless APIs that only accept bearer tokens don't.$hs$, 3, 'SUBMITTED', $hs$OWASP Cross-Site Request Forgery Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: CSRF tokens and SameSite cookies$hs$, $hs$How do a CSRF token and the `SameSite` cookie attribute each protect against CSRF?$hs$, $hs$- CSRF token: the server issues a random token that the site's own pages send back with every state-changing request (in a form field or header). Another site can't read it, so it can't include it, and the server rejects the forged request.
- `SameSite=Lax` or `Strict`: the browser doesn't send the cookie on cross-site requests (`Lax` still sends it on top-level `GET` navigations), so the forged request arrives unauthenticated.

`SameSite` is a good extra layer; the token remains the primary defence.$hs$, 3, 'SUBMITTED', $hs$OWASP Cross-Site Request Forgery Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: every POST returns 403 after adding Spring Security$hs$, $hs$After adding Spring Security to a gym's session-based web app with a JavaScript frontend, every `POST`, `PUT` and `DELETE` returns `403`, while `GET`s work. Why, and what's the right fix?$hs$, $hs$CSRF protection is on by default for unsafe methods, and these requests carry no CSRF token, so they're rejected.

Don't disable CSRF: the app authenticates with a session cookie, so it's exactly what CSRF targets. Send the token instead. For a single-page app, Spring Security can store it in a cookie readable by JavaScript, which the frontend copies into the `X-XSRF-TOKEN` header:

```java
http.csrf(csrf -> csrf.spa());
```

(Older versions: `CookieCsrfTokenRepository.withHttpOnlyFalse()`.)$hs$, 5, 'SUBMITTED', $hs$Spring Security docs, Cross Site Request Forgery (CSRF) https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: what CORS does and protects$hs$, $hs$What is CORS, and does it protect your API from other clients?$hs$, $hs$By default, the browser's same-origin policy prevents JavaScript on one origin from reading responses from another. CORS lets a server relax this: response headers like `Access-Control-Allow-Origin` tell the browser which other origins may read its responses. For non-simple requests, the browser first sends a preflight `OPTIONS` request to ask.

It protects users in their browsers, not the API: `curl`, scripts and other servers ignore CORS entirely. The API still needs authentication and authorization.$hs$, 3, 'SUBMITTED', $hs$MDN Web Docs, Cross-Origin Resource Sharing (CORS) (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CORS$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: CORS with a wildcard origin and cookies$hs$, $hs$A SPA on `https://app.bank.example` calls `https://api.bank.example` with its session cookie, and the browser blocks the responses, although the API allows the origin `*`. Why, and how do you configure it in Spring?$hs$, $hs$For requests with credentials (cookies), the browser doesn't accept the wildcard: the response must name the exact origin and allow credentials. Spring also rejects `*` combined with `allowCredentials`.

```java
@Bean
UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://app.bank.example"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

And enable it in the filter chain with `http.cors(Customizer.withDefaults())`, so CORS is handled before authentication; otherwise the preflight, which carries no cookies, is rejected.$hs$, 5, 'SUBMITTED', $hs$Spring Security docs, CORS https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: XSS and its main defences$hs$, $hs$What is cross-site scripting (XSS), and what are the main defences?$hs$, $hs$An attacker gets their script to run in other users' browsers on your site, e.g. through a transfer description shown unescaped on the recipient's statement. The script runs with the user's session: it can read the page, make requests, steal tokens.

- Output encoding for the context (HTML, attribute, JavaScript, URL); templates and frameworks like React escape by default, so avoid bypassing them.
- Sanitising rich text that must contain HTML.
- A Content Security Policy restricting where scripts may come from.
- `HttpOnly` cookies, so scripts can't read the session cookie.$hs$, 3, 'SUBMITTED', $hs$OWASP Cross Site Scripting Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: HttpOnly, Secure and SameSite cookies$hs$, $hs$What do the cookie attributes `HttpOnly`, `Secure` and `SameSite` each do for a session cookie?$hs$, $hs$- `HttpOnly`: JavaScript can't read the cookie, so an XSS attack can't steal it.
- `Secure`: the cookie is sent only over HTTPS, never in clear text.
- `SameSite` (`Strict`, `Lax`, `None`): controls whether the browser sends the cookie on cross-site requests, a defence against CSRF.$hs$, 2, 'SUBMITTED', $hs$MDN Web Docs, Set-Cookie (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Browser security: a CORS error instead of a 401$hs$, $hs$A single-page app on `https://app.example.com` calls a Spring Boot API on `https://api.example.com` with `fetch(url, { credentials: 'include' })`. The API signs users in with `oauth2Login()` (GitHub) and otherwise uses Spring Security's defaults. When a session has expired, the app's code that handles a 401 never runs: instead, the console shows a CORS error for `github.com`, and `fetch` rejects with a `TypeError`. Why, and how do you fix it?$hs$, $hs$With `oauth2Login()` as the only login method, Spring Security's default **authentication entry point** answers an unauthenticated request with a redirect (302) to `/oauth2/authorization/github`, which redirects again to GitHub's login page. `fetch` follows redirects automatically, and the last one goes to another origin, `github.com`, which doesn't allow the app's origin. The browser blocks the response, so `fetch` fails without ever seeing a status code.

An API should answer 401 instead of redirecting. Configure the entry point (and, for consistency, the access-denied handler):

```java
http.exceptionHandling(exceptions -> exceptions
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
```

The app then handles the 401 and starts sign-in by navigating the whole window to `/oauth2/authorization/github`. A `@RestControllerAdvice` can't do this, because the security filter chain handles the exception before the request reaches Spring MVC.$hs$, 6, 'SUBMITTED', $hs$Spring Security docs, Servlet Architecture (Handling Security Exceptions) https://docs.spring.io/spring-security/reference/servlet/architecture.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Vulnerabilities: the OWASP Top 10$hs$, $hs$What is the OWASP Top 10, and which risk is at the top of its current edition?$hs$, $hs$A regularly updated awareness document listing the most critical security risks to web applications, based on data and community surveys.

In the 2025 edition, broken access control is first (as in 2021), followed by security misconfiguration and software supply chain failures.$hs$, 1, 'SUBMITTED', $hs$OWASP Top 10:2025 https://owasp.org/Top10/2025/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Vulnerabilities: SSRF in a service that fetches user-supplied URLs$hs$, $hs$An athlete can set a profile picture by URL, and the service downloads it. What is server-side request forgery (SSRF) here, and how do you prevent it?$hs$, $hs$The attacker supplies an internal URL, such as `http://169.254.169.254/` (the cloud metadata service, which hands out credentials) or `http://admin-service.internal/`, and the server fetches it from inside the network, where it's trusted.

- Allow-list the domains and schemes (`https` only) when possible.
- Resolve the host and reject private, loopback and link-local addresses, and check again after redirects (or disable redirects).
- Run the fetcher with no access to internal networks, and limit size and time.$hs$, 4, 'SUBMITTED', $hs$OWASP Server-Side Request Forgery Prevention Cheat Sheet (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Secrets: what must never be logged$hs$, $hs$What must a banking service never write to its logs?$hs$, $hs$- Passwords and authentication secrets: session ids, access and refresh tokens, API keys.
- Full card numbers and their security codes, full bank account details where not needed.
- Sensitive personal data beyond what's needed.
- Encryption keys and other secrets.

Mask or remove them (e.g. the last four digits of a card), including in logged request bodies, headers and exception messages.$hs$, 2, 'SUBMITTED', $hs$OWASP Logging Cheat Sheet, Data to exclude (CC BY-SA 4.0) https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Vulnerabilities: dependencies with known vulnerabilities$hs$, $hs$How do you keep a Spring Boot service free of dependencies with known vulnerabilities?$hs$, $hs$- Scan dependencies automatically in the build (e.g. OWASP Dependency-Check, Snyk, GitHub Dependabot alerts) and fail on critical issues.
- Upgrade regularly: staying on supported Spring Boot versions brings patched libraries through its dependency management.
- Automated upgrade pull requests (Renovate, Dependabot).
- An inventory of what's deployed (a software bill of materials) and a process for urgent patches.$hs$, 3, 'SUBMITTED', $hs$OWASP Top 10:2025, A03 Software Supply Chain Failures https://owasp.org/Top10/2025/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Service security: authenticating service-to-service calls$hs$, $hs$How can the payment service authenticate itself when it calls the ledger service? Name the common options.$hs$, $hs$- OAuth 2.0 client credentials: the payment service gets an access token from the authorization server with its own client credentials, scoped to what it may do.
- Mutual TLS: both sides present certificates, often provided by a service mesh.
- API keys: simple but the weakest; long-lived shared secrets without scopes or expiry.

Often combined: mTLS for the connection, plus tokens for authorization.$hs$, 3, 'SUBMITTED', $hs$RFC 8705 OAuth 2.0 Mutual-TLS Client Authentication https://www.rfc-editor.org/rfc/rfc8705$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-security', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strategy: the test pyramid$hs$, $hs$What is the test pyramid?$hs$, $hs$A guide to the mix of automated tests: many fast unit tests at the bottom, fewer integration tests in the middle, and very few end-to-end tests at the top.

Tests higher up give more confidence that the parts work together, but they are slower, more expensive to maintain and more prone to flakiness.$hs$, 1, 'SUBMITTED', $hs$Martin Fowler, The Practical Test Pyramid https://martinfowler.com/articles/practical-test-pyramid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Strategy: unit vs integration vs end-to-end$hs$, $hs$What is the difference between a unit test, an integration test and an end-to-end test? Give an example of each for a transfer service.$hs$, $hs$- Unit test: one unit of behaviour in isolation, in milliseconds. The fee calculation for a transfer, without Spring or a database.
- Integration test: several parts with real infrastructure. The transfer repository against a real PostgreSQL, or the `POST /transfers` endpoint through the running service.
- End-to-end test: the whole system through its external interfaces. A customer signs in in the app, makes a transfer, and sees it on both accounts.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, The Practical Test Pyramid https://martinfowler.com/articles/practical-test-pyramid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Unit tests: what makes a good one$hs$, $hs$What makes a good unit test?$hs$, $hs$- Tests behaviour through the public API, not implementation details, so refactoring doesn't break it.
- One reason to fail, with a name that says which behaviour broke.
- Fast and deterministic: no real time, randomness, network or shared state.
- Independent of other tests and their order.
- Readable as documentation: a clear given, when and then.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, The Practical Test Pyramid https://martinfowler.com/articles/practical-test-pyramid.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Unit tests: the given, when, then structure$hs$, $hs$What are the three parts of a test in the given, when, then (or arrange, act, assert) structure?$hs$, $hs$- Given (arrange): the starting state. An athlete with three completed workouts.
- When (act): the one behaviour under test. They finish a fourth workout.
- Then (assert): the expected outcome. They receive the "four in a row" badge.

One "when" per test keeps each test focused on one behaviour.$hs$, 1, 'SUBMITTED', $hs$Martin Fowler, GivenWhenThen https://martinfowler.com/bliki/GivenWhenThen.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Test data: a builder for valid accounts$hs$, $hs$Integration tests need valid accounts, each with a customer, an IBAN, a currency and a balance. Every test builds them with long constructor calls, and adding a field to `Account` breaks 80 tests. Write a test data builder that lets a test state only what matters to it.$hs$, $hs$```java
class AccountBuilder {
    private Customer customer = aCustomer().build();   // valid defaults for everything
    private String iban = "DE89370400440532013000";
    private String currency = "EUR";
    private BigDecimal balance = BigDecimal.ZERO;

    static AccountBuilder anAccount() { return new AccountBuilder(); }

    AccountBuilder withBalance(String amount) { this.balance = new BigDecimal(amount); return this; }
    AccountBuilder withCurrency(String currency) { this.currency = currency; return this; }

    Account build() { return new Account(customer, iban, currency, balance); }
}

// in a test: only the balance matters here
Account account = anAccount().withBalance("100.00").build();
```

The defaults keep every built account valid, a new field changes only the builder, and each test shows only the values its assertion depends on.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, ObjectMother (and test data builders) https://martinfowler.com/bliki/ObjectMother.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JUnit: @Test, @BeforeEach and @BeforeAll$hs$, $hs$What do `@Test`, `@BeforeEach` and `@BeforeAll` do in JUnit Jupiter?$hs$, $hs$- `@Test`: marks a test method.
- `@BeforeEach`: runs before every test method, to set up fresh state.
- `@BeforeAll`: runs once before all tests of the class. It must be `static` by default, because JUnit creates a new instance of the test class for every test method.

`@AfterEach` and `@AfterAll` are the counterparts after the tests.$hs$, 1, 'SUBMITTED', $hs$JUnit User Guide, Annotations https://docs.junit.org/current/user-guide/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JUnit: one rule, many cases$hs$, $hs$Membership fees depend on age: 20 for under 18, 40 from 18 to 64, and 25 from 65. Test `fees.monthlyFee(age)` at every boundary without writing one method per case.$hs$, $hs$```java
@ParameterizedTest
@CsvSource({
        "17, 20",
        "18, 40",
        "64, 40",
        "65, 25"
})
void monthlyFeeDependsOnAge(int age, int expectedFee) {
    assertEquals(expectedFee, fees.monthlyFee(age));
}
```

Each row runs as its own test case, reported separately. `@ValueSource` and `@MethodSource` supply arguments in other ways.$hs$, 2, 'SUBMITTED', $hs$JUnit User Guide, Parameterized Tests https://docs.junit.org/current/user-guide/#writing-tests-parameterized-tests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JUnit: asserting an exception$hs$, $hs$Write a test that checks `account.withdraw(new BigDecimal("150"))` throws an `InsufficientFundsException` with a message containing "insufficient funds" when the balance is 100.$hs$, $hs$```java
@Test
void withdrawingMoreThanTheBalanceIsRejected() {
    Account account = new Account(new BigDecimal("100"));

    InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
            () -> account.withdraw(new BigDecimal("150")));

    assertTrue(exception.getMessage().contains("insufficient funds"));
    assertEquals(new BigDecimal("100"), account.balance());
}
```

`assertThrows` returns the exception, so you can check its details. Checking the balance too proves nothing was withdrawn. With AssertJ: `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`.$hs$, 3, 'SUBMITTED', $hs$JUnit User Guide, Assertions https://docs.junit.org/current/user-guide/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Unit tests: code that depends on the current time$hs$, $hs$How do you write a reliable test for this method, which expires memberships?

```java
public boolean isExpired(Membership membership) {
    return membership.endDate().isBefore(LocalDate.now());
}
```$hs$, $hs$Inject a `java.time.Clock` instead of reading the system clock, and use a fixed clock in tests:

```java
private final Clock clock;

public boolean isExpired(Membership membership) {
    return membership.endDate().isBefore(LocalDate.now(clock));
}
```

```java
Clock clock = Clock.fixed(Instant.parse("2026-03-01T10:00:00Z"), ZoneOffset.UTC);
MembershipService service = new MembershipService(clock);

assertTrue(service.isExpired(membershipEndingOn(LocalDate.of(2026, 2, 28))));
assertFalse(service.isExpired(membershipEndingOn(LocalDate.of(2026, 3, 1))));
```

In production, the bean gets `Clock.systemUTC()`.$hs$, 4, 'SUBMITTED', $hs$Java SE docs, java.time.Clock https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Clock.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Unit tests: passing alone, failing together$hs$, $hs$Each test passes when run alone, but some fail when the whole class runs. What is the likely cause, and the fix?$hs$, $hs$Shared state between tests: a `static` field, a singleton, rows left in a database, a mocked clock or system property that isn't reset. One test changes it, and the next one relies on the original.

Make every test set up what it needs and clean up after itself (fresh objects in `@BeforeEach`, tables emptied between tests). Don't rely on test order: JUnit's default order is deterministic but deliberately not obvious.$hs$, 3, 'SUBMITTED', $hs$Martin Fowler, Eradicating Non-Determinism in Tests https://martinfowler.com/articles/nonDeterminism.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: dummy, stub, fake, spy and mock$hs$, $hs$What are a dummy, a stub, a fake, a spy and a mock?$hs$, $hs$- Dummy: passed only to fill a parameter, never used.
- Stub: returns canned answers to calls. A rate provider that always returns 1.10.
- Fake: a working but simplified implementation. An in-memory repository backed by a map.
- Spy: a stub that also records how it was called, for checking afterwards.
- Mock: preprogrammed with expectations of the calls it should receive, verified by the test.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, Test Double https://martinfowler.com/bliki/TestDouble.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: mock() vs spy()$hs$, $hs$What is the difference between `mock(WorkoutPlanner.class)` and `spy(new WorkoutPlanner())` in Mockito?$hs$, $hs$- `mock(...)`: no real code runs. Every method returns a default (`null`, `0`, `false`, empty collections) unless stubbed.
- `spy(...)`: wraps a real object. Real methods run unless stubbed, and calls are recorded for verification.

Stub a spy with `doReturn(x).when(spy).method()`, because `when(spy.method())` calls the real method first.$hs$, 2, 'SUBMITTED', $hs$Mockito documentation, Spying on real objects https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: stubbing and verifying$hs$, $hs$`TransferService.transfer(from, to, amount)` loads both accounts through `AccountRepository.findById` and saves them. Write a unit test with Mockito that checks a transfer of 30 from an account with 100 moves the money and saves both accounts.$hs$, $hs$```java
@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock AccountRepository accountRepository;
    @InjectMocks TransferService transferService;

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account from = new Account(1L, new BigDecimal("100"));
        Account to = new Account(2L, new BigDecimal("0"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        transferService.transfer(1L, 2L, new BigDecimal("30"));

        assertEquals(new BigDecimal("70"), from.balance());
        assertEquals(new BigDecimal("30"), to.balance());
        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
    }
}
```$hs$, 2, 'SUBMITTED', $hs$Mockito documentation https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: capturing an argument$hs$, $hs$After a transfer, the service builds a `Notification` and passes it to `notifier.send(...)`. How do you assert on the recipient and the text of the notification that was sent?$hs$, $hs$Capture the argument with an `ArgumentCaptor`:

```java
ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

transferService.transfer(1L, 2L, new BigDecimal("30"));

verify(notifier).send(captor.capture());
Notification sent = captor.getValue();
assertEquals("ana@example.com", sent.recipient());
assertTrue(sent.text().contains("30.00"));
```$hs$, 3, 'SUBMITTED', $hs$Mockito documentation, Capturing arguments https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: making a collaborator fail$hs$, $hs$Test that the transfer is marked `FAILED` when `ledgerClient.book(transfer)`, a `void` method, throws a `LedgerUnavailableException`. How do you make the mock throw?$hs$, $hs$For a `void` method, use the `doThrow` form, since `when(...)` needs a return value:

```java
doThrow(new LedgerUnavailableException()).when(ledgerClient).book(any(Transfer.class));

Transfer transfer = transferService.submit(request);

assertEquals(TransferStatus.FAILED, transfer.status());
```

For methods that return a value: `when(rates.current("EUR")).thenThrow(new RateUnavailableException())`.$hs$, 3, 'SUBMITTED', $hs$Mockito documentation, Stubbing void methods with exceptions https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Mockito: over-mocking$hs$, $hs$What is over-mocking, and why is it harmful?$hs$, $hs$Mocking every collaborator, including simple value objects and your own small classes, and verifying the exact calls between them.

- The tests mirror the implementation: any refactoring breaks them, although the behaviour didn't change.
- They can pass while the real collaboration is wrong, because the mocks behave the way the test author assumed.
- They're long and hard to read.

Use real objects where they're cheap, and mock only at boundaries that are slow or outside your control (other services, the clock, message brokers).$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, Mocks Aren't Stubs https://martinfowler.com/articles/mocksArentStubs.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: what @SpringBootTest does$hs$, $hs$What does `@SpringBootTest` do?$hs$, $hs$It starts the whole application context for the test, the way the application starts, with all beans and auto-configuration, so you can inject beans and test them working together.

With `webEnvironment`:
- `MOCK` (the default): a mock web environment, no real server.
- `RANDOM_PORT` or `DEFINED_PORT`: a real embedded server, called over HTTP.
- `NONE`: no web environment.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, Testing Spring Boot Applications https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: test slices$hs$, $hs$What are Spring Boot's test slices? Name three, with what each loads.$hs$, $hs$Annotations that start only the part of the application context needed to test one layer, so tests start faster and stay focused:

- `@WebMvcTest`: controllers, filters, `@ControllerAdvice`, JSON conversion and `MockMvc`; no services or repositories.
- `@DataJpaTest`: JPA entities, repositories and a transaction rolled back after each test.
- `@JsonTest`: JSON serialization with Jackson.

Others: `@JdbcTest`, `@RestClientTest`, `@DataJdbcTest`. In Spring Boot 4, each comes from its own test module, e.g. `spring-boot-webmvc-test`.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Testing Spring Boot Applications (auto-configured tests) https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: testing a controller's validation with @WebMvcTest$hs$, $hs$Write a test that checks `POST /transfers` answers `400` for a negative amount, without starting the service or database layers.$hs$, $hs$```java
@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean TransferService transferService;

    @Test
    void negativeAmountIsRejected() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccountId": 1, "toAccountId": 2, "amount": -50}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transferService);
    }
}
```

`@WebMvcTest` loads only the web layer; `@MockitoBean` puts a mock of the service in the context.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Auto-configured Spring MVC Tests https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: @MockitoBean vs Mockito's @Mock$hs$, $hs$What is the difference between Spring's `@MockitoBean` and Mockito's `@Mock`?$hs$, $hs$- `@Mock`: creates a plain Mockito mock in a unit test; Spring isn't involved. You pass it to the class under test yourself (or with `@InjectMocks`).
- `@MockitoBean`: replaces (or adds) a bean in the Spring application context with a Mockito mock, so every bean that depends on it receives the mock. Used in Spring tests such as `@WebMvcTest` or `@SpringBootTest`.

`@MockitoBean` replaces Spring Boot's `@MockBean`, which is deprecated and removed in Spring Boot 4.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, @MockitoBean and @MockitoSpyBean https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: context caching$hs$, $hs$What is Spring's test context caching, and what prevents a test class from reusing a cached context?$hs$, $hs$The TestContext framework keeps started application contexts in a cache (32 by default, least recently used evicted) and reuses one for every test class with the same configuration, in the same JVM. Starting the context once instead of per class is what keeps Spring test suites fast.

A new context is started when the configuration differs: a different set of `@MockitoBean`s, different properties (`@TestPropertySource`, `properties = ...`), active profiles, configuration classes or slice. `@DirtiesContext` throws the context away on purpose.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Context Caching https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: what @DataJpaTest sets up$hs$, $hs$What does `@DataJpaTest` set up, and what must you change to run it against PostgreSQL in a container instead of an embedded database?$hs$, $hs$It scans `@Entity` classes, configures the Spring Data JPA repositories, logs SQL, and makes every test transactional, rolled back at the end. If an embedded database (H2, HSQLDB) is on the classpath, it uses one instead of the application's database.

To test against the real database, prevent the replacement and connect the container:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
}
```$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, Auto-configured Data JPA Tests https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: MockMvc vs a real server$hs$, $hs$Compare testing an endpoint with `MockMvc` and with `@SpringBootTest(webEnvironment = RANDOM_PORT)` plus an HTTP client such as `RestTestClient`.$hs$, $hs$- `MockMvc`: runs the Spring MVC stack in-process, without a server or real HTTP. Fast, and the request runs on the test's thread, so you can inspect the result in detail; but servlet container behaviour (real connections, the container's error handling) isn't exercised.
- `RANDOM_PORT`: starts the embedded server on a random port; the client sends real HTTP requests, through the whole stack as in production. Slower, and the server handles requests on its own threads.

In Spring Boot 4, the client is enabled with `@AutoConfigureRestTestClient` (or `@AutoConfigureTestRestTemplate`).$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Testing with a running server https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: a @Transactional test with a real server$hs$, $hs$An integration test class runs with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and is annotated `@Transactional` "so the data is rolled back". Yet transfers created through `POST /transfers` remain in the database after each test. Why?$hs$, $hs$With a real server, the HTTP client (the test) and the server run on separate threads, and so in separate transactions. The test's transaction is rolled back, but the server's transaction, in which the transfer was created, commits normally.

Clean up explicitly instead: empty the tables before or after each test (e.g. with `JdbcTemplate` or `@Sql`), and drop the `@Transactional` that suggests otherwise.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, Testing Spring Boot Applications https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring tests: calling a secured endpoint$hs$, $hs$`GET /accounts/42` requires a JWT with the scope `accounts:read` and returns only the customer's own accounts. How do you call it in a `MockMvc` test as customer `c-7` without a real identity provider?$hs$, $hs$Use Spring Security's `jwt()` request post-processor, which puts an authenticated JWT into the request:

```java
mockMvc.perform(get("/accounts/42")
                .with(jwt()
                        .jwt(token -> token.subject("c-7"))
                        .authorities(new SimpleGrantedAuthority("SCOPE_accounts:read"))))
        .andExpect(status().isOk());
```

Write the negative tests too: another customer (`404` or `403`), and a token without the scope (`403`). For simpler setups, `@WithMockUser(roles = "ADMIN")` sets an authenticated user on the test.$hs$, 4, 'SUBMITTED', $hs$Spring Security docs, Testing OAuth 2.0 (jwt()) https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testcontainers: what it is$hs$, $hs$What is Testcontainers?$hs$, $hs$A library that starts real services, such as PostgreSQL, Kafka or Redis, in throwaway Docker containers for the duration of the tests, and tells the tests how to connect to them. Tests run against the same software as production, and every run starts clean.$hs$, 1, 'SUBMITTED', $hs$Testcontainers for Java https://java.testcontainers.org/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testcontainers: compared with H2$hs$, $hs$Why test a PostgreSQL-based service against PostgreSQL in Testcontainers rather than an in-memory H2 database?$hs$, $hs$H2 is a different database: its SQL dialect, types, functions, locking and constraint behaviour differ from PostgreSQL. Tests can pass on H2 with queries that fail in production (e.g. `ON CONFLICT`, JSONB, window functions, a specific index or lock behaviour), and the reverse.

Testcontainers runs the same PostgreSQL version as production, at the cost of needing Docker and a few seconds to start the container.$hs$, 3, 'SUBMITTED', $hs$Spring Boot docs, Testcontainers https://docs.spring.io/spring-boot/reference/testing/testcontainers.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Testcontainers: sharing one database between tests$hs$, $hs$All integration tests share one PostgreSQL container. How do you keep them independent of each other?$hs$, $hs$- Every test arranges its own data and doesn't rely on data left by another test or on test order.
- Clean up between tests: empty the tables (e.g. `TRUNCATE ... RESTART IDENTITY CASCADE`) after each test, in one shared place.
- Where cleaning isn't possible, use unique data per test (random ids, unique emails) and query only that data.
- Never assert on global counts ("there are 3 accounts") that other tests can affect.$hs$, 4, 'SUBMITTED', $hs$Martin Fowler, Eradicating Non-Determinism in Tests (Isolation) https://martinfowler.com/articles/nonDeterminism.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Banking: testing that concurrent transfers don't overdraw$hs$, $hs$An account has 100. Write a test that shows two concurrent withdrawals of 80 can't both succeed.$hs$, $hs$Run both against the real database (Testcontainers), starting them at the same moment:

```java
@Test
void concurrentWithdrawalsCannotOverdraw() throws Exception {
    Long accountId = insertAccount(new BigDecimal("100"));
    CountDownLatch start = new CountDownLatch(1);

    try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
        Callable<Boolean> withdraw = () -> {
            start.await();
            try {
                accountService.withdraw(accountId, new BigDecimal("80"));
                return true;
            } catch (InsufficientFundsException | OptimisticLockingFailureException e) {
                return false;
            }
        };
        Future<Boolean> first = executor.submit(withdraw);
        Future<Boolean> second = executor.submit(withdraw);
        start.countDown();

        assertEquals(1, Stream.of(first.get(), second.get()).filter(ok -> ok).count());
    }
    assertEquals(new BigDecimal("20.00"), balanceOf(accountId));
}
```

A single run may pass by luck; repeat it (`@RepeatedTest`) to make a race visible, and check the final balance, not only the results.$hs$, 7, 'SUBMITTED', $hs$Spring Boot docs, Testcontainers https://docs.spring.io/spring-boot/reference/testing/testcontainers.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'java-concurrency', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Flaky tests: a flaky Thread.sleep$hs$, $hs$`finishWorkout` awards badges asynchronously. This test passes on a developer laptop but fails now and then on the CI server. Why, and how do you fix it?

```java
@Test
void finishingAWorkoutAwardsABadge() throws InterruptedException {
    workoutService.finishWorkout(workoutId);
    Thread.sleep(500);
    assertTrue(badgeRepository.existsByAthleteId(athleteId));
}
```$hs$, $hs$The test guesses how long the asynchronous work takes. On a slower or busier machine, the badge isn't saved within 500 ms, so the test fails; on a fast machine it waits longer than needed.

Wait for the condition instead of a fixed time, with Awaitility:

```java
@Test
void finishingAWorkoutAwardsABadge() {
    workoutService.finishWorkout(workoutId);
    await().atMost(Duration.ofSeconds(5))
           .until(() -> badgeRepository.existsByAthleteId(athleteId));   // returns as soon as it's true
}
```$hs$, 5, 'SUBMITTED', $hs$Awaitility https://github.com/awaitility/awaitility$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Flaky tests: what they are$hs$, $hs$What is a flaky test, and why is it dangerous?$hs$, $hs$A test that sometimes passes and sometimes fails without any change to the code.

It destroys trust in the suite: people rerun failures until they pass and learn to ignore red builds, so real regressions slip through. Quarantine a flaky test quickly and fix its cause.$hs$, 2, 'SUBMITTED', $hs$Martin Fowler, Eradicating Non-Determinism in Tests https://martinfowler.com/articles/nonDeterminism.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Flaky tests: causes and fixes$hs$, $hs$Name five typical causes of flaky tests, each with its fix.$hs$, $hs$- Time: `now()`, time zones, midnight or month ends. Fix: an injected fixed `Clock`, explicit zones.
- Asynchronous code checked after a fixed sleep. Fix: wait for the condition (Awaitility, futures).
- Shared state and test order: static fields, leftover rows. Fix: isolate and clean up per test.
- Randomness and unordered collections: asserting the order of a `HashSet`. Fix: fixed seeds, order-independent assertions.
- External dependencies and fixed ports: real services, port clashes. Fix: containers, stubs (WireMock), random ports.$hs$, 5, 'SUBMITTED', $hs$Martin Fowler, Eradicating Non-Determinism in Tests https://martinfowler.com/articles/nonDeterminism.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Quality: what code coverage tells you$hs$, $hs$The transfer module has 95% line coverage. What does that tell you, and what doesn't it? How can you measure test quality better?$hs$, $hs$It tells you which lines ran during the tests, and so which code is certainly not tested (the other 5%).

It doesn't tell you whether behaviour is checked: a test without assertions, or with weak ones, still produces coverage.

Mutation testing (PIT) measures that: it makes small changes to the code (flips a `>` to `>=`, removes a call) and reruns the tests. A change that no test detects ("a surviving mutant") shows a gap in the assertions.$hs$, 5, 'SUBMITTED', $hs$PIT Mutation Testing https://pitest.org/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Integration tests: testing a Kafka consumer$hs$, $hs$How do you test, end to end within the service, that the consumer of `transfer-booked` events updates the account's balance?$hs$, $hs$- Start Kafka (and PostgreSQL) in Testcontainers, connected with `@ServiceConnection`.
- In the test, produce a `transfer-booked` event with a `KafkaTemplate`.
- Wait for the effect with Awaitility: `await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertEquals(expected, balanceOf(accountId)))`.
- Test the hard cases too: the same event delivered twice (idempotency), a malformed event (sent to the dead-letter topic), the database failing (retried).$hs$, 6, 'SUBMITTED', $hs$Spring Boot docs, Testcontainers https://docs.spring.io/spring-boot/reference/testing/testcontainers.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Integration tests: stubbing an external API with WireMock$hs$, $hs$The `ExchangeRateClient` calls an external provider over HTTP. How do you test it against realistic responses, including the provider being slow or failing?$hs$, $hs$Run a WireMock server in the test, point the client's base URL at it, and stub each case:

```java
stubFor(get(urlEqualTo("/rates/EUR"))
        .willReturn(okJson("""
                {"currency": "EUR", "rate": 1.0842}
                """)));

stubFor(get(urlEqualTo("/rates/GBP"))
        .willReturn(aResponse().withStatus(503)));

stubFor(get(urlEqualTo("/rates/CHF"))
        .willReturn(okJson("{}").withFixedDelay(3_000)));   // longer than the client's timeout
```

The client's real HTTP code, JSON mapping, error handling and timeouts are then tested without the real provider.$hs$, 4, 'SUBMITTED', $hs$WireMock documentation, Simulating Faults https://wiremock.org/docs/simulating-faults/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Contract tests: consumer-driven contracts$hs$, $hs$What is a consumer-driven contract test, and what runs on the consumer's side and on the provider's side?$hs$, $hs$A test that checks a provider against what its consumers actually use. Each consumer writes a contract: the requests it sends, and the response fields it relies on.

- **Consumer side:** the consumer's tests run against a stub of the provider generated from its contract.
- **Provider side:** the provider's build replays every consumer's contract against the real provider and checks the responses. A change that would break a consumer fails the provider's build, before release.$hs$, 2, 'SUBMITTED', $hs$Pact documentation https://docs.pact.io/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$TDD: red, green, refactor$hs$, $hs$What is test-driven development?$hs$, $hs$Developing in short cycles, driven by tests:

1. Red: write a small test for the next bit of behaviour, and see it fail.
2. Green: write the simplest code that makes it pass.
3. Refactor: improve the code's design while all tests stay green.

Then repeat with the next small behaviour.$hs$, 1, 'SUBMITTED', $hs$Martin Fowler, Test Driven Development https://martinfowler.com/bliki/TestDrivenDevelopment.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$TDD: what it brings besides tests$hs$, $hs$Besides a suite of tests, what does test-driven development bring?$hs$, $hs$- Design feedback: code written to be tested first tends to be small, decoupled and easy to use, because the test is its first client.
- Confidence that every test can fail: each one was seen red, for the right reason.
- A fast feedback loop and small steps, so mistakes are found within minutes.
- Safe refactoring, since behaviour is covered as it's built.$hs$, 4, 'SUBMITTED', $hs$Martin Fowler, Test Driven Development https://martinfowler.com/bliki/TestDrivenDevelopment.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Quality: what load tests are$hs$, $hs$What is a load test, what does it measure, and which tools are commonly used?$hs$, $hs$A test that sends realistic traffic to the system, at the expected load and beyond, to measure throughput, response times (percentiles such as p95 and p99) and error rates, and to find the point where it degrades.

Tools: Gatling, k6, Apache JMeter. Run them against a production-like environment, with realistic data volumes.$hs$, 3, 'SUBMITTED', $hs$Gatling documentation https://docs.gatling.io/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-testing', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: safe methods$hs$, $hs$What does it mean for an HTTP method to be safe, and which methods are?$hs$, $hs$A safe method is read-only: the client doesn't request any change of state on the server. `GET`, `HEAD`, `OPTIONS` and `TRACE` are safe.

That's why `GET /accounts/42/balance` may be prefetched, cached and retried freely, and why a `GET` must never trigger a transfer.$hs$, 1, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §9.2.1 Safe Methods https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: idempotent methods$hs$, $hs$What does it mean for an HTTP method to be idempotent, and which methods are?$hs$, $hs$Sending the same request several times has the same intended effect on the server as sending it once.

Idempotent: `GET`, `HEAD`, `OPTIONS`, `TRACE`, `PUT` and `DELETE`. Not idempotent: `POST` and `PATCH`.

That's why clients may retry idempotent requests safely after a timeout, but not `POST`.$hs$, 2, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §9.2.2 Idempotent Methods https://www.rfc-editor.org/rfc/rfc9110#name-idempotent-methods$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: PUT vs PATCH vs POST$hs$, $hs$What is the difference between `PUT`, `PATCH` and `POST` when changing data?$hs$, $hs$- `PUT /accounts/42/limits`: replaces the whole resource at that URL with the representation sent. Idempotent.
- `PATCH /accounts/42/limits`: applies a partial change, e.g. only the daily limit. Not necessarily idempotent.
- `POST /accounts/42/transfers`: asks the server to process the request, typically creating a new resource under that URL. Not idempotent: two `POST`s create two transfers.$hs$, 2, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §9.3 Method Definitions https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: 401 vs 403$hs$, $hs$When should an API answer `401 Unauthorized`, and when `403 Forbidden`?$hs$, $hs$- `401`: the request isn't authenticated: no credentials, or invalid or expired ones. Authenticating may help.
- `403`: the client is authenticated, but isn't allowed to do this, e.g. viewing another customer's account. Authenticating again won't help.$hs$, 2, 'SUBMITTED', $hs$MDN Web Docs, HTTP response status codes (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: 400 vs 409 vs 422$hs$, $hs$For a transfer request, when would an API answer `400 Bad Request`, `409 Conflict` or `422 Unprocessable Content`?$hs$, $hs$- `400`: the request itself is malformed or invalid: unparseable JSON, a missing required field, an amount that isn't a number.
- `422`: the request is well-formed but semantically invalid; some APIs use it instead of `400` for validation failures, e.g. a negative amount.
- `409`: the request conflicts with the resource's current state, e.g. the same transfer request was already processed, or the account was changed since the client loaded it.$hs$, 3, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §15.5 Client Error 4xx https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: returning 201 Created$hs$, $hs$`POST /transfers` creates a transfer. Complete the method so it answers `201 Created`, with a `Location` header pointing to the new transfer.

```java
@PostMapping("/transfers")
ResponseEntity<TransferDto> create(@RequestBody NewTransfer request) {
    TransferDto created = transferService.create(request);
    // ?
}
```$hs$, $hs$```java
@PostMapping("/transfers")
ResponseEntity<TransferDto> create(@RequestBody NewTransfer request) {
    TransferDto created = transferService.create(request);
    return ResponseEntity
            .created(URI.create("/transfers/" + created.id()))   // 201 + Location header
            .body(created);
}
```$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, ResponseEntity https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: deleting twice$hs$, $hs$`DELETE /standing-orders/42` succeeds. What should the same request return the second time, and is `DELETE` still idempotent?$hs$, $hs$The first call returns `204 No Content` (or `200` with a body). The second can return `404 Not Found` (it no longer exists) or `204` again; both are defensible, as long as the API is consistent and documents it.

`DELETE` is still idempotent: idempotency is about the effect on the server, which is the same (the standing order is gone), not about getting the same response.$hs$, 3, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §9.3.5 DELETE https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: always returning 200$hs$, $hs$An API answers every request with `200 OK`, and reports errors as `{"success": false, "error": "..."}` in the body. Why is that a bad design?$hs$, $hs$Everything between client and server relies on status codes: HTTP clients and their error handling, retries, caches and proxies (which may cache the "successful" error), monitoring and alerting on error rates, and load balancers. Hiding errors behind `200` breaks all of them, and forces every client to parse each body to find out whether a call failed.$hs$, 4, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, §15 Status Codes https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: retrying a timed-out payment$hs$, $hs$A client calls `POST /payments` and gets no response before its timeout. Why is retrying the request unsafe, and how can the API make retries safe?$hs$, $hs$The timeout doesn't tell the client whether the payment went through; maybe only the response was lost. `POST` isn't idempotent, so a retry can pay twice.

Make it safe with an idempotency key:
1. The client generates a unique key per payment and sends it in an `Idempotency-Key` header, reusing it on every retry.
2. The server stores the key with the result, in the same transaction as the payment, under a unique constraint.
3. A request with a known key gets the stored result back, and nothing is paid again.$hs$, 5, 'SUBMITTED', $hs$IETF draft, The Idempotency-Key HTTP Header Field https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: telling a client to slow down$hs$, $hs$A partner exceeds its limit of 100 requests per minute. Which status code does the API answer, and which header tells the partner when to try again?$hs$, $hs$`429 Too Many Requests`, with a `Retry-After` header: the number of seconds to wait (or a date).

```http
HTTP/1.1 429 Too Many Requests
Retry-After: 30
```

A well-behaved client waits that long before its next attempt, instead of retrying at once and making the overload worse.$hs$, 3, 'SUBMITTED', $hs$RFC 6585, Additional HTTP Status Codes (429 Too Many Requests) https://www.rfc-editor.org/rfc/rfc6585#section-4$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: caching an account balance$hs$, $hs$`GET /accounts/42/balance` returns a customer's balance. Which `Cache-Control` value should the response carry, and why is `public, max-age=60` wrong here?$hs$, $hs$`Cache-Control: no-store`: no cache, in the browser or on the way, may keep a copy.

`public` allows shared caches (a corporate proxy, a CDN) to store the response and serve it to other users, and `max-age=60` lets everyone see a balance up to a minute old. A balance is personal and changes with every transfer. (`private, no-cache` would at least keep it to the user's own browser, revalidated on every use.)$hs$, 4, 'SUBMITTED', $hs$MDN Web Docs, Cache-Control (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: a rate limit keyed on X-Forwarded-For$hs$, $hs$A Spring Boot API runs behind a load balancer that appends the client's address to `X-Forwarded-For`. So that `request.getRemoteAddr()` returns the real client, the team sets `server.forward-headers-strategy=framework`, and limits each client to 300 requests a minute by that address. An attacker sends every request with a different made-up `X-Forwarded-For` value and is never limited. Why, and how do you fix it?

```java
String key = request.getRemoteAddr();   // one counter per key per minute
```$hs$, $hs$`X-Forwarded-For` is a list: the client may send its own value, and each proxy appends the address it received the request from. With the `framework` strategy, Spring's `ForwardedHeaderFilter` takes the **leftmost** entry as the client's address, and the leftmost entry is the one the attacker wrote (`X-Forwarded-For: 198.51.100.7, <real address>`). Every request therefore gets a new key, and a new counter.

Trust only what your own proxies added:
- Use `server.forward-headers-strategy=native` (Tomcat's `RemoteIpValve`). It reads the list **from the right** and stops at the first address that isn't a trusted proxy, so client-written entries are ignored. Configure the trusted proxy addresses if they aren't on a private network.
- Or have the proxy overwrite the header instead of appending to it.

In either case, the service must be reachable only through the proxy.$hs$, 7, 'SUBMITTED', $hs$Spring Boot docs, Running Behind a Front-end Proxy Server https://docs.spring.io/spring-boot/how-to/webserver.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'spring-security']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: long polling vs WebSockets vs server-sent events$hs$, $hs$A banking app must show a payment's status the moment it changes, without the user refreshing. What are long polling, server-sent events and WebSockets, and which fits this case?$hs$, $hs$All three let a server deliver updates to a browser as they happen:
- **Long polling:** the client sends an ordinary HTTP request, and the server holds it open until there is news (or a timeout), then the client immediately asks again. It works everywhere but costs a request per update.
- **Server-sent events (SSE):** one long-lived HTTP response through which the server streams events to the client (`text/event-stream`), one way only. Browsers reconnect automatically and resume from the last event id.
- **WebSockets:** a connection upgraded from HTTP to a full-duplex channel, where both sides send messages at any time. Suited to two-way, high-frequency exchanges such as chat or trading screens.

Status updates flow one way, from server to client, so **server-sent events** fit best: plain HTTP, automatic reconnection, and simpler to run through proxies than WebSockets. In Spring MVC, a controller returns an `SseEmitter`.$hs$, 3, 'SUBMITTED', $hs$MDN, Using server-sent events (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: what makes an API RESTful$hs$, $hs$What makes an HTTP API RESTful?$hs$, $hs$It's organised around resources and uses HTTP as designed:

- Resources are identified by URIs, named with nouns: `/accounts/42/transfers`, not `/getTransfers`.
- HTTP methods carry the action: `GET` reads, `POST` creates, `PUT` replaces, `PATCH` updates part, `DELETE` removes.
- Requests are stateless: each carries everything the server needs.
- Status codes report the outcome, and responses can be cached.$hs$, 1, 'SUBMITTED', $hs$Azure Architecture Center, Web API design best practices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: designing the URLs$hs$, $hs$Design the requests to list a customer's accounts, get one account, and block one of its cards.$hs$, $hs$```
GET  /customers/{customerId}/accounts
GET  /accounts/{accountId}
POST /cards/{cardId}/block           (or: PATCH /cards/{cardId} with {"status": "BLOCKED"})
```

Resources are nouns; the action comes from the method. A state change like blocking is modelled as a sub-resource or a status update, never as a verb in the URL like `/blockCard`.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Web API design best practices (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: pagination, filtering and sorting$hs$, $hs$How would you let clients page, filter and sort `GET /accounts/{id}/transactions`?$hs$, $hs$With optional query parameters on the collection resource:

```
GET /accounts/42/transactions?from=2026-09-01&type=CARD&sort=bookedAt,desc&size=50&cursor=eyJ...
```

- **Paging:** a page size with a maximum enforced by the server, and either a cursor (stable under concurrent inserts, efficient for long lists) or a page number for small lists. The response tells the client how to fetch the next page, with a `next` link or cursor.
- **Filtering:** each filter is a query parameter; filters apply before paging, so they change the total number of items and pages.
- **Sorting:** a `sort` parameter, limited to an allowed list of fields that the database can serve efficiently (with an index).$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Web API design best practices (filtering and pagination) https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: offset vs cursor pagination$hs$, $hs$For a long, constantly growing list of transactions, what are the trade-offs between offset pagination (`?page=500&size=20`) and cursor pagination (`?after=<cursor>`)?$hs$, $hs$- Offset: simple, and clients can jump to any page. But deep pages get slow (the database still reads and discards the skipped rows), and when new transactions arrive between requests, items shift: the client sees duplicates or misses some.
- Cursor: the cursor encodes the position of the last item seen (e.g. its timestamp and id), so each page is a fast index range scan, and new items don't shift it. But clients can only move forward (or back), not jump to page 500.

For feeds and statements, cursors; for small admin lists, offsets are fine.$hs$, 5, 'SUBMITTED', $hs$Use The Index, Luke: Fetching the next page https://use-the-index-luke.com/sql/partial-results/fetch-next-page$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: returning JPA entities from controllers$hs$, $hs$Why is returning JPA entities such as `Account` directly from controllers a problem?$hs$, $hs$- The API is coupled to the database model: renaming a column changes the API.
- Internal fields leak (version numbers, internal flags, relations), possibly sensitive ones.
- Serialisation touches lazy relations: extra queries during JSON writing, or `LazyInitializationException`.
- Bidirectional relations can recurse endlessly.

Return DTOs (e.g. records) shaped for each endpoint.$hs$, 5, 'SUBMITTED', $hs$Spring Boot docs, SQL Databases (open EntityManager in view) https://docs.spring.io/spring-boot/reference/data/sql.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: a request that takes minutes$hs$, $hs$A partner submits a batch of 1,000 payments in one `POST /payment-batches`. Processing takes several minutes, longer than any client should keep a connection open. Which status does the `POST` return, and how does the partner learn the outcome?$hs$, $hs$`202 Accepted`: the request was received and will be processed, but isn't done yet. The response points to a status resource:

```http
HTTP/1.1 202 Accepted
Location: /payment-batches/7f3c
```

The partner polls `GET /payment-batches/7f3c`, which reports the state (`PROCESSING`, `DONE`) and, when done, each payment's result, so partial failures are visible per payment. Alternatively the API calls the partner's webhook when the batch is finished.$hs$, 6, 'SUBMITTED', $hs$RFC 9110, HTTP Semantics (202 Accepted) https://www.rfc-editor.org/rfc/rfc9110#name-202-accepted$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: the order of the 404, 403, 400 and 409 checks$hs$, $hs$`PUT /workouts/{id}` updates a workout. Four things can be wrong at once: the workout doesn't exist (404), the caller isn't its owner (403), the body is invalid (400), or the workout is already finished and can't change (409). In which order should the API check them, and why?$hs$, $hs$404, then 403, then 400, then 409:
1. **404 Not Found:** without the resource, no other question makes sense.
2. **403 Forbidden:** authorization next, so a caller who may not touch the resource learns nothing about the validation rules or its state.
3. **400 Bad Request:** the request itself must be valid before it is applied.
4. **409 Conflict:** last, because it depends on the resource's current state, which matters only for an authorized, valid request.

Each response then names the most fundamental problem, and the result doesn't depend on the order in which the code happened to check things. One refinement: checking 404 before 403 tells any caller that the id exists. For resources that must stay private, such as another customer's account, answer 404 to callers who may not see them.$hs$, 5, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, Status Codes https://www.rfc-editor.org/rfc/rfc9110$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$REST: REST vs GraphQL vs gRPC$hs$, $hs$What are REST, GraphQL and gRPC, and when does each fit best?$hs$, $hs$- **REST** is an architectural style for HTTP APIs built around resources, identified by URLs and manipulated with the standard methods (`GET`, `POST`, `PUT`, `DELETE`), usually with JSON. It is simple, cacheable and universally supported: the default for public and browser-facing APIs.
- **GraphQL** is a query language for APIs: the client sends one query to a single endpoint and receives exactly the fields it asked for, possibly across several related objects. It suits clients with varied data needs, such as mobile apps, at the cost of harder caching and the need to limit query cost.
- **gRPC** is a remote procedure call framework: services and messages are defined in Protocol Buffers, calls run over HTTP/2 with a compact binary encoding, and client and server code are generated. It suits fast internal service-to-service calls and streaming, but browsers can't call it directly.$hs$, 3, 'SUBMITTED', $hs$gRPC, Introduction to gRPC https://grpc.io/docs/what-is-grpc/introduction/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring MVC: the DispatcherServlet$hs$, $hs$What role does the `DispatcherServlet` play in Spring MVC?$hs$, $hs$It's the front controller: one servlet that receives every request and delegates the processing to configurable components.

- `HandlerMapping` finds the handler method for the request.
- `HandlerAdapter` invokes it, resolving its arguments.
- `HttpMessageConverter`s read and write JSON bodies.
- `HandlerExceptionResolver`s handle exceptions (`@ExceptionHandler`, `@ControllerAdvice`).$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, DispatcherServlet https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring MVC: from request to JSON response$hs$, $hs$Walk through what happens between a `POST /transfers` request with a JSON body arriving and the `@RestController` method's return value being written as JSON.$hs$, $hs$1. Servlet filters run (e.g. Spring Security's filter chain).
2. The `DispatcherServlet` asks the handler mappings for the method matching the path and HTTP method.
3. A handler adapter prepares the arguments: an `HttpMessageConverter` (Jackson) reads the JSON body into the `@RequestBody` parameter, and `@Valid` triggers validation.
4. The method runs.
5. The return value is written to the response by an `HttpMessageConverter` (Jackson), with the status code.
6. If anything throws, the exception resolvers (`@ExceptionHandler`, `@ControllerAdvice`) produce the error response.$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, DispatcherServlet (processing) https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring MVC: @Controller vs @RestController$hs$, $hs$What is the difference between `@Controller` and `@RestController` in Spring MVC?$hs$, $hs$`@RestController` is `@Controller` plus `@ResponseBody`: every handler method's return value is written to the response body (usually as JSON) instead of being resolved as a view name.

```java
@Controller
class GymPages {
    @GetMapping("/gym")
    String gym() { return "gym"; }                // renders the "gym" view template
}

@RestController
class WorkoutApi {
    @GetMapping("/api/workouts/{id}")
    Workout workout(@PathVariable long id) { ... } // written as JSON
}
```$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Annotated Controllers https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring MVC: @PathVariable vs @RequestParam$hs$, $hs$Write the handler method signature for `GET /athletes/42/workouts?from=2026-01-01`, binding the athlete id and the date.$hs$, $hs$The id is part of the path, so `@PathVariable`; the date is a query parameter, so `@RequestParam`.

```java
@GetMapping("/athletes/{athleteId}/workouts")
List<Workout> workouts(@PathVariable long athleteId,
                       @RequestParam LocalDate from) { ... }
```

`@RequestParam(required = false)` or a `defaultValue` makes the parameter optional.$hs$, 1, 'SUBMITTED', $hs$Spring Framework docs, Annotated Controllers https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring MVC: a servlet filter vs a HandlerInterceptor$hs$, $hs$What is the difference between a servlet `Filter` and a Spring MVC `HandlerInterceptor`?$hs$, $hs$- A filter runs in the servlet container, around the whole request, before Spring MVC is even involved. It sees every request, including static resources, and can wrap the request and response. Typical uses: security, logging, compression, correlation ids.
- An interceptor runs inside Spring MVC, around the chosen handler: it knows which controller method will handle the request (`preHandle`, `postHandle`, `afterCompletion`).$hs$, 3, 'SUBMITTED', $hs$Spring Framework docs, Interceptors https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Validation: a @Valid request body that fails$hs$, $hs$`NewWorkout` has `@NotBlank String name`. What happens when a client posts an empty name to this method?

```java
@PostMapping("/workouts")
Workout create(@Valid @RequestBody NewWorkout request) { ... }
```$hs$, $hs$Bean Validation fails before the method runs: Spring throws `MethodArgumentNotValidException`, the method body is never executed, and the client gets `400 Bad Request`.

An `@ExceptionHandler` (or a `@RestControllerAdvice`) can turn the field errors into a clearer response body.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Validation (annotated controllers) https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Validation: a forgotten @Valid$hs$, $hs$`NewTransfer` has `@NotNull` and `@Positive` on its `amount`. What happens when a client posts a transfer without an amount?

```java
@PostMapping("/transfers")
TransferDto create(@RequestBody NewTransfer request) { ... }
```$hs$, $hs$The request is accepted: without `@Valid` (or `@Validated`) on the parameter, Spring doesn't run Bean Validation, and the invalid object reaches the service, which may fail later with a `NullPointerException`, or worse, store bad data.

```java
TransferDto create(@Valid @RequestBody NewTransfer request) { ... }
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Validation (annotated controllers) https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Validation: where each rule belongs$hs$, $hs$Where should each of these rules be checked: "the amount is a positive number", "the account has enough funds", "the IBAN is unique"?$hs$, $hs$- "Positive number": at the edge, with Bean Validation on the request DTO; it's about the request's shape.
- "Enough funds": in the service or domain, inside the transaction; it depends on current data.
- "Unique IBAN": in the database, as a unique constraint; only the database can guarantee it under concurrency. The service may check first, for a friendlier error.$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Validation (annotated controllers) https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: @ControllerAdvice with @ExceptionHandler$hs$, $hs$What is a `@ControllerAdvice` with `@ExceptionHandler` methods for?$hs$, $hs$Handling exceptions from all controllers in one place: each `@ExceptionHandler` method maps an exception type to a response, with a status code and an error body. Controllers and services just throw meaningful exceptions; the error format stays consistent across the API. `@RestControllerAdvice` adds `@ResponseBody`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Error Responses https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: RFC 9457 Problem Details$hs$, $hs$What is RFC 9457 Problem Details, and how does Spring support it?$hs$, $hs$A standard JSON format for HTTP API errors (media type `application/problem+json`), with the fields `type` (a URI for the kind of problem), `title`, `status`, `detail` and `instance`, plus any extensions.

Spring represents it as `ProblemDetail`; exception handlers can return one, and Spring Boot renders Spring MVC's own exceptions in that format with `spring.mvc.problemdetails.enabled=true`.$hs$, 3, 'SUBMITTED', $hs$RFC 9457 Problem Details for HTTP APIs https://www.rfc-editor.org/rfc/rfc9457$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: Problem Details for a missing account$hs$, $hs$`GET /accounts/42` should answer 404 in the Problem Details format when the account doesn't exist. Return it from a `@RestControllerAdvice`.$hs$, $hs$```java
@RestControllerAdvice
class ApiErrors extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    ProblemDetail accountNotFound(AccountNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Account not found");
        return problem;   // rendered as application/problem+json with status 404
    }
}
```$hs$, 4, 'SUBMITTED', $hs$Spring Framework docs, Error Responses https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: what never goes into an error response$hs$, $hs$What must an error response from a production API never contain, and what do you return instead?$hs$, $hs$Never: stack traces, SQL statements, internal class or table names, configuration values, or other customers' data. They help attackers and confuse clients.

Instead: a stable, meaningful message and error type, plus a correlation id (e.g. the trace id). The full details go to the server's logs under that id, so support can find them.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Error Responses https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Errors: a catch-all handler that turns 405 into 500$hs$, $hs$After this catch-all handler was added, requests with the wrong HTTP method, an unsupported content type or an unknown URL, which used to answer 405, 415 and 404, all answer 500 and are logged as errors. Why, and how do you fix the handler?

```java
@RestControllerAdvice
class ApiErrors {
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> unexpected(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError().body(Map.of("message", "Something went wrong"));
    }
}
```$hs$, $hs$`@ExceptionHandler` methods in a controller advice are tried before Spring MVC's own default handling. `Exception.class` therefore also catches the exceptions Spring MVC uses for client errors, such as `HttpRequestMethodNotSupportedException` (405), `HttpMediaTypeNotSupportedException` (415) and `NoResourceFoundException` (404), and turns them into 500s.

These exceptions implement `ErrorResponse`, which carries the right status and headers. Keep them:

```java
@ExceptionHandler(Exception.class)
ResponseEntity<Map<String, String>> unexpected(Exception e) {
    if (e instanceof ErrorResponse error) {   // Spring's own client errors: keep status and headers (e.g. Allow)
        return ResponseEntity.status(error.getStatusCode()).headers(error.getHeaders())
                .body(Map.of("message", "Request failed"));
    }
    log.error("Unexpected error", e);
    return ResponseEntity.internalServerError().body(Map.of("message", "Something went wrong"));
}
```

Alternatively, extend `ResponseEntityExceptionHandler`, which already handles all of them.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Exceptions (@ExceptionHandler) https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JSON: how Spring converts objects$hs$, $hs$How does Spring MVC turn a returned `AccountDto` into JSON, and a JSON request body into a `NewTransfer`?$hs$, $hs$Through an `HttpMessageConverter` backed by Jackson's `ObjectMapper`, chosen by the content type (`application/json`). Spring Boot auto-configures the `ObjectMapper` (e.g. Java time support, ignoring unknown properties), and lets you customise it with `spring.jackson.*` properties or customizer beans.$hs$, 2, 'SUBMITTED', $hs$Spring Boot docs, JSON https://docs.spring.io/spring-boot/reference/features/json.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JSON: an endless response$hs$, $hs$A controller returns an `Account` entity with a `List<Card> cards`, and each `Card` has a back-reference `Account account`. The request fails with a `StackOverflowError` (or a nesting depth error). Why?$hs$, $hs$Jackson serialises every property, following references: account → cards → each card's account → its cards → ... forever.

Return DTOs without back-references. `@JsonIgnore` on `Card.account` (or `@JsonManagedReference` / `@JsonBackReference`) only patches the symptom.$hs$, 4, 'SUBMITTED', $hs$Jackson documentation https://github.com/FasterXML/jackson-docs$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'spring-data']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$JSON: dates and times$hs$, $hs$How should the booking time of a transaction appear in a JSON API, and which Java type should hold it?$hs$, $hs$An ISO-8601 string with an offset, or in UTC: `"bookedAt": "2026-10-06T14:05:00Z"`, held as an `Instant` (or `OffsetDateTime`).

Spring Boot configures Jackson's Java time support and writes dates as ISO strings rather than numeric timestamps. Never send local date-times without a zone for moments in time: clients in other time zones misread them.$hs$, 4, 'SUBMITTED', $hs$Spring Boot docs, JSON https://docs.spring.io/spring-boot/reference/features/json.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API evolution: compatible and breaking changes$hs$, $hs$Which changes to a JSON API are backward compatible, and which break existing clients?$hs$, $hs$Compatible: adding an endpoint, adding an optional request field or parameter, adding a response field (for tolerant clients).

Breaking: removing or renaming a field, changing a field's type or meaning, making an optional field required, changing status codes or error formats, changing URLs.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Web API design best practices (versioning) https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API evolution: versioning approaches$hs$, $hs$Compare versioning an API in the URL (`/v2/transfers`), in a custom header, and in the `Accept` media type.$hs$, $hs$- URL: visible, easy to route, test and cache; but the resource's URL changes with each version.
- Custom header (`Api-Version: 2`): URLs stay stable; less visible, and caches must vary on the header.
- Media type (`Accept: application/vnd.bank.v2+json`): the most "HTTP" way, versioning the representation; the least convenient for clients and tools.

Whichever you choose: version rarely, and evolve compatibly in between.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Web API design best practices (versioning) https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$API evolution: documenting with OpenAPI$hs$, $hs$What is OpenAPI, and what's the difference between code-first and contract-first?$hs$, $hs$The OpenAPI Specification is a standard, language-independent format (JSON or YAML) for describing HTTP APIs: endpoints, parameters, request and response schemas, and errors. Tools generate documentation, client libraries, server stubs and tests from such a description.

- **Code-first:** the controllers are written first, and the description is generated from them (e.g. with springdoc). Fast for internal APIs.
- **Contract-first:** the description is written and agreed first, then server interfaces and clients are generated from it. Better for APIs shared across teams or companies, because the contract is reviewed before any code exists.$hs$, 4, 'SUBMITTED', $hs$OpenAPI Specification https://spec.openapis.org/oas/latest.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: what an ETag is$hs$, $hs$In HTTP, what is an ETag, and what are the `If-None-Match` and `If-Match` request headers used for?$hs$, $hs$An ETag (entity tag) is an opaque identifier of the current version of a resource's representation, such as a version number or a hash of the content. The server returns it in the `ETag` response header, and clients send it back in conditional requests:

- `If-None-Match: <etag>` on a `GET` means "send the resource only if it has changed". If it hasn't, the server answers `304 Not Modified` without a body, which saves bandwidth (caching).
- `If-Match: <etag>` on a `PUT` or `PATCH` means "apply the change only if the resource is still this version". If it isn't, the server answers `412 Precondition Failed`, which prevents lost updates (optimistic concurrency control).$hs$, 3, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, Conditional Requests https://www.rfc-editor.org/rfc/rfc9110#name-conditional-requests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP: preventing a lost update between two clients$hs$, $hs$Two bank employees open the same customer's address in their browsers and both save a change. How does the API stop the second save from silently overwriting the first?$hs$, $hs$Use the resource's version (e.g. the entity's `@Version`) as an ETag:

1. `GET /customers/42` returns `ETag: "7"`.
2. Each client sends its change with `If-Match: "7"`.
3. The server applies the first change (version becomes 8). The second request still says `If-Match: "7"`, which no longer matches, so the server answers `412 Precondition Failed`, and that client must reload and redo its change.

The `@Version` check in the database also catches races between the check and the update.$hs$, 6, 'SUBMITTED', $hs$RFC 9110 HTTP Semantics, Conditional Requests https://www.rfc-editor.org/rfc/rfc9110#name-conditional-requests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP clients: RestTemplate, RestClient, WebClient$hs$, $hs$Spring offers `RestTemplate`, `RestClient`, `WebClient` and HTTP interface clients. What is each?$hs$, $hs$- `RestTemplate`: the original synchronous client, in maintenance mode.
- `RestClient`: its modern synchronous successor, with a fluent API.
- `WebClient`: the reactive, non-blocking client, from WebFlux.
- HTTP interfaces: you declare a Java interface with `@HttpExchange` (`@GetExchange`, ...) methods, and Spring generates the client on top of `RestClient` or `WebClient`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, REST Clients https://docs.spring.io/spring-framework/reference/integration/rest-clients.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP clients: timeouts$hs$, $hs$Why must every outgoing HTTP call, e.g. with `RestClient`, have explicit connect and read timeouts?$hs$, $hs$A timeout bounds how long a call may wait: the connect timeout for establishing the connection, the read timeout for the response. The defaults depend on the underlying HTTP library, and several have no read timeout at all.

Without one, a hanging downstream service makes every thread that calls it wait indefinitely. The thread pool drains, the service stops answering its own callers, and the failure spreads through the system: a cascading failure.

Set both timeouts explicitly, through Spring Boot's HTTP client properties or on the client's request factory, sized to how long the caller can afford to wait.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, REST Clients https://docs.spring.io/spring-framework/reference/integration/rest-clients.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP clients: handling 404 and 500 differently$hs$, $hs$When the exchange-rate service answers 404, the currency is unsupported; when it answers 5xx, it's failing. Handle the two differently with `RestClient`.$hs$, $hs$```java
Rate rate = restClient.get()
        .uri("/rates/{currency}", currency)
        .retrieve()
        .onStatus(status -> status.value() == 404, (request, response) -> {
            throw new UnsupportedCurrencyException(currency);          // a domain answer
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
            throw new RateServiceUnavailableException(response.getStatusCode());   // retry or report
        })
        .body(Rate.class);
```

Without handlers, `retrieve()` throws `HttpClientErrorException` / `HttpServerErrorException` for 4xx and 5xx responses.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, REST Clients (RestClient, error handling) https://docs.spring.io/spring-framework/reference/integration/rest-clients.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$WebFlux: Spring MVC vs Spring WebFlux$hs$, $hs$What is the difference between Spring MVC and Spring WebFlux?$hs$, $hs$- Spring MVC: built on the Servlet API, one thread per request, blocking I/O.
- Spring WebFlux: non-blocking, built on Reactor (`Mono`, `Flux`), running on a small number of event-loop threads (Netty by default); a request never holds a thread while waiting.

Both support annotated controllers; WebFlux needs non-blocking libraries all the way down.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Spring WebFlux Overview https://docs.spring.io/spring-framework/reference/web/webflux/new-framework.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$WebFlux: a blocking call on the event loop$hs$, $hs$What happens when a WebFlux handler calls a blocking JDBC repository directly?$hs$, $hs$It blocks one of the few event-loop threads for the whole query. Under load, all event-loop threads end up blocked and the whole service stalls, including requests that never touch the database.

Use non-blocking drivers (R2DBC), or move the blocking call to a bounded scheduler (`Schedulers.boundedElastic()`); or use Spring MVC if most of the stack is blocking.$hs$, 5, 'SUBMITTED', $hs$Spring Framework docs, Spring WebFlux Overview https://docs.spring.io/spring-framework/reference/web/webflux/new-framework.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['spring-web', 'java-concurrency']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Method: the steps of a system design interview$hs$, $hs$What steps do you follow in a system design interview?$hs$, $hs$1. Clarify requirements: what the system must do, for whom, and the non-functional needs (scale, latency, availability, consistency).
2. Estimate: requests per second, data size and growth.
3. Sketch the API and the high-level design: clients, services, data stores.
4. Choose the data model and storage.
5. Deep-dive into the hard parts: the bottleneck, the consistency-critical path.
6. Discuss scaling, failures and trade-offs, and what you'd do differently at 10× the load.$hs$, 2, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Method: functional vs non-functional requirements$hs$, $hs$What is the difference between functional and non-functional requirements? Give five non-functional requirements of a payment system.$hs$, $hs$- Functional: what the system does. "A customer can send money to another account."
- Non-functional: how well it does it, the qualities it must have.

For payments:
- consistency: money is never lost or duplicated
- availability: e.g. 99.99%
- latency: e.g. p99 under 500 ms for a transfer
- throughput: peak transfers per second
- security, auditability and compliance (every change traceable).$hs$, 2, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Method: the most common interview mistake$hs$, $hs$What is the most common mistake candidates make in a system design interview?$hs$, $hs$Jumping straight into components (Kafka, Redis, microservices) before clarifying the requirements and the scale. The design then solves the wrong problem, or is over-engineered for 100 users or under-engineered for 100 million.

Close behind: presenting choices without their trade-offs, and never naming what happens when a component fails.$hs$, 3, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Estimates: requests per second$hs$, $hs$A banking app has 10 million daily active users, each making 20 requests a day. What is the average request rate, and what peak would you plan for?$hs$, $hs$10 million × 20 = 200 million requests a day.
200 million ÷ 86,400 seconds ≈ 2,300 requests per second on average.

Traffic isn't even: plan for a peak of 2–5× the average, roughly 5,000–10,000 requests per second, more around salary day or month end. Useful shortcut: 1 million requests a day ≈ 12 per second.$hs$, 3, 'SUBMITTED', $hs$System Design Primer, Back-of-the-envelope calculations (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Estimates: storage$hs$, $hs$A bank books 50 million transactions a day, at about 1 KB each including indexes, and must keep them for 10 years. Estimate the storage.$hs$, $hs$50 million × 1 KB = 50 GB a day.
50 GB × 365 ≈ 18 TB a year.
18 TB × 10 years ≈ 180 TB.

With 3 copies for replication, about 550 TB, plus backups. That's beyond one database server, which points to partitioning by time (and archiving old years to cheaper storage).$hs$, 4, 'SUBMITTED', $hs$System Design Primer, Back-of-the-envelope calculations (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Estimates: latency numbers$hs$, $hs$Give the rough orders of magnitude for: a main memory reference, a random SSD read, a round trip within a datacentre, and a round trip between continents.$hs$, $hs$- Main memory reference: about 100 nanoseconds.
- Random SSD read: about 100 microseconds (1,000× memory).
- Round trip within a datacentre: about 0.5 milliseconds.
- Round trip between continents: about 100–150 milliseconds.

So a remote call costs thousands of memory accesses, and a cross-region call in a request path costs more than everything else combined.$hs$, 3, 'SUBMITTED', $hs$System Design Primer, Latency numbers every programmer should know (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'java-performance']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: vertical vs horizontal$hs$, $hs$What is the difference between vertical and horizontal scaling?$hs$, $hs$- Vertical (scale up): a bigger machine, with more CPU and memory. Simple, no code changes, but limited by the largest machine, and one machine is a single point of failure.
- Horizontal (scale out): more machines behind a load balancer. Scales much further and adds redundancy, but requires stateless services and shared data stores.$hs$, 1, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: stateless services$hs$, $hs$Why must a service be stateless to scale horizontally, and where does the state go?$hs$, $hs$Any instance must be able to serve any request, so instances can be added, removed and replaced freely. If an instance held a user's session or data in memory, that user would depend on that instance.

The state moves out: sessions to a shared store (Redis, the database) or into tokens; data to databases; files to object storage.$hs$, 2, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: what limits adding instances$hs$, $hs$You double the number of instances of the transfer service, but throughput barely rises. What typically limits it?$hs$, $hs$A resource that all instances share:
- the database: CPU, I/O, connection limits, or lock contention on hot rows (a busy merchant account)
- a downstream service or its rate limit
- a cache node or a hot key
- a single partition of a queue.

More instances can even make it worse: more connections and more contention on the shared resource. Find the bottleneck with metrics before adding capacity.$hs$, 4, 'SUBMITTED', $hs$System Design Primer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: layer 4 vs layer 7 load balancers$hs$, $hs$What is the difference between a layer 4 and a layer 7 load balancer?$hs$, $hs$- Layer 4: works on TCP connections, routing by IP address and port without looking inside. Very fast, works for any protocol.
- Layer 7: understands HTTP: routes by path, host or header, terminates TLS, can retry, rewrite and add headers. More flexible, a bit more costly.$hs$, 2, 'SUBMITTED', $hs$System Design Primer, Load balancer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: sticky sessions$hs$, $hs$What are sticky sessions, and what are their downsides?$hs$, $hs$The load balancer sends all requests of a user to the same instance, typically so the instance can keep the session in memory.

Downsides:
- uneven load: heavy users pile up on some instances
- when an instance dies or is redeployed, its users lose their sessions
- scaling in and rolling deployments become disruptive.

Prefer stateless instances with sessions in a shared store or tokens.$hs$, 3, 'SUBMITTED', $hs$System Design Primer, Load balancer (CC BY 4.0) https://github.com/donnemartin/system-design-primer$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: replication vs partitioning$hs$, $hs$What is the difference between replication and partitioning (sharding), and what does each scale?$hs$, $hs$- Replication: the same data on several nodes. Scales reads and gives availability (a replica can take over), but not writes or storage: every node holds and writes everything.
- Partitioning: the data is split by a key, each node holding a part. Scales writes and storage, but queries across partitions are harder.

Large systems combine them: each partition is replicated.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Sharding pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/sharding$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'databases', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: hot partitions$hs$, $hs$Transactions are partitioned by account id. One partition, holding a large payment processor's settlement account, gets most of the writes. What is this problem, and what can you do?$hs$, $hs$A hot partition: the key's distribution is skewed, so one node does most of the work while the others idle.

- Split the hot key: spread the account's entries over several sub-keys (account id plus a suffix), and aggregate when reading the balance.
- Batch the hot account's writes.
- Give the hot tenant its own dedicated partition or node.
- Choose a partition key with a more even distribution where possible.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Sharding pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/sharding$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: moving work to a queue$hs$, $hs$Why move work like generating PDF statements to a queue processed by background workers?$hs$, $hs$- The request returns quickly; the customer doesn't wait for slow work.
- Spikes are absorbed: the queue grows and workers catch up.
- Failures are retried without the user resending anything.
- Workers scale independently of the web tier, by queue length.

For any work the user doesn't need to see completed in the response.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Competing Consumers pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/competing-consumers$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Scaling: batch vs stream processing$hs$, $hs$Compare batch processing and stream processing, with a banking example of each.$hs$, $hs$- Batch: processes a bounded set of data at intervals. High throughput and simple to rerun, but results are as old as the last run. Nightly interest calculation, monthly statements.
- Stream: processes each event continuously as it arrives. Results within seconds, but state, ordering and failures are harder to handle. Real-time fraud detection on card transactions.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Choose a data store (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/data-store-overview$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'messaging']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Storage: choosing data stores$hs$, $hs$Where would you store: customer accounts and balances, uploaded identity documents, a full-text search over transactions, session data, and a stream of card events for analytics?$hs$, $hs$- Accounts and balances: a relational database (transactions, constraints, strong consistency).
- Identity documents: object storage (e.g. S3), encrypted, with metadata and the object key in the database.
- Full-text search: a search engine (Elasticsearch, OpenSearch), fed from the database.
- Sessions: an in-memory store such as Redis, with expiry.
- Card events: a log such as Kafka, feeding an analytics store or data warehouse.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Choose a data store (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/guide/technology-choices/data-store-overview$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Availability: what the nines allow$hs$, $hs$How much downtime per year do 99.9% and 99.99% availability allow?$hs$, $hs$- 99.9%: about 8.8 hours a year (about 43 minutes a month).
- 99.99%: about 53 minutes a year (about 4.3 minutes a month).

Each extra nine cuts the allowed downtime tenfold, and usually costs much more to achieve.$hs$, 3, 'SUBMITTED', $hs$Google SRE book, Availability Table https://sre.google/sre-book/availability-table/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Availability: SLI, SLO and SLA$hs$, $hs$What are an SLI, an SLO and an SLA? Give an example of each for a payment API.$hs$, $hs$- SLI (indicator): a measured quantity. The share of payment requests that succeed within 500 ms.
- SLO (objective): the target for that indicator over a period. 99.9% of payment requests per 30 days.
- SLA (agreement): a contract with customers stating what they get if the target is missed, e.g. service credits for partners. Usually looser than the internal SLO.

The gap between 100% and the SLO is the error budget the team can spend on releases and risk.$hs$, 4, 'SUBMITTED', $hs$Google SRE book, Service Level Objectives https://sre.google/sre-book/service-level-objectives/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'devops']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: the layers$hs$, $hs$At which layers can the data of a web request be cached, from the user to the database?$hs$, $hs$1. The browser (HTTP caching).
2. A CDN, close to users.
3. A reverse proxy or gateway.
4. The application instance, in memory (e.g. Caffeine).
5. A distributed cache shared by instances (e.g. Redis).
6. The database's own buffer cache.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: local vs distributed$hs$, $hs$Compare a local in-process cache with a distributed cache such as Redis.$hs$, $hs$- Local: the fastest (no network), and simple. But each instance has its own copy, so memory is duplicated, instances can disagree after a change, and the cache is lost on restart.
- Distributed: one copy shared by all instances, consistent between them and surviving restarts. But each read is a network call, and it's one more system to run and to fail.

Often combined: a small, short-lived local cache in front of a distributed one, for the hottest keys.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: what to cache$hs$, $hs$What makes data a good candidate for caching? Give a banking example of data to cache and data not to cache.$hs$, $hs$Good candidates are:
- read far more often than written
- expensive to fetch or compute
- tolerant of some staleness
- a working set that fits in the cache.

Cache: exchange rates for display, branch information, product terms.
Don't cache: the available balance used to authorise a payment, which must always be read fresh.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: the cache-aside pattern$hs$, $hs$In the cache-aside pattern, what does the application do when a read misses the cache, and what does it do when the data is updated?$hs$, $hs$- Read miss: load the item from the database, put it in the cache (with an expiry), and return it.
- Update: write to the database first, then invalidate (delete) the cached item, so the next read loads the fresh value.

The application manages the cache itself; the cache knows nothing about the database. Spring's `@Cacheable` and `@CacheEvict` implement this pattern.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Cache-Aside pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/cache-aside$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: read-through, write-through, write-behind$hs$, $hs$What are read-through, write-through and write-behind caching?$hs$, $hs$- Read-through: the application only talks to the cache; on a miss, the cache itself loads the data from the database.
- Write-through: writes go to the cache, which writes them to the database synchronously before confirming. The cache is always current; writes are slower.
- Write-behind (write-back): writes go to the cache and are written to the database later, in batches. Very fast writes, but data is lost if the cache fails before writing. Never for money.$hs$, 3, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: delete or update on write?$hs$, $hs$In cache-aside, why is it safer to delete the cache entry after a database update than to write the new value into the cache?$hs$, $hs$With two concurrent updates, A and B, the database may end with B's value while the cache writes happen in the opposite order (B's first, then A's). The cache then holds A's stale value until it expires, possibly for hours.

Deleting the entry instead means the next read loads whatever the database holds now. A small race remains (a read loading the old value just before the delete), which a reasonable expiry (TTL) bounds.$hs$, 5, 'SUBMITTED', $hs$Azure Architecture Center, Cache-Aside pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/cache-aside$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: expiry (TTL)$hs$, $hs$What is a TTL, and why should almost every cache entry have one?$hs$, $hs$A time-to-live: after it, the entry expires and the next read loads fresh data.

It's the safety net: even when an invalidation is missed (a write path that forgot to evict, a lost message), the stale data disappears after the TTL. It also bounds how long unused entries take up memory.$hs$, 2, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: invalidating local caches on many instances$hs$, $hs$Fee schedules are cached in local caches on 8 instances. When a fee changes, how do you make all instances drop their copy?$hs$, $hs$Publish an invalidation message on a channel every instance subscribes to (Redis pub/sub, a Kafka topic with a consumer group per instance), and have each instance evict the key when it arrives.

Keep a short TTL as a safety net, since a message can be missed. If staleness must be avoided entirely, use a single distributed cache instead of local copies.$hs$, 4, 'SUBMITTED', $hs$Azure Architecture Center, Caching guidance (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/best-practices/caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: the stampede$hs$, $hs$What is a cache stampede (thundering herd)?$hs$, $hs$A popular cache entry expires, and the many requests that arrive at that moment all miss together. Each one goes to the database to load the same value, so the database gets a sudden burst of identical expensive queries, often enough to slow it down or bring it down.$hs$, 3, 'SUBMITTED', $hs$Wikipedia, Cache stampede (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Cache_stampede$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Caching: preventing a stampede$hs$, $hs$How do you prevent a cache stampede?$hs$, $hs$- Let only one request load a missing key while the others wait for its result (request coalescing): Caffeine's loading cache, or `@Cacheable(sync = true)` in Spring for one instance; a short lock in Redis across instances.
- Refresh popular entries in the background before they expire, serving the old value meanwhile.
- Add random jitter to TTLs, so entries loaded together don't all expire together.$hs$, 5, 'SUBMITTED', $hs$Wikipedia, Cache stampede (CC BY-SA 4.0) https://en.wikipedia.org/wiki/Cache_stampede$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Redis: what it offers$hs$, $hs$What is Redis, and which data structures does it offer? Name three uses besides caching.$hs$, $hs$An in-memory data store, very fast, executing commands one at a time, so each command is atomic. Data structures: strings, hashes, lists, sets, sorted sets, streams, and more.

Besides caching:
- rate limiting (atomic counters with expiry)
- distributed locks and leases
- leaderboards (sorted sets)
- session storage
- simple queues and pub/sub.$hs$, 2, 'SUBMITTED', $hs$Redis documentation, Sorted sets https://redis.io/docs/latest/develop/data-types/sorted-sets/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Redis: persistence$hs$, $hs$Compare Redis's RDB snapshots and AOF persistence.$hs$, $hs$- RDB: point-in-time snapshots of the whole dataset at intervals. Compact files and fast restarts, but writes since the last snapshot are lost on a crash.
- AOF (append-only file): logs every write, replayed on restart. Much less data loss (typically up to a second with the default fsync), but larger files and slower restarts.

They can be combined. A pure cache often needs neither, since it can be rebuilt from the database.$hs$, 4, 'SUBMITTED', $hs$Redis documentation, Persistence https://redis.io/docs/latest/operate/oss_and_stack/management/persistence/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Redis: a leaderboard$hs$, $hs$Build a monthly leaderboard of athletes by total kilos lifted with Redis. Which commands do you use to add a set, get the top 10, and get one athlete's rank?$hs$, $hs$A sorted set per month, with the athlete as member and the total as score:

```
ZINCRBY lifts:2026-10 120 athlete:42     # add 120 kg to athlete 42's total
ZREVRANGE lifts:2026-10 0 9 WITHSCORES   # top 10, highest first
ZREVRANK lifts:2026-10 athlete:42        # athlete 42's rank (0 = first)
```

Each operation is logarithmic in the number of athletes, so it stays fast with millions of them.$hs$, 5, 'SUBMITTED', $hs$Redis documentation, Sorted sets https://redis.io/docs/latest/develop/data-types/sorted-sets/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Spring caching: the annotations$hs$, $hs$What do Spring's `@Cacheable`, `@CachePut` and `@CacheEvict` do?$hs$, $hs$- `@Cacheable`: returns the cached result for these arguments if there is one; otherwise calls the method and caches the result.
- `@CachePut`: always calls the method and stores its result in the cache (to update an entry).
- `@CacheEvict`: removes an entry (or all entries with `allEntries = true`), e.g. when the data changes.

Caching must be enabled with `@EnableCaching`.$hs$, 2, 'SUBMITTED', $hs$Spring Framework docs, Declarative Annotation-based Caching https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'spring-core']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$HTTP caching: Cache-Control$hs$, $hs$What do the `Cache-Control` directives `max-age=3600`, `no-cache` and `no-store` mean?$hs$, $hs$- `max-age=3600`: the response may be stored and reused for 3,600 seconds without asking the server.
- `no-cache`: it may be stored, but must be revalidated with the server before each reuse (a conditional request, cheap when unchanged).
- `no-store`: it must not be stored at all. For sensitive data such as account statements.$hs$, 2, 'SUBMITTED', $hs$MDN Web Docs, HTTP caching (CC BY-SA 2.5) https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: short codes for shared links$hs$, $hs$Athletes share workout plans by short links like `hs.gym/p/aZ3k9Qx`, about 10 million new ones a month. Plans are private to whoever has the link, so codes must not be guessable. How do you generate the 7-character codes, and why not just encode an auto-increment id?$hs$, $hs$Generate 7 random characters from the 62 letters and digits (base62): 62⁷ ≈ 3.5 trillion possible codes. Store each under a unique constraint; on the rare collision, generate another.

An encoded auto-increment id is short and never collides, but it's sequential: anyone can count up from their own link and open other athletes' plans. Random codes from a space that large make guessing a valid one hopeless.$hs$, 6, 'SUBMITTED', $hs$Wikipedia, URL shortening (techniques) (CC BY-SA 4.0) https://en.wikipedia.org/wiki/URL_shortening$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: 301 or 302 for a short link$hs$, $hs$A short link redirects to the full plan page. Short links can expire, and each opening is counted for the athlete's statistics. Should the redirect answer `301 Moved Permanently` or `302 Found`, and why?$hs$, $hs$`302 Found` (or `307`). A `301` is permanent, so browsers cache it and go straight to the target next time, without asking the shortener: those openings are never counted, and an expired link keeps working in every browser that opened it before.

A `302` makes every opening go through the service, at the cost of one more request each time.$hs$, 4, 'SUBMITTED', $hs$RFC 9110, HTTP Semantics (301 Moved Permanently) https://www.rfc-editor.org/rfc/rfc9110#name-301-moved-permanently$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'spring-web']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: uploading 500 MB files without the service in the way$hs$, $hs$Customers upload identity videos of up to 500 MB for remote onboarding. Streaming them through the onboarding service ties up its threads and memory. How can the browser upload straight to object storage, while the service still controls who may upload what?$hs$, $hs$With a presigned URL. The service checks the customer, then asks the object store for a URL that allows exactly one upload (one key, a size limit, a content type) and expires in minutes. The browser uploads the file directly to that URL.

The object store then notifies the service (an event on the bucket), or the browser reports completion, and processing (virus scan, checks) starts from there. The large bytes never pass through the service.$hs$, 6, 'SUBMITTED', $hs$AWS docs, Uploading objects with presigned URLs https://docs.aws.amazon.com/AmazonS3/latest/userguide/PresignedUrlUploadObject.html$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: one rate limit across ten instances$hs$, $hs$Each partner API key may make at most 100 requests per minute, but the API runs on 10 stateless instances behind a load balancer. Why does a limiter in each instance's memory fail, and how does a shared Redis counter enforce the limit?$hs$, $hs$Each instance only sees its own share of the requests: with a local limit of 100, the partner can make up to 1,000 a minute in total; with a limit of 10 per instance, uneven load balancing rejects requests too early.

A counter per key and minute in Redis, shared by all instances:

```
count = INCR rate:{apiKey}:{currentMinute}
if count == 1: EXPIRE rate:{apiKey}:{currentMinute} 60
if count > 100: answer 429 with Retry-After
```

`INCR` is atomic, so concurrent requests can't both take the last slot. Do both commands in one Lua script, so a crash between them can't leave a key without expiry. A fixed window allows a burst of up to 200 across a minute boundary; a sliding window or token bucket smooths that out.$hs$, 7, 'SUBMITTED', $hs$Redis documentation, INCR (Pattern: Rate limiter) https://redis.io/docs/latest/commands/incr/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Redis: a distributed lock with SET NX PX$hs$, $hs$Several instances of a service run the nightly statement job, which must run on only one of them at a time. A developer takes a lock in Redis with `SET statement-job <instance-id> NX PX 60000`. What does the command do, and what can still go wrong?$hs$, $hs$`SET key value NX PX 60000` sets the key only if it doesn't exist (`NX`), with an expiry of 60 seconds (`PX`), in one atomic command. The instance whose `SET` succeeds holds the lock; the expiry releases it if that instance crashes.

What can still go wrong:
- **Releasing someone else's lock:** a plain `DEL` can delete a lock that has already expired and been taken by another instance. Release only if the value is still yours, atomically, with a small Lua script that compares and deletes.
- **The lease runs out while the job is still working:** a long garbage-collection pause or a slow step lets the lock expire, and a second instance starts the job too. Renew the lease while working, and make the work itself safe to run twice, for example with a fencing token (a number that increases with every lock grant, checked by the database it writes to).
- **A single Redis node fails over** and can lose the key, so two holders appear. Where correctness matters, enforce the rule in the database too, e.g. with a unique "job run for this date" row.$hs$, 6, 'SUBMITTED', $hs$Redis docs, Distributed Locks with Redis https://redis.io/docs/latest/develop/use/patterns/distributed-locks/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'distributed-systems']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: soft delete versus erasure under foreign keys$hs$, $hs$Members write exercises that other members answer; every content table has `created_by BIGINT NOT NULL REFERENCES app_user (id)`, and answers reference exercises with `ON DELETE RESTRICT`. A member asks to delete their account. Why not simply `DELETE FROM app_user WHERE id = ?`, and what should the operation do instead?$hs$, $hs$A hard delete either fails on the foreign keys or forces bad choices: `ON DELETE CASCADE` would destroy content other people depend on (and is blocked by the `RESTRICT` on answers), and `SET NULL` violates `NOT NULL`.

The usual design is a **soft delete that removes the personal data**:
- Keep the row as a nameless tombstone: status `DELETED` with a `deleted_at` time, and clear every personal field (name, email, avatar). Content keeps a valid author id, which no longer identifies anyone.
- Delete the sign-in identities, so the account can't be reached and a later sign-in creates a fresh account.
- End all the person's sessions, and treat a deleted account as "not found" everywhere.
- On a request for erasure (the GDPR's right to erasure), also hard-delete the rows that are only the person's own activity, such as their answers and votes, which nothing references.

All of it runs in one transaction, and the database can enforce the state with a check such as `CHECK ((status = 'DELETED') = (deleted_at IS NOT NULL))`.$hs$, 6, 'SUBMITTED', $hs$GDPR, Art. 17 Right to erasure https://gdpr-info.eu/art-17-gdpr/$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: peer-to-peer money transfers in a mobile banking app$hs$, $hs$Design the backend for sending money instantly to another user of the same bank app, on mobile networks that drop connections. Money must never be created, lost or moved twice, and the sender must see a clear outcome. Describe the API, the data model and how a transfer is processed.$hs$, $hs$**API:** `POST /transfers` with the recipient, amount, currency and an **idempotency key** generated by the app for each transfer the user confirms. The server stores the key with the result; a retry after a dropped connection returns the same result instead of sending the money again. Answer `201` with the transfer's status, and let the app poll `GET /transfers/{id}` or receive a push when it settles.

**Data model:** a **double-entry ledger**. Each transfer writes balanced entries in one database transaction: a debit on the sender's account and a credit on the recipient's, summing to zero. Balances are derived from the ledger (or kept alongside it and updated in the same transaction), with the minor unit stored as an integer amount plus a currency.

**Processing a transfer, in one database transaction:**
1. Insert the transfer with its idempotency key under a unique constraint, so a concurrent duplicate fails.
2. Lock both accounts in a fixed order (by id), to avoid deadlocks.
3. Check the sender's available balance and limits, and refuse with a clear reason if they fail.
4. Write the ledger entries and update the balances; mark the transfer completed.
5. Write an outbox event in the same transaction, published afterwards to notify both users and feed fraud monitoring and statements.

**Across services or shards** (accounts held in different places), a single database transaction isn't possible: use a saga with a reserved amount, compensations, and reconciliation jobs that compare the ledgers.$hs$, 9, 'SUBMITTED', $hs$Stripe API docs, Idempotent requests https://docs.stripe.com/api/idempotent_requests$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'distributed-systems', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: a fraud check inside a payment's latency budget$hs$, $hs$Every card payment must be authorised within 300 ms end to end, and a fraud-scoring service must check it first. The fraud service usually answers in 40 ms but sometimes takes seconds or fails. How do you call it so that payments stay within budget, and what happens when it doesn't answer?$hs$, $hs$The fraud check is a synchronous dependency on the critical path, so it needs a strict **time budget** and an explicit **decision for when it fails**.

- **Timeout:** give the call a fixed share of the budget, e.g. 80 ms, with no retries that could exceed it.
- **Circuit breaker:** after repeated failures or timeouts, stop calling the service for a while and use the fallback immediately, so a struggling service isn't overloaded further and payments don't each wait out the timeout.
- **Fallback, decided by the business per risk level:** *fail open* (approve) for low amounts and trusted customers, *fail closed* (decline, or ask for extra authentication) for high-risk payments. Approved-without-score payments are flagged for review afterwards.
- **Isolation:** a separate connection pool (bulkhead) for the fraud call, so its slowness can't exhaust the threads that serve other work.
- **Two stages:** a fast synchronous score on precomputed features (e.g. a cache of recent customer behaviour) within the budget, and slower, deeper analysis asynchronously after authorisation, from the payment events.$hs$, 8, 'SUBMITTED', $hs$Azure Architecture Center, Circuit Breaker pattern (CC BY 4.0) https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'microservices']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: a currency-exchange quote that holds its rate$hs$, $hs$Users exchange money between currencies in the app: they see a rate, and if they confirm within 30 seconds, they must get exactly that rate, even though market rates change every second. How do you design this?$hs$, $hs$Use a **quote**: a server-side record of an offered rate with an expiry.

1. `POST /quotes` with the currency pair and amount. The server takes the current market rate, adds its margin, and stores a quote: id, user, pair, amount, rate, fee and `expires_at` (now + 30 s). It returns the quote, and the app shows a countdown.
2. `POST /exchanges` with the quote id and an idempotency key. In one transaction, the server locks the quote, checks that it belongs to the user, is unused and hasn't expired, marks it used, and writes the ledger entries at the quoted rate. An expired or used quote is refused, and the app requests a new one.

Why a stored quote: the rate the user saw is exactly what's applied, and the client can't tamper with it, since it sends only an id. The bank carries the market risk for those 30 seconds: it limits it with short expiries, limits per quote, and by hedging its overall currency position. Quotes and exchanges are kept for audit.$hs$, 7, 'SUBMITTED', $hs$Wise API docs, Quote https://docs.wise.com/api-docs/api-reference/quote$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'databases']) AS tag;
WITH q AS (
    INSERT INTO question (title, requirement, sample_solution, difficulty_score, question_status, source, created_by, updated_by)
    SELECT $hs$Design: push notifications to millions of devices$hs$, $hs$The bank app sends a push notification for every card payment, for about ten million users. Payments come from the payment service as events. Design the notification pipeline so that a slow push provider never slows down payments, and users neither miss notifications nor get duplicates.$hs$, $hs$Decouple it with messaging:
1. The payment service publishes a `PaymentCompleted` event (through a transactional outbox, so no event is lost) to a topic, e.g. in Kafka, partitioned by user id so each user's notifications stay in order.
2. A **notification service** consumes the events, applies each user's preferences and language, renders the message, and looks up the user's device tokens.
3. It sends through the platform push services (Apple's APNs, Google's Firebase Cloud Messaging), with timeouts, retries with backoff for temporary errors, and a dead-letter topic for messages that keep failing.

Properties:
- **Payments are never slowed:** they only write an event; the push provider's speed affects only the consumer's lag.
- **At-least-once delivery with deduplication:** the consumer can see an event twice, so it records a notification id (e.g. the payment id) and skips repeats.
- **Scale:** more partitions and consumers, and rate limits towards the providers; invalid device tokens reported by the providers are removed.
- Push is best-effort, so important information also appears in the app's activity feed.$hs$, 8, 'SUBMITTED', $hs$Firebase docs, Firebase Cloud Messaging https://firebase.google.com/docs/cloud-messaging$hs$, house.id, house.id
    FROM house
    RETURNING id
)
INSERT INTO question_concept (question_id, concept_tag, link_status)
SELECT q.id, tag, 'ACCEPTED' FROM q, unnest(ARRAY['system-design', 'messaging']) AS tag;
