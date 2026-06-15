package com.careersandbox.app.ui.screens.interview

import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.InterviewRecord
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun InterviewHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            // === Hero 區 ===
            StaggeredAppear(delayMillis = 0) { HeroSection() }

            Spacer(Modifier.height(20.dp))

            // === #6 Avatar 成長卡 ===
            StaggeredAppear(delayMillis = 90) { AvatarGrowthCard() }

            Spacer(Modifier.height(20.dp))

            // === #1 快速練習(低門檻入口,與正式 mock 區分)===
            StaggeredAppear(delayMillis = 170) { QuickPracticeCard(navController) }

            Spacer(Modifier.height(32.dp))

            // === 兩個方案卡(都帶插畫)===
            StaggeredAppear(delayMillis = 250) { PlanCards(navController) }

            Spacer(Modifier.height(24.dp))

            // === 三對一 panel 入口(已上線)===

            Spacer(Modifier.height(32.dp))

            // === 歷史紀錄(無框列表)===
            StaggeredAppear(delayMillis = 410) { HistorySection(navController) }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeroSection() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 220,
        )
        ScatteredDecorations(
            modifier = Modifier.fillMaxSize().alpha(0.6f)
        )
        // 品牌大使(打氣,右下角)
        Image(
            painter = painterResource(R.drawable.beaver_celebrate),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 4.dp)
                .size(118.dp),
            contentScale = ContentScale.Fit,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
        ) {
            Text("INTERVIEW PRACTICE",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("面試")
                    withStyle(SpanStyle(color = BrandYellow)) { append("不再") }
                    append("\n緊張到失常。")
                },
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                lineHeight = 42.sp,
            )
            Spacer(Modifier.height(12.dp))
            // 統計
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(BrandYellow))
                Spacer(Modifier.width(6.dp))
                Text("已完成 4 次 · 平均 74 分",
                    color = PaperWhite.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PlanCards(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 大標
        Text(
            text = buildAnnotatedString {
                append("選一種")
                withStyle(SpanStyle(color = BrandOrange)) { append("開始") }
            },
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            modifier = Modifier.padding(start = 4.dp),
        )

        // 個人面試卡
        PlanCard(
            number = "01",
            title = "個人面試",
            eyebrow = "1 對 1",
            description = "AI 扮演面試官,\n從履歷出題、即時追問",
            tagText = "入門推薦",
            tagBg = BrandYellow,
            tagFg = InkCharcoal,
            cardBg = SolidColor(BrandDeepOrange),
            illustrationRes = R.drawable.undraw_video_call_i5de,
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_INDIVIDUAL) },
        )

        // 團體面試卡
        PlanCard(
            number = "02",
            title = "團體面試",
            eyebrow = "3-5 人小組",
            description = "AI 扮演其他應徵者,\n真實小組討論演練",
            tagText = "AI 同儕同場",
            tagBg = BrandYellow,
            tagFg = InkCharcoal,
            cardBg = SolidColor(InkCharcoal),
            illustrationRes = R.drawable.undraw_group_video_k4jx,
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_GROUP) },
        )

        // 影像面試卡(新,創新性)
        PlanCard(
            number = "03",
            title = "影像面試",
            eyebrow = "鏡頭 + 即時覺察",
            description = "開鏡頭對著河狸面試官練,\n看自己的眼神與表現",
            tagText = "練習工具",
            tagBg = AccentGreen,
            tagFg = PaperWhite,
            cardBg = SolidColor(BrandOrange),
            illustrationRes = R.drawable.beaver_present,
            onClick = { navController.navigate(Routes.INTERVIEW_VIDEO) },
        )
    }
}

