package com.luczka.baristaai.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object GenerateRecipe : Route

    @Serializable
    data class GeneratedRecipes(
        val requestId: String
    ) : Route

    @Serializable
    data class EditRecipe(
        val mode: EditRecipeMode,
        val recipeId: String? = null,
        val requestId: String? = null
    ) : Route

    @Serializable
    data class RecipeDetail(
        val recipeId: String
    ) : Route

    @Serializable
    data object Profile : Route
}

@Serializable
sealed interface Graph {

    @Serializable
    data object Auth : Graph

    @Serializable
    data object App : Graph
}

@Serializable
enum class EditRecipeMode {
    DRAFT,
    SAVED,
    MANUAL
}
