package com.loopstack.presentation.agent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.system.SystemMetrics

@Composable
fun resolveAgentContext(
    currentRoute: String?,
    viewModel: AgentViewModel = hiltViewModel()
): String {
    val loopbackStatus by viewModel.loopbackStatus.collectAsState()
    val systemMetrics by viewModel.systemMetrics.collectAsState()
    val recentLogSummary by viewModel.recentLogSummary.collectAsState()

    return when (currentRoute) {
        "dashboard" -> {
            val statusStr = when (val status = loopbackStatus) {
                is LoopbackStatus.Active -> "Active (${status.providerName})"
                is LoopbackStatus.Degraded -> "Degraded"
                is LoopbackStatus.Inactive -> "Inactive"
            }
            "User is on the Dashboard. OmniRoute status: $statusStr."
        }
        "analytics" -> {
            val metrics = systemMetrics ?: SystemMetrics(0, 0, 0, 0)
            "User is viewing Analytics. Live stats: RAM ${metrics.availableRamMb}/${metrics.totalRamMb} MB available, Storage ${metrics.freeStorageGb}/${metrics.totalStorageGb} GB free."
        }
        "history", "terminal" -> {
            "User is in the Terminal/History view. Recent commands summary: $recentLogSummary."
        }
        "security" -> {
            "User is in the Security screen. Ensure safety guidelines and explain active background services."
        }
        else -> {
            "User is navigating LoopStack at route: $currentRoute."
        }
    }
}
