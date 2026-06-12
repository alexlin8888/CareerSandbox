package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    // #25 / #11:第一次進來先用教學卡介紹四大功能與「母版」概念,再進入填資料表單
    var showIntro by remember { mutableStateOf(true) }
    if (showIntro) {
        OnboardingIntro(onStart = { showIntro = false })
    } else {
        OnboardingForm(onDone = onDone)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun OnboardingForm(onDone: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    val total = 4
    val interests = remember { mutableStateListOf<String>() }
    val skillsHave = remember { mutableStateListOf<String>() }
    val skillsWant = remember { mutableStateListOf<String>() }
    var name by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(PaperOff).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(56.dp))
        // 進度
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { i ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50))
                        .background(if (i + 1 <= step) BrandOrange else InkGray200)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Step $step / $total", style = MaterialTheme.typography.labelSmall, color = InkGray400)

        Spacer(Modifier.height(28.dp))

        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "step",
        ) { current ->
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (current) {
                    1 -> Step1(name, { name = it }, school, { school = it },
                        dept, { dept = it }, year, { year = it })
                    2 -> Step2(interests)
                    3 -> Step3(skillsHave, skillsWant)
                    4 -> Step4()
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            if (step > 1) {
                SecondaryButton(text = "上一步", onClick = { step-- },
                    modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
            }
            PrimaryDarkButton(
                text = if (step == total) "開始使用" else "下一步",
                onClick = { if (step < total) step++ else onDone() },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Step1(
    name: String, onName: (String) -> Unit,
    school: String, onSchool: (String) -> Unit,
    dept: String, onDept: (String) -> Unit,
    year: String, onYear: (String) -> Unit,
) {
    Text("先認識你", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("這些只用來推薦合適內容", color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(32.dp))
    OnboardField("姓名", name, onName)
    OnboardField("學校", school, onSchool)
    OnboardField("系所", dept, onDept)
    OnboardField("年級", year, onYear)
}

@Composable
private fun OnboardField(label: String, value: String, onChange: (String) -> Unit) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = InkGray700, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InkBlack,
                unfocusedBorderColor = InkGray200,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step2(selected: MutableList<String>) {
    Text("你想探索的方向", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("選 3-5 個,可以隨時改 ・ 已選 ${selected.size}/5",
        color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MockData.jobInterests.forEach { item ->
            PillChip(label = item, selected = item in selected, onClick = {
                if (item in selected) selected.remove(item)
                else if (selected.size < 5) selected.add(item)
            })
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step3(skillsHave: MutableList<String>, skillsWant: MutableList<String>) {
    Text("你會什麼", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("先盤點手上有的", color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))
    Text("我擅長", style = MaterialTheme.typography.titleMedium,
        color = InkBlack, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MockData.skills.forEach { s ->
            PillChip(s, selected = s in skillsHave) {
                if (s in skillsHave) skillsHave.remove(s) else skillsHave.add(s)
            }
        }
    }
    Spacer(Modifier.height(22.dp))
    Text("我想學", style = MaterialTheme.typography.titleMedium,
        color = InkBlack, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MockData.skills.forEach { s ->
            PillChip(s, selected = s in skillsWant) {
                if (s in skillsWant) skillsWant.remove(s) else skillsWant.add(s)
            }
        }
    }
}

@Composable
private fun Step4() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.beaver_thumbsup),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("準備好了", style = MaterialTheme.typography.headlineLarge,
                color = InkBlack, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("根據你的選擇,以下幾件事可以先做",
                color = InkGray500, style = MaterialTheme.typography.bodyMedium)
        }
    }
    Spacer(Modifier.height(24.dp))

    val items = listOf(
        "01" to ("先試試模擬一場面試" to "用團體面試體驗看看會怎樣"),
        "02" to ("12 個適合你的職位" to "可以從職場沙盒裡看真實樣貌"),
        "03" to ("先寫一份履歷草稿" to "從你過去的經驗開始"),
    )
    items.forEachIndexed { idx, (no, content) ->
        StaggeredAppear(delayMillis = idx * 100) {
            WhiteCard(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(no, style = MaterialTheme.typography.displaySmall,
                        color = BrandOrange, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(content.first, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold, color = InkBlack)
                        Spacer(Modifier.height(2.dp))
                        Text(content.second, style = MaterialTheme.typography.bodySmall,
                            color = InkGray500)
                    }
                }
            }
        }
    }
}

/* ===================== #25 / #11 首次登入教學卡 ===================== */

@Composable
private fun OnboardingIntro(onStart: () -> Unit) {
    val totalCards = 5
    var card by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperOff)
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))
        // 跳過介紹
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                "跳過介紹",
                color = InkGray400,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.pressScale { onStart() }.padding(8.dp),
            )
        }

        AnimatedContent(
            targetState = card,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "introCard",
        ) { c ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when (c) {
                    0 -> IntroBeaverCard(
                        beaver = R.drawable.beaver_wave,
                        title = "歡迎來到 CareerSandbox",
                        highlight = null,
                        body = "履歷、模擬面試、職涯探索、職場體驗,在同一個地方把求職一次準備好。",
                    )
                    1 -> IntroBeaverCard(
                        beaver = R.drawable.beaver_resume,
                        title = "先建一份「母版」",
                        highlight = "母版 = 你最完整的綜合履歷",
                        body = "把所有經歷、技能、作品都放進去,先不為任何公司修飾。之後的客製版本,都從這份母版長出來。",
                    )
                    2 -> IntroTierCard()
                    3 -> IntroBeaverCard(
                        beaver = R.drawable.beaver_celebrate,
                        title = "跟 AI 面試官練習",
                        highlight = "想練幾分鐘,都可以",
                        body = "可以調面試官的強度、貼上 JD、選擇要給他看哪一份履歷版本。練完拿到分面向的回饋:內容、結構、表達。",
                    )
                    4 -> IntroBeaverCard(
                        beaver = R.drawable.beaver_search,
                        title = "用自己的話找方向",
                        highlight = "重點是你「還缺什麼」",
                        body = "還不確定想要什麼工作?用一句話描述你在意的事,系統幫你收斂、清楚標出你還缺哪些技能。選定後,進職場沙盒提前體驗那份工作的一天。",
                    )
                }
            }
        }

        // 進度圓點
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(totalCards) { i ->
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (i == card) 9.dp else 7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (i == card) BrandOrange else InkGray200),
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            if (card > 0) {
                SecondaryButton(
                    text = "上一步",
                    onClick = { card-- },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
            }
            PrimaryDarkButton(
                text = if (card == totalCards - 1) "開始建立我的資料" else "下一步",
                onClick = { if (card < totalCards - 1) card++ else onStart() },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IntroBeaverCard(
    beaver: Int,
    title: String,
    highlight: String?,
    body: String,
) {
    Image(
        painter = painterResource(beaver),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(160.dp),
    )
    Spacer(Modifier.height(28.dp))
    Text(
        title,
        color = InkBlack,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
    )
    if (highlight != null) {
        Spacer(Modifier.height(16.dp))
        HighlighterText(
            text = highlight,
            highlightColor = BrandAmber,
            textColor = InkBlack,
            fontSize = 16,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(Modifier.height(16.dp))
    Text(
        body,
        color = InkGray500,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp),
    )
}

@Composable
private fun IntroTierCard() {
    Image(
        painter = painterResource(R.drawable.beaver_present),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentScale = ContentScale.Fit,
    )
    Spacer(Modifier.height(16.dp))
    Text(
        "再依職缺客製版本",
        color = InkBlack,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        "同一份母版,針對不同公司強調不同經歷。每個版本可以單獨標記投遞狀態。",
        color = InkGray500,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp),
    )
    Spacer(Modifier.height(28.dp))

    // 母版
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BrandAmber)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("母版", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                "完整綜合履歷(不修飾)",
                color = BrandDeepOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

    // 手繪箭頭往下
    HandDrawnArrow(
        modifier = Modifier.height(40.dp).width(64.dp),
        color = BrandDeepOrange,
        strokeWidth = 2.5f,
    )

    // 兩個版本
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VersionMini(
            modifier = Modifier.weight(1f),
            company = "台積電 PM 版",
            accent = AccentGreen,
            statusLabel = "投遞中",
        )
        VersionMini(
            modifier = Modifier.weight(1f),
            company = "新創 PM 版",
            accent = BrandOrange,
            statusLabel = "草稿",
        )
    }
}

@Composable
private fun VersionMini(
    modifier: Modifier = Modifier,
    company: String,
    accent: androidx.compose.ui.graphics.Color,
    statusLabel: String,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite)
            .padding(14.dp),
    ) {
        Column {
            Box(
                Modifier
                    .size(width = 28.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent),
            )
            Spacer(Modifier.height(10.dp))
            Text(company, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(statusLabel, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
