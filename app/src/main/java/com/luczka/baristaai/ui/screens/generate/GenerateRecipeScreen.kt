package com.luczka.baristaai.ui.screens.generate

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.luczka.baristaai.ui.components.textfields.ClickableOutlinedTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GenerateRecipeRoute(
    viewModel: GenerateRecipeViewModel = hiltViewModel(),
    onEvent: (GenerateRecipeEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is GenerateRecipeEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    GenerateRecipeScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRecipeScreen(
    uiState: GenerateRecipeUiState,
    onAction: (GenerateRecipeAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val selectedMethodName = uiState.selectedBrewMethodName.orEmpty()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Generate recipe") },
                navigationIcon = {
                    IconButton(onClick = { onAction(GenerateRecipeAction.NavigateBack) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                    onClick = { onAction(GenerateRecipeAction.OpenBrewMethodSheet) },
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
                    onValueChange = { onAction(GenerateRecipeAction.UpdateCoffeeAmount(it)) },
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Can regulate temperature")
                    Switch(
                        checked = uiState.canAdjustTemperature,
                        onCheckedChange = { onAction(GenerateRecipeAction.UpdateCanAdjustTemperature(it)) }
                    )
                }

                OutlinedTextField(
                    value = uiState.userComment,
                    onValueChange = { onAction(GenerateRecipeAction.UpdateUserComment(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Comment*") },
                    supportingText = { Text(text = "*Optional") },
                    minLines = 3
                )
            }

            Button(
                onClick = { onAction(GenerateRecipeAction.SubmitRequest) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                enabled = uiState.isSubmitEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(18.dp)
                    )
                } else {
                    Text(text = "Generate")
                }
            }
        }
    }

    if (uiState.isBrewMethodSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(GenerateRecipeAction.DismissBrewMethodSheet) },
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.brewMethods, key = { it.id }) { method ->
                        val isSelected = method.id == uiState.selectedBrewMethodId
                        BottomSheetListItem(
                            headlineText = method.name,
                            trailingContent = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onAction(GenerateRecipeAction.SelectBrewMethod(method.id)) }
                                )
                            },
                            onClick = { onAction(GenerateRecipeAction.SelectBrewMethod(method.id)) }
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
private fun GenerateRecipeScreenPreview() {
    GenerateRecipeScreen(
        uiState = GenerateRecipeUiState(),
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
