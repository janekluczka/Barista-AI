package com.luczka.baristaai.ui.screens.recipedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.ui.components.icons.ArrowBackIcon
import com.luczka.baristaai.ui.components.icons.DeleteIcon
import com.luczka.baristaai.ui.components.icons.EditIcon
import com.luczka.baristaai.domain.model.RecipeStatus
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun RecipeDetailsRoute(
    viewModel: RecipeDetailsViewModel = hiltViewModel(),
    onEvent: (RecipeDetailsEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is RecipeDetailsEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is RecipeDetailsEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    RecipeDetailsScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    uiState: RecipeDetailsUiState,
    onAction: (RecipeDetailsAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Recipe details") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecipeDetailsAction.NavigateBack) }) {
                        ArrowBackIcon()
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onAction(RecipeDetailsAction.Edit) },
                        enabled = uiState.recipe != null && !uiState.isLoading
                    ) {
                        EditIcon()
                    }
                    IconButton(
                        onClick = { onAction(RecipeDetailsAction.DeleteClick) },
                        enabled = uiState.recipe != null && !uiState.isLoading
                    ) {
                        DeleteIcon()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val recipe = uiState.recipe
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage ?: "Recipe not found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onAction(RecipeDetailsAction.Retry) }) {
                        Text(text = "Retry")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle(text = "Parameters")
            MetricRow(label = "Method", value = uiState.brewMethodName ?: "Unknown")
            MetricRow(label = "Coffee", value = "${formatAmount(recipe.coffeeAmount)} g")
            MetricRow(label = "Ratio", value = "${recipe.ratioCoffee}:${recipe.ratioWater}")
            MetricRow(label = "Water", value = "${formatAmount(recipe.waterAmount)} g")
            MetricRow(label = "Temperature", value = "${recipe.temperature}°C")

            if (!recipe.assistantTip.isNullOrBlank()) {
                SectionTitle(text = "Assistant tip")
                Text(
                    text = recipe.assistantTip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(RecipeDetailsAction.DismissDelete) },
            title = { Text(text = "Delete recipe?") },
            text = { Text(text = "This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { onAction(RecipeDetailsAction.ConfirmDelete) },
                    enabled = !uiState.isDeleting
                ) {
                    Text(text = if (uiState.isDeleting) "Deleting..." else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RecipeDetailsAction.DismissDelete) }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.getDefault(), "%.1f", value)
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailsPreview() {
    RecipeDetailsScreen(
        uiState = RecipeDetailsUiState(
            recipe = Recipe(
                id = "1",
                userId = "user",
                generationRequestId = null,
                brewMethodId = "v60",
                coffeeAmount = 18.0,
                waterAmount = 270.0,
                ratioCoffee = 1,
                ratioWater = 15,
                temperature = 92,
                assistantTip = "Pour slowly in circles.",
                status = RecipeStatus.Saved,
                createdAt = "2025-01-01T12:00:00Z",
                updatedAt = "2025-01-01T12:00:00Z"
            ),
            brewMethodName = "V60"
        ),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
