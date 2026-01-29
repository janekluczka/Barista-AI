package com.luczka.baristaai.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false
)
