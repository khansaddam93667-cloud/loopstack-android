package com.loopstack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeltaDto(
    val content: String? = null
)

@Serializable
data class ChoiceDto(
    val index: Int = 0,
    val delta: DeltaDto? = null,
    val finish_reason: String? = null
)

@Serializable
data class ChatChunkDto(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<ChoiceDto>? = null,
    val isFallback: Boolean = false // Keep existing properties if needed by other components, though we could just rely on OpenAI schema.
)
