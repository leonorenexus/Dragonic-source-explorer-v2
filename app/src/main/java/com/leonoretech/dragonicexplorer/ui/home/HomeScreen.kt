package com.leonoretech.dragonicexplorer.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.ui.components.QuickActionButton
import com.leonoretech.dragonicexplorer.ui.components.SectionLabel
import com.leonoretech.dragonicexplorer.ui.components.StatusBadge
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.NeonBlue
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.SuccessGreen
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceDark
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceElevated
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onNavigateToNewScan: () -> Unit,
    onNavigateToWorkflowStatus: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { BrandHeader() }
        item { QuickScanCard(onScan = onNavigateToNewScan) }
        item { WorkflowSummaryCard(runningCount = state.runningCount, onViewAll = onNavigateToWorkflowStatus) }
        item {
            QuickActionsGrid(
                onNewScan = onNavigateToNewScan,
                onWorkflowStatus = onNavigateToWorkflowStatus,
                onReports = onNavigateToReports,
                onDownloads = onNavigateToDownloads
            )
        }
        item { SectionLabel("Recent Activity") }
        items(state.recentRuns) { run -> RecentRunRow(run) }
        if (state.recentRuns.isEmpty() && !state.isLoading) {
            item {
                Text(
                    "Belum ada scan. Mulai scan pertamamu di atas.",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Hub, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(40.dp))
        }
        Text(
            "DRAGONIC",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            "SOURCE EXPLORER",
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
            color = NeonCyan
        )
    }
}

@Composable
private fun QuickScanCard(onScan: () -> Unit) {
    var url by remember { mutableStateOf("") }
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Public, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                Text(
                    "Website URL",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                placeholder = { Text("https://example.com") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            Button(
                onClick = onScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
            ) {
                Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                Text("SCAN DATABASE", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun WorkflowSummaryCard(runningCount: Int, onViewAll: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("WORKFLOW STATUS", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(
                    "View All",
                    color = NeonCyan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 14.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (runningCount > 0) "BUSY" else "IDLE",
                        color = if (runningCount > 0) NeonCyan else SuccessGreen,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        if (runningCount > 0) "$runningCount workflow running" else "No workflow running",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Ready to start a new scan", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onNewScan: () -> Unit,
    onWorkflowStatus: () -> Unit,
    onReports: () -> Unit,
    onDownloads: () -> Unit
) {
    Column {
        SectionLabel("Quick Actions")
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            QuickActionButton(Icons.Filled.RocketLaunch, "START\nSCAN", Modifier.weight(1f), onClick = onNewScan)
            QuickActionButton(Icons.Filled.ListAlt, "WORKFLOW\nSTATUS", Modifier.weight(1f), onClick = onWorkflowStatus)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            QuickActionButton(Icons.Filled.Description, "REPORTS", Modifier.weight(1f), onClick = onReports)
            QuickActionButton(Icons.Filled.Download, "DOWNLOADS", Modifier.weight(1f), onClick = onDownloads)
        }
    }
}

@Composable
private fun RecentRunRow(run: WorkflowRun) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Website Analysis #${run.runNumber}", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                Text(run.headBranch ?: "main", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            StatusBadge(run.status, run.conclusion)
        }
    }
}
