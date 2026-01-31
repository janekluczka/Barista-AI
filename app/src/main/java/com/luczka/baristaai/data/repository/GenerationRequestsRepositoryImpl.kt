package com.luczka.baristaai.data.repository

import android.util.Log
import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toCommand
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.data.models.GenerateRecipesResponseDto
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateGenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequestFilter
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateGenerationRequest
import com.luczka.baristaai.domain.repository.GenerationRequestsRepository
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import javax.inject.Inject

class GenerationRequestsRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : GenerationRequestsRepository {

    override suspend fun listGenerationRequests(
        filter: GenerationRequestFilter,
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<GenerationRequest>> {
        // TODO: Implement Supabase query for generation requests list.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun getGenerationRequest(id: String): RepositoryResult<GenerationRequest> {
        // TODO: Implement Supabase query for generation request by id.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun createGenerationRequest(
        input: CreateGenerationRequest
    ): RepositoryResult<GenerationRequest> {
        val result = runCatching {
            val command = input.toCommand()
            dataSource.client.functions.invoke(
                function = "generate-recipes",
                body = command,
            ).body<GenerateRecipesResponseDto>()
                .generationRequest
                .toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = {
                Log.e(TAG, "Failed to create generation request.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
        )
    }

    override suspend fun updateGenerationRequest(
        id: String,
        input: UpdateGenerationRequest
    ): RepositoryResult<GenerationRequest> {
        // TODO: Implement Supabase update for generation request.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun deleteGenerationRequest(id: String): RepositoryResult<Unit> {
        // TODO: Implement Supabase delete for generation request.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    private companion object {
        const val TAG: String = "GenerationRequestsRepository"
    }
}
