package com.luczka.baristaai.ui.screens.recipedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.usecase.DeleteRecipeUseCase
import com.luczka.baristaai.domain.usecase.GetRecipeUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RecipeDetailsUiState> =
        MutableStateFlow(RecipeDetailsUiState())
    val uiState: StateFlow<RecipeDetailsUiState> = _uiState

    private val _event: MutableSharedFlow<RecipeDetailsEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<RecipeDetailsEvent> = _event.asSharedFlow()

    init {
        val recipeId = savedStateHandle.get<String>("recipeId")
        updateState { it.copy(recipeId = recipeId) }
        handleAction(RecipeDetailsAction.Load)
    }

    fun handleAction(action: RecipeDetailsAction) {
        when (action) {
            RecipeDetailsAction.Load -> loadRecipe()
            RecipeDetailsAction.Retry -> loadRecipe()
            RecipeDetailsAction.RetryDelete -> confirmDelete()
            RecipeDetailsAction.NavigateBack -> sendEvent(RecipeDetailsEvent.NavigateBack)
            RecipeDetailsAction.Edit -> navigateToEdit()
            RecipeDetailsAction.DeleteClick -> updateState { it.copy(isDeleteDialogVisible = true) }
            RecipeDetailsAction.DismissDelete -> updateState { it.copy(isDeleteDialogVisible = false) }
            RecipeDetailsAction.ConfirmDelete -> confirmDelete()
        }
    }

    private fun loadRecipe() {
        val recipeId = _uiState.value.recipeId
        if (recipeId.isNullOrBlank()) {
            showError("Recipe not found.")
            sendEvent(RecipeDetailsEvent.NavigateBack)
            return
        }
        updateState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val brewMethodsResult = listBrewMethodsUseCase()
            if (brewMethodsResult is RepositoryResult.Failure) {
                val error = brewMethodsResult.error
                showError(
                    "Failed to load brew methods.",
                    if (error.isRetryable) RecipeDetailsAction.Retry else null
                )
                return@launch
            }
            val recipeResult = getRecipeUseCase(recipeId)
            if (recipeResult is RepositoryResult.Failure) {
                val error = recipeResult.error
                showError(
                    resolveErrorMessage(error),
                    if (error.isRetryable) RecipeDetailsAction.Retry else null
                )
                if (error is RepositoryError.NotFound) {
                    sendEvent(RecipeDetailsEvent.NavigateBack)
                }
                return@launch
            }

            val brewMethods = (brewMethodsResult as RepositoryResult.Success).value
            val recipe = (recipeResult as RepositoryResult.Success).value
            if (recipe.status == RecipeStatus.Deleted) {
                sendEvent(RecipeDetailsEvent.ShowMessage("This recipe was deleted."))
                sendEvent(RecipeDetailsEvent.NavigateBack)
                updateState { it.copy(isLoading = false, recipe = null, brewMethodName = null) }
                return@launch
            }
            val methodName = brewMethods.firstOrNull { it.id == recipe.brewMethodId }?.name
            updateState {
                it.copy(
                    isLoading = false,
                    recipe = recipe,
                    brewMethodName = methodName,
                    errorMessage = null
                )
            }
        }
    }

    private fun navigateToEdit() {
        val recipeId = _uiState.value.recipeId ?: return
        sendEvent(RecipeDetailsEvent.NavigateToEdit(recipeId))
    }

    private fun confirmDelete() {
        val recipeId = _uiState.value.recipeId
        if (recipeId.isNullOrBlank()) {
            showError("Recipe not found.")
            return
        }
        if (_uiState.value.isDeleting) {
            return
        }
        updateState { it.copy(isDeleting = true) }
        viewModelScope.launch {
            when (val result = deleteRecipeUseCase(recipeId)) {
                is RepositoryResult.Success -> {
                    updateState { it.copy(isDeleting = false, isDeleteDialogVisible = false) }
                    sendEvent(RecipeDetailsEvent.ShowMessage("Recipe deleted."))
                    sendEvent(RecipeDetailsEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> {
                    updateState { it.copy(isDeleting = false) }
                    showError(
                        resolveErrorMessage(result.error),
                        if (result.error.isRetryable) RecipeDetailsAction.RetryDelete else null
                    )
                }
            }
        }
    }

    private fun showError(message: String, retryAction: RecipeDetailsAction? = null) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
        sendEvent(RecipeDetailsEvent.ShowError(message, retryAction))
    }

    private fun updateState(reducer: (RecipeDetailsUiState) -> RecipeDetailsUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun sendEvent(event: RecipeDetailsEvent) {
        viewModelScope.launch {
            _event.emit(event)
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
}
