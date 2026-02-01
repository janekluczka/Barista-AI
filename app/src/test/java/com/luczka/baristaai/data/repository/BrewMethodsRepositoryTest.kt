package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for BrewMethodsRepositoryImpl.
 */
class BrewMethodsRepositoryTest {

    private lateinit var dataSource: SupabaseDataSource
    private lateinit var repository: BrewMethodsRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = BrewMethodsRepositoryImpl(dataSource)
    }

    // ===== Not Implemented Methods =====

    @Test
    fun `getBrewMethodById returns not implemented error`() = runTest {
        val result = repository.getBrewMethodById("brew-method-123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }

    @Test
    fun `getBrewMethodBySlug returns not implemented error`() = runTest {
        val result = repository.getBrewMethodBySlug("v60")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }
}
