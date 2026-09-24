package com.loopstack.presentation.dashboard

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnalyticsSystemDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var totalRamGB by remember { mutableStateOf(0.0) }
    var availRamGB by remember { mutableStateOf(0.0) }
    var isLowMemory by remember { mutableStateOf(false) }
    var bgServiceState by remember { mutableStateOf("Unknown") }

    LaunchedEffect(Unit) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        totalRamGB = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        availRamGB = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0)
        isLowMemory = memoryInfo.lowMemory

        val services = activityManager.getRunningServices(Int.MAX_VALUE)
        val isOurServiceRunning = services.any { it.service.packageName == context.packageName }
        bgServiceState = if (isOurServiceRunning) "Active" else "Inactive"
    }

    val usedRamGB = totalRamGB - availRamGB

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Analytics / System Status", color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Text(
                    text = "Device Memory Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format("RAM Usage: %.2f GB / %.2f GB", usedRamGB, totalRamGB),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text("Background Service: $bgServiceState", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))

                if (isLowMemory) {
                    Text(
                        text = "Warning: Low Memory State",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Memory State: Healthy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
