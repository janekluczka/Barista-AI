package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.repository.BrewMethodsRepository
import javax.inject.Inject

class BrewMethodsRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : BrewMethodsRepository {

    override suspend fun listBrewMethods(
        page: PageRequest,
        sort: SortOption
    ): RepositoryResult<List<BrewMethod>> {
        // TODO: Implement Supabase query for brew methods list.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun getBrewMethodById(id: String): RepositoryResult<BrewMethod> {
        // TODO: Implement Supabase query for brew method by id.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }

    override suspend fun getBrewMethodBySlug(slug: String): RepositoryResult<BrewMethod> {
        // TODO: Implement Supabase query for brew method by slug.
        return RepositoryResult.Failure(
            RepositoryError.Unknown("Not implemented yet.")
        )
    }
}
