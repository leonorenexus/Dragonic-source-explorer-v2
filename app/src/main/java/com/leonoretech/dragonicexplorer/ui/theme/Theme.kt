package com.leonoretech.dragonicexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dragonic Source Explorer ships dark-mode only, matching the Leonore Tech Team
// cyberpunk/terminal aesthetic used across all Pai Leonore projects.
private val DragonicDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    secondary = NeonMagenta,
    onSecondary = Color.Black,
    tertiary = NeonBlue,
    background = DeepSpaceBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = FailedRed,
    outline = CardBorder
)

@Composable
fun DragonicSourceExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DragonicDarkColorScheme,
        typography = DragonicTypography,
        shapes = DragonicShapes,
        content = content
    )
}
