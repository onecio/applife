package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    onPrimary = OnPrimaryWhite,
    secondary = SecondaryIndigo,
    onSecondary = OnPrimaryWhite,
    tertiary = TertiaryRose,
    background = BackgroundDark,
    onBackground = OnBackgroundText,
    surface = SurfaceDark,
    onSurface = OnSurfaceText,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = SubtextGrey,
    outline = DividerSlate
)

// Auxiliary light colors for fallback
private val Color80Dark = androidx.compose.ui.graphics.Color(0xFF0F172A)
private val ColorLightSlate = androidx.compose.ui.graphics.Color(0xFFF1F5F9)
private val SubtextGreyLight = androidx.compose.ui.graphics.Color(0xFF64748B)
private val DividerLight = androidx.compose.ui.graphics.Color(0xFFE2E8F0)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTealLight,
    onPrimary = OnPrimaryWhite,
    secondary = SecondaryIndigoLight,
    onSecondary = OnPrimaryWhite,
    tertiary = TertiaryRose,
    background = BackgroundLight,
    onBackground = Color80Dark,
    surface = SurfaceLight,
    onSurface = Color80Dark,
    surfaceVariant = ColorLightSlate,
    onSurfaceVariant = SubtextGreyLight,
    outline = DividerLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We strictly enforce our custom theme to ensure approved Stitch design fidelity
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Enforce dark theme as prime aesthetic for stress reduction and premium feeling

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
