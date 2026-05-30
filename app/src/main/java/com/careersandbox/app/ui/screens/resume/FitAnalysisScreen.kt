package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class Capability(val label: String, val score: Int)

private data class FitTask(
    val id: String,
    val title: String,
    val subtitle: String,
    val tagText: String,
    var done: Boolean = false,
)

@Composable
fun FitAnalysisScreen(navController: NavHostController) {
    val capabilities = remember {
        listOf(
            Capability("數據能力", 96),
            Capability("溝通協作", 90),
            Capability("團隊合作", 76),
            Capability("領導力", 72),
            Capability("抗壓性", 65),
            Capability("創新思考", 58),
        )
    }
    var tasks by remember {
        mutableStateOf(
            listOf(
                FitTask("t1", "參加一場黑客松", "建議 4 月前完成", "創新 +12", false),
                FitTask("t2", "寫一段失敗復原經歷", "面試考古題出現率高", "抗壓 +8", false),
                FitTask("t3", "完成電商實習量化描述", "已完成 · 數據 +5", "", true),
            )
        )
    }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            FitHeroSection(onBack = { navController.popBackStack() })

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp)) {
                AnimatedSection(visible = visible, delayMs = 0) {
                    FitHeroJobCard()
                }
                Spacer(Modifier.height(20.dp))

                AnimatedSection(visible = visible, delayMs = 120) {
                    TabBar(tabs = listOf("能力分布", "補強路徑", "推薦行動"), activeIndex = 0)
                }
                Spacer(Modifier.height(20.dp))

                AnimatedSection(visible = visible, delayMs = 200) {
                    CapabilityRadar(capabilities = capabilities, animateProgress = visible)
                }
                Spacer(Modifier.height(20.dp))

                AnimatedSection(visible = visible, delayMs = 280) {
                    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        capabilities.forEach { cap ->
                            CapabilityRow(cap)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                AnimatedSection(visible = visible, delayMs = 320) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("補強路徑", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        val doneCount = tasks.count { it.done }
                        Text("$doneCount / ${tasks.size} 完成",
                            color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(12.dp))

                AnimatedSection(visible = visible, delayMs = 400) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        tasks.forEach { task ->
                            TaskCard(
                                task = task,
                                onToggle = {
                                    tasks = tasks.map {
                                        if (it.id == task.id) it.copy(done = !it.done) else it
                                    }
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                AnimatedSection(visible = visible, delayMs = 520) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InkBlack)
                            .pressScale { navController.navigate(Routes.EXPERIENCE_EDIT) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("開始補強", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedSection(visible: Boolean, delayMs: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing),
            ),
    ) {
        content()
    }
}

@Composable
private fun FitHeroSection(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandAmber, BrandOrange, BrandDeepOrange),
            ),
            heightDp = 240,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        // Back button (top-left)
        Box(
            Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.2f))
                .pressScale(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
        }

        // === undraw personal data illustration ===
        Image(
            painter = painterResource(R.drawable.undraw_personal_data_a1n8),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = 8.dp)
                .size(width = 175.dp, height = 110.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )

        // Title block
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp)
                .fillMaxWidth(0.55f),
        ) {
            Text("FIT ANALYSIS",
                color = PaperWhite.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("適配分析",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                lineHeight = 30.sp)
            Spacer(Modifier.height(7.dp))
            Text("看看你和目標職位的距離",
                color = PaperWhite.copy(alpha = 0.92f),
                fontSize = 12.sp)
        }
    }
}

