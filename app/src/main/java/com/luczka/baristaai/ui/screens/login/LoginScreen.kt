package com.luczka.baristaai.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.luczka.baristaai.BuildConfig
import com.luczka.baristaai.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onEvent: (LoginEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is LoginEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                LoginEvent.RequestGoogleSignIn -> {
                    if (webClientId.isBlank()) {
                        viewModel.handleAction(
                            LoginAction.ReportGoogleSignInFailure(
                                "Google Sign-In is not configured."
                            )
                        )
                    } else {
                        scope.launch {
                            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(signInWithGoogleOption)
                                .build()

                            try {
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context
                                )
                                val googleIdTokenCredential = GoogleIdTokenCredential
                                    .createFrom(result.credential.data)
                                val idToken = googleIdTokenCredential.idToken
                                viewModel.handleAction(LoginAction.SubmitGoogleSignIn(idToken))
                            } catch (e: GetCredentialCancellationException) {
                                viewModel.handleAction(
                                    LoginAction.ReportGoogleSignInFailure(
                                        "Google Sign-In was cancelled."
                                    )
                                )
                            } catch (e: GetCredentialException) {
                                viewModel.handleAction(
                                    LoginAction.ReportGoogleSignInFailure(
                                        "Google Sign-In failed: ${e.message}"
                                    )
                                )
                            }
                        }
                    }
                }
                else -> onEvent(event)
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to BaristaAI",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your guide for daily brews.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onAction(LoginAction.UpdateEmail(it)) },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.emailError != null,
                supportingText = {
                    if (uiState.emailError != null) {
                        Text(text = uiState.emailError)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onAction(LoginAction.UpdatePassword(it)) },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.passwordError != null,
                supportingText = {
                    if (uiState.passwordError != null) {
                        Text(text = uiState.passwordError)
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    val icon = if (uiState.isPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }
                    val description = if (uiState.isPasswordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                    IconButton(onClick = { onAction(LoginAction.TogglePasswordVisibility) }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onAction(LoginAction.SubmitLogin) }
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onAction(LoginAction.SubmitLogin) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(18.dp)
                    )
                } else {
                    Text(text = "Log in")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onAction(LoginAction.RequestGoogleSignIn) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sign in with Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account?")
                TextButton(onClick = { onAction(LoginAction.NavigateToRegister) }) {
                    Text(text = "Create one")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginUiState(),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
