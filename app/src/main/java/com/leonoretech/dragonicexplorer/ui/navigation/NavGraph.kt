package com.leonoretech.dragonicexplorer.ui.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp   // ← baris ini yang ditambahin
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leonoretech.dragonicexplorer.ui.downloads.DownloadsScreen
import com.leonoretech.dragonicexplorer.ui.history.HistoryScreen
import com.leonoretech.dragonicexplorer.ui.home.HomeScreen
import com.leonoretech.dragonicexplorer.ui.newscan.NewScanScreen
import com.leonoretech.dragonicexplorer.ui.reports.ReportsScreen
import com.leonoretech.dragonicexplorer.ui.settings.SettingsScreen
import com.leonoretech.dragonicexplorer.ui.workflowstatus.WorkflowDetailScreen
import com.leonoretech.dragonicexplorer.ui.workflowstatus.WorkflowStatusScreen

private data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.History, "History", Icons.Filled.History),
    BottomNavItem(Screen.Reports, "Reports", Icons.Filled.Description),
    BottomNavItem(Screen.Downloads, "Downloads", Icons.Filled.Download),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings)
)

@Composable
fun DragonicNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { DragonicBottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToNewScan = { navController.navigate(Screen.NewScan.route) },
                    onNavigateToWorkflowStatus = { navController.navigate(Screen.WorkflowStatus.route) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) }
                )
            }
            composable(Screen.NewScan.route) {
                NewScanScreen(
                    onBack = { navController.popBackStack() },
                    onScanQueued = {
                        navController.navigate(Screen.WorkflowStatus.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
            composable(Screen.WorkflowStatus.route) {
                WorkflowStatusScreen(
                    onBack = { navController.popBackStack() },
                    onRunSelected = { runId -> navController.navigate(Screen.WorkflowDetail.createRoute(runId)) }
                )
            }
            composable(
                route = Screen.WorkflowDetail.route,
                arguments = listOf(navArgument("runId") { type = NavType.LongType })
            ) { backStackEntry ->
                val runId = backStackEntry.arguments?.getLong("runId") ?: -1L
                WorkflowDetailScreen(runId = runId, onBack = { navController.popBackStack() })
            }
            composable(Screen.Reports.route) {
                ReportsScreen(onRunSelected = { runId -> navController.navigate(Screen.WorkflowDetail.createRoute(runId)) })
            }
            composable(Screen.Downloads.route) {
                DownloadsScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
private fun DragonicBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
