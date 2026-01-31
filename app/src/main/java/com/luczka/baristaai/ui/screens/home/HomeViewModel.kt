package com.luczka.baristaai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.ListRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val listRecipesUseCase: ListRecipesUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _event: MutableSharedFlow<HomeEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<HomeEvent> = _event.asSharedFlow()

    init {
        handleAction(HomeAction.LoadRecipes)
    }

    fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.LoadRecipes -> loadRecipes()
            HomeAction.LoadMore -> loadMoreRecipes()
            is HomeAction.SelectFilter -> selectFilter(action.filterId)
            HomeAction.OpenProfile -> sendEvent(HomeEvent.NavigateToProfile)
            HomeAction.OpenAddOptions -> updateState { it.copy(isAddOptionSheetVisible = true) }
            HomeAction.DismissAddOptions -> updateState { it.copy(isAddOptionSheetVisible = false) }
            HomeAction.OpenGenerate -> openGenerate()
            HomeAction.OpenManual -> openManual()
            is HomeAction.OpenRecipeDetail -> sendEvent(
                HomeEvent.NavigateToRecipeDetail(action.recipeId)
            )
        }
    }

    private fun loadRecipes() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isLoadingMore = false,
            canLoadMore = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val filtersResult = listBrewMethodsUseCase()
            if (filtersResult is RepositoryResult.Failure) {
                showError("Failed to load brew methods.")
                return@launch
            }

            val filters = buildFilters((filtersResult as RepositoryResult.Success).value)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                filters = filters,
                recipes = emptyList(),
                canLoadMore = true,
                errorMessage = null
            )

            loadRecipePage(reset = true)
        }
    }

    private fun selectFilter(filterId: String?) {
        val selectedFilterId = filterId?.takeIf { it != FilterUiState.ALL_FILTER_ID }
        _uiState.value = _uiState.value.copy(
            selectedFilterId = selectedFilterId,
            recipes = emptyList(),
            canLoadMore = true,
            isLoading = true,
            errorMessage = null
        )
        viewModelScope.launch {
            loadRecipePage(reset = true)
        }
    }

    private fun openGenerate() {
        updateState { it.copy(isAddOptionSheetVisible = false) }
        sendEvent(HomeEvent.NavigateToGenerate)
    }

    private fun openManual() {
        updateState { it.copy(isAddOptionSheetVisible = false) }
        sendEvent(HomeEvent.NavigateToManual)
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoadingMore = false,
            errorMessage = message
        )
        sendEvent(HomeEvent.ShowError(message))
    }

    private fun updateState(reducer: (HomeUiState) -> HomeUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    private fun buildFilters(methods: List<BrewMethod>): List<FilterUiState> {
        val allFilter = FilterUiState(
            id = FilterUiState.ALL_FILTER_ID,
            label = "All recipes"
        )
        val methodFilters = methods.map { it.toFilterUiState() }
        return listOf(allFilter) + methodFilters
    }

    private fun loadMoreRecipes() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || !_uiState.value.canLoadMore) {
            return
        }
        updateState { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            loadRecipePage(reset = false)
        }
    }

    private suspend fun loadRecipePage(reset: Boolean) {
        val currentRecipes = if (reset) emptyList() else _uiState.value.recipes
        val offset = currentRecipes.size
        val filter = RecipeFilter(
            brewMethodId = _uiState.value.selectedFilterId
        )
        val page = PageRequest(limit = PAGE_SIZE, offset = offset)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        when (val result = listRecipesUseCase(filter, page, sort)) {
            is RepositoryResult.Success -> {
                val methodsMap = _uiState.value.filters
                    .filter { it.id != FilterUiState.ALL_FILTER_ID }
                    .associateBy { it.id }
                val newItems = result.value
                    .filter { it.status == RecipeStatus.Saved }
                    .map { it.toUiState(methodsMap) }
                val updatedRecipes = if (reset) newItems else currentRecipes + newItems
                updateState {
                    it.copy(
                        recipes = updatedRecipes,
                        isLoading = false,
                        isLoadingMore = false,
                        canLoadMore = result.value.size == PAGE_SIZE,
                        errorMessage = null
                    )
                }
            }
            is RepositoryResult.Failure -> {
                handleLoadError(result.error, reset)
            }
        }
    }

    private fun handleLoadError(error: RepositoryError, reset: Boolean) {
        val message = when (error) {
            is RepositoryError.Network -> "Check your connection and try again."
            is RepositoryError.NotFound -> "Recipes not found."
            is RepositoryError.Unauthorized -> "Please sign in again."
            is RepositoryError.Validation -> error.message
            is RepositoryError.Unknown -> error.message
        }
        updateState {
            it.copy(
                isLoading = false,
                isLoadingMore = false,
                errorMessage = message
            )
        }
        if (reset) {
            sendEvent(HomeEvent.ShowError(message))
        }
    }

    private fun Recipe.toUiState(methodsMap: Map<String, FilterUiState>): RecipeUiState {
        val methodLabel = methodsMap[brewMethodId]?.label ?: brewMethodId
        return RecipeUiState(
            id = id,
            title = "Recipe",
            methodId = methodLabel,
            status = status.name.lowercase().replaceFirstChar { it.titlecase() }
        )
    }

    private companion object {
        const val PAGE_SIZE: Int = 20
    }
}
