package com.loopstack.domain.usecase

import com.loopstack.core.network.LoopbackHttpClient
import com.loopstack.domain.model.LoopbackStatus
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.TimeoutCancellationException
import io.ktor.client.engine.mock.respondError

class ObserveLoopbackHealthUseCaseTest {

    @Test
    fun `health check returns Active when status is OK`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = "OK",
                status = HttpStatusCode.OK
            )
        }

        val client = LoopbackHttpClient(mockEngine)
        val useCase = ObserveLoopbackHealthUseCase(client)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Active, status)
    }

    @Test
    fun `health check returns Degraded when status is not OK`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = "Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val client = LoopbackHttpClient(mockEngine)
        val useCase = ObserveLoopbackHealthUseCase(client)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Degraded, status)
    }

    @Test
    fun `health check returns Inactive on exception`() = runBlocking {
        val mockEngine = MockEngine { request ->
            throw RuntimeException("Timeout")
        }

        val client = LoopbackHttpClient(mockEngine)
        val useCase = ObserveLoopbackHealthUseCase(client)

        val status = useCase().first()
        assertEquals(LoopbackStatus.Inactive, status)
    }
}
