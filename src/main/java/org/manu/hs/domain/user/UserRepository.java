package org.manu.hs.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User insert(User user);
    /** Saves display name, avatar, role, status, last login and deletion time. */
    User update(User user);
    Optional<User> findById(Long id);
    /** The user who signs in with the given provider account, if any. */
    Optional<User> findByIdentity(AuthProvider provider, String providerUserId);
    /** All users, optionally filtered; a null filter means "any". Ordered by id. */
    List<User> findAll(UserRole role, UserStatus status);
    int countActiveAdmins();

    UserIdentity insertIdentity(UserIdentity identity);
    Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId);
    /** Saves the provider's current username and the last login time. */
    UserIdentity updateIdentity(UserIdentity identity);
    void deleteIdentitiesOf(Long userId);
}
