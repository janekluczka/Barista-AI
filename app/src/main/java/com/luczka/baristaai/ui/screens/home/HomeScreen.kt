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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onEvent: (HomeEvent) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> onEvent(event)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
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

    if (uiState.isAddOptionSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onAction(HomeAction.DismissAddOptions) },
            sheetState = sheetState
        ) {
            Text(
                text = "Add recipe",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            ListItem(
                headlineContent = { Text(text = "AI assisted") },
                supportingContent = { Text(text = "Generate recipe with AI") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(HomeAction.OpenGenerate) }
            )
            ListItem(
                headlineContent = { Text(text = "Manual") },
                supportingContent = { Text(text = "Create recipe by hand") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(HomeAction.OpenManual) }
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
            RecipePlaceholderItem(
                recipe = recipe,
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
private fun RecipePlaceholderItem(
    recipe: RecipeUiState,
    onClick: () -> Unit
) {
    // TODO: Replace with real recipe card component.
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = recipe.methodId, style = MaterialTheme.typography.bodyMedium)
                Text(text = recipe.status, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
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
        onAction = {},
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
        onAction = {},
        snackbarHostState = SnackbarHostState()
    )
}