@Composable
private fun PlanCard(
    number: String,
    title: String,
    eyebrow: String,
    description: String,
    tagText: String,
    tagBg: Color,
    tagFg: Color,
    cardBg: Brush,
    illustrationRes: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(20.dp, RoundedCornerShape(28.dp),
                spotColor = BrandOrange.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(28.dp))
            .background(cardBg)
            .pressScale(onClick = onClick),
    ) {
        // 卡內裝飾線稿
        ScatteredDecorations(
            modifier = Modifier.fillMaxSize().alpha(0.3f)
        )
        // 文字內容(左側)
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.6f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(number,
                        color = PaperWhite.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(eyebrow,
                        color = PaperWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Text(title,
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text(description,
                    color = PaperWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp)
            }
            // 底部 tag + 箭頭
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(tagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(tagText,
                        color = tagFg,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = PaperWhite)
            }
        }
        // 插畫破框(右下)
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 0.dp)
                .size(150.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun HistorySection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("歷史紀錄",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f))
            Text("全部",
                color = BrandOrange,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.INTERVIEW_HISTORY)
                })
        }
        Spacer(Modifier.height(12.dp))
        MockData.interviewHistory.forEachIndexed { idx, r ->
            HistoryRow(r) { navController.navigate(Routes.INTERVIEW_REPORT) }
            if (idx < MockData.interviewHistory.size - 1) {
                SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun HistoryRow(r: InterviewRecord, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandYellow.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(r.type.label,
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(r.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray400)
            }
            Spacer(Modifier.height(4.dp))
            Text(r.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${r.score}",
                color = BrandOrange,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp)
            Spacer(Modifier.width(2.dp))
            Text("分",
                style = MaterialTheme.typography.labelSmall,
                color = InkGray500,
                modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

/* ===================== #6 Avatar 成長卡 ===================== */

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun AvatarGrowthCard() {
    val abilities = listOf(
        Triple("內容深度", 78, 4),
        Triple("邏輯清晰", 82, 2),
        Triple("表達流暢", 71, 6),
        Triple("互動", 68, 3),
        Triple("應變", 64, -1),
        Triple("自信", 80, 5),
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BrandPeach.copy(alpha = 0.4f))
            .padding(18.dp),
    ) {
        val power = 74
        val rank = rankOf(power)
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShieldBadge(rank.title)
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("面試力", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${rememberCountUp(power)}", color = InkBlack, fontSize = 44.sp, fontWeight = FontWeight.Black, lineHeight = 46.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(176.dp)) {
            HexRadar(
                values = abilities.map { it.second },
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 14.dp).size(156.dp),
            )
            // 河狸去框,破框站在卡緣
            Image(
                painter = painterResource(rank.beaver),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.align(Alignment.BottomEnd).size(132.dp).offset(x = 10.dp, y = 10.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            abilities.take(3).forEach { (l, v, d) -> StatCell(l, v, d) }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            abilities.drop(3).forEach { (l, v, d) -> StatCell(l, v, d) }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "上次練習 +6 ・ 距下一段位還差 26",
            color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        )
    }
}

private data class InterviewRank(val title: String, val beaver: Int)

private fun rankOf(score: Int): InterviewRank = when {
    score >= 85 -> InterviewRank("面試大師 I", R.drawable.beaver_trophy)
    score >= 75 -> InterviewRank("面試好手 II", R.drawable.beaver_celebrate)
    score >= 60 -> InterviewRank("面試新星 IV", R.drawable.beaver_flex)
    else -> InterviewRank("面試新手 V", R.drawable.beaver_climb)
}

@Composable
private fun ShieldBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(CutCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(BrandAmber)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text, color = InkCharcoal, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
    }
}

@Composable
private fun StatCell(label: String, value: Int, delta: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${rememberCountUp(value)}", color = InkBlack, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(3.dp))
            Text(
                if (delta >= 0) "+$delta" else "$delta",
                color = if (delta >= 0) AccentGreen else AccentRed,
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun HexRadar(values: List<Int>, modifier: Modifier = Modifier) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val progress by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = tween(900),
        label = "radarGrow",
    )
    Canvas(modifier = modifier) {
        val n = values.size
        if (n < 3) return@Canvas
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rMax = size.minDimension / 2f * 0.92f
        fun point(i: Int, r: Float): Offset {
            val ang = Math.toRadians(-90.0 + i * 360.0 / n)
            return Offset(cx + (r * cos(ang)).toFloat(), cy + (r * sin(ang)).toFloat())
        }
        // 網格(兩圈)+ 軸線
        listOf(0.5f, 1f).forEach { ring ->
            val grid = Path()
            for (i in 0 until n) {
                val pt = point(i, rMax * ring)
                if (i == 0) grid.moveTo(pt.x, pt.y) else grid.lineTo(pt.x, pt.y)
            }
            grid.close()
            drawPath(grid, color = PaperWhite.copy(alpha = 0.9f), style = Stroke(width = 1.2.dp.toPx()))
        }
        for (i in 0 until n) {
            drawLine(
                color = PaperWhite.copy(alpha = 0.7f),
                start = Offset(cx, cy),
                end = point(i, rMax),
                strokeWidth = 1.dp.toPx(),
            )
        }
        // 能力形狀
        val shape = Path()
        for (i in 0 until n) {
            val r = rMax * (values[i] / 100f) * progress
            val pt = point(i, r)
            if (i == 0) shape.moveTo(pt.x, pt.y) else shape.lineTo(pt.x, pt.y)
        }
        shape.close()
        drawPath(shape, color = BrandDeepOrange.copy(alpha = 0.30f))
        drawPath(shape, color = BrandDeepOrange, style = Stroke(width = 2.dp.toPx()))
        for (i in 0 until n) {
            val r = rMax * (values[i] / 100f) * progress
            drawCircle(color = BrandDeepOrange, radius = 3.dp.toPx(), center = point(i, r))
        }
    }
}

/* ===================== #1 快速練習(低門檻入口)===================== */

@Composable
private fun QuickPracticeCard(navController: NavHostController) {
    val pulse = rememberInfiniteTransition(label = "quickPulse")
    val glow by pulse.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "quickGlow",
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkBlack)
            .pressScale {
                navController.navigate(Routes.INTERVIEW_QUICK)
            }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(50.dp).clip(CircleShape)
                .background(BrandOrange.copy(alpha = glow)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Bolt, contentDescription = null,
                tint = PaperWhite, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("快速面試", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandOrange)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("60 秒一題", color = PaperWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                "免設定 · 抽一題就開始 · 答完馬上再來一題",
                color = PaperWhite.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 17.sp,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = PaperWhite.copy(alpha = 0.8f))
    }
}

