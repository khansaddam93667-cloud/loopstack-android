package com.loopstack.core.di

import com.loopstack.core.network.LoopbackHttpClient
import com.loopstack.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoopbackHttpClient(
        settingsRepository: SettingsRepository
    ): LoopbackHttpClient {
        return LoopbackHttpClient(settingsRepository)
    }
}
