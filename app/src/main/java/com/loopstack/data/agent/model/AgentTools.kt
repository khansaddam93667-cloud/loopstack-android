package com.loopstack.data.agent.model

object AgentTools {

    val executeCommandTool = ToolDefinition(
        function = FunctionDefinition(
            name = "execute_command",
            description = "Executes a shell/bash command inside the local Termux environment",
            parameters = ParametersSchema(
                properties = mapOf(
                    "command" to PropertySchema(
                        type = "string",
                        description = "The shell/bash command to execute"
                    )
                ),
                required = listOf("command")
            )
        )
    )

    val writeFileTool = ToolDefinition(
        function = FunctionDefinition(
            name = "write_file",
            description = "Writes or updates text content in a file at the specified path",
            parameters = ParametersSchema(
                properties = mapOf(
                    "path" to PropertySchema(
                        type = "string",
                        description = "The path to the file to write to"
                    ),
                    "content" to PropertySchema(
                        type = "string",
                        description = "The text content to write to the file"
                    )
                ),
                required = listOf("path", "content")
            )
        )
    )

    val readFileTool = ToolDefinition(
        function = FunctionDefinition(
            name = "read_file",
            description = "Reads text content from a file at the specified path",
            parameters = ParametersSchema(
                properties = mapOf(
                    "path" to PropertySchema(
                        type = "string",
                        description = "The path to the file to read"
                    )
                ),
                required = listOf("path")
            )
        )
    )

    val allTools = listOf(
        executeCommandTool,
        writeFileTool,
        readFileTool
    )
}
