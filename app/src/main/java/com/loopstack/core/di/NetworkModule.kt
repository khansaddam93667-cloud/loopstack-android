package com.loopstack.core.di

import com.loopstack.core.network.LoopbackHttpClient
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
    fun provideLoopbackHttpClient(): LoopbackHttpClient {
        return LoopbackHttpClient()
    }
}
