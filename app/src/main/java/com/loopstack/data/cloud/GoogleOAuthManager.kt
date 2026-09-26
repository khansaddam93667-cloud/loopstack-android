package com.loopstack.data.cloud

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm

import io.ktor.http.Parameters

import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("scope") val scope: String,
    @SerialName("token_type") val tokenType: String
)

@Singleton
class GoogleOAuthManager @Inject constructor(
    private val httpClient: HttpClient
) {
    private var accessToken: String? = null
    private var expirationTimeMillis: Long = 0
    private val mutex = Mutex()

    companion object {
        const val DRIVE_FILE_SCOPE = "https://www.googleapis.com/auth/drive.file"
        private const val TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
    }

    // Credentials should be injected or set via setCredentials before use


    private var clientId: String? = null
    private var clientSecret: String? = null
    private var refreshToken: String? = null

    fun setCredentials(clientId: String, clientSecret: String, refreshToken: String) {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.refreshToken = refreshToken
    }

    suspend fun getAccessToken(): String? = mutex.withLock {
        val currentTime = System.currentTimeMillis()
        // Refresh token if it's expired or about to expire (within 1 minute)
        if (accessToken == null || currentTime >= expirationTimeMillis - 60_000) {
            refreshAccessToken()
        }
        return accessToken
    }

    private suspend fun refreshAccessToken() {
        try {
            val response = httpClient.submitForm(
                url = TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("client_id", clientId ?: "")
                    append("client_secret", clientSecret ?: "")
                    append("refresh_token", refreshToken ?: "")
                    append("grant_type", "refresh_token")
                }
            )

            if (response.status.isSuccess()) {
                val tokenResponse: TokenResponse = response.body()
                accessToken = tokenResponse.accessToken
                // Calculate expiration time, subtracting a small buffer (e.g., 5 seconds)
                expirationTimeMillis = System.currentTimeMillis() + (tokenResponse.expiresIn * 1000L) - 5000L
            } else {
                // Handle token refresh failure (e.g., revoked refresh token)
                accessToken = null
                expirationTimeMillis = 0
                println("Failed to refresh access token: ${response.status}")
            }
        } catch (e: Exception) {
            accessToken = null
            expirationTimeMillis = 0
            e.printStackTrace()
        }
    }
}
