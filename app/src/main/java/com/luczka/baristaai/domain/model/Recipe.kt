package com.luczka.baristaai.domain.model

data class Recipe(
    val id: String,
    val userId: String,
    val generationRequestId: String?,
    val brewMethodId: String,
    val coffeeAmount: Double,
    val waterAmount: Double,
    val ratioCoffee: Int,
    val ratioWater: Int,
    val temperature: Int,
    val assistantTip: String?,
    val status: RecipeStatus,
    val createdAt: String,
    val updatedAt: String
)
