package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.theme.PaperWhite

/* =====================================================================
   NovaKit —— Nova 系列共用元件
   - 主角頭像吃既有立繪（clip 成圓 / 方圓角）；非主角用字母色圓。
   - 確認清單外的小圖示一律 Canvas 自繪，避免 material-icons 缺漏導致 compile fail。
   擬真靠通用 UI 語言，不碰任何真品牌 logo / 商標 / trade dress。
   ===================================================================== */

// espresso 深色（不在 Color.kt，本地定義，dashboard / 鎖屏用）
val Espresso = Color(0xFF281C12)
val EspressoDeep = Color(0xFF1A1109)

// ---------- 頭像 ----------

/** 圓形頭像：res 有值吃立繪，否則畫字母色圓。 */
@Composable
fun NovaCircleAvatar(
    size: Dp,
    res: Int? = null,
    letter: String = "",
    bg: Color = Color(0xFFCBD5E1),
    fg: Color = PaperWhite,
    modifier: Modifier = Modifier,
) {
    if (res != null) {
        Image(
            painter = painterResource(res),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(size).clip(CircleShape).background(PaperWhite),
        )
    } else {
        Box(
            modifier = modifier.size(size).clip(CircleShape).background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(letter, color = fg, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.4f).sp)
        }
    }
}

/** 方圓角頭像（Team 用，刻意非圓以區隔通用 IM）。 */
@Composable
fun NovaSquareAvatar(
    size: Dp,
    res: Int? = null,
    letter: String = "",
    bg: Color = Color(0xFFCBD5E1),
    fg: Color = PaperWhite,
    corner: Dp = 10.dp,
    modifier: Modifier = Modifier,
) {
    if (res != null) {
        Image(
            painter = painterResource(res),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(size).clip(RoundedCornerShape(corner)).background(PaperWhite),
        )
    } else {
        Box(
            modifier = modifier.size(size).clip(RoundedCornerShape(corner)).background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(letter, color = fg, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.42f).sp)
        }
    }
}

/** 重要標記菱形（旋轉方塊，避免依賴未確認 icon）。 */
@Composable
fun NovaDiamond(color: Color, size: Dp = 10.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size + 4.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(size).rotate(45f).clip(RoundedCornerShape(2.dp)).background(color))
    }
}

// ---------- Canvas 自繪小圖示（確認清單外的一律自畫） ----------

@Composable
fun NovaSearchIcon(tint: Color, size: Dp = 20.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.09f
        val r = w * 0.30f
        drawCircle(tint, radius = r, center = Offset(w * 0.42f, w * 0.42f), style = Stroke(sw))
        drawLine(tint, Offset(w * 0.64f, w * 0.64f), Offset(w * 0.88f, w * 0.88f), sw, StrokeCap.Round)
    }
}

/** 水平三點（kebab / ⋯）。 */
@Composable
fun NovaKebabIcon(tint: Color, size: Dp = 20.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val r = w * 0.07f
        for (k in 0..2) drawCircle(tint, r, Offset(w * (0.25f + k * 0.25f), w * 0.5f))
    }
}

@Composable
fun NovaPinIcon(tint: Color, size: Dp = 14.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.12f
        // 圖釘頭 + 針，45°
        drawCircle(tint, w * 0.20f, Offset(w * 0.62f, w * 0.36f))
        drawLine(tint, Offset(w * 0.50f, w * 0.48f), Offset(w * 0.24f, w * 0.78f), sw, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.74f, w * 0.24f), Offset(w * 0.84f, w * 0.14f), sw, StrokeCap.Round)
    }
}

/** 靜音：鈴鐺 + 斜線。 */
@Composable
fun NovaMuteIcon(tint: Color, size: Dp = 14.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.10f
        val dome = Path().apply {
            moveTo(w * 0.28f, w * 0.62f)
            lineTo(w * 0.50f, w * 0.20f)
            lineTo(w * 0.72f, w * 0.62f)
            close()
        }
        drawPath(dome, tint, style = Stroke(sw))
        drawCircle(tint, w * 0.06f, Offset(w * 0.50f, w * 0.74f))
        drawLine(tint, Offset(w * 0.16f, w * 0.84f), Offset(w * 0.84f, w * 0.16f), sw, StrokeCap.Round)
    }
}

/** 附件迴紋針。 */
@Composable
fun NovaClipIcon(tint: Color, size: Dp = 16.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.10f
        val p = Path().apply {
            moveTo(w * 0.66f, w * 0.28f)
            lineTo(w * 0.34f, w * 0.60f)
            cubicTo(w * 0.22f, w * 0.72f, w * 0.40f, w * 0.90f, w * 0.52f, w * 0.78f)
            lineTo(w * 0.78f, w * 0.52f)
            cubicTo(w * 0.92f, w * 0.38f, w * 0.66f, w * 0.12f, w * 0.52f, w * 0.26f)
            lineTo(w * 0.30f, w * 0.48f)
        }
        drawPath(p, tint, style = Stroke(sw))
    }
}

