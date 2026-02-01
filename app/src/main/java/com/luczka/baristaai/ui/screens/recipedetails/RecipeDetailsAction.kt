package com.luczka.baristaai.ui.screens.recipedetails

sealed interface RecipeDetailsAction {
    data object Load : RecipeDetailsAction
    data object Retry : RecipeDetailsAction
    data object NavigateBack : RecipeDetailsAction
    data object Edit : RecipeDetailsAction
    data object DeleteClick : RecipeDetailsAction
    data object ConfirmDelete : RecipeDetailsAction
    data object DismissDelete : RecipeDetailsAction
}
