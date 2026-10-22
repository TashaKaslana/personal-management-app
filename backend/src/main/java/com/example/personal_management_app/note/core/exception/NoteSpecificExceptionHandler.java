package com.example.personal_management_app.note.core.exception;

import lombok.extern.slf4j.Slf4j;
import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.core.utils.HttpRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception handler for note subdomain specific exceptions
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NoteSpecificExceptionHandler {

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNoteNotFoundException(NoteNotFoundException ex,
                                                                           WebRequest request) {
        log.debug("Note Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(NoteTagAlreadyAssignedException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNoteTagAlreadyAssignedException(NoteTagAlreadyAssignedException ex,
                                                                                      WebRequest request) {
        log.debug("Note Tag Already Assigned: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(NoteTagNotAssignedException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNoteTagNotAssignedException(NoteTagNotAssignedException ex,
                                                                                  WebRequest request) {
        log.debug("Note Tag Not Assigned: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    // Generic note domain exception handler
    @ExceptionHandler(NoteDomainException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNoteDomainException(NoteDomainException ex,
                                                                         WebRequest request) {
        log.debug("Note Domain Exception: {}", ex.getMessage());
        return RestApiResponse.badRequest(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
