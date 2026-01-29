package com.luczka.baristaai.ui.screens.generated

sealed interface GeneratedRecipesAction {
    data class Initialize(val requestId: String) : GeneratedRecipesAction
    data class AcceptRecipe(val recipeId: String) : GeneratedRecipesAction
    data class RejectRecipe(val recipeId: String) : GeneratedRecipesAction
    data class EditRecipe(val recipeId: String) : GeneratedRecipesAction
    data object NavigateBack : GeneratedRecipesAction
}
