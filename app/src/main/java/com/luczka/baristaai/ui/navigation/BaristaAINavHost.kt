package com.luczka.baristaai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.luczka.baristaai.ui.screens.auth.AuthLandingRoute
import com.luczka.baristaai.ui.screens.home.HomeEvent
import com.luczka.baristaai.ui.screens.home.HomeRoute

@Composable
fun BaristaAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Graph.App,
        modifier = modifier
    ) {
        navigation<Graph.Auth>(startDestination = Route.AuthLanding) {
            composable<Route.AuthLanding> {
                AuthLandingRoute(
                    onLoginClick = { navController.navigate(Route.Login) },
                    onRegisterClick = { navController.navigate(Route.Register) }
                )
            }

            composable<Route.Login> {
            }

            composable<Route.Register> {
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
            }
        }
    }
}
