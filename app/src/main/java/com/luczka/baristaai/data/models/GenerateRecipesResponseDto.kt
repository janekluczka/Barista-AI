package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateRecipesResponseDto(
    @SerialName("generation_request")
    val generationRequest: GenerationRequestDto,
    @SerialName("recipes")
    val recipes: List<RecipeDto>
)
