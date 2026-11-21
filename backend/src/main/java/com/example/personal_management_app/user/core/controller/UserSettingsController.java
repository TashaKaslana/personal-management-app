package com.example.personal_management_app.user.core.controller;

import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.user.core.dtos.UpdateUserSettingsRequest;
import com.example.personal_management_app.user.core.dtos.UserSettingsDto;
import com.example.personal_management_app.user.core.service.UserSettingsService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{id}/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<RestApiResponse<UserSettingsDto>> getSettings(@PathVariable UUID id) {
        return RestApiResponse.success(userSettingsService.getSettings(id), "User settings retrieved successfully");
    }

    @PutMapping
    public ResponseEntity<RestApiResponse<UserSettingsDto>> updateSettings(
            @PathVariable UUID id, @Valid @RequestBody UpdateUserSettingsRequest request) {
        return RestApiResponse.success(userSettingsService.updateSettings(id, request), "User settings updated successfully");
    }
}
