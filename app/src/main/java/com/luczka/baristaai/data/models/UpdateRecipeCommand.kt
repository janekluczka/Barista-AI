package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String? = null, // UUID as string
    @SerialName("coffee_amount")
    val coffeeAmount: Double? = null, // numeric(6,1)
    @SerialName("water_amount")
    val waterAmount: Double? = null, // numeric(6,1)
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
