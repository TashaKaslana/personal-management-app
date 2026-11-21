package com.example.personal_management_app.user.permission.exception;

import lombok.Getter;
import com.example.personal_management_app.user.permission.enums.PermissionError;

/**
 * Base exception class for permission subdomain exceptions
 */
@Getter
public class PermissionDomainException extends RuntimeException {
    private final PermissionError error;

    public PermissionDomainException(PermissionError error, String details) {
        super(error.formatMessage(details));
        this.error = error;
    }
}
