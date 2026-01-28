package com.luczka.baristaai.data.mapper

import com.luczka.baristaai.domain.model.AuthUser
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

fun UserInfo.toDomain(): AuthUser {
    return AuthUser(
        id = id,
        email = email,
        displayName = getMetadataString(
            this,
            listOf("full_name", "name", "preferred_username")
        ),
        avatarUrl = getMetadataString(this, listOf("avatar_url", "picture"))
    )
}

private fun getMetadataString(
    user: UserInfo,
    keys: List<String>
): String? {
    val metadata = user.userMetadata ?: return null
    keys.forEach { key ->
        val element = metadata[key] ?: return@forEach
        val value = element.jsonPrimitive.contentOrNull
        if (!value.isNullOrBlank()) {
            return value
        }
    }
    return null
}
