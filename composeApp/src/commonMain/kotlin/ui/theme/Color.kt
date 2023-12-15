package ui.theme

import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFFB7086B)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFD9E4)
val md_theme_light_onPrimaryContainer = Color(0xFF3E0020)
val md_theme_light_secondary = Color(0xFF006D31)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFF8DF9A3)
val md_theme_light_onSecondaryContainer = Color(0xFF00210A)
val md_theme_light_tertiary = Color(0xFF7D5636)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDCC4)
val md_theme_light_onTertiaryContainer = Color(0xFF2F1500)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF201A1C)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF201A1C)
val md_theme_light_surfaceVariant = Color(0xFFF2DDE2)
val md_theme_light_onSurfaceVariant = Color(0xFF514347)
val md_theme_light_outline = Color(0xFF837377)
val md_theme_light_inverseOnSurface = Color(0xFFFAEEF0)
val md_theme_light_inverseSurface = Color(0xFF352F30)
val md_theme_light_inversePrimary = Color(0xFFFFB0CC)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFFB7086B)
val md_theme_light_outlineVariant = Color(0xFFD5C2C6)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFFFB0CC)
val md_theme_dark_onPrimary = Color(0xFF640038)
val md_theme_dark_primaryContainer = Color(0xFF8D0051)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFD9E4)
val md_theme_dark_secondary = Color(0xFF70DC89)
val md_theme_dark_onSecondary = Color(0xFF003916)
val md_theme_dark_secondaryContainer = Color(0xFF005323)
val md_theme_dark_onSecondaryContainer = Color(0xFF8DF9A3)
val md_theme_dark_tertiary = Color(0xFFF0BC95)
val md_theme_dark_onTertiary = Color(0xFF48290D)
val md_theme_dark_tertiaryContainer = Color(0xFF623F21)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDCC4)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF201A1C)
val md_theme_dark_onBackground = Color(0xFFEBE0E1)
val md_theme_dark_surface = Color(0xFF201A1C)
val md_theme_dark_onSurface = Color(0xFFEBE0E1)
val md_theme_dark_surfaceVariant = Color(0xFF514347)
val md_theme_dark_onSurfaceVariant = Color(0xFFD5C2C6)
val md_theme_dark_outline = Color(0xFF9D8C91)
val md_theme_dark_inverseOnSurface = Color(0xFF201A1C)
val md_theme_dark_inverseSurface = Color(0xFFEBE0E1)
val md_theme_dark_inversePrimary = Color(0xFFB7086B)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFFFB0CC)
val md_theme_dark_outlineVariant = Color(0xFF514347)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFFCB237A)

val base_Positive = Color(0xFF00AA50)
val base_Warning = Color(0xFFF9B100)
val base_Negative = Color(0xFFD30023)
val base_Neutral = Color(0xFF3776FF)

val light_Positive = Color(0xFF006D31)
val light_onPositive = Color(0xFFFFFFFF)
val light_PositiveContainer = Color(0xFF76FD98)
val light_onPositiveContainer = Color(0xFF00210A)
val dark_Positive = Color(0xFF57E07E)
val dark_onPositive = Color(0xFF003916)
val dark_PositiveContainer = Color(0xFF005323)
val dark_onPositiveContainer = Color(0xFF76FD98)
val light_Warning = Color(0xFF7D5700)
val light_onWarning = Color(0xFFFFFFFF)
val light_WarningContainer = Color(0xFFFFDEAA)
val light_onWarningContainer = Color(0xFF271900)
val dark_Warning = Color(0xFFFFBA2F)
val dark_onWarning = Color(0xFF422C00)
val dark_WarningContainer = Color(0xFF5F4100)
val dark_onWarningContainer = Color(0xFFFFDEAA)
val light_Negative = Color(0xFFC0001F)
val light_onNegative = Color(0xFFFFFFFF)
val light_NegativeContainer = Color(0xFFFFDAD7)
val light_onNegativeContainer = Color(0xFF410004)
val dark_Negative = Color(0xFFFFB3AE)
val dark_onNegative = Color(0xFF68000C)
val dark_NegativeContainer = Color(0xFF930015)
val dark_onNegativeContainer = Color(0xFFFFDAD7)
val light_Neutral = Color(0xFF0055D5)
val light_onNeutral = Color(0xFFFFFFFF)
val light_NeutralContainer = Color(0xFFDAE1FF)
val light_onNeutralContainer = Color(0xFF001849)
val dark_Neutral = Color(0xFFB3C5FF)
val dark_onNeutral = Color(0xFF002B75)
val dark_NeutralContainer = Color(0xFF003FA4)
val dark_onNeutralContainer = Color(0xFFDAE1FF)

object ExtendedColors {
    val Positive = ExtendedColor(
        base_Positive,
        light_Positive, light_onPositive, light_PositiveContainer, light_onPositiveContainer,
        dark_Positive, dark_onPositive, dark_PositiveContainer, dark_onPositiveContainer
    )

    val Warning = ExtendedColor(
        base_Warning,
        light_Warning, light_onWarning, light_WarningContainer, light_onWarningContainer,
        dark_Warning, dark_onWarning, dark_WarningContainer, dark_onWarningContainer
    )

    val Negative = ExtendedColor(
        base_Negative,
        light_Negative, light_onNegative, light_NegativeContainer, light_onNegativeContainer,
        dark_Negative, dark_onNegative, dark_NegativeContainer, dark_onNegativeContainer
    )

    val Neutral = ExtendedColor(
        base_Neutral,
        light_Neutral, light_onNeutral, light_NeutralContainer, light_onNeutralContainer,
        dark_Neutral, dark_onNeutral, dark_NeutralContainer, dark_onNeutralContainer
    )
}
