package com.example.personal_management_app.entites

import androidx.compose.ui.graphics.Color

data class NoteEntity(
    var id: String,
    var title: String,
    var content: String,
    var tag: String? = null,
    var backgroundColor: Color
)
