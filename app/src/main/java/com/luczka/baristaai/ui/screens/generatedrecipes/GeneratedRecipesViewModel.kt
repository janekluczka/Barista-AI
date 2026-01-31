package com.luczka.baristaai.ui.screens.generatedrecipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeActionModel
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.usecase.CreateRecipeActionLogUseCase
import com.luczka.baristaai.domain.usecase.GetRecipeUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.ListRecipesUseCase
import com.luczka.baristaai.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GeneratedRecipesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val listRecipesUseCase: ListRecipesUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val createRecipeActionLogUseCase: CreateRecipeActionLogUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<GeneratedRecipesUiState> =
        MutableStateFlow(GeneratedRecipesUiState())
    val uiState: StateFlow<GeneratedRecipesUiState> = _uiState

    private val _event: MutableSharedFlow<GeneratedRecipesEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<GeneratedRecipesEvent> = _event.asSharedFlow()

    init {
        val requestId = savedStateHandle.get<String>("requestId")
        updateState { it.copy(requestId = requestId) }
        if (requestId.isNullOrBlank()) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Request not found."))
            sendEvent(GeneratedRecipesEvent.NavigateBack)
        } else {
            initialize(requestId)
        }
    }

    fun handleAction(action: GeneratedRecipesAction) {
        when (action) {
            is GeneratedRecipesAction.EditRecipe -> openEditSheet(action.recipeId)
            is GeneratedRecipesAction.ToggleSelection -> toggleSelection(action.recipeId, action.selection)
            GeneratedRecipesAction.OpenEditBrewMethodSheet -> openEditBrewMethodSheet()
            GeneratedRecipesAction.DismissEditBrewMethodSheet -> dismissEditBrewMethodSheet()
            GeneratedRecipesAction.DismissEditSheet -> dismissEditSheet()
            is GeneratedRecipesAction.SelectEditBrewMethod -> selectEditBrewMethod(action.brewMethodId)
            is GeneratedRecipesAction.UpdateEditCoffeeAmount -> updateEditCoffeeAmount(action.value)
            is GeneratedRecipesAction.UpdateEditRatioCoffee -> updateEditRatioCoffee(action.value)
            is GeneratedRecipesAction.UpdateEditRatioWater -> updateEditRatioWater(action.value)
            is GeneratedRecipesAction.UpdateEditTemperature -> updateEditTemperature(action.value)
            is GeneratedRecipesAction.UpdateEditAssistantTip -> updateEditAssistantTip(action.value)
            GeneratedRecipesAction.SubmitEdit -> submitEdit()
            GeneratedRecipesAction.ConfirmSelections -> confirmSelections()
            GeneratedRecipesAction.ShowAbortDialog -> showAbortDialog()
            GeneratedRecipesAction.DismissAbortDialog -> dismissAbortDialog()
            GeneratedRecipesAction.ConfirmAbort -> confirmAbort()
            GeneratedRecipesAction.NavigateBack -> sendEvent(GeneratedRecipesEvent.NavigateBack)
        }
    }

    private fun initialize(requestId: String) {
        updateState { state ->
            state.copy(
                requestId = requestId,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val brewMethodsResult = listBrewMethodsUseCase()
            if (brewMethodsResult is RepositoryResult.Failure) {
                showError(resolveErrorMessage(brewMethodsResult.error))
                return@launch
            }

            val filter = RecipeFilter(
                generationRequestId = requestId,
                status = RecipeStatus.Draft
            )
            val page = PageRequest(limit = DRAFT_PAGE_SIZE, offset = 0)
            val sort = SortOption(field = "created_at", direction = SortDirection.DESC)
            val recipesResult = listRecipesUseCase(filter, page, sort)
            if (recipesResult is RepositoryResult.Failure) {
                showError(resolveErrorMessage(recipesResult.error))
                return@launch
            }

            val brewMethods = (brewMethodsResult as RepositoryResult.Success).value
            val selectionById = _uiState.value.recipes.associateBy({ it.id }, { it.selection })
            val recipes = (recipesResult as RepositoryResult.Success).value
                .distinctBy { it.id }
                .mapIndexed { index, recipe ->
                    val methodName = brewMethods.firstOrNull { it.id == recipe.brewMethodId }?.name
                    GeneratedRecipeCardUiState(
                        id = recipe.id,
                        title = methodName ?: "Recipe ${index + 1}",
                        coffeeAmount = "${formatAmount(recipe.coffeeAmount)} g",
                        waterAmount = "${formatAmount(recipe.waterAmount)} g",
                        ratio = "${recipe.ratioCoffee}:${recipe.ratioWater}",
                        temperature = "${recipe.temperature}°C",
                        assistantTip = recipe.assistantTip,
                        selection = selectionById[recipe.id] ?: RecipeSelection.None
                    )
                }

            updateState { state ->
                state.copy(
                    isLoading = false,
                    recipes = recipes,
                    brewMethods = brewMethods,
                    errorMessage = null
                )
            }
        }
    }

    private fun openEditSheet(recipeId: String) {
        updateState { state ->
            state.copy(
                isEditSheetVisible = true,
                isEditLoading = true,
                editRecipeId = recipeId,
                brewMethodError = null,
                coffeeAmountError = null,
                waterAmountError = null,
                ratioCoffeeError = null,
                ratioWaterError = null,
                temperatureError = null
            )
        }
        viewModelScope.launch {
            val recipeResult = getRecipeUseCase(recipeId)
            if (recipeResult is RepositoryResult.Failure) {
                showEditError(resolveErrorMessage(recipeResult.error))
                return@launch
            }
            val recipe = (recipeResult as RepositoryResult.Success).value
            if (recipe.status != RecipeStatus.Draft) {
                updateState { it.copy(isEditLoading = false) }
                sendEvent(GeneratedRecipesEvent.ShowMessage("This draft can't be edited here."))
                dismissEditSheet()
                return@launch
            }
            updateState { state -> applyRecipeToEditState(state, recipe, state.brewMethods) }
            recalculateEditWaterAmount()
            updateEditSubmitEnabled()
        }
    }

    private fun dismissEditSheet() {
        updateState { state ->
            state.copy(
                isEditSheetVisible = false,
                isEditBrewMethodSheetVisible = false,
                isEditLoading = false,
                editRecipeId = null
            )
        }
    }

    private fun openEditBrewMethodSheet() {
        updateState { it.copy(isEditBrewMethodSheetVisible = true) }
    }

    private fun dismissEditBrewMethodSheet() {
        updateState { it.copy(isEditBrewMethodSheetVisible = false) }
    }

    private fun selectEditBrewMethod(brewMethodId: String) {
        updateState { state ->
            val methodName = state.brewMethods.firstOrNull { it.id == brewMethodId }?.name
            state.copy(
                selectedBrewMethodId = brewMethodId,
                selectedBrewMethodName = methodName,
                isEditBrewMethodSheetVisible = false
            )
        }
        updateEditSubmitEnabled()
    }

    private fun updateEditCoffeeAmount(value: String) {
        updateState { it.copy(coffeeAmountInput = value) }
        recalculateEditWaterAmount()
        updateEditSubmitEnabled()
    }

    private fun updateEditRatioCoffee(value: String) {
        updateState { it.copy(ratioCoffeeInput = value) }
        recalculateEditWaterAmount()
        updateEditSubmitEnabled()
    }

    private fun updateEditRatioWater(value: String) {
        updateState { it.copy(ratioWaterInput = value) }
        recalculateEditWaterAmount()
        updateEditSubmitEnabled()
    }

    private fun updateEditTemperature(value: String) {
        updateEditText(
            value = value,
            update = { state, input -> state.copy(temperatureInput = input) }
        )
    }

    private fun updateEditAssistantTip(value: String) {
        updateState { it.copy(assistantTipInput = value) }
    }

    private fun updateEditText(
        value: String,
        update: (GeneratedRecipesUiState, String) -> GeneratedRecipesUiState
    ) {
        updateState { state -> update(state, value) }
        updateEditSubmitEnabled()
    }

    private fun recalculateEditWaterAmount() {
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

    private fun updateEditSubmitEnabled() {
        updateState { state ->
            val hasRequiredFields = state.selectedBrewMethodId != null &&
                state.coffeeAmountInput.isNotBlank() &&
                state.waterAmountInput.isNotBlank() &&
                state.ratioCoffeeInput.isNotBlank() &&
                state.ratioWaterInput.isNotBlank() &&
                state.temperatureInput.isNotBlank()
            state.copy(isEditSubmitEnabled = hasRequiredFields && !state.isEditLoading)
        }
    }

    private fun submitEdit() {
        val state = _uiState.value
        if (!state.isEditSubmitEnabled) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Fill in all required fields."))
            return
        }
        val validation = validateEditInputs()
        if (!validation.isValid) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Fix the highlighted fields."))
            return
        }
        if (!hasEditChanges(state)) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("No changes to save."))
            dismissEditSheet()
            return
        }
        updateState { it.copy(isEditLoading = true) }
        viewModelScope.launch {
            updateDraftRecipe(validation)
        }
    }

    private fun validateEditInputs(): ValidationResult {
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
                temperatureError = temperatureError
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

    private fun hasEditChanges(state: GeneratedRecipesUiState): Boolean {
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
        val recipeId = _uiState.value.editRecipeId ?: return showEditError("Missing recipe id.")
        val input = UpdateRecipe(
            brewMethodId = validation.brewMethodId,
            coffeeAmount = validation.coffeeAmount,
            waterAmount = validation.waterAmount,
            ratioCoffee = validation.ratioCoffee,
            ratioWater = validation.ratioWater,
            temperature = validation.temperature,
            assistantTip = validation.assistantTip
        )
        val result = updateRecipeUseCase(recipeId, input)
        when (result) {
            is RepositoryResult.Success -> {
                updateState { it.copy(isEditLoading = false) }
                sendEvent(GeneratedRecipesEvent.ShowMessage("Draft updated."))
                dismissEditSheet()
            }
            is RepositoryResult.Failure -> showEditError(resolveErrorMessage(result.error))
        }
    }

    private fun showEditError(message: String) {
        updateState { it.copy(isEditLoading = false) }
        sendEvent(GeneratedRecipesEvent.ShowMessage(message))
    }

    private fun applyRecipeToEditState(
        state: GeneratedRecipesUiState,
        recipe: Recipe,
        brewMethods: List<BrewMethod>
    ): GeneratedRecipesUiState {
        val methodName = brewMethods.firstOrNull { it.id == recipe.brewMethodId }?.name
        val coffeeAmount = recipe.coffeeAmount.toString()
        val waterAmount = recipe.waterAmount.toString()
        val ratioCoffee = recipe.ratioCoffee.toString()
        val ratioWater = recipe.ratioWater.toString()
        val temperature = recipe.temperature.toString()
        val assistantTip = recipe.assistantTip.orEmpty()
        return state.copy(
            isEditLoading = false,
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

    private fun toggleSelection(recipeId: String, selection: RecipeSelection) {
        updateState { state ->
            state.copy(
                recipes = state.recipes.map { recipe ->
                    if (recipe.id != recipeId) {
                        recipe
                    } else {
                        recipe.copy(selection = selection)
                    }
                }
            )
        }
    }

    private fun confirmSelections() {
        if (_uiState.value.isSubmitting) {
            return
        }
        val recipes = _uiState.value.recipes
        if (recipes.isEmpty()) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("No recipes to confirm."))
            return
        }

        val hasIncompleteSelection = recipes.any { it.selection == RecipeSelection.None }
        if (hasIncompleteSelection) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Select accept or reject for each recipe."))
            return
        }

        updateState { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val failures = mutableListOf<String>()
            recipes.forEach { recipe ->
                when (recipe.selection) {
                    RecipeSelection.Accept -> {
                        val result = processRecipeSelection(
                            recipeId = recipe.id,
                            status = RecipeStatus.Saved,
                            action = RecipeActionModel.Accepted
                        )
                        if (result != null) failures.add(result)
                    }
                    RecipeSelection.Reject -> {
                        val result = processRecipeSelection(
                            recipeId = recipe.id,
                            status = RecipeStatus.Rejected,
                            action = RecipeActionModel.Rejected
                        )
                        if (result != null) failures.add(result)
                    }
                    RecipeSelection.None -> Unit
                }
            }

            if (failures.isNotEmpty()) {
                updateState { it.copy(isSubmitting = false) }
                sendEvent(
                    GeneratedRecipesEvent.ShowMessage(
                        failures.first()
                    )
                )
                return@launch
            }

            updateState { state ->
                state.copy(
                    isSubmitting = false,
                    recipes = emptyList()
                )
            }
            sendEvent(GeneratedRecipesEvent.ShowMessage("Selections saved."))
            sendEvent(GeneratedRecipesEvent.NavigateToSuccess)
        }
    }

    private fun showAbortDialog() {
        if (_uiState.value.requestId.isNullOrBlank()) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Request not found."))
            sendEvent(GeneratedRecipesEvent.NavigateBack)
            return
        }
        updateState { state -> state.copy(isAbortDialogVisible = true) }
    }

    private fun dismissAbortDialog() {
        updateState { state -> state.copy(isAbortDialogVisible = false) }
    }

    private fun confirmAbort() {
        updateState { state -> state.copy(isAbortDialogVisible = false) }
        sendEvent(GeneratedRecipesEvent.NavigateBack)
    }

    private fun updateState(reducer: (GeneratedRecipesUiState) -> GeneratedRecipesUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun sendEvent(event: GeneratedRecipesEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showError(message: String) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
        sendEvent(GeneratedRecipesEvent.ShowMessage(message))
    }

    private fun resolveErrorMessage(error: RepositoryError): String {
        return when (error) {
            is RepositoryError.Network -> "Check your connection and try again."
            is RepositoryError.NotFound -> "Request not found."
            is RepositoryError.Unauthorized -> "Please sign in again."
            is RepositoryError.Validation -> error.message
            is RepositoryError.Unknown -> error.message
        }
    }

    private fun formatAmount(value: Double): String {
        return String.format(Locale.getDefault(), "%.1f", value)
    }

    private suspend fun logRecipeAction(recipeId: String, action: RecipeActionModel) {
        val requestId = _uiState.value.requestId ?: return
        val result = createRecipeActionLogUseCase(
            CreateRecipeActionLogModel(
                recipeId = recipeId,
                generationRequestId = requestId,
                action = action
            )
        )
        if (result is RepositoryResult.Failure) {
            Log.e(TAG, "Failed to log recipe action: ${result.error}")
        }
    }

    private suspend fun processRecipeSelection(
        recipeId: String,
        status: RecipeStatus,
        action: RecipeActionModel
    ): String? {
        val updateResult = updateRecipeUseCase(
            id = recipeId,
            input = UpdateRecipe(status = status)
        )
        if (updateResult is RepositoryResult.Failure) {
            Log.e(TAG, "Failed to update recipe status: ${updateResult.error}")
            return resolveErrorMessage(updateResult.error)
        }
        logRecipeAction(recipeId, action)
        return null
    }

    private companion object {
        const val TAG: String = "GeneratedRecipesViewModel"
        const val DRAFT_PAGE_SIZE: Int = 10
    }
}
