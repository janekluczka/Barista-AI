package com.luczka.baristaai.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.usecase.SignInUseCase
import com.luczka.baristaai.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _event: MutableSharedFlow<LoginEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<LoginEvent> = _event.asSharedFlow()

    fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateEmail(action.email)
            is LoginAction.UpdatePassword -> updatePassword(action.password)
            LoginAction.SubmitLogin -> submitLogin()
            LoginAction.RequestGoogleSignIn -> sendEvent(LoginEvent.RequestGoogleSignIn)
            is LoginAction.SubmitGoogleSignIn -> submitGoogleSignIn(action.idToken)
            is LoginAction.ReportGoogleSignInFailure -> reportGoogleSignInFailure(action.message)
            LoginAction.NavigateToRegister -> sendEvent(LoginEvent.NavigateToRegister)
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

    private fun submitLogin() {
        if (_uiState.value.isLoading) {
            return
        }
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            emailError = null,
            passwordError = null
        )

        viewModelScope.launch {
            when (val result = signInUseCase(_uiState.value.email, _uiState.value.password)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(LoginEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun submitGoogleSignIn(idToken: String) {
        if (_uiState.value.isLoading) {
            return
        }
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            emailError = null,
            passwordError = null
        )

        viewModelScope.launch {
            when (val result = signInWithGoogleUseCase(idToken)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(LoginEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun reportGoogleSignInFailure(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false)
        if (message.isNotBlank()) {
            sendEvent(LoginEvent.ShowError(message))
        }
    }

    private fun handleError(error: RepositoryError) {
        val (emailError, passwordError, message) = when (error) {
            is RepositoryError.Validation -> mapValidationError(error.message)
            is RepositoryError.Network -> Triple(null, null, error.message)
            is RepositoryError.Unauthorized -> Triple(null, null, error.message)
            is RepositoryError.NotFound -> Triple(null, null, error.message)
            is RepositoryError.Unknown -> Triple(null, null, error.message)
        }
        val fallbackMessage = mapFallbackMessage(error)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            emailError = emailError,
            passwordError = passwordError
        )

        val messageToShow = message?.takeIf { it.isNotBlank() } ?: fallbackMessage
        if (emailError == null && passwordError == null && !messageToShow.isNullOrBlank()) {
            sendEvent(LoginEvent.ShowError(messageToShow))
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
            is RepositoryError.Unauthorized -> "Invalid email or password."
            is RepositoryError.NotFound -> "Account not found."
            is RepositoryError.Unknown -> "Unexpected error."
            is RepositoryError.Validation -> null
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
