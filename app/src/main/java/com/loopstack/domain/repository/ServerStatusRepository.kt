package com.loopstack.domain.repository

import com.loopstack.domain.model.LoopbackStatus
import kotlinx.coroutines.flow.Flow

interface ServerStatusRepository {
    fun observeServerStatus(): Flow<LoopbackStatus>
    fun forceActiveStatus(providerName: String)
}
