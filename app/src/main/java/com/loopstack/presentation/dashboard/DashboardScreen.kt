package com.loopstack.presentation.dashboard

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

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNetworkStatusDialog by remember { mutableStateOf(false) }
    var showHistoryLogsDialog by remember { mutableStateOf(false) }
    var showAnalyticsSystemDialog by remember { mutableStateOf(false) }
    var showModelGatewayDialog by remember { mutableStateOf(false) }
    var genericStatusDialogTitle by remember { mutableStateOf<String?>(null) }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }

    if (showNetworkStatusDialog) {
        NetworkStatusDialog(onDismiss = { showNetworkStatusDialog = false })
    }

    if (showHistoryLogsDialog) {
        HistoryLogsDialog(onDismiss = { showHistoryLogsDialog = false })
    }

    if (showAnalyticsSystemDialog) {
        AnalyticsSystemDialog(onDismiss = { showAnalyticsSystemDialog = false })
    }

    if (showModelGatewayDialog) {
        ModelGatewayDialog(onDismiss = { showModelGatewayDialog = false })
    }

    genericStatusDialogTitle?.let { title ->
        GenericStatusDialog(
            title = title,
            onDismiss = { genericStatusDialogTitle = null }
        )
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
                            "History Logs" -> showHistoryLogsDialog = true
                            "Analytics / System" -> showAnalyticsSystemDialog = true
                            "Model Gateway" -> showModelGatewayDialog = true
                            else -> genericStatusDialogTitle = tool.title
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
