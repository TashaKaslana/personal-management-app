package com.example.personal_management_app.user.permission.exception;

import com.example.personal_management_app.user.permission.enums.PermissionError;

/**
 * Exception thrown when a permission already exists
 */
public class PermissionAlreadyExistsException extends PermissionDomainException {
    public PermissionAlreadyExistsException(String feature, String action) {
        super(PermissionError.PERMISSION_ALREADY_EXISTS, "feature: " + feature + ", action: " + action);
    }
}
