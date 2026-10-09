package org.manu.hs.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Limits how many requests one client (one address) may make per minute, so a script can't flood
 * sign-in, writes, or the exercises visitors may read. Over the limit: 429, with Retry-After saying
 * how many seconds are left in the minute.
 * <p>
 * Runs before everything but the request id, CORS and size checks, Spring Security included, so refused
 * requests cost next to nothing and sign-in starts are counted too. Counts are kept in memory per
 * instance, in fixed one-minute windows; a platform's own limits, if any, come on top.
 * <p>
 * Behind a reverse proxy the client's address is only right with forwarded headers on
 * (FORWARD_HEADERS_STRATEGY=native); otherwise every client would share the proxy's address.
 * Switched off with app.rate-limit.enabled=false (the integration tests do).
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
public class RateLimitFilter extends OncePerRequestFilter {

    /** What a request counts against, and how many of those one client may make per minute. */
    enum Kind {
        SIGN_IN(10),
        WRITE(60),
        READ(300);

        final int perMinute;

        Kind(int perMinute) {
            this.perMinute = perMinute;
        }
    }

    private static final long MINUTE_MILLIS = 60_000;
    static final String TOO_MANY_MESSAGE = "Too many requests: slow down a little and try again";

    private final boolean enabled;
    private final Clock clock;
    /** "address kind" -> the count in its current minute. */
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong sweptMinute = new AtomicLong();

    @Autowired
    public RateLimitFilter(@Value("${app.rate-limit.enabled:true}") boolean enabled) {
        this(enabled, Clock.systemUTC());
    }

    RateLimitFilter(boolean enabled, Clock clock) {
        this.enabled = enabled;
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final Kind kind = kindOf(request);
        if (!enabled || kind == null) {
            chain.doFilter(request, response);
            return;
        }

        final long nowMillis = clock.millis();
        final long minute = nowMillis / MINUTE_MILLIS;
        forgetPastMinutes(minute);

        final String key = request.getRemoteAddr() + " " + kind;
        final Window window = windows.compute(key, (k, current) ->
                current == null || current.minute != minute ? new Window(minute, 1) : current.plusOne());

        if (window.count > kind.perMinute) {
            if (window.count == kind.perMinute + 1) {
                log.warn("Rate limit reached: {} requests from {} this minute", kind, request.getRemoteAddr());
            }
            final long secondsLeft = ((minute + 1) * MINUTE_MILLIS - nowMillis + 999) / 1000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(secondsLeft));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"" + TOO_MANY_MESSAGE + "\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * null: not limited (health checks and probes). Only sign-in starts count as SIGN_IN: GitHub's callback
     * (/login/oauth2/code/...) is a READ, as refusing it would strand someone who already approved,
     * and a callback without a sign-in started from the same session is rejected anyway.
     */
    private static Kind kindOf(HttpServletRequest request) {
        final String path = request.getRequestURI();
        if (path.equals("/actuator/health") || path.startsWith("/actuator/health/")) {
            return null;
        }
        if (path.startsWith("/oauth2/authorization/")) {
            return Kind.SIGN_IN;
        }
        return switch (request.getMethod()) {
            case "GET", "HEAD", "OPTIONS" -> Kind.READ;
            default -> Kind.WRITE;
        };
    }

    /** Once a minute, drops the counts of earlier minutes, so memory stays bounded. */
    private void forgetPastMinutes(long minute) {
        final long swept = sweptMinute.get();
        if (swept < minute && sweptMinute.compareAndSet(swept, minute)) {
            windows.values().removeIf(window -> window.minute < minute);
        }
    }

    /** One client's count of one kind of request, in one minute. Immutable: compute() swaps it. */
    private static final class Window {

        private final long minute;
        private final int count;

        private Window(long minute, int count) {
            this.minute = minute;
            this.count = count;
        }

        private Window plusOne() {
            return new Window(minute, count + 1);
        }
    }
}
