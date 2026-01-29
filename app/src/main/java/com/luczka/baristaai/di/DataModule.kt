package com.luczka.baristaai.di

import com.luczka.baristaai.data.datasource.SupabaseDataSource
import com.luczka.baristaai.data.datasource.SupabaseDataSourceImpl
import com.luczka.baristaai.data.network.AndroidNetworkMonitor
import com.luczka.baristaai.data.repository.AuthRepositoryImpl
import com.luczka.baristaai.data.repository.BrewMethodsRepositoryImpl
import com.luczka.baristaai.data.repository.GenerationRequestsRepositoryImpl
import com.luczka.baristaai.data.repository.RecipeActionLogsRepositoryImpl
import com.luczka.baristaai.data.repository.RecipesRepositoryImpl
import com.luczka.baristaai.domain.network.NetworkMonitor
import com.luczka.baristaai.domain.repository.AuthRepository
import com.luczka.baristaai.domain.repository.BrewMethodsRepository
import com.luczka.baristaai.domain.repository.GenerationRequestsRepository
import com.luczka.baristaai.domain.repository.RecipeActionLogsRepository
import com.luczka.baristaai.domain.repository.RecipesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSupabaseDataSource(
        impl: SupabaseDataSourceImpl
    ): SupabaseDataSource

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: AndroidNetworkMonitor
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBrewMethodsRepository(
        impl: BrewMethodsRepositoryImpl
    ): BrewMethodsRepository

    @Binds
    @Singleton
    abstract fun bindGenerationRequestsRepository(
        impl: GenerationRequestsRepositoryImpl
    ): GenerationRequestsRepository

    @Binds
    @Singleton
    abstract fun bindRecipesRepository(
        impl: RecipesRepositoryImpl
    ): RecipesRepository

    @Binds
    @Singleton
    abstract fun bindRecipeActionLogsRepository(
        impl: RecipeActionLogsRepositoryImpl
    ): RecipeActionLogsRepository
}
