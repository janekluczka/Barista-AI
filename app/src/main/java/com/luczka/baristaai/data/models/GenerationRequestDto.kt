package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generation request record from `generation_requests`.
 *
 * @property id UUID string.
 * @property userId UUID string.
 * @property brewMethodId UUID string.
 * @property coffeeAmount numeric(6,1) mapped to Double.
 * @property canAdjustTemperature User can adjust temperature.
 * @property userComment Optional user note.
 * @property createdAt timestamptz ISO-8601 string.
 */
@Serializable
data class GenerationRequestDto(
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("brew_method_id")
    val brewMethodId: String,
    @SerialName("coffee_amount")
    val coffeeAmount: Double,
    @SerialName("can_adjust_temperature")
    val canAdjustTemperature: Boolean,
    @SerialName("user_comment")
    val userComment: String?,
    @SerialName("created_at")
    val createdAt: String
)
