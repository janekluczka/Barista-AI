package com.luczka.baristaai.presentation.home

import app.cash.turbine.test
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.AuthUser
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.usecase.GetCurrentUserUseCase
import com.luczka.baristaai.domain.usecase.ListBrewMethodsUseCase
import com.luczka.baristaai.domain.usecase.ListRecipesUseCase
import com.luczka.baristaai.domain.usecase.SignOutUseCase
import com.luczka.baristaai.ui.screens.home.HomeAction
import com.luczka.baristaai.ui.screens.home.HomeEvent
import com.luczka.baristaai.ui.screens.home.HomeViewModel
import com.luczka.baristaai.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Integration tests for HomeViewModel: actions, state updates, and events
 * with mocked use cases.
 */
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var listBrewMethodsUseCase: ListBrewMethodsUseCase
    private lateinit var listRecipesUseCase: ListRecipesUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var viewModel: HomeViewModel

    private val defaultBrewMethods = listOf(
        BrewMethod(id = "v60", name = "V60", slug = "v60", createdAt = "2024-01-01"),
        BrewMethod(id = "aeropress", name = "Aeropress", slug = "aeropress", createdAt = "2024-01-01")
    )

    private fun defaultRecipe(id: String = "recipe-1", brewMethodId: String = "v60") = Recipe(
        id = id,
        userId = "user-1",
        generationRequestId = null,
        brewMethodId = brewMethodId,
        coffeeAmount = 15.0,
        waterAmount = 250.0,
        ratioCoffee = 1,
        ratioWater = 16,
        temperature = 93,
        assistantTip = null,
        status = RecipeStatus.Saved,
        createdAt = "2024-01-01",
        updatedAt = "2024-01-01"
    )

    @Before
    fun setup() {
        listBrewMethodsUseCase = mockk(relaxed = true)
        listRecipesUseCase = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)
        signOutUseCase = mockk(relaxed = true)

        coEvery { listBrewMethodsUseCase() } returns RepositoryResult.Success(defaultBrewMethods)
        coEvery { getCurrentUserUseCase() } returns RepositoryResult.Success(
            AuthUser(id = "user-1", email = "test@example.com", displayName = null, avatarUrl = null)
        )
        coEvery { listRecipesUseCase(any(), any(), any()) } returns RepositoryResult.Success(
            listOf(defaultRecipe())
        )

        viewModel = HomeViewModel(
            listBrewMethodsUseCase = listBrewMethodsUseCase,
            listRecipesUseCase = listRecipesUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            signOutUseCase = signOutUseCase
        )
    }

    @Test
    fun `initial load sets filters and recipes and profile email`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(3, state.filters.size)
            assertTrue(state.filters.any { it.id == "all" && it.label == "All recipes" })
            assertTrue(state.filters.any { it.id == "v60" })
            assertTrue(state.filters.any { it.id == "aeropress" })
            assertEquals(1, state.recipes.size)
            assertEquals("recipe-1", state.recipes.first().id)
            assertEquals("test@example.com", state.profileEmail)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectFilter updates selectedFilterId and reloads recipes`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.SelectFilter("v60"))
            val state = awaitItem()
            assertEquals("v60", state.selectedFilterId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectFilter with ALL_FILTER_ID clears selectedFilterId`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.SelectFilter("all"))
            val state = awaitItem()
            assertNull(state.selectedFilterId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenProfile sets isProfileSheetVisible true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenProfile)
            val state = awaitItem()
            assertTrue(state.isProfileSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissProfile sets isProfileSheetVisible false`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenProfile)
            awaitItem()
            viewModel.handleAction(HomeAction.DismissProfile)
            val state = awaitItem()
            assertFalse(state.isProfileSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenAddOptions sets isAddOptionSheetVisible true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenAddOptions)
            val state = awaitItem()
            assertTrue(state.isAddOptionSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissAddOptions sets isAddOptionSheetVisible false`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenAddOptions)
            awaitItem()
            viewModel.handleAction(HomeAction.DismissAddOptions)
            val state = awaitItem()
            assertFalse(state.isAddOptionSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenGenerate emits NavigateToGenerate and closes add options sheet`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenAddOptions)
            awaitItem()
            viewModel.handleAction(HomeAction.OpenGenerate)
            viewModel.event
            val state = awaitItem()
            assertFalse(state.isAddOptionSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.event.test {
            viewModel.handleAction(HomeAction.OpenAddOptions)
            viewModel.handleAction(HomeAction.OpenGenerate)
            val event = awaitItem()
            assertTrue(event is HomeEvent.NavigateToGenerate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenManual emits NavigateToManual and closes add options sheet`() = runTest {
        viewModel.handleAction(HomeAction.OpenAddOptions)
        viewModel.event.test {
            viewModel.handleAction(HomeAction.OpenManual)
            val event = awaitItem()
            assertTrue(event is HomeEvent.NavigateToManual)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenRecipeDetails emits NavigateToRecipeDetails with recipeId`() = runTest {
        viewModel.event.test {
            viewModel.handleAction(HomeAction.OpenRecipeDetails("recipe-123"))
            val event = awaitItem()
            assertTrue(event is HomeEvent.NavigateToRecipeDetails)
            assertEquals("recipe-123", (event as HomeEvent.NavigateToRecipeDetails).recipeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenLogoutDialog sets isLogoutDialogVisible true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenLogoutDialog)
            val state = awaitItem()
            assertTrue(state.isLogoutDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissLogoutDialog sets isLogoutDialogVisible false`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.handleAction(HomeAction.OpenLogoutDialog)
            awaitItem()
            viewModel.handleAction(HomeAction.DismissLogoutDialog)
            val state = awaitItem()
            assertFalse(state.isLogoutDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmLogout on success emits NavigateToLogin`() = runTest {
        coEvery { signOutUseCase() } returns RepositoryResult.Success(Unit)
        viewModel.handleAction(HomeAction.OpenProfile)
        viewModel.handleAction(HomeAction.OpenLogoutDialog)
        viewModel.event.test {
            viewModel.handleAction(HomeAction.ConfirmLogout)
            val event = awaitItem()
            assertTrue(event is HomeEvent.NavigateToLogin)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmLogout on failure emits ShowError`() = runTest {
        coEvery { signOutUseCase() } returns RepositoryResult.Failure(
            RepositoryError.Network("Offline")
        )
        viewModel.handleAction(HomeAction.OpenLogoutDialog)
        viewModel.event.test {
            viewModel.handleAction(HomeAction.ConfirmLogout)
            val event = awaitItem()
            assertTrue(event is HomeEvent.ShowError)
            assertTrue((event as HomeEvent.ShowError).message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadRecipes when brew methods fail sets errorMessage and emits ShowError`() = runTest {
        coEvery { listBrewMethodsUseCase() } returns RepositoryResult.Failure(
            RepositoryError.Network("Failed")
        )
        val viewModelFailure = HomeViewModel(
            listBrewMethodsUseCase = listBrewMethodsUseCase,
            listRecipesUseCase = listRecipesUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            signOutUseCase = signOutUseCase
        )
        viewModelFailure.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.errorMessage != null)
            assertEquals("Failed to load brew methods.", state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadMore appends next page to recipes when canLoadMore`() = runTest {
        val fullPage = (1..20).map { i -> defaultRecipe("recipe-$i") }
        val secondPage = listOf(defaultRecipe("recipe-21"))
        coEvery { listRecipesUseCase(any(), any(), any()) } returnsMany listOf(
            RepositoryResult.Success(fullPage),
            RepositoryResult.Success(secondPage)
        )
        val vm = HomeViewModel(
            listBrewMethodsUseCase = listBrewMethodsUseCase,
            listRecipesUseCase = listRecipesUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            signOutUseCase = signOutUseCase
        )
        vm.uiState.test {
            val afterFirst = awaitItem()
            assertEquals(20, afterFirst.recipes.size)
            assertTrue(afterFirst.canLoadMore)
            vm.handleAction(HomeAction.LoadMore)
            val loadingMoreState = awaitItem()
            assertTrue(loadingMoreState.isLoadingMore)
            val afterMore = awaitItem()
            assertEquals(21, afterMore.recipes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
