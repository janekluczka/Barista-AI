package com.luczka.baristaai.ui.screens.auth

sealed interface AuthLandingEvent {
    data object NavigateToLogin : AuthLandingEvent
    data object NavigateToRegister : AuthLandingEvent
}
