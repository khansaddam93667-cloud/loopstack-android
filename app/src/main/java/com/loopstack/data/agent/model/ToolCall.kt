package com.loopstack.data.agent.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: FunctionCallChunk
)

@Serializable
data class FunctionCallChunk(
    val name: String? = null,
    val arguments: String? = null
)
