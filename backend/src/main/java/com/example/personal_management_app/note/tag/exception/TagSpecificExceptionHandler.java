package com.example.personal_management_app.note.tag.exception;

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
 * Exception handler for tag subdomain specific exceptions
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TagSpecificExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleTagNotFoundException(TagNotFoundException ex,
                                                                           WebRequest request) {
        log.debug("Tag Not Found: {}", ex.getMessage());
        return RestApiResponse.notFound(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    @ExceptionHandler(TagNameExistsException.class)
    public ResponseEntity<RestApiResponse<Void>> handleTagNameExistsException(TagNameExistsException ex,
                                                                              WebRequest request) {
        log.debug("Tag Name Exists: {}", ex.getMessage());
        return RestApiResponse.conflict(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }

    // Generic tag domain exception handler
    @ExceptionHandler(TagDomainException.class)
    public ResponseEntity<RestApiResponse<Void>> handleTagDomainException(TagDomainException ex,
                                                                         WebRequest request) {
        log.debug("Tag Domain Exception: {}", ex.getMessage());
        return RestApiResponse.badRequest(HttpRequestUtils.getRequestPath(request), ex.getMessage());
    }
}
