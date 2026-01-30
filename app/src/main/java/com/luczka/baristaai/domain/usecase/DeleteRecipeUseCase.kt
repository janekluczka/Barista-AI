package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(id: String): RepositoryResult<Unit> {
        return repository.deleteRecipe(id)
    }
}
