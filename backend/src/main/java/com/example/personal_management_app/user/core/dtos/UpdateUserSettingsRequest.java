package com.example.personal_management_app.user.core.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record UpdateUserSettingsRequest(@NotNull Map<String, Object> settings) {
}
