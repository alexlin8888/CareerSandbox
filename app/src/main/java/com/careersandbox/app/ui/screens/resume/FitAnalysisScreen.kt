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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

private data class Capability(
    val key: String,
    val label: String,
    val score: Int,
)

private data class FitTask(
    val id: String,
    val tagDelta: String,
    val title: String,
    val description: String,
    var done: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitAnalysisScreen(navController: NavHostController) {
    val capabilities = remember {
        listOf(
            Capability("data", "數據", 96),
            Capability("comm", "溝通", 90),
            Capability("team", "團隊", 76),
            Capability("lead", "領導", 72),
            Capability("stress", "抗壓", 65),
            Capability("inno", "創新", 58),
        )
    }
    val overallScore = 78

    var tasks by remember {
        mutableStateOf(
            listOf(
                FitTask("t1", "創新 +12", "參加一場黑客松", "建議 4 月前完成", false),
                FitTask("t2", "抗壓 +8", "寫一段失敗復原經歷", "面試考古題出現率高", false),
                FitTask("t3", "數據 +5", "完成電商實習量化描述", "2 週前完成", true),
            )
        )
    }

    // 進場動畫
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = PaperWarm,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("適配分析", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = InkBlack)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = null, tint = InkBlack, modifier = Modifier.size(18.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = InkGray500, modifier = Modifier.size(18.dp))
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
            // ===== 1. Hero row (Match + Strength + Growth) =====
            AnimatedSection(visible = visible, delayMs = 0) {
                HeroRow(score = overallScore, capabilities = capabilities)
            }

            Spacer(Modifier.height(12.dp))

            // ===== 2. Capabilities grid 2x3 =====
            AnimatedSection(visible = visible, delayMs = 120) {
                CapabilitiesGrid(capabilities)
            }

            Spacer(Modifier.height(20.dp))

            // ===== 3. Tasks with illustration =====
            AnimatedSection(visible = visible, delayMs = 240) {
                TasksSection(
                    tasks = tasks,
                    onToggle = { id ->
                        tasks = tasks.map { if (it.id == id) it.copy(done = !it.done) else it }
                    },
                )
            }

            Spacer(Modifier.height(20.dp))

            // ===== 4. Footer sources =====
            AnimatedSection(visible = visible, delayMs = 360) {
                FooterSourcesRow()
            }
        }
    }
}

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
// Hero row — left big match + right strength/growth
// ============================================================

@Composable
private fun HeroRow(score: Int, capabilities: List<Capability>) {
    val sorted = capabilities.sortedByDescending { it.score }
    val top2 = sorted.take(2)
    val bottom2 = sorted.takeLast(2)

    // Count-up 動畫 (M1)
    var triggered by remember { mutableStateOf(false) }
    val animatedScore by animateIntAsState(
        targetValue = if (triggered) score else 0,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "score",
    )
    LaunchedEffect(Unit) { triggered = true }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 左:大適配度卡
        Box(
            modifier = Modifier
                .weight(1.4f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFCEFD9), Color(0xFFFDDDB8)),
                    ),
                )
                .padding(16.dp),
        ) {
            Column {
                Text(
                    "MATCH",
                    color = Color(0xFF993C1D),
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$animatedScore",
                        color = InkBlack,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 48.sp,
                    )
                    Text(
                        "%",
                        color = Color(0xFF993C1D),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Junior PM",
                    color = InkBlack,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Acer · 產品實習",
                    color = InkGray500,
                    fontSize = 11.sp,
                )
            }
        }

        // 右:Strength / Growth 雙小卡(直立)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MiniStatCard(
                label = "STRENGTH",
                title = top2.joinToString(" · ") { it.label },
                value = top2.joinToString(" / ") { "${it.score}" },
                modifier = Modifier.weight(1f),
            )
            MiniStatCard(
                label = "GROWTH",
                title = bottom2.joinToString(" · ") { it.label },
                value = bottom2.joinToString(" / ") { "${it.score}" },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            color = InkGray500,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            title,
            color = InkBlack,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            value,
            color = InkGray500,
            fontSize = 11.sp,
        )
    }
}

// ============================================================
// Capabilities grid 2x3
// ============================================================

@Composable
private fun CapabilitiesGrid(capabilities: List<Capability>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PaperWhite)
            .border(0.5.dp, InkGray200.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "能力分布",
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentGreen.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    "數據導向型",
                    color = AccentGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        // 2 列 x 3 列
        val rows = capabilities.chunked(2)
        rows.forEachIndexed { rowIdx, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                row.forEach { cap ->
                    CapabilityRowItem(
                        cap = cap,
                        modifier = Modifier.weight(1f),
                    )
                }
                // 填滿(奇數時)
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
            if (rowIdx < rows.size - 1) Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CapabilityRowItem(cap: Capability, modifier: Modifier = Modifier) {
    val isStrong = cap.score >= 75
    val isWeak = cap.score < 70
    val barColor = when {
        cap.score >= 85 -> BrandDeepOrange
        isStrong -> BrandAmber
        else -> InkGray300
    }
    val textColor = if (isWeak) InkGray500 else InkBlack

    // 進場時 bar 填充動畫 (M2)
    var triggered by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (triggered) cap.score / 100f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "bar-${cap.key}",
    )
    LaunchedEffect(Unit) { triggered = true }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            cap.label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(36.dp),
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(CircleShape)
                .background(InkGray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(barColor),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${cap.score}",
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp),
        )
    }
}

