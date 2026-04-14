package com.localhelp.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Color Schemes ────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary          = OrangePrimary,
    onPrimary        = TextOnPrimary,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,

    secondary        = BlueSecondary,
    onSecondary      = TextOnPrimary,

    background       = Background,
    onBackground     = TextPrimary,

    surface          = Surface,
    onSurface        = TextPrimary,

    surfaceVariant   = SurfaceVariant,     // Bổ sung nền phụ
    onSurfaceVariant = TextSecondary,      // Slot chuẩn cho chữ phụ/icon phụ

    outline          = Divider,            // Dùng cho viền của Input, Card
    outlineVariant   = Divider,

    error            = Error,
    onError          = TextOnPrimary       // Chữ trắng trên nền đỏ
)

private val DarkColorScheme = darkColorScheme(
    primary          = OrangePrimaryDark,
    onPrimary        = OnOrangePrimaryDark,
    primaryContainer = OrangeContainerDark,
    onPrimaryContainer = OnOrangeContainerDark,

    secondary        = BlueSecondaryDark,
    onSecondary      = BackgroundDark,

    background       = BackgroundDark,
    onBackground     = TextPrimaryDark,

    surface          = SurfaceDark,
    onSurface        = TextPrimaryDark,

    surfaceVariant   = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    surfaceContainer = SurfaceContainerDark, // Added for cards and containers

    outline          = DividerDark,
    outlineVariant   = DividerDark,

    error            = Error,
    onError          = TextPrimaryDark
)

@Composable
fun LocalHelpTheme(
    darkTheme: Boolean = false, // Force light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    
    // Đổi màu status bar theo theme light
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}