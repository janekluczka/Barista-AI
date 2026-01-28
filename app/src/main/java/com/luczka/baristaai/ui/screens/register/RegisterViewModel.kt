package com.luczka.baristaai.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _event: MutableSharedFlow<RegisterEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<RegisterEvent> = _event.asSharedFlow()

    fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UpdateEmail -> updateEmail(action.email)
            is RegisterAction.UpdatePassword -> updatePassword(action.password)
            RegisterAction.SubmitRegister -> submitRegister()
            RegisterAction.NavigateToLogin -> sendEvent(RegisterEvent.NavigateToLogin)
        }
    }

    private fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    private fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    private fun submitRegister() {
        if (_uiState.value.isLoading) {
            return
        }
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            emailError = null,
            passwordError = null
        )

        viewModelScope.launch {
            when (val result = signUpUseCase(_uiState.value.email, _uiState.value.password)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(RegisterEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun handleError(error: RepositoryError) {
        val (emailError, passwordError, message) = when (error) {
            is RepositoryError.Validation -> mapValidationError(error.message)
            else -> Triple(null, null, error.message)
        }
        val fallbackMessage = mapFallbackMessage(error)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            emailError = emailError,
            passwordError = passwordError
        )

        val messageToShow = message?.takeIf { it.isNotBlank() } ?: fallbackMessage
        if (emailError == null && passwordError == null && !messageToShow.isNullOrBlank()) {
            sendEvent(RegisterEvent.ShowError(messageToShow))
        }
    }

    private fun mapValidationError(message: String): Triple<String?, String?, String?> {
        val emailError = if (message.contains("Email", ignoreCase = true)) message else null
        val passwordError = if (message.contains("Password", ignoreCase = true)) message else null
        val generalError = if (emailError == null && passwordError == null) message else null
        return Triple(emailError, passwordError, generalError)
    }

    private fun mapFallbackMessage(error: RepositoryError): String? {
        return when (error) {
            is RepositoryError.Network -> "Network error. Check your connection."
            is RepositoryError.Unauthorized -> "Unable to create account."
            is RepositoryError.NotFound -> "Account could not be created."
            is RepositoryError.Unknown -> "Unexpected error."
            is RepositoryError.Validation -> null
        }
    }

    private fun sendEvent(event: RegisterEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
