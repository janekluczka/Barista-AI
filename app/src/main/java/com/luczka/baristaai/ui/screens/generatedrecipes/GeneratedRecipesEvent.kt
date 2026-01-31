package com.luczka.baristaai.ui.screens.generatedrecipes

sealed interface GeneratedRecipesEvent {
    data object NavigateBack : GeneratedRecipesEvent
    data object NavigateToSuccess : GeneratedRecipesEvent
    data class NavigateToEditGeneratedRecipe(val recipeId: String) : GeneratedRecipesEvent
    data class ShowMessage(val message: String) : GeneratedRecipesEvent
}
