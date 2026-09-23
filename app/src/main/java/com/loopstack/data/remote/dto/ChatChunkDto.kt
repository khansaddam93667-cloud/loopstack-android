package com.loopstack.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatChunkDto(
    val delta: String? = null,
    val model: String? = null,
    val finishReason: String? = null,
    val isFallback: Boolean = false
)
