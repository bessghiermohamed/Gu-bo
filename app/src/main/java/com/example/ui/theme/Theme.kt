package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = TalibCrimson,
  onPrimary = Color.White,
  primaryContainer = TalibCrimsonDark,
  onPrimaryContainer = Color.White,
  secondary = TalibCrimsonLight,
  onSecondary = Color.White,
  background = TalibDarkBg,
  onBackground = TalibDarkText,
  surface = TalibDarkSurface,
  onSurface = TalibDarkText,
  surfaceVariant = TalibDarkContainer,
  onSurfaceVariant = TalibDarkMuted,
  outline = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
  primary = TalibPurple,
  onPrimary = Color.White,
  primaryContainer = TalibPurpleContainer,
  onPrimaryContainer = TalibPurpleDark,
  secondary = TalibPurpleDark,
  onSecondary = Color.White,
  background = TalibPurpleBg,
  onBackground = TextPrimaryLight,
  surface = TalibPurpleSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondaryLight,
  outline = Color(0xFFE2E8F0)
)

@Composable
fun TalibTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
