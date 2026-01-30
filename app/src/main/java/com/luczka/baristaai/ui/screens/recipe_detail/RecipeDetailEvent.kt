package com.luczka.baristaai.ui.screens.recipe_detail

sealed interface RecipeDetailEvent {
    data object NavigateBack : RecipeDetailEvent
    data object NavigateToHome : RecipeDetailEvent
    data class NavigateToEdit(val recipeId: String) : RecipeDetailEvent
    data class ShowMessage(val message: String) : RecipeDetailEvent
    data class ShowError(val message: String) : RecipeDetailEvent
}
