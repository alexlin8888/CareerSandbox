package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   績效儀表板 —— 入職第一週的三項計量與綜合趨勢回顧
   三計量：主管信任 / 同事情誼 / 專業形象，配綜合趨勢與本週關鍵決定。
   暖白底 + espresso 強調，沿用 workplace 沙盒視覺，全部自繪不碰真品牌。
   ===================================================================== */

private val PaperCream = Color(0xFFFFF8F3)
private val DashOrange = Color(0xFFF2531C)
private val DashAmber = Color(0xFFFFB627)
private val DashGreen = Color(0xFF06994E)

@Composable
fun NovaDashboardScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    // 接真實遊玩數值(三計量 0-10 → 百分比),不再寫死
    val mtP = WorkplaceState.managerTrust.value * 10
    val pbP = WorkplaceState.peerBond.value * 10
    val piP = WorkplaceState.proImage.value * 10
    fun dText(p: Int) = if (p >= 60) "▲ 不錯" else if (p >= 30) "— 持平" else "▼ 待加強"
    fun dCol(p: Int) = if (p >= 60) DashGreen else if (p >= 30) Color(0xFFC47F17) else Color(0xFFC0532B)
    // 本週關鍵決定:反映 Day3 排程那拍實際選了什麼
    val keyDecision = when {
        WorkplaceState.hasFlag("d3_sacrifice_eng") -> "硬上排程，工程加班補"
        WorkplaceState.hasFlag("d3_sacrifice_vivian") -> "延一週，穩住品質"
        WorkplaceState.hasFlag("d3_sacrifice_client") -> "縮減範圍、先上基本版"
        else -> "在排程壓力下做了取捨"
    }
    // 整體等第:三計量總分(0-30)
    val total = mtP + pbP + piP
    val grade = when {
        total >= 240 -> "A"
        total >= 180 -> "B"
        total >= 120 -> "C"
        else -> "D"
    }

    Column(Modifier.fillMaxSize().background(PaperCream).verticalScroll(scroll)) {

        // ===== Hero =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 14.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("WEEK 1 · 績效", color = DashOrange, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5f.sp)
                Spacer(Modifier.height(4.dp))
                Text("你的第一週", color = Espresso, fontSize = 26.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
            }
            NovaCircleAvatar(size = 38.dp, letter = "我", bg = Color(0xFFB85C3A))
        }

        Column(
            Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            // ===== 三計量環 =====
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("主管信任", mtP, Color(0xFFF3E3DA), DashOrange, dText(mtP), dCol(mtP), Modifier.weight(1f))
                MetricCard("同事情誼", pbP, Color(0xFFF5ECD9), DashAmber, dText(pbP), dCol(pbP), Modifier.weight(1f))
                MetricCard("專業形象", piP, Color(0xFFDFF0EC), Color(0xFF5BB6A6), dText(piP), dCol(piP), Modifier.weight(1f))
            }

            // ===== 綜合趨勢 =====
            Column(
                Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(18.dp))
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(start = 16.dp, end = 16.dp, top = 15.dp, bottom = 10.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("綜合表現趨勢", color = Espresso, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("▲ 本週 +18%", color = DashOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                TrendChart(Modifier.fillMaxWidth().height(110.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("一", "二", "三", "四", "五").forEach {
                        Text(it, color = Color(0xFF9B8A7D), fontSize = 11.sp)
                    }
                }
            }

            // ===== 本週關鍵決定 =====
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Espresso)
                    .padding(horizontal = 18.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("本週關鍵決定", color = Color(0xA6FFF8F3), fontSize = 12.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(keyDecision, color = PaperCream, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier.size(42.dp).clip(CircleShape).background(Color(0x2EFFB627)),
                    contentAlignment = Alignment.Center,
                ) { Text(grade, color = DashAmber, fontSize = 22.sp, fontWeight = FontWeight.Black) }
            }
        }
    }
}

// ---------- 私有元件 ----------

@Composable
private fun MetricCard(
    label: String,
    pct: Int,
    trackColor: Color,
    ringColor: Color,
    delta: String,
    deltaColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.shadow(6.dp, RoundedCornerShape(18.dp))
            .background(Color.White, RoundedCornerShape(18.dp))
            .padding(vertical = 13.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val sw = 8.dp.toPx()
                val d = size.minDimension - sw
                val topLeft = Offset(sw / 2f, sw / 2f)
                val arcSize = Size(d, d)
                drawArc(trackColor, 0f, 360f, false, topLeft, arcSize, style = Stroke(sw))
                drawArc(
                    ringColor, -90f, pct / 100f * 360f, false,
                    topLeft, arcSize, style = Stroke(sw, cap = StrokeCap.Round),
                )
            }
            Text("$pct%", color = Espresso, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(3.dp))
        Text(label, color = Espresso, fontSize = 11.5f.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(1.dp))
        Text(delta, color = deltaColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

/** 綜合趨勢面積折線（向上）；末點以白心高亮。 */
@Composable
private fun TrendChart(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        // 設計座標 (viewBox 320×128) 換算成比例
        val xs = listOf(0.0625f, 0.281f, 0.5f, 0.719f, 0.9375f)
        val ys = listOf(0.75f, 0.656f, 0.688f, 0.422f, 0.281f)
        val baseline = 0.891f

        // 格線
        listOf(0.234f, 0.516f, 0.797f).forEach { gy ->
            drawLine(Color(0xFFF0E8E0), Offset(0f, h * gy), Offset(w, h * gy), 1.dp.toPx())
        }

        // 面積
        val area = Path().apply {
            moveTo(w * xs[0], h * ys[0])
            for (i in 1 until xs.size) lineTo(w * xs[i], h * ys[i])
            lineTo(w * xs.last(), h * baseline)
            lineTo(w * xs.first(), h * baseline)
            close()
        }
        drawPath(area, Brush.verticalGradient(listOf(Color(0x2EF2531C), Color(0x00F2531C))))

        // 折線
        val line = Path().apply {
            moveTo(w * xs[0], h * ys[0])
            for (i in 1 until xs.size) lineTo(w * xs[i], h * ys[i])
        }
        drawPath(line, DashOrange, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))

        // 節點
        for (i in 0 until xs.size - 1) {
            drawCircle(DashOrange, 4.dp.toPx(), Offset(w * xs[i], h * ys[i]))
        }
        val lx = w * xs.last(); val ly = h * ys.last()
        drawCircle(Color.White, 5.5.dp.toPx(), Offset(lx, ly))
        drawCircle(DashOrange, 5.5.dp.toPx(), Offset(lx, ly), style = Stroke(3.dp.toPx()))
    }
}
