package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
