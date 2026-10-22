package com.example.personal_management_app.entities

enum class DayPeriod(val title: String, val hourRangeText: String) {
    MORNING("Buổi sáng", "05:00 - 12:59"),
    AFTERNOON("Buổi chiều", "13:00 - 17:59"),
    NIGHT("Buổi tối & Đêm", "18:00 - 04:59");

    companion object {
        fun fromHour(hour: Int): DayPeriod {
            return when (hour) {
                in 5..12 -> MORNING
                in 13..17 -> AFTERNOON
                else -> NIGHT
            }
        }
    }
}

