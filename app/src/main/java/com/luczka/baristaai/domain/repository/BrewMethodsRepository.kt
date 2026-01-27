package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption

interface BrewMethodsRepository {
    suspend fun listBrewMethods(
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.ASC)
    ): RepositoryResult<List<BrewMethod>>

    suspend fun getBrewMethodById(id: String): RepositoryResult<BrewMethod>

    suspend fun getBrewMethodBySlug(slug: String): RepositoryResult<BrewMethod>
}
