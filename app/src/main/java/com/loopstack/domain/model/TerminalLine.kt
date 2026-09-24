package com.loopstack.domain.model

import java.util.UUID

data class TerminalLine(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isError: Boolean = false,
    val model: String? = null
)
