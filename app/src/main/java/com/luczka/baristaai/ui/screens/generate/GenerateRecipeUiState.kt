package com.luczka.baristaai.ui.screens.generate

import com.luczka.baristaai.domain.model.BrewMethod

data class GenerateRecipeUiState(
    val isLoading: Boolean = false,
    val brewMethods: List<BrewMethod> = emptyList(),
    val selectedBrewMethodId: String? = null,
    val selectedBrewMethodName: String? = null,
    val isBrewMethodSheetVisible: Boolean = false,
    val coffeeAmountInput: String = "",
    val canAdjustTemperature: Boolean = false,
    val userComment: String = "",
    val brewMethodError: String? = null,
    val coffeeAmountError: String? = null,
    val errorMessage: String? = null,
    val isSubmitEnabled: Boolean = false
)
