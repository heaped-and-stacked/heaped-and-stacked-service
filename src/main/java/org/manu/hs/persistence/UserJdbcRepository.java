package org.manu.hs.persistence;

import lombok.RequiredArgsConstructor;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
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

    private static final String COUNT_ACTIVE_ADMINS = """
            SELECT COUNT(*)
            FROM app_user
            WHERE user_role = 'ADMIN'
              AND user_status = 'ACTIVE'
            """;

    private static final String INSERT = """
            INSERT INTO app_user (display_name, avatar_url, user_role, user_status, last_login_at)
            VALUES (:display_name, :avatar_url, :user_role, :user_status, :last_login_at)
            RETURNING id
            """;

    private static final String UPDATE = """
            UPDATE app_user
            SET display_name = :display_name,
                avatar_url = :avatar_url,
                user_role = :user_role,
                user_status = :user_status,
                last_login_at = :last_login_at,
                deleted_at = :deleted_at
            WHERE id = :id
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
        jdbcTemplate.update(
                UPDATE,
                userParameters(user)
                        .addValue("id", user.getId())
                        .addValue("deleted_at", toTimestamp(user.getDeletedAt())));

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
    public int countActiveAdmins() {
        Integer count = jdbcTemplate.queryForObject(
                COUNT_ACTIVE_ADMINS,
                Map.of(),
                Integer.class);

        return count == null ? 0 : count;
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
