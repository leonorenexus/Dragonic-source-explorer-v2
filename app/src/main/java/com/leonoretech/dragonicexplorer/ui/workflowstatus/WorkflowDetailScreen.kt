package com.leonoretech.dragonicexplorer.ui.workflowstatus

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.data.model.Job
import com.leonoretech.dragonicexplorer.ui.components.DragonicTopBar
import com.leonoretech.dragonicexplorer.ui.components.StatusBadge
import com.leonoretech.dragonicexplorer.util.DateUtils
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceDark
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceElevated
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun WorkflowDetailScreen(
    runId: Long,
    onBack: () -> Unit,
    viewModel: WorkflowDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(runId) { viewModel.load(runId) }

    Column(modifier = Modifier.fillMaxSize().background(DeepSpaceBlack)) {
        DragonicTopBar(title = "Run #${state.run?.runNumber ?: runId}", onBack = onBack, onRefresh = { viewModel.load(runId) })

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.run?.let { run ->
                item {
                    Card(
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
                                Text("Branch ${run.headBranch ?: "main"}", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                                StatusBadge(run.status, run.conclusion)
                            }
                            Text(
                                "${DateUtils.relativeTime(run.createdAt)} · durasi ${DateUtils.durationLabel(run.runStartedAt, run.updatedAt)}",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            if (state.jobs.isNotEmpty()) {
                item { Text("JOBS", color = TextSecondary, style = MaterialTheme.typography.labelSmall) }
                items(state.jobs) { job -> JobRow(job, onViewLogs = { viewModel.loadLogs(job.id) }) }
            }

            if (state.artifacts.isNotEmpty()) {
                item { Text("ARTIFACTS", color = TextSecondary, style = MaterialTheme.typography.labelSmall) }
                items(state.artifacts) { artifact ->
                    ArtifactRow(artifact, onDownload = { viewModel.downloadArtifact(artifact) })
                }
            }

            state.errorMessage?.let { message ->
                item {
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (state.isLoadingLogs || state.logsText != null) {
        LogsDialog(
            isLoading = state.isLoadingLogs,
            logsText = state.logsText,
            onDismiss = viewModel::dismissLogs
        )
    }
}

@Composable
private fun JobRow(job: Job, onViewLogs: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewLogs() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(job.name, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${job.steps?.size ?: 0} steps · tap untuk lihat logs",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusBadge(job.status, job.conclusion)
        }
    }
}

@Composable
private fun ArtifactRow(artifact: Artifact, onDownload: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.FolderZip,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier
                        .height(28.dp)
                        .padding(end = 12.dp)
                )
                Column {
                    Text(artifact.name, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                    Text(DateUtils.formatBytes(artifact.sizeInBytes), color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDownload) {
                Icon(Icons.Filled.Download, contentDescription = "Download", tint = NeonCyan)
            }
        }
    }
}

@Composable
private fun LogsDialog(isLoading: Boolean, logsText: String?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("CLOSE", color = NeonCyan) } },
        containerColor = SurfaceElevated,
        title = { Text("Job Logs", color = TextPrimary) },
        text = {
            Box(modifier = Modifier.height(360.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(color = NeonCyan)
                } else {
                    LazyColumn {
                        items(logsText.orEmpty().lines()) { line ->
                            Text(
                                line,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        }
    )
}
