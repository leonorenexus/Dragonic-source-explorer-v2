package com.leonoretech.dragonicexplorer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leonoretech.dragonicexplorer.ui.theme.CardBorder
import com.leonoretech.dragonicexplorer.ui.theme.FailedRed
import com.leonoretech.dragonicexplorer.ui.theme.NeonCyan
import com.leonoretech.dragonicexplorer.ui.theme.RunningBlue
import com.leonoretech.dragonicexplorer.ui.theme.SuccessGreen
import com.leonoretech.dragonicexplorer.ui.theme.SurfaceElevated
import com.leonoretech.dragonicexplorer.ui.theme.TextPrimary
import com.leonoretech.dragonicexplorer.ui.theme.TextSecondary

@Composable
fun DragonicTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
            }
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(start = if (onBack == null) 12.dp else 0.dp)
        )
        if (onRefresh != null) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = NeonCyan)
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, valueColor: Color = NeonCyan, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
                color = valueColor
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = NeonCyan)
            Text(
                label,
                color = TextPrimary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

fun statusDisplay(status: String, conclusion: String?): Pair<String, Color> {
    return when {
        status != "completed" -> "RUNNING" to RunningBlue
        conclusion == "success" -> "SUCCESS" to SuccessGreen
        conclusion == "failure" -> "FAILED" to FailedRed
        conclusion == "cancelled" -> "CANCELLED" to TextSecondary
        else -> (conclusion ?: "UNKNOWN").uppercase() to TextSecondary
    }
}

@Composable
fun StatusBadge(status: String, conclusion: String?) {
    val (text, color) = statusDisplay(status, conclusion)
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextSecondary, modifier = modifier)
}
