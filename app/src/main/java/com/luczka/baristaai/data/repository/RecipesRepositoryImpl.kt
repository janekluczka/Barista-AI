package com.luczka.baristaai.data.repository

import android.util.Log
import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toCommand
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toDto
import com.luczka.baristaai.data.mapper.toPayload
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.data.models.RecipeDto
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipe
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.Recipe
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.RecipeStatus
import com.luczka.baristaai.domain.model.SimilarRecipeQuery
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateRecipe
import com.luczka.baristaai.domain.repository.RecipesRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class RecipesRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : RecipesRepository {

    override suspend fun listRecipes(
        filter: RecipeFilter,
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<Recipe>> {
        if (page.limit <= 0 || page.offset < 0) {
            return RepositoryResult.Failure(
                RepositoryError.Validation("Invalid pagination parameters.")
            )
        }

        val result = runCatching {
            dataSource.client
                .from("recipes")
                .select {
                    filter {
                        filter.brewMethodId?.let { eq("brew_method_id", it) }
                        filter.generationRequestId?.let { eq("generation_request_id", it) }
                        filter.status?.let { eq("status", it.toDto()) }
                        filter.createdAfterIso?.let { gte("created_at", it) }
                        filter.createdBeforeIso?.let { lte("created_at", it) }

                        if (filter.status == null) {
                            neq("status", RecipeStatus.Deleted.toDto())
                        }
                    }
                    order(
                        column = sort.field,
                        order = when (sort.direction) {
                            SortDirection.ASC -> Order.ASCENDING
                            SortDirection.DESC -> Order.DESCENDING
                        }
                    )
                    limit(page.limit.toLong())
                }
                .decodeList<RecipeDto>()
                .map { it.toDomain() }
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to list recipes.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    override suspend fun getRecipe(id: String): RepositoryResult<Recipe> {
        val result = runCatching {
            dataSource.client
                .from("recipes")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<RecipeDto>()
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to load recipe.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    override suspend fun createRecipe(input: CreateRecipe): RepositoryResult<Recipe> {
        val result = runCatching {
            val userId = dataSource.currentUserId()
            val payload = input.toPayload(userId)
            dataSource.client
                .from("recipes")
                .insert(payload) {
                    select()
                }
                .decodeSingle<RecipeDto>()
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to create recipe.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    override suspend fun updateRecipe(
        id: String,
        input: UpdateRecipe
    ): RepositoryResult<Recipe> {
        val result = runCatching {
            val command = input.toCommand(id)
            dataSource.client
                .from("recipes")
                .update(command) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingle<RecipeDto>()
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to update recipe.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    override suspend fun deleteRecipe(id: String): RepositoryResult<Unit> {
        val result = runCatching {
            val command = UpdateRecipe(status = RecipeStatus.Deleted).toCommand(id)
            dataSource.client
                .from("recipes")
                .update(command) {
                    filter {
                        eq("id", id)
                    }
                }
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(Unit) },
            onFailure = {
                Log.e(TAG, "Failed to delete recipe.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
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

    private companion object {
        const val TAG: String = "RecipesRepository"
    }
}
