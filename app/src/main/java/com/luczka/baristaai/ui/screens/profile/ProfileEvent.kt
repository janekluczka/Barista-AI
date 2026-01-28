package com.luczka.baristaai.ui.screens.profile

sealed interface ProfileEvent {
    data object NavigateBack : ProfileEvent
    data object NavigateToLogin : ProfileEvent
    data class ShowError(val message: String) : ProfileEvent
}
