package com.careersandbox.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.careersandbox.app.ui.theme.*

/* ============== Section Title ============== */
@Composable
fun SectionTitle(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.pressScale { onAction() },
            )
        }
    }
}

/* ============== Cards ============== */

/** 純白底卡片,薄陰影 */
@Composable
fun WhiteCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x14000000))
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surface)
        .then(if (onClick != null) Modifier.pressScale { onClick() } else Modifier)
        .padding(20.dp)
    Column(modifier = base, content = content)
}

/** 深底 Hero 卡片 - 深藍黑漸層 */
@Composable
fun DarkHeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp), spotColor = InkBlack.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(28.dp))
        .background(DarkHeroGradient)
        .then(if (onClick != null) Modifier.pressScale { onClick() } else Modifier)
        .padding(24.dp)
    Column(modifier = base, content = content)
}

/** 橘黃漸層卡片 */
@Composable
fun OrangeGradientCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = BrandOrange.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(24.dp))
        .background(HeroGradient)
        .then(if (onClick != null) Modifier.pressScale { onClick() } else Modifier)
        .padding(20.dp)
    Column(modifier = base, content = content)
}

/* ============== Buttons ============== */

/** 深底 + 白字主按鈕 */
@Composable
fun PrimaryDarkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) InkBlack else InkGray400)
            .pressScale(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, color = PaperWhite,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 橘色主按鈕 (用於 hero CTA) */
@Composable
fun PrimaryOrangeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(56.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp),
                spotColor = BrandOrange.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(16.dp))
            .background(HeroGradient)
            .pressScale { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, color = PaperWhite,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Outline secondary 按鈕 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .pressScale { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }
    }
}

/* ============== Chip ============== */
@Composable
fun PillChip(
    label: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val bg = if (selected) InkBlack else MaterialTheme.colorScheme.surface
    val fg = if (selected) PaperWhite else InkGray500
    val borderColor = if (selected) InkBlack else MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .then(if (onClick != null) Modifier.pressScale { onClick() } else Modifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = fg, fontWeight = FontWeight.Medium)
    }
}

/* ============== Stat Pill ============== */
@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = InkGray500)
    }
}

/* ============== Progress ============== */
@Composable
fun ThinProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = InkGray200,
    indicatorBrush: Brush = HeroGradient,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(indicatorBrush)
        )
    }
}

/* ============== Empty State ============== */
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Outlined.Inbox,
    title: String,
    description: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape)
                .background(InkGray100),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = InkGray500,
                modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium,
                color = InkGray500, textAlign = TextAlign.Center)
        }
    }
}

/* ============== Avatar ============== */
@Composable
fun Avatar(
    name: String,
    size: androidx.compose.ui.unit.Dp = 44.dp,
    background: Color = InkBlack,
    foreground: Color = PaperWhite,
) {
    Box(
        Modifier.size(size).clip(CircleShape).background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            name.first().toString(),
            color = foreground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

/* ============== Helper: 已直接使用 androidx.compose.foundation.border ============== */
