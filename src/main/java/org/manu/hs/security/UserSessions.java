package org.manu.hs.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

/**
 * Signs a user out everywhere. A session keeps the role the user had when they signed
 * in, so after a role or status change (or account deletion) all their sessions are
 * ended: a blocked user is out immediately, and a new role applies from the next sign-in.
 * <p>
 * Sessions are found through Spring Session's index on the principal name, which is the
 * user id (see SignedInUser#getName).
 */
@Component
@RequiredArgsConstructor
public class UserSessions {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public void endAllSessionsOf(Long userId, HttpServletRequest request) {
        final HttpSession currentSession = request.getSession(false);
        final String currentSessionId = currentSession == null ? null : currentSession.getId();

        sessionRepository.findByPrincipalName(String.valueOf(userId)).keySet().stream()
                .filter(sessionId -> !sessionId.equals(currentSessionId))
                .forEach(sessionRepository::deleteById);

        // The current request's session is ended through the request instead: deleting it
        // from the repository would let it be saved again when the request completes.
        if (currentSession != null && isSignedInAs(userId)) {
            currentSession.invalidate();
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isSignedInAs(Long userId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getPrincipal() instanceof SignedInUser user
                && user.getUserId().equals(userId);
    }
}
