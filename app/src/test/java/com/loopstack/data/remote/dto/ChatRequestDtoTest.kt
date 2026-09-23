package com.loopstack.data.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRequestDtoTest {
    @Test
    fun `default model is openai-auto and serialized correctly`() {
        val request = ChatRequestDto(
            messages = listOf(MessageDto(role = "user", content = "hello"))
        )
        val jsonString = Json.encodeToString(request)
        assertTrue(jsonString.contains("\"model\":\"openai/auto\""))
    }
}
