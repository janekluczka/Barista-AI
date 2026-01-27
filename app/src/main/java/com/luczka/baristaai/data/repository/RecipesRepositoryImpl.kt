package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipe
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.SimilarRecipeQuery
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.repository.RecipesRepository
import javax.inject.Inject

class RecipesRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : RecipesRepository {

    override suspend fun listRecipes(
        filter: RecipeFilter,
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<Recipe>> {
        // TODO: Implement Supabase query for recipes list.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun getRecipe(id: String): RepositoryResult<Recipe> {
        // TODO: Implement Supabase query for recipe by id.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun createRecipe(input: CreateRecipe): RepositoryResult<Recipe> {
        // TODO: Implement Supabase insert for recipe.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun updateRecipe(
        id: String,
        input: UpdateRecipe
    ): RepositoryResult<Recipe> {
        // TODO: Implement Supabase update for recipe.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun deleteRecipe(id: String): RepositoryResult<Unit> {
        // TODO: Implement Supabase delete for recipe.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun findSimilarRecipes(
        query: SimilarRecipeQuery,
        page: PageRequest
    ): RepositoryResult<List<Recipe>> {
        // TODO: Implement Supabase query for similar recipes.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }
}
