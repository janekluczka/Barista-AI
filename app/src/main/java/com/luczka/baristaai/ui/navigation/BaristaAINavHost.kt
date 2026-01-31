package com.luczka.baristaai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.luczka.baristaai.ui.screens.edit.EditRecipeEvent
import com.luczka.baristaai.ui.screens.edit.EditRecipeRoute
import com.luczka.baristaai.ui.screens.editgeneratedrecipe.EditGeneratedRecipeEvent
import com.luczka.baristaai.ui.screens.editgeneratedrecipe.EditGeneratedRecipeRoute
import com.luczka.baristaai.ui.screens.generatedrecipes.GeneratedRecipesEvent
import com.luczka.baristaai.ui.screens.generatedrecipes.GeneratedRecipesRoute
import com.luczka.baristaai.ui.screens.generatedrecipes.GeneratedRecipesSuccessEvent
import com.luczka.baristaai.ui.screens.generatedrecipes.GeneratedRecipesSuccessRoute
import com.luczka.baristaai.ui.screens.generate.GenerateRecipeEvent
import com.luczka.baristaai.ui.screens.generate.GenerateRecipeRoute
import com.luczka.baristaai.ui.screens.home.HomeEvent
import com.luczka.baristaai.ui.screens.home.HomeRoute
import com.luczka.baristaai.ui.screens.login.LoginEvent
import com.luczka.baristaai.ui.screens.login.LoginRoute
import com.luczka.baristaai.ui.screens.profile.ProfileEvent
import com.luczka.baristaai.ui.screens.profile.ProfileRoute
import com.luczka.baristaai.ui.screens.recipe_detail.RecipeDetailEvent
import com.luczka.baristaai.ui.screens.recipe_detail.RecipeDetailRoute
import com.luczka.baristaai.ui.screens.register.RegisterEvent
import com.luczka.baristaai.ui.screens.register.RegisterRoute

