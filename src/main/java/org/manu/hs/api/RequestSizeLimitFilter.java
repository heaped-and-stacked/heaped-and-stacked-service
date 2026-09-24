package org.manu.hs.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Refuses request bodies over MAX_BODY_BYTES, before they're read: the field length limits would
 * refuse a huge text too, but only after the whole body was in memory.
 * <p>
 * A body that announces its size (Content-Length, as browsers send) is refused at once with 413.
 * One that doesn't (chunked) is counted while it's read; past the limit, reading fails with
 * RequestBodyTooLargeException, which GlobalExceptionHandler turns into the same 413.
 * <p>
 * Runs right after RequestIdFilter and CORS (SecurityConfig.corsFirst), so the 413 carries a
 * request id and the browser lets the web app read it.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RequestSizeLimitFilter extends OncePerRequestFilter {

    /**
     * 256 KB. The largest valid request, a new question (two texts of 10,000 characters plus a
     * title and source), stays under 100 KB even if every character takes the most bytes.
     */
    public static final int MAX_BODY_BYTES = 256 * 1024;

    static final String TOO_LARGE_MESSAGE = "Request body too large";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final long announcedSize = request.getContentLengthLong();
        if (announcedSize > MAX_BODY_BYTES) {
            response.setStatus(HttpStatus.CONTENT_TOO_LARGE.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"" + TOO_LARGE_MESSAGE + "\"}");
            return;
        }
        chain.doFilter(announcedSize < 0 ? new SizeLimitedRequest(request) : request, response);
    }

    /** Reading a body of unknown size went past MAX_BODY_BYTES. */
    public static class RequestBodyTooLargeException extends IOException {
        public RequestBodyTooLargeException() {
            super(TOO_LARGE_MESSAGE);
        }
    }

    /** The request, with a body that counts the bytes read and stops past the limit. */
    private static final class SizeLimitedRequest extends HttpServletRequestWrapper {

        private ServletInputStream body;

        private SizeLimitedRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (body == null) {
                body = new SizeLimitedInputStream(super.getInputStream());
            }
            return body;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            final String encoding = getCharacterEncoding();
            final Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }
    }

    private static final class SizeLimitedInputStream extends ServletInputStream {

        private final ServletInputStream delegate;
        private long bytesRead;

        private SizeLimitedInputStream(ServletInputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            final int next = delegate.read();
            if (next != -1) {
                count(1);
            }
            return next;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            final int read = delegate.read(buffer, offset, length);
            if (read > 0) {
                count(read);
            }
            return read;
        }

        private void count(int bytes) throws RequestBodyTooLargeException {
            bytesRead += bytes;
            if (bytesRead > MAX_BODY_BYTES) {
                throw new RequestBodyTooLargeException();
            }
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(ReadListener listener) {
            delegate.setReadListener(listener);
        }
    }
}
