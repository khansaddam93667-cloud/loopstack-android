package com.loopstack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "session_logs")
data class SessionLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val prompt: String,
    val response: String
)
