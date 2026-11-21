package com.example.personal_management_app.core.security;

import java.util.Optional;
import java.util.UUID;

/** Shared identity lookup, independent of domain entities and repositories. */
public interface CurrentUserProvider {
    /** Returns the current user ID, or empty when no identity is available. */
    Optional<UUID> getCurrentUserId();
}
