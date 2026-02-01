package com.luczka.baristaai.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.luczka.baristaai.ui.components.ButtonWithLoader
import com.luczka.baristaai.ui.components.DividerWithText
import com.luczka.baristaai.ui.components.GoogleSignInButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
    onEvent: (RegisterEvent) -> Unit = {}
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
                is RegisterEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                RegisterEvent.RequestGoogleSignIn -> {
                    if (webClientId.isBlank()) {
                        viewModel.handleAction(
                            RegisterAction.ReportGoogleSignInFailure(
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
                                viewModel.handleAction(RegisterAction.SubmitGoogleSignIn(idToken))
                            } catch (_: GetCredentialCancellationException) {
                                viewModel.handleAction(
                                    RegisterAction.ReportGoogleSignInFailure(
                                        "Google Sign-In was cancelled."
                                    )
                                )
                            } catch (e: GetCredentialException) {
                                viewModel.handleAction(
                                    RegisterAction.ReportGoogleSignInFailure(
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

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create account") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RegisterAction.NavigateToLogin) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                GoogleSignInButton(
                    onClick = { onAction(RegisterAction.RequestGoogleSignIn) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DividerWithText(
                    text = "or",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fill in your data.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { onAction(RegisterAction.UpdateEmail(it)) },
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
                    onValueChange = { onAction(RegisterAction.UpdatePassword(it)) },
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.passwordError != null,
                    supportingText = {
                        if (uiState.passwordError != null) {
                            Text(text = uiState.passwordError)
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onAction(RegisterAction.SubmitRegister) }
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { onAction(RegisterAction.UpdateConfirmPassword(it)) },
                    label = { Text(text = "Confirm password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.confirmPasswordError != null,
                    supportingText = {
                        if (uiState.confirmPasswordError != null) {
                            Text(text = uiState.confirmPasswordError)
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onAction(RegisterAction.SubmitRegister) }
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            ButtonWithLoader(
                text = "Create account",
                onClick = { onAction(RegisterAction.SubmitRegister) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                isLoading = uiState.isLoading
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(
        uiState = RegisterUiState(),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
