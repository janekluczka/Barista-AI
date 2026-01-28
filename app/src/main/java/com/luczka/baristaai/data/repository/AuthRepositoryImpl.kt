package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.mapper.toDomain
import com.luczka.baristaai.data.mapper.toRepositoryError
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.AuthUser
import com.luczka.baristaai.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseDataSource
) : AuthRepository {

    override suspend fun signIn(
        email: String,
        password: String
    ): RepositoryResult<AuthUser> {
        val validationError = validateCredentials(email, password)
        if (validationError != null) {
            return RepositoryResult.Failure(validationError)
        }

        val result = runCatching {
            dataSource.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = dataSource.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("User not found after sign in.")
            user.toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(it.toRepositoryError()) }
        )
    }

    override suspend fun signInWithGoogle(idToken: String): RepositoryResult<AuthUser> {
        if (idToken.isBlank()) {
            return RepositoryResult.Failure(
                RepositoryError.Validation("ID token is required.")
            )
        }

        val result = runCatching {
            dataSource.client.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            val user = dataSource.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("User not found after sign in.")
            user.toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(it.toRepositoryError()) }
        )
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): RepositoryResult<AuthUser> {
        val validationError = validateCredentials(email, password)
        if (validationError != null) {
            return RepositoryResult.Failure(validationError)
        }

        val result = runCatching {
            dataSource.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = dataSource.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("User not found after sign up.")
            user.toDomain()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(it.toRepositoryError()) }
        )
    }

    override suspend fun signOut(): RepositoryResult<Unit> {
        val result = runCatching {
            dataSource.signOut()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(Unit) },
            onFailure = { RepositoryResult.Failure(it.toRepositoryError()) }
        )
    }

    override suspend fun getCurrentUser(): RepositoryResult<AuthUser?> {
        val result = runCatching {
            dataSource.client.auth.currentUserOrNull()?.let { it.toDomain() }
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(it.toRepositoryError()) }
        )
    }

    override fun observeAuthState(): Flow<AuthUser?> {
        return dataSource.client.auth.sessionStatus.map {
            dataSource.client.auth.currentUserOrNull()?.let { user -> user.toDomain() }
        }
    }

    private fun validateCredentials(
        email: String,
        password: String
    ): RepositoryError? {
        if (email.isBlank()) {
            return RepositoryError.Validation("Email is required.")
        }
        if (password.isBlank()) {
            return RepositoryError.Validation("Password is required.")
        }
        return null
    }

}
