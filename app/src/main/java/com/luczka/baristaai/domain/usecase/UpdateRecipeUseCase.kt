package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class UpdateRecipeUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(id: String, input: UpdateRecipe): RepositoryResult<Recipe> {
        return repository.updateRecipe(id, input)
    }
}
