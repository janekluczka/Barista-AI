package com.luczka.baristaai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
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
class HomeViewModel @Inject constructor(
    private val listBrewMethodsUseCase: ListBrewMethodsUseCase
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
            is HomeAction.SelectFilter -> selectFilter(action.filterId)
            HomeAction.OpenProfile -> sendEvent(HomeEvent.NavigateToProfile)
            HomeAction.OpenGenerate -> sendEvent(HomeEvent.NavigateToGenerate)
            is HomeAction.OpenRecipeDetail -> sendEvent(
                HomeEvent.NavigateToRecipeDetail(action.recipeId)
            )
        }
    }

    private fun loadRecipes() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
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
                errorMessage = null
            )
        }
    }

    private fun selectFilter(filterId: String?) {
        val selectedFilterId = filterId?.takeIf { it != FilterUiState.ALL_FILTER_ID }
        // TODO: Apply filtering when data source is connected.
        _uiState.value = _uiState.value.copy(
            selectedFilterId = selectedFilterId
        )
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
        sendEvent(HomeEvent.ShowError(message))
    }

    private fun buildFilters(methods: List<BrewMethod>): List<FilterUiState> {
        val allFilter = FilterUiState(
            id = FilterUiState.ALL_FILTER_ID,
            label = "All recipes"
        )
        val methodFilters = methods.map { it.toFilterUiState() }
        return listOf(allFilter) + methodFilters
    }
}
