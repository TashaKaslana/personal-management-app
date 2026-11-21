package com.example.personal_management_app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.example.personal_management_app.utils.exportDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PersonalManagementApp : Application() {
    override fun onCreate() {
        super.onCreate()
        exportDatabase(this)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let { exportDatabase(it) }
            }
        }
        val filter = IntentFilter("com.example.personal_management_app.EXPORT_DB")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
    }
}
