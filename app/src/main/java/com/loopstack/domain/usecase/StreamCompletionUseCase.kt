package com.loopstack.domain.usecase

import com.loopstack.data.remote.dto.ChatChunkDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class StreamCompletionUseCase @Inject constructor() {
    operator fun invoke(): Flow<ChatChunkDto> {
        // Implementation stub
        return emptyFlow()
    }
}
