package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.Overview;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository implements UserRepository {

    private static final String USER_COLUMNS = """
            u.id, u.display_name, u.avatar_url, u.user_role, u.user_status,
            u.created_at, u.updated_at, u.last_login_at, u.deleted_at
            """;

    private static final String FIND_BY_ID =
            "SELECT " + USER_COLUMNS + " FROM app_user u WHERE u.id = :id";

    private static final String FIND_BY_IDENTITY =
            "SELECT " + USER_COLUMNS + """
                     FROM app_user u
                     JOIN user_identity i ON i.user_id = u.id
                    WHERE i.provider = :provider
                      AND i.provider_user_id = :provider_user_id
                    """;

    // A null filter means "any". The CASTs tell Postgres the parameter type, which it
    // can't work out on its own when the value is null.
    private static final String FIND_ALL =
            "SELECT " + USER_COLUMNS + """
                     FROM app_user u
                    WHERE (CAST(:role AS VARCHAR) IS NULL OR u.user_role = CAST(:role AS VARCHAR))
                      AND (CAST(:status AS VARCHAR) IS NULL OR u.user_status = CAST(:status AS VARCHAR))
                    ORDER BY u.id
                    """;

    // FOR UPDATE locks the rows counted (Postgres refuses it next to an aggregate, so the ids are
    // counted in Java). A transaction waiting on the lock re-checks the WHERE after the other one
    // commits, so it sees an admin demoted meanwhile as no longer counting.
    private static final String LOCK_ACTIVE_ADMINS = """
            SELECT id
            FROM app_user
            WHERE user_role = 'ADMIN'
              AND user_status = 'ACTIVE'
            FOR UPDATE
            """;

    // The admin overview in one round trip. Users: accounts that aren't deleted, without the house
    // account, which owns the content and has no sign-in (a member who renamed themselves like it
    // still has their GitHub login). :since is the start of the last 7 days. Questions awaiting
    // review: the same filter as QuestionJdbcRepository.SELECT_LINKS_AWAITING_REVIEW, each once.
    private static final String COUNT_OVERVIEW = """
            SELECT COUNT(*) AS users,
                   COUNT(*) FILTER (WHERE u.created_at >= :since) AS new_users_this_week,
                   (SELECT COUNT(DISTINCT a.user_id) FROM answer a WHERE a.answered_at >= :since)
                       AS active_users_this_week,
                   (SELECT COUNT(*) FROM answer a WHERE a.answered_at >= :since) AS answers_this_week,
                   (SELECT COUNT(*) FROM question_report r WHERE r.report_status = 'OPEN') AS open_reports,
                   (SELECT COUNT(DISTINCT qc.question_id)
                      FROM question_concept qc
                      JOIN question q ON q.id = qc.question_id
                     WHERE qc.link_status = 'PENDING'
                       AND q.question_status = 'SUBMITTED') AS questions_awaiting_review
            FROM app_user u
            WHERE u.user_status <> 'DELETED'
              AND (u.display_name IS DISTINCT FROM :house_account_name
                   OR EXISTS (SELECT 1 FROM user_identity i WHERE i.user_id = u.id))
            """;

    private static final String INSERT = """
            INSERT INTO app_user (display_name, avatar_url, user_role, user_status, last_login_at)
            VALUES (:display_name, :avatar_url, :user_role, :user_status, :last_login_at)
            RETURNING id
            """;

    // Optimistic locking: the row is only written if nobody changed it since it was read (the
    // trigger moves updated_at on every change), so a stale copy can't undo a newer change, such
    // as a sign-in writing ACTIVE back over a block that landed in between.
    private static final String UPDATE = """
            UPDATE app_user
            SET display_name = :display_name,
                avatar_url = :avatar_url,
                user_role = :user_role,
                user_status = :user_status,
                last_login_at = :last_login_at,
                deleted_at = :deleted_at
            WHERE id = :id
              AND updated_at = :read_updated_at
            """;

    private static final String IDENTITY_COLUMNS = """
            id, user_id, provider, provider_user_id, provider_login, created_at, last_login_at
            """;

    private static final String FIND_IDENTITY =
            "SELECT " + IDENTITY_COLUMNS + """
                     FROM user_identity
                    WHERE provider = :provider
                      AND provider_user_id = :provider_user_id
                    """;

    private static final String FIND_IDENTITY_BY_ID =
            "SELECT " + IDENTITY_COLUMNS + " FROM user_identity WHERE id = :id";

    private static final String INSERT_IDENTITY = """
            INSERT INTO user_identity (user_id, provider, provider_user_id, provider_login, last_login_at)
            VALUES (:user_id, :provider, :provider_user_id, :provider_login, :last_login_at)
            RETURNING id
            """;

    private static final String UPDATE_IDENTITY = """
            UPDATE user_identity
            SET provider_login = :provider_login,
                last_login_at = :last_login_at
            WHERE id = :id
            """;

    private static final String DELETE_IDENTITIES_OF = """
            DELETE FROM user_identity
            WHERE user_id = :user_id
            """;

    private static final String HAS_IDENTITY = """
            SELECT EXISTS (SELECT 1 FROM user_identity WHERE user_id = :user_id)
            """;

    // Feedback and votes first: nothing refers to them. Feedback this user decided as a coach
    // stays (resolved_by), as does everything they wrote.
    private static final String ERASE_ACTIVITY_OF = """
            DELETE FROM question_report WHERE reported_by = :user_id;
            DELETE FROM question_vote WHERE user_id = :user_id;
            DELETE FROM answer WHERE user_id = :user_id;
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // ---------------------------------------------------------------------
    // Users
    // ---------------------------------------------------------------------

    @Override
    public User insert(User user) {
        Long id = jdbcTemplate.queryForObject(
                INSERT,
                userParameters(user),
                Long.class);

        return findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User was inserted but could not be retrieved"));
    }

    @Override
    public User update(User user) {
        final int updated = jdbcTemplate.update(
                UPDATE,
                userParameters(user)
                        .addValue("id", user.getId())
                        .addValue("deleted_at", toTimestamp(user.getDeletedAt()))
                        .addValue("read_updated_at", toTimestamp(user.getUpdatedAt())));
        if (updated == 0) {
            throw new OptimisticLockingFailureException(
                    "User '%d' was changed by someone else meanwhile".formatted(user.getId()));
        }

        return findById(user.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User was updated but could not be retrieved"));
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcTemplate.query(
                        FIND_BY_ID,
                        Map.of("id", id),
                        this::mapUser)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByIdentity(AuthProvider provider, String providerUserId) {
        return jdbcTemplate.query(
                        FIND_BY_IDENTITY,
                        Map.of("provider", provider.name(), "provider_user_id", providerUserId),
                        this::mapUser)
                .stream()
                .findFirst();
    }

    @Override
    public List<User> findAll(UserRole role, UserStatus status) {
        return jdbcTemplate.query(
                FIND_ALL,
                new MapSqlParameterSource()
                        .addValue("role", role == null ? null : role.name())
                        .addValue("status", status == null ? null : status.name()),
                this::mapUser);
    }

    @Override
    public int countActiveAdminsLocking() {
        return jdbcTemplate.queryForList(LOCK_ACTIVE_ADMINS, Map.of(), Long.class).size();
    }

    @Override
    public Overview countOverview(Instant since, String houseAccountName) {
        return jdbcTemplate.queryForObject(
                COUNT_OVERVIEW,
                new MapSqlParameterSource()
                        .addValue("since", toTimestamp(since))
                        .addValue("house_account_name", houseAccountName),
                (resultSet, rowNumber) -> Overview.builder()
                        .users(resultSet.getInt("users"))
                        .newUsersThisWeek(resultSet.getInt("new_users_this_week"))
                        .activeUsersThisWeek(resultSet.getInt("active_users_this_week"))
                        .answersThisWeek(resultSet.getInt("answers_this_week"))
                        .openReports(resultSet.getInt("open_reports"))
                        .questionsAwaitingReview(resultSet.getInt("questions_awaiting_review"))
                        .build());
    }

    // ---------------------------------------------------------------------
    // Identities
    // ---------------------------------------------------------------------

    @Override
    public UserIdentity insertIdentity(UserIdentity identity) {
        Long id = jdbcTemplate.queryForObject(
                INSERT_IDENTITY,
                new MapSqlParameterSource()
                        .addValue("user_id", identity.getUserId())
                        .addValue("provider", identity.getProvider().name())
                        .addValue("provider_user_id", identity.getProviderUserId())
                        .addValue("provider_login", identity.getProviderLogin())
                        .addValue("last_login_at", toTimestamp(identity.getLastLoginAt())),
                Long.class);

        return findIdentityById(id);
    }

    @Override
    public Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId) {
        return jdbcTemplate.query(
                        FIND_IDENTITY,
                        Map.of("provider", provider.name(), "provider_user_id", providerUserId),
                        this::mapIdentity)
                .stream()
                .findFirst();
    }

    @Override
    public UserIdentity updateIdentity(UserIdentity identity) {
        jdbcTemplate.update(
                UPDATE_IDENTITY,
                new MapSqlParameterSource()
                        .addValue("id", identity.getId())
                        .addValue("provider_login", identity.getProviderLogin())
                        .addValue("last_login_at", toTimestamp(identity.getLastLoginAt())));

        return findIdentityById(identity.getId());
    }

    @Override
    public void deleteIdentitiesOf(Long userId) {
        jdbcTemplate.update(
                DELETE_IDENTITIES_OF,
                Map.of("user_id", userId));
    }

    @Override
    public boolean hasIdentity(Long userId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                HAS_IDENTITY,
                Map.of("user_id", userId),
                Boolean.class));
    }

    @Override
    public void eraseActivityOf(Long userId) {
        jdbcTemplate.update(
                ERASE_ACTIVITY_OF,
                Map.of("user_id", userId));
    }

    // ---------------------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------------------

    private UserIdentity findIdentityById(Long id) {
        return jdbcTemplate.query(
                        FIND_IDENTITY_BY_ID,
                        Map.of("id", id),
                        this::mapIdentity)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User identity was saved but could not be retrieved"));
    }

    private MapSqlParameterSource userParameters(User user) {
        return new MapSqlParameterSource()
                .addValue("display_name", user.getDisplayName())
                .addValue("avatar_url", user.getAvatarUrl())
                .addValue("user_role", user.getRole().name())
                .addValue("user_status", user.getStatus().name())
                .addValue("last_login_at", toTimestamp(user.getLastLoginAt()));
    }

    private User mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .displayName(resultSet.getString("display_name"))
                .avatarUrl(resultSet.getString("avatar_url"))
                .role(UserRole.valueOf(resultSet.getString("user_role")))
                .status(UserStatus.valueOf(resultSet.getString("user_status")))
                .createdAt(toInstant(resultSet.getTimestamp("created_at")))
                .updatedAt(toInstant(resultSet.getTimestamp("updated_at")))
                .lastLoginAt(toInstant(resultSet.getTimestamp("last_login_at")))
                .deletedAt(toInstant(resultSet.getTimestamp("deleted_at")))
                .build();
    }

    private UserIdentity mapIdentity(ResultSet resultSet, int rowNumber) throws SQLException {
        return UserIdentity.builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .provider(AuthProvider.valueOf(resultSet.getString("provider")))
                .providerUserId(resultSet.getString("provider_user_id"))
                .providerLogin(resultSet.getString("provider_login"))
                .createdAt(toInstant(resultSet.getTimestamp("created_at")))
                .lastLoginAt(toInstant(resultSet.getTimestamp("last_login_at")))
                .build();
    }

    // last_login_at and deleted_at can be null.
    private static Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
