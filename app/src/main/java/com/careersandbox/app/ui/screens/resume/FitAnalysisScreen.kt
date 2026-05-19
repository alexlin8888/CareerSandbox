package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class Capability(
    val key: String,
    val label: String,
    val score: Int, // 0-100
)

private data class FitTask(
    val id: String,
    val tag: String,
    val tagDelta: String, // e.g. "創新 +12"
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
            Capability("lead", "領導", 72),
            Capability("team", "團隊", 76),
            Capability("inno", "創新", 58),
            Capability("stress", "抗壓", 65),
        )
    }
    val fingerprint = "數據導向型"
    val overallScore = 78

    var tasks by remember {
        mutableStateOf(
            listOf(
                FitTask(
                    "t1", "創新", "創新 +12",
                    "參加一場黑客松 / 設計衝刺",
                    "建議 4 月前完成,可補一段創新類經歷",
                    false,
                ),
                FitTask(
                    "t2", "抗壓", "抗壓 +8",
                    "寫一段失敗復原經歷",
                    "面試考古題顯示挫折題出現率高",
                    false,
                ),
                FitTask(
                    "t3", "數據", "數據 +5",
                    "完成電商實習量化描述",
                    "2 週前完成",
                    true,
                ),
            )
        )
    }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("適配分析", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    IconButton(onClick = { /* share */ }) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = InkGray500)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
        ) {
            // ===== 1. Target + Score =====
            TargetHeader(score = overallScore)

            Spacer(Modifier.height(8.dp))

            // ===== 2. Fingerprint =====
            FingerprintCard(
                capabilities = capabilities,
                fingerprintLabel = fingerprint,
            )

            Spacer(Modifier.height(14.dp))

            // ===== 3. Strength / Growth dual cards =====
            StrengthGrowthRow(capabilities)

            Spacer(Modifier.height(22.dp))

            // ===== 4. Section divider =====
            SectionDivider()

            // ===== 5. Tasks =====
            Spacer(Modifier.height(16.dp))
            TaskSection(
                tasks = tasks,
                onToggle = { id ->
                    tasks = tasks.map {
                        if (it.id == id) it.copy(done = !it.done) else it
                    }
                },
            )

            Spacer(Modifier.height(22.dp))
            SectionDivider()
            Spacer(Modifier.height(16.dp))

            // ===== 6. Sources =====
            SourcesRow(onEdit = { /* TODO */ })
        }
    }
}

// ============================================================
// Target Header
// ============================================================

@Composable
private fun TargetHeader(score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "TARGET",
                color = InkGray500,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Junior PM",
                color = InkBlack,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Acer · 產品實習",
                color = InkGray500,
                fontSize = 11.sp,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$score",
                    color = BrandDeepOrange,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 36.sp,
                )
                Text(
                    "%",
                    color = BrandDeepOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Text(
                "適配度",
                color = InkGray500,
                fontSize = 10.sp,
            )
        }
    }
}

// ============================================================
// Fingerprint Card
// ============================================================

