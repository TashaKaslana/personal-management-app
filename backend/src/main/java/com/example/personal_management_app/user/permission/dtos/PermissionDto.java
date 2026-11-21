package com.example.personal_management_app.user.permission.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PermissionDto {
    private UUID id;
    private String feature;
    private String action;
    private String description;
}
