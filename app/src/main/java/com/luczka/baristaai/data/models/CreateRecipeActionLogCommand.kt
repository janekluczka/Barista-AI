package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeActionLogCommand(
    @SerialName("user_id")
    val userId: String, // UUID as string
    @SerialName("recipe_id")
    val recipeId: String, // UUID as string
    @SerialName("generation_request_id")
    val generationRequestId: String?, // UUID as string
    @SerialName("action")
    val action: RecipeAction
)
