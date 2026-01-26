package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command for inserting a `generation_requests` row.
 *
 * @property brewMethodId UUID string.
 * @property coffeeAmount numeric(6,1) mapped to Double.
 * @property canAdjustTemperature User can adjust temperature.
 * @property userComment Optional user note.
 */
@Serializable
data class CreateGenerationRequestCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String,
    @SerialName("coffee_amount")
    val coffeeAmount: Double,
    @SerialName("can_adjust_temperature")
    val canAdjustTemperature: Boolean,
    @SerialName("user_comment")
    val userComment: String? = null
)
