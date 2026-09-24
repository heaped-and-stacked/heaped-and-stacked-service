package org.manu.hs.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for RequestSizeLimitFilter's second line of defence: bodies that don't announce their
 * size (chunked uploads) are counted while they're read. Bodies that do announce it are covered
 * end to end by RequestSizeLimitTest.
 */
class RequestSizeLimitFilterTest {

    private final RequestSizeLimitFilter filter = new RequestSizeLimitFilter();

    @Test
    @DisplayName("""
            Given a body of unknown size that is over the limit
            When the API reads it
            Then reading stops at the limit with RequestBodyTooLargeException
            """)
    void unannouncedOversizedBodyStopsAtTheLimit() throws Exception {
        // Given
        HttpServletRequest request = bodyOfUnknownSize(RequestSizeLimitFilter.MAX_BODY_BYTES + 1);

        // When
        FilterChain readEverything = (req, res) -> req.getInputStream().readAllBytes();

        // Then
        assertThrows(RequestSizeLimitFilter.RequestBodyTooLargeException.class,
                () -> filter.doFilter(request, new MockHttpServletResponse(), readEverything));
    }

    @Test
    @DisplayName("""
            Given a body of unknown size that is within the limit
            When the API reads it
            Then all of it is read
            """)
    void unannouncedBodyWithinTheLimitIsReadInFull() throws Exception {
        // Given
        HttpServletRequest request = bodyOfUnknownSize(RequestSizeLimitFilter.MAX_BODY_BYTES);
        int[] read = new int[1];

        // When
        filter.doFilter(request, new MockHttpServletResponse(),
                (req, res) -> read[0] = req.getInputStream().readAllBytes().length);

        // Then
        assertEquals(RequestSizeLimitFilter.MAX_BODY_BYTES, read[0]);
    }

    @Test
    @DisplayName("""
            Given a body that turned out too large while it was being read as JSON
            When the error reaches the API's error handling
            Then the client gets HTTP 413, like for a body that announced its size
            """)
    void tooLargeWhileReadingBecomes413() {
        // Given
        HttpMessageNotReadableException unreadable = new HttpMessageNotReadableException(
                "I/O error while reading input message",
                new RequestSizeLimitFilter.RequestBodyTooLargeException(),
                new MockHttpInputMessage(new byte[0]));

        // When
        ResponseEntity<Map<String, String>> response = new GlobalExceptionHandler().handleUnreadableBody(unreadable);

        // Then
        assertEquals(HttpStatus.CONTENT_TOO_LARGE, response.getStatusCode());
        assertEquals("Request body too large", response.getBody().get("message"));
    }

    /** A POST whose body doesn't say how long it is, as with chunked transfer encoding. */
    private static HttpServletRequest bodyOfUnknownSize(int bytes) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/content/questions") {
            @Override
            public long getContentLengthLong() {
                return -1;
            }

            @Override
            public int getContentLength() {
                return -1;
            }
        };
        request.setContent(new byte[bytes]);
        return request;
    }
}
