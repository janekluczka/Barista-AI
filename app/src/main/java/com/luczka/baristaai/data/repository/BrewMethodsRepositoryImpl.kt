package com.luczka.baristaai.data.repository

import android.util.Log
import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.data.models.BrewMethodDto
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.BrewMethod
import com.luczka.baristaai.domain.repository.BrewMethodsRepository
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class BrewMethodsRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : BrewMethodsRepository {

    override suspend fun listBrewMethods(): RepositoryResult<List<BrewMethod>> {
        if (cachedMethods.isNotEmpty()) {
            return RepositoryResult.Success(cachedMethods)
        }
        val result = runCatching {
            dataSource.client
                .from("brew_methods")
                .select()
                .decodeList<BrewMethodDto>()
                .map { it.toDomain() }
        }

        return result.fold(
            onSuccess = {
                cachedMethods = it
                RepositoryResult.Success(it)
            },
            onFailure = {
                Log.e(TAG, "Failed to load brew methods.", it)
                RepositoryResult.Failure(it.toRepositoryError())
            }
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

    private companion object {
        const val TAG: String = "BrewMethodsRepository"
    }

    private var cachedMethods: List<BrewMethod> = emptyList()
}
