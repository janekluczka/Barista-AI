package com.luczka.baristaai.ui.screens.generatedrecipes

data class GeneratedRecipeCardUiState(
    val id: String,
    val title: String,
    val coffeeAmount: String,
    val waterAmount: String,
    val ratio: String,
    val temperature: String,
    val assistantTip: String? = null,
    val selection: RecipeSelection = RecipeSelection.None
)

enum class RecipeSelection {
    None,
    Accept,
    Reject
}
