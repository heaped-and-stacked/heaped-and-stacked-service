package org.manu.hs.security;

import lombok.extern.slf4j.Slf4j;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The hook Spring Security calls after a provider (GitHub) confirms who signed in:
 * reads the provider's profile, then finds or registers our user. The returned
 * SignedInUser is what the session keeps.
 * <p>
 * Every failure leaves as an OAuth2AuthenticationException: it's the only kind Spring Security
 * sends to the failure URL (the web app, with ?signInError); anything else would end on a raw
 * error page on the API.
 */
@Slf4j
@Service
public class OAuth2SignInService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    /** Asks the provider who signed in (GitHub's user-info endpoint). */
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> providerUsers;
    private final UserApplication userApplication;
    private final List<ProviderProfileReader> profileReaders;

    @Autowired
    public OAuth2SignInService(UserApplication userApplication, List<ProviderProfileReader> profileReaders) {
        this(new DefaultOAuth2UserService(), userApplication, profileReaders);
    }

    OAuth2SignInService(
            OAuth2UserService<OAuth2UserRequest, OAuth2User> providerUsers,
            UserApplication userApplication,
            List<? extends ProviderProfileReader> profileReaders) {
        this.providerUsers = providerUsers;
        this.userApplication = userApplication;
        this.profileReaders = List.copyOf(profileReaders);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        final OAuth2User providerUser = providerUsers.loadUser(request);
        final SignInCommand command = readerFor(request.getClientRegistration().getRegistrationId())
                .read(providerUser);

        try {
            final User user = userApplication.signIn(command);
            return new SignedInUser(user.getId(), user.getRole());
        } catch (UserNotActiveException exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("account_not_active"), exception.getMessage());
        } catch (RuntimeException exception) {
            log.error("Sign-in failed after the provider confirmed the user", exception);
            throw new OAuth2AuthenticationException(new OAuth2Error("sign_in_failed"), "Sign-in failed", exception);
        }
    }

    private ProviderProfileReader readerFor(String registrationId) {
        return profileReaders.stream()
                .filter(reader -> reader.provider().name().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error("unsupported_provider"),
                        "No profile reader for login provider '%s'".formatted(registrationId)));
    }
}
