package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase enum mapping for `recipe_status_enum`.
 */
@Serializable
enum class RecipeStatus {
    @SerialName("draft")
    Draft,
    @SerialName("saved")
    Saved,
    @SerialName("rejected")
    Rejected,
    @SerialName("deleted")
    Deleted
}
