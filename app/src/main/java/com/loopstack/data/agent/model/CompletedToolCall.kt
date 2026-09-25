package com.loopstack.data.agent.model

data class CompletedToolCall(
    val id: String,
    val name: String,
    val argumentsJson: String
)
