package com.example.personal_management_app.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DisplaySettingsSection(
    addItemsToBottom: Boolean,
    onAddItemsToBottomChange: (Boolean) -> Unit,
    moveCheckedToBottom: Boolean,
    onMoveCheckedToBottomChange: (Boolean) -> Unit,
    displayLinkPreviews: Boolean,
    onDisplayLinkPreviewsChange: (Boolean) -> Unit,
    themeSelection: String,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Mục hiển thị")

        SettingsSwitchItem(
            title = "Thêm các mục mới vào cuối",
            checked = addItemsToBottom,
            onCheckedChange = onAddItemsToBottomChange
        )

        SettingsSwitchItem(
            title = "Chuyển các mục đã chọn xuống dưới cùng",
            checked = moveCheckedToBottom,
            onCheckedChange = onMoveCheckedToBottomChange
        )

        SettingsSwitchItem(
            title = "Hiển thị xem trước đường liên kết đa phương tiện",
            checked = displayLinkPreviews,
            onCheckedChange = onDisplayLinkPreviewsChange
        )

        SettingsClickableItem(
            title = "Giao diện",
            subtitle = themeSelection,
            onClick = onThemeClick
        )
    }
}

