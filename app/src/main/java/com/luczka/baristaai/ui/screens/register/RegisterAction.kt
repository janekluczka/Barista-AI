package com.luczka.baristaai.ui.screens.register

sealed interface RegisterAction {
    data class UpdateEmail(val email: String) : RegisterAction
    data class UpdatePassword(val password: String) : RegisterAction
    data object SubmitRegister : RegisterAction
    data object NavigateToLogin : RegisterAction
}
