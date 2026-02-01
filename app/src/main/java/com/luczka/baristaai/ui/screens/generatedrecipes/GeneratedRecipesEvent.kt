package com.luczka.baristaai.ui.screens.generatedrecipes

sealed interface GeneratedRecipesEvent {
    data object NavigateBack : GeneratedRecipesEvent
    data object NavigateToSuccess : GeneratedRecipesEvent
    data class ShowMessage(val message: String) : GeneratedRecipesEvent
    data class ShowError(val message: String, val retryAction: GeneratedRecipesAction? = null) : GeneratedRecipesEvent
}
