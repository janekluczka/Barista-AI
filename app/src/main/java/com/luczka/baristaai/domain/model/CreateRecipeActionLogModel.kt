package com.luczka.baristaai.domain.model

data class CreateRecipeActionLogModel(
    val recipeId: String,
    val generationRequestId: String?,
    val action: RecipeActionModel
)
