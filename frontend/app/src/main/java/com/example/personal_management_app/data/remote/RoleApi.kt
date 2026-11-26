package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface RoleApi {
    @POST("roles")
    suspend fun createRole(
        @Body request: CreateRoleRequest,
    ): Response<ApiResponse<RoleDto>>

    @POST("roles/bulk")
    suspend fun createRoles(
        @Body requests: List<CreateRoleRequest>,
    ): Response<ApiResponse<List<RoleDto>>>

    @GET("roles/{id}")
    suspend fun getRoleById(
        @Path("id") id: String,
    ): Response<ApiResponse<RoleDto>>

    @GET("roles")
    suspend fun getAllRoles(): Response<ApiResponse<List<RoleDto>>>

    @GET("roles/paginated")
    suspend fun getAllRoles(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): Response<ApiResponse<List<RoleDto>>>

    @GET("roles/name/{name}")
    suspend fun getRoleByName(
        @Path("name") name: String,
    ): Response<ApiResponse<RoleDto>>

    @POST("roles/names")
    suspend fun getRolesByNames(
        @Body names: List<String>,
    ): Response<ApiResponse<List<RoleDto>>>

    @GET("roles/check/name/{name}")
    suspend fun checkRoleExistsByName(
        @Path("name") name: String,
    ): Response<ApiResponse<Boolean>>

    @GET("roles/default")
    suspend fun getDefaultRole(): Response<ApiResponse<RoleDto>>

    @GET("roles/admin")
    suspend fun getAdminRole(): Response<ApiResponse<RoleDto>>

    @GET("roles/owner")
    suspend fun getOwnerRole(): Response<ApiResponse<RoleDto>>

    @PUT("roles/{id}")
    suspend fun updateRole(
        @Path("id") id: String,
        @Body request: CreateRoleRequest,
    ): Response<ApiResponse<RoleDto>>

    @DELETE("roles/{id}")
    suspend fun deleteRole(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "roles/bulk", hasBody = true)
    suspend fun deleteRoles(
        @Body ids: List<String>,
    ): Response<ApiResponse<Unit>>

    @GET("roles/count")
    suspend fun getRoleCount(): Response<ApiResponse<Long>>

    @GET("roles/{id}/exists")
    suspend fun checkRoleExists(
        @Path("id") id: String,
    ): Response<ApiResponse<Boolean>>
}
