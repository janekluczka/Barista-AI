package com.luczka.baristaai.ui.screens.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateGenerationRequest
import com.luczka.baristaai.domain.network.NetworkMonitor
import com.luczka.baristaai.domain.usecase.CreateGenerationRequestUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GenerateRecipeViewModel @Inject constructor(
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val createGenerationRequestUseCase: CreateGenerationRequestUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _uiState: MutableStateFlow<GenerateRecipeUiState> =
        MutableStateFlow(GenerateRecipeUiState())
    val uiState: StateFlow<GenerateRecipeUiState> = _uiState

    private val _event: MutableSharedFlow<GenerateRecipeEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<GenerateRecipeEvent> = _event.asSharedFlow()

    init {
        handleAction(GenerateRecipeAction.LoadData)
    }

    fun handleAction(action: GenerateRecipeAction) {
        when (action) {
            GenerateRecipeAction.LoadData -> loadBrewMethods()
            GenerateRecipeAction.OpenBrewMethodSheet -> showBrewMethodSheet()
            GenerateRecipeAction.DismissBrewMethodSheet -> hideBrewMethodSheet()
            is GenerateRecipeAction.SelectBrewMethod -> selectBrewMethod(action.brewMethodId)
            is GenerateRecipeAction.UpdateCoffeeAmount -> updateCoffeeAmount(action.coffeeAmount)
            is GenerateRecipeAction.UpdateCanAdjustTemperature -> updateCanAdjustTemperature(
                action.canAdjustTemperature
            )
            is GenerateRecipeAction.UpdateUserComment -> updateUserComment(action.userComment)
            GenerateRecipeAction.SubmitRequest -> submitRequest()
            GenerateRecipeAction.NavigateBack -> sendEvent(GenerateRecipeEvent.NavigateBack)
        }
    }

    private fun loadBrewMethods() {
        updateState { state ->
            state.copy(isLoading = true, errorMessage = null)
        }
        viewModelScope.launch {
            when (val result = listBrewMethodsUseCase()) {
                is RepositoryResult.Success -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            brewMethods = result.value,
                            errorMessage = null
                        )
                    }
                }
                is RepositoryResult.Failure -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "Failed to load brew methods."
                        )
                    }
                    sendEvent(GenerateRecipeEvent.ShowError("Failed to load brew methods."))
                }
            }
        }
    }

    private fun selectBrewMethod(brewMethodId: String) {
        val methodName = _uiState.value.brewMethods
            .firstOrNull { it.id == brewMethodId }
            ?.name
        updateState { state ->
            state.copy(
                selectedBrewMethodId = brewMethodId,
                selectedBrewMethodName = methodName,
                brewMethodError = null,
                isBrewMethodSheetVisible = false
            )
        }
    }

    private fun updateCoffeeAmount(coffeeAmount: String) {
        updateState { state ->
            state.copy(
                coffeeAmountInput = coffeeAmount,
                coffeeAmountError = null
            )
        }
    }

    private fun updateCanAdjustTemperature(canAdjustTemperature: Boolean) {
        updateState { state ->
            state.copy(canAdjustTemperature = canAdjustTemperature)
        }
    }

    private fun updateUserComment(userComment: String) {
        updateState { state ->
            state.copy(userComment = userComment)
        }
    }

    private fun submitRequest() {
        if (_uiState.value.isLoading) {
            return
        }
        if (!networkMonitor.isOnline()) {
            val message = "No internet connection."
            updateState { state -> state.copy(errorMessage = message) }
            sendEvent(GenerateRecipeEvent.ShowError(message))
            return
        }

        val coffeeAmount = validateForm() ?: return
        val brewMethodId = _uiState.value.selectedBrewMethodId ?: return
        val userComment = _uiState.value.userComment.trim().ifBlank { null }

        val request = CreateGenerationRequest(
            brewMethodId = brewMethodId,
            coffeeAmount = coffeeAmount,
            canAdjustTemperature = _uiState.value.canAdjustTemperature,
            userComment = userComment
        )
        updateState { state -> state.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = createGenerationRequestUseCase(request)) {
                is RepositoryResult.Success -> {
                    updateState { state -> state.copy(isLoading = false) }
                    sendEvent(
                        GenerateRecipeEvent.NavigateToGeneratedRecipes(result.value.id)
                    )
                }
                is RepositoryResult.Failure -> handleCreateFailure(result.error)
            }
        }
    }

    private fun validateForm(): Double? {
        val brewMethodError = if (_uiState.value.selectedBrewMethodId == null) {
            "Select a brew method."
        } else {
            null
        }

        val coffeeAmountError = validateCoffeeAmount(_uiState.value.coffeeAmountInput)

        updateState { state ->
            state.copy(
                brewMethodError = brewMethodError,
                coffeeAmountError = coffeeAmountError
            )
        }

        if (brewMethodError != null || coffeeAmountError != null) {
            return null
        }
        return parseCoffeeAmount(_uiState.value.coffeeAmountInput)
    }

    private fun validateCoffeeAmount(value: String): String? {
        if (value.isBlank()) {
            return "Enter coffee amount."
        }
        val amount = parseCoffeeAmount(value) ?: return "Enter a valid number."
        if (amount <= 0.0) {
            return "Enter a positive amount."
        }
        return null
    }

    private fun parseCoffeeAmount(value: String): Double? {
        val normalized = value.trim().replace(',', '.')
        return normalized.toDoubleOrNull()
    }

    private fun updateState(reducer: (GenerateRecipeUiState) -> GenerateRecipeUiState) {
        val updated = reducer(_uiState.value)
        val submitEnabled = isReadyToSubmit(updated)
        _uiState.value = updated.copy(isSubmitEnabled = submitEnabled)
    }

    private fun isReadyToSubmit(state: GenerateRecipeUiState): Boolean {
        if (state.isLoading) {
            return false
        }
        val hasMethod = state.selectedBrewMethodId != null
        val amount = parseCoffeeAmount(state.coffeeAmountInput)
        return hasMethod && amount != null && amount > 0.0
    }

    private fun sendEvent(event: GenerateRecipeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showBrewMethodSheet() {
        updateState { state ->
            state.copy(isBrewMethodSheetVisible = true)
        }
    }

    private fun hideBrewMethodSheet() {
        updateState { state ->
            state.copy(isBrewMethodSheetVisible = false)
        }
    }

    private fun handleCreateFailure(error: RepositoryError) {
        val message = when (error) {
            is RepositoryError.Network -> "Network error. Check your connection."
            is RepositoryError.Validation -> error.message
            is RepositoryError.Unauthorized -> "Session expired. Please sign in."
            is RepositoryError.NotFound -> error.message
            is RepositoryError.Unknown -> "Failed to create request."
        }
        updateState { state ->
            state.copy(
                isLoading = false,
                errorMessage = message
            )
        }
        sendEvent(GenerateRecipeEvent.ShowError(message))
    }
}
