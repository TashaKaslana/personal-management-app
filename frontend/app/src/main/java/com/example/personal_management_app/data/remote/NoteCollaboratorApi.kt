package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface NoteCollaboratorApi {
    @GET("notes/{id}/collaborators")
    suspend fun collaborators(
        @Path("id") id: String,
    ): Response<ApiResponse<Map<String, String>>>

    @PUT("notes/{id}/collaborators/{userId}")
    suspend fun setCollaborator(
        @Path("id") id: String,
        @Path("userId") userId: String,
        @Query("role") role: String,
    ): Response<ApiResponse<Unit>>

    @DELETE("notes/{id}/collaborators/{userId}")
    suspend fun removeCollaborator(
        @Path("id") id: String,
        @Path("userId") userId: String,
    ): Response<ApiResponse<Unit>>
}
