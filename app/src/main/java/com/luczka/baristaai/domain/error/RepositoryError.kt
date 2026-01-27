package com.luczka.baristaai.domain.error

sealed interface RepositoryError {
    data class Unauthorized(val message: String) : RepositoryError
    data class NotFound(val message: String) : RepositoryError
    data class Validation(val message: String) : RepositoryError
    data class Network(val message: String) : RepositoryError
    data class Unknown(
        val message: String,
        val cause: Throwable? = null
    ) : RepositoryError
}
