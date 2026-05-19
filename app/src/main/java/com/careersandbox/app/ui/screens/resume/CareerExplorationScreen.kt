package com.careersandbox.app.ui.screens.resume

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
    val recs = remember {
        listOf(
            JobRec("資料分析師", "Data Analyst · BI / Marketing", 92, "SQL 進階 · A/B test", "45–65k", 1240),
            JobRec("產品經理", "Product Manager · SaaS / B2C", 78, "用戶研究 · 創新案例", "50–80k", 980),
            JobRec("行銷策略", "Growth · Strategy Consultant", 71, "品牌經營 · 數位行銷", "42–58k", 760),
        )
    }
    val learningSteps = remember {
        listOf(
            LearningStep("本學期", "選修「資料庫管理」", "商管院・3 學分"),
            LearningStep("下學期", "考 Google Data Analytics 證照", "Coursera・6 個月"),
            LearningStep("暑假", "投遞外商 BI 實習", "補強用戶研究經驗"),
        )
    }
    val suggestedKeywords = listOf("UX 設計師", "數位行銷", "創投分析")

    var searchInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("職涯探索", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = InkGray500)
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
            // ===== 1. Basis chips =====
            BasisSection()

            Spacer(Modifier.height(14.dp))

            // ===== 2. Recommended jobs =====
            JobRecCardList(recs)

            Spacer(Modifier.height(22.dp))
            FitDivider()
            Spacer(Modifier.height(18.dp))

            // ===== 3. Search & suggestions =====
            SearchSection(
                value = searchInput,
                onChange = { searchInput = it },
                suggestions = suggestedKeywords,
                onSuggestionClick = { searchInput = it },
            )

            Spacer(Modifier.height(22.dp))
            FitDivider()
            Spacer(Modifier.height(18.dp))

            // ===== 4. Learning plan =====
            LearningPlanSection(
                targetTitle = recs[0].title,
                steps = learningSteps,
            )
        }
    }
}

// ============================================================
// Basis Section
// ============================================================

@Composable
private fun BasisSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        Text(
            "基於",
            color = InkGray500,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BasisChip("4 段經歷")
            BasisChip("42 門修課")
            BasisChip("數據導向型")
        }
        Spacer(Modifier.height(14.dp))
        Row {
            Text(
                "最適合你的 ",
                color = InkBlack,
                fontSize = 13.sp,
                lineHeight = 22.sp,
            )
            Text(
                "3 條職涯路徑",
                color = BrandDeepOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun BasisChip(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(BrandPeach.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text,
            color = Color(0xFF993C1D),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ============================================================
// Job rec cards
// ============================================================

@Composable
private fun JobRecCardList(recs: List<JobRec>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        recs.forEachIndexed { idx, rec ->
            if (idx == 0) {
                BestMatchCard(rec = rec)
            } else {
                NormalRecCard(rec = rec, idx = idx)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun BestMatchCard(rec: JobRec) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PaperWhite)
                .border(
                    width = 1.5.dp,
                    color = BrandDeepOrange,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(14.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        rec.title,
                        color = InkBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        rec.titleEn,
                        color = InkGray500,
                        fontSize = 10.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${rec.match}",
                        color = BrandDeepOrange,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 22.sp,
                    )
                    Text(
                        "% 匹配",
                        color = InkGray500,
                        fontSize = 9.sp,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            // Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(InkGray100),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(rec.match / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(BrandDeepOrange),
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "已具備 ${rec.match}%",
                    modifier = Modifier.weight(1f),
                    color = InkGray500,
                    fontSize = 9.sp,
                )
                Text(
                    "缺 ${rec.gap}",
                    color = InkGray500,
                    fontSize = 9.sp,
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(InkGray200),
            )
            Spacer(Modifier.height(10.dp))
            Row {
                Text("月薪", modifier = Modifier.weight(1f), color = InkGray500, fontSize = 10.sp)
                Text(rec.salaryRange, color = InkBlack, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(4.dp))
            Row {
                Text("徵才", modifier = Modifier.weight(1f), color = InkGray500, fontSize = 10.sp)
                Text("${rec.openings} 個職缺", color = InkBlack, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        // Best Match 標籤
        Box(
            modifier = Modifier
                .padding(start = 14.dp)
                .offset(y = (-8).dp)
                .clip(CircleShape)
                .background(BrandDeepOrange)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                "BEST MATCH",
                color = PaperWhite,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NormalRecCard(rec: JobRec, idx: Int) {
    val barColor = if (idx == 1) BrandAmber else BrandYellow
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PaperWhite)
            .border(
                width = 0.5.dp,
                color = InkGray200,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    rec.title,
                    color = InkBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    rec.titleEn,
                    color = InkGray500,
                    fontSize = 10.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${rec.match}",
                    color = barColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 22.sp,
                )
                Text(
                    "% 匹配",
                    color = InkGray500,
                    fontSize = 9.sp,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape)
                .background(InkGray100),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(rec.match / 100f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(barColor),
            )
        }
        Spacer(Modifier.height(4.dp))
        Row {
            Text(
                "已具備 ${rec.match}%",
                modifier = Modifier.weight(1f),
                color = InkGray500,
                fontSize = 9.sp,
            )
            Text(
                "缺 ${rec.gap}",
                color = InkGray500,
                fontSize = 9.sp,
            )
        }
    }
}

// ============================================================
// Search Section
// ============================================================

@Composable
private fun SearchSection(
    value: String,
    onChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            "想看其他職位?",
            color = InkBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(10.dp))
        // Search input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PaperWhite)
                .border(0.5.dp, InkGray200, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = InkGray500,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        "例如:UX 設計師 / 數位行銷",
                        color = InkGray400,
                        fontSize = 12.sp,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onChange,
                    textStyle = TextStyle(
                        color = InkBlack,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Icon(
                Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = BrandDeepOrange,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            suggestions.forEach { kw ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PaperWhite)
                        .border(0.5.dp, InkGray200, CircleShape)
                        .pressScale(onClick = { onSuggestionClick(kw) })
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        kw,
                        color = InkBlack,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

// ============================================================
// Learning Plan
// ============================================================

@Composable
private fun LearningPlanSection(
    targetTitle: String,
    steps: List<LearningStep>,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            "為「$targetTitle」制定的學習計畫",
            color = InkBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PaperWhite)
                .padding(14.dp),
        ) {
            steps.forEachIndexed { idx, step ->
                LearningStepRow(step = step, idx = idx)
                if (idx < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(InkGray200),
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                "展開完整學習路徑 ↘",
                color = BrandDeepOrange,
                fontSize = 10.sp,
                modifier = Modifier.pressScale(onClick = { }),
            )
        }
    }
}

@Composable
private fun LearningStepRow(step: LearningStep, idx: Int) {
    val color = when (idx) {
        0 -> BrandDeepOrange
        1 -> BrandAmber
        else -> BrandYellow
    }
    val textColor = if (idx >= 2) Color(0xFF993C1D) else PaperWhite
    Row(
        modifier = Modifier.padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "${idx + 1}",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                step.phase,
                color = InkGray500,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                step.title,
                color = InkBlack,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                step.source,
                color = InkGray500,
                fontSize = 10.sp,
            )
        }
    }
}

// ============================================================
// Divider
// ============================================================

@Composable
private fun FitDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(InkGray100.copy(alpha = 0.5f)),
    )
}
