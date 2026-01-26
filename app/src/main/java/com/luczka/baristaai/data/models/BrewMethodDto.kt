package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrewMethodDto(
    @SerialName("id")
    val id: String, // UUID as string
    @SerialName("name")
    val name: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("created_at")
    val createdAt: String // timestamptz as ISO-8601 string
)
