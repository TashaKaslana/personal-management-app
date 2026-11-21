package com.example.personal_management_app.core.security;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/** Temporary identity source until current-user resolution is connected to authentication. */
@Component
public class PlaceholderCurrentUserProvider implements CurrentUserProvider {
    private static final UUID PLACEHOLDER_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public Optional<UUID> getCurrentUserId() {
        return Optional.of(PLACEHOLDER_USER_ID);
    }
}
