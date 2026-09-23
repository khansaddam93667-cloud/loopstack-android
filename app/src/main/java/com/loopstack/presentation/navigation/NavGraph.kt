package com.loopstack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loopstack.presentation.dashboard.DashboardScreen
import com.loopstack.presentation.terminal.TerminalScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToTerminal = { navController.navigate("terminal") }
            )
        }
        composable("terminal") {
            TerminalScreen()
        }
    }
}
