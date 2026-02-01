package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.GenerationRequestFilter
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import com.luczka.baristaai.domain.model.UpdateGenerationRequest
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GenerationRequestsRepositoryImpl.
 */
class GenerationRequestsRepositoryTest {

    private lateinit var dataSource: SupabaseDataSource
    private lateinit var repository: GenerationRequestsRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = GenerationRequestsRepositoryImpl(dataSource)
    }

    // ===== Not Implemented Methods =====

    @Test
    fun `listGenerationRequests returns not implemented error`() = runTest {
        val filter = GenerationRequestFilter()
        val page = PageRequest(limit = 10, offset = 0)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        val result = repository.listGenerationRequests(filter, page, sort)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }

    @Test
    fun `getGenerationRequest returns not implemented error`() = runTest {
        val result = repository.getGenerationRequest("request-123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }

    @Test
    fun `updateGenerationRequest returns not implemented error`() = runTest {
        val input = UpdateGenerationRequest(userComment = "Updated comment")
        val result = repository.updateGenerationRequest("request-123", input)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }

    @Test
    fun `deleteGenerationRequest returns not implemented error`() = runTest {
        val result = repository.deleteGenerationRequest("request-123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }
}
