package com.luczka.baristaai.data.repository

import android.util.Log
import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toGenerateRecipesCommand
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.data.models.GenerateRecipesResponseDto
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.GenerateRecipesResult
import com.luczka.baristaai.domain.repository.RecipeGenerationRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import javax.inject.Inject

class RecipeGenerationRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : RecipeGenerationRepository {
    override suspend fun generateRecipes(
        generationRequestId: String
    ): RepositoryResult<GenerateRecipesResult> {
        val result = runCatching {
            // Ensure session is fresh to avoid "Invalid JWT" errors with Edge Functions
            dataSource.client.auth.refreshCurrentSession()

            val command = generationRequestId.toGenerateRecipesCommand()
            dataSource.client.functions.invoke(
                function = "generate-recipes",
                body = command,
            ).body<GenerateRecipesResponseDto>()
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to generate recipes.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    private companion object {
        const val TAG: String = "RecipeGenerationRepository"
    }
}
