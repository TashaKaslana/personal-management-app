package com.example.personal_management_app.user.core.dtos;

import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.role.enums.UserRoleEnum;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UpdateUserRequest(String name, String username, String email,
                                UserRoleEnum roleName) implements Serializable {
}
