package com.loopstack.core.di

import com.loopstack.data.repository.SettingsRepositoryImpl
import com.loopstack.domain.repository.SettingsRepository
import com.loopstack.domain.system.SystemMetricsRepository
import com.loopstack.domain.system.SystemMetricsRepositoryImpl
import com.loopstack.domain.repository.ServerStatusRepository
import com.loopstack.data.remote.ServerStatusRepositoryImpl


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
    @Binds
    @Singleton
    abstract fun bindSystemMetricsRepository(
        systemMetricsRepositoryImpl: SystemMetricsRepositoryImpl
    ): SystemMetricsRepository
    @Binds
    @Singleton
    abstract fun bindServerStatusRepository(
        serverStatusRepositoryImpl: ServerStatusRepositoryImpl
    ): ServerStatusRepository
}
