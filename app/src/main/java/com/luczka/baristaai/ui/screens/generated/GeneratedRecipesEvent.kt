package com.luczka.baristaai.ui.screens.generated

sealed interface GeneratedRecipesEvent {
    data object NavigateBack : GeneratedRecipesEvent
    data class NavigateToEditRecipe(val recipeId: String) : GeneratedRecipesEvent
    data class ShowMessage(val message: String) : GeneratedRecipesEvent
}
