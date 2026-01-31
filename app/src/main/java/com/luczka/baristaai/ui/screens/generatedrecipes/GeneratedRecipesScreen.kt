package com.luczka.baristaai.ui.screens.generatedrecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.ui.components.textfields.ClickableOutlinedTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GeneratedRecipesRoute(
    viewModel: GeneratedRecipesViewModel = hiltViewModel(),
    onEvent: (GeneratedRecipesEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is GeneratedRecipesEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    GeneratedRecipesScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedRecipesScreen(
    uiState: GeneratedRecipesUiState,
    onAction: (GeneratedRecipesAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val isConfirmEnabled = uiState.recipes.isNotEmpty() &&
        uiState.recipes.all { it.selection != RecipeSelection.None } &&
        !uiState.isSubmitting &&
        !uiState.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Generated recipes") },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(GeneratedRecipesAction.ShowAbortDialog) },
                        enabled = !uiState.isSubmitting
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.recipes.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { onAction(GeneratedRecipesAction.ConfirmSelections) },
                        enabled = isConfirmEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(20.dp)
                                    .padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                            Text(text = "Saving...")
                        } else {
                            Text(text = "Confirm selections")
                        }
                    }
                }
            }
        }
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

        if (uiState.recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No generated recipes yet.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Request id: ${uiState.requestId.orEmpty()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.recipes, key = { it.id }) { recipe ->
                GeneratedRecipeCard(
                    recipe = recipe,
                    onSelectionChange = { selection ->
                        onAction(GeneratedRecipesAction.ToggleSelection(recipe.id, selection))
                    },
                    onEdit = { onAction(GeneratedRecipesAction.EditRecipe(recipe.id)) }
                )
            }
        }
    }

    if (uiState.isAbortDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(GeneratedRecipesAction.DismissAbortDialog) },
            title = { Text(text = "Abort acceptance?") },
            text = { Text(text = "You can return later to your drafts.") },
            confirmButton = {
                TextButton(onClick = { onAction(GeneratedRecipesAction.ConfirmAbort) }) {
                    Text(text = "Abort")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(GeneratedRecipesAction.DismissAbortDialog) }) {
                    Text(text = "Stay")
                }
            }
        )
    }

    if (uiState.isEditSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(GeneratedRecipesAction.DismissEditSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit generated recipe",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Brew method", style = MaterialTheme.typography.titleSmall)
                ClickableOutlinedTextField(
                    value = uiState.selectedBrewMethodName.orEmpty(),
                    onClick = { onAction(GeneratedRecipesAction.OpenEditBrewMethodSheet) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Select method") },
                    isError = uiState.brewMethodError != null,
                    supportingText = {
                        if (uiState.brewMethodError != null) {
                            Text(text = uiState.brewMethodError)
                        }
                    }
                )
                OutlinedTextField(
                    value = uiState.coffeeAmountInput,
                    onValueChange = { onAction(GeneratedRecipesAction.UpdateEditCoffeeAmount(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Coffee amount (g)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = uiState.coffeeAmountError != null,
                    supportingText = {
                        if (uiState.coffeeAmountError != null) {
                            Text(text = uiState.coffeeAmountError)
                        }
                    }
                )
                Text(text = "Ratio coffee : Ratio water", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.ratioCoffeeInput,
                        onValueChange = { onAction(GeneratedRecipesAction.UpdateEditRatioCoffee(it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Ratio coffee") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = uiState.ratioCoffeeError != null,
                        supportingText = {
                            if (uiState.ratioCoffeeError != null) {
                                Text(text = uiState.ratioCoffeeError)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = uiState.ratioWaterInput,
                        onValueChange = { onAction(GeneratedRecipesAction.UpdateEditRatioWater(it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Ratio water") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = uiState.ratioWaterError != null,
                        supportingText = {
                            if (uiState.ratioWaterError != null) {
                                Text(text = uiState.ratioWaterError)
                            }
                        }
                    )
                }
                OutlinedTextField(
                    value = uiState.waterAmountInput,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Water amount (g)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    enabled = false,
                    isError = uiState.waterAmountError != null,
                    supportingText = {
                        if (uiState.waterAmountError != null) {
                            Text(text = uiState.waterAmountError)
                        }
                    }
                )
                OutlinedTextField(
                    value = uiState.temperatureInput,
                    onValueChange = { onAction(GeneratedRecipesAction.UpdateEditTemperature(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = uiState.temperatureError != null,
                    supportingText = {
                        if (uiState.temperatureError != null) {
                            Text(text = uiState.temperatureError)
                        }
                    }
                )
                OutlinedTextField(
                    value = uiState.assistantTipInput,
                    onValueChange = { onAction(GeneratedRecipesAction.UpdateEditAssistantTip(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Assistant tip (optional)") },
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAction(GeneratedRecipesAction.SubmitEdit) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isEditSubmitEnabled && !uiState.isEditLoading
                ) {
                    Text(text = "Save changes")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (uiState.isEditBrewMethodSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(GeneratedRecipesAction.DismissEditBrewMethodSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Text(
                text = "Select brew method",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            if (uiState.brewMethods.isEmpty()) {
                Text(
                    text = "No brew methods available.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.brewMethods, key = { it.id }) { method ->
                        val isSelected = method.id == uiState.selectedBrewMethodId
                        ListItem(
                            headlineContent = { Text(text = method.name) },
                            supportingContent = { Text(text = method.slug) },
                            trailingContent = {
                                androidx.compose.material3.RadioButton(
                                    selected = isSelected,
                                    onClick = { onAction(GeneratedRecipesAction.SelectEditBrewMethod(method.id)) }
                                )
                            },
                            modifier = Modifier.clickable {
                                onAction(GeneratedRecipesAction.SelectEditBrewMethod(method.id))
                            }
                        )
                        Divider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GeneratedRecipeCard(
    recipe: GeneratedRecipeCardUiState,
    onSelectionChange: (RecipeSelection) -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coffee: ${recipe.coffeeAmount} • Water: ${recipe.waterAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ratio: ${recipe.ratio} • Temp: ${recipe.temperature}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (!recipe.assistantTip.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.assistantTip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAccepted = recipe.selection == RecipeSelection.Accept
                val isRejected = recipe.selection == RecipeSelection.Reject

                OutlinedSelectionButton(
                    label = "Accept",
                    isSelected = isAccepted,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onSelectionChange(RecipeSelection.Accept) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedSelectionButton(
                    label = "Reject",
                    isSelected = isRejected,
                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                    selectedContentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = { onSelectionChange(RecipeSelection.Reject) },
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(
                onClick = onEdit,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Edit")
            }
        }
    }
}

@Composable
private fun OutlinedSelectionButton(
    label: String,
    isSelected: Boolean,
    selectedContainerColor: androidx.compose.ui.graphics.Color,
    selectedContentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isSelected) selectedContainerColor else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) selectedContentColor else MaterialTheme.colorScheme.onSurface
    )
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier
    ) {
        Text(text = label)
    }
}

@Preview(showBackground = true)
@Composable
private fun GeneratedRecipesScreenPreview() {
    GeneratedRecipesScreen(
        uiState = GeneratedRecipesUiState(
            requestId = "request_123",
            recipes = listOf(
                GeneratedRecipeCardUiState(
                    id = "1",
                    title = "V60 Balanced",
                    coffeeAmount = "18 g",
                    waterAmount = "300 g",
                    ratio = "1:16",
                    temperature = "94°C",
                    assistantTip = "Pour in slow circles for even extraction.",
                    selection = RecipeSelection.Accept
                )
            )
        ),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
