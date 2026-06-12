package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.InterviewRecord
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewHistoryScreen(navController: NavHostController) {
    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("面試歷史", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(PaperWhite),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // 統計 hero
            item {
                StaggeredAppear(delayMillis = 0) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(BrandOrange.copy(alpha = 0.1f))
                                .padding(20.dp),
                        ) {
                            StatBox("4", "完成", Modifier.weight(1f))
                            StatBox("71", "平均分", Modifier.weight(1f))
                            StatBox("78", "最高分", Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
            // === #8 進步趨勢 dashboard ===
            item {
                StaggeredAppear(delayMillis = 90) {
                    Column {
                        TrendDashboard()
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
            // 標題
            item {
                StaggeredAppear(delayMillis = 180) {
                    Column {
                        Text(
                            "全部紀錄",
                            color = InkBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            // 紀錄列
            itemsIndexed(MockData.interviewHistory) { idx, record ->
                StaggeredAppear(delayMillis = 260 + idx * 70) {
                    Column {
                        HistoryRowFull(record) {
                            navController.navigate(Routes.INTERVIEW_REPORT)
                        }
                        SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value,
            color = BrandDeepOrange,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp)
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HistoryRowFull(r: InterviewRecord, onClick: () -> Unit) {
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

/* ===================== #8 進步趨勢 dashboard ===================== */

@Composable
private fun TrendDashboard() {
    val scores = MockData.interviewHistory.reversed().map { it.score } // 舊 → 新
    val first = scores.firstOrNull() ?: 0
    val last = scores.lastOrNull() ?: 0
    val delta = last - first
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("進步趨勢", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            "每場面試的整體分數變化。練越多次,看得到自己在成長。",
            color = InkGray500, fontSize = 13.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(14.dp))
        ScoreTrendChart(scores)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "首場 $first → 最新 $last",
                color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            if (delta != 0) {
                val up = delta > 0
                val mag = if (up) delta else -delta
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background((if (up) AccentGreen else AccentRed).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        (if (up) "↑ 進步 " else "↓ 退步 ") + "$mag 分",
                        color = if (up) AccentGreen else AccentRed,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("各面向進步(首場 → 最新)", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        FacetTrendRow("內容", 64, 71)
        FacetTrendRow("結構", 70, 82)
        FacetTrendRow("表達", 66, 70)
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BrandPeach.copy(alpha = 0.4f))
                .padding(12.dp),
        ) {
            Text(
                "這些數字也會反映在面試 Hub 的 Avatar 成長卡上。練習就是在養角色。",
                color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun ScoreTrendChart(scores: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BrandOrange.copy(alpha = 0.08f))
            .padding(16.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (scores.size >= 2) {
                val pad = 8.dp.toPx()
                val w = size.width - pad * 2
                val h = size.height - pad * 2
                val minS = 50f
                val maxS = 100f
                fun px(i: Int) = pad + (i.toFloat() / (scores.size - 1)) * w
                fun py(s: Int) = pad + (1f - (s - minS) / (maxS - minS)) * h
                // 基線
                drawLine(
                    color = InkGray200,
                    start = Offset(pad, pad + h),
                    end = Offset(pad + w, pad + h),
                    strokeWidth = 1.5.dp.toPx(),
                )
                // 折線
                for (i in 0 until scores.size - 1) {
                    drawLine(
                        color = BrandDeepOrange,
                        start = Offset(px(i), py(scores[i])),
                        end = Offset(px(i + 1), py(scores[i + 1])),
                        strokeWidth = 3.dp.toPx(),
                    )
                }
                // 點
                scores.forEachIndexed { i, s ->
                    val lastPt = i == scores.size - 1
                    drawCircle(
                        color = if (lastPt) BrandDeepOrange else BrandOrange,
                        radius = if (lastPt) 6.dp.toPx() else 4.dp.toPx(),
                        center = Offset(px(i), py(s)),
                    )
                    if (lastPt) {
                        drawCircle(color = PaperWhite, radius = 2.5.dp.toPx(), center = Offset(px(i), py(s)))
                    }
                }
            }
        }
    }
}

@Composable
private fun FacetTrendRow(name: String, from: Int, to: Int) {
    val delta = to - from
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(52.dp))
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(50)).background(InkGray100),
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth((to / 100f).coerceIn(0f, 1f)).clip(RoundedCornerShape(50)).background(BrandOrange.copy(alpha = 0.5f)),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text("$from→$to", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        if (delta != 0) {
            Spacer(Modifier.width(6.dp))
            val up = delta > 0
            Text(
                (if (up) "↑" else "↓") + (if (up) delta else -delta),
                color = if (up) AccentGreen else AccentRed,
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
            )
        }
    }
}
