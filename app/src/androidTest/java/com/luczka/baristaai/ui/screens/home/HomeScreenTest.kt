package com.luczka.baristaai.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.luczka.baristaai.EmptyComposeActivity
import com.luczka.baristaai.ui.theme.BaristaAITheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration/UI tests for HomeScreen: display and user interactions
 * with given UI state and callback capture.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<EmptyComposeActivity>()

    @Test
    fun homeScreen_displaysAppTitle() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes"),
                        FilterUiState(id = "v60", label = "V60")
                    ),
                    profileEmail = "test@example.com"
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("BaristaAI").assertIsDisplayed()
    }

    @Test
    fun homeScreen_emptyState_displaysNoRecipesMessage() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes")
                    ),
                    recipes = emptyList(),
                    profileEmail = null
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("No recipes yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap + to generate your first recipe.").assertIsDisplayed()
    }

    @Test
    fun homeScreen_loadingState_displaysProgressIndicator() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    isLoading = true,
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes")
                    ),
                    profileEmail = null
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("BaristaAI").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFilterChips() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes"),
                        FilterUiState(id = "v60", label = "V60"),
                        FilterUiState(id = "aeropress", label = "Aeropress")
                    ),
                    profileEmail = null
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("All recipes").assertIsDisplayed()
        composeTestRule.onNodeWithText("V60").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aeropress").assertIsDisplayed()
    }

    @Test
    fun homeScreen_withRecipes_displaysRecipeCards() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes")
                    ),
                    recipes = listOf(
                        RecipeUiState(
                            id = "recipe-1",
                            methodName = "V60",
                            coffeeAmount = 15.0,
                            ratioCoffee = 1,
                            ratioWater = 16,
                            waterAmount = 250.0,
                            temperature = 93
                        )
                    ),
                    profileEmail = null
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("V60").assertIsDisplayed()
        composeTestRule.onNodeWithText("Method").assertIsDisplayed()
        composeTestRule.onNodeWithText("15.0 g").assertIsDisplayed()
        composeTestRule.onNodeWithText("1:16").assertIsDisplayed()
        composeTestRule.onNodeWithText("93°C").assertIsDisplayed()
    }

    @Test
    fun homeScreen_profileSheetVisible_displaysEmailAndLogout() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                    uiState = HomeUiState(
                        filters = listOf(
                            FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes")
                        ),
                        isProfileSheetVisible = true,
                        profileEmail = "user@example.com"
                    ),
                    onAction = {},
                    snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("user@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log out").assertIsDisplayed()
    }

    @Test
    fun homeScreen_errorMessage_displaysError() {
        composeTestRule.setContent {
            BaristaAITheme {
                HomeScreen(
                uiState = HomeUiState(
                    filters = listOf(
                        FilterUiState(id = FilterUiState.ALL_FILTER_ID, label = "All recipes")
                    ),
                    errorMessage = "Check your connection and try again.",
                    profileEmail = null
                ),
                onAction = {},
                snackbarHostState = androidx.compose.material3.SnackbarHostState()
                )
            }
        }
        composeTestRule.onNodeWithText("Check your connection and try again.").assertIsDisplayed()
    }
}