@Composable
fun BaristaAINavHost(
    navController: NavHostController,
    authState: AuthStateUiState,
    modifier: Modifier = Modifier
) {
    val startDestination = if (authState.isAuthenticated) {
        Graph.App
    } else {
        Graph.Auth
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation<Graph.Auth>(startDestination = Route.Login) {
            composable<Route.Login> {
                LoginRoute { event ->
                    when (event) {
                        LoginEvent.NavigateToRegister -> navController.navigate(Route.Register)
                        LoginEvent.NavigateToHome -> {
                            navController.navigate(Route.Home) {
                                popUpTo<Graph.Auth> {
                                    inclusive = true
                                }
                            }
                        }
                        LoginEvent.RequestGoogleSignIn -> Unit
                        is LoginEvent.ShowError -> Unit
                    }
                }
            }

            composable<Route.Register> {
                RegisterRoute { event ->
                    when (event) {
                        RegisterEvent.NavigateToLogin -> navController.navigate(Route.Login)
                        RegisterEvent.NavigateToHome -> {
                            navController.navigate(Route.Home) {
                                popUpTo<Graph.Auth> {
                                    inclusive = true
                                }
                            }
                        }
                        RegisterEvent.RequestGoogleSignIn -> Unit
                        is RegisterEvent.ShowError -> Unit
                    }
                }
            }
        }

        navigation<Graph.App>(startDestination = Route.Home) {
            composable<Route.Home> {
                HomeRoute(
                    onEvent = { event ->
                        when (event) {
                            HomeEvent.NavigateToProfile -> navController.navigate(Route.Profile)
                            HomeEvent.NavigateToGenerate -> navController.navigate(Route.GenerateRecipe)
                            HomeEvent.NavigateToManual -> navController.navigate(
                                Route.EditRecipe(mode = EditRecipeMode.MANUAL)
                            )
                            is HomeEvent.NavigateToRecipeDetail -> navController.navigate(
                                Route.RecipeDetail(recipeId = event.recipeId)
                            )
                            is HomeEvent.ShowError -> Unit
                        }
                    }
                )
            }

            composable<Route.GenerateRecipe> {
                GenerateRecipeRoute(
                    onEvent = { event ->
                        when (event) {
                            GenerateRecipeEvent.NavigateBack -> navController.popBackStack()
                            is GenerateRecipeEvent.NavigateToGeneratedRecipes -> navController.navigate(
                                Route.GeneratedRecipes(requestId = event.requestId)
                            )
                            is GenerateRecipeEvent.ShowError -> Unit
                        }
                    }
                )
            }

            composable<Route.GeneratedRecipes> {
                val route = it.toRoute<Route.GeneratedRecipes>()
                GeneratedRecipesRoute(
                    onEvent = { event ->
                        when (event) {
                            GeneratedRecipesEvent.NavigateBack -> navController.popBackStack()
                            GeneratedRecipesEvent.NavigateToSuccess -> navController.navigate(
                                Route.GeneratedRecipesSuccess
                            )
                            is GeneratedRecipesEvent.NavigateToEditGeneratedRecipe -> navController.navigate(
                                Route.EditGeneratedRecipe(
                                    recipeId = event.recipeId
                                )
                            )
                            is GeneratedRecipesEvent.ShowMessage -> Unit
                        }
                    }
                )
            }

            composable<Route.GeneratedRecipesSuccess> {
                GeneratedRecipesSuccessRoute(
                    onEvent = { event ->
                        when (event) {
                            GeneratedRecipesSuccessEvent.NavigateToHome -> {
                                navController.navigate(Route.Home) {
                                    popUpTo<Route.Home> {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                )
            }

            composable<Route.EditRecipe> {
                EditRecipeRoute(
                    onEvent = { event ->
                        when (event) {
                            EditRecipeEvent.NavigateBack -> navController.popBackStack()
                            EditRecipeEvent.NavigateToHome -> navController.navigate(Route.Home) {
                                popUpTo<Route.Home> {
                                    inclusive = true
                                }
                            }
                            is EditRecipeEvent.ShowError -> Unit
                            is EditRecipeEvent.ShowMessage -> Unit
                        }
                    }
                )
            }

            composable<Route.EditGeneratedRecipe> {
                EditGeneratedRecipeRoute(
                    onEvent = { event ->
                        when (event) {
                            EditGeneratedRecipeEvent.NavigateBack -> navController.popBackStack()
                            EditGeneratedRecipeEvent.NavigateBackWithRefresh -> {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("refreshGeneratedRecipes", System.currentTimeMillis())
                                navController.popBackStack()
                            }
                            is EditGeneratedRecipeEvent.ShowError -> Unit
                            is EditGeneratedRecipeEvent.ShowMessage -> Unit
                        }
                    }
                )
            }

            composable<Route.RecipeDetail> {
                RecipeDetailRoute(
                    onEvent = { event ->
                        when (event) {
                            RecipeDetailEvent.NavigateBack -> navController.popBackStack()
                            RecipeDetailEvent.NavigateToHome -> navController.navigate(Route.Home) {
                                popUpTo<Route.Home> {
                                    inclusive = true
                                }
                            }
                            is RecipeDetailEvent.NavigateToEdit -> navController.navigate(
                                Route.EditRecipe(
                                    mode = EditRecipeMode.SAVED,
                                    recipeId = event.recipeId
                                )
                            )
                            is RecipeDetailEvent.ShowError -> Unit
                            is RecipeDetailEvent.ShowMessage -> Unit
                        }
                    }
                )
            }

            composable<Route.Profile> {
                ProfileRoute(
                    onEvent = { event ->
                        when (event) {
                            ProfileEvent.NavigateBack -> navController.popBackStack()
                            ProfileEvent.NavigateToLogin -> {
                                navController.navigate(Route.Login) {
                                    popUpTo<Route.Home> {
                                        inclusive = true
                                    }
                                }
                            }
                            is ProfileEvent.ShowError -> Unit
                        }
                    }
                )
            }
        }
    }
}
