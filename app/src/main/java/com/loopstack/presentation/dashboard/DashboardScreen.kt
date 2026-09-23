package com.loopstack.presentation.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.loopstack.presentation.herocard.HeroCard
import com.loopstack.presentation.herocard.HeroCardViewModel
import com.loopstack.presentation.toolgrid.ToolGrid

@Composable
fun DashboardScreen(
    onNavigateToTerminal: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    heroCardViewModel: HeroCardViewModel = hiltViewModel()
) {
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val heroCardUiState by heroCardViewModel.uiState.collectAsState()

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
                ToolGrid(tools = dashboardUiState.tools)
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
