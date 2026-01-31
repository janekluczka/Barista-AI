package com.luczka.baristaai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luczka.baristaai.ui.screens.profile.ProfileAction
import com.luczka.baristaai.ui.screens.profile.ProfileEvent
import com.luczka.baristaai.ui.screens.profile.ProfileUiState
import com.luczka.baristaai.ui.screens.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onEvent: (HomeEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileUiState by profileViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLogoutDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.event.collectLatest { event ->
            when (event) {
                is ProfileEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                ProfileEvent.NavigateToLogin -> onEvent(HomeEvent.NavigateToLogin)
                ProfileEvent.NavigateBack -> Unit
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        profileUiState = profileUiState,
        onAction = viewModel::handleAction,
        onLogoutClick = { isLogoutDialogVisible = true },
        snackbarHostState = snackbarHostState
    )

    if (isLogoutDialogVisible) {
        LogoutConfirmationDialog(
            onConfirm = {
                isLogoutDialogVisible = false
                viewModel.handleAction(HomeAction.DismissProfile)
                profileViewModel.handleAction(ProfileAction.ConfirmLogout)
            },
            onDismiss = { isLogoutDialogVisible = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    profileUiState: ProfileUiState,
    onAction: (HomeAction) -> Unit,
    onLogoutClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "BaristaAI") },
                actions = {
                    IconButton(onClick = { onAction(HomeAction.OpenProfile) }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(HomeAction.OpenAddOptions) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add recipe"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterRow(
                filters = uiState.filters,
                selectedFilterId = uiState.selectedFilterId,
                onSelectFilter = { filterId ->
                    onAction(HomeAction.SelectFilter(filterId))
                }
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            RecipeList(
                recipes = uiState.recipes,
                isLoadingMore = uiState.isLoadingMore,
                canLoadMore = uiState.canLoadMore,
                modifier = Modifier.fillMaxSize(),
                onRecipeClick = { recipeId ->
                    onAction(HomeAction.OpenRecipeDetail(recipeId))
                },
                onLoadMore = {
                    onAction(HomeAction.LoadMore)
                }
            )
        }
    }

    if (uiState.isProfileSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onAction(HomeAction.DismissProfile) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User avatar",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = profileUiState.email ?: "Email not available",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (profileUiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                } else {
                    HomeBottomSheetListItem(
                        headlineText = "Log out",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Log out"
                            )
                        },
                        color = MaterialTheme.colorScheme.error,
                        onClick = onLogoutClick
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (uiState.isAddOptionSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onAction(HomeAction.DismissAddOptions) },
            sheetState = sheetState
        ) {
            Text(
                text = "Add recipes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            HomeBottomSheetListItem(
                headlineText = "AI Assisted",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI Assisted"
                    )
                },
                onClick = { onAction(HomeAction.OpenGenerate) }
            )
            HomeBottomSheetListItem(
                headlineText = "Manual",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Manual"
                    )
                },
                onClick = { onAction(HomeAction.OpenManual) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterRow(
    filters: List<FilterUiState>,
    selectedFilterId: String?,
    onSelectFilter: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters, key = { it.id }) { filter ->
            val isSelected = if (filter.id == FilterUiState.ALL_FILTER_ID) {
                selectedFilterId == null
            } else {
                filter.id == selectedFilterId
            }
            FilterChip(
                selected = isSelected,
                onClick = { onSelectFilter(filter.id) },
                label = {
                    Text(
                        text = filter.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<RecipeUiState>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    if (recipes.isEmpty()) {
        Box(
            modifier = modifier.padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No recipes yet.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap + to generate your first recipe.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore, canLoadMore, isLoadingMore) {
        if (shouldLoadMore && canLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                modifier = Modifier.animateItem(),
                onClick = { onRecipeClick(recipe.id) }
            )
        }
        if (isLoadingMore) {
            item(key = "loading-more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeBottomSheetListItem(
    icon: @Composable () -> Unit,
    headlineText: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = headlineText, color = color) },
        leadingContent = icon,
        colors = ListItemDefaults.colors(
            leadingIconColor = color,
            headlineColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

@Composable
private fun RecipeCard(
    recipe: RecipeUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.methodName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MetricRow(label = "Coffee", value = "${formatAmount(recipe.coffeeAmount)} g")
                MetricRow(label = "Ratio", value = "${recipe.ratioCoffee}:${recipe.ratioWater}")
                MetricRow(label = "Water", value = "${formatAmount(recipe.waterAmount)} g")
                MetricRow(label = "Temperature", value = "${recipe.temperature}°C")
            }
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
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.getDefault(), "%.1f", value)
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Log out") },
        text = { Text(text = "Are you sure you want to log out?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Log out")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScreen(
        uiState = HomeUiState(
            filters = listOf(
                FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes"),
                FilterUiState(id = "v60", label = "V60"),
                FilterUiState(id = "aeropress", label = "Aeropress")
            )
        ),
        profileUiState = ProfileUiState(email = "alex@example.com"),
        onAction = {},
        onLogoutClick = {},
        snackbarHostState = SnackbarHostState()
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    HomeScreen(
        uiState = HomeUiState(
            isLoading = true,
            filters = listOf(
                FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes"),
                FilterUiState(id = "v60", label = "V60"),
                FilterUiState(id = "aeropress", label = "Aeropress")
            )
        ),
        profileUiState = ProfileUiState(email = "alex@example.com"),
        onAction = {},
        onLogoutClick = {},
        snackbarHostState = SnackbarHostState()
    )
}
