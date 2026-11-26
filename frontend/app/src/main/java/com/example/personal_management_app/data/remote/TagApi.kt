package com.example.personal_management_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface TagApi {
    @POST("tags")
    suspend fun createTag(
        @Body request: CreateTagRequest,
    ): Response<ApiResponse<TagDto>>

    @POST("tags/bulk")
    suspend fun createTags(
        @Body requests: List<CreateTagRequest>,
    ): Response<ApiResponse<List<TagDto>>>

    @GET("tags/{id}")
    suspend fun getTagById(
        @Path("id") id: String,
    ): Response<ApiResponse<TagDto>>

    @GET("tags")
    suspend fun getAllTags(): Response<ApiResponse<List<TagDto>>>

    @GET("tags/paginated")
    suspend fun getAllTags(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): Response<ApiResponse<List<TagDto>>>

    @GET("tags/name/{name}")
    suspend fun getTagByName(
        @Path("name") name: String,
    ): Response<ApiResponse<TagDto>>

    @PUT("tags/{id}")
    suspend fun updateTag(
        @Path("id") id: String,
        @Body request: CreateTagRequest,
    ): Response<ApiResponse<TagDto>>

    @DELETE("tags/{id}")
    suspend fun deleteTag(
        @Path("id") id: String,
    ): Response<ApiResponse<Unit>>

    @DELETE("tags/{id}/links")
    suspend fun clearTag(
        @Path("id") id: String,
    ): Response<ApiResponse<Int>>

    @GET("tags/{id}/notes")
    suspend fun getNotesByTag(
        @Path("id") id: String,
    ): Response<ApiResponse<List<NoteDto>>>

    @GET("tags/{id}/note-count")
    suspend fun getNoteCountByTag(
        @Path("id") id: String,
    ): Response<ApiResponse<Long>>

    @GET("tags/count")
    suspend fun getTagCount(): Response<ApiResponse<Long>>

    @GET("tags/check/name/{name}")
    suspend fun checkTagExistsByName(
        @Path("name") name: String,
    ): Response<ApiResponse<Boolean>>

    @GET("tags/{id}/exists")
    suspend fun checkTagExists(
        @Path("id") id: String,
    ): Response<ApiResponse<Boolean>>
}
