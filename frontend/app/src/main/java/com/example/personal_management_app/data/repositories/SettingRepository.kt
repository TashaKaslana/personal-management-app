package com.example.personal_management_app.data.repositories

import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.remote.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import android.content.Context
import com.example.personal_management_app.data.entities.SettingEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val api: BackendApi,
    private val requests: RepositoryRequests,
    private val sessions: SessionManager,
    @ApplicationContext private val context: Context
) {
    private val sharedPrefs get() = context.getSharedPreferences(
        sessions.sessionState.value?.let { "app_settings_${it.userId}" } ?: "app_settings",
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<SettingEntity> = _settings.asStateFlow()

    private fun readSettings(): SettingEntity {
        return SettingEntity(
            addItemsToBottom = sharedPrefs.getBoolean("add_items_to_bottom", false),
            moveCheckedToBottom = sharedPrefs.getBoolean("move_checked_to_bottom", false),
            displayLinkPreviews = sharedPrefs.getBoolean("display_link_previews", false),
            themeSelection = sharedPrefs.getString("theme_selection", "Mặc định theo hệ thống") ?: "Mặc định theo hệ thống",
            disableAllNotifications = sharedPrefs.getBoolean("disable_all_notifications", false),
            disableMorning = sharedPrefs.getBoolean("disable_morning", false),
            morningTime = sharedPrefs.getString("morning_time", "08:00") ?: "08:00",
            disableAfternoon = sharedPrefs.getBoolean("disable_afternoon", false),
            afternoonTime = sharedPrefs.getString("afternoon_time", "13:00") ?: "13:00",
            disableNight = sharedPrefs.getBoolean("disable_night", false),
            nightTime = sharedPrefs.getString("night_time", "18:00") ?: "18:00",
            enableSharing = sharedPrefs.getBoolean("enable_sharing", false)
        )
    }

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            sessions.sessionState.collect { session ->
                _settings.value = readSettings()
                if (session != null) requests.submit { uid ->
                    val values = api.userSettings.getSettings(uid).payload().settings.orEmpty()
                    if (session == sessions.sessionState.value) persist(json.decodeFromJsonElement(JsonObject(values)))
                }
            }
        }
    }

    suspend fun updateSettings(transform: (SettingEntity) -> SettingEntity) {
        if (sessions.sessionState.value == null) {
            persist(transform(_settings.value))
            return
        }
        requests.execute { uid ->
            val existing = api.userSettings.getSettings(uid).payload().settings.orEmpty()
            val updated = transform(json.decodeFromJsonElement<SettingEntity>(JsonObject(existing)))
            val values = existing + json.encodeToJsonElement(updated).jsonObject
            api.userSettings.updateSettings(uid, UpdateUserSettingsRequest(values)).payload()
            if (sessions.sessionState.value?.userId == uid) persist(updated)
        }
    }

    private fun persist(updated: SettingEntity) {
        _settings.value = updated
        sharedPrefs.edit().apply {
            putBoolean("add_items_to_bottom", updated.addItemsToBottom)
            putBoolean("move_checked_to_bottom", updated.moveCheckedToBottom)
            putBoolean("display_link_previews", updated.displayLinkPreviews)
            putString("theme_selection", updated.themeSelection)
            putBoolean("disable_all_notifications", updated.disableAllNotifications)
            putBoolean("disable_morning", updated.disableMorning)
            putString("morning_time", updated.morningTime)
            putBoolean("disable_afternoon", updated.disableAfternoon)
            putString("afternoon_time", updated.afternoonTime)
            putBoolean("disable_night", updated.disableNight)
            putString("night_time", updated.nightTime)
            putBoolean("enable_sharing", updated.enableSharing)
            apply()
        }
    }
}

