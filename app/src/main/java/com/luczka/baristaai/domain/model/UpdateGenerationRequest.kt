package com.luczka.baristaai.domain.model

data class UpdateGenerationRequest(
    val brewMethodId: String? = null,
    val coffeeAmount: Double? = null,
    val canAdjustTemperature: Boolean? = null,
    val userComment: String? = null
)
