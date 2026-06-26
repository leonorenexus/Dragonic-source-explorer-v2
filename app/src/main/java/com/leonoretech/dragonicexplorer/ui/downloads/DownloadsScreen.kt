package com.leonoretech.dragonicexplorer.ui.downloads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.ui.components.DragonicTopBar
import com.leonoretech.dragonicexplorer.util.DateUtils
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceDark
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceElevated
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary

@Composable
fun DownloadsScreen(viewModel: DownloadsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(DeepSpaceBlack)) {
        DragonicTopBar(title = "Downloads", onRefresh = viewModel::refresh)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(120.dp),
                        color = NeonCyan,
                        strokeWidth = 6.dp,
                        trackColor = SurfaceElevated
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(DateUtils.formatBytes(state.totalSizeBytes), color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text("${state.artifacts.size} artifacts", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.artifacts) { artifact ->
                DownloadRow(
                    artifact = artifact,
                    isResolving = state.resolvingArtifactId == artifact.id,
                    onDownload = { viewModel.downloadArtifact(artifact) }
                )
            }
            if (state.artifacts.isEmpty() && !state.isLoading) {
                item {
                    Text(
                        "Belum ada artifact yang tersedia untuk diunduh.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadRow(artifact: Artifact, isResolving: Boolean, onDownload: () -> Unit) {
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
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(artifact.name, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "${DateUtils.formatBytes(artifact.sizeInBytes)} · ${DateUtils.relativeTime(artifact.createdAt)}",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (isResolving) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = NeonCyan, strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Filled.Download, contentDescription = "Download", tint = NeonCyan)
                }
            }
        }
    }
}
