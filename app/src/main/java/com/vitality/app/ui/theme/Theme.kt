package com.vitality.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────
// VITALITY COLOR PALETTE — Minimalist Neumorphism
// ─────────────────────────────────────────────────────────────

// Base background (warm light gray for neumorphism)
val NeuBackground    = Color(0xFFEEF0F5)
val NeuSurface       = Color(0xFFEEF0F5)

// Neumorphic shadows
val NeuShadowDark    = Color(0xFFCBCDD4)
val NeuShadowLight   = Color(0xFFFFFFFF)

// Card surface (slightly lighter)
val NeuCard          = Color(0xFFF2F4F8)

// ─────────────────────────────────────────────────────────────
// STATUS COLORS — Soft & Non-threatening
// ─────────────────────────────────────────────────────────────

// Healthy — Teal / Mint
val HealthyGreen     = Color(0xFF4ECDC4)
val HealthyGreenDark = Color(0xFF2BAB9F)
val HealthyGreenBg   = Color(0xFFE8FAF8)

// Attention — Warm Yellow / Peach
val AttentionYellow  = Color(0xFFFFB347)
val AttentionYellowDark = Color(0xFFE8960E)
val AttentionYellowBg = Color(0xFFFFF8EC)

// Poor — Soft Coral (NOT harsh red)
val PoorCoral        = Color(0xFFFF6B6B)
val PoorCoralDark    = Color(0xFFE84545)
val PoorCoralBg      = Color(0xFFFFEEEE)

// ─────────────────────────────────────────────────────────────
// BRAND COLORS
// ─────────────────────────────────────────────────────────────

val BrandTeal        = Color(0xFF4ECDC4)
val BrandBlue        = Color(0xFF74B9FF)
val BrandPurple      = Color(0xFFA29BFE)
val BrandPink        = Color(0xFFFF7675)

// Vitality Ring gradient colors
val RingGradientStart = Color(0xFF4ECDC4)
val RingGradientEnd   = Color(0xFF74B9FF)

// ─────────────────────────────────────────────────────────────
// TEXT COLORS
// ─────────────────────────────────────────────────────────────

val TextPrimary      = Color(0xFF2D3436)    // Dark gray (not black)
val TextSecondary    = Color(0xFF636E72)    // Medium gray
val TextTertiary     = Color(0xFFB2BEC3)    // Light gray
val TextOnColor      = Color(0xFFFFFFFF)

// ─────────────────────────────────────────────────────────────
// MATERIAL THEME
// ─────────────────────────────────────────────────────────────

private val VitalityColorScheme = lightColorScheme(
    primary          = BrandTeal,
    onPrimary        = TextOnColor,
    primaryContainer = HealthyGreenBg,
    secondary        = BrandBlue,
    onSecondary      = TextOnColor,
    tertiary         = BrandPurple,
    background       = NeuBackground,
    surface          = NeuSurface,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = PoorCoral,
)

@Composable
fun VitalityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VitalityColorScheme,
        typography  = VitalityTypography,
        content     = content,
    )
}
