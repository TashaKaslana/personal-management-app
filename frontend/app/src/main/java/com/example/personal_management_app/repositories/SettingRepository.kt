package com.example.personal_management_app.repositories

import android.content.Context
import com.example.personal_management_app.entities.SettingEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

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

    fun updateSettings(transform: (SettingEntity) -> SettingEntity) {
        val updated = transform(_settings.value)
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

