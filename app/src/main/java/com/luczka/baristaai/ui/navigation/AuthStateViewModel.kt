package com.luczka.baristaai.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.usecase.GetCurrentUserUseCase
import com.luczka.baristaai.domain.usecase.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthStateViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<AuthStateUiState> = MutableStateFlow(AuthStateUiState())
    val uiState: StateFlow<AuthStateUiState> = _uiState

    init {
        loadInitialAuthState()
        observeAuthState()
    }

    private fun loadInitialAuthState() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is RepositoryResult.Success -> {
                    _uiState.value = AuthStateUiState(
                        isLoading = false,
                        isAuthenticated = result.value != null
                    )
                }
                is RepositoryResult.Failure -> {
                    _uiState.value = AuthStateUiState(
                        isLoading = false,
                        isAuthenticated = false
                    )
                }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { user ->
                _uiState.value = AuthStateUiState(
                    isLoading = false,
                    isAuthenticated = user != null
                )
            }
        }
    }
}
