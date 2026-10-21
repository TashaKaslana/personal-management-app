package com.example.personal_management_app.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.ui.theme.Loginblue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    var addItemsToBottom by remember { mutableStateOf(false) }
    var moveCheckedToBottom by remember { mutableStateOf(false) }
    var displayLinkPreviews by remember { mutableStateOf(false) }
    var themeSelection by remember { mutableStateOf("Mặc định theo hệ thống") }
    var morningReminder by remember { mutableStateOf("08:00") }
    var afternoonReminder by remember { mutableStateOf("13:00") }
    var eveningReminder by remember { mutableStateOf("18:00") }
    var enableSharing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1F1F1F)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF444746)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            GoogleKeepSectionHeader(title = "Mục hiển thị")

            GoogleKeepSwitchItem(
                title = "Thêm các mục mới vào cuối",
                checked = addItemsToBottom,
                onCheckedChange = { addItemsToBottom = it }
            )

            GoogleKeepSwitchItem(
                title = "Chuyển các mục đã chọn xuống dưới cùng",
                checked = moveCheckedToBottom,
                onCheckedChange = { moveCheckedToBottom = it }
            )

            GoogleKeepSwitchItem(
                title = "Hiển thị xem trước đường liên kết đa phương tiện",
                checked = displayLinkPreviews,
                onCheckedChange = { displayLinkPreviews = it }
            )

            GoogleKeepClickableItem(
                title = "Giao diện",
                subtitle = themeSelection,
                onClick = { }
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            GoogleKeepSectionHeader(title = "Mặc định lời nhắc")

            GoogleKeepClickableItem(
                title = "Buổi sáng",
                subtitle = morningReminder,
                onClick = { }
            )

            GoogleKeepClickableItem(
                title = "Buổi chiều",
                subtitle = afternoonReminder,
                onClick = { }
            )

            GoogleKeepClickableItem(
                title = "Buổi tối",
                subtitle = eveningReminder,
                onClick = { }
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            GoogleKeepSectionHeader(title = "Chia sẻ")

            GoogleKeepSwitchItem(
                title = "Bật tính năng chia sẻ",
                checked = enableSharing,
                onCheckedChange = { enableSharing = it }
            )
        }
    }
}

@Composable
fun GoogleKeepSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Loginblue,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@Composable
fun GoogleKeepSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF1F1F1F),
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Loginblue,
                uncheckedThumbColor = Color(0xFF747775),
                uncheckedTrackColor = Color(0xFFE1E3E1)
            )
        )
    }
}

@Composable
fun GoogleKeepClickableItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF1F1F1F)
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF747775)
            )
        }
    }
}

