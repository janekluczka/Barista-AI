package com.luczka.baristaai.ui.screens.generated

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionModel
import com.luczka.baristaai.domain.usecase.CreateRecipeActionLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GeneratedRecipesViewModel @Inject constructor(
    private val createRecipeActionLogUseCase: CreateRecipeActionLogUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<GeneratedRecipesUiState> =
        MutableStateFlow(GeneratedRecipesUiState())
    val uiState: StateFlow<GeneratedRecipesUiState> = _uiState

    private val _event: MutableSharedFlow<GeneratedRecipesEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<GeneratedRecipesEvent> = _event.asSharedFlow()

    fun handleAction(action: GeneratedRecipesAction) {
        when (action) {
            is GeneratedRecipesAction.Initialize -> initialize(action.requestId)
            is GeneratedRecipesAction.AcceptRecipe -> acceptRecipe(action.recipeId)
            is GeneratedRecipesAction.RejectRecipe -> rejectRecipe(action.recipeId)
            is GeneratedRecipesAction.EditRecipe -> editRecipe(action.recipeId)
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

        // TODO: Replace placeholder loading with Edge Function call for generated recipes.
        updateState { state ->
            state.copy(
                isLoading = false,
                recipes = emptyList()
            )
        }
    }

    private fun acceptRecipe(recipeId: String) {
        // TODO: Persist accepted recipe.
        viewModelScope.launch {
            logRecipeAction(recipeId, RecipeActionModel.Accepted)
            sendEvent(GeneratedRecipesEvent.ShowMessage("Recipe accepted."))
        }
    }

    private fun rejectRecipe(recipeId: String) {
        updateState { state ->
            state.copy(recipes = state.recipes.filterNot { it.id == recipeId })
        }
        viewModelScope.launch {
            logRecipeAction(recipeId, RecipeActionModel.Rejected)
            sendEvent(GeneratedRecipesEvent.ShowMessage("Recipe rejected."))
        }
    }

    private fun editRecipe(recipeId: String) {
        sendEvent(GeneratedRecipesEvent.NavigateToEditRecipe(recipeId))
    }

    private fun updateState(reducer: (GeneratedRecipesUiState) -> GeneratedRecipesUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun sendEvent(event: GeneratedRecipesEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
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

    private companion object {
        const val TAG: String = "GeneratedRecipesViewModel"
    }
}
