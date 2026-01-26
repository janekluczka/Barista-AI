package com.luczka.baristaai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.luczka.baristaai.ui.screens.home.HomeScreen

@Composable
fun BaristaAINavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Graph.Auth,
        modifier = modifier
    ) {
        navigation<Graph.Auth>(startDestination = Route.AuthLanding) {
            composable<Route.AuthLanding> {
            }

            composable<Route.Login> {
            }

            composable<Route.Register> {
            }
        }

        navigation<Graph.App>(startDestination = Route.Home) {
            composable<Route.Home> {
                HomeScreen()
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
