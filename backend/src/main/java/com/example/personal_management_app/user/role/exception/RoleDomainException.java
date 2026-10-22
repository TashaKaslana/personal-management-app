package com.example.personal_management_app.user.role.exception;

import lombok.Getter;
import com.example.personal_management_app.user.role.enums.RoleError;

@Getter
public class RoleDomainException  extends RuntimeException {
    private final RoleError error;

    public RoleDomainException(RoleError error, String details) {
        super(error.formatMessage(details));
        this.error = error;
    }
}