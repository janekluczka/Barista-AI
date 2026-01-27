package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command for updating a `generation_requests` row with partial fields.
 *
 * @property brewMethodId UUID string.
 * @property coffeeAmount numeric(6,1) mapped to Double.
 * @property canAdjustTemperature User can adjust temperature.
 * @property userComment Optional user note.
 */
@Serializable
data class UpdateGenerationRequestCommand(
    @SerialName("brew_method_id")
    val brewMethodId: String? = null,
    @SerialName("coffee_amount")
    val coffeeAmount: Double? = null,
    @SerialName("can_adjust_temperature")
    val canAdjustTemperature: Boolean? = null,
    @SerialName("user_comment")
    val userComment: String? = null
)
