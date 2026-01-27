package com.luczka.baristaai.domain.model

data class UpdateRecipe(
    val brewMethodId: String? = null,
    val coffeeAmount: Double? = null,
    val waterAmount: Double? = null,
    val ratioCoffee: Int? = null,
    val ratioWater: Int? = null,
    val temperature: Int? = null,
    val assistantTip: String? = null,
    val status: RecipeStatus? = null
)
