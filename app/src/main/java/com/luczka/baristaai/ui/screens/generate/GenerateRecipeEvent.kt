package com.luczka.baristaai.ui.screens.generate

sealed interface GenerateRecipeEvent {
    data class NavigateToGeneratedRecipes(val requestId: String) : GenerateRecipeEvent
    data object NavigateBack : GenerateRecipeEvent
    data class ShowError(val message: String) : GenerateRecipeEvent
}
