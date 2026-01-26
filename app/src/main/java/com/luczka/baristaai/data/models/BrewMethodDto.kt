package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Brew method record from `brew_methods`.
 *
 * @property id UUID string.
 * @property name Display name.
 * @property slug URL-safe identifier.
 * @property createdAt timestamptz ISO-8601 string.
 */
@Serializable
data class BrewMethodDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("created_at")
    val createdAt: String
)
