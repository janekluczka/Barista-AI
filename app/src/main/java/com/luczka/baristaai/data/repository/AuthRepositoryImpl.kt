package com.luczka.baristaai.data.repository

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.datasource.UnauthorizedException
import com.luczka.baristaai.domain.error.RepositoryError
import com.luczka.baristaai.domain.error.RepositoryResult
import com.luczka.baristaai.domain.model.AuthUser
import com.luczka.baristaai.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.ktor.client.plugins.ResponseException
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

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
            val user = dataSource.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            mapAuthUser(user)
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(mapThrowableToError(it)) }
        )
    }

    override suspend fun signInWithGoogle(idToken: String): RepositoryResult<AuthUser> {
        if (idToken.isBlank()) {
            return RepositoryResult.Failure(
                RepositoryError.Validation("ID token is required.")
            )
        }

        val result = runCatching {
            val user = dataSource.client.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            mapAuthUser(user)
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(mapThrowableToError(it)) }
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
            val user = dataSource.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            mapAuthUser(user)
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(mapThrowableToError(it)) }
        )
    }

    override suspend fun signOut(): RepositoryResult<Unit> {
        val result = runCatching {
            dataSource.signOut()
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(Unit) },
            onFailure = { RepositoryResult.Failure(mapThrowableToError(it)) }
        )
    }

    override suspend fun getCurrentUser(): RepositoryResult<AuthUser?> {
        val result = runCatching {
            dataSource.client.auth.currentUserOrNull()?.let { mapAuthUser(it) }
        }

        return result.fold(
            onSuccess = { RepositoryResult.Success(it) },
            onFailure = { RepositoryResult.Failure(mapThrowableToError(it)) }
        )
    }

    override fun observeAuthState(): Flow<AuthUser?> {
        return dataSource.client.auth.sessionStatus.map {
            dataSource.client.auth.currentUserOrNull()?.let { user -> mapAuthUser(user) }
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

    private fun mapAuthUser(user: UserInfo): AuthUser {
        return AuthUser(
            id = user.id,
            email = user.email,
            displayName = getMetadataString(
                user,
                listOf("full_name", "name", "preferred_username")
            ),
            avatarUrl = getMetadataString(user, listOf("avatar_url", "picture"))
        )
    }

    private fun getMetadataString(
        user: UserInfo,
        keys: List<String>
    ): String? {
        val metadata = user.userMetadata ?: return null
        keys.forEach { key ->
            val element = metadata[key] ?: return@forEach
            val value = element.jsonPrimitive.contentOrNull
            if (!value.isNullOrBlank()) {
                return value
            }
        }
        return null
    }

    private fun mapThrowableToError(throwable: Throwable): RepositoryError {
        return when (throwable) {
            is UnauthorizedException -> RepositoryError.Unauthorized("User is not authenticated.")
            is IllegalArgumentException -> RepositoryError.Validation(
                throwable.message ?: "Invalid input."
            )
            is ResponseException -> mapResponseError(throwable)
            is IOException -> RepositoryError.Network("Network error.", throwable)
            else -> RepositoryError.Unknown("Unexpected error.", throwable)
        }
    }

    private fun mapResponseError(exception: ResponseException): RepositoryError {
        val status = exception.response.status.value
        val message = exception.message ?: "Request failed."
        return when (status) {
            400, 422 -> RepositoryError.Validation(message)
            401, 403 -> RepositoryError.Unauthorized(message)
            404 -> RepositoryError.NotFound(message)
            in 500..599 -> RepositoryError.Network("Server error.", exception)
            else -> RepositoryError.Unknown(message, exception)
        }
    }
}
