package com.loopstack.data.cloud

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class DriveFileList(
    @SerialName("files") val files: List<DriveFile>
)

@Serializable
data class DriveFile(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("md5Checksum") val md5Checksum: String? = null
)

@Singleton
class DriveSyncRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val oAuthManager: GoogleOAuthManager
) {
    companion object {
        private const val DRIVE_API_URL = "https://www.googleapis.com/drive/v3/files"
        private const val DRIVE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3/files"
        private const val WORKSPACE_FOLDER_NAME = "LoopStack-Workspace"
        private const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"
    }

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Verifies or creates a dedicated cloud folder named 'LoopStack-Workspace' on Google Drive.
     * Returns the folder ID.
     */
    suspend fun ensureWorkspaceFolder(): String? = withContext(Dispatchers.IO) {
        val accessToken = oAuthManager.getAccessToken() ?: return@withContext null

        // 1. Check if folder exists
        val query = "name='$WORKSPACE_FOLDER_NAME' and mimeType='$FOLDER_MIME_TYPE' and trashed=false"
        try {
            val response: HttpResponse = httpClient.get(DRIVE_API_URL) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                url {
                    parameters.append("q", query)
                    parameters.append("spaces", "drive")
                    parameters.append("fields", "files(id, name)")
                }
            }

            if (response.status.isSuccess()) {
                val fileList: DriveFileList = response.body()
                if (fileList.files.isNotEmpty()) {
                    return@withContext fileList.files.first().id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }

        // 2. Folder doesn't exist, create it
        try {
            val metadata = buildJsonObject {
                put("name", WORKSPACE_FOLDER_NAME)
                put("mimeType", FOLDER_MIME_TYPE)
            }

            val createResponse: HttpResponse = httpClient.post(DRIVE_API_URL) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(metadata.toString())
            }

            if (createResponse.status.isSuccess()) {
                val createdFolder: DriveFile = createResponse.body()
                return@withContext createdFolder.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }

    /**
     * Calculates local MD5 hash / timestamp before uploading.
     * Performs delta uploads only for updated or new files.
     */
    suspend fun syncFile(file: File, folderId: String) = withContext(Dispatchers.IO) {
        if (!file.exists() || !file.isFile) return@withContext

        val accessToken = oAuthManager.getAccessToken() ?: return@withContext
        val localMd5 = calculateMD5(file)

        // 1. Check if file exists in the cloud folder
        val query = "name='${file.name}' and '$folderId' in parents and trashed=false"
        var existingFileId: String? = null
        var remoteMd5: String? = null

        try {
            val response: HttpResponse = httpClient.get(DRIVE_API_URL) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                url {
                    parameters.append("q", query)
                    parameters.append("spaces", "drive")
                    parameters.append("fields", "files(id, name, md5Checksum)")
                }
            }

            if (response.status.isSuccess()) {
                val fileList: DriveFileList = response.body()
                if (fileList.files.isNotEmpty()) {
                    val existingFile = fileList.files.first()
                    existingFileId = existingFile.id
                    remoteMd5 = existingFile.md5Checksum
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext
        }

        // 2. Compare hashes (delta sync)
        if (existingFileId != null && localMd5 == remoteMd5) {
            println("Skipping sync for ${file.name}: Hashes match.")
            return@withContext
        }

        // 3. Upload file (New or Update)
        try {
            val mimeType = ContentType.Application.OctetStream.toString() // Fallback, could be more specific

            // Step 1 of Resumable Upload: Initiate session
            val metadata = buildJsonObject {
                put("name", file.name)
                if (existingFileId == null) {
                    put("parents", buildJsonObject {
                        // put doesn't support JsonArray easily without full DSL, use simple format
                    })
                }
            }

            // Correct parent structure
            val metadataString = if (existingFileId == null) {
                """{"name": "${file.name}", "parents": ["$folderId"]}"""
            } else {
                """{"name": "${file.name}"}"""
            }

            val uploadUrl = if (existingFileId == null) {
                "$DRIVE_UPLOAD_URL?uploadType=resumable" // Create
            } else {
                "$DRIVE_UPLOAD_URL/$existingFileId?uploadType=resumable" // Update
            }

            val httpMethod = if (existingFileId == null) io.ktor.http.HttpMethod.Post else io.ktor.http.HttpMethod.Patch

            val initResponse = httpClient.request(uploadUrl) {
                method = httpMethod
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header("X-Upload-Content-Type", mimeType)
                header("X-Upload-Content-Length", file.length().toString())
                contentType(ContentType.Application.Json)
                setBody(metadataString)
            }

            if (initResponse.status.isSuccess()) {
                val locationUrl = initResponse.headers[HttpHeaders.Location] ?: return@withContext

                // Step 2: Upload the actual file data
                val fileBytes = file.readBytes()
                val uploadDataResponse = httpClient.request(locationUrl) {
                    method = io.ktor.http.HttpMethod.Put
                    header(HttpHeaders.ContentLength, file.length().toString())
                    setBody(fileBytes)
                }

                if (uploadDataResponse.status.isSuccess()) {
                    println("Successfully synced ${file.name}")
                } else {
                    println("Failed to upload data for ${file.name}: ${uploadDataResponse.status}")
                }
            } else {
                println("Failed to initiate upload for ${file.name}: ${initResponse.status}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Recursively scan workspace files and sync modified files to the cloud folder.
     */
    suspend fun syncWorkspace(workspaceDir: File) = withContext(Dispatchers.IO) {
        if (!workspaceDir.exists() || !workspaceDir.isDirectory) return@withContext

        val folderId = ensureWorkspaceFolder() ?: run {
            println("Failed to ensure workspace folder. Sync aborted.")
            return@withContext
        }

        workspaceDir.walkTopDown().forEach { file ->
            if (file.isFile) {
                syncFile(file, folderId)
            }
        }
    }

    private fun calculateMD5(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = file.readBytes()
        val md5Bytes = digest.digest(bytes)
        return md5Bytes.joinToString("") { "%02x".format(it) }
    }
}
