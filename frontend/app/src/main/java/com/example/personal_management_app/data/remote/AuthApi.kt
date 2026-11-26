package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<ApiResponse<AuthResponse>>
}
