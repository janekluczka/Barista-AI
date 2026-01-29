package com.luczka.baristaai.ui.screens.generate

sealed interface GenerateRecipeAction {
    data object LoadData : GenerateRecipeAction
    data object OpenBrewMethodSheet : GenerateRecipeAction
    data object DismissBrewMethodSheet : GenerateRecipeAction
    data class SelectBrewMethod(val brewMethodId: String) : GenerateRecipeAction
    data class UpdateCoffeeAmount(val coffeeAmount: String) : GenerateRecipeAction
    data class UpdateCanAdjustTemperature(val canAdjustTemperature: Boolean) : GenerateRecipeAction
    data class UpdateUserComment(val userComment: String) : GenerateRecipeAction
    data object SubmitRequest : GenerateRecipeAction
    data object NavigateBack : GenerateRecipeAction
}
