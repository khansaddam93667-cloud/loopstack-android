package com.loopstack.presentation.toolgrid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.loopstack.presentation.dashboard.ToolCardModel

@Composable
fun ToolCard(tool: ToolCardModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = tool.title, style = MaterialTheme.typography.titleMedium)
            Text(text = tool.subtitle, style = MaterialTheme.typography.bodySmall)
            if (tool.badgeCount > 0) {
                Text(
                    text = "${tool.badgeCount} notifications",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
