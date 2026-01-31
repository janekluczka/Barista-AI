package com.luczka.baristaai.ui.screens.editgeneratedrecipe

sealed interface EditGeneratedRecipeAction {
    data object NavigateBack : EditGeneratedRecipeAction

    data object OpenBrewMethodSheet : EditGeneratedRecipeAction
    data object DismissBrewMethodSheet : EditGeneratedRecipeAction
    data class SelectBrewMethod(val brewMethodId: String) : EditGeneratedRecipeAction

    data class UpdateCoffeeAmount(val value: String) : EditGeneratedRecipeAction
    data class UpdateWaterAmount(val value: String) : EditGeneratedRecipeAction
    data class UpdateRatioCoffee(val value: String) : EditGeneratedRecipeAction
    data class UpdateRatioWater(val value: String) : EditGeneratedRecipeAction
    data class UpdateTemperature(val value: String) : EditGeneratedRecipeAction
    data class UpdateAssistantTip(val value: String) : EditGeneratedRecipeAction

    data object Submit : EditGeneratedRecipeAction
}
