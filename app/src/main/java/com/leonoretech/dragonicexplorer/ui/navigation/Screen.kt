package com.leonoretech.dragonicexplorer.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NewScan : Screen("new_scan")
    object WorkflowStatus : Screen("workflow_status")
    object WorkflowDetail : Screen("workflow_detail/{runId}") {
        fun createRoute(runId: Long) = "workflow_detail/$runId"
    }
    object Reports : Screen("reports")
    object Downloads : Screen("downloads")
    object History : Screen("history")
    object Settings : Screen("settings")
}
