package com.luczka.baristaai.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
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
        // TODO: Load user details from domain layer when available.
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            email = null,
            userId = null
        )
    }

    private fun confirmLogout() {
        // TODO: Execute logout use case when domain layer is ready.
        sendEvent(ProfileEvent.NavigateToAuthLanding)
    }

    private fun sendEvent(event: ProfileEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
