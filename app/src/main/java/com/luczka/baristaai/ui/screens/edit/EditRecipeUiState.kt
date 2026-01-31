package com.luczka.baristaai.ui.screens.edit

import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.ui.navigation.EditRecipeMode

data class EditRecipeUiState(
    val mode: EditRecipeMode = EditRecipeMode.MANUAL,
    val recipeId: String? = null,
    val requestId: String? = null,
    val isLoading: Boolean = false,
    val brewMethods: List<BrewMethod> = emptyList(),
    val selectedBrewMethodId: String? = null,
    val selectedBrewMethodName: String? = null,
    val isBrewMethodSheetVisible: Boolean = false,
    val coffeeAmountInput: String = "",
    val waterAmountInput: String = "",
    val ratioCoffeeInput: String = "",
    val ratioWaterInput: String = "",
    val temperatureInput: String = "100",
    val canRegulateTemperature: Boolean = false,
    val assistantTipInput: String = "",
    val initialBrewMethodId: String? = null,
    val initialCoffeeAmountInput: String? = null,
    val initialWaterAmountInput: String? = null,
    val initialRatioCoffeeInput: String? = null,
    val initialRatioWaterInput: String? = null,
    val initialTemperatureInput: String? = null,
    val initialCanRegulateTemperature: Boolean = false,
    val initialAssistantTipInput: String? = null,
    val brewMethodError: String? = null,
    val coffeeAmountError: String? = null,
    val waterAmountError: String? = null,
    val ratioCoffeeError: String? = null,
    val ratioWaterError: String? = null,
    val temperatureError: String? = null,
    val errorMessage: String? = null,
    val isSubmitEnabled: Boolean = false
)
