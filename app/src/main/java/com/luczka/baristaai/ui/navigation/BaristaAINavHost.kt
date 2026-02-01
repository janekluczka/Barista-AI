package com.luczka.baristaai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.luczka.baristaai.ui.screens.edit.EditRecipeEvent
import com.luczka.baristaai.ui.screens.edit.EditRecipeRoute
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
import com.luczka.baristaai.ui.screens.recipedetails.RecipeDetailsEvent
import com.luczka.baristaai.ui.screens.recipedetails.RecipeDetailsRoute
import com.luczka.baristaai.ui.screens.register.RegisterEvent
import com.luczka.baristaai.ui.screens.register.RegisterRoute

@Composable
fun AuthNavHost(
    navController: NavHostController,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginRoute { event ->
                when (event) {
                    LoginEvent.NavigateToRegister -> navController.navigate(Route.Register)
                    LoginEvent.NavigateToHome -> onAuthSuccess()
                    LoginEvent.RequestGoogleSignIn -> Unit
                    is LoginEvent.ShowError -> Unit
                }
            }
        }

        composable<Route.Register> {
            RegisterRoute { event ->
                when (event) {
                    RegisterEvent.NavigateToLogin -> navController.navigate(Route.Login)
                    RegisterEvent.NavigateToHome -> onAuthSuccess()
                    RegisterEvent.RequestGoogleSignIn -> Unit
                    is RegisterEvent.ShowError -> Unit
                }
            }
        }
    }
}

@Composable
fun MainAppNavHost(
    navController: NavHostController,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeRoute(
                onEvent = { event ->
                    when (event) {
                        HomeEvent.NavigateToGenerate -> navController.navigate(Route.GenerateRecipe)
                        HomeEvent.NavigateToManual -> navController.navigate(
                            Route.EditRecipe(mode = EditRecipeMode.MANUAL)
                        )
                        is HomeEvent.NavigateToRecipeDetails -> navController.navigate(
                            Route.RecipeDetails(recipeId = event.recipeId)
                        )
                        HomeEvent.NavigateToLogin -> onNavigateToAuth()
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
            GeneratedRecipesRoute(
                onEvent = { event ->
                    when (event) {
                        GeneratedRecipesEvent.NavigateBack -> navController.popBackStack()
                        GeneratedRecipesEvent.NavigateToSuccess -> navController.navigate(
                            Route.GeneratedRecipesSuccess
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


        composable<Route.RecipeDetails> {
            RecipeDetailsRoute(
                onEvent = { event ->
                    when (event) {
                        RecipeDetailsEvent.NavigateBack -> navController.popBackStack()
                        RecipeDetailsEvent.NavigateToHome -> navController.navigate(Route.Home) {
                            popUpTo<Route.Home> {
                                inclusive = true
                            }
                        }
                        is RecipeDetailsEvent.NavigateToEdit -> navController.navigate(
                            Route.EditRecipe(
                                mode = EditRecipeMode.SAVED,
                                recipeId = event.recipeId
                            )
                        )
                        is RecipeDetailsEvent.ShowError -> Unit
                        is RecipeDetailsEvent.ShowMessage -> Unit
                    }
                }
            )
        }

        composable<Route.Profile> {
            ProfileRoute(
                onEvent = { event ->
                    when (event) {
                        ProfileEvent.NavigateBack -> navController.popBackStack()
                        ProfileEvent.NavigateToLogin -> onNavigateToAuth()
                        is ProfileEvent.ShowError -> Unit
                    }
                }
            )
        }
    }
}
