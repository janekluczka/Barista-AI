package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class ListRecipesUseCase @Inject constructor(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(
        filter: RecipeFilter,
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<Recipe>> {
        return repository.listRecipes(filter, page, sort)
    }
}
