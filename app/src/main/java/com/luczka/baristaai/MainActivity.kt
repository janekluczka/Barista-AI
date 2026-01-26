package com.luczka.baristaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.luczka.baristaai.ui.navigation.BaristaAINavHost
import com.luczka.baristaai.ui.theme.BaristaAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaristaAITheme {
                BaristaAINavHost(
                    navController = rememberNavController(),
                )
            }
        }
    }
}
