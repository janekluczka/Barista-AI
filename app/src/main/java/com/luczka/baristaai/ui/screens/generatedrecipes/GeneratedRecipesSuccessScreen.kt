package com.luczka.baristaai.ui.screens.generatedrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luczka.baristaai.ui.components.icons.CheckCircleIcon
import kotlinx.coroutines.delay

sealed interface GeneratedRecipesSuccessEvent {
    data object NavigateToHome : GeneratedRecipesSuccessEvent
}

@Composable
fun GeneratedRecipesSuccessRoute(
    onEvent: (GeneratedRecipesSuccessEvent) -> Unit = {}
) {
    GeneratedRecipesSuccessScreen(
        onBackToHome = { onEvent(GeneratedRecipesSuccessEvent.NavigateToHome) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedRecipesSuccessScreen(
    onBackToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(AUTO_CLOSE_DELAY_MS)
        onBackToHome()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "All set") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CheckCircleIcon(
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Selections saved",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Your recipes are ready on the home list.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            Button(onClick = onBackToHome) {
                Text(text = "Back to home")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GeneratedRecipesSuccessPreview() {
    GeneratedRecipesSuccessScreen(onBackToHome = {})
}

private const val AUTO_CLOSE_DELAY_MS: Long = 5_000
