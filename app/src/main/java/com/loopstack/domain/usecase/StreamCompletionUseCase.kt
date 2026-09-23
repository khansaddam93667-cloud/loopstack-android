package com.loopstack.domain.usecase

import com.loopstack.core.network.LoopbackHttpClient
import com.loopstack.data.remote.dto.ChatChunkDto
import com.loopstack.data.remote.dto.ChatRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamCompletionUseCase @Inject constructor(
    private val httpClient: LoopbackHttpClient
) {
    suspend operator fun invoke(request: ChatRequestDto): Flow<ChatChunkDto> {
        return httpClient.streamCompletion(request)
    }
}
