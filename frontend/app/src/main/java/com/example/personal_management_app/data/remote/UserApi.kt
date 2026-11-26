package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @POST("users")
    suspend fun createUser(
        @Body request: CreateUserRequest,
    ): Response<ApiResponse<UserDto>>

    @POST("users/bulk")
    suspend fun createUsers(
        @Body requests: List<CreateUserRequest>,
    ): Response<ApiResponse<List<UserDto>>>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String,
    ): Response<ApiResponse<UserDto>>

    @GET("users")
    suspend fun getAllUsers(): Response<ApiResponse<List<UserDto>>>

    @GET("users/paginated")
    suspend fun getAllUsers(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): Response<ApiResponse<List<UserDto>>>

    @GET("users/email/{email}")
    suspend fun getUserByEmail(
        @Path("email") email: String,
    ): Response<ApiResponse<UserDto>>

    @GET("users/username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String,
    ): Response<ApiResponse<UserDto>>

    @GET("users/role/{roleName}")
    suspend fun getUsersByRole(
        @Path("roleName") roleName: String,
    ): Response<ApiResponse<List<UserDto>>>

    @POST("users/emails")
    suspend fun getUsersByEmails(
        @Body emails: List<String>,
    ): Response<ApiResponse<List<UserDto>>>

    @POST("users/usernames")
    suspend fun getUsersByUsernames(
        @Body usernames: List<String>,
    ): Response<ApiResponse<List<UserDto>>>

    @GET("users/active")
    suspend fun getActiveUsers(): Response<ApiResponse<List<UserDto>>>

    @GET("users/deleted")
    suspend fun getDeletedUsers(): Response<ApiResponse<List<UserDto>>>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest,
    ): Response<ApiResponse<UserDto>>

    @PUT("users/{id}/password")
    suspend fun updateUserPassword(
        @Path("id") id: String,
        @Body request: ChangePasswordRequest,
    ): Response<ApiResponse<UserDto>>

    @PUT("users/{id}/roles/{newRole}")
    suspend fun changeUserRole(
        @Path("id") id: String,
        @Path("newRole") newRole: String,
    ): Response<ApiResponse<UserDto>>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "users/bulk", hasBody = true)
    suspend fun deleteUsers(
        @Body ids: List<String>,
    ): Response<ApiResponse<Unit>>

    @DELETE("users/{id}/hard")
    suspend fun hardDeleteUser(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @PUT("users/{id}/restore")
    suspend fun restoreUser(
        @Path("id") id: String,
    ): Response<ApiResponse<UserDto>>

    @GET("users/check/email/{email}")
    suspend fun checkEmailTaken(
        @Path("email") email: String,
    ): Response<ApiResponse<Boolean>>

    @GET("users/check/username/{username}")
    suspend fun checkUsernameTaken(
        @Path("username") username: String,
    ): Response<ApiResponse<Boolean>>

    @GET("users/count")
    suspend fun getUserCount(): Response<ApiResponse<Long>>

    @GET("users/emails")
    suspend fun getAllEmails(): Response<ApiResponse<List<String>>>

    @GET("users/usernames")
    suspend fun getAllUsernames(): Response<ApiResponse<List<String>>>

    @GET("users/{id}/permissions")
    suspend fun getUserPermissions(
        @Path("id") id: String,
    ): Response<ApiResponse<List<PermissionDto>>>

    @GET("users/{id}/permissions/check")
    suspend fun checkUserPermission(
        @Path("id") id: String,
        @Query("feature") feature: String,
        @Query("action") action: String,
    ): Response<ApiResponse<Boolean>>

    @POST("users/{id}/permissions/check-any")
    suspend fun checkUserHasAnyPermission(
        @Path("id") id: String,
        @Query("features") features: List<String>,
        @Query("actions") actions: List<String>,
    ): Response<ApiResponse<Boolean>>

    @POST("users/{id}/permissions/check-all")
    suspend fun checkUserHasAllPermissions(
        @Path("id") id: String,
        @Query("features") features: List<String>,
        @Query("actions") actions: List<String>,
    ): Response<ApiResponse<Boolean>>
}
