package com.example.personal_management_app.ui.components.utils

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShowToastMessage(
    message: String?,
    duration: Int = Toast.LENGTH_SHORT
) {
    val context = LocalContext.current

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, duration).show()
        }
    }
}