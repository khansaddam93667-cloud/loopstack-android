package com.loopstack.core.network

import com.loopstack.data.remote.dto.ChatChunkDto
import com.loopstack.data.remote.dto.ChatRequestDto
import com.loopstack.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import io.ktor.client.engine.HttpClientEngine
import javax.inject.Inject

import com.loopstack.domain.repository.ServerStatusRepository

class LoopbackHttpClient @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val serverStatusRepository: ServerStatusRepository,
    engine: HttpClientEngine = OkHttp.create()
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val client = HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000L
            requestTimeoutMillis = 60_000L
            socketTimeoutMillis = 60_000L
        }
    }

    val healthClient = HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 3_000L
            requestTimeoutMillis = 3_000L
            socketTimeoutMillis = 3_000L
        }
    }

    suspend fun getBaseUrl(): String {
        val port = settingsRepository.routerPort.first()
        val pathPrefix = settingsRepository.apiPathPrefix.first()
        val formattedPathPrefix = if (pathPrefix.startsWith("/")) pathPrefix else "/$pathPrefix"
        return "http://127.0.0.1:$port$formattedPathPrefix"
    }

    suspend fun getApiKey(): String {
        return settingsRepository.apiKey.first()
    }

    suspend fun streamCompletion(request: ChatRequestDto): Flow<ChatChunkDto> = flow {
        val requestBody = json.encodeToString(request)
        val baseUrl = getBaseUrl()
        val token = getApiKey()

        try {
            client.preparePost("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
                setBody(requestBody)
            }.execute { response ->
                if (response.status.value !in 200..299) {
                    throw Exception("HTTP ${response.status.value}: ${response.status.description}")
                }
                val channel = response.bodyAsChannel()
                serverStatusRepository.forceActiveStatus("OmniRoute Local")
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
        } catch (e: Exception) {
            throw e
        }
    }
}
