package com.loopstack.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val routerPort: Flow<Int>
    val apiPathPrefix: Flow<String>
    val apiKey: Flow<String>

    suspend fun saveSettings(port: Int, pathPrefix: String, key: String)
}
