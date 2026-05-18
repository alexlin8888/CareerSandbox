package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
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

/**
 * 適配分析 (Batch 28)
 * - mobile 化的 talent dashboard
 * - 花瓣圖(8 花瓣,4 色分群)+ 推薦行動 checklist + 技能進度
 */

private data class PetalSkill(
    val label: String,
    val score: Int, // 0-100
    val cluster: Int, // 0..3,4 個 cluster 對應 4 色
)

private data class RecommendedAction(
    val title: String,
    val description: String,
)

private data class SkillProgress(
    val name: String,
    val score: Int, // 0-100
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitAnalysisScreen(navController: NavHostController) {
    // mock data
    val skills = remember {
        listOf(
            PetalSkill("學習力", 96, 0),
            PetalSkill("適應力", 72, 0),
            PetalSkill("時間管理", 76, 1),
            PetalSkill("抗壓性", 90, 1),
            PetalSkill("倫理判斷", 58, 2),
            PetalSkill("溝通", 92, 2),
            PetalSkill("協作", 65, 3),
            PetalSkill("創新", 74, 3),
        )
    }
    val actions = remember {
        listOf(
            RecommendedAction(
                "補一段「失敗復原」經歷",
                "面試常問挫折題,你目前 2 段相關經歷不夠",
            ),
            RecommendedAction(
                "強化數據相關描述",
                "你的職位目標需要量化能力,可在 2 段經歷補上數據",
            ),
            RecommendedAction(
                "練一輪團體面試模擬",
                "團體面試是你最弱類型,建議下週前完成 1 場",
            ),
        )
    }
    val skillProgress = remember {
        listOf(
            SkillProgress("溝通表達", 88),
            SkillProgress("批判思考", 76),
            SkillProgress("創意發想", 65),
            SkillProgress("協作能力", 82),
            SkillProgress("數據敏感", 70),
            SkillProgress("學習速度", 92),
        )
    }
    var checkedActions by remember { mutableStateOf(setOf<Int>()) }

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
            // ===== 1. Hero =====
            HeroSection()

            Spacer(Modifier.height(20.dp))

            // ===== 2. 花瓣圖 =====
            SectionTitle("能力分布")
            Spacer(Modifier.height(8.dp))
            PetalChartCard(skills = skills)

            Spacer(Modifier.height(20.dp))

            // ===== 3. 推薦行動 =====
            SectionTitle("下一步推薦")
            Spacer(Modifier.height(8.dp))
            ActionsCard(
                actions = actions,
                checked = checkedActions,
                onToggle = { idx ->
                    checkedActions = if (idx in checkedActions) {
                        checkedActions - idx
                    } else {
                        checkedActions + idx
                    }
                },
            )

            Spacer(Modifier.height(20.dp))

            // ===== 4. 技能熟練度 =====
            SectionTitle("技能熟練度")
            Spacer(Modifier.height(8.dp))
            SkillProgressCard(skillProgress)
        }
    }
}

// ============================================================
// Hero
// ============================================================

@Composable
private fun HeroSection() {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BrandPeach.copy(alpha = 0.6f),
                        PaperWhite,
                    ),
                ),
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 大頭照圓
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(BrandAmber, BrandDeepOrange),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "A",
                color = PaperWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.width(14.dp))
        // 姓名 + 目標
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Alex 政大資管大三",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "目標:Junior PM",
                color = InkGray500,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BrandDeepOrange.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    "Pending · 7 段經歷可用",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                )
            }
        }
        // 適配度大數字
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "78",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp,
                    lineHeight = 36.sp,
                )
                Text(
                    "%",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
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
// Section title
// ============================================================

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 20.dp),
        color = InkBlack,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
    )
}

// ============================================================
// Petal Chart Card
// ============================================================

@Composable
private fun PetalChartCard(skills: List<PetalSkill>) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .background(InkGray100.copy(alpha = 0.4f))
            .padding(vertical = 24.dp, horizontal = 16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Canvas with petals
            Canvas(modifier = Modifier.size(280.dp)) {
                drawPetalChart(size = size, skills = skills)
            }

            Spacer(Modifier.height(16.dp))

            // 簡述
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandPeach.copy(alpha = 0.25f))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = BrandDeepOrange,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "你的學習力、溝通、抗壓性最強,協作與倫理判斷可再加強。",
                    color = InkGray700,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

