package com.luczka.baristaai.ui.screens.home

sealed interface HomeAction {
    data object LoadRecipes : HomeAction
    data class SelectFilter(val filterId: String?) : HomeAction
    data object OpenProfile : HomeAction
    data object OpenGenerate : HomeAction
    data class OpenRecipeDetail(val recipeId: String) : HomeAction
}
