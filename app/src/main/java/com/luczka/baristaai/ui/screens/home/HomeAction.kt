package com.luczka.baristaai.ui.screens.home

sealed interface HomeAction {
    data object LoadRecipes : HomeAction
    data object LoadMore : HomeAction
    data class SelectFilter(val filterId: String?) : HomeAction
    data object OpenProfile : HomeAction
    data object DismissProfile : HomeAction
    data object OpenAddOptions : HomeAction
    data object DismissAddOptions : HomeAction
    data object OpenGenerate : HomeAction
    data object OpenManual : HomeAction
    data class OpenRecipeDetail(val recipeId: String) : HomeAction
}
