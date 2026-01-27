package com.luczka.baristaai.domain.model

data class GenerationRequest(
    val id: String,
    val userId: String,
    val brewMethodId: String,
    val coffeeAmount: Double,
    val canAdjustTemperature: Boolean,
    val userComment: String?,
    val createdAt: String
)
