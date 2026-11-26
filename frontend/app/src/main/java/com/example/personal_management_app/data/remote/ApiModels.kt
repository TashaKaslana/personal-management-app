package com.example.personal_management_app.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class AuthResponse(
    val token: String? = null,
    val user: UserDto? = null,
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String? = null,
    val newPassword: String? = null,
)

@Serializable
data class CreateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val titleStyle: NoteTextStyleDto? = null,
    val contentStyle: NoteTextStyleDto? = null,
    val backgroundColor: String? = null,
    val pinned: Boolean? = null,
    val reminderAt: String? = null,
    val reminderRepeat: String? = null,
    val tagIds: List<String>? = null,
)

@Serializable
data class CreatePermissionRequest(
    val feature: String? = null,
    val action: String? = null,
    val description: String? = null,
)

@Serializable
data class CreateRoleRequest(
    val name: String? = null,
    val description: String? = null,
)

@Serializable
data class CreateTagRequest(
    val name: String? = null,
    val color: String? = null,
)

@Serializable
data class CreateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val username: String? = null,
    val roleName: String? = null,
)

@Serializable
data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
)

@Serializable
data class NoteDto(
    val id: String? = null,
    val version: Long? = null,
    val title: String? = null,
    val titleStyle: NoteTextStyleDto? = null,
    val content: String? = null,
    val contentStyle: NoteTextStyleDto? = null,
    val tags: List<TagDto>? = null,
    val backgroundColor: String? = null,
    val pinned: Boolean? = null,
    val archivedAt: String? = null,
    val deletedAt: String? = null,
    val reminderAt: String? = null,
    val reminderRepeat: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)

@Serializable
data class NoteTextStyleDto(
    val fontSize: Float? = null,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val underline: Boolean? = null,
)

@Serializable
data class PermissionDto(
    val id: String? = null,
    val feature: String? = null,
    val action: String? = null,
    val description: String? = null,
)

@Serializable
data class RegisterRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
)

@Serializable
data class RoleDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)

@Serializable
data class SetArchiveStatusRequest(
    val archived: Boolean? = null,
)

@Serializable
data class SetNoteTagsRequest(
    val tagIds: List<String>? = null,
)

@Serializable
data class SetPinStatusRequest(
    val pinned: Boolean? = null,
)

@Serializable
data class SetReminderRequest(
    val at: String? = null,
    val repeat: String? = null,
)

@Serializable
data class TagDto(
    val id: String? = null,
    val name: String? = null,
    val color: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val titleStyle: NoteTextStyleDto? = null,
    val contentStyle: NoteTextStyleDto? = null,
    val backgroundColor: String? = null,
    val pinned: Boolean? = null,
    val reminderAt: String? = null,
    val reminderRepeat: String? = null,
    val tagIds: List<String>? = null,
    val version: Long? = null,
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val roleName: String? = null,
)

@Serializable
data class UpdateUserSettingsRequest(
    val settings: Map<String, JsonElement>? = null,
)

@Serializable
data class UserDto(
    val id: String? = null,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val role: RoleDto? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null,
)

@Serializable
data class UserSettingsDto(
    val settings: Map<String, JsonElement>? = null,
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val metadata: JsonObject? = null,
)
