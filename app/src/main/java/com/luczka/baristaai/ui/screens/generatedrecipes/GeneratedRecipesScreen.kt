package com.luczka.baristaai.ui.screens.generatedrecipes

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.ui.components.BottomSheetListItem
import com.luczka.baristaai.ui.components.ButtonWithLoader
import com.luczka.baristaai.ui.components.icons.ArrowDropDownIcon
import com.luczka.baristaai.ui.components.icons.CloseIcon
import com.luczka.baristaai.ui.components.icons.EditOutlinedIcon
import com.luczka.baristaai.ui.components.icons.InfoIcon
import com.luczka.baristaai.ui.components.textfields.ClickableOutlinedTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

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
                is GeneratedRecipesEvent.ShowError -> {
                    val result = if (event.retryAction != null) {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = "Retry",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                    } else {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    if (result == SnackbarResult.ActionPerformed && event.retryAction != null) {
                        viewModel.handleAction(event.retryAction)
                    }
                }
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
                        CloseIcon()
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
                    ButtonWithLoader(
                        text = "Confirm selections",
                        onClick = { onAction(GeneratedRecipesAction.ConfirmSelections) },
                        enabled = isConfirmEnabled,
                        isLoading = uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    )
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 12.dp,
                bottom = padding.calculateBottomPadding() + 12.dp
            )
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
                    },
                    trailingIcon = {
                        ArrowDropDownIcon()
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
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
                    readOnly = true,
                    interactionSource = rememberNoInteractionSource(),
                    trailingIcon = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(text = "Calculated from coffee amount and ratio.")
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            InfoIcon()
                        }
                    },
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
                    label = { Text(text = "Comment*") },
                    supportingText = { Text(text = "*Optional") },
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
                        BottomSheetListItem(
                            headlineText = method.name,
                            trailingContent = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onAction(GeneratedRecipesAction.SelectEditBrewMethod(method.id)) }
                                )
                            },
                            onClick = { onAction(GeneratedRecipesAction.SelectEditBrewMethod(method.id)) }
                        )
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
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MetricRow(label = "Coffee", value = recipe.coffeeAmount)
                MetricRow(label = "Ratio", value = recipe.ratio)
                MetricRow(label = "Water", value = recipe.waterAmount)
                MetricRow(label = "Temperature", value = recipe.temperature)
            }
            if (!recipe.assistantTip.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.assistantTip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            GeneratedRecipeActions(
                recipe = recipe,
                onSelectionChange = onSelectionChange,
                onEdit = onEdit
            )
        }
    }
}

@Composable
private fun GeneratedRecipeActions(
    recipe: GeneratedRecipeCardUiState,
    onSelectionChange: (RecipeSelection) -> Unit,
    onEdit: () -> Unit
) {
    val isAccepted = recipe.selection == RecipeSelection.Accept
    val isRejected = recipe.selection == RecipeSelection.Reject

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        OutlinedIconButton(onClick = onEdit) {
            EditOutlinedIcon()
        }
    }
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
                ),
                GeneratedRecipeCardUiState(
                    id = "2",
                    title = "Aeropress Bright",
                    coffeeAmount = "15 g",
                    waterAmount = "240 g",
                    ratio = "1:16",
                    temperature = "92°C",
                    assistantTip = "Press slowly to avoid bitterness.",
                    selection = RecipeSelection.Reject
                ),
                GeneratedRecipeCardUiState(
                    id = "3",
                    title = "Chemex Clean",
                    coffeeAmount = "30 g",
                    waterAmount = "500 g",
                    ratio = "1:17",
                    temperature = "93°C",
                    assistantTip = "Keep the slurry level steady.",
                    selection = RecipeSelection.None
                )
            )
        ),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}

@Composable
private fun rememberNoInteractionSource(): MutableInteractionSource {
    return remember {
        object : MutableInteractionSource {
            override val interactions = emptyFlow<Interaction>()

            override suspend fun emit(interaction: Interaction) {
                // No-op to avoid emitting interactions.
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return false
            }
        }
    }
}
