package com.careersandbox.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// === Brand 主色 ===
val BrandOrange = Color(0xFFFF6B35)
val BrandAmber = Color(0xFFFFB627)
val BrandPeach = Color(0xFFFFE0B2)
val BrandDeepOrange = Color(0xFFD84315)
val BrandYellow = Color(0xFFFFD93D)

// === 光暈色(新)===
val GlowPink = Color(0xFFFF9AAA)
val GlowPurple = Color(0xFFC79AFF)
val GlowOrange = Color(0xFFFFAA6B)
val GlowAmber = Color(0xFFFFD93D)

// === Ink 深色 ===
val InkBlack = Color(0xFF0B0E14)
val InkDeepBlue = Color(0xFF111827)
val InkSlate = Color(0xFF1F2937)
val InkCharcoal = Color(0xFF0A0D12)
val InkGray700 = Color(0xFF374151)
val InkGray500 = Color(0xFF6B7280)
val InkGray400 = Color(0xFF9CA3AF)
val InkGray300 = Color(0xFFD1D5DB)
val InkGray200 = Color(0xFFE5E7EB)
val InkGray100 = Color(0xFFF3F4F6)
val InkGray50 = Color(0xFFF9FAFB)

// === Surface ===
val PaperWhite = Color(0xFFFFFFFF)
val PaperOff = Color(0xFFFAFAFA)
val PaperWarm = Color(0xFFFFF8F3)
val GlassWhite = Color(0x80FFFFFF)
val GlassDark = Color(0x33FFFFFF)

// === Accent ===
val AccentGreen = Color(0xFF10B981)
val AccentBlue = Color(0xFF3B82F6)
val AccentRed = Color(0xFFEF4444)
val AccentYellow = Color(0xFFF59E0B)

// === Gradient ===
val HeroGradient = Brush.linearGradient(
    colors = listOf(BrandOrange, BrandAmber),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end = androidx.compose.ui.geometry.Offset(1000f, 800f),
)

val DarkHeroGradient = Brush.linearGradient(
    colors = listOf(InkCharcoal, InkBlack, InkDeepBlue),
)

val WarmCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFEAD8), Color(0xFFFFE0B2)),
)

// 粉紫光暈背景(從底部往上)
val PinkPurpleGlow = Brush.verticalGradient(
    colors = listOf(
        Color(0x00FFFFFF),
        GlowPink.copy(alpha = 0.25f),
        GlowPurple.copy(alpha = 0.2f),
        GlowAmber.copy(alpha = 0.3f),
    ),
)

// 黑底光暈散射(hero 卡)
val DarkGlowRadial = Brush.radialGradient(
    colors = listOf(
        BrandOrange.copy(alpha = 0.4f),
        GlowPurple.copy(alpha = 0.15f),
        InkCharcoal,
    ),
)

// 玻璃感邊框
val GlassBorderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x66FFFFFF),
        Color(0x11FFFFFF),
    ),
)