// ============================================================
// Tasks section with illustration
// ============================================================

@Composable
private fun TasksSection(
    tasks: List<FitTask>,
    onToggle: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "補強路徑",
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            val doneCount = tasks.count { it.done }
            Text(
                "$doneCount / ${tasks.size} 完成",
                color = InkGray500,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(10.dp))

        tasks.forEachIndexed { idx, task ->
            // 不對稱微旋轉 (技巧 5)
            val rotation = when (idx % 3) {
                0 -> -0.6f
                1 -> 0.8f
                else -> -0.3f
            }
            TaskCard(
                task = task,
                rotation = if (!task.done) rotation else 0f,
                onToggle = { onToggle(task.id) },
            )
            if (idx < tasks.size - 1) Spacer(Modifier.height(6.dp))
        }

        // 插畫(技巧 6)— 任務區底下放小人 illustration
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            ReadingFigure(modifier = Modifier.size(80.dp))
        }
        Text(
            "完成補強,讓你的指紋更接近目標",
            modifier = Modifier.fillMaxWidth(),
            color = InkGray500,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun TaskCard(
    task: FitTask,
    rotation: Float,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(rotation)
            .shadow(
                elevation = if (!task.done) 2.dp else 0.dp,
                shape = RoundedCornerShape(14.dp),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(if (task.done) InkGray100.copy(alpha = 0.6f) else PaperWhite)
            .border(0.5.dp, InkGray200.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .pressScale(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (task.done) BrandDeepOrange else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            if (task.done) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(12.dp),
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = InkGray300,
                        cornerRadius = CornerRadius(6.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx()),
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                color = if (task.done) InkGray500 else InkBlack,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (task.done) TextDecoration.LineThrough else null,
            )
            if (!task.done) {
                Spacer(Modifier.height(1.dp))
                Text(
                    task.description,
                    color = InkGray500,
                    fontSize = 13.sp,
                )
            }
        }
        if (!task.done) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(BrandPeach.copy(alpha = 0.6f))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            ) {
                Text(
                    task.tagDelta,
                    color = Color(0xFF993C1D),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ============================================================
// Footer sources row
// ============================================================

@Composable
private fun FooterSourcesRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SourceChip(icon = Icons.Outlined.Folder, value = "4", label = "經歷")
        Spacer(Modifier.width(12.dp))
        SourceChip(icon = Icons.Outlined.School, value = "42", label = "修課")
        Spacer(Modifier.width(12.dp))
        SourceChip(icon = Icons.Outlined.GpsFixed, value = "1", label = "目標")
        Spacer(Modifier.weight(1f))
        Text(
            "編輯 ›",
            color = BrandDeepOrange,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.pressScale(onClick = { }),
        )
    }
}

@Composable
private fun SourceChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = InkGray500, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            "$value $label",
            color = InkGray500,
            fontSize = 13.sp,
        )
    }
}

// ============================================================
// Illustration — 簡易 vector 小人 (Reading)
// ============================================================

@Composable
private fun ReadingFigure(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // body — 漸層橘
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(BrandAmber, BrandDeepOrange),
                start = Offset(cx - w * 0.2f, h * 0.4f),
                end = Offset(cx + w * 0.2f, h * 0.95f),
            ),
            topLeft = Offset(cx - w * 0.18f, h * 0.4f),
            size = Size(w * 0.36f, h * 0.45f),
            cornerRadius = CornerRadius(w * 0.05f),
        )

        // head
        drawCircle(
            color = Color(0xFFFAC775),
            radius = w * 0.13f,
            center = Offset(cx, h * 0.35f),
        )

        // book(舉在前面)
        drawRoundRect(
            color = PaperWhite,
            topLeft = Offset(cx - w * 0.22f, h * 0.55f),
            size = Size(w * 0.44f, h * 0.18f),
            cornerRadius = CornerRadius(w * 0.02f),
        )
        // book lines
        val lineY = h * 0.62f
        drawLine(
            color = InkGray300,
            start = Offset(cx - w * 0.16f, lineY),
            end = Offset(cx + w * 0.16f, lineY),
            strokeWidth = 1f,
        )
        drawLine(
            color = InkGray300,
            start = Offset(cx - w * 0.16f, lineY + h * 0.04f),
            end = Offset(cx + w * 0.08f, lineY + h * 0.04f),
            strokeWidth = 1f,
        )

        // book center spine
        drawLine(
            color = InkGray400,
            start = Offset(cx, h * 0.55f),
            end = Offset(cx, h * 0.73f),
            strokeWidth = 0.5f,
        )
    }
}
