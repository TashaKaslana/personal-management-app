package com.example.personal_management_app.user.core.infrastructure.persistence.projections;

/**
 * Projection for getting only the username field from User
 */
public interface UserUsernameProjection {
    String getUsername();
}
