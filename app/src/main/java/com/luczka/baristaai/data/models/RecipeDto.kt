package com.luczka.baristaai.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    @SerialName("id")
    val id: String, // UUID as string
    @SerialName("user_id")
    val userId: String, // UUID as string
    @SerialName("generation_request_id")
    val generationRequestId: String?, // UUID as string
    @SerialName("brew_method_id")
    val brewMethodId: String, // UUID as string
    @SerialName("coffee_amount")
    val coffeeAmount: Double, // numeric(6,1)
    @SerialName("water_amount")
    val waterAmount: Double, // numeric(6,1)
    @SerialName("ratio_coffee")
    val ratioCoffee: Int,
    @SerialName("ratio_water")
    val ratioWater: Int,
    @SerialName("temperature")
    val temperature: Int,
    @SerialName("assistant_tip")
    val assistantTip: String?,
    @SerialName("status")
    val status: RecipeStatus,
    @SerialName("created_at")
    val createdAt: String, // timestamptz as ISO-8601 string
    @SerialName("updated_at")
    val updatedAt: String // timestamptz as ISO-8601 string
)
