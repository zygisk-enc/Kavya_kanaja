package com.zygisk.kavya_kanaja.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    secondary = YellowSecondary,
    tertiary = YellowTertiary,
    primaryContainer = YellowPrimaryContainer,
    secondaryContainer = YellowSecondaryContainer,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = YellowPrimaryDark,
    secondary = YellowSecondary,
    tertiary = YellowTertiary,
    primaryContainer = YellowPrimaryContainer,
    secondaryContainer = YellowSecondaryContainer,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black
)

@Composable
fun KavyaKanajaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enable dynamic color as requested
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val scheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) {
                scheme.copy(
                    primary = YellowPrimary,
                    primaryContainer = YellowPrimaryContainer,
                    secondaryContainer = YellowSecondaryContainer,
                    background = Color.Black,
                    surface = Color.Black,
                    surfaceVariant = Color.Black
                )
            } else {
                scheme.copy(
                    primary = YellowPrimaryDark,
                    primaryContainer = YellowPrimaryContainer,
                    secondaryContainer = YellowSecondaryContainer
                )
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            val window = activity?.window
            if (window != null) {
                // For AMOLED theme, we want the status bar to be black too
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
