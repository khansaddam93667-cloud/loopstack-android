package com.loopstack.domain.system

import kotlinx.coroutines.flow.Flow

data class SystemMetrics(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val totalStorageGb: Long,
    val freeStorageGb: Long
)

interface SystemMetricsRepository {
    fun observeSystemMetrics(): Flow<SystemMetrics>
}
