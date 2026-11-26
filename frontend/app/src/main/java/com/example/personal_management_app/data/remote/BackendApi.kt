package com.example.personal_management_app.data.remote

import com.example.personal_management_app.BuildConfig
import com.example.personal_management_app.core.service.SessionManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Singleton
class BackendApi @Inject constructor(sessionManager: SessionManager) {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val token = sessionManager.sessionState.value?.token
            val authenticated = if (token == null) request else request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticated)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BACKEND_URL)
        .client(client)
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build()

    val noteCollaborator: NoteCollaboratorApi = retrofit.create(NoteCollaboratorApi::class.java)
    val note: NoteApi = retrofit.create(NoteApi::class.java)
    val noteReminder: NoteReminderApi = retrofit.create(NoteReminderApi::class.java)
    val tag: TagApi = retrofit.create(TagApi::class.java)
    val auth: AuthApi = retrofit.create(AuthApi::class.java)
    val user: UserApi = retrofit.create(UserApi::class.java)
    val userSettings: UserSettingsApi = retrofit.create(UserSettingsApi::class.java)
    val permission: PermissionApi = retrofit.create(PermissionApi::class.java)
    val role: RoleApi = retrofit.create(RoleApi::class.java)
    val rolePermission: RolePermissionApi = retrofit.create(RolePermissionApi::class.java)
}
