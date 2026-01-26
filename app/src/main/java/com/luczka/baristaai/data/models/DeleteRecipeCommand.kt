package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteRecipeCommand(
    @SerialName("id")
    val id: String // UUID as string
)
