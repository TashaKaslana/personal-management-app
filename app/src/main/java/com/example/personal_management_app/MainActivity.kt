package com.example.personal_management_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.personal_management_app.ui.theme.PersonalManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalManagementApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PersonalManagementAppTheme {
        PersonalManagementApp()
    }
}