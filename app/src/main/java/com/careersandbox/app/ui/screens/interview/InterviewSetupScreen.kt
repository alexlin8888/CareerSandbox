package com.careersandbox.app.ui.screens.interview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

private data class Interviewer(val name: String, val angle: String, val drawable: Int)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupScreen(navController: NavHostController) {
    val jobs = MockData.jobApplications
    var selectedJobId by remember { mutableStateOf(jobs.firstOrNull()?.id ?: "custom") }
    var customRole by remember { mutableStateOf("") }
    var customCompany by remember { mutableStateOf("") }
    var customSeniority by remember { mutableStateOf("新鮮人") }
    var customIndustry by remember { mutableStateOf("") }
    var customJd by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("single") }   // single | panel
    var difficulty by remember { mutableStateOf(InterviewConfig.difficulty) }
    var showWarmup by remember { mutableStateOf(false) }
    var round by remember { mutableStateOf(InterviewConfig.round) }
    var language by remember { mutableStateOf(InterviewConfig.language) }
    var type by remember { mutableStateOf(InterviewConfig.type) }
    val isCustom = selectedJobId == "custom"

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("個人面試設定", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(PaperWhite).padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkBlack)
                        .pressScale {
                            InterviewConfig.round = round
                            InterviewConfig.language = language
                            InterviewConfig.type = type
                            InterviewConfig.difficulty = difficulty
                            if (isCustom) {
                                InterviewConfig.customRole = customRole
                                InterviewConfig.customCompany = customCompany
                                InterviewConfig.customSeniority = customSeniority
                                InterviewConfig.customIndustry = customIndustry
                                InterviewConfig.customJd = customJd
                            }
                            showWarmup = true
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (format == "panel") "開始 panel 面試" else "開始一對一面試",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Text("為哪個職缺練?",
                color = InkBlack, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text("面試官會用這個職缺的 JD 跟你的履歷出題。",
                color = InkGray500, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            jobs.forEach { job ->
                JobTargetCard(job = job, selected = selectedJobId == job.id) {
                    selectedJobId = job.id
                }
                Spacer(Modifier.height(10.dp))
            }
            CustomJobCard(
                selected = isCustom,
                role = customRole, onRole = { customRole = it },
                company = customCompany, onCompany = { customCompany = it },
                seniority = customSeniority, onSeniority = { customSeniority = it },
                industry = customIndustry, onIndustry = { customIndustry = it },
                jd = customJd, onJd = { customJd = it },
                onSelect = { selectedJobId = "custom" },
            )

            Spacer(Modifier.height(28.dp))

            SectionLabel("面試形式")
            Spacer(Modifier.height(10.dp))
            FormatSegmented(format) { format = it }
            Spacer(Modifier.height(10.dp))
            Text(
                if (format == "panel")
                    "HR、技術、用人主管三位輪流問,從不同角度評估你。"
                else
                    "一位面試官,從你的履歷與 JD 出題、即時追問。",
                color = InkGray500, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp,
            )

            Spacer(Modifier.height(20.dp))

            RoomPreview(format)

            Spacer(Modifier.height(28.dp))

            SectionLabel("難度")
            Spacer(Modifier.height(10.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("新手", "中等", "困難").forEach {
                    PillChip(it, selected = it == difficulty) { difficulty = it }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionLabel("輪次")
            Spacer(Modifier.height(10.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("初試", "複試", "主管面").forEach {
                    PillChip(it, selected = it == round) { round = it }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionLabel("面試類型")
            Spacer(Modifier.height(10.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("行為", "技術", "情境").forEach {
                    PillChip(it, selected = it == type) { type = it }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                when (type) {
                    "技術" -> "追問會偏向工具、查錯與驗證,講得出細節才算會。"
                    "情境" -> "丟突發狀況給你:衝突指令、資源砍半、上線前爆雷。"
                    else -> "從你的經歷出題,追 STAR 的細節與反思。"
                },
                color = InkGray500, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp,
            )

            Spacer(Modifier.height(28.dp))

            SectionLabel("語言")
            Spacer(Modifier.height(10.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("中文", "English").forEach {
                    PillChip(it, selected = it == language) { language = it }
                }
            }
            if (language == "English") {
                Spacer(Modifier.height(10.dp))
                Text("面試官全程用英文提問,你的回答也請用英文。",
                    color = InkGray500, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showWarmup) {
        val roundLabel = when (round) {
            "初試" -> "初試 · 暖身關"
            "複試" -> "複試 · 深入問經歷"
            else -> "主管面 · 壓力測試"
        }
        Dialog(onDismissRequest = { showWarmup = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PaperWhite)
                    .padding(24.dp),
            ) {
                Text("準備好了嗎", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandPeach.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) { Text(roundLabel, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(16.dp))
                WarmupLine("這場大概問 3-4 題,答錯不扣分,這裡只是練習。")
                WarmupLine("講經歷時想一下 STAR:情境、任務、行動、結果。")
                WarmupLine("不用搶快,先深呼吸一口,想好再開口。")
                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(InkBlack)
                        .pressScale {
                            showWarmup = false
                            navController.navigate(
                                if (format == "panel") Routes.INTERVIEW_LIVE_PANEL
                                else Routes.INTERVIEW_LIVE_INDIVIDUAL
                            ) { popUpTo(Routes.INTERVIEW_HUB) }
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("開始", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp) }
                Spacer(Modifier.height(8.dp))
                Text("再看一下設定", color = InkGray500, fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .pressScale { showWarmup = false }
                        .padding(8.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = InkGray500, style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
}

@Composable
private fun JobTargetCard(job: JobApplication, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BrandOrange.copy(alpha = 0.12f) else InkGray100)
            .pressScale(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(20.dp).clip(CircleShape)
                .background(if (selected) BrandOrange else InkGray300),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(job.position,
                color = if (selected) BrandDeepOrange else InkBlack,
                fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(job.company, color = InkGray500, style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${job.matchScore}",
                color = BrandOrange, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Text("契合", color = InkGray400, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun CustomJobCard(
    selected: Boolean,
    role: String, onRole: (String) -> Unit,
    company: String, onCompany: (String) -> Unit,
    seniority: String, onSeniority: (String) -> Unit,
    industry: String, onIndustry: (String) -> Unit,
    jd: String, onJd: (String) -> Unit,
    onSelect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BrandPeach.copy(alpha = 0.25f) else InkGray50)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) BrandOrange else InkGray200,
                shape = RoundedCornerShape(16.dp),
            )
            .pressScale(onClick = onSelect)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(20.dp).clip(CircleShape)
                    .background(if (selected) BrandOrange else InkGray300),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text("自訂職位",
                color = if (selected) BrandDeepOrange else InkBlack,
                fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            if (selected) {
                val done = listOf(role, company, industry, jd).count { it.isNotBlank() } + 1
                Text("$done / 5",
                    color = InkGray400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (selected) {
            Spacer(Modifier.height(14.dp))
            CtxField("職位名稱", role, onRole, "例:資料分析師")
            Spacer(Modifier.height(10.dp))
            CtxField("公司(選填)", company, onCompany, "例:某電商新創")
            Spacer(Modifier.height(10.dp))
            Text("資歷", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("新鮮人", "1-3年", "資深").forEach { lv ->
                    val on = seniority == lv
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(if (on) InkBlack else InkGray100)
                            .pressScale { onSeniority(lv) }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                    ) {
                        Text(lv, color = if (on) PaperWhite else InkGray700,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            CtxField("產業(選填)", industry, onIndustry, "例:電商、金融、教育")
            Spacer(Modifier.height(10.dp))
            CtxField("貼上 JD(選填,越完整題目越準)", jd, onJd,
                "把職缺描述貼進來,面試官會照著出題", multiline = true)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lightbulb, contentDescription = null,
                    tint = BrandAmber, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("填得越多,面試官的問題就越貼近這個職位。",
                    color = InkGray500, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun CtxField(
    label: String, value: String, onChange: (String) -> Unit,
    hint: String, multiline: Boolean = false,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            if (value.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(5.dp).clip(CircleShape).background(AccentGreen))
            }
        }
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = !multiline, minLines = if (multiline) 3 else 1,
            placeholder = { Text(hint, color = InkGray400, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange, unfocusedBorderColor = InkGray300,
            ),
        )
    }
}

@Composable
private fun FormatSegmented(format: String, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentButton("一對一", format == "single", Modifier.weight(1f)) { onChange("single") }
        SegmentButton("主管 panel", format == "panel", Modifier.weight(1f)) { onChange("panel") }
    }
}

@Composable
private fun SegmentButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) PaperWhite else Color.Transparent)
            .pressScale(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text,
            color = if (selected) BrandDeepOrange else InkGray500,
            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun RoomPreview(format: String) {
    val panel = listOf(
        Interviewer("HR 主管", "人格特質 · 文化適配", R.drawable.interviewer_hr),
        Interviewer("技術主管", "專業深度", R.drawable.interviewer_tech),
        Interviewer("用人主管", "即戰力 · 整體評估", R.drawable.interviewer_lead),
    )
    val single = listOf(
        Interviewer("面試官", "從履歷與 JD 出題", R.drawable.interviewer_lead),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(InkBlack, InkGray700)))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
            Spacer(Modifier.width(6.dp))
            Text("面試房間預覽",
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(if (format == "panel") "3 位面試官" else "1 位面試官",
                color = PaperWhite.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(16.dp))
        AnimatedContent(
            targetState = format,
            transitionSpec = {
                (fadeIn(tween(300)) + scaleIn(initialScale = 0.9f, animationSpec = tween(300)))
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "room",
        ) { f ->
            val people = if (f == "panel") panel else single
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                people.forEach { p ->
                    InterviewerTile(p, big = people.size == 1, modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PaperWhite.copy(alpha = 0.08f))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(24.dp).clip(CircleShape).background(BrandOrange),
                contentAlignment = Alignment.Center) {
                Text("你", color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(8.dp))
            Text("你會在這場面試裡回答他們的提問",
                color = PaperWhite.copy(alpha = 0.75f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun InterviewerTile(p: Interviewer, big: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite.copy(alpha = 0.06f))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(p.drawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(if (big) 116.dp else 78.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(p.name, color = PaperWhite, fontWeight = FontWeight.Bold,
            fontSize = if (big) 15.sp else 13.sp)
        Spacer(Modifier.height(2.dp))
        Text(p.angle, color = PaperWhite.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center)
    }
}


@Composable
private fun WarmupLine(text: String) {
    Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.padding(top = 5.dp).size(6.dp).clip(CircleShape).background(BrandOrange))
        Spacer(Modifier.width(10.dp))
        Text(text, color = InkGray700, fontSize = 13.sp, lineHeight = 20.sp)
    }
}
