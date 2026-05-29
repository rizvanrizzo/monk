package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Strictly Monk aesthetic dark color scheme
private val MonkColorScheme = darkColorScheme(
    primary = Color.White,
    secondary = TextGray8E,
    tertiary = AccentZenGreen,
    background = Black0A,
    surface = SurfaceDark16,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = TextWhiteFA,
    onSurface = TextWhiteFA,
    outline = BorderDark26
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Always force the aesthetic Dark Minimal Monk Theme
    content: @Composable () -> Unit,
) {
    // For Monk we force our bespoke dark mode palette to retain the Linear/Nothing/GenZ dark theme aesthetic
    MaterialTheme(
        colorScheme = MonkColorScheme,
        typography = Typography,
        content = content
    )
}
