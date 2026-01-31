package com.luczka.baristaai.domain.model

data class GenerateRecipesResult(
    val generationRequest: GenerationRequest,
    val recipes: List<Recipe>
)
