package org.manu.hs.domain.user;

import java.time.Instant;
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
    /**
     * How many active admins there are, locking their rows until the transaction ends: two
     * transactions that each demote a different admin then can't both see "2" and leave none.
     */
    int countActiveAdminsLocking();
    /**
     * The admin overview: users (not deleted, not the house account named houseAccountName), how many
     * joined, answered and how many answers since the given time, open reports, and submitted
     * questions waiting for a concept owner's review.
     */
    Overview countOverview(Instant since, String houseAccountName);

    UserIdentity insertIdentity(UserIdentity identity);
    Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId);
    /** Saves the provider's current username and the last login time. */
    UserIdentity updateIdentity(UserIdentity identity);
    void deleteIdentitiesOf(Long userId);
    /** Whether anyone can sign in as this user (at least one provider account is linked). */
    boolean hasIdentity(Long userId);
    /** Erases the user's sets, votes and feedback; what they wrote (exercises, muscles) stays. */
    void eraseActivityOf(Long userId);

    /** Whether sign-in is invite-only: there's at least one invite. */
    boolean hasInvites();
    /** Whether the username is invited, in any case. */
    boolean isInvited(AuthProvider provider, String providerLogin);
    /** Every invite, by username in any case. */
    List<Invite> findInvites();
    Invite insertInvite(Invite invite);
    /** False when there was no such invite. */
    boolean deleteInvite(Long id);
}
