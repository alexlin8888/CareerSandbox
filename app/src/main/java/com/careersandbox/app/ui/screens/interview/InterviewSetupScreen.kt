package com.careersandbox.app.ui.screens.interview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

private data class Interviewer(val name: String, val angle: String, val accent: Color)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupScreen(navController: NavHostController) {
    val jobs = MockData.jobApplications
    var selectedJobId by remember { mutableStateOf(jobs.firstOrNull()?.id ?: "custom") }
    var customJob by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("single") }   // single | panel
    var difficulty by remember { mutableStateOf("中等") }
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
                            navController.navigate(Routes.INTERVIEW_LIVE_INDIVIDUAL) {
                                popUpTo(Routes.INTERVIEW_HUB)
                            }
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
                value = customJob,
                onSelect = { selectedJobId = "custom" },
                onValueChange = { customJob = it },
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

            Spacer(Modifier.height(32.dp))
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
private fun CustomJobCard(selected: Boolean, value: String, onSelect: () -> Unit, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BrandOrange.copy(alpha = 0.12f) else InkGray100)
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
        }
        if (selected) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = value, onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                placeholder = { Text("輸入職位名稱", color = InkGray400) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange, unfocusedBorderColor = InkGray300,
                ),
            )
        }
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
        Interviewer("HR 主管", "人格特質 · 文化適配", BrandAmber),
        Interviewer("技術主管", "專業深度", BrandOrange),
        Interviewer("用人主管", "即戰力 · 整體評估", BrandDeepOrange),
    )
    val single = listOf(
        Interviewer("面試官", "從履歷與 JD 出題", BrandOrange),
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
        // 頭像佔位(之後換成河狸:interviewer_hr / _tech / _lead)
        Box(
            Modifier.size(if (big) 72.dp else 56.dp).clip(CircleShape)
                .background(p.accent.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Person, contentDescription = null,
                tint = PaperWhite, modifier = Modifier.size(if (big) 38.dp else 30.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(p.name, color = PaperWhite, fontWeight = FontWeight.Bold,
            fontSize = if (big) 15.sp else 13.sp)
        Spacer(Modifier.height(2.dp))
        Text(p.angle, color = PaperWhite.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center)
    }
}
