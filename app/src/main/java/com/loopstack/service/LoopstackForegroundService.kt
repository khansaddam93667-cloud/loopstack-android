package com.loopstack.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.repository.ServerStatusRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoopstackForegroundService : Service() {

    @Inject
    lateinit var serverStatusRepository: ServerStatusRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val initialNotification = notificationHelper.getNotification("Initializing...", "Starting")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                initialNotification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                }
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID, initialNotification)
        }

        observeStatus()

        return START_STICKY
    }

    private fun observeStatus() {
        serviceScope.launch {
            serverStatusRepository.observeServerStatus().collect { status ->
                val (providerName, healthStatus) = when (status) {
                    is LoopbackStatus.Active -> Pair(status.providerName, "Healthy")
                    is LoopbackStatus.Degraded -> Pair("Unknown", "Degraded")
                    is LoopbackStatus.Inactive -> Pair("None", "Disconnected")
                }
                notificationHelper.updateNotification(providerName, healthStatus)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
