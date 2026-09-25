package com.loopstack.data.agent.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CommandValidatorTest {

    @Test
    fun testBlockedCommands() {
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("su"))
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("su root"))
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("sudo su"))
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("mkfs -t ext4 /dev/sda1"))
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("sudo mkfs.ext4 /dev/sda1"))
        assertEquals(CommandSafetyLevel.BLOCKED, CommandValidator.evaluateCommand("dd if=/dev/zero of=/dev/sda"))
    }

    @Test
    fun testConfirmationRequiredCommands() {
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("rm -rf /"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("rm -r ./dir"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("rm -rf file.txt"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("chmod 777 file.txt"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("git push origin main"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("git reset --hard HEAD~1"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("pkill java"))
        assertEquals(CommandSafetyLevel.REQUIRES_CONFIRMATION, CommandValidator.evaluateCommand("kill -9 1234"))
    }

    @Test
    fun testSafeCommands() {
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("ls -l"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("cat file.txt"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("python script.py"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("git status"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("git commit -m \"update\""))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("npm install"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("./gradlew assembleDebug"))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("echo \"hello world\""))
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("rm file.txt")) // Basic rm is safe, -rf is confirmation
        assertEquals(CommandSafetyLevel.SAFE, CommandValidator.evaluateCommand("chmod +x script.sh")) // Basic chmod is safe, 777 is confirmation
    }
}
