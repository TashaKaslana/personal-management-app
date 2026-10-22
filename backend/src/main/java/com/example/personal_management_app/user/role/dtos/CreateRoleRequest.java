package com.example.personal_management_app.user.role.dtos;

import lombok.Data;
import com.example.personal_management_app.user.role.enums.UserRoleEnum;

@Data
public class CreateRoleRequest {
    private UserRoleEnum name;
    private String description;
}
