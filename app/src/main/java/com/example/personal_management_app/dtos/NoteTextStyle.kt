package com.example.personal_management_app.dtos

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

data class NoteTextStyle(
    val fontSize: Float = 16f,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false
)

fun NoteTextStyle.toCompose(): TextStyle =
    TextStyle(
        fontSize = fontSize.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        textDecoration =
            if (underline) TextDecoration.Underline
            else TextDecoration.None
    )
