package com.loopstack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loopstack.data.local.entity.FileRegistryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileRegistryDao {
    @Query("SELECT * FROM file_registry ORDER BY createdAt DESC")
    fun getAllFiles(): Flow<List<FileRegistryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileRegistryEntity)

    @Query("DELETE FROM file_registry")
    suspend fun clearAllFiles()
}
