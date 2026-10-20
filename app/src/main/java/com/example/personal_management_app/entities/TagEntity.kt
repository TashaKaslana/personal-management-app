package com.example.personal_management_app.entities

data class TagEntity(
    val id: String,
    val name: String,
    val color: String
)

val tagColors = listOf(
    "0xFFE57373",
    "0xFFF06292",
    "0xFFBA68C8",
    "0xFF9575CD",
    "0xFF64B5F6",
    "0xFF4FC3F7",
    "0xFF4DB6AC",
    "0xFF81C784",
    "0xFFAED581",
    "0xFFFFD54F",
    "0xFFFFB74D",
    "0xFFA1887F"
)
