package com.luczka.baristaai.domain.model

data class CreateGenerationRequest(
    val brewMethodId: String,
    val coffeeAmount: Double,
    val canAdjustTemperature: Boolean,
    val userComment: String? = null
)
