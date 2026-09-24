package com.loopstack.presentation.toolgrid

import androidx.compose.foundation.clickable
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
import com.loopstack.core.theme.neonGlow
import com.loopstack.core.theme.ElectricCyan
import com.loopstack.core.theme.glassmorphic

@Composable
fun ToolCard(tool: ToolCardModel, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .glassmorphic(alpha = 0.9f)
            .neonGlow(color = ElectricCyan, alpha = 0.3f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = tool.subtitle, style = MaterialTheme.typography.bodySmall)
            if (tool.badgeCount > 0) {
                Text(
                    text = "${tool.badgeCount} notifications",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
