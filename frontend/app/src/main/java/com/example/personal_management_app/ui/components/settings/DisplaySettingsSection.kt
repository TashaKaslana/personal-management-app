package com.example.personal_management_app.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.personal_management_app.entities.SettingEntity

@Composable
fun DisplaySettingsSection(
    settings: SettingEntity,
    onUpdateSettings: ((SettingEntity) -> SettingEntity) -> Unit,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Mục hiển thị")

        SettingsSwitchItem(
            title = "Thêm các mục mới vào cuối",
            checked = settings.addItemsToBottom,
            onCheckedChange = { checked ->
                onUpdateSettings { it.copy(addItemsToBottom = checked) }
            }
        )

        SettingsSwitchItem(
            title = "Chuyển các mục đã chọn xuống dưới cùng",
            checked = settings.moveCheckedToBottom,
            onCheckedChange = { checked ->
                onUpdateSettings { it.copy(moveCheckedToBottom = checked) }
            }
        )

        SettingsSwitchItem(
            title = "Hiển thị xem trước đường liên kết đa phương tiện",
            checked = settings.displayLinkPreviews,
            onCheckedChange = { checked ->
                onUpdateSettings { it.copy(displayLinkPreviews = checked) }
            }
        )

        SettingsClickableItem(
            title = "Giao diện",
            subtitle = settings.themeSelection,
            onClick = onThemeClick
        )
    }
}
