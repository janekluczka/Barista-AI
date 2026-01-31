package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.models.GenerateRecipesResponseDto
import com.luczka.baristaai.domain.model.GenerateRecipesResult

fun GenerateRecipesResponseDto.toDomain(): GenerateRecipesResult {
    return GenerateRecipesResult(
        generationRequest = generationRequest.toDomain(),
        recipes = recipes.map { it.toDomain() }
    )
}
