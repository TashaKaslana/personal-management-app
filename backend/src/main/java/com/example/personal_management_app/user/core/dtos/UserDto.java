package com.example.personal_management_app.user.core.dtos;

import lombok.Data;
import com.example.personal_management_app.user.role.dtos.RoleDto;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String avatarUrl;
    private RoleDto role;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
