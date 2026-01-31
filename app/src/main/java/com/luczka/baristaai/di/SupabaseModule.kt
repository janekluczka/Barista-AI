package com.luczka.baristaai.di

import android.content.Context
import com.luczka.baristaai.BuildConfig
import com.russhwolf.settings.SharedPreferencesSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(
        @ApplicationContext context: Context
    ): SupabaseClient {
        val sharedPreferences = context.getSharedPreferences(
            "supabase_auth",
            Context.MODE_PRIVATE
        )
        val settings = SharedPreferencesSettings(sharedPreferences)
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 45000
                    connectTimeoutMillis = 20000
                    socketTimeoutMillis = 45000
                }
            }
            install(Auth) {
                autoLoadFromStorage = true
                autoSaveToStorage = true
                sessionManager = SettingsSessionManager(settings)
            }
            install(Functions)
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun providePostgrest(client: SupabaseClient): Postgrest {
        return client.postgrest
    }
}