package com.luczka.baristaai.data.datasource

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class SupabaseDataSourceImpl @Inject constructor(
    override val client: SupabaseClient
) : SupabaseDataSource {
    override suspend fun currentUserId(): String {
        // TODO: Re-enable when Supabase Auth is implemented.
        // val userId = client.auth.currentUserOrNull()?.id
        // if (userId.isNullOrBlank()) {
        //     throw UnauthorizedException("User is not authenticated.")
        // }
        // return userId
        Log.e(TAG, "Supabase auth not configured; returning empty user id.")
        return ""
    }

    private companion object {
        const val TAG: String = "SupabaseDataSource"
    }
}
