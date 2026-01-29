package com.luczka.baristaai.ui.screens.edit

import com.luczka.baristaai.ui.navigation.EditRecipeMode

sealed interface EditRecipeAction {
    data object NavigateBack : EditRecipeAction

    data object OpenBrewMethodSheet : EditRecipeAction
    data object DismissBrewMethodSheet : EditRecipeAction
    data class SelectBrewMethod(val brewMethodId: String) : EditRecipeAction

    data class UpdateCoffeeAmount(val value: String) : EditRecipeAction
    data class UpdateWaterAmount(val value: String) : EditRecipeAction
    data class UpdateRatioCoffee(val value: String) : EditRecipeAction
    data class UpdateRatioWater(val value: String) : EditRecipeAction
    data class UpdateTemperature(val value: String) : EditRecipeAction
    data class UpdateAssistantTip(val value: String) : EditRecipeAction

    data object Submit : EditRecipeAction
}
