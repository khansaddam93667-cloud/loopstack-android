package com.loopstack.domain.system

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SystemMetricsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemMetricsRepository {

    override fun observeSystemMetrics(): Flow<SystemMetrics> = flow {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        while (true) {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val statFs = try {
                StatFs(Environment.getDataDirectory().path)
            } catch (e: Exception) {
                null
            }

            val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
            val availableRamMb = memoryInfo.availMem / (1024 * 1024)

            val totalStorageGb = statFs?.totalBytes?.div(1024 * 1024 * 1024) ?: 0L
            val freeStorageGb = statFs?.availableBytes?.div(1024 * 1024 * 1024) ?: 0L

            emit(
                SystemMetrics(
                    totalRamMb = totalRamMb,
                    availableRamMb = availableRamMb,
                    totalStorageGb = totalStorageGb,
                    freeStorageGb = freeStorageGb
                )
            )
            delay(2000)
        }
    }
}
