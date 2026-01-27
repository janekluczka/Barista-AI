package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipe
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.SimilarRecipeQuery
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateRecipe

interface RecipesRepository {
    suspend fun listRecipes(
        filter: RecipeFilter,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<Recipe>>

    suspend fun getRecipe(id: String): RepositoryResult<Recipe>

    suspend fun createRecipe(input: CreateRecipe): RepositoryResult<Recipe>

    suspend fun updateRecipe(
        id: String,
        input: UpdateRecipe
    ): RepositoryResult<Recipe>

    suspend fun deleteRecipe(id: String): RepositoryResult<Unit>

    suspend fun findSimilarRecipes(
        query: SimilarRecipeQuery,
        page: PageRequest
    ): RepositoryResult<List<Recipe>>
}
