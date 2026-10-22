package com.example.personal_management_app.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.settings.DisplaySettingsSection
import com.example.personal_management_app.ui.components.settings.NotificationSettingsSection
import com.example.personal_management_app.ui.components.settings.SharingSettingsSection
import com.example.personal_management_app.viewmodel.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by settingViewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                settingViewModel.loadNotes()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
            DisplaySettingsSection(
                settings = uiState.settings,
                onUpdateSettings = settingViewModel::updateSettings,
                onThemeClick = { }
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            NotificationSettingsSection(
                settings = uiState.settings,
                onUpdateSettings = settingViewModel::updateSettings,
                activeRemindersCount = uiState.reminderNotes.size,
                upcomingRemindersSummary = uiState.upcomingSummary,
                reminderNotes = uiState.reminderNotes
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            SharingSettingsSection(
                settings = uiState.settings,
                onUpdateSettings = settingViewModel::updateSettings
            )
        }
    }
}
