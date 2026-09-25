package com.loopstack.presentation.agent

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.data.remote.dto.ChatRequestDto
import com.loopstack.data.remote.dto.MessageDto
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.system.SystemMetrics
import com.loopstack.domain.system.SystemMetricsRepository
import com.loopstack.domain.usecase.ObserveLoopbackHealthUseCase
import com.loopstack.domain.usecase.StreamCompletionUseCase
import com.loopstack.data.local.dao.SessionLogDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import kotlinx.coroutines.flow.map

data class AgentMessage(
    val text: String,
    val isUser: Boolean = false,
    val isError: Boolean = false
)

@HiltViewModel
class AgentViewModel @Inject constructor(
    private val streamCompletionUseCase: StreamCompletionUseCase,
    observeLoopbackHealthUseCase: ObserveLoopbackHealthUseCase,
    systemMetricsRepository: SystemMetricsRepository,
    sessionLogDao: SessionLogDao
) : ViewModel() {

    private val _messages = mutableStateListOf<AgentMessage>()
    val messages: List<AgentMessage> get() = _messages

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    val loopbackStatus: StateFlow<LoopbackStatus> = observeLoopbackHealthUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoopbackStatus.Inactive
        )

    val systemMetrics: StateFlow<SystemMetrics?> = systemMetricsRepository.observeSystemMetrics()
        .catch { emit(SystemMetrics(0, 0, 0, 0)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val recentLogSummary: StateFlow<String> = sessionLogDao.getAllLogs()
        .map { logs ->
            if (logs.isEmpty()) "No recent logs."
            else logs.take(5).joinToString(separator = " | ") { it.prompt.take(20) + "..." }.take(300)
        }
        .catch { emit("Failed to load logs") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Loading logs..."
        )

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage(contextualPrefix: String = "") {
        val text = _inputText.value
        if (text.isBlank()) return

        _messages.add(AgentMessage(text = text, isUser = true))
        _inputText.value = ""

        val messagesList = if (contextualPrefix.isNotBlank()) {
            listOf(
                MessageDto(role = "system", content = contextualPrefix),
                MessageDto(role = "user", content = text)
            )
        } else {
            listOf(MessageDto(role = "user", content = text))
        }

        viewModelScope.launch {
            try {
                val request = ChatRequestDto(
                    messages = messagesList,
                    stream = true
                )

                var fullResponse = ""
                _messages.add(AgentMessage(text = "", isUser = false))
                val lineIndex = _messages.lastIndex

                streamCompletionUseCase(request)
                    .collect { chunk ->
                        val chunkContent = chunk.choices?.firstOrNull()?.delta?.content ?: ""
                        if (chunkContent.isNotEmpty()) {
                            fullResponse += chunkContent
                            _messages[lineIndex] = AgentMessage(text = fullResponse, isUser = false)
                        }
                    }
            } catch (e: Exception) {
                _messages.add(AgentMessage(text = "Connection failed: ${e.message}", isUser = false, isError = true))
            }
        }
    }

    fun sendQuickAction(action: String, contextStr: String) {
        _inputText.value = action
        sendMessage(contextStr)
    }
}
