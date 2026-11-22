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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import com.careersandbox.app.data.remote.InterviewSessionSummaryDto
import com.careersandbox.app.data.repository.RemoteInterviewSessionRepository
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewHistoryScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    // null while loading
    var records by remember { mutableStateOf<List<InterviewSessionSummaryDto>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        RemoteInterviewSessionRepository().list()
            .onSuccess { records = it }
            .onFailure { error = it.message; records = emptyList() }
    }
    val list = records.orEmpty()
    // Newest first, as the server returns them; sessions without a score are left out.
    val scores = list.mapNotNull { it.score?.roundToInt() }

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
                            StatBox("${list.size}", "完成", Modifier.weight(1f))
                            StatBox(
                                if (scores.isEmpty()) "—" else "${scores.sum() / scores.size}",
                                "平均分", Modifier.weight(1f),
                            )
                            StatBox(
                                scores.maxOrNull()?.toString() ?: "—",
                                "最高分", Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
            // 進步趨勢
            item {
                StaggeredAppear(delayMillis = 90) {
                    Column {
                        TrendDashboard(scores.reversed()) // 舊 → 新
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
            if (records == null) {
                item { Text("載入中…", color = InkGray400, fontSize = 13.sp) }
            } else if (error != null) {
                item { Text(error ?: "", color = InkGray400, fontSize = 13.sp) }
            } else if (list.isEmpty()) {
                item {
                    Text(
                        "還沒有儲存的面試。完成一場面試後，在報告頁按「儲存報告」就會出現在這裡。",
                        color = InkGray400, fontSize = 13.sp, lineHeight = 19.sp,
                    )
                }
            }
            // 紀錄列
            itemsIndexed(list) { idx, record ->
                StaggeredAppear(delayMillis = 260 + idx * 70) {
                    Column {
                        SavedInterviewRow(record) {
                            openSavedReport(scope, ctx, navController, record.id)
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


/* ===================== #8 進步趨勢 dashboard ===================== */

@Composable
private fun TrendDashboard(scores: List<Int>) { // oldest → newest
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("進步趨勢", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            "每場面試的整體分數變化。練越多次,看得到自己在成長。",
            color = InkGray500, fontSize = 13.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(14.dp))
        if (scores.size >= 2) {
            val first = scores.first()
            val last = scores.last()
            val delta = last - first
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
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandOrange.copy(alpha = 0.08f))
                    .padding(16.dp),
            ) {
                Text(
                    "再儲存 ${2 - scores.size} 場有分數的面試，就能看到趨勢。",
                    color = InkGray500, fontSize = 13.sp,
                )
            }
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

