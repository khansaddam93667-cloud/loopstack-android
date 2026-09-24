package com.loopstack.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.presentation.dashboard.DashboardTopBar
import com.loopstack.presentation.dashboard.DashboardViewModel
import com.loopstack.presentation.dashboard.ExpandableToolchainSection
import com.loopstack.presentation.dashboard.SecondaryWorkflowBanner
import com.loopstack.presentation.dashboard.TerminalViewportEntryPoint
import com.loopstack.presentation.herocard.HeroCard
import com.loopstack.presentation.herocard.HeroCardViewModel
import com.loopstack.presentation.toolgrid.ToolGrid

// Retain non-requested dialogs as dialogs
import com.loopstack.presentation.dashboard.DatabaseDialog
import com.loopstack.presentation.dashboard.ModelGatewayDialog
import com.loopstack.presentation.dashboard.NetworkStatusDialog
import com.loopstack.presentation.settings.SettingsDialog

@Composable
fun HomeScreen(
    onNavigateToTerminal: () -> Unit,
    onNavigateTo: (String) -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    heroCardViewModel: HeroCardViewModel = hiltViewModel()
) {
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val heroCardUiState by heroCardViewModel.uiState.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNetworkStatusDialog by remember { mutableStateOf(false) }
    var showModelGatewayDialog by remember { mutableStateOf(false) }
    var showDatabaseDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    if (showNetworkStatusDialog) {
        NetworkStatusDialog(onDismiss = { showNetworkStatusDialog = false })
    }

    if (showModelGatewayDialog) {
        ModelGatewayDialog(onDismiss = { showModelGatewayDialog = false })
    }

    if (showDatabaseDialog) {
        DatabaseDialog(onDismiss = { showDatabaseDialog = false })
    }

    Scaffold(
        topBar = { DashboardTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).navigationBarsPadding().imePadding()
        ) {
            item(key = "hero_card") {
                HeroCard(uiState = heroCardUiState)
            }
            item(key = "tool_grid") {
                ToolGrid(
                    tools = dashboardUiState.tools,
                    onToolClick = { tool ->
                        when (tool.title) {
                            "Settings" -> showSettingsDialog = true
                            "Network / Status" -> showNetworkStatusDialog = true
                            "History Logs" -> onNavigateTo("history")
                            "Analytics / System" -> onNavigateTo("analytics")
                            "Model Gateway" -> showModelGatewayDialog = true
                            "Database" -> showDatabaseDialog = true
                            "Security" -> onNavigateTo("security")
                            "Plugins" -> onNavigateTo("plugins")
                            else -> {}
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
