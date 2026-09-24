package com.loopstack.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.domain.system.SystemMetrics
import com.loopstack.domain.system.SystemMetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    systemMetricsRepository: SystemMetricsRepository
) : ViewModel() {

    val systemMetrics: StateFlow<SystemMetrics> = systemMetricsRepository.observeSystemMetrics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SystemMetrics(0, 0, 0, 0)
        )
}
