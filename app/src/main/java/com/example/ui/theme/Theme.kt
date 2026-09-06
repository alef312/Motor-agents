package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

fun parseHexColor(hex: String, fallback: Color = TurboAmber): Color {
    return try {
        val clean = hex.removePrefix("#").trim()
        if (clean.length == 6) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else if (clean.length == 8) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else {
            fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    customPrimaryHex: String? = null,
    cornerRadiusDp: Int = 16,
    content: @Composable () -> Unit
) {
    val basePrimary = if (!customPrimaryHex.isNullOrBlank()) {
        parseHexColor(customPrimaryHex, TurboAmber)
    } else {
        TurboAmber
    }

    val darkScheme = darkColorScheme(
        primary = basePrimary,
        onPrimary = Color(0xFF000000),
        primaryContainer = basePrimary.copy(alpha = 0.25f),
        onPrimaryContainer = Color.White,
        secondary = TurboCyan,
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF083344),
        onSecondaryContainer = Color(0xFFE0F2FE),
        tertiary = TurboOrange,
        onTertiary = Color.White,
        background = DarkBackground,
        onBackground = DarkOnSurface,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        error = StatusOverdue,
        onError = Color.White
    )

    val lightScheme = lightColorScheme(
        primary = basePrimary,
        onPrimary = Color.White,
        primaryContainer = basePrimary.copy(alpha = 0.15f),
        onPrimaryContainer = Color(0xFF1E293B),
        secondary = Color(0xFF0284C7),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0F2FE),
        onSecondaryContainer = Color(0xFF0369A1),
        tertiary = TurboOrange,
        onTertiary = Color.White,
        background = LightBackground,
        onBackground = LightOnSurface,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        error = StatusOverdue,
        onError = Color.White
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && customPrimaryHex.isNullOrBlank() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val customShapes = Shapes(
        small = RoundedCornerShape((cornerRadiusDp * 0.5f).dp),
        medium = RoundedCornerShape(cornerRadiusDp.dp),
        large = RoundedCornerShape((cornerRadiusDp * 1.5f).dp),
        extraLarge = RoundedCornerShape((cornerRadiusDp * 2.0f).dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = customShapes,
        content = content
    )
}
