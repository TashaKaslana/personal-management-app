package com.example.personal_management_app.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SharingSettingsSection(
    enableSharing: Boolean,
    onEnableSharingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Chia sẻ")

        SettingsSwitchItem(
            title = "Bật tính năng chia sẻ",
            checked = enableSharing,
            onCheckedChange = onEnableSharingChange
        )
    }
}

