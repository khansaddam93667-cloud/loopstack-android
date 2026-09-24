package com.loopstack.presentation.herocard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.loopstack.core.theme.neonGlow
import com.loopstack.core.theme.CyberViolet
import com.loopstack.core.theme.glassmorphic

@Composable
fun HeroCard(uiState: HeroCardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .glassmorphic(alpha = 0.85f)
            .neonGlow(color = CyberViolet, alpha = 0.5f, radius = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("LoopStack Control", style = MaterialTheme.typography.titleLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                when (uiState) {
                    is HeroCardUiState.Active -> {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Termux Active") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${uiState.latency}ms latency")
                    }
                    is HeroCardUiState.Degraded -> {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Degraded") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.fallbackModel.name)
                    }
                    is HeroCardUiState.Inactive -> {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Inactive") }
                        )
                    }
                    is HeroCardUiState.Checking -> {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Checking...") }
                        )
                    }
                }
            }
        }
    }
}
