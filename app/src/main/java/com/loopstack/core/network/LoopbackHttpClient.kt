package com.loopstack.core.network

import com.loopstack.BuildConfig
import com.loopstack.data.remote.dto.ChatChunkDto
import com.loopstack.data.remote.dto.ChatRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import io.ktor.client.engine.HttpClientEngine

class LoopbackHttpClient(
    engine: HttpClientEngine = OkHttp.create()
) {
    private val json = Json { ignoreUnknownKeys = true }

    val baseUrl = "http://127.0.0.1:${BuildConfig.LOOPBACK_PORT}"

    private val client = HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 20_000L
            requestTimeoutMillis = 30_000L
            socketTimeoutMillis = 30_000L
        }
    }

    val healthClient = HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 3_000L
            requestTimeoutMillis = 3_000L
            socketTimeoutMillis = 3_000L
        }
    }

    suspend fun streamCompletion(request: ChatRequestDto): Flow<ChatChunkDto> = flow {
        val requestBody = json.encodeToString(request)

        client.preparePost("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.execute { response ->
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line(limit = 8192)
                if (line != null && line.startsWith("data: ")) {
                    val dataStr = line.removePrefix("data: ").trim()
                    if (dataStr == "[DONE]") {
                        break
                    }
                    if (dataStr.isNotEmpty()) {
                        try {
                            val chunk = json.decodeFromString<ChatChunkDto>(dataStr)
                            emit(chunk)
                        } catch (e: Exception) {
                            // Ignored or handle parsing error
                        }
                    }
                }
            }
        }
    }
}
