package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command for inserting a `recipe_action_logs` row.
 *
 * @property userId UUID string.
 * @property recipeId UUID string.
 * @property generationRequestId UUID string.
 * @property action User decision on a recipe.
 */
@Serializable
data class CreateRecipeActionLogCommand(
    @SerialName("user_id")
    val userId: String,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("generation_request_id")
    val generationRequestId: String?,
    @SerialName("action")
    val action: RecipeAction
)
