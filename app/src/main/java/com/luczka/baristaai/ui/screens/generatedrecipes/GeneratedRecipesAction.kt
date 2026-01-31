package com.luczka.baristaai.ui.screens.generatedrecipes

sealed interface GeneratedRecipesAction {
    data class EditRecipe(val recipeId: String) : GeneratedRecipesAction
    data class ToggleSelection(
        val recipeId: String,
        val selection: RecipeSelection
    ) : GeneratedRecipesAction
    data object ConfirmSelections : GeneratedRecipesAction
    data object ShowAbortDialog : GeneratedRecipesAction
    data object DismissAbortDialog : GeneratedRecipesAction
    data object ConfirmAbort : GeneratedRecipesAction
    data object NavigateBack : GeneratedRecipesAction
}
