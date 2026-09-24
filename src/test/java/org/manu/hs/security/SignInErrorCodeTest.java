package org.manu.hs.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Why a sign-in failed, as the web app receives it: /?signInError=<code> (SecurityConfig).
 */
class SignInErrorCodeTest {

    @Test
    @DisplayName("""
            Given a sign-in refused by the sign-in hook, as not invited or blocked
            When the browser is sent back to the web app
            Then the reason travels as it was raised
            """)
    void signInErrorCodeIsTheHooksReason() {
        assertEquals("not_invited", SecurityConfig.signInErrorCode(
                new OAuth2AuthenticationException(new OAuth2Error("not_invited"))));
        assertEquals("account_not_active", SecurityConfig.signInErrorCode(
                new OAuth2AuthenticationException(new OAuth2Error("account_not_active"))));
    }

    @Test
    @DisplayName("""
            Given a failure that isn't the hook's, or a code that isn't a plain word (from GitHub)
            When the browser is sent back to the web app
            Then the reason is the general sign_in_failed: nothing else goes into the address
            """)
    void signInErrorCodeIsGeneralForAnythingElse() {
        assertEquals("sign_in_failed", SecurityConfig.signInErrorCode(new BadCredentialsException("nope")));
        assertEquals("sign_in_failed", SecurityConfig.signInErrorCode(
                new OAuth2AuthenticationException(new OAuth2Error("access_denied&x=<script>"))));
    }
}
