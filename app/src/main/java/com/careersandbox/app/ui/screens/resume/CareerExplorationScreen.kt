package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class JobRec(
    val title: String,
    val titleEn: String,
    val match: Int,
    val gap: String,
    val salaryRange: String,
    val openings: Int,
)

private data class LearningStep(
    val phase: String,
    val title: String,
    val source: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerExplorationScreen(navController: NavHostController) {
    val topRec = remember {
        JobRec("資料分析師", "Data Analyst", 92, "SQL 進階 · A/B test", "45–65k", 1240)
    }
    val otherRecs = remember {
        listOf(
            JobRec("產品經理", "PM", 78, "用戶研究", "50–80k", 980),
            JobRec("行銷策略", "Growth", 71, "品牌經營", "42–58k", 760),
        )
    }
    val learningSteps = remember {
        listOf(
            LearningStep("本學期", "資料庫管理", "商管院 · 3 學分"),
            LearningStep("下學期", "GDA 證照", "Coursera · 6 月"),
            LearningStep("暑假", "外商 BI 實習", "補用戶研究"),
        )
    }
    val suggestedKeywords = listOf("UX 設計師", "數位行銷", "創投分析")

    var searchInput by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = PaperWarm,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("職涯探索", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = InkGray500)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = null, tint = InkBlack, modifier = Modifier.size(18.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = InkGray500, modifier = Modifier.size(18.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PaperWarm),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 32.dp),
        ) {
            // ===== 1. Banner =====
            AnimatedSection(visible, 0) {
                IntroBanner()
            }

            Spacer(Modifier.height(12.dp))

            // ===== 2. Top match card =====
            AnimatedSection(visible, 120) {
                TopMatchCard(topRec)
            }

            Spacer(Modifier.height(8.dp))

            // ===== 3. 2/3 名橫排 =====
            AnimatedSection(visible, 240) {
                OtherRecsRow(otherRecs)
            }

            Spacer(Modifier.height(14.dp))

            // ===== 4. Search row =====
            AnimatedSection(visible, 360) {
                SearchRow(
                    value = searchInput,
                    onChange = { searchInput = it },
                    suggestions = suggestedKeywords,
                    onSuggestionClick = { searchInput = it },
                )
            }

            Spacer(Modifier.height(18.dp))

            // ===== 5. Learning path =====
            AnimatedSection(visible, 480) {
                LearningPathSection(
                    targetTitle = topRec.title,
                    steps = learningSteps,
                )
            }
        }
    }
}

// (shared with FitAnalysis — 但避免衝突重新定義)
@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400, delayMillis = delayMs)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(400, delayMillis = delayMs, easing = FastOutSlowInEasing),
            ),
    ) {
        content()
    }
}

// ============================================================
// Banner
// ============================================================

@Composable
private fun IntroBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFCEFD9), PaperWarm),
                ),
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 插畫 — sparkle
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            SparkleFigure()
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "3 條最適合你的路徑",
                color = InkBlack,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "基於 4 段經歷 · 42 門修課",
                color = Color(0xFF993C1D),
                fontSize = 9.sp,
            )
        }
    }
}

// ============================================================
// Top match card with count-up + animated bar
// ============================================================

@Composable
private fun TopMatchCard(rec: JobRec) {
    var triggered by remember { mutableStateOf(false) }
    val animatedMatch by animateIntAsState(
        targetValue = if (triggered) rec.match else 0,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "match",
    )
    val animatedProgress by animateFloatAsState(
        targetValue = if (triggered) rec.match / 100f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "bar",
    )
    LaunchedEffect(Unit) { triggered = true }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(PaperWhite)
                .border(1.5.dp, BrandDeepOrange, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        rec.title,
                        color = InkBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        rec.titleEn,
                        color = InkGray500,
                        fontSize = 10.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Text("月薪 ", color = InkGray500, fontSize = 10.sp)
                        Text(
                            rec.salaryRange,
                            color = InkBlack,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("職缺 ", color = InkGray500, fontSize = 10.sp)
                        Text(
                            "${rec.openings}",
                            color = InkBlack,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "$animatedMatch",
                        color = BrandDeepOrange,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 36.sp,
                    )
                    Text(
                        "%",
                        color = BrandDeepOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(InkGray100),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(BrandAmber, BrandDeepOrange),
                            ),
                        ),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("缺 :", color = InkGray500, fontSize = 9.sp)
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(InkGray100)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("SQL 進階", color = InkBlack, fontSize = 9.sp)
                }
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(InkGray100)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("A/B test", color = InkBlack, fontSize = 9.sp)
                }
            }
        }
        // TOP MATCH 標籤
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .offset(y = (-7).dp)
                .clip(CircleShape)
                .background(BrandDeepOrange)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                "TOP MATCH",
                color = PaperWhite,
                fontSize = 8.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

