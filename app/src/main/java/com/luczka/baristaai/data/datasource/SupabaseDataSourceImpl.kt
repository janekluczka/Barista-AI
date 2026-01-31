package com.luczka.baristaai.data.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject

class SupabaseDataSourceImpl @Inject constructor(
    override val client: SupabaseClient
) : SupabaseDataSource {
    override suspend fun currentUserId(): String {
        val userId = client.auth.currentUserOrNull()?.id
        if (userId.isNullOrBlank()) {
            throw UnauthorizedException("User is not authenticated.")
        }
        return userId
    }

    override suspend fun currentAccessToken(): String {
        val accessToken = client.auth.currentSessionOrNull()?.accessToken
        if (accessToken.isNullOrBlank()) {
            throw UnauthorizedException("User is not authenticated.")
        }
        return accessToken
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}
