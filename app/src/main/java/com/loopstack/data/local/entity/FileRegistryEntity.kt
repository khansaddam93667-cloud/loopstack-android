package com.loopstack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "file_registry")
data class FileRegistryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val filePath: String,
    val createdAt: Long = System.currentTimeMillis()
)
