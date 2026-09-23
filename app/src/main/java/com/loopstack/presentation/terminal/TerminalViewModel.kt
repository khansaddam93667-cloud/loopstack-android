package com.loopstack.presentation.terminal

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.data.remote.dto.ChatRequestDto
import com.loopstack.data.remote.dto.MessageDto
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.model.TerminalLine
import com.loopstack.domain.usecase.ObserveLoopbackHealthUseCase
import com.loopstack.domain.usecase.StreamCompletionUseCase
import com.loopstack.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val streamCompletionUseCase: StreamCompletionUseCase,
    observeLoopbackHealthUseCase: ObserveLoopbackHealthUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _terminalLines = mutableStateListOf<TerminalLine>()
    val terminalLines: List<TerminalLine> get() = _terminalLines

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _isMockMode = MutableStateFlow(false)
    val isMockMode: StateFlow<Boolean> = _isMockMode

    val loopbackStatus: StateFlow<LoopbackStatus> = observeLoopbackHealthUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoopbackStatus.Inactive
        )

    private val _baseUrl = MutableStateFlow("")
    val baseUrl: StateFlow<String> = _baseUrl

    init {
        viewModelScope.launch {
            val port = settingsRepository.routerPort.first()
            val pathPrefix = settingsRepository.apiPathPrefix.first()
            val formattedPathPrefix = if (pathPrefix.startsWith("/")) pathPrefix else "/$pathPrefix"
            _baseUrl.value = "127.0.0.1:$port$formattedPathPrefix"
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun toggleMockMode(enabled: Boolean) {
        _isMockMode.value = enabled
    }

    fun sendMessage() {
        val text = _inputText.value
        if (text.isBlank()) return

        _terminalLines.add(TerminalLine(text = "> $text"))
        _inputText.value = ""

        viewModelScope.launch {
            try {
                val request = ChatRequestDto(
                    messages = listOf(MessageDto(role = "user", content = text)),
                    stream = true
                )

                streamCompletionUseCase(request)
                    .chunked(60L)
                    .collect { chunks ->
                        val combinedText = chunks.joinToString(separator = "") { chunk ->
                            chunk.choices?.firstOrNull()?.delta?.content ?: ""
                        }
                        if (combinedText.isNotEmpty()) {
                            _terminalLines.add(TerminalLine(text = combinedText))
                            while (_terminalLines.size > 2000) {
                                _terminalLines.removeAt(0)
                            }
                        }
                    }
            } catch (e: Exception) {
                _terminalLines.add(TerminalLine(text = "[ERROR] Connection failed: ${e.message}", isError = true))
            }
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.chunked(durationMillis: Long): Flow<List<T>> = flow {
    coroutineScope {
        val channel = Channel<T>(Channel.UNLIMITED)
        val producer = launch {
            collect { value ->
                channel.send(value)
            }
            channel.close()
        }

        var buffer = mutableListOf<T>()
        while (!channel.isClosedForReceive) {
            val result = channel.tryReceive()
            if (result.isSuccess) {
                buffer.add(result.getOrThrow())
            } else if (buffer.isNotEmpty()) {
                emit(buffer.toList())
                buffer = mutableListOf()
            }

            if (buffer.isNotEmpty() && channel.isEmpty) {
                 emit(buffer.toList())
                 buffer = mutableListOf()
            }

            if (channel.isClosedForReceive && buffer.isNotEmpty()) {
                 emit(buffer.toList())
                 buffer = mutableListOf()
                 break
            }

            delay(durationMillis)
        }
        producer.cancel()
    }
}
