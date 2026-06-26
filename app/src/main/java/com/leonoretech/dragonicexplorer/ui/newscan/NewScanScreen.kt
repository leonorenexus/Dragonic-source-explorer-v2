package com.leonoretech.dragonicexplorer.ui.newscan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leonoretech.dragonicexplorer.ui.components.DragonicTopBar
import com.leonoretech.dragonicexplorer.ui.theme.DeepSpaceBlack
import com.leonoretech.dragonicexplorer.ui.theme.FailedRed
import com.leonoretech.dragonicexplorer.ui.theme.NeonBlue
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.TextMuted
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun NewScanScreen(
    onBack: () -> Unit,
    onScanQueued: () -> Unit,
    viewModel: NewScanViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.dispatchState) {
        if (state.dispatchState is ScanDispatchState.Success) {
            onScanQueued()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
    ) {
        DragonicTopBar(title = "New Scan", onBack = onBack)
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Masukkan URL website publik untuk dianalisis",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = state.url,
                onValueChange = viewModel::onUrlChanged,
                label = { Text("Website URL") },
                placeholder = { Text("https://example.com") },
                isError = state.urlError != null,
                supportingText = { state.urlError?.let { Text(it, color = FailedRed) } },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CardBorder
                )
            )
            Button(
                onClick = viewModel::submitScan,
                enabled = state.dispatchState !is ScanDispatchState.Dispatching,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
            ) {
                if (state.dispatchState is ScanDispatchState.Dispatching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                    Text("TRIGGER WORKFLOW", modifier = Modifier.padding(start = 8.dp))
                }
            }
            val dispatchError = state.dispatchState
            if (dispatchError is ScanDispatchState.Error) {
                Text(
                    dispatchError.message,
                    color = FailedRed,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Text(
                "Catatan keamanan: aplikasi ini hanya memproses resource publik. " +
                    "Tidak ada autentikasi atau kontrol akses yang dilewati.",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}
