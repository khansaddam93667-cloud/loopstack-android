package com.loopstack.core.di

import com.loopstack.core.network.LoopbackHttpClient
import com.loopstack.domain.repository.SettingsRepository
import com.loopstack.domain.repository.ServerStatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideLoopbackHttpClient(
        settingsRepository: SettingsRepository,
        serverStatusRepository: ServerStatusRepository
    ): LoopbackHttpClient {
        return LoopbackHttpClient(settingsRepository, serverStatusRepository)
    }
}
