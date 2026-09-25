package com.loopstack.data.agent.parser

import com.loopstack.data.agent.model.CompletedToolCall
import com.loopstack.data.agent.model.ToolCall

class SseChunkAggregator {
    private val nameBuffers = mutableMapOf<String, StringBuilder>()
    private val argumentBuffers = mutableMapOf<String, StringBuilder>()

    fun appendChunk(toolCallChunk: ToolCall) {
        val id = toolCallChunk.id

        toolCallChunk.function.name?.let { namePart ->
            nameBuffers.getOrPut(id) { StringBuilder() }.append(namePart)
        }

        toolCallChunk.function.arguments?.let { argsPart ->
            argumentBuffers.getOrPut(id) { StringBuilder() }.append(argsPart)
        }
    }

    fun getCompletedToolCalls(): List<CompletedToolCall> {
        val allIds = (nameBuffers.keys + argumentBuffers.keys).toSet()
        return allIds.map { id ->
            CompletedToolCall(
                id = id,
                name = nameBuffers[id]?.toString() ?: "",
                argumentsJson = argumentBuffers[id]?.toString() ?: ""
            )
        }
    }

    fun reset() {
        nameBuffers.clear()
        argumentBuffers.clear()
    }
}
