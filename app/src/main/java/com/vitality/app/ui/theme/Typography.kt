package com.vitality.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default sans-serif which maps to Roboto on Android
// For production: add Poppins/Montserrat font files to res/font/
val VitalityFontFamily = FontFamily.Default

val VitalityTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 57.sp,
        color      = TextPrimary,
    ),
    headlineLarge = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        color      = TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        color      = TextPrimary,
    ),
    headlineSmall = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        color      = TextPrimary,
    ),
    titleLarge = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        color      = TextPrimary,
    ),
    titleMedium = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        color      = TextPrimary,
    ),
    titleSmall = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        color      = TextSecondary,
    ),
    bodyLarge = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = TextPrimary,
    ),
    bodyMedium = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextSecondary,
    ),
    bodySmall = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        color      = TextTertiary,
    ),
    labelLarge = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        color      = TextPrimary,
    ),
    labelMedium = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        color      = TextSecondary,
    ),
    labelSmall = TextStyle(
        fontFamily = VitalityFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        color      = TextTertiary,
    ),
)
