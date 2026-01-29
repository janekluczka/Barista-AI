package com.luczka.baristaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.luczka.baristaai.ui.navigation.AuthStateViewModel
import com.luczka.baristaai.ui.navigation.BaristaAINavHost
import com.luczka.baristaai.ui.theme.BaristaAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authStateViewModel: AuthStateViewModel by viewModels()

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
                BaristaAINavHost(
                    navController = rememberNavController(),
                    authState = authState
                )
            }
        }
    }
}