/** 定位 pin（地圖水滴）。 */
@Composable
fun NovaLocationIcon(tint: Color, size: Dp = 14.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.10f
        drawCircle(tint, w * 0.26f, Offset(w * 0.5f, w * 0.38f), style = Stroke(sw))
        val tail = Path().apply {
            moveTo(w * 0.30f, w * 0.52f)
            lineTo(w * 0.5f, w * 0.90f)
            lineTo(w * 0.70f, w * 0.52f)
        }
        drawPath(tail, tint, style = Stroke(sw))
    }
}

/** 舉手。 */
@Composable
fun NovaHandIcon(tint: Color, size: Dp = 18.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.09f
        // 掌
        drawRoundRect(
            tint, topLeft = Offset(w * 0.30f, w * 0.46f), size = Size(w * 0.40f, w * 0.34f),
            cornerRadius = CornerRadius(w * 0.10f), style = Stroke(sw),
        )
        // 四指
        for (k in 0..3) {
            val x = w * (0.36f + k * 0.10f)
            drawLine(tint, Offset(x, w * 0.46f), Offset(x, w * 0.20f), sw, StrokeCap.Round)
        }
        // 拇指
        drawLine(tint, Offset(w * 0.30f, w * 0.58f), Offset(w * 0.16f, w * 0.50f), sw, StrokeCap.Round)
    }
}

/** 手電筒。 */
@Composable
fun NovaFlashlightIcon(tint: Color, size: Dp = 22.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.08f
        val body = Path().apply {
            moveTo(w * 0.40f, w * 0.30f)
            lineTo(w * 0.60f, w * 0.30f)
            lineTo(w * 0.56f, w * 0.78f)
            lineTo(w * 0.44f, w * 0.78f)
            close()
        }
        drawPath(body, tint, style = Stroke(sw))
        drawLine(tint, Offset(w * 0.40f, w * 0.40f), Offset(w * 0.60f, w * 0.40f), sw, StrokeCap.Round)
        // 光束
        for (k in -1..1) {
            val x = w * (0.5f + k * 0.12f)
            drawLine(tint, Offset(x, w * 0.24f), Offset(x, w * 0.12f), sw * 0.8f, StrokeCap.Round)
        }
    }
}

/** 對話泡泡（底部導覽用）。 */
@Composable
fun NovaBubbleIcon(tint: Color, size: Dp = 24.dp, filled: Boolean = false, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.08f
        val style = if (filled) androidx.compose.ui.graphics.drawscope.Fill else Stroke(sw)
        drawRoundRect(
            tint, topLeft = Offset(w * 0.16f, w * 0.20f), size = Size(w * 0.68f, w * 0.48f),
            cornerRadius = CornerRadius(w * 0.16f), style = style,
        )
        val tail = Path().apply {
            moveTo(w * 0.30f, w * 0.66f)
            lineTo(w * 0.26f, w * 0.84f)
            lineTo(w * 0.46f, w * 0.66f)
            close()
        }
        drawPath(tail, tint, style = if (filled) androidx.compose.ui.graphics.drawscope.Fill else Stroke(sw))
    }
}

/** 動態（feed，堆疊條）。 */
@Composable
fun NovaFeedIcon(tint: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.08f
        for (k in 0..2) {
            val y = w * (0.28f + k * 0.22f)
            drawRoundRect(
                tint, topLeft = Offset(w * 0.20f, y), size = Size(w * 0.60f, w * 0.10f),
                cornerRadius = CornerRadius(w * 0.05f),
            )
        }
    }
}

/** 行事曆（identity / 底部導覽）。 */
@Composable
fun NovaCalendarIcon(tint: Color, size: Dp = 24.dp, filled: Boolean = false, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = size.toPx(); val sw = w * 0.08f
        drawRoundRect(
            tint, topLeft = Offset(w * 0.16f, w * 0.22f), size = Size(w * 0.68f, w * 0.62f),
            cornerRadius = CornerRadius(w * 0.10f),
            style = if (filled) androidx.compose.ui.graphics.drawscope.Fill else Stroke(sw),
        )
        if (!filled) {
            drawLine(tint, Offset(w * 0.16f, w * 0.40f), Offset(w * 0.84f, w * 0.40f), sw, StrokeCap.Round)
            drawLine(tint, Offset(w * 0.34f, w * 0.14f), Offset(w * 0.34f, w * 0.28f), sw, StrokeCap.Round)
            drawLine(tint, Offset(w * 0.66f, w * 0.14f), Offset(w * 0.66f, w * 0.28f), sw, StrokeCap.Round)
        }
    }
}

/** 績效趨勢 sparkline（向上）。 */
@Composable
fun NovaSparkline(line: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height; val sw = h * 0.06f
        val pts = listOf(0.10f to 0.78f, 0.28f to 0.66f, 0.46f to 0.70f, 0.62f to 0.44f, 0.80f to 0.36f, 0.94f to 0.20f)
        val path = Path()
        pts.forEachIndexed { i, (px, py) ->
            val x = w * px; val y = h * py
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, line, style = Stroke(sw, cap = StrokeCap.Round))
        pts.forEach { (px, py) -> drawCircle(line, sw * 1.4f, Offset(w * px, h * py)) }
    }
}
