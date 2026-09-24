package org.manu.hs.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Hands the UI its CSRF token. The UI runs on another origin, so it can't read the
 * API's cookies; it calls this once and sends the token back in the named header on
 * every POST, PUT and DELETE.
 */
@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of(
                "headerName", token.getHeaderName(),
                "token", token.getToken());
    }
}
