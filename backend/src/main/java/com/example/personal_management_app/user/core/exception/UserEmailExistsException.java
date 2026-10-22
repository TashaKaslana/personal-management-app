package com.example.personal_management_app.user.core.exception;

import com.example.personal_management_app.user.core.enums.UserError;

/**
 * Exception thrown when attempting to create a user with an email that already exists
 */
public class UserEmailExistsException extends UserDomainException {
    public UserEmailExistsException(String email) {
        super(UserError.USER_EMAIL_EXISTS, email);
    }
}