@Composable
private fun FingerprintCard(
    capabilities: List<Capability>,
    fingerprintLabel: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = minOf(cx, cy) * 0.78f

            // 1. 3 圈虛線軌道
            listOf(0.4f, 0.7f, 1f).forEach { factor ->
                drawCircle(
                    color = InkGray200.copy(alpha = 0.5f),
                    radius = maxR * factor,
                    center = Offset(cx, cy),
                    style = Stroke(
                        width = 0.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f),
                    ),
                )
            }

            // 2. 計算 6 個能力的極座標點
            val points = capabilities.mapIndexed { idx, cap ->
                val angle = idx * (2 * PI / capabilities.size) - PI / 2
                val radius = maxR * (cap.score / 100f)
                Offset(
                    (cx + cos(angle) * radius).toFloat(),
                    (cy + sin(angle) * radius).toFloat(),
                )
            }

            // 3. 用平滑 cubic bezier 連接成有機形狀
            val path = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in points.indices) {
                        val cur = points[i]
                        val next = points[(i + 1) % points.size]
                        val prev = points[(i - 1 + points.size) % points.size]
                        val nextNext = points[(i + 2) % points.size]
                        // Catmull-Rom → Cubic Bezier conversion(tension 0.18)
                        val tension = 0.18f
                        val ctrl1 = Offset(
                            cur.x + (next.x - prev.x) * tension,
                            cur.y + (next.y - prev.y) * tension,
                        )
                        val ctrl2 = Offset(
                            next.x - (nextNext.x - cur.x) * tension,
                            next.y - (nextNext.y - cur.y) * tension,
                        )
                        cubicTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, next.x, next.y)
                    }
                    close()
                }
            }

            // 4. 填充半透明橘
            drawPath(
                path = path,
                color = BrandDeepOrange.copy(alpha = 0.22f),
            )
            // 5. 邊框實線橘
            drawPath(
                path = path,
                color = BrandDeepOrange,
                style = Stroke(width = 1.5.dp.toPx()),
            )

            // 6. 點(強項大,弱項小)
            capabilities.forEachIndexed { idx, cap ->
                val isStrong = cap.score >= 80
                val isMid = cap.score in 60..79
                val pointR = when {
                    isStrong -> 4.5f.dp.toPx()
                    isMid -> 3f.dp.toPx()
                    else -> 2.5f.dp.toPx()
                }
                val pointColor = when {
                    isStrong -> BrandDeepOrange
                    isMid -> BrandAmber
                    else -> BrandYellow
                }
                drawCircle(
                    color = pointColor,
                    radius = pointR,
                    center = points[idx],
                )
            }

            // 7. 外圍標籤
            val labelR = maxR * 1.18f
            capabilities.forEachIndexed { idx, cap ->
                val angle = idx * (2 * PI / capabilities.size) - PI / 2
                val lx = (cx + cos(angle) * labelR).toFloat()
                val ly = (cy + sin(angle) * labelR).toFloat()
                drawContext.canvas.nativeCanvas.let { canvas ->
                    val labelPaint = android.graphics.Paint().apply {
                        setColor(
                            if (cap.score >= 80)
                                android.graphics.Color.parseColor("#0B0E14")
                            else
                                android.graphics.Color.parseColor("#888780"),
                        )
                        textSize = 11.sp.toPx()
                        isFakeBoldText = cap.score >= 80
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText(cap.label, lx, ly, labelPaint)
                    val scorePaint = android.graphics.Paint().apply {
                        setColor(
                            if (cap.score >= 80)
                                android.graphics.Color.parseColor("#D85A30")
                            else
                                android.graphics.Color.parseColor("#888780"),
                        )
                        textSize = 9.sp.toPx()
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText("${cap.score}", lx, ly + 12.sp.toPx(), scorePaint)
                }
            }

            // 8. 中心 label
            drawContext.canvas.nativeCanvas.let { canvas ->
                val labelPaint = android.graphics.Paint().apply {
                    setColor(android.graphics.Color.parseColor("#888780"))
                    textSize = 9.sp.toPx()
                    letterSpacing = 0.15f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("FINGERPRINT", cx, cy - 4.sp.toPx(), labelPaint)

                val typePaint = android.graphics.Paint().apply {
                    setColor(android.graphics.Color.parseColor("#0B0E14"))
                    textSize = 12.sp.toPx()
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText(fingerprintLabel, cx, cy + 10.sp.toPx(), typePaint)
            }
        }
    }
}

// ============================================================
// Strength / Growth Row
// ============================================================

@Composable
private fun StrengthGrowthRow(capabilities: List<Capability>) {
    val sorted = capabilities.sortedByDescending { it.score }
    val top2 = sorted.take(2)
    val bottom2 = sorted.takeLast(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Strength
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandDeepOrange.copy(alpha = 0.08f))
                .padding(12.dp),
        ) {
            Text(
                "STRENGTH",
                color = Color(0xFF993C1D),
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                top2.joinToString(" · ") { it.label },
                color = InkBlack,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        // Growth
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandDeepOrange.copy(alpha = 0.04f))
                .padding(12.dp),
        ) {
            Text(
                "GROWTH",
                color = InkGray500,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                bottom2.joinToString(" · ") { it.label },
                color = InkBlack,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ============================================================
// Section Divider
// ============================================================

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(InkGray100.copy(alpha = 0.5f)),
    )
}

// ============================================================
// Task Section
// ============================================================

@Composable
private fun TaskSection(
    tasks: List<FitTask>,
    onToggle: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "補強路徑",
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                "${tasks.size} 項任務",
                color = InkGray500,
                fontSize = 10.sp,
            )
        }
        Spacer(Modifier.height(10.dp))
        tasks.forEach { task ->
            FitTaskCard(
                task = task,
                onToggle = { onToggle(task.id) },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FitTaskCard(
    task: FitTask,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PaperWhite)
            .pressScale(onClick = onToggle)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (task.done) BrandDeepOrange else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            if (task.done) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(14.dp),
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = InkGray300,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx()),
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (!task.done) {
                // Tag chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrandPeach.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        task.tagDelta,
                        color = Color(0xFF993C1D),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
            Text(
                task.title,
                color = if (task.done) InkGray500 else InkBlack,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (task.done)
                    androidx.compose.ui.text.style.TextDecoration.LineThrough
                else null,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                task.description,
                color = if (task.done) Color(0xFFB4B2A9) else InkGray500,
                fontSize = 10.sp,
                lineHeight = 15.sp,
            )
        }
    }
}

// ============================================================
// Sources Row
// ============================================================

@Composable
private fun SourcesRow(onEdit: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "資料來源",
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                "編輯 ↗",
                color = BrandDeepOrange,
                fontSize = 10.sp,
                modifier = Modifier.pressScale(onClick = onEdit),
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SourceCard(
                icon = Icons.Outlined.Folder,
                value = "4 段",
                label = "經歷",
                modifier = Modifier.weight(1f),
            )
            SourceCard(
                icon = Icons.Outlined.School,
                value = "42 門",
                label = "修課",
                modifier = Modifier.weight(1f),
            )
            SourceCard(
                icon = Icons.Outlined.GpsFixed,
                value = "1 個",
                label = "目標",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SourceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperWhite)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = BrandDeepOrange,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = InkBlack,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            label,
            color = InkGray500,
            fontSize = 9.sp,
        )
    }
}
