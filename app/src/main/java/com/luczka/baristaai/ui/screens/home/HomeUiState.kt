package com.luczka.baristaai.ui.screens.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val filters: List<FilterUiState> = emptyList(),
    val selectedFilterId: String? = null,
    val recipes: List<RecipeUiState> = emptyList(),
    val errorMessage: String? = null,
    val isAddOptionSheetVisible: Boolean = false,
    val isProfileSheetVisible: Boolean = false
)
