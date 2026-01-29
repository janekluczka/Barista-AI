package com.luczka.baristaai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.luczka.baristaai.ui.screens.home.HomeEvent
import com.luczka.baristaai.ui.screens.home.HomeRoute
import com.luczka.baristaai.ui.screens.login.LoginEvent
import com.luczka.baristaai.ui.screens.login.LoginRoute
import com.luczka.baristaai.ui.screens.profile.ProfileEvent
import com.luczka.baristaai.ui.screens.profile.ProfileRoute
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
                            is HomeEvent.NavigateToRecipeDetail -> navController.navigate(
                                Route.RecipeDetail(recipeId = event.recipeId)
                            )
                            is HomeEvent.ShowError -> Unit
                        }
                    }
                )
            }

            composable<Route.GenerateRecipe> {
            }

            composable<Route.GeneratedRecipes> {
            }

            composable<Route.EditRecipe> {
            }

            composable<Route.RecipeDetail> {
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
