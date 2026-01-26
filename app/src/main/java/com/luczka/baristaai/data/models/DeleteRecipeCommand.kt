package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Command payload for deleting a `recipes` row by id.
 *
 * @property id UUID string.
 */
@Serializable
data class DeleteRecipeCommand(
    @SerialName("id")
    val id: String
)
