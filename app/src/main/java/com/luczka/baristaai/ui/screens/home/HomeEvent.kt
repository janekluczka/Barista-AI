package com.luczka.baristaai.ui.screens.home

sealed interface HomeEvent {
    data object NavigateToGenerate : HomeEvent
    data object NavigateToManual : HomeEvent
    data class NavigateToRecipeDetails(val recipeId: String) : HomeEvent
    data object NavigateToLogin : HomeEvent
    data class ShowError(val message: String, val retryAction: HomeAction? = null) : HomeEvent
}
