package com.careersandbox.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.BrandYellow
import com.careersandbox.app.ui.theme.InkBlack

/**
 * 去 AI 化 — 手作風元件集
 * 三個共用元件:手繪底線、螢光筆塗字、傾斜便利貼。
 * 全部是純視覺,拿掉不影響邏輯。要復原直接刪除使用處即可。
 */

/** 手繪波浪底線 — 放在標題下方 */
@Composable
fun HandDrawnUnderline(
    width: Dp,
    modifier: Modifier = Modifier,
    color: Color = BrandYellow,
    strokeWidth: Float = 3f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h * 0.55f
        val path = Path().apply {
            moveTo(w * 0.02f, midY)
            cubicTo(
                w * 0.25f, midY - h * 0.4f,
                w * 0.45f, midY + h * 0.25f,
                w * 0.6f, midY,
            )
            cubicTo(
                w * 0.75f, midY - h * 0.3f,
                w * 0.9f, midY + h * 0.2f,
                w * 0.98f, midY - h * 0.1f,
            )
        }
        drawPath(path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/** 螢光筆塗過的文字 — 強調關鍵字 */
@Composable
fun HighlighterText(
    text: String,
    modifier: Modifier = Modifier,
    highlightColor: Color = BrandYellow,
    textColor: Color = InkBlack,
    fontSize: Int = 14,
    fontWeight: FontWeight = FontWeight.SemiBold,
    rotation: Float = -1.5f,
) {
    Box(modifier = modifier) {
        // 螢光筆痕(不規則圓角 + 微旋轉 + 半透明),墊在文字底下、撐滿文字大小
        Box(
            modifier = Modifier
                .matchParentSize()
                .rotate(rotation)
                .background(
                    highlightColor.copy(alpha = 0.55f),
                    RoundedCornerShape(topStartPercent = 45, topEndPercent = 55, bottomStartPercent = 50, bottomEndPercent = 48),
                ),
        )
        Text(
            text,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
        )
    }
}

/** 傾斜便利貼 — 隨手貼的標註 */
@Composable
fun StickyNote(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = BrandPeach,
    textColor: Color = Color(0xFF7A3A00),
    rotation: Float = 3f,
    fontSize: Int = 11,
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .background(bgColor, RoundedCornerShape(3.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
    ) {
        Text(text, color = textColor, fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** 手繪箭頭 — 從 from 指向 to(相對 Canvas 座標,0..1) */
@Composable
fun HandDrawnArrow(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF7A3A00),
    strokeWidth: Float = 1.6f,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // 一條彎曲線從右上到左下 + 箭頭
        val path = Path().apply {
            moveTo(w * 0.9f, h * 0.15f)
            quadraticBezierTo(w * 0.5f, h * 0.1f, w * 0.18f, h * 0.7f)
        }
        drawPath(path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        // 箭頭兩撇
        val tip = Offset(w * 0.18f, h * 0.7f)
        drawLine(color, tip, Offset(w * 0.32f, h * 0.52f), strokeWidth, StrokeCap.Round)
        drawLine(color, tip, Offset(w * 0.36f, h * 0.78f), strokeWidth, StrokeCap.Round)
    }
}
