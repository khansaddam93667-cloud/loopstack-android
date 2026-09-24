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
import android.util.Log
import com.loopstack.domain.repository.SettingsRepository
import io.ktor.client.request.header
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Serializable
data class ModelsResponse(val data: List<ModelData>)

@Serializable
data class ModelData(val id: String)



class ServerStatusRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ServerStatusRepository {

    private val manualStatusUpdates = MutableSharedFlow<LoopbackStatus>(extraBufferCapacity = 1)

    override fun forceActiveStatus(providerName: String) {
        manualStatusUpdates.tryEmit(LoopbackStatus.Active(providerName))
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 1500L
            requestTimeoutMillis = 1500L
            socketTimeoutMillis = 1500L
        }
    }

    override fun observeServerStatus(): Flow<LoopbackStatus> = channelFlow {
        launch {
            manualStatusUpdates.collect {
                send(it)
            }
        }
        launch {
        while (true) {
            val status = try {
                val apiKey = settingsRepository.apiKey.first()
                val token = if (apiKey.isNotBlank()) apiKey else "local"
                val response = client.get("http://127.0.0.1:20128/v1/models") {
                    header("Authorization", "Bearer $token")
                }
                Log.d("OmniRoutePing", "Response status: ${response.status}")
                if (response.status.value in 200..299) {
                    val body = response.bodyAsText()
                    val providerName = try {
                        val parsed = json.decodeFromString<ModelsResponse>(body)
                        if (parsed.data.isNotEmpty()) parsed.data.first().id else "OmniRoute Local"
                    } catch (e: Exception) {
                        Log.d("OmniRoutePing", "Exception parsing JSON: ${e.message}")
                        "OmniRoute Local"
                    }
                    LoopbackStatus.Active(providerName)
                } else {
                    LoopbackStatus.Inactive
                }
            } catch (e: Exception) {
                Log.d("OmniRoutePing", "Exception during HTTP ping: ${e.message}")
                LoopbackStatus.Inactive
            }

            send(status)
            delay(10_000L) // Adjust the polling interval if necessary
        }
        }
    }
}
