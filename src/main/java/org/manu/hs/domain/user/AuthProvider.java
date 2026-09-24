package org.manu.hs.domain.user;

/**
 * The login providers a user can sign in with. Adding one here also needs the
 * provider added to the chk_user_identity_provider and chk_sign_in_invite_provider constraints
 * in the migration.
 */
public enum AuthProvider {
    GITHUB
}
