package com.loopstack.data.agent.util

object CommandValidator {

    private val blockedPatterns = listOf(
        Regex("""\bsu\b"""),
        Regex("""\bmkfs\b"""),
        Regex("""\bdd\s+if=""")
    )

    private val confirmationRequiredPatterns = listOf(
        Regex("""\brm\s+-r[fF]?\b"""),
        Regex("""\bchmod\s+777\b"""),
        Regex("""\bgit\s+push\b"""),
        Regex("""\bgit\s+reset\s+--hard\b"""),
        Regex("""\bpkill\b"""),
        Regex("""\bkill\s+-9\b""")
    )

    fun evaluateCommand(command: String): CommandSafetyLevel {
        for (pattern in blockedPatterns) {
            if (pattern.containsMatchIn(command)) {
                return CommandSafetyLevel.BLOCKED
            }
        }

        for (pattern in confirmationRequiredPatterns) {
            if (pattern.containsMatchIn(command)) {
                return CommandSafetyLevel.REQUIRES_CONFIRMATION
            }
        }

        return CommandSafetyLevel.SAFE
    }
}
