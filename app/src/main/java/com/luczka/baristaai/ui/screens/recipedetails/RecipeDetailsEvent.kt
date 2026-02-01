package com.luczka.baristaai.ui.screens.recipedetails

sealed interface RecipeDetailsEvent {
    data object NavigateBack : RecipeDetailsEvent
    data object NavigateToHome : RecipeDetailsEvent
    data class NavigateToEdit(val recipeId: String) : RecipeDetailsEvent
    data class ShowMessage(val message: String) : RecipeDetailsEvent
    data class ShowError(val message: String) : RecipeDetailsEvent
}
