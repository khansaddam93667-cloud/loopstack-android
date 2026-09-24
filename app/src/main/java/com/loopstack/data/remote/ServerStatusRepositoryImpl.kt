package com.loopstack.data.remote

import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.repository.ServerStatusRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class ModelsResponse(val data: List<ModelData>)

@Serializable
data class ModelData(val id: String)

class ServerStatusRepositoryImpl @Inject constructor() : ServerStatusRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 1500L
            requestTimeoutMillis = 1500L
            socketTimeoutMillis = 1500L
        }
    }

    override fun observeServerStatus(): Flow<LoopbackStatus> = flow {
        while (true) {
            val status = try {
                val response = client.get("http://127.0.0.1:20128/v1/models")
                if (response.status.isSuccess()) {
                    val body = response.bodyAsText()
                    val providerName = try {
                        val parsed = json.decodeFromString<ModelsResponse>(body)
                        parsed.data.firstOrNull()?.id ?: "Termux Active"
                    } catch (e: Exception) {
                        "Termux Active"
                    }
                    LoopbackStatus.Active(providerName)
                } else {
                    LoopbackStatus.Inactive
                }
            } catch (e: Exception) {
                LoopbackStatus.Inactive
            }

            emit(status)
            delay(10_000L) // Adjust the polling interval if necessary
        }
    }
}
