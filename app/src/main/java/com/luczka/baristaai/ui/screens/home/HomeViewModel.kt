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
import com.luczka.baristaai.domain.usecase.GetCurrentUserUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.ListRecipesUseCase
import com.luczka.baristaai.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase,
    private val listRecipesUseCase: ListRecipesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _event: MutableSharedFlow<HomeEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<HomeEvent> = _event.asSharedFlow()

    init {
        handleAction(HomeAction.LoadRecipes)
        loadProfile()
    }

    fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.LoadRecipes -> loadRecipes()
            HomeAction.RetryLoadProfile -> loadProfile()
            HomeAction.LoadMore -> loadMoreRecipes()
            is HomeAction.SelectFilter -> selectFilter(action.filterId)
            HomeAction.OpenProfile -> updateState { it.copy(isProfileSheetVisible = true) }
            HomeAction.DismissProfile -> updateState { it.copy(isProfileSheetVisible = false) }
            HomeAction.OpenAddOptions -> updateState { it.copy(isAddOptionSheetVisible = true) }
            HomeAction.DismissAddOptions -> updateState { it.copy(isAddOptionSheetVisible = false) }
            HomeAction.OpenGenerate -> openGenerate()
            HomeAction.OpenManual -> openManual()
            is HomeAction.OpenRecipeDetails -> sendEvent(
                HomeEvent.NavigateToRecipeDetails(action.recipeId)
            )
            HomeAction.OpenLogoutDialog -> updateState { it.copy(isLogoutDialogVisible = true) }
            HomeAction.DismissLogoutDialog -> updateState { it.copy(isLogoutDialogVisible = false) }
            HomeAction.ConfirmLogout -> confirmLogout()
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
                val error = filtersResult.error
                showError(
                    "Failed to load brew methods.",
                    if (error.isRetryable) HomeAction.LoadRecipes else null
                )
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

    private fun showError(message: String, retryAction: HomeAction? = null) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoadingMore = false,
            errorMessage = message
        )
        sendEvent(HomeEvent.ShowError(message, retryAction))
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
            brewMethodId = _uiState.value.selectedFilterId,
            status = RecipeStatus.Saved
        )
        val page = PageRequest(limit = PAGE_SIZE, offset = offset)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        when (val result = listRecipesUseCase(filter, page, sort)) {
            is RepositoryResult.Success -> {
                val methodsMap = _uiState.value.filters
                    .filter { it.id != FilterUiState.ALL_FILTER_ID }
                    .associateBy { it.id }
                val newItems = result.value.map { it.toUiState(methodsMap) }
                val updatedRecipes = if (reset) {
                    newItems
                } else {
                    (currentRecipes + newItems).distinctBy { it.id }
                }
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
            sendEvent(HomeEvent.ShowError(message, if (error.isRetryable) HomeAction.LoadRecipes else null))
        }
    }

    private fun Recipe.toUiState(methodsMap: Map<String, FilterUiState>): RecipeUiState {
        val methodLabel = methodsMap[brewMethodId]?.label ?: brewMethodId
        return RecipeUiState(
            id = id,
            methodName = methodLabel,
            coffeeAmount = coffeeAmount,
            ratioCoffee = ratioCoffee,
            ratioWater = ratioWater,
            waterAmount = waterAmount,
            temperature = temperature
        )
    }

    private fun loadProfile() {
        updateState { it.copy(isProfileLoading = true) }
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is RepositoryResult.Success -> {
                    updateState {
                        it.copy(
                            isProfileLoading = false,
                            profileEmail = result.value?.email,
                            profileUserId = result.value?.id
                        )
                    }
                }
                is RepositoryResult.Failure -> {
                    updateState { it.copy(isProfileLoading = false) }
                    sendEvent(
                        HomeEvent.ShowError(
                            mapProfileError(result.error),
                            if (result.error.isRetryable) HomeAction.RetryLoadProfile else null
                        )
                    )
                }
            }
        }
    }

    private fun confirmLogout() {
        updateState { it.copy(isProfileLoading = true, isProfileSheetVisible = false, isLogoutDialogVisible = false) }
        viewModelScope.launch {
            when (val result = signOutUseCase()) {
                is RepositoryResult.Success -> {
                    updateState { it.copy(isProfileLoading = false) }
                    sendEvent(HomeEvent.NavigateToLogin)
                }
                is RepositoryResult.Failure -> {
                    updateState { it.copy(isProfileLoading = false) }
                    sendEvent(
                        HomeEvent.ShowError(
                            mapProfileError(result.error),
                            if (result.error.isRetryable) HomeAction.ConfirmLogout else null
                        )
                    )
                }
            }
        }
    }

    private fun mapProfileError(error: RepositoryError): String {
        return when (error) {
            is RepositoryError.Network -> "Network error. Check your connection."
            is RepositoryError.Unauthorized -> "You are not signed in."
            is RepositoryError.NotFound -> error.message
            is RepositoryError.Validation -> error.message
            is RepositoryError.Unknown -> error.message
        }
    }

    private companion object {
        const val PAGE_SIZE: Int = 20
    }
}
