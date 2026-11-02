package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm

private val AnaLine = Color(0xFFEADFD6)

@Composable
fun SandboxAnalysisScreen(navController: NavHostController) {
    val state = SandboxV2State
    LaunchedEffect(Unit) { state.onAnalysisOpened() }

    // Clear or partial clarity: pick a question first. Vague: free exploration.
    val questionMode = state.clarity != Clarity.VAGUE

    var qSel by remember { mutableStateOf<Int?>(null) }
    var metric by remember { mutableStateOf<String?>(null) }
    var seg by remember { mutableStateOf<String?>(null) }
    var intent by remember { mutableStateOf<String?>(null) }

    val locked = state.station >= 5   // the summary is already sent, this window is read-only
    val canBuild = !locked && (!questionMode || qSel != null)
    val full = state.pins.size >= SandboxV2State.MAX_PINS

    Column(Modifier.fillMaxSize().background(PaperWarm)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "‹ 工作台", color = InkGray500, fontSize = 13.sp,
                modifier = Modifier.pressScale { navController.popBackStack() }.padding(end = 12.dp, top = 4.dp, bottom = 4.dp),
            )
            Text("分析", color = InkSlate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        ) {
            // Task prompt
            if (questionMode) {
                Section("你現在想回答哪一題？")
                state.taskList.forEachIndexed { i, q ->
                    val used = state.pins.any { it.questionIndex == i }
                    val on = qSel == i
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .alpha(if (used) 0.45f else 1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (on) BrandPeach.copy(alpha = 0.6f) else Color.White)
                            .border(if (on) 2.dp else 1.dp, if (on) BrandOrange else AnaLine, RoundedCornerShape(10.dp))
                            .pressScale { if (!used && !full) qSel = i }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(
                            "${i + 1}  $q" + if (used) "（已有圖）" else "",
                            color = InkSlate, fontSize = 13.sp,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandPeach.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Text("現在要做的事", color = InkGray500, fontSize = 11.sp)
                    Text(
                        "小昀只問你「收藏清單值不值得做？」你先決定，這份資料今天最值得確認什麼。畫好的圖釘起來，寫摘要時會用到。",
                        color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            if (locked) {
                Text("摘要已經送出，這裡只能看。", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
            } else if (full) {
                Text("最多釘四張。要換圖，先取消其中一張。", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
            } else if (canBuild) {
                // Metric
                Section("指標")
                ChipRow(SandboxAnalysis.metrics.map { it.name }, metric) { metric = it }
                SandboxAnalysis.metrics.firstOrNull { it.name == metric }?.let { Explain(it.explain) }

                // Segmentation
                Section("分群")
                ChipRow(SandboxAnalysis.segs, seg) { seg = it }
                seg?.let { Explain(SandboxAnalysis.segSource(it)) }

                // Chart
                val m = metric
                val s = seg
                if (m != null && s != null) {
                    val labels = SandboxAnalysis.labels(s)
                    val values = SandboxAnalysis.values(m, s)
                    // Column 3 (稍後再看次數) is only recorded since 6/1, unless the player handled that limit
                    val limitNoted = m == "稍後再看使用率" && state.limitHandled[3] !in setOf(0, 1, 3)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, AnaLine, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                    ) {
                        Text(
                            "$m · $s",
                            color = InkSlate, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(10.dp))
                        BarChart(labels, values)
                        if (limitNoted) {
                            Text("此欄自 6/1 起記錄", color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    val duplicate = state.pins.any { it.metric == m && it.seg == s }

                    // Pin
                    if (!questionMode && !duplicate) {
                        Section("這張圖主要想確認（可不填）")
                        ChipRow(SandboxAnalysis.intents, intent) { intent = it }
                    }
                    if (duplicate) {
                        Text(
                            "這張圖已經釘過了。換一個指標或分群。",
                            color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp),
                        )
                    } else {
                        Box(
                            // keep the existing pin button as it is
                            modifier = Modifier
                                .padding(top = 14.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(BrandOrange)
                                .pressScale {
                                    state.pinChart(
                                        ChartPin(
                                            metric = m, seg = s, method = null,
                                            questionIndex = if (questionMode) qSel else null,
                                            intent = if (questionMode) null else intent,
                                            labels = labels, values = values,
                                        )
                                    )
                                    qSel = null; metric = null; seg = null; intent = null
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) { Text("釘選這張圖", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)  }
                    }
                }
            }

            // Pinned charts
            if (state.pins.isNotEmpty()) {
                Section("已釘 ${state.pins.size} 張")
                state.pins.forEachIndexed { i, p ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, AnaLine, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${p.metric} · ${p.seg}",
                                color = InkSlate, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            if (!locked) {
                                Text(
                                    "取消釘選", color = InkGray500, fontSize = 12.sp,
                                    modifier = Modifier.pressScale { state.unpinChart(i) }.padding(start = 12.dp),
                                )
                            }
                        }
                        val target = when {
                            p.questionIndex != null -> "回答：" + (state.taskList.getOrNull(p.questionIndex) ?: "")
                            p.intent != null -> "想確認：${p.intent}"
                            else -> "沒有標記想確認什麼"
                        }
                        Text(target, color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(8.dp))
                        BarChart(p.labels, p.values)
                    }
                }
            }
            if (!locked) {
                Box(
                    // keep the existing modifier chain and content as they are
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 24.dp)
                        .fillMaxWidth()
                        .alpha(if (state.pins.isEmpty()) 0.4f else 1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(BrandOrange)
                        .pressScale {
                            if (state.pins.isNotEmpty()) {
                                state.finishAnalysis()
                                navController.popBackStack()
                            }
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("分析做完了", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(24.dp))

        }
    }
}

@Composable
private fun Section(text: String) {
    Text(text, color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
}

@Composable
private fun Explain(text: String) {
    Text(text, color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun ChipRow(items: List<String>, selected: String?, onPick: (String) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { name ->
            val on = name == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (on) BrandOrange else Color.White)
                    .border(1.dp, if (on) BrandOrange else AnaLine, RoundedCornerShape(20.dp))
                    .pressScale { onPick(name) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(name, color = if (on) Color.White else InkSlate, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun BarChart(labels: List<String>, values: List<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth().height(170.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { i, v ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text("$v%", color = InkSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .width(52.dp)
                        .height(110.dp * (v / 100f))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(BrandOrange),
                )
                Text(labels[i], color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}