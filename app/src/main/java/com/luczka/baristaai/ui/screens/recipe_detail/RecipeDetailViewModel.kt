package com.luczka.baristaai.ui.screens.recipe_detail

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
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RecipeDetailUiState> =
        MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState

    private val _event: MutableSharedFlow<RecipeDetailEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<RecipeDetailEvent> = _event.asSharedFlow()

    init {
        val recipeId = savedStateHandle.get<String>("recipeId")
        updateState { it.copy(recipeId = recipeId) }
        handleAction(RecipeDetailAction.Load)
    }

    fun handleAction(action: RecipeDetailAction) {
        when (action) {
            RecipeDetailAction.Load -> loadRecipe()
            RecipeDetailAction.Retry -> loadRecipe()
            RecipeDetailAction.NavigateBack -> sendEvent(RecipeDetailEvent.NavigateBack)
            RecipeDetailAction.Edit -> navigateToEdit()
            RecipeDetailAction.DeleteClick -> updateState { it.copy(isDeleteDialogVisible = true) }
            RecipeDetailAction.DismissDelete -> updateState { it.copy(isDeleteDialogVisible = false) }
            RecipeDetailAction.ConfirmDelete -> confirmDelete()
        }
    }

    private fun loadRecipe() {
        val recipeId = _uiState.value.recipeId
        if (recipeId.isNullOrBlank()) {
            showError("Recipe not found.")
            sendEvent(RecipeDetailEvent.NavigateBack)
            return
        }
        updateState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val brewMethodsResult = listBrewMethodsUseCase()
            if (brewMethodsResult is RepositoryResult.Failure) {
                showError("Failed to load brew methods.")
                return@launch
            }
            val recipeResult = getRecipeUseCase(recipeId)
            if (recipeResult is RepositoryResult.Failure) {
                showError(resolveErrorMessage(recipeResult.error))
                if (recipeResult.error is RepositoryError.NotFound) {
                    sendEvent(RecipeDetailEvent.NavigateBack)
                }
                return@launch
            }

            val brewMethods = (brewMethodsResult as RepositoryResult.Success).value
            val recipe = (recipeResult as RepositoryResult.Success).value
            if (recipe.status == RecipeStatus.Deleted) {
                sendEvent(RecipeDetailEvent.ShowMessage("This recipe was deleted."))
                sendEvent(RecipeDetailEvent.NavigateBack)
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
        sendEvent(RecipeDetailEvent.NavigateToEdit(recipeId))
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
                    sendEvent(RecipeDetailEvent.ShowMessage("Recipe deleted."))
                    sendEvent(RecipeDetailEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> {
                    updateState { it.copy(isDeleting = false) }
                    showError(resolveErrorMessage(result.error))
                }
            }
        }
    }

    private fun showError(message: String) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
        sendEvent(RecipeDetailEvent.ShowError(message))
    }

    private fun updateState(reducer: (RecipeDetailUiState) -> RecipeDetailUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun sendEvent(event: RecipeDetailEvent) {
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
