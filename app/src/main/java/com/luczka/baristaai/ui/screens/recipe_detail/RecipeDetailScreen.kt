package com.luczka.baristaai.ui.screens.recipe_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecipeDetailRoute(
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onEvent: (RecipeDetailEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is RecipeDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is RecipeDetailEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    RecipeDetailScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    uiState: RecipeDetailUiState,
    onAction: (RecipeDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Recipe details") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecipeDetailAction.NavigateBack) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onAction(RecipeDetailAction.Edit) },
                        enabled = uiState.recipe != null && !uiState.isLoading
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(
                        onClick = { onAction(RecipeDetailAction.DeleteClick) },
                        enabled = uiState.recipe != null && !uiState.isLoading
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
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
                    Button(onClick = { onAction(RecipeDetailAction.Retry) }) {
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
            SectionTitle(text = "Brew method")
            Text(text = uiState.brewMethodName ?: recipe.brewMethodId)

            SectionTitle(text = "Parameters")
            DetailRow(label = "Coffee", value = "${formatAmount(recipe.coffeeAmount)} g")
            DetailRow(label = "Water", value = "${formatAmount(recipe.waterAmount)} g")
            DetailRow(
                label = "Ratio",
                value = "${recipe.ratioCoffee}:${recipe.ratioWater}"
            )
            DetailRow(label = "Temperature", value = "${recipe.temperature}°C")

            if (!recipe.assistantTip.isNullOrBlank()) {
                SectionTitle(text = "Assistant tip")
                Text(
                    text = recipe.assistantTip.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionTitle(text = "Meta")
            DetailRow(label = "Status", value = recipe.status.toLabel())
            DetailRow(label = "Created", value = formatDateTime(recipe.createdAt))
            DetailRow(label = "Updated", value = formatDateTime(recipe.updatedAt))
        }
    }

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(RecipeDetailAction.DismissDelete) },
            title = { Text(text = "Delete recipe?") },
            text = { Text(text = "This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { onAction(RecipeDetailAction.ConfirmDelete) },
                    enabled = !uiState.isDeleting
                ) {
                    Text(text = if (uiState.isDeleting) "Deleting..." else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RecipeDetailAction.DismissDelete) }) {
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
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun RecipeStatus.toLabel(): String {
    return when (this) {
        RecipeStatus.Draft -> "Draft"
        RecipeStatus.Saved -> "Saved"
        RecipeStatus.Edited -> "Edited"
        RecipeStatus.Rejected -> "Rejected"
        RecipeStatus.Deleted -> "Deleted"
    }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.getDefault(), "%.1f", value)
}

private fun formatDateTime(value: String): String {
    return runCatching {
        val instant = Instant.parse(value)
        val zoned = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.format(zoned)
    }.getOrDefault(value)
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailPreview() {
    RecipeDetailScreen(
        uiState = RecipeDetailUiState(
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
