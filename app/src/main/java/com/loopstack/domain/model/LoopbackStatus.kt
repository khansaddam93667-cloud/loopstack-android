package com.loopstack.domain.model

sealed interface LoopbackStatus {
    data class Active(val providerName: String) : LoopbackStatus
    data object Inactive : LoopbackStatus
    data object Degraded : LoopbackStatus
}
