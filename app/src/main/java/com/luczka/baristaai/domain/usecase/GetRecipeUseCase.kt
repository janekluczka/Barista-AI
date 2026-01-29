package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(id: String): RepositoryResult<Recipe> {
        return repository.getRecipe(id)
    }
}
