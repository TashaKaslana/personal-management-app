package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface UserSettingsApi {
    @GET("users/{id}/settings")
    suspend fun getSettings(
        @Path("id") id: String,
    ): Response<ApiResponse<UserSettingsDto>>

    @PUT("users/{id}/settings")
    suspend fun updateSettings(
        @Path("id") id: String,
        @Body request: UpdateUserSettingsRequest,
    ): Response<ApiResponse<UserSettingsDto>>
}
