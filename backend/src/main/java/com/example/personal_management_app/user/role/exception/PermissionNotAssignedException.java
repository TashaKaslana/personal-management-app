package com.example.personal_management_app.user.role.exception;

import com.example.personal_management_app.user.role.enums.RoleError;

/**
 * Exception thrown when attempting to remove a permission that is not assigned to a role
 */
public class PermissionNotAssignedException extends RoleDomainException {

    /**
     * Constructor with role ID and permission ID
     *
     * @param roleId The ID of the role
     * @param permissionId The ID of the permission
     */
    public PermissionNotAssignedException(String roleId, String permissionId) {
        super(RoleError.PERMISSION_NOT_ASSIGNED, "roleId: " + roleId + ", permissionId: " + permissionId);
    }
}
