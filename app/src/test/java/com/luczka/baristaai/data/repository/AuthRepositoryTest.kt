package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AuthRepositoryImpl focusing on validation logic.
 */
class AuthRepositoryTest {

    private lateinit var dataSource: SupabaseDataSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = AuthRepositoryImpl(dataSource)
    }

    // ===== signIn Validation Tests =====

    @Test
    fun `signIn validates email - blank email returns validation error`() = runTest {
        val result = repository.signIn("", "password123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signIn validates email - whitespace email returns validation error`() = runTest {
        val result = repository.signIn("   ", "password123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signIn validates password - blank password returns validation error`() = runTest {
        val result = repository.signIn("user@example.com", "")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signIn validates password - whitespace password returns validation error`() = runTest {
        val result = repository.signIn("user@example.com", "   ")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signIn validates both - blank email takes precedence`() = runTest {
        val result = repository.signIn("", "")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    // ===== signUp Validation Tests =====

    @Test
    fun `signUp validates email - blank email returns validation error`() = runTest {
        val result = repository.signUp("", "password123")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signUp validates password - blank password returns validation error`() = runTest {
        val result = repository.signUp("user@example.com", "")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    // ===== signInWithGoogle Validation Tests =====

    @Test
    fun `signInWithGoogle validates idToken - blank token returns validation error`() = runTest {
        val result = repository.signInWithGoogle("")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }

    @Test
    fun `signInWithGoogle validates idToken - whitespace token returns validation error`() = runTest {
        val result = repository.signInWithGoogle("   ")

        assertTrue("Expected Failure result", result is RepositoryResult.Failure)
        val error = (result as RepositoryResult.Failure).error
        assertTrue("Expected Validation error", error is RepositoryError.Validation)
    }
}
