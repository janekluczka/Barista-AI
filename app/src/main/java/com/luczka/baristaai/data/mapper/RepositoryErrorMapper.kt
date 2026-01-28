package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.data.datasource.UnauthorizedException
import com.luczka.baristaai.domain.error.RepositoryError
import io.ktor.client.plugins.ResponseException
import java.io.IOException

fun Throwable.toRepositoryError(): RepositoryError {
    return when (this) {
        is UnauthorizedException -> RepositoryError.Unauthorized("User is not authenticated.")
        is IllegalArgumentException -> RepositoryError.Validation(
            message ?: "Invalid input."
        )
        is ResponseException -> toRepositoryError()
        is IOException -> RepositoryError.Network("Network error.")
        else -> RepositoryError.Unknown("Unexpected error.", this)
    }
}

private fun ResponseException.toRepositoryError(): RepositoryError {
    val status = response.status.value
    val message = message ?: "Request failed."
    return when (status) {
        400, 422 -> RepositoryError.Validation(message)
        401, 403 -> RepositoryError.Unauthorized(message)
        404 -> RepositoryError.NotFound(message)
        in 500..599 -> RepositoryError.Network("Server error.")
        else -> RepositoryError.Unknown(message, this)
    }
}
