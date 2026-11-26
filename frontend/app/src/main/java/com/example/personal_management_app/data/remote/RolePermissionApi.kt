package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*
import kotlinx.serialization.json.JsonObject

interface RolePermissionApi {
    @POST("role-permissions/assign")
    suspend fun assignPermissionToRole(
        @Query("roleId") roleId: String,
        @Query("permissionId") permissionId: String,
    ): Response<ApiResponse<Unit>>

    @POST("role-permissions/assign-bulk")
    suspend fun assignPermissionsToRole(
        @Query("roleId") roleId: String,
        @Body permissionIds: List<String>,
    ): Response<ApiResponse<Unit>>

    @DELETE("role-permissions/remove")
    suspend fun removePermissionFromRole(
        @Query("roleId") roleId: String,
        @Query("permissionId") permissionId: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "role-permissions/remove-bulk", hasBody = true)
    suspend fun removePermissionsFromRole(
        @Query("roleId") roleId: String,
        @Body permissionIds: List<String>,
    ): Response<ApiResponse<Unit>>

    @DELETE("role-permissions/remove-all")
    suspend fun removeAllPermissionsFromRole(
        @Query("roleId") roleId: String,
    ): Response<ApiResponse<Unit>>

    @GET("role-permissions/role/{roleId}/permissions")
    suspend fun getPermissionsByRole(
        @Path("roleId") roleId: String,
    ): Response<ApiResponse<List<JsonObject>>>

    @GET("role-permissions/permission/{permissionId}/roles")
    suspend fun getRolesByPermission(
        @Path("permissionId") permissionId: String,
    ): Response<ApiResponse<List<JsonObject>>>

    @GET("role-permissions/check")
    suspend fun checkPermissionAssignedToRole(
        @Query("roleId") roleId: String,
        @Query("permissionId") permissionId: String,
    ): Response<ApiResponse<Boolean>>

    @GET("role-permissions/check-feature-action")
    suspend fun checkRoleHasPermission(
        @Query("roleId") roleId: String,
        @Query("feature") feature: String,
        @Query("action") action: String,
    ): Response<ApiResponse<Boolean>>

    @GET("role-permissions/role/{roleId}/permissions/count")
    suspend fun countPermissionsByRole(
        @Path("roleId") roleId: String,
    ): Response<ApiResponse<Long>>

    @GET("role-permissions/permission/{permissionId}/roles/count")
    suspend fun countRolesByPermission(
        @Path("permissionId") permissionId: String,
    ): Response<ApiResponse<Long>>

    @GET("role-permissions/count")
    suspend fun getTotalRolePermissionCount(): Response<ApiResponse<Long>>
}
