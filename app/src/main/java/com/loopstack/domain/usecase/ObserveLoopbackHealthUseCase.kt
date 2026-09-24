package com.loopstack.domain.usecase

import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.repository.ServerStatusRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLoopbackHealthUseCase @Inject constructor(
    private val serverStatusRepository: ServerStatusRepository
) {
    operator fun invoke(): Flow<LoopbackStatus> = serverStatusRepository.observeServerStatus()
}
