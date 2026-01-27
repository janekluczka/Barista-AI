package com.luczka.baristaai.domain.model

data class GenerationRequestFilter(
    val brewMethodId: String? = null,
    val createdAfterIso: String? = null,
    val createdBeforeIso: String? = null
)
