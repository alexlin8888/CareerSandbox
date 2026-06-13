package com.careersandbox.app.ui.screens.resume

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class Capability(
    val label: String,
    val score: Int,
    val kind: String = "軟實力",
    val basis: String = "",
    val howTo: String = "",
)

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
            Capability(
                "數據能力", 96, "硬實力",
                "從你修過的資料庫、統計課,加上 2 段含量化成果的經歷推估。",
                "已經很強,面試時直接用具體數字佐證即可。",
            ),
            Capability(
                "溝通協作", 90, "軟實力",
                "從社團幹部、簡報、跨組專案的敘述推估。",
                "維持就好,可補一句處理意見衝突的例子讓它更立體。",
            ),
            Capability(
                "團隊合作", 76, "軟實力",
                "從 3 段團隊經歷推估,但多半是執行角色。",
                "找一次主導或協調角色的經歷寫進去,分數會更有說服力。",
            ),
            Capability(
                "領導力", 72, "軟實力",
                "從幹部經歷推估,但缺帶人、帶專案的量化結果。",
                "寫一段你帶領 N 個人完成某件事、結果如何的 STAR 敘述。",
            ),
            Capability(
                "抗壓性", 65, "軟實力",
                "面試常考的面向;你的履歷目前少有面對挫折的敘述。",
                "補一段「遇到挫折 → 怎麼處理 → 結果」的經歷,這項會明顯拉高。",
            ),
            Capability(
                "創新思考", 58, "軟實力",
                "從專案的新穎性推估,目前偏少。",
                "參加一次黑客松或提案活動,留下一個可以寫的成果。",
            ),
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

                var activeTab by remember { mutableStateOf(0) }
                AnimatedSection(visible = visible, delayMs = 120) {
                    TabBar(
                        tabs = listOf("能力分布", "補強路徑", "推薦行動"),
                        activeIndex = activeTab,
                        onTabSelected = { activeTab = it },
                    )
                }
                Spacer(Modifier.height(20.dp))

                when (activeTab) {
                    0 -> {
                        // === #20 技能差距(文氏圖,強調差集)===
                        SkillGapSection()
                        Spacer(Modifier.height(20.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray200))
                        Spacer(Modifier.height(20.dp))

                        // === 能力分布:雷達 + bar ===
                        Text("能力輪廓", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Spacer(Modifier.height(12.dp))
                        CapabilityRadar(capabilities = capabilities, animateProgress = visible)
                        Spacer(Modifier.height(20.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            capabilities.forEach { cap -> CapabilityRow(cap) }
                        }
                        Spacer(Modifier.height(14.dp))
                        // === #12 分數怎麼算的解釋 ===
                        ScoreExplainerCard()
                        Spacer(Modifier.height(28.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
                                .background(InkBlack).pressScale { activeTab = 1 },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("看補強路徑", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    1 -> {
                        // === 補強路徑:任務清單 ===
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
                        Spacer(Modifier.height(12.dp))
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
                        Spacer(Modifier.height(28.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
                                .background(InkBlack).pressScale { navController.navigate(Routes.EXPERIENCE_EDIT) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("開始補強", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    else -> {
                        // === 推薦行動 ===
                        RecommendedActions(navController)
                    }
                }
            }
        }
    }
}

/** 推薦行動:依能力缺口給的具體下一步 */
@Composable
private fun RecommendedActions(navController: NavHostController) {
    data class ActionItem(val icon: ImageVector, val title: String, val desc: String, val cta: String, val route: String)
    val actions = listOf(
        ActionItem(Icons.Outlined.Edit, "補上量化數字", "你的經歷少了具體成果數字,recruiter 最在意這個", "去編輯經歷", Routes.EXPERIENCE_LIST),
        ActionItem(Icons.Outlined.Explore, "看相近的職位", "根據你的能力輪廓,還有幾個適合的方向", "探索職涯", Routes.CAREER_EXPLORATION),
        ActionItem(Icons.Outlined.Description, "為這份 JD 客製履歷", "把母版調整成更貼近這個職缺的版本", "開始客製", Routes.JD_CUSTOMIZE),
    )
    Text("推薦行動", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
    Spacer(Modifier.height(2.dp))
    Text("根據你的能力缺口,這是接下來最值得做的", color = InkGray500, fontSize = 12.sp)
    Spacer(Modifier.height(14.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        actions.forEach { a ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(PaperWhite).pressScale { navController.navigate(a.route) }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(BrandPeach.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(a.icon, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(a.title, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(a.desc, color = InkGray500, fontSize = 11.sp, lineHeight = 15.sp)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400, modifier = Modifier.size(18.dp))
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

        // === 河狸插畫(還缺什麼)===
        Image(
            painter = painterResource(R.drawable.beaver_confused),
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
        targetValue = 82,
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
private fun TabBar(tabs: List<String>, activeIndex: Int, onTabSelected: (Int) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { i, label ->
                val isActive = i == activeIndex
                Column(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .width(IntrinsicSize.Max)
                        .pressScale { onTabSelected(i) },
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

@Composable
private fun CapabilityRow(cap: Capability) {
    var expanded by remember { mutableStateOf(false) }
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
    Column(modifier = Modifier.fillMaxWidth().pressScale { expanded = !expanded }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(cap.label, color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$animScore", color = textColor, fontWeight = FontWeight.Black, fontSize = 13.sp)
                if (cap.howTo.isNotEmpty()) {
                    Spacer(Modifier.width(5.dp))
                    Icon(
                        if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "說明",
                        tint = InkGray400,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
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
        if (expanded && cap.howTo.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(InkGray100.copy(alpha = 0.5f))
                    .padding(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (cap.kind == "硬實力") BrandPeach else InkGray200)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        cap.kind,
                        color = if (cap.kind == "硬實力") BrandDeepOrange else InkGray700,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("怎麼算", color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(cap.basis, color = InkGray700, fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text("怎麼提升", color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(cap.howTo, color = InkBlack, fontSize = 12.sp, lineHeight = 18.sp)
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

/* ===================== #20 技能差距(文氏圖差集)===================== */

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SkillGapSection() {
    val userHas = MockData.currentUser.skillsHave
    // 這份 JD(產品經理)要求的技能
    val roleRequires = listOf("SQL", "使用者訪談", "A/B 測試", "Figma", "Python", "GA4")
    val gap = roleRequires.filter { it !in userHas }      // 差集:要、但你還沒有
    val matched = roleRequires.filter { it in userHas }   // 交集:要、你有
    val other = userHas.filter { it !in roleRequires }    // 你有、但這份 JD 沒要求

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(18.dp),
    ) {
        Text("技能差距", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            "重點不是你已符合多少,而是你還缺什麼。",
            color = InkGray500, fontSize = 13.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(16.dp))
        LedgerHeader(matchedCount = matched.size, gapCount = gap.size)
        Spacer(Modifier.height(16.dp))

        // 還缺:差集是主角,直接全展開
        var heroIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { heroIn = true }
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulseDot()
            Spacer(Modifier.width(8.dp))
            Text("還缺 ${gap.size} 項", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Text("先補這裡", color = InkGray400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            gap.forEachIndexed { gi, gSkill ->
                val rv by animateFloatAsState(
                    targetValue = if (heroIn) 1f else 0f,
                    animationSpec = tween(380, delayMillis = gi * 110, easing = FastOutSlowInEasing),
                    label = "heroRow$gi",
                )
                Box(
                    Modifier
                        .alpha(rv)
                        .offset(y = ((1f - rv) * 14).dp),
                ) { GapDetailCard(gSkill) }
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
            Spacer(Modifier.width(8.dp))
            Text("已符合 ${matched.size} 項", color = InkGray700, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            matched.forEach { c ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(InkGray100)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("✓ $c", color = InkGray700, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        GapGroup(dot = InkGray400, title = "你有、但這份 JD 沒特別要求", chips = other, emphasized = false)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun GapGroup(dot: Color, title: String, chips: List<String>, emphasized: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                color = if (emphasized) BrandDeepOrange else InkGray700,
                fontWeight = if (emphasized) FontWeight.Black else FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        if (chips.isEmpty()) {
            Text("(無)", color = InkGray400, fontSize = 12.sp)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                chips.forEach { c ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (emphasized) BrandPeach else InkGray100)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            c,
                            color = if (emphasized) BrandDeepOrange else InkGray700,
                            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

private data class GapDetail(val jdLine: String, val howTo: String)

private val gapDetails = mapOf(
    "SQL" to GapDetail("「需熟悉 SQL,能自行撈取數據」", "線上課約 12 小時,再拿一份公開資料寫 10 個查詢練手。"),
    "A/B 測試" to GapDetail("「規劃並執行 A/B 測試,以數據驗證假設」", "把社團貼文做成兩版小實驗,寫一頁結果報告。"),
    "Figma" to GapDetail("「能用 Figma 與設計師協作」", "臨摹一個你喜歡的 App 三個畫面,練熟 auto-layout。"),
    "Python" to GapDetail("「會 Python 資料處理者佳」", "用 pandas 重做一次你在 Excel 上做過的分析。"),
    "GA4" to GapDetail("「熟 GA4 事件與漏斗分析」", "幫社團網站掛上 GA4,跑出一份兩週流量報告。"),
    "使用者訪談" to GapDetail("「具使用者訪談與需求彙整能力」", "訪談 3 位同學的求職流程,整理成痛點清單。"),
)

@Composable
private fun GapDetailCard(skill: String) {
    var open by remember(skill) { mutableStateOf(false) }
    val detail = gapDetails[skill]
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandPeach.copy(alpha = 0.35f))
            .pressScale { open = !open }
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(skill, color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Text(
                if (open) "收合" else "怎麼補",
                color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                if (open) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray400, modifier = Modifier.size(16.dp),
            )
        }
        if (open && detail != null) {
            Spacer(Modifier.height(10.dp))
            Text("JD 原句", color = InkGray400, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(Modifier.height(3.dp))
            Text(detail.jdLine, color = InkGray700, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("怎麼補", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(Modifier.height(3.dp))
            Text(detail.howTo, color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
        }
    }
}

/* ===================== #12 分數怎麼算 ===================== */

@Composable
private fun ScoreExplainerCard() {
    var open by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale { open = !open }
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(18.dp).clip(CircleShape).background(InkGray200),
                contentAlignment = Alignment.Center,
            ) {
                Text("?", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "這些分數怎麼來的?",
                color = InkGray700, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (open) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray500, modifier = Modifier.size(18.dp),
            )
        }
        if (open) {
            Spacer(Modifier.height(10.dp))
            Text(
                "這些是「軟實力」分數,不是考試成績,是從你的經歷敘述、修課、社團活動推估出來的。",
                color = InkGray700, fontSize = 12.sp, lineHeight = 19.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "硬實力(像 SQL、Python)直接看履歷上的關鍵字命中,比較直覺;軟實力(像抗壓性、領導力)比較抽象,分數低通常不代表你不行,而是履歷上還沒有足夠的證據。",
                color = InkGray700, fontSize = 12.sp, lineHeight = 19.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "點上面任一項能力,看它是怎麼算的、以及怎麼補。",
                color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun LedgerHeader(matchedCount: Int, gapCount: Int) {
    val total = matchedCount + gapCount
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val animMatched by animateIntAsState(
        targetValue = if (appear) matchedCount else 0,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "ledMatched",
    )
    val prog by animateFloatAsState(
        targetValue = if (appear && total > 0) matchedCount.toFloat() / total else 0f,
        animationSpec = tween(1000, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "ledProg",
    )
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("符合", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 5.dp))
            Spacer(Modifier.width(8.dp))
            Text("$animMatched", color = InkBlack, fontWeight = FontWeight.Black,
                fontSize = 34.sp, lineHeight = 34.sp)
            Text(" / $total 項", color = InkGray400, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp))
            Spacer(Modifier.weight(1f))
            Text("${(prog * 100).toInt()}%", color = BrandDeepOrange,
                fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(8.dp)
                .clip(RoundedCornerShape(50)).background(InkGray100),
        ) {
            Box(
                Modifier.fillMaxWidth(prog.coerceAtLeast(0.02f)).fillMaxHeight()
                    .clip(RoundedCornerShape(50)).background(BrandOrange),
            )
        }
    }
}

@Composable
private fun PulseDot() {
    val t = rememberInfiniteTransition(label = "pulseDot")
    val sc by t.animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "pd",
    )
    Box(
        Modifier.size(10.dp)
            .graphicsLayer { scaleX = sc; scaleY = sc }
            .clip(CircleShape).background(BrandDeepOrange),
    )
}
