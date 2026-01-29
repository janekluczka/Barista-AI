package com.luczka.baristaai.data.repository

import android.util.Log
import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toPayload
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.data.models.RecipeActionLogDto
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateRecipeActionLogModel
import com.luczka.baristaai.domain.model.RecipeActionLogModel
import com.luczka.baristaai.domain.repository.RecipeActionLogsRepository
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class RecipeActionLogsRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : RecipeActionLogsRepository {

    override suspend fun createRecipeActionLog(
        input: CreateRecipeActionLogModel
    ): RepositoryResult<RecipeActionLogModel> {
        val result = runCatching {
            val userId = dataSource.currentUserId()
            val payload = input.toPayload(userId)
            dataSource.client
                .from("recipe_action_logs")
                .insert(payload) {
                    select()
                }
                .decodeSingle<RecipeActionLogDto>()
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to create recipe action log.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    private companion object {
        const val TAG: String = "RecipeActionLogsRepository"
    }
}
