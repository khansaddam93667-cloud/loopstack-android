import re

with open('./app/src/main/java/com/loopstack/data/remote/ServerStatusRepositoryImpl.kt', 'r') as f:
    content = f.read()

# Add Log import
content = re.sub(r'import javax.inject.Inject', 'import javax.inject.Inject\nimport android.util.Log', content)

# Fix Json configuration
content = re.sub(r'private val json = Json \{ ignoreUnknownKeys = true \}', 'private val json = Json { ignoreUnknownKeys = true; isLenient = true }', content)

# Fix try/catch inside observeServerStatus
old_try_catch = """            val status = try {
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
            }"""

new_try_catch = """            val status = try {
                val response = client.get("http://127.0.0.1:20128/v1/models")
                Log.d("OmniRoutePing", "Response status: ${response.status}")
                if (response.status.isSuccess()) {
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
            }"""

content = content.replace(old_try_catch, new_try_catch)

with open('./app/src/main/java/com/loopstack/data/remote/ServerStatusRepositoryImpl.kt', 'w') as f:
    f.write(content)
