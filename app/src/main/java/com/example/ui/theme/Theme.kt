package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.domain.GraphicsQuality

@Immutable
data class StudioPalette(
    val isDark: Boolean = true,
    val bgTop: Color = NavyDeep,
    val bgMid: Color = NavyMid,
    val bgBottom: Color = NavyRoom,
    val surface: Color = PanelGlassDark,
    val card: Color = PanelGlassLight,
    val cardBorder: Color = PanelBorderCyan,
    val cardBorderAlt: Color = PanelBorderPurple,
    val cardLip: Color = Color(0xFF071029),
    val ink: Color = TextPrimaryWhite,
    val mutedInk: Color = TextMutedIce,
    val primary: Color = NeonGoldMain,
    val primaryTop: Color = NeonGoldTop,
    val primaryLip: Color = NeonGoldLip,
    val secondary: Color = NeonBlueMain,
    val secondaryTop: Color = NeonBlueTop,
    val secondaryLip: Color = NeonBlueLip,
    val tertiary: Color = NeonPurpleMain,
    val tertiaryLip: Color = NeonPurpleLip,
    val success: Color = NeonGreenMain,
    val successLip: Color = NeonGreenLip,
    val danger: Color = NeonRedMain,
    val dangerLip: Color = NeonRedLip,
    val cyan: Color = ElectricCyan,
    val suddenDeath: Boolean = false
)

@Immutable
data class MotionConfig(
    val quality: GraphicsQuality = GraphicsQuality.HIGH,
    val reduceMotion: Boolean = false
) {
    val particleCount: Int
        get() = when {
            reduceMotion -> 0
            quality == GraphicsQuality.LOW -> 4
            quality == GraphicsQuality.MEDIUM -> 10
            else -> 18
        }

    val enableAmbientGlow: Boolean get() = !reduceMotion && quality != GraphicsQuality.LOW
    val enableIdleLoops: Boolean get() = !reduceMotion
}

val LocalStudioPalette = staticCompositionLocalOf { StudioPalette() }
val LocalMotionConfig = staticCompositionLocalOf { MotionConfig() }

@Composable
fun ImposterGTheme(
    graphicsQuality: GraphicsQuality = GraphicsQuality.HIGH,
    reduceMotion: Boolean = false,
    suddenDeath: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = if (suddenDeath) {
        StudioPalette(
            bgTop = SuddenDeathBgTop,
            bgMid = Color(0xFF24050E),
            bgBottom = SuddenDeathBgBottom,
            surface = Color(0xFF2B0914),
            card = Color(0xFF3E0E1D),
            cardBorder = NeonRedMain,
            cardBorderAlt = NeonGoldMain,
            cardLip = Color(0xFF19030A),
            suddenDeath = true
        )
    } else {
        StudioPalette()
    }

    val motion = MotionConfig(
        quality = graphicsQuality,
        reduceMotion = reduceMotion
    )

    val colorScheme = darkColorScheme(
        primary = palette.primary,
        onPrimary = Color(0xFF1E1300),
        secondary = palette.secondary,
        onSecondary = Color.White,
        tertiary = palette.tertiary,
        onTertiary = Color.White,
        background = palette.bgTop,
        onBackground = palette.ink,
        surface = palette.surface,
        onSurface = palette.ink,
        surfaceVariant = palette.card,
        onSurfaceVariant = palette.mutedInk,
        outline = palette.cardBorder,
        error = palette.danger,
        onError = Color.White
    )

    CompositionLocalProvider(
        LocalStudioPalette provides palette,
        LocalMotionConfig provides motion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ImposterGTypography,
            content = content
        )
    }
}
