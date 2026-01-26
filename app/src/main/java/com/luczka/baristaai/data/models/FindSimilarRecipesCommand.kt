package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command used to query similar `recipes`.
 *
 * @property brewMethodId UUID string.
 * @property coffeeAmount numeric(6,1) mapped to Double.
 * @property ratioCoffee Coffee part of ratio.
 * @property ratioWater Water part of ratio.
 * @property temperature Brew temperature in Celsius.
 */
@Serializable
data class FindSimilarRecipesCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String,
    @SerialName("coffee_amount")
    val coffeeAmount: Double,
    @SerialName("ratio_coffee")
    val ratioCoffee: Int,
    @SerialName("ratio_water")
    val ratioWater: Int,
    @SerialName("temperature")
    val temperature: Int
)
