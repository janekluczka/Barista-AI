package com.luczka.baristaai.ui.screens.editgeneratedrecipe

sealed interface EditGeneratedRecipeEvent {
    data object NavigateBack : EditGeneratedRecipeEvent
    data object NavigateBackWithRefresh : EditGeneratedRecipeEvent
    data class ShowMessage(val message: String) : EditGeneratedRecipeEvent
    data class ShowError(val message: String) : EditGeneratedRecipeEvent
}
