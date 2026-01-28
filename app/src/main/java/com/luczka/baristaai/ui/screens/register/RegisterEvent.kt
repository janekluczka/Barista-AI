package com.luczka.baristaai.ui.screens.register

sealed interface RegisterEvent {
    data object NavigateToLogin : RegisterEvent
    data object NavigateToHome : RegisterEvent
    data class ShowError(val message: String) : RegisterEvent
}
