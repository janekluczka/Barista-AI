package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase enum mapping for `recipe_action_enum`.
 */
@Serializable
enum class RecipeAction {
    @SerialName("accepted")
    Accepted,
    @SerialName("edited")
    Edited,
    @SerialName("rejected")
    Rejected
}
