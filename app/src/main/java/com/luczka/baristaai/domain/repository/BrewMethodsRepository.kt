package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod

interface BrewMethodsRepository {
    suspend fun listBrewMethods(): RepositoryResult<List<BrewMethod>>

    suspend fun getBrewMethodById(id: String): RepositoryResult<BrewMethod>

    suspend fun getBrewMethodBySlug(slug: String): RepositoryResult<BrewMethod>
}
