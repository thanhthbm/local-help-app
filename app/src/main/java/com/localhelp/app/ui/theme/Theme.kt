package com.localhelp.app.ui.theme

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
import androidx.compose.ui.platform.LocalContext
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
    onPrimary        = BackgroundDark,
    primaryContainer = OrangeDark,
    onPrimaryContainer = OrangeLight,

    secondary        = BlueSecondaryDark,
    onSecondary      = BackgroundDark,

    background       = BackgroundDark,
    onBackground     = TextPrimaryDark,

    surface          = SurfaceDark,
    onSurface        = TextPrimaryDark,

    surfaceVariant   = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline          = DividerDark,
    outlineVariant   = DividerDark,

    error            = Error,
    onError          = TextPrimaryDark
)

@Composable
fun LocalHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // tắt dynamic color để giữ màu cam của app
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // Đổi màu status bar theo theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}