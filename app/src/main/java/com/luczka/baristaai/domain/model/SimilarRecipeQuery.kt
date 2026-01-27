package com.luczka.baristaai.domain.model

data class SimilarRecipeQuery(
    val brewMethodId: String,
    val coffeeAmount: Double,
    val ratioCoffee: Int,
    val ratioWater: Int,
    val temperature: Int,
    val toleranceCoffee: Double = 2.0,
    val toleranceRatio: Int = 1,
    val toleranceTemperature: Int = 5
)
