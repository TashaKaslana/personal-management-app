package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface NoteReminderApi {
    @PUT("notes/{id}/reminder")
    suspend fun setNoteReminder(
        @Path("id") id: String,
        @Body request: SetReminderRequest,
    ): Response<ApiResponse<NoteDto>>

    @DELETE("notes/{id}/reminder")
    suspend fun clearNoteReminder(
        @Path("id") id: String,
    ): Response<ApiResponse<NoteDto>>
}
