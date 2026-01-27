package com.luczka.baristaai.data.datasource

import io.github.jan.supabase.SupabaseClient

interface SupabaseDataSource {
    val client: SupabaseClient
    suspend fun currentUserId(): String
}
