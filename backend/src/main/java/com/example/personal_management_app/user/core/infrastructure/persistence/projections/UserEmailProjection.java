package com.example.personal_management_app.user.core.infrastructure.persistence.projections;

/**
 * Projection for getting only the email field from User
 */
public interface UserEmailProjection {
    String getEmail();
}
