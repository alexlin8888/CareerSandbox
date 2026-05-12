package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JdCustomizeScreen(navController: NavHostController) {
    var step by remember { mutableIntStateOf(1) }
    var jdText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("JD 客製化", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth().background(PaperOff).padding(20.dp)) {
                if (step > 1) {
                    SecondaryButton("上一步", { step-- }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                }
                PrimaryDarkButton(
                    if (step == 5) "完成" else "下一步",
                    { if (step < 5) step++ else navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            StepIndicator(step, 5)
            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                        .togetherWith(fadeOut(tween(150)))
                },
                label = "step",
            ) { s ->
                Column {
                    when (s) {
                        1 -> Step1Paste(jdText) { jdText = it }
                        2 -> Step2Analyze()
                        3 -> Step3Match()
                        4 -> Step4Base()
                        5 -> Step5Generate()
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StepIndicator(current: Int, total: Int) {
    val labels = listOf("貼 JD", "分析", "比對", "選底稿", "生成")
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..total) {
            val active = i <= current
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(50))
                    .background(if (active) InkBlack else InkGray200),
                contentAlignment = Alignment.Center,
            ) {
                if (active && i < current) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = PaperWhite,
                        modifier = Modifier.size(16.dp))
                } else {
                    Text("$i", color = if (active) PaperWhite else InkGray400,
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            if (i < total) {
                Box(Modifier.weight(1f).height(2.dp)
                    .background(if (i < current) InkBlack else InkGray200))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text(labels.getOrElse(current - 1) { "" },
        style = MaterialTheme.typography.labelLarge,
        color = BrandOrange, fontWeight = FontWeight.Bold)
}

@Composable
private fun Step1Paste(jd: String, onChange: (String) -> Unit) {
    Text("貼上目標職位描述", style = MaterialTheme.typography.headlineMedium,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("從 104、LinkedIn 等網站複製即可", color = InkGray500,
        style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(20.dp))
    OutlinedTextField(
        value = jd, onValueChange = onChange,
        placeholder = { Text("把整段 JD 貼進來⋯", color = InkGray400) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

@Composable
private fun Step2Analyze() {
    Text("AI 分析這份 JD", style = MaterialTheme.typography.headlineMedium,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(20.dp))
    AnalysisCard("必備條件", Icons.Outlined.CheckCircle, AccentGreen,
        listOf("資管/商管相關科系", "Excel 進階操作", "基礎 SQL 能力"))
    AnalysisCard("加分條件", Icons.Outlined.AddCircle, BrandOrange,
        listOf("Tableau / Power BI", "Python 資料處理", "電商產業經驗"))
    AnalysisCard("隱性需求", Icons.Outlined.Visibility, AccentBlue,
        listOf("能跨團隊溝通", "面對大量未整理資料的耐性", "願意主動找問題"))
}

@Composable
private fun AnalysisCard(title: String, icon: ImageVector, accent: Color, items: List<String>) {
    StaggeredAppear {
        WhiteCard(modifier = Modifier.padding(vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = accent,
                        modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium,
                    color = InkBlack, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            items.forEach {
                Row(Modifier.padding(vertical = 3.dp)) {
                    Text("・ ", color = accent, fontWeight = FontWeight.Bold)
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = InkBlack)
                }
            }
        }
    }
}

@Composable
private fun Step3Match() {
    Text("比對你的素材", style = MaterialTheme.typography.headlineMedium,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(20.dp))
    MatchRow("Excel 進階操作", "已覆蓋", AccentGreen)
    MatchRow("基礎 SQL 能力", "已覆蓋", AccentGreen)
    MatchRow("跨團隊溝通", "需強化", AccentYellow)
    MatchRow("Python 資料處理", "缺失", AccentRed)
    MatchRow("Tableau / Power BI", "缺失", AccentRed)
}

@Composable
private fun MatchRow(item: String, status: String, color: Color) {
    WhiteCard(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).clip(RoundedCornerShape(50)).background(color))
            Spacer(Modifier.width(12.dp))
            Text(item, style = MaterialTheme.typography.bodyLarge,
                color = InkBlack, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(status, style = MaterialTheme.typography.labelSmall,
                    color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Step4Base() {
    Text("選一份底稿", style = MaterialTheme.typography.headlineMedium,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(20.dp))
    var selectedId by remember { mutableStateOf("r1") }
    MockData.resumes.forEach { r ->
        val sel = r.id == selectedId
        Box(
            Modifier.fillMaxWidth().padding(vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (sel) InkBlack else MaterialTheme.colorScheme.surface)
                .pressScale { selectedId = r.id }
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = sel, onClick = { selectedId = r.id },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = BrandOrange,
                        unselectedColor = InkGray400,
                    ))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(r.title, style = MaterialTheme.typography.titleMedium,
                        color = if (sel) PaperWhite else InkBlack,
                        fontWeight = FontWeight.SemiBold)
                    Text("${r.targetJob} ・ ${r.lastEdited}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (sel) InkGray300 else InkGray500)
                }
            }
        }
    }
}

@Composable
private fun Step5Generate() {
    Text("客製版本已生成", style = MaterialTheme.typography.headlineMedium,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("AI 根據 JD 重組這份履歷,每段都標註了改寫原因",
        color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(20.dp))

    val items = listOf(
        Triple("自我介紹",
            "資管系大三,具備 SQL + Excel 量化分析能力,曾將業務週報流程從 6 小時優化至 1.5 小時。",
            "JD 強調量化分析,因此把實習中的時間節省幅度提前"),
        Triple("工作經驗 — 電商實習",
            "・用 SQL 從千萬筆訂單中拆解品類銷售趨勢,提供業務週報\n・建立自動化模板,後續同事可直接套用",
            "JD 提到「跨團隊」,所以強調工作成果被其他人沿用"),
    )
    items.forEachIndexed { idx, (title, body, reason) ->
        StaggeredAppear(delayMillis = idx * 100) {
            WhiteCard(modifier = Modifier.padding(vertical = 6.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall,
                    color = BrandOrange, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium, color = InkBlack)
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(BrandPeach.copy(alpha = 0.4f)).padding(10.dp)
                ) {
                    Row {
                        Icon(Icons.Outlined.Info, contentDescription = null,
                            tint = BrandDeepOrange, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("為什麼這樣寫:$reason",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandDeepOrange)
                    }
                }
            }
        }
    }
}
