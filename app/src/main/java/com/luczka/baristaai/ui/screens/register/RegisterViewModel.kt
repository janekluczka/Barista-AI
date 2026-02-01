package com.luczka.baristaai.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.usecase.SignInWithGoogleUseCase
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
    private val signUpUseCase: SignUpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _event: MutableSharedFlow<RegisterEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<RegisterEvent> = _event.asSharedFlow()

    fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UpdateEmail -> updateEmail(action.email)
            is RegisterAction.UpdatePassword -> updatePassword(action.password)
            is RegisterAction.UpdateConfirmPassword -> updateConfirmPassword(action.confirmPassword)
            RegisterAction.SubmitRegister -> submitRegister()
            RegisterAction.RetrySignUp -> submitRegister()
            RegisterAction.RequestGoogleSignIn -> sendEvent(RegisterEvent.RequestGoogleSignIn)
            is RegisterAction.SubmitGoogleSignIn -> submitGoogleSignIn(action.idToken)
            is RegisterAction.ReportGoogleSignInFailure -> reportGoogleSignInFailure(action.message)
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
            passwordError = null,
            confirmPasswordError = null
        )
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    private fun submitRegister() {
        if (_uiState.value.isLoading) {
            return
        }
        val confirmError = validateConfirmPassword(
            _uiState.value.password,
            _uiState.value.confirmPassword
        )
        if (confirmError != null) {
            _uiState.value = _uiState.value.copy(
                passwordError = confirmError,
                confirmPasswordError = confirmError
            )
            sendEvent(RegisterEvent.ShowError(confirmError))
            return
        }
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null
        )

        viewModelScope.launch {
            when (val result = signUpUseCase(_uiState.value.email, _uiState.value.password)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(RegisterEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> handleError(
                    result.error,
                    if (result.error.isRetryable) RegisterAction.RetrySignUp else null
                )
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
            passwordError = null,
            confirmPasswordError = null
        )

        viewModelScope.launch {
            when (val result = signInWithGoogleUseCase(idToken)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    sendEvent(RegisterEvent.NavigateToHome)
                }
                is RepositoryResult.Failure -> handleError(result.error, retryAction = null)
            }
        }
    }

    private fun reportGoogleSignInFailure(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false)
        if (message.isNotBlank()) {
            sendEvent(RegisterEvent.ShowError(message))
        }
    }

    private fun handleError(error: RepositoryError, retryAction: RegisterAction? = null) {
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
            passwordError = passwordError,
            confirmPasswordError = null
        )

        val messageToShow = message?.takeIf { it.isNotBlank() } ?: fallbackMessage
        if (emailError == null && passwordError == null && !messageToShow.isNullOrBlank()) {
            sendEvent(RegisterEvent.ShowError(messageToShow, retryAction))
        }
    }

    private fun mapValidationError(message: String): Triple<String?, String?, String?> {
        val emailError = if (message.contains("Email", ignoreCase = true)) message else null
        val passwordError = if (message.contains("Password", ignoreCase = true)) message else null
        val generalError = if (emailError == null && passwordError == null) message else null
        return Triple(emailError, passwordError, generalError)
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) {
            return "Confirm your password."
        }
        if (password != confirmPassword) {
            return "Passwords do not match."
        }
        return null
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
