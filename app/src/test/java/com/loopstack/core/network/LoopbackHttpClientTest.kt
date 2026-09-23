package com.loopstack.core.network
import com.loopstack.domain.repository.MockSettingsRepository

import com.loopstack.data.remote.dto.ChatChunkDto
import com.loopstack.data.remote.dto.ChatRequestDto
import com.loopstack.data.remote.dto.MessageDto
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class LoopbackHttpClientTest {

    @Test
    fun `streamCompletion emits chunks successfully`() = runBlocking {
        val sseResponse = """
            data: {"choices":[{"delta":{"content":"Hello"}}]}

            data: {"choices":[{"delta":{"content":" "}}]}

            data: {"choices":[{"delta":{"content":"World"}}]}

            data: {"choices":[{"delta":{"content":"!"}}]}

            data: {"choices":[{"finish_reason":"stop"}]}

            data: [DONE]

        """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = sseResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.EventStream.toString())
            )
        }

        val client = LoopbackHttpClient(MockSettingsRepository(), mockEngine)
        val request = ChatRequestDto(model = "test-model", messages = listOf(MessageDto("user", "hi")))
        val chunks = client.streamCompletion(request).toList()

        assertEquals(5, chunks.size)
        assertEquals("Hello", chunks[0].choices?.firstOrNull()?.delta?.content)
        assertEquals("stop", chunks[4].choices?.firstOrNull()?.finish_reason)
    }

    @Test
    fun `streamCompletion handles fallback correctly`() = runBlocking {
        val sseResponse = """
            data: {"choices":[{"delta":{"content":"Part 1"}}]}

            data: {"isFallback":true}

            data: [DONE]

        """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = sseResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.EventStream.toString())
            )
        }

        val client = LoopbackHttpClient(MockSettingsRepository(), mockEngine)
        val request = ChatRequestDto(model = "test-model", messages = listOf(MessageDto("user", "hi")))
        val chunks = client.streamCompletion(request).toList()

        assertEquals(2, chunks.size)
        assertEquals(true, chunks[1].isFallback)
    }

    @Test
    fun `streamCompletion does not crash on timeout`() = runBlocking {
        val mockEngine = MockEngine { request ->
            throw RuntimeException("Timeout")
        }

        val client = LoopbackHttpClient(MockSettingsRepository(), mockEngine)
        val request = ChatRequestDto(model = "test-model", messages = listOf(MessageDto("user", "hi")))

        var caughtException = false
        try {
            client.streamCompletion(request).toList()
        } catch (e: Exception) {
            caughtException = true
        }

        assertEquals(true, caughtException)
    }
}
