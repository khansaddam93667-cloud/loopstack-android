package com.loopstack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.loopstack.data.local.entity.SessionLogEntity
import com.loopstack.data.local.entity.FileRegistryEntity
import com.loopstack.data.local.dao.SessionLogDao
import com.loopstack.data.local.dao.FileRegistryDao

@Database(entities = [SessionLogEntity::class, FileRegistryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionLogDao(): SessionLogDao
    abstract fun fileRegistryDao(): FileRegistryDao
}
