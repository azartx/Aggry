package com.solo4.aggry.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple80 = Color(0xFFD0BCFF)
private val PurpleGrey80 = Color(0xFFCCC2DC)
private val Pink80 = Color(0xFFEFB8C8)

private val Purple40 = Color(0xFF6650a4)
private val PurpleGrey40 = Color(0xFF625b71)
private val Pink40 = Color(0xFF7D5260)

// Новая цветовая палитра - Purple-Teal
private val DeepPurple = Color(0xFF5D3FD3)
private val LightPurple = Color(0xFF7C5CFF)
private val Teal = Color(0xFF00BFA6)
private val LightTeal = Color(0xFF5DE2D0)
private val CoralRed = Color(0xFFFF6B6B)
private val EmeraldGreen = Color(0xFF10B981)
private val LightSurface = Color(0xFFF5F5F7)
private val DarkSurface = Color(0xFF1C1C1E)

private val AggryLightColorScheme = lightColorScheme(
    primary = DeepPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6DEFF),
    onPrimaryContainer = DeepPurple,
    
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2F5EA),
    onSecondaryContainer = Color(0xFF005048),
    
    tertiary = LightPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6DEFF),
    onTertiaryContainer = LightPurple,
    
    background = LightSurface,
    onBackground = Color(0xFF1C1C1E),
    
    surface = Color.White,
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF666666),
    
    outline = Color(0xFFD0D0D0),
    outlineVariant = Color(0xFFE0E0E0),
    
    error = CoralRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFE5E5),
    onErrorContainer = CoralRed,
)

private val AggryDarkColorScheme = darkColorScheme(
    primary = LightPurple,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2A1E5C),
    onPrimaryContainer = LightPurple,
    
    secondary = LightTeal,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = LightTeal,
    
    tertiary = Purple80,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2A1E5C),
    onTertiaryContainer = Purple80,
    
    background = DarkSurface,
    onBackground = Color(0xFFF2F2F7),
    
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF2F2F7),
    surfaceVariant = Color(0xFF3A3A3C),
    onSurfaceVariant = Color(0xFFC7C7C8),
    
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF3A3A3C),
    
    error = Color(0xFFFF8A80),
    onError = Color.Black,
    errorContainer = Color(0xFF5C0000),
    onErrorContainer = Color(0xFFFF8A80),

//    success = Color(0xFF81C995),
//    onSuccess = Color.Black,
//    successContainer = Color(0xFF004D27),
//    onSuccessContainer = Color(0xFF81C995),
)

@Composable
fun AggryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        AggryDarkColorScheme
    } else {
        AggryLightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

object AggryTheme {
    val colors: ColorScheme
        @Composable get() = MaterialTheme.colorScheme
}