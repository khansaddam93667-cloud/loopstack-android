package com.loopstack.domain.repository

import com.loopstack.domain.model.LoopbackStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockServerStatusRepository : ServerStatusRepository {
    override fun observeServerStatus(): Flow<LoopbackStatus> = flowOf(LoopbackStatus.Inactive)
    override fun forceActiveStatus(providerName: String) {}
}
