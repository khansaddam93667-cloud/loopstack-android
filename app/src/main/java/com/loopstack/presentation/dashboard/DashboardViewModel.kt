package com.loopstack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import java.util.UUID

data class ToolCardModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val badgeCount: Int = 0
)

data class DashboardUiState(
    val tools: List<ToolCardModel> = emptyList(),
    val bannerProgress: Float = 0f
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val staticTools = listOf(
        ToolCardModel(title = "Network / Status", subtitle = "View internal API traffic"),
        ToolCardModel(title = "Settings", subtitle = "App configurations"),
        ToolCardModel(title = "Analytics", subtitle = "Usage stats"),
        ToolCardModel(title = "Database", subtitle = "Local store info"),
        ToolCardModel(title = "Plugins", subtitle = "Manage extensions"),
        ToolCardModel(title = "Security", subtitle = "App permissions"),
        ToolCardModel(title = "Developer", subtitle = "Advanced tools"),
        ToolCardModel(title = "Account", subtitle = "User profile")
    )

    private val _historyLogsBadgeCount = MutableStateFlow(0)
    private val _bannerProgress = MutableStateFlow(0f)

    val uiState: StateFlow<DashboardUiState> = combine(
        _historyLogsBadgeCount,
        _bannerProgress
    ) { badgeCount, progress ->
        val dynamicTools = staticTools.toMutableList()
        // Replace the 8th item with History Logs to keep it at exactly 8 items, or just modify the 8th.
        // The prompt says "renders exactly 8 ToolCard items".
        // We'll replace the first one with History Logs if badge count > 0, otherwise we keep 8 items.
        // Better: let's just use static tools if badge count is 0, but since we need to show badge count, let's always put it in slot 0.
        // We'll drop the last item to keep it at 8.

        dynamicTools.removeAt(7)
        dynamicTools.add(0, ToolCardModel(title = "History Logs", subtitle = "Recent chat sessions", badgeCount = badgeCount))

        DashboardUiState(
            tools = dynamicTools,
            bannerProgress = progress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    fun updateHistoryLogsBadgeCount(count: Int) {
        _historyLogsBadgeCount.update { count }
    }

    fun updateBannerProgress(progress: Float) {
        _bannerProgress.update { progress }
    }
}
