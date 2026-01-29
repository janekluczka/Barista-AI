package com.luczka.baristaai.ui.screens.home

sealed interface HomeEvent {
    data object NavigateToProfile : HomeEvent
    data object NavigateToGenerate : HomeEvent
    data object NavigateToManual : HomeEvent
    data class NavigateToRecipeDetail(val recipeId: String) : HomeEvent
    data class ShowError(val message: String) : HomeEvent
}
