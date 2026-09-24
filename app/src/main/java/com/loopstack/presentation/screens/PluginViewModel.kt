package com.loopstack.presentation.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PluginViewModel @Inject constructor() : ViewModel() {
    private val _plugins = MutableStateFlow(
        listOf(
            PluginItem("1", "Shell Extension", "Execute shell commands directly.", true),
            PluginItem("2", "Git Integration", "Manage git repositories from tools.", false),
            PluginItem("3", "Python Runtime", "Run Python scripts within LoopStack.", true),
            PluginItem("4", "Network Scanner", "Scan local network for devices.", false)
        )
    )
    val plugins: StateFlow<List<PluginItem>> = _plugins.asStateFlow()

    fun togglePlugin(id: String, isEnabled: Boolean) {
        _plugins.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(isEnabled = isEnabled) else it
            }
        }
    }
}
