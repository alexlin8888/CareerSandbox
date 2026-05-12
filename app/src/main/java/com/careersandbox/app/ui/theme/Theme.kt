package com.careersandbox.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightScheme = lightColorScheme(
    primary = BrandOrange,
    onPrimary = PaperWhite,
    primaryContainer = BrandPeach,
    onPrimaryContainer = BrandDeepOrange,

    secondary = InkDeepBlue,
    onSecondary = PaperWhite,
    secondaryContainer = InkGray100,
    onSecondaryContainer = InkDeepBlue,

    tertiary = BrandAmber,
    onTertiary = InkBlack,

    background = PaperOff,
    onBackground = InkDeepBlue,
    surface = PaperWhite,
    onSurface = InkDeepBlue,
    surfaceVariant = InkGray100,
    onSurfaceVariant = InkGray500,

    outline = InkGray200,
    outlineVariant = InkGray300,

    error = AccentRed,
)

@Composable
fun CareerSandboxTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            if (activity != null) {
                val window = activity.window
                try {
                    // 透明狀態列
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    // 淺色狀態列文字(白色 icon),適合深底/橘底 hero 頁
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = false
                } catch (_: Throwable) {}
            }
        }
    }
    MaterialTheme(
        colorScheme = LightScheme,
        typography = AppTypography,
        content = content
    )
}
