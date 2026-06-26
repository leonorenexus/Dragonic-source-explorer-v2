package com.leonoretech.dragonicexplorer.ui.workflowstatus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.ui.components.DragonicTopBar
import com.leonoretech.dragonicexplorer.ui.components.StatCard
import com.leonoretech.dragonicexplorer.ui.components.StatusBadge
import com.leonoretech.dragonicexplorer.util.DateUtils
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.FailedRed
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.RunningBlue
import com.leonoretech.dragonicexplorer.ui.theme.SuccessGreen
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceDark
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun WorkflowStatusScreen(
    onBack: () -> Unit,
    onRunSelected: (Long) -> Unit,
    viewModel: WorkflowStatusViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(DeepSpaceBlack)) {
        DragonicTopBar(title = "Workflow Status", onBack = onBack, onRefresh = viewModel::refresh)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("Total", state.total.toString(), NeonCyan, Modifier.weight(1f))
            StatCard("Success", state.success.toString(), SuccessGreen, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("Failed", state.failed.toString(), FailedRed, Modifier.weight(1f))
            StatCard("Running", state.running.toString(), RunningBlue, Modifier.weight(1f))
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.runs) { run ->
                WorkflowRunCard(run = run, onClick = { onRunSelected(run.id) })
            }
            if (state.runs.isEmpty() && !state.isLoading) {
                item {
                    Text(
                        "Belum ada workflow run. Trigger scan baru dari menu New Scan.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkflowRunCard(run: WorkflowRun, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Run #${run.runNumber}", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                StatusBadge(run.status, run.conclusion)
            }
            Text(
                "Branch: ${run.headBranch ?: "main"}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                "${DateUtils.relativeTime(run.createdAt)} · durasi ${DateUtils.durationLabel(run.runStartedAt, run.updatedAt)}",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
