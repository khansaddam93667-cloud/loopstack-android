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

data class ToolCardModel(
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
        ToolCardModel(title = "Network", subtitle = "View internal API traffic"),
        ToolCardModel(title = "Settings", subtitle = "App configurations")
    )

    private val _historyLogsBadgeCount = MutableStateFlow(0)
    private val _bannerProgress = MutableStateFlow(0f)

    val uiState: StateFlow<DashboardUiState> = combine(
        _historyLogsBadgeCount,
        _bannerProgress
    ) { badgeCount, progress ->
        val dynamicTools = staticTools.toMutableList()
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
