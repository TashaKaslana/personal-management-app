package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.entities.DayPeriod
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.entities.SettingEntity
import com.example.personal_management_app.repositories.NoteRepository
import com.example.personal_management_app.repositories.SettingRepository
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.screen.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class SettingsUiState(
    val settings: SettingEntity = SettingEntity(),
    val reminderNotes: List<NoteEntity> = emptyList(),
    val nextUpcomingReminder: NoteEntity? = null,
    val upcomingSummary: String = "Không có lời nhắc nào sắp tới"
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val noteRepository: NoteRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = noteRepository.getList()
        }
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        settingRepository.settings,
        _notes
    ) { settings, notes ->
        val now = System.currentTimeMillis()
        val reminderList = notes
            .filter { it.reminder != null && it.deletedAt == null && it.archivedAt == null }
            .sortedBy { it.reminder?.atMillis ?: 0L }

        val upcoming = reminderList
            .filter { (it.reminder?.atMillis ?: 0L) >= now }
            .minByOrNull { it.reminder?.atMillis ?: Long.MAX_VALUE }

        val summary = upcoming?.let { note ->
            val reminderLabel = note.reminder?.label() ?: ""
            "${note.title.ifBlank { "Không có tiêu đề" }} ($reminderLabel)"
        } ?: "Không có lời nhắc nào sắp tới"

        SettingsUiState(
            settings = settings,
            reminderNotes = reminderList,
            nextUpcomingReminder = upcoming,
            upcomingSummary = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(settings = settingRepository.settings.value)
    )

    fun updateSettings(transform: (SettingEntity) -> SettingEntity) {
        viewModelScope.launch {
            settingRepository.updateSettings(transform)
            syncAlarms()
        }
    }

    private fun syncAlarms() {
        val currentSettings = settingRepository.settings.value
        val reminderList = uiState.value.reminderNotes
        reminderList.forEach { note ->
            val noteReminder = note.reminder ?: return@forEach
            val cal = Calendar.getInstance().apply { timeInMillis = noteReminder.atMillis }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val period = DayPeriod.fromHour(hour)

            val isMuted = currentSettings.disableAllNotifications || when (period) {
                DayPeriod.MORNING -> currentSettings.disableMorning
                DayPeriod.AFTERNOON -> currentSettings.disableAfternoon
                DayPeriod.NIGHT -> currentSettings.disableNight
            }

            if (isMuted) {
                reminderScheduler.cancel(note.id)
            } else {
                reminderScheduler.apply(note)
            }
        }
    }
}

