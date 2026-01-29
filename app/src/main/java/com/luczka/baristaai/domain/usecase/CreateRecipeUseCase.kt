package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipe
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class CreateRecipeUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(input: CreateRecipe): RepositoryResult<Recipe> {
        return repository.createRecipe(input)
    }
}
