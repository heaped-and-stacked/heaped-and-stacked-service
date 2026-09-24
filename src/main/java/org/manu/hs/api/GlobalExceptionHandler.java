package org.manu.hs.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.manu.hs.application.common.exception.base.ConflictException;
import org.manu.hs.application.common.exception.base.ForbiddenException;
import org.manu.hs.application.common.exception.base.NotFoundException;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.slf4j.MDC;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Turns exceptions into {"message": ...} responses: the application's own (404, 403, 409, 400)
 * with their message, Spring's request errors with their status, and anything unexpected into a
 * logged 500 that carries the request id.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NotFoundException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbidden(ForbiddenException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(ConflictException exception) {
        return Map.of("message", exception.getMessage());
    }

    /**
     * A write that lost a race: a check passed, then a unique index refused the duplicate (two
     * identical requests at once), or the row changed between reading and writing it (optimistic
     * locking). The database kept the data right; the caller only has to reload and try again.
     */
    @ExceptionHandler({DuplicateKeyException.class, OptimisticLockingFailureException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleLostRace(RuntimeException exception) {
        log.info("Lost a race: {}", exception.getMessage());
        return Map.of("message", "Someone else changed this just now: reload and try again");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(ValidationException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return Map.of("message", "'%s' is not a valid value for %s".formatted(exception.getValue(), exception.getName()));
    }

    /** 400, or 413 when the body was cut off for being too large (RequestSizeLimitFilter). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody(HttpMessageNotReadableException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof RequestSizeLimitFilter.RequestBodyTooLargeException) {
                return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                        .body(Map.of("message", RequestSizeLimitFilter.TOO_LARGE_MESSAGE));
            }
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Request body is malformed or contains an invalid value"));
    }

    /**
     * Everything nobody planned for. Spring's own request errors (wrong method, unsupported media
     * type, ...) keep their status. Anything else is a bug: logged once with its stack trace, while
     * the request id is still known (RequestIdFilter), and answered with a plain 500 that carries
     * only that id, so a user can quote it and it finds the log line.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception, HttpServletRequest request) {
        if (exception instanceof ErrorResponse errorResponse) {
            final HttpStatusCode status = errorResponse.getStatusCode();
            // Their headers matter too, e.g. Allow on a 405.
            return ResponseEntity.status(status)
                    .headers(errorResponse.getHeaders())
                    .body(Map.of("message", reasonOf(status)));
        }

        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), exception);
        final String requestId = MDC.get(RequestIdFilter.MDC_KEY);
        return ResponseEntity.internalServerError().body(Map.of(
                "message", "Something went wrong",
                "requestId", requestId == null ? "" : requestId));
    }

    private static String reasonOf(HttpStatusCode status) {
        final HttpStatus known = HttpStatus.resolve(status.value());
        return known == null ? "Request failed" : known.getReasonPhrase();
    }
}