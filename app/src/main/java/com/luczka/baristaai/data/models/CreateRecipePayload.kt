package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload for inserting a `recipes` row with `user_id`.
 *
 * @property userId UUID string.
 * @property generationRequestId UUID string.
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
data class CreateRecipePayload(
    @SerialName("user_id")
    val userId: String,
    @SerialName("generation_request_id")
    val generationRequestId: String?,
    @SerialName("brew_method_id")
    val brewMethodId: String,
    @SerialName("coffee_amount")
    val coffeeAmount: Double,
    @SerialName("water_amount")
    val waterAmount: Double,
    @SerialName("ratio_coffee")
    val ratioCoffee: Int,
    @SerialName("ratio_water")
    val ratioWater: Int,
    @SerialName("temperature")
    val temperature: Int,
    @SerialName("assistant_tip")
    val assistantTip: String? = null,
    @SerialName("status")
    val status: RecipeStatus
)
