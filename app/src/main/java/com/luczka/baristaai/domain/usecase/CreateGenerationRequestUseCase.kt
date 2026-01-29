package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.CreateGenerationRequest
import com.luczka.baristaai.domain.model.GenerationRequest
import com.luczka.baristaai.domain.repository.GenerationRequestsRepository
import javax.inject.Inject

class CreateGenerationRequestUseCase @Inject constructor(
    private val repository: GenerationRequestsRepository
) {
    suspend operator fun invoke(
        input: CreateGenerationRequest
    ): RepositoryResult<GenerationRequest> {
        return repository.createGenerationRequest(input)
    }
}
