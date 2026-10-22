package com.example.personal_management_app.user.permission.infrastructure.persistence.projections;

/**
 * Projection for getting only the action field from Permission
 */
public interface PermissionActionProjection {
    String getAction();
}
