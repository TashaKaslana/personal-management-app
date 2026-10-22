package com.example.personal_management_app.user.core.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.example.personal_management_app.user.role.enums.UserRoleEnum;

@Data
public class CreateUserRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank @Size(min = 6, max = 128)
    private String password;

    private String username;

    @NotNull
    private UserRoleEnum roleName = UserRoleEnum.USER;
}
