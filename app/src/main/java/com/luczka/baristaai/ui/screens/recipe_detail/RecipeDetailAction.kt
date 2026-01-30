package com.luczka.baristaai.ui.screens.recipe_detail

sealed interface RecipeDetailAction {
    data object Load : RecipeDetailAction
    data object Retry : RecipeDetailAction
    data object NavigateBack : RecipeDetailAction
    data object Edit : RecipeDetailAction
    data object DeleteClick : RecipeDetailAction
    data object ConfirmDelete : RecipeDetailAction
    data object DismissDelete : RecipeDetailAction
}
