package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChatGptEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0D7A5F),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF68D391),
    onSecondary = Color(0xFF003822),
    background = ChatGptDarkBg,
    onBackground = ChatGptText,
    surface = ChatGptSurface,
    onSurface = ChatGptText,
    surfaceVariant = ChatGptCard,
    onSurfaceVariant = ChatGptTextSecondary,
    outline = ChatGptBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ChatGptEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1E1E1E),
    surface = Color(0xFFF7F7F8),
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFECECF1),
    onSurfaceVariant = Color(0xFF565869),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use ChatGPT curated emerald theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
