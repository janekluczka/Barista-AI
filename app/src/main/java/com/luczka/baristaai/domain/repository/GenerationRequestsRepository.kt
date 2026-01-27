package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateGenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequestFilter
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateGenerationRequest

interface GenerationRequestsRepository {
    suspend fun listGenerationRequests(
        filter: GenerationRequestFilter,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<GenerationRequest>>

    suspend fun getGenerationRequest(id: String): RepositoryResult<GenerationRequest>

    suspend fun createGenerationRequest(
        input: CreateGenerationRequest
    ): RepositoryResult<GenerationRequest>

    suspend fun updateGenerationRequest(
        id: String,
        input: UpdateGenerationRequest
    ): RepositoryResult<GenerationRequest>

    suspend fun deleteGenerationRequest(id: String): RepositoryResult<Unit>
}
