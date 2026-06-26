package com.leonoretech.dragonicexplorer.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.ui.components.DragonicTopBar
import com.leonoretech.dragonicexplorer.ui.components.SectionLabel
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.SuccessGreen
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceDark
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceElevated
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var tokenInput by remember { mutableStateOf("") }
    var tokenVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .verticalScroll(rememberScrollState())
    ) {
        DragonicTopBar(title = "Settings")

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            SectionLabel("GitHub Authentication")
            SettingsCard {
                Text("Personal Access Token", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                Text(
                    if (state.hasToken) "Token tersimpan (terenkripsi)" else "Belum ada token tersimpan",
                    color = if (state.hasToken) SuccessGreen else TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    placeholder = { Text("ghp_xxxxxxxxxxxxxxxxxxxx") },
                    singleLine = true,
                    visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { tokenVisible = !tokenVisible }) {
                            Icon(
                                if (tokenVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = CardBorder)
                )
                TextButton(
                    onClick = {
                        if (tokenInput.isNotBlank()) {
                            viewModel.saveToken(tokenInput)
                            tokenInput = ""
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, tint = NeonCyan)
                    Text("SAVE TOKEN", color = NeonCyan, modifier = Modifier.padding(start = 6.dp))
                }
                Text(
                    "Token butuh scope 'repo' dan 'workflow' agar bisa trigger Actions di repo privat/publik milikmu.",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionLabel("Repository", Modifier.padding(top = 18.dp))
            SettingsCard {
                LabeledField("Repo Owner", state.repoOwner, viewModel::updateRepoOwner, "e.g. leonore-tech")
                LabeledField("Repo Name", state.repoName, viewModel::updateRepoName, "e.g. dragonic-source-explorer", topPadding = 14.dp)
                LabeledField("Workflow File", state.workflowFile, viewModel::updateWorkflowFile, "website-analysis.yml", topPadding = 14.dp)
                LabeledField("Branch", state.defaultBranch, viewModel::updateBranch, "main", topPadding = 14.dp)
            }

            SectionLabel("Monitoring", Modifier.padding(top = 18.dp))
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Notifikasi", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                        Text("Beri tahu saat workflow selesai", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = viewModel::updateNotificationsEnabled,
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }
                Text(
                    "Auto Monitor: setiap ${state.autoMonitorMinutes} menit",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    listOf(15, 30, 60).forEach { minutes ->
                        TextButton(onClick = { viewModel.updateAutoMonitorMinutes(minutes) }) {
                            Text(
                                "${minutes}m",
                                color = if (state.autoMonitorMinutes == minutes) NeonCyan else TextSecondary
                            )
                        }
                    }
                }
            }

            SectionLabel("System Info", Modifier.padding(top = 18.dp))
            SettingsCard {
                InfoRow("Repository", "${state.repoOwner.ifBlank { "-" }}/${state.repoName.ifBlank { "-" }}")
                InfoRow("Branch", state.defaultBranch, topPadding = 8.dp)
                InfoRow("Workflow", state.workflowFile, topPadding = 8.dp)
                InfoRow("Architecture", "MVVM · Repository Pattern · Hilt DI", topPadding = 8.dp)
            }

            Text(
                "LEONORE TECH TEAM",
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    topPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    Column(modifier = Modifier.padding(top = topPadding)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = CardBorder)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
    }
}
