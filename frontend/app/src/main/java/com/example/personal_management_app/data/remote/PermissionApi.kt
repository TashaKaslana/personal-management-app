package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface PermissionApi {
    @POST("permissions")
    suspend fun createPermission(
        @Body request: CreatePermissionRequest,
    ): Response<ApiResponse<PermissionDto>>

    @POST("permissions/bulk")
    suspend fun createPermissions(
        @Body requests: List<CreatePermissionRequest>,
    ): Response<ApiResponse<List<PermissionDto>>>

    @GET("permissions/{id}")
    suspend fun getPermissionById(
        @Path("id") id: String,
    ): Response<ApiResponse<PermissionDto>>

    @GET("permissions")
    suspend fun getAllPermissions(): Response<ApiResponse<List<PermissionDto>>>

    @GET("permissions/paginated")
    suspend fun getAllPermissions(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): Response<ApiResponse<List<PermissionDto>>>

    @GET("permissions/feature/{feature}")
    suspend fun getPermissionsByFeature(
        @Path("feature") feature: String,
    ): Response<ApiResponse<List<PermissionDto>>>

    @GET("permissions/action/{action}")
    suspend fun getPermissionsByAction(
        @Path("action") action: String,
    ): Response<ApiResponse<List<PermissionDto>>>

    @POST("permissions/features")
    suspend fun getPermissionsByFeatures(
        @Body features: List<String>,
    ): Response<ApiResponse<List<PermissionDto>>>

    @POST("permissions/actions")
    suspend fun getPermissionsByActions(
        @Body actions: List<String>,
    ): Response<ApiResponse<List<PermissionDto>>>

    @GET("permissions/feature/{feature}/action/{action}")
    suspend fun getPermissionByFeatureAndAction(
        @Path("feature") feature: String,
        @Path("action") action: String,
    ): Response<ApiResponse<PermissionDto>>

    @GET("permissions/check/feature/{feature}/action/{action}")
    suspend fun checkPermissionExists(
        @Path("feature") feature: String,
        @Path("action") action: String,
    ): Response<ApiResponse<Boolean>>

    @GET("permissions/features")
    suspend fun getAllFeatures(): Response<ApiResponse<List<String>>>

    @GET("permissions/actions")
    suspend fun getAllActions(): Response<ApiResponse<List<String>>>

    @PUT("permissions/{id}")
    suspend fun updatePermission(
        @Path("id") id: String,
        @Body request: CreatePermissionRequest,
    ): Response<ApiResponse<PermissionDto>>

    @DELETE("permissions/{id}")
    suspend fun deletePermission(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "permissions/bulk", hasBody = true)
    suspend fun deletePermissions(
        @Body ids: List<String>,
    ): Response<ApiResponse<Unit>>

    @GET("permissions/count")
    suspend fun getPermissionCount(): Response<ApiResponse<Long>>

    @GET("permissions/{id}/exists")
    suspend fun checkPermissionExists(
        @Path("id") id: String,
    ): Response<ApiResponse<Boolean>>
}
