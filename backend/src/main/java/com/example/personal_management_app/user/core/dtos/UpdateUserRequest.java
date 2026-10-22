package com.example.personal_management_app.user.core.dtos;

import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.role.enums.UserRoleEnum;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link User}
 */
public record UpdateUserRequest(String username, String email, String passwordHash,
                                UserRoleEnum roleName, OffsetDateTime deletedAt) implements Serializable {
}