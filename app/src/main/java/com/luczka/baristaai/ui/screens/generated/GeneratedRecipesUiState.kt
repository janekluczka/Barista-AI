package com.luczka.baristaai.ui.screens.generated

data class GeneratedRecipesUiState(
    val requestId: String? = null,
    val isLoading: Boolean = false,
    val recipes: List<GeneratedRecipeCardUiState> = emptyList(),
    val errorMessage: String? = null,
    val isAbortDialogVisible: Boolean = false,
    val isSubmitting: Boolean = false
)
