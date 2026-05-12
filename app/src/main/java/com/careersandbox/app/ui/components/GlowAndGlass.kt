package com.careersandbox.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.careersandbox.app.ui.theme.*

/** 粉紫光暈背景容器 — 用在淺底頁面下半部 */
@Composable
fun GlowBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 粉紫光暈往上散
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    // 底部光暈圓
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GlowPink.copy(alpha = 0.4f),
                                GlowPink.copy(alpha = 0f),
                            ),
                            center = Offset(size.width * 0.2f, size.height * 1.0f),
                            radius = size.width * 0.8f,
                        ),
                        radius = size.width * 0.8f,
                        center = Offset(size.width * 0.2f, size.height * 1.0f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GlowPurple.copy(alpha = 0.35f),
                                GlowPurple.copy(alpha = 0f),
                            ),
                            center = Offset(size.width * 0.9f, size.height * 0.85f),
                            radius = size.width * 0.7f,
                        ),
                        radius = size.width * 0.7f,
                        center = Offset(size.width * 0.9f, size.height * 0.85f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GlowAmber.copy(alpha = 0.3f),
                                GlowAmber.copy(alpha = 0f),
                            ),
                            center = Offset(size.width * 0.5f, size.height * 1.1f),
                            radius = size.width * 0.6f,
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width * 0.5f, size.height * 1.1f),
                    )
                }
        )
        content()
    }
}

/** 深底光暈背景 — 用在 splash 之類整頁深色 */
@Composable
fun DarkGlowBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(InkCharcoal)) {
        Box(
            Modifier.fillMaxSize().drawBehind {
                // 左下橘色光暈
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandOrange.copy(alpha = 0.45f),
                            BrandOrange.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.15f, size.height * 0.85f),
                        radius = size.width * 0.9f,
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(size.width * 0.15f, size.height * 0.85f),
                )
                // 右上紫色光暈
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPurple.copy(alpha = 0.35f),
                            GlowPurple.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.1f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                )
                // 中段琥珀光
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandAmber.copy(alpha = 0.25f),
                            BrandAmber.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.55f),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.8f, size.height * 0.55f),
                )
            }
        )
        content()
    }
}

/** 玻璃感卡(深底用):半透明白底 + 漸層邊框 */
@Composable
fun GlassDarkCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color(0x14FFFFFF))
        .drawBehind {
            // 漸層邊框
            val stroke = 1.5.dp.toPx()
            drawRoundRect(
                brush = GlassBorderGradient,
                style = Stroke(stroke),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                size = Size(size.width - stroke, size.height - stroke),
                topLeft = Offset(stroke / 2, stroke / 2),
            )
        }
        .padding(20.dp)
    androidx.compose.foundation.layout.Column(modifier = base, content = content)
}
