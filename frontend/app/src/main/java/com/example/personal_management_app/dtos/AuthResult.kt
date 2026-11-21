package com.example.personal_management_app.dtos

import com.example.personal_management_app.entities.ProfileEntity

sealed interface AuthResult {
    data class Success(
        val user: ProfileEntity,
        val token: String
    ) : AuthResult

    data object WrongCredentials : AuthResult
    data object EmailTaken : AuthResult
}
