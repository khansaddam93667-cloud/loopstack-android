package com.loopstack.data.agent.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolDefinition(
    val type: String = "function",
    val function: FunctionDefinition
)

@Serializable
data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: ParametersSchema
)

@Serializable
data class ParametersSchema(
    val type: String = "object",
    val properties: Map<String, PropertySchema>,
    val required: List<String> = emptyList()
)

@Serializable
data class PropertySchema(
    val type: String,
    val description: String
)
