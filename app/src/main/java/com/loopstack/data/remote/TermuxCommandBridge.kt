package com.loopstack.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log

class TermuxCommandBridge(private val context: Context) {

    fun executeCommand(
        path: String,
        arguments: Array<String> = emptyArray(),
        workDir: String? = null
    ) {
        try {
            val intent = Intent().apply {
                setClassName("com.termux", "com.termux.app.RunCommandService")
                action = "com.termux.RUN_COMMAND"
                putExtra("com.termux.RUN_COMMAND_PATH", path)
                putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arguments)
                if (workDir != null) {
                    putExtra("com.termux.RUN_COMMAND_WORKDIR", workDir)
                }
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
            }
            context.startService(intent)
            Log.d("TermuxCommandBridge", "Started Termux RunCommandService for: \$path")
        } catch (e: Exception) {
            Log.e("TermuxCommandBridge", "Failed to start Termux RunCommandService", e)
        }
    }
}
