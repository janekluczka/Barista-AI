package com.luczka.baristaai.ui.screens.editgeneratedrecipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.usecase.GetRecipeUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditGeneratedRecipeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<EditGeneratedRecipeUiState> =
        MutableStateFlow(EditGeneratedRecipeUiState())
    val uiState: StateFlow<EditGeneratedRecipeUiState> = _uiState

    private val _event: MutableSharedFlow<EditGeneratedRecipeEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<EditGeneratedRecipeEvent> = _event.asSharedFlow()

    init {
        val recipeId = savedStateHandle.get<String>("recipeId")
        updateState { it.copy(recipeId = recipeId) }
        if (recipeId.isNullOrBlank()) {
            sendEvent(EditGeneratedRecipeEvent.ShowError("Missing recipe id."))
            sendEvent(EditGeneratedRecipeEvent.NavigateBack)
        } else {
            loadInitialData(recipeId)
        }
    }

    fun handleAction(action: EditGeneratedRecipeAction) {
        when (action) {
            EditGeneratedRecipeAction.NavigateBack -> navigateBack()
            EditGeneratedRecipeAction.OpenBrewMethodSheet -> openBrewMethodSheet()
            EditGeneratedRecipeAction.DismissBrewMethodSheet -> dismissBrewMethodSheet()
            is EditGeneratedRecipeAction.SelectBrewMethod -> selectBrewMethod(action.brewMethodId)
            is EditGeneratedRecipeAction.UpdateCoffeeAmount -> updateCoffeeAmount(action.value)
            is EditGeneratedRecipeAction.UpdateWaterAmount -> updateWaterAmount(action.value)
            is EditGeneratedRecipeAction.UpdateRatioCoffee -> updateRatioCoffee(action.value)
            is EditGeneratedRecipeAction.UpdateRatioWater -> updateRatioWater(action.value)
            is EditGeneratedRecipeAction.UpdateTemperature -> updateTemperature(action.value)
            is EditGeneratedRecipeAction.UpdateAssistantTip -> updateAssistantTip(action.value)
            EditGeneratedRecipeAction.Submit -> submit()
        }
    }

    private fun navigateBack() {
        sendEvent(EditGeneratedRecipeEvent.NavigateBack)
    }

    private fun openBrewMethodSheet() {
        updateState { it.copy(isBrewMethodSheetVisible = true) }
    }

    private fun dismissBrewMethodSheet() {
        updateState { it.copy(isBrewMethodSheetVisible = false) }
    }

    private fun selectBrewMethod(brewMethodId: String) {
        updateState { state ->
            val methodName = state.brewMethods.firstOrNull { it.id == brewMethodId }?.name
            state.copy(
                selectedBrewMethodId = brewMethodId,
                selectedBrewMethodName = methodName,
                isBrewMethodSheetVisible = false
            )
        }
        updateSubmitEnabled()
    }

    private fun updateCoffeeAmount(value: String) {
        updateState { it.copy(coffeeAmountInput = value) }
        recalculateWaterAmount()
        updateSubmitEnabled()
    }

    private fun updateWaterAmount(value: String) {
        updateText(
            value = value,
            update = { state, input -> state.copy(waterAmountInput = input) }
        )
    }

    private fun updateRatioCoffee(value: String) {
        updateState { it.copy(ratioCoffeeInput = value) }
        recalculateWaterAmount()
        updateSubmitEnabled()
    }

    private fun updateRatioWater(value: String) {
        updateState { it.copy(ratioWaterInput = value) }
        recalculateWaterAmount()
        updateSubmitEnabled()
    }

    private fun updateTemperature(value: String) {
        updateText(
            value = value,
            update = { state, input -> state.copy(temperatureInput = input) }
        )
    }

    private fun updateAssistantTip(value: String) {
        updateState { it.copy(assistantTipInput = value) }
    }

    private fun recalculateWaterAmount() {
        val state = _uiState.value
        val coffeeAmount = state.coffeeAmountInput.toDoubleOrNull()
        val ratioCoffee = state.ratioCoffeeInput.toIntOrNull()
        val ratioWater = state.ratioWaterInput.toIntOrNull()
        if (coffeeAmount == null || ratioCoffee == null || ratioWater == null) {
            updateState { it.copy(waterAmountInput = "") }
            return
        }
        if (coffeeAmount < 0 || ratioCoffee < 1 || ratioWater < 1) {
            updateState { it.copy(waterAmountInput = "") }
            return
        }
        val waterAmount = coffeeAmount * ratioWater / ratioCoffee
        val formatted = String.format(Locale.US, "%.1f", waterAmount)
        updateState { it.copy(waterAmountInput = formatted) }
    }

    private fun loadInitialData(recipeId: String) {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val brewMethodsResult = listBrewMethodsUseCase()
            if (brewMethodsResult is RepositoryResult.Failure) {
                showError("Failed to load brew methods.")
                return@launch
            }

            val brewMethods = (brewMethodsResult as RepositoryResult.Success).value
            updateState { state ->
                state.copy(
                    isLoading = false,
                    brewMethods = brewMethods
                )
            }

            val recipeResult = getRecipeUseCase(recipeId)
            if (recipeResult is RepositoryResult.Failure) {
                showError(resolveErrorMessage(recipeResult.error))
                return@launch
            }

            val recipe = (recipeResult as RepositoryResult.Success).value
            if (recipe.status != RecipeStatus.Draft) {
                updateState { it.copy(isLoading = false) }
                sendEvent(EditGeneratedRecipeEvent.ShowMessage("This draft can't be edited here."))
                sendEvent(EditGeneratedRecipeEvent.NavigateBack)
                return@launch
            }

            updateState { state ->
                applyRecipeToState(state, recipe, brewMethods)
            }
            recalculateWaterAmount()
            updateSubmitEnabled()
        }
    }

    private fun applyRecipeToState(
        state: EditGeneratedRecipeUiState,
        recipe: Recipe,
        brewMethods: List<BrewMethod>
    ): EditGeneratedRecipeUiState {
        val methodName = brewMethods.firstOrNull { it.id == recipe.brewMethodId }?.name
        val coffeeAmount = recipe.coffeeAmount.toString()
        val waterAmount = recipe.waterAmount.toString()
        val ratioCoffee = recipe.ratioCoffee.toString()
        val ratioWater = recipe.ratioWater.toString()
        val temperature = recipe.temperature.toString()
        val assistantTip = recipe.assistantTip.orEmpty()
        return state.copy(
            selectedBrewMethodId = recipe.brewMethodId,
            selectedBrewMethodName = methodName,
            coffeeAmountInput = coffeeAmount,
            waterAmountInput = waterAmount,
            ratioCoffeeInput = ratioCoffee,
            ratioWaterInput = ratioWater,
            temperatureInput = temperature,
            assistantTipInput = assistantTip,
            initialBrewMethodId = recipe.brewMethodId,
            initialCoffeeAmountInput = coffeeAmount,
            initialWaterAmountInput = waterAmount,
            initialRatioCoffeeInput = ratioCoffee,
            initialRatioWaterInput = ratioWater,
            initialTemperatureInput = temperature,
            initialAssistantTipInput = assistantTip
        )
    }

    private fun showError(message: String) {
        updateState { state ->
            state.copy(
                isLoading = false,
                errorMessage = message
            )
        }
        sendEvent(EditGeneratedRecipeEvent.ShowError(message))
    }

    private fun updateText(
        value: String,
        update: (EditGeneratedRecipeUiState, String) -> EditGeneratedRecipeUiState
    ) {
        updateState { state -> update(state, value) }
        updateSubmitEnabled()
    }

    private fun updateSubmitEnabled() {
        updateState { state ->
            val hasRequiredFields = state.selectedBrewMethodId != null &&
                state.coffeeAmountInput.isNotBlank() &&
                state.waterAmountInput.isNotBlank() &&
                state.ratioCoffeeInput.isNotBlank() &&
                state.ratioWaterInput.isNotBlank() &&
                state.temperatureInput.isNotBlank()
            state.copy(isSubmitEnabled = hasRequiredFields && !state.isLoading)
        }
    }

    private fun submit() {
        if (!_uiState.value.isSubmitEnabled) {
            sendEvent(EditGeneratedRecipeEvent.ShowError("Fill in all required fields."))
            return
        }

        val validation = validateInputs()
        if (!validation.isValid) {
            sendEvent(EditGeneratedRecipeEvent.ShowError("Fix the highlighted fields."))
            return
        }

        if (!hasChanges(_uiState.value)) {
            sendEvent(EditGeneratedRecipeEvent.ShowMessage("No changes to save."))
            sendEvent(EditGeneratedRecipeEvent.NavigateBack)
            return
        }

        updateState { it.copy(isLoading = true) }
        viewModelScope.launch {
            updateDraftRecipe(validation)
        }
    }

    private fun validateInputs(): ValidationResult {
        val state = _uiState.value
        val brewMethodError = if (state.selectedBrewMethodId == null) "Select a brew method." else null
        val coffeeAmount = state.coffeeAmountInput.toDoubleOrNull()
        val waterAmount = state.waterAmountInput.toDoubleOrNull()
        val ratioCoffee = state.ratioCoffeeInput.toIntOrNull()
        val ratioWater = state.ratioWaterInput.toIntOrNull()
        val temperature = state.temperatureInput.toIntOrNull()

        val coffeeAmountError = validatePositiveDecimal(state.coffeeAmountInput, coffeeAmount)
        val waterAmountError = validatePositiveDecimal(state.waterAmountInput, waterAmount)
        val ratioCoffeeError = validatePositiveInt(state.ratioCoffeeInput, ratioCoffee)
        val ratioWaterError = validatePositiveInt(state.ratioWaterInput, ratioWater)
        val temperatureError = validateTemperature(state.temperatureInput, temperature)

        updateState {
            it.copy(
                brewMethodError = brewMethodError,
                coffeeAmountError = coffeeAmountError,
                waterAmountError = waterAmountError,
                ratioCoffeeError = ratioCoffeeError,
                ratioWaterError = ratioWaterError,
                temperatureError = temperatureError,
                errorMessage = null
            )
        }

        val isValid = listOf(
            brewMethodError,
            coffeeAmountError,
            waterAmountError,
            ratioCoffeeError,
            ratioWaterError,
            temperatureError
        ).all { it == null }

        return ValidationResult(
            isValid = isValid,
            brewMethodId = state.selectedBrewMethodId,
            coffeeAmount = coffeeAmount,
            waterAmount = waterAmount,
            ratioCoffee = ratioCoffee,
            ratioWater = ratioWater,
            temperature = temperature,
            assistantTip = state.assistantTipInput.trim().takeIf { it.isNotBlank() }
        )
    }

    private fun validatePositiveDecimal(input: String, value: Double?): String? {
        if (input.isBlank()) {
            return "Required."
        }
        if (value == null) {
            return "Invalid number."
        }
        if (value < 0) {
            return "Must be 0 or greater."
        }
        return null
    }

    private fun validatePositiveInt(input: String, value: Int?): String? {
        if (input.isBlank()) {
            return "Required."
        }
        if (value == null) {
            return "Invalid number."
        }
        if (value < 1) {
            return "Must be 1 or greater."
        }
        return null
    }

    private fun validateTemperature(input: String, value: Int?): String? {
        if (input.isBlank()) {
            return "Required."
        }
        if (value == null) {
            return "Invalid number."
        }
        if (value !in 0..100) {
            return "Temperature must be 0-100."
        }
        return null
    }

    private fun hasChanges(state: EditGeneratedRecipeUiState): Boolean {
        if (state.initialBrewMethodId == null) {
            return true
        }
        return state.selectedBrewMethodId != state.initialBrewMethodId ||
            state.coffeeAmountInput != state.initialCoffeeAmountInput ||
            state.waterAmountInput != state.initialWaterAmountInput ||
            state.ratioCoffeeInput != state.initialRatioCoffeeInput ||
            state.ratioWaterInput != state.initialRatioWaterInput ||
            state.temperatureInput != state.initialTemperatureInput ||
            state.assistantTipInput.trim() != state.initialAssistantTipInput.orEmpty()
    }

    private suspend fun updateDraftRecipe(validation: ValidationResult) {
        val recipeId = _uiState.value.recipeId ?: return showError("Missing recipe id.")
        val input = UpdateRecipe(
            brewMethodId = validation.brewMethodId,
            coffeeAmount = validation.coffeeAmount,
            waterAmount = validation.waterAmount,
            ratioCoffee = validation.ratioCoffee,
            ratioWater = validation.ratioWater,
            temperature = validation.temperature,
            assistantTip = validation.assistantTip
        )
        handleRepositoryResult(updateRecipeUseCase(recipeId, input))
    }

    private fun handleRepositoryResult(result: RepositoryResult<Recipe>) {
        when (result) {
            is RepositoryResult.Success -> {
                updateState { it.copy(isLoading = false) }
                sendEvent(EditGeneratedRecipeEvent.ShowMessage("Draft updated."))
                sendEvent(EditGeneratedRecipeEvent.NavigateBackWithRefresh)
            }
            is RepositoryResult.Failure -> showError(resolveErrorMessage(result.error))
        }
    }

    private fun resolveErrorMessage(error: RepositoryError): String {
        return when (error) {
            is RepositoryError.Network -> "Check your connection and try again."
            is RepositoryError.NotFound -> "Recipe not found."
            is RepositoryError.Unauthorized -> "Please sign in again."
            is RepositoryError.Validation -> error.message
            is RepositoryError.Unknown -> error.message
        }
    }

    private fun updateState(reducer: (EditGeneratedRecipeUiState) -> EditGeneratedRecipeUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun sendEvent(event: EditGeneratedRecipeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private data class ValidationResult(
        val isValid: Boolean,
        val brewMethodId: String?,
        val coffeeAmount: Double?,
        val waterAmount: Double?,
        val ratioCoffee: Int?,
        val ratioWater: Int?,
        val temperature: Int?,
        val assistantTip: String?
    )
}
