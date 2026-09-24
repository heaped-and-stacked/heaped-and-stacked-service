package org.manu.hs.security;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The hook Spring Security calls after a provider (GitHub) confirms who signed in:
 * reads the provider's profile, then finds or registers our user. The returned
 * SignedInUser is what the session keeps.
 */
@Service
@RequiredArgsConstructor
public class OAuth2SignInService extends DefaultOAuth2UserService {

    private final UserApplication userApplication;
    private final List<ProviderProfileReader> profileReaders;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        final OAuth2User providerUser = super.loadUser(request);
        final SignInCommand command = readerFor(request.getClientRegistration().getRegistrationId())
                .read(providerUser);

        try {
            final User user = userApplication.signIn(command);
            return new SignedInUser(user.getId(), user.getRole());
        } catch (UserNotActiveException exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("account_not_active"), exception.getMessage());
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
