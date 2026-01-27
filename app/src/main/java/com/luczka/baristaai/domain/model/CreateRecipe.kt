package com.luczka.baristaai.domain.model

data class CreateRecipe(
    val generationRequestId: String?,
    val brewMethodId: String,
    val coffeeAmount: Double,
    val waterAmount: Double,
    val ratioCoffee: Int,
    val ratioWater: Int,
    val temperature: Int,
    val assistantTip: String? = null,
    val status: RecipeStatus
)
