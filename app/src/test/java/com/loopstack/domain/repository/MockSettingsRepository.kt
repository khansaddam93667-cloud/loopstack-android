package com.loopstack.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockSettingsRepository : SettingsRepository {
    override val routerPort: Flow<Int> = flowOf(20128)
    override val apiPathPrefix: Flow<String> = flowOf("/v1")
    override val apiKey: Flow<String> = flowOf("test-token")

    override suspend fun saveSettings(port: Int, pathPrefix: String, key: String) {
        // No-op for tests
    }
}
