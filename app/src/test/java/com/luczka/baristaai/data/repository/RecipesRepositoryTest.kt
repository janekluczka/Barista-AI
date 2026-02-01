package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.PageRequest
import com.luczka.baristaai.domain.model.RecipeFilter
import com.luczka.baristaai.domain.model.SortDirection
import com.luczka.baristaai.domain.model.SortOption
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RecipesRepositoryImpl focusing on business logic validation.
 */
class RecipesRepositoryTest {

    private lateinit var dataSource: SupabaseDataSource
    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = RecipesRepositoryImpl(dataSource)
    }

    // ===== Pagination Validation Tests =====

    @Test
    fun `listRecipes validates pagination - zero limit returns validation error`() = runTest {
        val filter = RecipeFilter()
        val page = PageRequest(limit = 0, offset = 0)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        val result = repository.listRecipes(filter, page, sort)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `listRecipes validates pagination - negative limit returns validation error`() = runTest {
        val filter = RecipeFilter()
        val page = PageRequest(limit = -1, offset = 0)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        val result = repository.listRecipes(filter, page, sort)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `listRecipes validates pagination - negative offset returns validation error`() = runTest {
        val filter = RecipeFilter()
        val page = PageRequest(limit = 10, offset = -1)
        val sort = SortOption(field = "created_at", direction = SortDirection.DESC)

        val result = repository.listRecipes(filter, page, sort)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    // ===== findSimilarRecipes Not Implemented =====

    @Test
    fun `findSimilarRecipes returns not implemented error`() = runTest {
        val query = mockk<com.luczka.baristaai.domain.model.SimilarRecipeQuery>()
        val page = PageRequest(limit = 10, offset = 0)

        val result = repository.findSimilarRecipes(query, page)

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Unknown error", error is RepositoryError.Unknown)
    }
}
