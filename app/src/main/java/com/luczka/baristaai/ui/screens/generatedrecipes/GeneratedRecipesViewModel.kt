package com.luczka.baristaai.ui.screens.generatedrecipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.RecipeActionModel
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.usecase.CreateRecipeActionLogUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.ListRecipesUseCase
import com.luczka.baristaai.domain.usecase.UpdateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GeneratedRecipesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
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
        val refreshFlow = savedStateHandle.getStateFlow(REFRESH_KEY, 0)
        updateState { it.copy(requestId = requestId) }
        if (requestId.isNullOrBlank()) {
            sendEvent(GeneratedRecipesEvent.ShowMessage("Request not found."))
            sendEvent(GeneratedRecipesEvent.NavigateBack)
        } else {
            initialize(requestId)
        }

        viewModelScope.launch {
            refreshFlow.drop(1).collect {
                val currentRequestId = _uiState.value.requestId
                if (!currentRequestId.isNullOrBlank()) {
                    initialize(currentRequestId)
                }
            }
        }
    }

    fun handleAction(action: GeneratedRecipesAction) {
        when (action) {
            is GeneratedRecipesAction.EditRecipe -> editRecipe(action.recipeId)
            is GeneratedRecipesAction.ToggleSelection -> toggleSelection(action.recipeId, action.selection)
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
            val recipes = (recipesResult as RepositoryResult.Success).value
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
                        selection = RecipeSelection.None
                    )
                }

            updateState { state ->
                state.copy(
                    isLoading = false,
                    recipes = recipes,
                    errorMessage = null
                )
            }
        }
    }

    private fun editRecipe(recipeId: String) {
        sendEvent(GeneratedRecipesEvent.NavigateToEditGeneratedRecipe(recipeId))
    }

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
        const val REFRESH_KEY: String = "refreshGeneratedRecipes"
    }
}
