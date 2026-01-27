package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.RecipeActionLogFilterModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption

interface RecipeActionLogsRepository {
    suspend fun listRecipeActionLogs(
        filter: RecipeActionLogFilterModel,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<RecipeActionLogModel>>

    suspend fun createRecipeActionLog(
        input: CreateRecipeActionLogModel
    ): RepositoryResult<RecipeActionLogModel>

    suspend fun deleteRecipeActionLog(id: String): RepositoryResult<Unit>
}
