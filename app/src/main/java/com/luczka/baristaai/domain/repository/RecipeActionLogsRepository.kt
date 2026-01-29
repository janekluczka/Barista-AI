package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel

interface RecipeActionLogsRepository {
    suspend fun createRecipeActionLog(
        input: CreateRecipeActionLogModel
    ): RepositoryResult<RecipeActionLogModel>
}
