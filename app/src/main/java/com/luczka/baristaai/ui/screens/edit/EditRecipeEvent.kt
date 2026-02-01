package com.luczka.baristaai.ui.screens.edit

sealed interface EditRecipeEvent {
    data object NavigateBack : EditRecipeEvent
    data object NavigateToHome : EditRecipeEvent
    data class ShowMessage(val message: String) : EditRecipeEvent
    data class ShowError(val message: String, val retryAction: EditRecipeAction? = null) : EditRecipeEvent
}
