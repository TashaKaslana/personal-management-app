package com.example.personal_management_app.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.personal_management_app.entities.SettingEntity

@Composable
fun SharingSettingsSection(
    settings: SettingEntity,
    onUpdateSettings: ((SettingEntity) -> SettingEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Chia sẻ")

        SettingsSwitchItem(
            title = "Bật tính năng chia sẻ",
            checked = settings.enableSharing,
            onCheckedChange = { checked ->
                onUpdateSettings { it.copy(enableSharing = checked) }
            }
        )
    }
}
