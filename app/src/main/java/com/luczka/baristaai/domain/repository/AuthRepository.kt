package com.luczka.baristaai.domain.repository

import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.AuthUser
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(
        email: String,
        password: String
    ): RepositoryResult<AuthUser>

    suspend fun signInWithGoogle(idToken: String): RepositoryResult<AuthUser>

    suspend fun signUp(
        email: String,
        password: String
    ): RepositoryResult<AuthUser>

    suspend fun signOut(): RepositoryResult<Unit>

    suspend fun getCurrentUser(): RepositoryResult<AuthUser?>

    fun observeAuthState(): Flow<SessionStatus>
}
