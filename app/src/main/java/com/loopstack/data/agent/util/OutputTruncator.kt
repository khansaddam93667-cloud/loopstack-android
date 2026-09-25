package com.loopstack.data.agent.util

object OutputTruncator {
    private val ansiRegex = Regex("\u001B\\[[;\\d]*m")

    fun truncate(output: String, maxChars: Int = 8000): String {
        val sanitized = output.replace(ansiRegex, "")

        if (sanitized.length <= maxChars) {
            return sanitized
        }

        val headChars = (maxChars * 0.25).toInt()
        val tailChars = (maxChars * 0.75).toInt()

        val head = sanitized.substring(0, headChars)
        val tail = sanitized.substring(sanitized.length - tailChars)
        val truncatedCount = sanitized.length - maxChars

        val separator = "\n\n... [TRUNCATED $truncatedCount CHARACTERS TO FIT CONTEXT WINDOW] ...\n\n"

        return "$head$separator$tail"
    }
}
