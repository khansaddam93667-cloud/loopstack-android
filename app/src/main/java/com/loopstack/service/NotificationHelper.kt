package com.loopstack.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.loopstack.MainActivity
import com.loopstack.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "loopstack_bridge_channel"
        const val NOTIFICATION_ID = 20128
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "LoopStack Bridge Service"
            val descriptionText = "Maintains connection and background health monitoring."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotification(providerName: String, healthStatus: String): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("LoopStack AI Bridge Active")
            .setContentText("Provider: $providerName | Status: $healthStatus")
            .setSmallIcon(R.mipmap.ic_launcher) // Using launcher icon as placeholder, Android might tint this to white block if not transparent
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(providerName: String, healthStatus: String) {
        val notification = getNotification(providerName, healthStatus)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
