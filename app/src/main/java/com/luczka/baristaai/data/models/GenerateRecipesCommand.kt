package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateRecipesCommand(
    @SerialName("generation_request_id")
    val generationRequestId: String
)
