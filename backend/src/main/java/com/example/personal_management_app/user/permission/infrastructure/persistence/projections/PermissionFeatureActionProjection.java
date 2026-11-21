package com.example.personal_management_app.user.permission.infrastructure.persistence.projections;

/**
 * Projection for getting feature and action fields from Permission
 */
public interface PermissionFeatureActionProjection {
    String getFeature();
    String getAction();
}
