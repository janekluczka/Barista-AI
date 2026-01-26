package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Action log record from `recipe_action_logs`.
 *
 * @property id UUID string.
 * @property userId UUID string.
 * @property recipeId UUID string.
 * @property generationRequestId UUID string.
 * @property action User decision on a recipe.
 * @property createdAt timestamptz ISO-8601 string.
 */
@Serializable
data class RecipeActionLogDto(
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("generation_request_id")
    val generationRequestId: String?,
    @SerialName("action")
    val action: RecipeAction,
    @SerialName("created_at")
    val createdAt: String
)
