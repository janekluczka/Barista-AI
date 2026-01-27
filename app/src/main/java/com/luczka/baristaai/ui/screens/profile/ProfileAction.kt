package com.luczka.baristaai.ui.screens.profile

sealed interface ProfileAction {
    data object LoadProfile : ProfileAction
    data object ConfirmLogout : ProfileAction
}
