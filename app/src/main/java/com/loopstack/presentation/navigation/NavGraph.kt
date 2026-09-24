package com.loopstack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loopstack.presentation.screens.AnalyticsScreen
import com.loopstack.presentation.screens.HistoryScreen
import com.loopstack.presentation.screens.HomeScreen
import com.loopstack.presentation.screens.PluginsScreen
import com.loopstack.presentation.screens.SecurityScreen
import com.loopstack.presentation.terminal.TerminalScreen
import com.loopstack.presentation.agent.AgentDrawerWrapper

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    AgentDrawerWrapper(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                HomeScreen(
                    onNavigateToTerminal = { navController.navigate("terminal") },
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }
            composable("terminal") {
                TerminalScreen(navController = navController)
            }
            composable("history") { HistoryScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("analytics") { AnalyticsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("security") { SecurityScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("plugins") { PluginsScreen(onNavigateBack = { navController.popBackStack() }) }
        }
    }
}
