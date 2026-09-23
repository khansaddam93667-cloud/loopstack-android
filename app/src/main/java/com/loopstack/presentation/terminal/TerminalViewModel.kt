package com.loopstack.presentation.terminal

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.data.remote.dto.ChatChunkDto
import com.loopstack.domain.model.TerminalLine
import com.loopstack.domain.usecase.StreamCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val streamCompletionUseCase: StreamCompletionUseCase
) : ViewModel() {

    private val _terminalLines = mutableStateListOf<TerminalLine>()
    val terminalLines: List<TerminalLine> get() = _terminalLines

    fun isAtBottom(lazyListState: LazyListState): State<Boolean> {
        return derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                true
            } else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true
                lastVisibleItem.index == layoutInfo.totalItemsCount - 1
            }
        }
    }

    init {
        viewModelScope.launch {
            streamCompletionUseCase()
                .chunked(60L)
                .collect { chunks ->
                    val combinedText = chunks.joinToString(separator = "") { it.delta ?: "" }
                    if (combinedText.isNotEmpty()) {
                        _terminalLines.add(TerminalLine(text = combinedText))
                        while (_terminalLines.size > 2000) {
                            _terminalLines.removeAt(0)
                        }
                    }
                }
        }
    }
}

// A custom operator to buffer items for a given duration
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
