package com.luczka.baristaai.ui.screens.recipe_detail

import com.luczka.baristaai.domain.model.Recipe

data class RecipeDetailUiState(
    val recipeId: String? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val recipe: Recipe? = null,
    val brewMethodName: String? = null,
    val errorMessage: String? = null,
    val isDeleteDialogVisible: Boolean = false
)
