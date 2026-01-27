package com.luczka.baristaai.domain.model

data class RecipeActionLogModel(
    val id: String,
    val userId: String,
    val recipeId: String,
    val generationRequestId: String?,
    val action: RecipeActionModel,
    val createdAt: String
)
