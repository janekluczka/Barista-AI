package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindSimilarRecipesCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String, // UUID as string
    @SerialName("coffee_amount")
    val coffeeAmount: Double, // numeric(6,1)
    @SerialName("ratio_coffee")
    val ratioCoffee: Int,
    @SerialName("ratio_water")
    val ratioWater: Int,
    @SerialName("temperature")
    val temperature: Int
)
