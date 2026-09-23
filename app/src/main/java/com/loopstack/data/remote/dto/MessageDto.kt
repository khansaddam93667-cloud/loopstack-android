package com.loopstack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val role: String,
    val content: String
)
