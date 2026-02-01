package com.luczka.baristaai.ui.screens.register

sealed interface RegisterAction {
    data class UpdateEmail(val email: String) : RegisterAction
    data class UpdatePassword(val password: String) : RegisterAction
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterAction
    data object SubmitRegister : RegisterAction
    data object RetrySignUp : RegisterAction
    data object RequestGoogleSignIn : RegisterAction
    data class SubmitGoogleSignIn(val idToken: String) : RegisterAction
    data class ReportGoogleSignInFailure(val message: String) : RegisterAction
    data object NavigateToLogin : RegisterAction
}
