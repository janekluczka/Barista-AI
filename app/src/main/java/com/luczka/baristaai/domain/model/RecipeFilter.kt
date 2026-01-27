package com.luczka.baristaai.domain.model

data class RecipeFilter(
    val brewMethodId: String? = null,
    val status: RecipeStatus? = null,
    val generationRequestId: String? = null,
    val createdAfterIso: String? = null,
    val createdBeforeIso: String? = null
)
