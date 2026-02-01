package com.luczka.baristaai.ui.screens.login

sealed interface LoginAction {
    data class UpdateEmail(val email: String) : LoginAction
    data class UpdatePassword(val password: String) : LoginAction
    data object TogglePasswordVisibility : LoginAction
    data object SubmitLogin : LoginAction
    data object RetrySignIn : LoginAction
    data object RequestGoogleSignIn : LoginAction
    data class SubmitGoogleSignIn(val idToken: String) : LoginAction
    data class ReportGoogleSignInFailure(val message: String) : LoginAction
    data object NavigateToRegister : LoginAction
}
