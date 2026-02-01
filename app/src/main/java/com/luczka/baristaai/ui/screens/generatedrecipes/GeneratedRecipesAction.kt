package com.luczka.baristaai.ui.screens.generatedrecipes

sealed interface GeneratedRecipesAction {
    data class EditRecipe(val recipeId: String) : GeneratedRecipesAction
    data class ToggleSelection(
        val recipeId: String,
        val selection: RecipeSelection
    ) : GeneratedRecipesAction
    data object OpenEditBrewMethodSheet : GeneratedRecipesAction
    data object DismissEditBrewMethodSheet : GeneratedRecipesAction
    data object DismissEditSheet : GeneratedRecipesAction
    data class SelectEditBrewMethod(val brewMethodId: String) : GeneratedRecipesAction
    data class UpdateEditCoffeeAmount(val value: String) : GeneratedRecipesAction
    data class UpdateEditRatioCoffee(val value: String) : GeneratedRecipesAction
    data class UpdateEditRatioWater(val value: String) : GeneratedRecipesAction
    data class UpdateEditTemperature(val value: String) : GeneratedRecipesAction
    data class UpdateEditAssistantTip(val value: String) : GeneratedRecipesAction
    data object SubmitEdit : GeneratedRecipesAction
    data object ConfirmSelections : GeneratedRecipesAction
    data object ShowAbortDialog : GeneratedRecipesAction
    data object DismissAbortDialog : GeneratedRecipesAction
    data object ConfirmAbort : GeneratedRecipesAction
    data object Retry : GeneratedRecipesAction
    data object NavigateBack : GeneratedRecipesAction
}
