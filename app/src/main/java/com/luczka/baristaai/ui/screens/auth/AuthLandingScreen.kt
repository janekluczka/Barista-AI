package com.luczka.baristaai.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AuthLandingRoute(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    AuthLandingScreen(
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun AuthLandingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to BaristaAI",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create and save coffee recipes tailored to you.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Log in")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create account")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthLandingScreenPreview() {
    AuthLandingScreen(onLoginClick = {}, onRegisterClick = {})
}
