package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.repositories.NoteRepository
import com.example.personal_management_app.ui.screen.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    fun syncAlarms() {
        scheduler.sync(repository.getList())
    }
}
