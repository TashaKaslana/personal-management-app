package com.example.personal_management_app.user.core.exception;

import com.example.personal_management_app.user.core.enums.UserError;

/**
 * Exception thrown when login credentials are invalid
 */
public class InvalidCredentialsException extends UserDomainException {
    public InvalidCredentialsException() {
        super(UserError.USER_INVALID_CREDENTIALS);
    }
}
