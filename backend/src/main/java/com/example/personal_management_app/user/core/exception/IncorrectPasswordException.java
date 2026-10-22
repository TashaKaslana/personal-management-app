package com.example.personal_management_app.user.core.exception;

import com.example.personal_management_app.user.core.enums.UserError;

/**
 * Exception thrown when the provided current password does not match
 */
public class IncorrectPasswordException extends UserDomainException {
    public IncorrectPasswordException() {
        super(UserError.USER_PASSWORD_INCORRECT);
    }
}