// ============================================================
// Other recs row
// ============================================================

@Composable
private fun OtherRecsRow(recs: List<JobRec>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        recs.forEachIndexed { idx, rec ->
            val color = if (idx == 0) BrandAmber else BrandYellow
            OtherRecCard(rec = rec, color = color, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun OtherRecCard(
    rec: JobRec,
    color: Color,
    modifier: Modifier = Modifier,
) {
    var triggered by remember { mutableStateOf(false) }
    val animatedMatch by animateIntAsState(
        targetValue = if (triggered) rec.match else 0,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "om-${rec.title}",
    )
    val animatedProgress by animateFloatAsState(
        targetValue = if (triggered) rec.match / 100f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ob-${rec.title}",
    )
    LaunchedEffect(Unit) { triggered = true }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite)
            .border(0.5.dp, InkGray200.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    rec.title,
                    color = InkBlack,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    rec.titleEn,
                    color = InkGray500,
                    fontSize = 9.sp,
                )
            }
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    "$animatedMatch",
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 18.sp,
                )
                Text(
                    "%",
                    color = color,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(CircleShape)
                .background(InkGray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "${rec.salaryRange} · 缺 ${rec.gap}",
            color = InkGray500,
            fontSize = 9.sp,
        )
    }
}

// ============================================================
// Search row
// ============================================================

@Composable
private fun SearchRow(
    value: String,
    onChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite)
            .border(0.5.dp, InkGray200.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = InkGray500,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    "搜尋其他職位...",
                    color = InkGray400,
                    fontSize = 11.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                textStyle = TextStyle(color = InkBlack, fontSize = 11.sp),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        suggestions.take(2).forEach { kw ->
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(InkGray100)
                    .pressScale(onClick = { onSuggestionClick(kw) })
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    kw,
                    color = InkBlack,
                    fontSize = 9.sp,
                )
            }
        }
    }
}

// ============================================================
// Learning Path (3 mini cards horizontal)
// ============================================================

@Composable
private fun LearningPathSection(
    targetTitle: String,
    steps: List<LearningStep>,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "給「$targetTitle」的學習路徑",
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "展開 ›",
                color = BrandDeepOrange,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.pressScale(onClick = { }),
            )
        }
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            steps.forEachIndexed { idx, step ->
                val color = when (idx) {
                    0 -> BrandDeepOrange
                    1 -> BrandAmber
                    else -> BrandYellow
                }
                val textOnColor = if (idx >= 2) Color(0xFF993C1D) else PaperWhite
                // 不對稱微旋轉
                val rotation = when (idx) {
                    0 -> -0.5f
                    1 -> 0.4f
                    else -> -0.3f
                }
                LearningStepCard(
                    idx = idx,
                    step = step,
                    color = color,
                    textOnColor = textOnColor,
                    rotation = rotation,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun LearningStepCard(
    idx: Int,
    step: LearningStep,
    color: Color,
    textOnColor: Color,
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .rotate(rotation)
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(PaperWhite)
            .border(0.5.dp, InkGray200.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "${idx + 1}",
                color = textOnColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            step.phase,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            step.title,
            color = InkBlack,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 13.sp,
        )
        Spacer(Modifier.height(1.dp))
        Text(
            step.source,
            color = InkGray500,
            fontSize = 9.sp,
            lineHeight = 11.sp,
        )
    }
}

// ============================================================
// Sparkle illustration
// ============================================================

@Composable
private fun SparkleFigure() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // 4-pointer star main
        val mainSize = w * 0.45f
        drawStar4(cx, cy, mainSize, BrandDeepOrange)

        // 2 個小 sparkles
        drawStar4(cx - w * 0.32f, cy - h * 0.28f, w * 0.18f, BrandAmber)
        drawStar4(cx + w * 0.3f, cy + h * 0.28f, w * 0.15f, BrandYellow)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar4(
    cx: Float,
    cy: Float,
    size: Float,
    color: Color,
) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(cx, cy - size)
        cubicTo(cx + size * 0.2f, cy - size * 0.2f, cx + size * 0.2f, cy - size * 0.2f, cx + size, cy)
        cubicTo(cx + size * 0.2f, cy + size * 0.2f, cx + size * 0.2f, cy + size * 0.2f, cx, cy + size)
        cubicTo(cx - size * 0.2f, cy + size * 0.2f, cx - size * 0.2f, cy + size * 0.2f, cx - size, cy)
        cubicTo(cx - size * 0.2f, cy - size * 0.2f, cx - size * 0.2f, cy - size * 0.2f, cx, cy - size)
        close()
    }
    drawPath(path, color)
}
