package com.loopstack.presentation.dashboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import com.loopstack.presentation.agent.LocalAgentDrawerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    val agentDrawerController = LocalAgentDrawerController.current
    TopAppBar(
        title = { Text("Dashboard") },
        actions = {
            IconButton(onClick = { agentDrawerController.toggle() }) {
                Icon(Icons.Default.Face, contentDescription = "Open Agent")
            }
        }
    )
}
