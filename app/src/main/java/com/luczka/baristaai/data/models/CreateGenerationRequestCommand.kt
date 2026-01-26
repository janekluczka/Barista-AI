package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGenerationRequestCommand(
    @SerialName("user_id")
    val userId: String, // UUID as string
    @SerialName("brew_method_id")
    val brewMethodId: String, // UUID as string
    @SerialName("coffee_amount")
    val coffeeAmount: Double, // numeric(6,1)
    @SerialName("can_adjust_temperature")
    val canAdjustTemperature: Boolean,
    @SerialName("user_comment")
    val userComment: String? = null
)
