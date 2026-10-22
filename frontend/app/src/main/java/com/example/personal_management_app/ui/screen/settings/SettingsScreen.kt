package com.example.personal_management_app.ui.screen.settings

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.components.settings.DayPeriod
import com.example.personal_management_app.ui.components.settings.DisplaySettingsSection
import com.example.personal_management_app.ui.components.settings.NotificationSettingsSection
import com.example.personal_management_app.ui.components.settings.SharingSettingsSection
import com.example.personal_management_app.ui.screen.reminder.ReminderScheduler
import com.example.personal_management_app.viewmodel.NoteViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    var addItemsToBottom by remember {
        mutableStateOf(sharedPrefs.getBoolean("add_items_to_bottom", false))
    }
    var moveCheckedToBottom by remember {
        mutableStateOf(sharedPrefs.getBoolean("move_checked_to_bottom", false))
    }
    var displayLinkPreviews by remember {
        mutableStateOf(sharedPrefs.getBoolean("display_link_previews", false))
    }
    var themeSelection by remember {
        mutableStateOf(sharedPrefs.getString("theme_selection", "Mặc định theo hệ thống") ?: "Mặc định theo hệ thống")
    }

    var disableAllNotifications by remember {
        mutableStateOf(sharedPrefs.getBoolean("disable_all_notifications", false))
    }
    var disableMorning by remember {
        mutableStateOf(sharedPrefs.getBoolean("disable_morning", false))
    }
    var morningTime by remember {
        mutableStateOf(sharedPrefs.getString("morning_time", "08:00") ?: "08:00")
    }

    var disableAfternoon by remember {
        mutableStateOf(sharedPrefs.getBoolean("disable_afternoon", false))
    }
    var afternoonTime by remember {
        mutableStateOf(sharedPrefs.getString("afternoon_time", "13:00") ?: "13:00")
    }

    var disableNight by remember {
        mutableStateOf(sharedPrefs.getBoolean("disable_night", false))
    }
    var nightTime by remember {
        mutableStateOf(sharedPrefs.getString("night_time", "18:00") ?: "18:00")
    }

    var enableSharing by remember {
        mutableStateOf(sharedPrefs.getBoolean("enable_sharing", false))
    }

    val notes by noteViewModel.notes.collectAsStateWithLifecycle()
    val now = System.currentTimeMillis()

    val reminderNotes = remember(notes) {
        notes.filter { it.reminder != null && it.deletedAt == null && it.archivedAt == null }
            .sortedBy { it.reminder?.atMillis ?: 0L }
    }

    val nextUpcomingReminder = remember(reminderNotes, now) {
        reminderNotes
            .filter { (it.reminder?.atMillis ?: 0L) >= now }
            .minByOrNull { it.reminder?.atMillis ?: Long.MAX_VALUE }
    }

    val upcomingSummary = remember(nextUpcomingReminder) {
        nextUpcomingReminder?.let { note ->
            val reminderLabel = note.reminder?.label() ?: ""
            "${note.title.ifBlank { "Không có tiêu đề" }} ($reminderLabel)"
        } ?: "Không có lời nhắc nào sắp tới"
    }

    val reminderScheduler = remember {
        val entryPoint = dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            SettingsEntryPoint::class.java
        )
        entryPoint.reminderScheduler()
    }

    fun syncAlarmsWithSettings(
        allMuted: Boolean,
        morningMuted: Boolean,
        afternoonMuted: Boolean,
        nightMuted: Boolean
    ) {
        reminderNotes.forEach { note ->
            val noteReminder = note.reminder ?: return@forEach
            val cal = Calendar.getInstance().apply { timeInMillis = noteReminder.atMillis }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val period = DayPeriod.fromHour(hour)

            val isMuted = allMuted || when (period) {
                DayPeriod.MORNING -> morningMuted
                DayPeriod.AFTERNOON -> afternoonMuted
                DayPeriod.NIGHT -> nightMuted
            }

            if (isMuted) {
                reminderScheduler.cancel(note.id)
            } else {
                reminderScheduler.apply(note)
            }
        }
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
                addItemsToBottom = addItemsToBottom,
                onAddItemsToBottomChange = {
                    addItemsToBottom = it
                    sharedPrefs.edit().putBoolean("add_items_to_bottom", it).apply()
                },
                moveCheckedToBottom = moveCheckedToBottom,
                onMoveCheckedToBottomChange = {
                    moveCheckedToBottom = it
                    sharedPrefs.edit().putBoolean("move_checked_to_bottom", it).apply()
                },
                displayLinkPreviews = displayLinkPreviews,
                onDisplayLinkPreviewsChange = {
                    displayLinkPreviews = it
                    sharedPrefs.edit().putBoolean("display_link_previews", it).apply()
                },
                themeSelection = themeSelection,
                onThemeClick = { }
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            NotificationSettingsSection(
                disableAllNotifications = disableAllNotifications,
                onDisableAllNotificationsChange = { disabled ->
                    disableAllNotifications = disabled
                    sharedPrefs.edit().putBoolean("disable_all_notifications", disabled).apply()
                    syncAlarmsWithSettings(disabled, disableMorning, disableAfternoon, disableNight)
                },

                disableMorning = disableMorning,
                onDisableMorningChange = { disabled ->
                    disableMorning = disabled
                    sharedPrefs.edit().putBoolean("disable_morning", disabled).apply()
                    syncAlarmsWithSettings(disableAllNotifications, disabled, disableAfternoon, disableNight)
                },
                morningTime = morningTime,
                onMorningTimeChange = { time ->
                    morningTime = time
                    sharedPrefs.edit().putString("morning_time", time).apply()
                },

                disableAfternoon = disableAfternoon,
                onDisableAfternoonChange = { disabled ->
                    disableAfternoon = disabled
                    sharedPrefs.edit().putBoolean("disable_afternoon", disabled).apply()
                    syncAlarmsWithSettings(disableAllNotifications, disableMorning, disabled, disableNight)
                },
                afternoonTime = afternoonTime,
                onAfternoonTimeChange = { time ->
                    afternoonTime = time
                    sharedPrefs.edit().putString("afternoon_time", time).apply()
                },

                disableNight = disableNight,
                onDisableNightChange = { disabled ->
                    disableNight = disabled
                    sharedPrefs.edit().putBoolean("disable_night", disabled).apply()
                    syncAlarmsWithSettings(disableAllNotifications, disableMorning, disableAfternoon, disabled)
                },
                nightTime = nightTime,
                onNightTimeChange = { time ->
                    nightTime = time
                    sharedPrefs.edit().putString("night_time", time).apply()
                },

                activeRemindersCount = reminderNotes.size,
                upcomingRemindersSummary = upcomingSummary,
                reminderNotes = reminderNotes
            )

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

            SharingSettingsSection(
                enableSharing = enableSharing,
                onEnableSharingChange = {
                    enableSharing = it
                    sharedPrefs.edit().putBoolean("enable_sharing", it).apply()
                }
            )
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface SettingsEntryPoint {
    fun reminderScheduler(): ReminderScheduler
}
