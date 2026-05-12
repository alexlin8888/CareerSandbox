package com.careersandbox.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import com.careersandbox.app.ui.theme.BrandAmber
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandYellow
import com.careersandbox.app.ui.theme.GlowPink
import com.careersandbox.app.ui.theme.GlowPurple

/**
 * 頂部 wave hero 區:
 *  - 上半部漸層色塊
 *  - 底部用 cubic bezier 弧線過渡到白底,沒有硬邊界
 *  - 整個畫面寬度,沒有圓角(因為它就是頁面延伸)
 */
@Composable
fun WaveHeroBackground(
    modifier: Modifier = Modifier,
    gradient: Brush,
    heightDp: Int = 280,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
    ) {
        // 主漸層區塊
        val wavePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.82f)
            // 不對稱波形,左低右高
            cubicTo(
                size.width * 0.7f, size.height * 1.05f,
                size.width * 0.3f, size.height * 0.68f,
                0f, size.height * 0.92f,
            )
            close()
        }
        drawPath(path = wavePath, brush = gradient)
    }
}

/**
 * 裝飾線稿圓圈 — 從畫面邊緣探出來的細圓
 *  - 用 stroke 描邊,不填色
 *  - 半透明,在畫面邊緣破框感
 */
@Composable
fun DecorativeCircle(
    modifier: Modifier = Modifier,
    color: Color = BrandOrange,
    strokeWidthDp: Int = 2,
    alpha: Float = 0.4f,
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size.minDimension / 2 - strokeWidthDp.dp.toPx() / 2,
            style = Stroke(width = strokeWidthDp.dp.toPx()),
        )
    }
}

/**
 * 散落的線稿幾何裝飾 — 圓圈、菱形、十字、星星
 *  - 全部用細線,半透明
 *  - 隨機散佈在畫面 4 個區塊位置
 */
@Composable
fun ScatteredDecorations(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val stroke = Stroke(width = 1.5.dp.toPx())

        // 左上小菱形
        translate(left = size.width * 0.08f, top = size.height * 0.15f) {
            rotate(45f, pivot = Offset(0f, 0f)) {
                drawRect(
                    color = BrandOrange.copy(alpha = 0.3f),
                    topLeft = Offset(-12.dp.toPx(), -12.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(24.dp.toPx(), 24.dp.toPx()),
                    style = stroke,
                )
            }
        }

        // 右上線稿圓
        drawCircle(
            color = GlowPurple.copy(alpha = 0.35f),
            radius = 18.dp.toPx(),
            center = Offset(size.width * 0.88f, size.height * 0.08f),
            style = stroke,
        )

        // 中右閃光星(四角星簡化版)
        val starCenter = Offset(size.width * 0.92f, size.height * 0.45f)
        drawLine(
            color = BrandAmber.copy(alpha = 0.5f),
            start = Offset(starCenter.x - 10.dp.toPx(), starCenter.y),
            end = Offset(starCenter.x + 10.dp.toPx(), starCenter.y),
            strokeWidth = 1.5.dp.toPx(),
        )
        drawLine(
            color = BrandAmber.copy(alpha = 0.5f),
            start = Offset(starCenter.x, starCenter.y - 10.dp.toPx()),
            end = Offset(starCenter.x, starCenter.y + 10.dp.toPx()),
            strokeWidth = 1.5.dp.toPx(),
        )

        // 左中圓圈
        drawCircle(
            color = GlowPink.copy(alpha = 0.3f),
            radius = 22.dp.toPx(),
            center = Offset(size.width * 0.05f, size.height * 0.55f),
            style = stroke,
        )

        // 左下十字
        val crossCenter = Offset(size.width * 0.15f, size.height * 0.88f)
        drawLine(
            color = BrandYellow.copy(alpha = 0.5f),
            start = Offset(crossCenter.x - 8.dp.toPx(), crossCenter.y),
            end = Offset(crossCenter.x + 8.dp.toPx(), crossCenter.y),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = BrandYellow.copy(alpha = 0.5f),
            start = Offset(crossCenter.x, crossCenter.y - 8.dp.toPx()),
            end = Offset(crossCenter.x, crossCenter.y + 8.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )

        // 右下大圓 — 半透明,部分出畫面
        drawCircle(
            color = BrandOrange.copy(alpha = 0.2f),
            radius = 60.dp.toPx(),
            center = Offset(size.width * 1.0f, size.height * 0.95f),
            style = Stroke(width = 1.dp.toPx()),
        )
    }
}

/**
 * 細線分區 divider — 用斷續線 + 文字標記分區,代替卡片邊框
 */
@Composable
fun SectionDivider(
    modifier: Modifier = Modifier,
    color: Color = Color(0x22000000),
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val dashWidth = 4.dp.toPx()
        val gap = 4.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = color,
                start = Offset(x, 0f),
                end = Offset(x + dashWidth, 0f),
                strokeWidth = 1.dp.toPx(),
            )
            x += dashWidth + gap
        }
    }
}
