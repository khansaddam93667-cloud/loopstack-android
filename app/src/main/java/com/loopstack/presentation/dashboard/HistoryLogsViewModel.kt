package com.loopstack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.data.local.dao.SessionLogDao
import com.loopstack.data.local.entity.SessionLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryLogsViewModel @Inject constructor(
    private val sessionLogDao: SessionLogDao
) : ViewModel() {

    val logs: StateFlow<List<SessionLogEntity>> = sessionLogDao.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteLog(id: String) {
        viewModelScope.launch {
            sessionLogDao.deleteLogById(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            sessionLogDao.clearAllLogs()
        }
    }
}
