package com.luczka.baristaai.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.usecase.ObserveAuthStateUseCase
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthStateViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {
    private val logTag = "AuthStateViewModel"
    private val _uiState: MutableStateFlow<AuthStateUiState> = MutableStateFlow(AuthStateUiState())
    val uiState: StateFlow<AuthStateUiState> = _uiState

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { status ->
                Log.d(logTag, "Session status: $status")
                _uiState.value = when (status) {
                    SessionStatus.Initializing -> AuthStateUiState(
                        isLoading = true,
                        isAuthenticated = false
                    )
                    is SessionStatus.Authenticated -> AuthStateUiState(
                        isLoading = false,
                        isAuthenticated = true
                    )
                    is SessionStatus.NotAuthenticated -> AuthStateUiState(
                        isLoading = false,
                        isAuthenticated = false
                    )
                    is SessionStatus.RefreshFailure -> AuthStateUiState(
                        isLoading = false,
                        isAuthenticated = false
                    )
                }
                Log.d(logTag, "Auth UI state: ${_uiState.value}")
            }
        }
    }
}
