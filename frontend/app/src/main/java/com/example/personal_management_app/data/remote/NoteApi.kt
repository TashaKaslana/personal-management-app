package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface NoteApi {
    @POST("notes")
    suspend fun createNote(
        @Body request: CreateNoteRequest,
    ): Response<ApiResponse<NoteDto>>

    @POST("notes/bulk")
    suspend fun createNotes(
        @Body requests: List<CreateNoteRequest>,
    ): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/{id}")
    suspend fun getNoteById(
        @Path("id") id: String,
    ): Response<ApiResponse<NoteDto>>

    @GET("notes")
    suspend fun getAllNotes(): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/unarchived")
    suspend fun getUnarchivedNotes(): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/archived")
    suspend fun getArchivedNotes(): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/pinned")
    suspend fun getPinnedNotes(): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/paginated")
    suspend fun getAllNotes(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/search")
    suspend fun searchNotes(
        @Query("q") query: String = "",
    ): Response<ApiResponse<List<NoteDto>>>

    @GET("notes/tag/{tagId}")
    suspend fun getNotesByTag(
        @Path("tagId") tagId: String,
    ): Response<ApiResponse<List<NoteDto>>>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body request: UpdateNoteRequest,
    ): Response<ApiResponse<NoteDto>>

    @PATCH("notes/{id}/pin")
    suspend fun setNotePinned(
        @Path("id") id: String,
        @Body request: SetPinStatusRequest,
    ): Response<ApiResponse<NoteDto>>

    @PATCH("notes/{id}/archive")
    suspend fun setNoteArchived(
        @Path("id") id: String,
        @Body request: SetArchiveStatusRequest,
    ): Response<ApiResponse<NoteDto>>

    @DELETE("notes/{id}")
    suspend fun deleteNote(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "notes/bulk", hasBody = true)
    suspend fun deleteNotes(
        @Body ids: List<String>,
    ): Response<ApiResponse<Unit>>

    @GET("notes/trash")
    suspend fun getTrashItems(
        @Query("q") query: String = "",
    ): Response<ApiResponse<List<NoteDto>>>

    @POST("notes/{id}/restore")
    suspend fun restoreNote(
        @Path("id") id: String,
    ): Response<ApiResponse<NoteDto>>

    @DELETE("notes/{id}/permanent")
    suspend fun deleteNotePermanently(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @DELETE("notes/trash")
    suspend fun emptyTrash(): Response<ApiResponse<Int>>

    @POST("notes/trash/purge")
    suspend fun purgeExpiredTrash(): Response<ApiResponse<Int>>

    @GET("notes/{id}/tags")
    suspend fun getNoteTags(
        @Path("id") id: String,
    ): Response<ApiResponse<List<TagDto>>>

    @PUT("notes/{id}/tags")
    suspend fun setNoteTags(
        @Path("id") id: String,
        @Body request: SetNoteTagsRequest,
    ): Response<ApiResponse<NoteDto>>

    @POST("notes/{id}/tags/{tagId}")
    suspend fun assignTagToNote(
        @Path("id") id: String,
        @Path("tagId") tagId: String,
    ): Response<ApiResponse<Unit>>

    @DELETE("notes/{id}/tags/{tagId}")
    suspend fun removeTagFromNote(
        @Path("id") id: String,
        @Path("tagId") tagId: String,
    ): Response<ApiResponse<Unit>>

    @GET("notes/count")
    suspend fun getNoteCount(): Response<ApiResponse<Long>>

    @GET("notes/count/active")
    suspend fun getActiveNoteCount(): Response<ApiResponse<Long>>

    @GET("notes/count/archived")
    suspend fun getArchivedNoteCount(): Response<ApiResponse<Long>>

    @GET("notes/count/trashed")
    suspend fun getTrashedNoteCount(): Response<ApiResponse<Long>>

    @GET("notes/count/tag/{tagId}")
    suspend fun getNoteCountByTag(
        @Path("tagId") tagId: String,
    ): Response<ApiResponse<Long>>

    @GET("notes/{id}/exists")
    suspend fun checkNoteExists(
        @Path("id") id: String,
    ): Response<ApiResponse<Boolean>>
}
