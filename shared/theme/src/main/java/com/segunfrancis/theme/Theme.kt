package com.segunfrancis.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    outline = DarkOutline,
    surfaceVariant = DarkCard,
    secondary = DarkSearchBox,
    onSecondary = DarkOnSurface,
    onPrimaryContainer = LightChipText
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    outline = LightOutline,
    surfaceVariant = LightCard,
    secondary = LightSearchBox,
    onSecondary = LightOnSurface,
    onPrimaryContainer = LightPrimary
)

data class CustomButtonColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
)

val LocalButtonColors = staticCompositionLocalOf {
    CustomButtonColors(
        primary = LightPrimaryButtonColor,
        onPrimary = LightOnPrimaryButtonColor,
        secondary = LightSecondaryButtonColor,
        onSecondary = LightOnSecondaryButtonColor
    )
}

@Composable
fun WallpaperDownloaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val buttonColors = if (darkTheme) {
        CustomButtonColors(
            primary = DarkPrimaryButtonColor,
            onPrimary = DarkOnPrimaryButtonColor,
            secondary = DarkSecondaryButtonColor,
            onSecondary = DarkOnSecondaryButtonColor
        )
    } else {
        CustomButtonColors(
            primary = LightPrimaryButtonColor,
            onPrimary = LightOnPrimaryButtonColor,
            secondary = LightSecondaryButtonColor,
            onSecondary = LightOnSecondaryButtonColor
        )
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
                window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                    view.setBackgroundColor(colorScheme.onPrimaryContainer.toArgb())
                    // Adjust padding to avoid overlap
                    view.setPadding(0, 0, 0, 0)
                    insets
                }
            } else {
                // For Android 14 and below
                window.statusBarColor = colorScheme.onPrimaryContainer.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(LocalButtonColors provides buttonColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WallpaperTypography,
            shapes = WallpaperDownloadedShapes,
            content = content
        )
    }
}
