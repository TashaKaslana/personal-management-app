package com.example.personal_management_app.utils

import androidx.compose.ui.graphics.Color

fun String.toComposeColor(): Color {
    return try {
        Color(removePrefix("0x").toLong(16).toInt())
    } catch (_: NumberFormatException) {
        Color.White
    }
}