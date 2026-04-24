package com.financeia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = Green400,
    onPrimary        = Navy900,
    primaryContainer = Green600,
    onPrimaryContainer = Color(0xFFDCFCE7.toInt()),
    secondary        = Blue400,
    onSecondary      = Navy900,
    background       = Navy900,
    onBackground     = Navy100,
    surface          = Navy800,
    onSurface        = Navy100,
    surfaceVariant   = Navy700,
    onSurfaceVariant = Navy400,
    outline          = Navy600,
    error            = Red400,
)

private val LightColorScheme = lightColorScheme(
    primary          = Green600,
    onPrimary        = Color(0xFFFFFFFF.toInt()),
    primaryContainer = Color(0xFFDCFCE7.toInt()),
    onPrimaryContainer = Green900,
    secondary        = Blue500,
    onSecondary      = Color(0xFFFFFFFF.toInt()),
    background       = Navy100,
    onBackground     = Navy900,
    surface          = Color(0xFFFFFFFF.toInt()),
    onSurface        = Navy900,
    surfaceVariant   = Navy200,
    onSurfaceVariant = Navy700,
    outline          = Navy400,
    error            = Red500,
)

@Composable
fun FinanceIATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FinanceTypography,
        content     = content
    )
}
