package com.loopstack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    val model: String = "openai/auto",
    val messages: List<MessageDto>,
    val stream: Boolean = true
)
