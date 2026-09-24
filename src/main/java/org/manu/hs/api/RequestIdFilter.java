package org.manu.hs.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Gives every request an id: sent back in the X-Request-Id header and written on every log line
 * of the request (MDC key "requestId", see logging.pattern.correlation). A user who reports an
 * error can quote it, and the matching log lines are one search away.
 * <p>
 * Runs before everything else, Spring Security included, so refused requests carry an id too.
 * An id that arrives with the request (e.g. from a proxy) is kept when it looks like one;
 * anything else is replaced, so nobody can write junk into the logs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    private static final Pattern TRUSTWORTHY_ID = Pattern.compile("[A-Za-z0-9-]{1,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestId = requestIdOf(request);
        response.setHeader(HEADER, requestId);
        MDC.put(MDC_KEY, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private static String requestIdOf(HttpServletRequest request) {
        final String incoming = request.getHeader(HEADER);
        if (incoming != null && TRUSTWORTHY_ID.matcher(incoming).matches()) {
            return incoming;
        }
        // 12 hex characters: short enough to read out, unique enough for one app's logs.
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
