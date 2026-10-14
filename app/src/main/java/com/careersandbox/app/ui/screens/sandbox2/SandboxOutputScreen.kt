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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandAmber
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm

private val OutLine = Color(0xFFEADFD6)

@Composable
fun SandboxOutputScreen(navController: NavHostController) {
    val state = SandboxV2State
    LaunchedEffect(Unit) { state.onOutputOpened() }

    val questionMode = state.clarity != Clarity.VAGUE
    val fits = state.pins.map { SandboxAnalysis.fits(it, state.taskList) }
    val fitIdx = fits.indices.filter { fits[it] }

    // Question mode only: questions with no chart at all, and questions whose charts do not fit
    val noPin = if (!questionMode) emptyList() else state.taskList.indices.filter { qi ->
        state.pins.none { it.questionIndex == qi }
    }
    val noFit = if (!questionMode) emptyList() else state.taskList.indices.filter { qi ->
        state.pins.any { it.questionIndex == qi } &&
                state.pins.indices.none { pi -> state.pins[pi].questionIndex == qi && fits[pi] }
    }

    Column(Modifier.fillMaxSize().background(PaperWarm)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "‹ 工作台", color = InkGray500, fontSize = 13.sp,
                modifier = Modifier.pressScale { navController.popBackStack() }.padding(end = 12.dp, top = 4.dp, bottom = 4.dp),
            )
            Text("產出", color = InkSlate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandPeach.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text("現在要做的事", color = InkGray500, fontSize = 11.sp)
                Text(
                    "看著你釘的圖，決定摘要裡怎麼寫。對不上問題的圖，小昀會先講話。",
                    color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 2.dp),
                )
            }

            // Findings
            OutSection(if (questionMode) "發現" else "你今天選了幾個方向。整理成一份給小昀看的摘要。")
            state.pins.forEachIndexed { pi, p -> PinCard(pi, p, fits[pi]) }
            if (fitIdx.isEmpty()) {
                Text(
                    "目前沒有一張圖對得上問題，摘要的「發現」會是空的。回「分析」換一張，或直接送出。",
                    color = InkGray500, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(vertical = 6.dp),
                )
            }
            if (fitIdx.size > 1) {
                Column(OutCard()) {
                    Text("再一題", color = InkGray500, fontSize = 11.sp)
                    Text("如果老闆只看一張圖，你會給哪張？", color = InkSlate, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fitIdx.forEach { i ->
                            OutChip("圖 ${i + 1}：${state.pins[i].metric}", state.keyChart == i) { state.chooseKeyChart(i) }
                        }
                    }
                }
            }

            // Limitations
            OutSection("這份資料看不出來的事")
            SandboxAnalysis.summaryLimits.forEachIndexed { i, l ->
                val tag = l.col?.let { col ->
                    when (state.limitHandled[col]) {
                        0 -> "阿凱確認過"
                        3 -> "小昀知道"
                        1 -> "你記下來的"
                        else -> null
                    }
                }
                CheckRow(l.text, i in state.selectedLimits, tag) { state.toggleLimit(i) }
            }

            // Recommendation
            OutSection("建議")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SandboxAnalysis.recommendations.forEach { r ->
                    OutChip(r, state.recommendation == r) { state.chooseRecommendation(r) }
                }
            }

            // Preview
            OutSection("預覽")
            val findings = state.selectedCands.mapNotNull { key ->
                val (pi, ci) = key.split("-").map { it.toInt() }
                state.pins.getOrNull(pi)?.let { SandboxAnalysis.candidates(it).getOrNull(ci)?.text }
            }
            val limits = state.selectedLimits.map { SandboxAnalysis.summaryLimits[it].text }
            Column(OutCard()) {
                PreviewLine("發現", findings.joinToString(" ").ifEmpty { "—" })
                if (noPin.isNotEmpty()) Missing("第 ${noPin.joinToString("、") { (it + 1).toString() }} 題還沒有圖")
                if (noFit.isNotEmpty()) Missing("第 ${noFit.joinToString("、") { (it + 1).toString() }} 題的圖對不上，等於沒答")
                PreviewLine("看不出來的事", limits.joinToString("；").ifEmpty { "—" })
                PreviewLine("建議", state.recommendation ?: "—")
            }

            // Submit
            val ready = state.recommendation != null
            Box(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 24.dp)
                    .fillMaxWidth()
                    .alpha(if (ready) 1f else 0.4f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BrandOrange)
                    .pressScale {
                        if (ready) {
                            state.submitOutput()
                            // TODO: station 5 follow-up goes here; for now jump to the result screen
                            navController.navigate(Routes.SANDBOX_V2_RESULT)
                        }
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) { Text("送出給小昀", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun PinCard(pi: Int, p: ChartPin, fit: Boolean) {
    val state = SandboxV2State
    val source = buildString {
        append("${p.metric} · ${p.seg}")
        p.questionIndex?.let { append("  →  第 ${it + 1} 題") }
        p.intent?.let { append("  ·  $it") }
    }
    Column(OutCard()) {
        Text(source, color = InkGray500, fontSize = 11.sp)
        MiniChart(p)
        if (!fit) {
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(28.dp).clip(CircleShape).background(BrandAmber),
                    contentAlignment = Alignment.Center,
                ) { Text("昀", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("小昀（看了你的草稿）", color = InkGray500, fontSize = 11.sp)
                    Text(
                        SandboxAnalysis.remark(p, state.taskList),
                        color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp,
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .clip(RoundedCornerShape(4.dp, 12.dp, 12.dp, 12.dp))
                            .background(PaperWarm)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                    )
                }
            }
            Text(
                "這張圖沒有可以寫進摘要的句子。要換圖可以回「分析」。",
                color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            Text("這張圖，你會在摘要裡怎麼寫？可以選一句以上。", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            SandboxAnalysis.candidates(p).forEachIndexed { ci, c ->
                val key = "$pi-$ci"
                CheckRow(c.text, key in state.selectedCands, null) { state.toggleCand(key) }
            }
        }
    }
}

@Composable
private fun MiniChart(p: ChartPin) {
    Row(
        modifier = Modifier.fillMaxWidth().height(110.dp).padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        p.values.forEachIndexed { i, v ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                Text("$v%", color = InkSlate, fontSize = 10.sp)
                Box(
                    Modifier
                        .width(36.dp)
                        .height(60.dp * (v / 100f))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(BrandOrange),
                )
                Text(p.labels[i], color = InkGray500, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

@Composable
private fun OutCard(): Modifier = Modifier
    .fillMaxWidth()
    .padding(top = 8.dp)
    .clip(RoundedCornerShape(12.dp))
    .background(Color.White)
    .border(1.dp, OutLine, RoundedCornerShape(12.dp))
    .padding(12.dp)

@Composable
private fun OutSection(text: String) {
    Text(text, color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 18.dp, bottom = 2.dp))
}

@Composable
private fun OutChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) BrandOrange else Color.White)
            .border(1.dp, if (selected) BrandOrange else OutLine, RoundedCornerShape(20.dp))
            .pressScale { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) { Text(text, color = if (selected) Color.White else InkSlate, fontSize = 13.sp) }
}

@Composable
private fun CheckRow(text: String, checked: Boolean, tag: String?, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().pressScale { onToggle() }.padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (checked) BrandOrange else Color.White)
                .border(1.5.dp, if (checked) BrandOrange else OutLine, RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center,
        ) { if (checked) Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(text, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp)
            if (tag != null) Text(tag, color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
        }
    }
}

@Composable
private fun PreviewLine(title: String, body: String) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(title, color = InkGray500, fontSize = 11.sp)
        Text(body, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 1.dp))
    }
}

@Composable
private fun Missing(text: String) {
    Text(text, color = BrandOrange, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
}