private fun DrawScope.drawPetalChart(size: Size, skills: List<PetalSkill>) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val maxR = minOf(size.width, size.height) / 2f * 0.92f
    val petalCount = skills.size
    val anglePer = 2 * PI / petalCount

    // 顏色 cluster
    val clusterColors = listOf(
        BrandDeepOrange,
        BrandAmber,
        GlowPurple,
        AccentGreen,
    )

    skills.forEachIndexed { idx, skill ->
        val angle = idx * anglePer - PI / 2 // 從頂部開始
        val petalLength = maxR * (skill.score / 100f)
        val color = clusterColors[skill.cluster % clusterColors.size]

        // 花瓣中心點
        val tipX = (center.x + cos(angle) * petalLength).toFloat()
        val tipY = (center.y + sin(angle) * petalLength).toFloat()

        // 花瓣寬度(在中心附近往兩側張開)
        val petalWidth = (2 * PI / petalCount).toFloat() * petalLength * 0.55f

        // 用 bezier 畫花瓣形狀
        val perpAngle = angle + PI / 2
        val side1X = (center.x + cos(angle) * petalLength * 0.5f + cos(perpAngle) * petalWidth).toFloat()
        val side1Y = (center.y + sin(angle) * petalLength * 0.5f + sin(perpAngle) * petalWidth).toFloat()
        val side2X = (center.x + cos(angle) * petalLength * 0.5f - cos(perpAngle) * petalWidth).toFloat()
        val side2Y = (center.y + sin(angle) * petalLength * 0.5f - sin(perpAngle) * petalWidth).toFloat()

        val path = Path().apply {
            moveTo(center.x, center.y)
            quadraticBezierTo(side1X, side1Y, tipX, tipY)
            quadraticBezierTo(side2X, side2Y, center.x, center.y)
            close()
        }

        // 漸層花瓣
        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.85f),
                    color.copy(alpha = 0.45f),
                ),
                center = center,
                radius = petalLength,
            ),
        )
        // 邊框
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.2f),
        )

        // 中心數字
        val labelX = (center.x + cos(angle) * petalLength * 0.55f).toFloat()
        val labelY = (center.y + sin(angle) * petalLength * 0.55f).toFloat()
        drawContext.canvas.nativeCanvas.let { canvas ->
            val paint = android.graphics.Paint().apply {
                setColor(android.graphics.Color.WHITE)
                textSize = 26f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("${skill.score}%", labelX, labelY + 8f, paint)
        }

        // 花瓣外標籤
        val outerLabelR = maxR * 1.08f
        val outerX = (center.x + cos(angle) * outerLabelR).toFloat()
        val outerY = (center.y + sin(angle) * outerLabelR).toFloat()
        drawContext.canvas.nativeCanvas.let { canvas ->
            val paint = android.graphics.Paint().apply {
                setColor(android.graphics.Color.parseColor("#0B0E14"))
                textSize = 22f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(skill.label, outerX, outerY + 7f, paint)
        }
    }
}

// ============================================================
// Actions Card
// ============================================================

@Composable
private fun ActionsCard(
    actions: List<RecommendedAction>,
    checked: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(2.dp),
    ) {
        actions.forEachIndexed { idx, action ->
            ActionRow(
                action = action,
                isChecked = idx in checked,
                onToggle = { onToggle(idx) },
            )
            if (idx < actions.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(0.5.dp)
                        .background(InkGray200),
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
    action: RecommendedAction,
    isChecked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onToggle)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) BrandDeepOrange else PaperWhite)
                .padding(2.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isChecked) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(14.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PaperWhite),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = InkGray300,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                            style = Stroke(width = 1.2.dp.toPx()),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                action.title,
                color = if (isChecked) InkGray500 else InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                action.description,
                color = InkGray500,
                fontSize = 11.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

// ============================================================
// Skill Progress
// ============================================================

@Composable
private fun SkillProgressCard(progress: List<SkillProgress>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        progress.forEach { skill ->
            SkillProgressRow(skill)
        }
    }
}

@Composable
private fun SkillProgressRow(skill: SkillProgress) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                skill.name,
                modifier = Modifier.weight(1f),
                color = InkBlack,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
            Text(
                "${skill.score}",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(6.dp))
        // 進度條
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(InkGray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(skill.score / 100f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(BrandAmber, BrandDeepOrange),
                        ),
                    ),
            )
        }
    }
}
