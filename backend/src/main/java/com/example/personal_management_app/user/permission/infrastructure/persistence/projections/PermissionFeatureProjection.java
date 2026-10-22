package com.example.personal_management_app.user.permission.infrastructure.persistence.projections;

/**
 * Projection for getting only the feature field from Permission
 */
public interface PermissionFeatureProjection {
    String getFeature();
}
