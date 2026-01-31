package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.GenerateRecipesResult

interface RecipeGenerationRepository {
    suspend fun generateRecipes(
        generationRequestId: String
    ): RepositoryResult<GenerateRecipesResult>
}
