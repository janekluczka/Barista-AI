package com.luczka.baristaai.ui.screens.generatedrecipes

import com.luczka.baristaai.domain.model.BrewMethod

data class GeneratedRecipesUiState(
    val requestId: String? = null,
    val isLoading: Boolean = false,
    val recipes: List<GeneratedRecipeCardUiState> = emptyList(),
    val errorMessage: String? = null,
    val isAbortDialogVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val brewMethods: List<BrewMethod> = emptyList(),
    val isEditSheetVisible: Boolean = false,
    val isEditLoading: Boolean = false,
    val editRecipeId: String? = null,
    val isEditBrewMethodSheetVisible: Boolean = false,
    val selectedBrewMethodId: String? = null,
    val selectedBrewMethodName: String? = null,
    val coffeeAmountInput: String = "",
    val waterAmountInput: String = "",
    val ratioCoffeeInput: String = "",
    val ratioWaterInput: String = "",
    val temperatureInput: String = "",
    val assistantTipInput: String = "",
    val initialBrewMethodId: String? = null,
    val initialCoffeeAmountInput: String? = null,
    val initialWaterAmountInput: String? = null,
    val initialRatioCoffeeInput: String? = null,
    val initialRatioWaterInput: String? = null,
    val initialTemperatureInput: String? = null,
    val initialAssistantTipInput: String? = null,
    val brewMethodError: String? = null,
    val coffeeAmountError: String? = null,
    val waterAmountError: String? = null,
    val ratioCoffeeError: String? = null,
    val ratioWaterError: String? = null,
    val temperatureError: String? = null,
    val isEditSubmitEnabled: Boolean = false
)
