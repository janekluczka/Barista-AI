package com.luczka.baristaai.ui.screens.login

sealed interface LoginEvent {
    data object NavigateToRegister : LoginEvent
    data object NavigateToHome : LoginEvent
    data object RequestGoogleSignIn : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}