@Composable
private fun FitHeroJobCard() {
    val animScore by animateIntAsState(
        targetValue = 78,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "hero_score",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(BrandDeepOrange, BrandAmber))),
                contentAlignment = Alignment.Center,
            ) {
                Text("A", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Junior PM", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Acer · 產品實習", color = InkGray500, fontSize = 12.sp)
            }
            Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = InkGray400, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("產品實習", "全職", "初級").forEach { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandPeach)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(chip, color = BrandDeepOrange, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("MATCH SCORE",
                    color = InkGray400,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$animScore", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 48.sp, lineHeight = 48.sp)
                    Text("%", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("數據導向型", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("月薪", color = InkGray500, fontSize = 11.sp)
                Spacer(Modifier.height(2.dp))
                Text("50-80k", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                Text("10/14 截止", color = InkGray400, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TabBar(tabs: List<String>, activeIndex: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { i, label ->
                val isActive = i == activeIndex
                Column(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .width(IntrinsicSize.Max),
                ) {
                    Text(
                        label,
                        color = if (isActive) InkBlack else InkGray400,
                        fontWeight = if (isActive) FontWeight.Black else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp),
                    )
                    if (isActive) {
                        Box(modifier = Modifier.height(2.5.dp).fillMaxWidth().background(BrandDeepOrange))
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
    }
}

/**
 * 能力雷達圖(計畫要求的「能力雷達圖」)
 * 6 軸對應 6 個能力,多邊形面積 = 能力輪廓。進場時從中心展開。
 */
@Composable
private fun CapabilityRadar(capabilities: List<Capability>, animateProgress: Boolean) {
    val n = capabilities.size
    val anim by animateFloatAsState(
        targetValue = if (animateProgress) 1f else 0f,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "radar",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.minDimension / 2f * 0.78f
            // 從正上方開始,順時針
            fun axisAngle(i: Int): Double = -Math.PI / 2 + 2 * Math.PI * i / n
            fun point(i: Int, r: Float): Offset {
                val a = axisAngle(i)
                return Offset(cx + (r * kotlin.math.cos(a)).toFloat(), cy + (r * kotlin.math.sin(a)).toFloat())
            }

            // 背景同心多邊形(4 圈)
            for (ring in 1..4) {
                val rr = radius * ring / 4f
                val path = androidx.compose.ui.graphics.Path()
                for (i in 0 until n) {
                    val p = point(i, rr)
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                path.close()
                drawPath(path, color = InkGray200, style = Stroke(width = 1f))
            }
            // 軸線
            for (i in 0 until n) {
                drawLine(InkGray200, start = Offset(cx, cy), end = point(i, radius), strokeWidth = 1f)
            }
            // 能力多邊形(動畫展開)
            val dataPath = androidx.compose.ui.graphics.Path()
            for (i in 0 until n) {
                val ratio = (capabilities[i].score / 100f) * anim
                val p = point(i, radius * ratio)
                if (i == 0) dataPath.moveTo(p.x, p.y) else dataPath.lineTo(p.x, p.y)
            }
            dataPath.close()
            drawPath(dataPath, color = BrandDeepOrange.copy(alpha = 0.18f))
            drawPath(dataPath, color = BrandDeepOrange, style = Stroke(width = 2f))
            // 頂點圓點
            for (i in 0 until n) {
                val ratio = (capabilities[i].score / 100f) * anim
                drawCircle(BrandDeepOrange, radius = 3f, center = point(i, radius * ratio))
            }
        }
        // 軸標籤(用 6 個定位的 Text 疊上去)
        capabilities.forEachIndexed { i, cap ->
            val a = -Math.PI / 2 + 2 * Math.PI * i / n
            val labelR = 0.5f  // 相對 fraction,用 offset 近似
            val dx = (kotlin.math.cos(a) * 118).toFloat()
            val dy = (kotlin.math.sin(a) * 118).toFloat()
            Text(
                cap.label,
                color = InkGray700,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.offset(x = dx.dp, y = dy.dp),
            )
        }
    }
}


    val (textColor, barColor) = when {
        cap.score >= 85 -> BrandDeepOrange to BrandDeepOrange
        cap.score >= 70 -> Color(0xFFBA7517) to BrandAmber
        else -> InkGray500 to InkGray300
    }
    val animScore by animateIntAsState(
        targetValue = cap.score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "${cap.label}-score",
    )
    val animProgress by animateFloatAsState(
        targetValue = cap.score / 100f,
        animationSpec = tween(1100, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "${cap.label}-prog",
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(cap.label, color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("$animScore", color = textColor, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Spacer(Modifier.height(5.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
            drawRoundRect(
                color = InkGray100,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(3.dp.toPx()),
            )
            val w = size.width * animProgress.coerceIn(0f, 1f)
            if (w > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, size.height),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun TaskCard(task: FitTask, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (task.done) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(BrandDeepOrange),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(14.dp))
            }
        } else {
            Canvas(modifier = Modifier.size(24.dp)) {
                drawCircle(
                    color = InkGray300,
                    radius = size.width / 2f - 1.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                color = if (task.done) InkGray500 else InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                task.subtitle,
                color = if (task.done) AccentGreen else InkGray500,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (!task.done && task.tagText.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandPeach)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Text(task.tagText, color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
