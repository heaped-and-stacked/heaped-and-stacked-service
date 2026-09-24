package org.manu.hs.security;

import jakarta.servlet.http.HttpServletResponse;
import org.manu.hs.api.RequestIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Who may call what, and how signing in works.
 * <ul>
 *     <li>Sign-in: GET /oauth2/authorization/github starts it (GitHub shows its account
 *     picker every time); GitHub calls back
 *     /login/oauth2/code/github; the browser is then sent back to the UI.</li>
 *     <li>The session lives in a cookie; CSRF protection is on, the UI gets its token
 *     from GET /csrf.</li>
 *     <li>API clients get a 401/403 JSON body, never a redirect to a login page.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2SignInService signInService,
            ClientRegistrationRepository clientRegistrations,
            @Value("${app.ui-url}") String uiUrl) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/error").permitAll()
                        // Only signed-in people write, so only they need a token. Handing one out
                        // to anyone would store a 14-day session for every visitor and bot.
                        .requestMatchers(HttpMethod.GET, "/csrf").authenticated()
                        // Status only (UP/DOWN), for healthchecks, platform probes and uptime monitors.
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness").permitAll()
                        // API docs; only served where springdoc is switched on (application.properties).
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Visitors may try a workout: pick muscles, get exercises. Saving a set (and
                        // seeing the model answer) still requires signing in.
                        .requestMatchers(HttpMethod.GET, "/content/concepts", "/learning/questions/next").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/content/**", "/learning/**", "/users/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2Login(login -> login
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(withAccountPicker(clientRegistrations)))
                        .userInfoEndpoint(userInfo -> userInfo.userService(signInService))
                        .defaultSuccessUrl(uiUrl, true)
                        // Back to the web app with why: ?signInError=account_not_active, not_invited, ...
                        .failureHandler((request, response, exception) -> response.sendRedirect(
                                uiUrl + "?signInError=" + signInErrorCode(exception))))
                // No "request cache": a refused anonymous request would otherwise be saved in a new
                // session (to return to it after sign-in), so every visitor's "am I signed in?" and
                // every bot hit would add a session row. Sign-in always returns to the UI instead.
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                writeMessage(response, HttpStatus.UNAUTHORIZED, "Sign in required"))
                        .accessDeniedHandler((request, response, exception) ->
                                writeMessage(response, HttpStatus.FORBIDDEN, "Access denied")));

        return http.build();
    }

    /**
     * Asks GitHub to show its account picker on every sign-in (prompt=select_account): otherwise
     * GitHub silently uses whichever account is signed in to github.com, so someone with two GitHub
     * accounts couldn't choose, and signing out here and in again changes nothing.
     */
    private static OAuth2AuthorizationRequestResolver withAccountPicker(ClientRegistrationRepository clientRegistrations) {
        final DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrations, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        resolver.setAuthorizationRequestCustomizer(request ->
                request.additionalParameters(parameters -> parameters.put("prompt", "select_account")));
        return resolver;
    }

    /** Only the UI may call the API from a browser, with its session cookie. */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.ui-url}") String uiUrl) {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(uiUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        // Lets the web app read the request id, to show it in error messages.
        configuration.setExposedHeaders(List.of(RequestIdFilter.HEADER));
        // Browsers may reuse a preflight answer for an hour instead of asking before every write.
        configuration.setMaxAge(Duration.ofHours(1));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * CORS right after the request id, ahead of the size and rate limits: their 413 and 429 are
     * written before Spring Security runs, and without CORS headers the browser would hide them
     * from the web app, which would then report the API as unreachable. Preflights are answered
     * here too, so they don't count against the read limit.
     */
    @Bean
    FilterRegistrationBean<CorsFilter> corsFirst(CorsConfigurationSource corsConfigurationSource) {
        final FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    /**
     * Why a sign-in failed, for the web app: the OAuth2 error code the sign-in hook raised
     * (account_not_active, not_invited, sign_in_failed, ...), or sign_in_failed for anything else.
     */
    static String signInErrorCode(AuthenticationException exception) {
        final String code = exception instanceof OAuth2AuthenticationException oauth2
                ? oauth2.getError().getErrorCode()
                : null;
        // Only plain codes go into the address.
        return code != null && code.matches("[a-z_]{1,40}") ? code : "sign_in_failed";
    }

    /** Same {"message": "..."} body as GlobalExceptionHandler, for errors raised before a controller runs. */
    private static void writeMessage(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"%s\"}".formatted(message));
    }
}
