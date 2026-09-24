package org.manu.hs.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

/**
 * Who may call what, and how signing in works.
 * <ul>
 *     <li>Sign-in: GET /oauth2/authorization/github starts it; GitHub calls back
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
            @Value("${app.ui-url}") String uiUrl) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/csrf", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/content/**", "/users/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo.userService(signInService))
                        .defaultSuccessUrl(uiUrl, true)
                        .failureUrl(uiUrl + "?signInError"))
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                writeMessage(response, HttpStatus.UNAUTHORIZED, "Sign in required"))
                        .accessDeniedHandler((request, response, exception) ->
                                writeMessage(response, HttpStatus.FORBIDDEN, "Access denied")));

        return http.build();
    }

    /** Only the UI may call the API from a browser, with its session cookie. */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.ui-url}") String uiUrl) {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(uiUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /** Same {"message": "..."} body as GlobalExceptionHandler, for errors raised before a controller runs. */
    private static void writeMessage(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"%s\"}".formatted(message));
    }
}
