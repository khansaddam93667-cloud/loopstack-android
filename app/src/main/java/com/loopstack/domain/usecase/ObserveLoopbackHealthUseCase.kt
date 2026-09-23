package com.loopstack.domain.usecase

import com.loopstack.core.network.LoopbackHttpClient
import com.loopstack.domain.model.LoopbackStatus
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveLoopbackHealthUseCase @Inject constructor(
    private val httpClient: LoopbackHttpClient
) {
    operator fun invoke(): Flow<LoopbackStatus> = flow {
        while (true) {
            val status = try {
                val response = httpClient.healthClient.get("${httpClient.baseUrl}/health")
                if (response.status.isSuccess()) {
                    LoopbackStatus.Active
                } else {
                    LoopbackStatus.Degraded
                }
            } catch (e: Exception) {
                LoopbackStatus.Inactive
            }
            emit(status)
            delay(10_000L)
        }
    }
}
