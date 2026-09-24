package com.loopstack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loopstack.data.local.entity.SessionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionLogDao {
    @Query("SELECT * FROM session_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<SessionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SessionLogEntity)

    @Query("DELETE FROM session_logs WHERE id = :id")
    suspend fun deleteLogById(id: String)

    @Query("DELETE FROM session_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM session_logs")
    suspend fun getLogCount(): Int
}
