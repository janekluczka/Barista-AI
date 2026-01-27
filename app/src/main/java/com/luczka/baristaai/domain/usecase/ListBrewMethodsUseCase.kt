package com.luczka.baristaai.domain.usecase

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.repository.BrewMethodsRepository
import javax.inject.Inject

class ListBrewMethodsUseCase @Inject constructor(
    private val repository: BrewMethodsRepository
) {
    suspend operator fun invoke(): RepositoryResult<List<BrewMethod>> {
        return repository.listBrewMethods()
    }
}
