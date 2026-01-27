package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.RecipeActionLogFilterModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.repository.RecipeActionLogsRepository
import javax.inject.Inject

class RecipeActionLogsRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : RecipeActionLogsRepository {

    override suspend fun listRecipeActionLogs(
        filter: RecipeActionLogFilterModel,
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<RecipeActionLogModel>> {
        // TODO: Implement Supabase query for recipe action logs list.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun createRecipeActionLog(
        input: CreateRecipeActionLogModel
    ): RepositoryResult<RecipeActionLogModel> {
        // TODO: Implement Supabase insert for recipe action log.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun deleteRecipeActionLog(id: String): RepositoryResult<Unit> {
        // TODO: Implement Supabase delete for recipe action log.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }
}
