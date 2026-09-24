package com.loopstack.core.di

import android.content.Context
import androidx.room.Room
import com.loopstack.data.local.dao.FileRegistryDao
import com.loopstack.data.local.dao.SessionLogDao
import com.loopstack.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "loopstack_db"
        ).build()
    }

    @Provides
    fun provideSessionLogDao(appDatabase: AppDatabase): SessionLogDao {
        return appDatabase.sessionLogDao()
    }

    @Provides
    fun provideFileRegistryDao(appDatabase: AppDatabase): FileRegistryDao {
        return appDatabase.fileRegistryDao()
    }
}
