package com.loopstack.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SessionLog(val id: String, val date: String, val snippet: String)

@Composable
fun HistoryLogsDialog(
    onDismiss: () -> Unit
) {
    // Simulated database query for history logs since Room is not implemented yet.
    val logs = listOf(
        SessionLog("1", "2023-10-24 14:30", "How do I optimize Jetpack Compose?"),
        SessionLog("2", "2023-10-23 09:15", "Write a Python script for data scraping."),
        SessionLog("3", "2023-10-20 18:45", "Explain quantum computing simply.")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("History Logs") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (logs.isEmpty()) {
                    Text("No past sessions found.")
                } else {
                    logs.forEach { log ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = log.date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.snippet,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
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
