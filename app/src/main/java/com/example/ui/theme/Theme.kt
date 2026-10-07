package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.ThemeMode

private val DarkColorScheme = darkColorScheme(
  primary = O2BlueDark,
  onPrimary = O2OnBlueDark,
  primaryContainer = O2OmniboxDark,
  onPrimaryContainer = O2BlueDark,
  surface = O2SurfaceDark,
  surfaceVariant = O2OmniboxDark,
  background = O2SurfaceDark,
  onSurface = O2TextPrimaryDark,
  onSurfaceVariant = O2TextSecondaryDark,
  outline = O2BorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = O2BlueLight,
  onPrimary = O2OnBlueLight,
  primaryContainer = O2OmniboxLight,
  onPrimaryContainer = O2BlueLight,
  surface = O2SurfaceLight,
  surfaceVariant = O2OmniboxLight,
  background = O2SurfaceLight,
  onSurface = O2TextPrimaryLight,
  onSurfaceVariant = O2TextSecondaryLight,
  outline = O2BorderLight
)

@Composable
fun MyApplicationTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  content: @Composable () -> Unit,
) {
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
  }

  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
