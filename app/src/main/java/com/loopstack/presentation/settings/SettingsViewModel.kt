package com.loopstack.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val routerPort: String = "",
    val apiPathPrefix: String = "",
    val apiKey: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    routerPort = settingsRepository.routerPort.first().toString(),
                    apiPathPrefix = settingsRepository.apiPathPrefix.first(),
                    apiKey = settingsRepository.apiKey.first()
                )
            }
        }
    }

    fun updatePort(port: String) {
        _uiState.update { it.copy(routerPort = port) }
    }

    fun updatePathPrefix(prefix: String) {
        _uiState.update { it.copy(apiPathPrefix = prefix) }
    }

    fun updateApiKey(key: String) {
        _uiState.update { it.copy(apiKey = key) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            val portInt = state.routerPort.toIntOrNull() ?: 20128
            settingsRepository.saveSettings(portInt, state.apiPathPrefix, state.apiKey)
        }
    }
}
