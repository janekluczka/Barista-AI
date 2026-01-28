package com.luczka.baristaai.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.usecase.GetCurrentUserUseCase
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
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _event: MutableSharedFlow<ProfileEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<ProfileEvent> = _event.asSharedFlow()

    init {
        handleAction(ProfileAction.LoadProfile)
    }

    fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> loadProfile()
            ProfileAction.ConfirmLogout -> confirmLogout()
        }
    }

    private fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        email = result.value?.email,
                        userId = result.value?.id
                    )
                }
                is RepositoryResult.Failure -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    showError(mapProfileError(result.error))
                }
            }
        }
    }

    private fun confirmLogout() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = signOutUseCase()) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(ProfileEvent.NavigateToLogin)
                }
                is RepositoryResult.Failure -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    showError(mapProfileError(result.error))
                }
            }
        }
    }

    private fun sendEvent(event: ProfileEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showError(message: String) {
        sendEvent(ProfileEvent.ShowError(message))
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
}
