package com.luczka.baristaai.ui.screens.edit

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.ui.components.BottomSheetListItem
import com.luczka.baristaai.ui.components.ButtonWithLoader
import com.luczka.baristaai.ui.components.textfields.ClickableOutlinedTextField
import com.luczka.baristaai.ui.navigation.EditRecipeMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun EditRecipeRoute(
    viewModel: EditRecipeViewModel = hiltViewModel(),
    onEvent: (EditRecipeEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is EditRecipeEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is EditRecipeEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    EditRecipeScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    uiState: EditRecipeUiState,
    onAction: (EditRecipeAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedMethodName = uiState.selectedBrewMethodName.orEmpty()
    val temperatureInteractionSource = if (uiState.canRegulateTemperature) {
        remember { MutableInteractionSource() }
    } else {
        rememberNoInteractionSource()
    }
    val title = when (uiState.mode) {
        EditRecipeMode.MANUAL -> "Add recipe"
        EditRecipeMode.DRAFT,
        EditRecipeMode.SAVED -> "Edit recipe"
    }
    val submitLabel = when (uiState.mode) {
        EditRecipeMode.MANUAL -> "Save recipe"
        EditRecipeMode.DRAFT,
        EditRecipeMode.SAVED -> "Save changes"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { onAction(EditRecipeAction.NavigateBack) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                ClickableOutlinedTextField(
                    value = selectedMethodName,
                    onClick = { onAction(EditRecipeAction.OpenBrewMethodSheet) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Select method") },
                    isError = uiState.brewMethodError != null,
                    supportingText = {
                        if (uiState.brewMethodError != null) {
                            Text(text = uiState.brewMethodError)
                        }
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Open")
                    }
                )

                OutlinedTextField(
                    value = uiState.coffeeAmountInput,
                    onValueChange = { onAction(EditRecipeAction.UpdateCoffeeAmount(it)) },
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
                        onValueChange = { onAction(EditRecipeAction.UpdateRatioCoffee(it)) },
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
                        onValueChange = { onAction(EditRecipeAction.UpdateRatioWater(it)) },
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
                    onValueChange = { onAction(EditRecipeAction.UpdateWaterAmount(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Water amount (g)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    readOnly = true,
                    isError = uiState.waterAmountError != null,
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
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Water amount info"
                            )
                        }
                    },
                    supportingText = {
                        if (uiState.waterAmountError != null) {
                            Text(text = uiState.waterAmountError)
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Can regulate temperature")
                    Switch(
                        checked = uiState.canRegulateTemperature,
                        enabled = uiState.mode == EditRecipeMode.MANUAL,
                        onCheckedChange = {
                            onAction(EditRecipeAction.UpdateCanRegulateTemperature(it))
                        }
                    )
                }

                OutlinedTextField(
                    value = uiState.temperatureInput,
                    onValueChange = { onAction(EditRecipeAction.UpdateTemperature(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    readOnly = !uiState.canRegulateTemperature,
                    isError = uiState.temperatureError != null,
                    interactionSource = temperatureInteractionSource,
                    supportingText = {
                        if (uiState.temperatureError != null) {
                            Text(text = uiState.temperatureError)
                        }
                    }
                )

                OutlinedTextField(
                    value = uiState.assistantTipInput,
                    onValueChange = { onAction(EditRecipeAction.UpdateAssistantTip(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Comment*") },
                    supportingText = { Text(text = "*Optional") },
                    minLines = 3
                )
            }

            ButtonWithLoader(
                text = submitLabel,
                onClick = { onAction(EditRecipeAction.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                enabled = uiState.isSubmitEnabled,
                isLoading = uiState.isLoading
            )
        }
    }

    if (uiState.isBrewMethodSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(EditRecipeAction.DismissBrewMethodSheet) },
            sheetState = sheetState
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
                                androidx.compose.material3.RadioButton(
                                    selected = isSelected,
                                    onClick = { onAction(EditRecipeAction.SelectBrewMethod(method.id)) }
                                )
                            },
                            onClick = { onAction(EditRecipeAction.SelectBrewMethod(method.id)) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditRecipeScreenPreview() {
    EditRecipeScreen(
        uiState = EditRecipeUiState(mode = EditRecipeMode.MANUAL),
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
