package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.GenerateRecipesResult
import com.luczka.baristaai.domain.repository.RecipeGenerationRepository
import javax.inject.Inject

class GenerateRecipesForRequestUseCase @Inject constructor(
    private val repository: RecipeGenerationRepository
) {
    suspend operator fun invoke(
        generationRequestId: String
    ): RepositoryResult<GenerateRecipesResult> {
        return repository.generateRecipes(generationRequestId)
    }
}
