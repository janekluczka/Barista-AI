package com.luczka.baristaai.ui.screens.home

data class RecipeUiState(
    val id: String,
    val methodName: String,
    val coffeeAmount: Double,
    val ratioCoffee: Int,
    val ratioWater: Int,
    val waterAmount: Double,
    val temperature: Int
)
