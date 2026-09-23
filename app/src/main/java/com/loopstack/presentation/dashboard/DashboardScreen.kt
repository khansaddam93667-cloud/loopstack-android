package com.loopstack.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.presentation.herocard.HeroCard
import com.loopstack.presentation.herocard.HeroCardViewModel
import com.loopstack.presentation.toolgrid.ToolGrid
import com.loopstack.presentation.settings.SettingsDialog

@Composable
fun DashboardScreen(
    onNavigateToTerminal: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    heroCardViewModel: HeroCardViewModel = hiltViewModel()
) {
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val heroCardUiState by heroCardViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    Scaffold(
        topBar = { DashboardTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item(key = "hero_card") {
                HeroCard(uiState = heroCardUiState)
            }
            item(key = "tool_grid") {
                ToolGrid(
                    tools = dashboardUiState.tools,
                    onToolClick = { tool ->
                        if (tool.title == "Settings") {
                            showSettingsDialog = true
                        } else {
                            Toast.makeText(context, "${tool.title} clicked", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            item(key = "secondary_banner") {
                SecondaryWorkflowBanner()
            }
            item(key = "expandable_toolchain") {
                ExpandableToolchainSection()
            }
            item(key = "terminal_entry") {
                TerminalViewportEntryPoint(onNavigateToTerminal = onNavigateToTerminal)
            }
        }
    }
}
