package com.example.personal_management_app.entities

data class SettingEntity(
    val addItemsToBottom: Boolean = false,
    val moveCheckedToBottom: Boolean = false,
    val displayLinkPreviews: Boolean = false,
    val themeSelection: String = "Mặc định theo hệ thống",
    val disableAllNotifications: Boolean = false,
    val disableMorning: Boolean = false,
    val morningTime: String = "08:00",
    val disableAfternoon: Boolean = false,
    val afternoonTime: String = "13:00",
    val disableNight: Boolean = false,
    val nightTime: String = "18:00",
    val enableSharing: Boolean = false
)

