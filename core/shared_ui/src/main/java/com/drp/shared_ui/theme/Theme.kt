package com.drp.shared_ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.drp.refah.ui.theme.Blue10
import com.drp.refah.ui.theme.Blue40
import com.drp.refah.ui.theme.Blue80
import com.drp.refah.ui.theme.Green
import com.drp.refah.ui.theme.Grey20
import com.drp.refah.ui.theme.Grey40
import com.drp.refah.ui.theme.Grey60
import com.drp.refah.ui.theme.Grey80
import com.drp.refah.ui.theme.Red
import com.drp.refah.ui.theme.White
import com.google.android.material.internal.EdgeToEdgeUtils

/**
 * tertiary is used for hint colors through out whole app
 **/
private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = Blue80,
    outline = Grey20,
    onPrimary = White,
    onSurface = Grey80,
    primaryContainer = White,
    tertiary = Grey40,
    onSecondaryContainer = Grey60,
    surfaceVariant = Blue10
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = Blue80,
    onPrimary = White,
    outline = Grey20,
    onSurface = Grey80,
    primaryContainer = White,
    tertiary = Grey40,
    onSecondaryContainer = Grey60,
    surfaceVariant = Blue10,
    error = Red,
    onTertiary = Green
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@SuppressLint("RestrictedApi")
@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    val colorScheme= LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            if (view.context is Activity) {
                val window = (view.context as Activity).window
                window.statusBarColor = colorScheme.primary.toArgb()
                /** system navigation bar color :just for fixing navigation bar icons color in
                 * compose bottom sheets */
                EdgeToEdgeUtils.setLightNavigationBar(window, !darkTheme)

                WindowCompat.setDecorFitsSystemWindows(window, false)
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                windowInsetsController.isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    Box(modifier = Modifier.safeDrawingPadding()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }

}