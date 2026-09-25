package com.loopstack.data.agent.util

import org.junit.Assert.assertEquals
import org.junit.Test

class OutputTruncatorTest {

    @Test
    fun `truncate returns original output when length is less than maxChars`() {
        val input = "Hello World!"
        val result = OutputTruncator.truncate(input, maxChars = 20)
        assertEquals("Hello World!", result)
    }

    @Test
    fun `truncate strips ANSI codes successfully`() {
        val input = "\u001B[31mError\u001B[0m occurred!"
        val result = OutputTruncator.truncate(input, maxChars = 20)
        assertEquals("Error occurred!", result)
    }

    @Test
    fun `truncate applies truncation when output exceeds maxChars`() {
        val input = "A".repeat(100) // length 100
        val maxChars = 20
        // Expected head size: 20 * 0.25 = 5
        // Expected tail size: 20 * 0.75 = 15
        // Truncated count: 100 - 20 = 80
        val head = "A".repeat(5)
        val tail = "A".repeat(15)
        val separator = "\n\n... [TRUNCATED 80 CHARACTERS TO FIT CONTEXT WINDOW] ...\n\n"

        val expected = "$head$separator$tail"
        val result = OutputTruncator.truncate(input, maxChars)

        assertEquals(expected, result)
    }
}
