package com.luczka.baristaai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.luczka.baristaai.ui.navigation.AuthStateViewModel
import com.luczka.baristaai.ui.theme.BaristaAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthCheckActivity : ComponentActivity() {
    private val authStateViewModel: AuthStateViewModel by viewModels()
    private val logTag = "AuthCheckActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            authStateViewModel.uiState.value.isLoading
        }
        enableEdgeToEdge()
        setContent {
            val authState by authStateViewModel.uiState.collectAsState()
            BaristaAITheme {
                Box(modifier = Modifier.fillMaxSize())
                LaunchedEffect(authState.isLoading, authState.isAuthenticated) {
                    Log.d(logTag, "Auth state: $authState")
                    if (!authState.isLoading) {
                        if (authState.isAuthenticated) {
                            Log.d(logTag, "Routing to MainAppActivity")
                            startActivity(
                                MainAppActivity.createIntent(this@AuthCheckActivity).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                            )
                        } else {
                            Log.d(logTag, "Routing to AuthActivity")
                            startActivity(
                                AuthActivity.createIntent(this@AuthCheckActivity).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                            )
                        }
                        finish()
                    }
                }
            }
        }
    }
}
