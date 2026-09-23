package com.loopstack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    val model: String,
    val messages: List<MessageDto>,
    val stream: Boolean = true
)
