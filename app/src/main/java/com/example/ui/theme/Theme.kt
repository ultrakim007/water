package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SeniorCareColors(
    val backgroundGradientTop: Color = IvoryBackgroundTop,
    val backgroundGradientBottom: Color = IvoryBackgroundBottom,
    val cardSurface: Color = IvoryCardSurface,
    val subtleSurface: Color = IvorySubtleSurface,
    val cardBorder: Color = IvoryBorder,
    val inkPrimary: Color = WarmInkPrimary,
    val inkSecondary: Color = WarmInkSecondary,
    val inkMuted: Color = WarmInkMuted,
    val takenGreen: Color = HerbalGreen,
    val takenGreenBorder: Color = HerbalGreenBorder,
    val takenGreenSoft: Color = HerbalGreenSoft,
    val takenGreenOnSoft: Color = HerbalGreenOnSoft,
    val morningAccent: Color = MorningAmber,
    val morningSoft: Color = MorningAmberSoft,
    val lunchAccent: Color = LunchSun,
    val lunchSoft: Color = LunchSunSoft,
    val eveningAccent: Color = EveningIndigo,
    val eveningSoft: Color = EveningIndigoSoft,
    val waterPrimary: Color = WaterBlue,
    val waterFill: Color = WaterBlueBright,
    val waterHighlight: Color = WaterBlueHighlight,
    val waterSoft: Color = WaterBlueSoft,
    val waterOnSoft: Color = WaterBlueOnSoft,
    val waterCupEmpty: Color = WaterCupEmptyFill,
    val waterCupOutline: Color = WaterCupOutline,
    val onColoredSurface: Color = PureWhite,
)

val LocalSeniorCareColors = staticCompositionLocalOf { SeniorCareColors() }

private val IvoryLightColorScheme = lightColorScheme(
    primary = HerbalGreen,
    onPrimary = PureWhite,
    primaryContainer = HerbalGreenSoft,
    onPrimaryContainer = HerbalGreenOnSoft,
    secondary = WaterBlue,
    onSecondary = PureWhite,
    secondaryContainer = WaterBlueSoft,
    onSecondaryContainer = WaterBlueOnSoft,
    tertiary = MorningAmber,
    onTertiary = PureWhite,
    tertiaryContainer = MorningAmberSoft,
    onTertiaryContainer = WarmInkPrimary,
    background = IvoryBackgroundTop,
    onBackground = WarmInkPrimary,
    surface = IvoryCardSurface,
    onSurface = WarmInkPrimary,
    surfaceVariant = IvorySubtleSurface,
    onSurfaceVariant = WarmInkSecondary,
    outline = IvoryBorder,
    error = SoftErrorRed,
    onError = PureWhite,
    errorContainer = SoftErrorContainer,
    onErrorContainer = SoftErrorRed
)

@Composable
fun SeniorCareTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSeniorCareColors provides SeniorCareColors()) {
        MaterialTheme(
            colorScheme = IvoryLightColorScheme,
            typography = SeniorCareTypography,
            content = content
        )
    }
}

object SeniorCareThemeDefaults {
    val colors: SeniorCareColors
        @Composable get() = LocalSeniorCareColors.current
}
