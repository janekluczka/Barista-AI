package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command for updating a `recipes` row with partial fields.
 *
 * @property brewMethodId UUID string.
 * @property coffeeAmount numeric(6,1) mapped to Double.
 * @property waterAmount numeric(6,1) mapped to Double.
 * @property ratioCoffee Coffee part of ratio.
 * @property ratioWater Water part of ratio.
 * @property temperature Brew temperature in Celsius.
 * @property assistantTip Optional assistant note.
 * @property status Lifecycle status.
 */
@Serializable
data class UpdateRecipeCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String? = null,
    @SerialName("coffee_amount")
    val coffeeAmount: Double? = null,
    @SerialName("water_amount")
    val waterAmount: Double? = null,
    @SerialName("ratio_coffee")
    val ratioCoffee: Int? = null,
    @SerialName("ratio_water")
    val ratioWater: Int? = null,
    @SerialName("temperature")
    val temperature: Int? = null,
    @SerialName("assistant_tip")
    val assistantTip: String? = null,
    @SerialName("status")
    val status: RecipeStatus? = null
)
