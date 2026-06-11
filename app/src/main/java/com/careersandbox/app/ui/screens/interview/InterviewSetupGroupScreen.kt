package com.careersandbox.app.ui.screens.interview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

private data class PeerPersona(val name: String, val trait: String, val drawable: Int)

private val allPeers = listOf(
    PeerPersona("AI-強勢", "搶話、主導節奏", R.drawable.peer_assertive),
    PeerPersona("AI-邏輯", "凡事要數據", R.drawable.peer_logical),
    PeerPersona("AI-親切", "會接住你的話", R.drawable.peer_friendly),
    PeerPersona("AI-沉默", "話少,但偶爾一針見血", R.drawable.peer_quiet),
)

private data class GroupRole(val title: String, val subtitle: String)

private val roleOptions = listOf(
    GroupRole("一般應徵者", "預設模式,公平競爭"),
    GroupRole("較資深應徵者", "其他人比你新鮮,你被預期帶話題"),
    GroupRole("較資淺應徵者", "其他人比你資深,挑戰更大"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSetupGroupScreen(navController: NavHostController) {
    val jobs = MockData.jobApplications
    var selectedJobId by remember { mutableStateOf(jobs.firstOrNull()?.id ?: "") }
    var customSelected by remember { mutableStateOf(false) }
    var customJob by remember { mutableStateOf("") }
    var groupSize by remember { mutableIntStateOf(4) } // 含你
    var interviewers by remember { mutableIntStateOf(InterviewConfig.groupInterviewers) }
    var role by remember { mutableStateOf(roleOptions[0]) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("團體面試設定", fontWeight = FontWeight.Bold, color = InkBlack) },
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
                            InterviewConfig.groupInterviewers = interviewers
                            navController.navigate(Routes.INTERVIEW_LIVE_GROUP) {
                                popUpTo(Routes.INTERVIEW_HUB)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("開始團體面試",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium)
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

            // 1. 目標職缺 — 從你的職缺帶入
            SectionLabelGroup("目標職缺")
            Spacer(Modifier.height(4.dp))
            Text("AI 應徵者會跟你搶同一個位子",
                color = InkGray400, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            jobs.forEach { job ->
                GroupJobCard(
                    job = job,
                    selected = !customSelected && selectedJobId == job.id,
                    onClick = { selectedJobId = job.id; customSelected = false },
                )
                Spacer(Modifier.height(8.dp))
            }
            GroupCustomJobCard(
                selected = customSelected,
                value = customJob,
                onSelect = { customSelected = true },
                onValueChange = { customJob = it },
            )

            Spacer(Modifier.height(28.dp))

            // 2. 小組人數
            SectionLabelGroup("小組人數")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 4, 5).forEach { n ->
                    val sel = groupSize == n
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (sel) BrandOrange else BrandOrange.copy(alpha = 0.08f))
                            .pressScale { groupSize = n }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                    ) {
                        Text("$n 人(含你)",
                            color = if (sel) PaperWhite else BrandDeepOrange,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // 3. 你的角色設定
            SectionLabelGroup("面試官")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "1 位主持", 3 to "3 位 panel").forEach { (n, label) ->
                    val sel = interviewers == n
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (sel) InkBlack else MaterialTheme.colorScheme.surface)
                            .pressScale { interviewers = n }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                    ) {
                        Text(label,
                            color = if (sel) PaperWhite else InkGray700,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                if (interviewers == 3)
                    "HR、技術、用人主管同場輪流提問,壓力更接近真實終面。"
                else
                    "一位主考官主持討論,專注在你跟其他應徵者的互動。",
                color = InkGray500, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp,
            )

            Spacer(Modifier.height(28.dp))

            SectionLabelGroup("你的角色")
            Spacer(Modifier.height(10.dp))
            roleOptions.forEach { option ->
                val sel = option == role
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (sel) BrandOrange.copy(alpha = 0.12f) else InkGray100)
                        .pressScale { role = option }
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(20.dp).clip(CircleShape)
                                .background(if (sel) BrandOrange else InkGray300),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (sel) Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(option.title,
                                color = if (sel) BrandDeepOrange else InkBlack,
                                fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(option.subtitle, color = InkGray500,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(20.dp))

            // 4. 討論室預覽 — N 位 AI 應徵者就座
            GroupRoomPreview(groupSize)

            Spacer(Modifier.height(16.dp))

            // 提醒卡(誠實語氣)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandPeach.copy(alpha = 0.3f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_calm),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("別急著搶話",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("團體面試看的是協作:先接住別人的觀點、再補上你的想法,比輾壓全場更加分。",
                        color = InkGray500, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SectionLabelGroup(text: String) {
    Text(text,
        color = InkGray500,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp)
}

@Composable
private fun GroupJobCard(job: JobApplication, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) BrandOrange.copy(alpha = 0.12f) else InkGray100)
            .pressScale(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(20.dp).clip(CircleShape)
                .background(if (selected) BrandOrange else InkGray300),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(job.position,
                color = if (selected) BrandDeepOrange else InkBlack,
                fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(2.dp))
            Text(job.company, color = InkGray500,
                style = MaterialTheme.typography.bodySmall)
        }
        Text("${job.matchScore}%",
            color = if (selected) BrandDeepOrange else InkGray400,
            fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
private fun GroupCustomJobCard(
    selected: Boolean,
    value: String,
    onSelect: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) BrandOrange.copy(alpha = 0.12f) else InkGray100)
            .pressScale(onClick = onSelect)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(20.dp).clip(CircleShape)
                    .background(if (selected) BrandOrange else InkGray300),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite))
            }
            Spacer(Modifier.width(12.dp))
            Text("自訂職缺",
                color = if (selected) BrandDeepOrange else InkBlack,
                fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        if (selected) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("例如:後端工程師 ・ LINE", color = InkGray400) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = InkGray300,
                    focusedContainerColor = PaperWhite,
                    unfocusedContainerColor = PaperWhite,
                ),
            )
        }
    }
}

@Composable
private fun GroupRoomPreview(groupSize: Int) {
    val peers = allPeers.take(groupSize - 1)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(InkCharcoal, Color(0xFF3A322C))))
            .padding(18.dp),
    ) {
        Text("討論室預覽",
            color = PaperWhite.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp)
        Spacer(Modifier.height(4.dp))
        Text("這 ${peers.size} 位會跟你搶話",
            color = PaperWhite,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp)
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            peers.forEach { p ->
                PeerTile(p, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
            Spacer(Modifier.width(6.dp))
            Text("加上你,共 $groupSize 人。主考官會在旁觀察每個人的表現。",
                color = PaperWhite.copy(alpha = 0.7f),
                fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun PeerTile(p: PeerPersona, modifier: Modifier = Modifier) {
    val appear by animateFloatAsState(targetValue = 1f, label = "peer-${p.name}")
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite.copy(alpha = 0.07f))
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .alpha(appear),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(p.drawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(p.name,
            color = PaperWhite,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(p.trait,
            color = PaperWhite.copy(alpha = 0.55f),
            fontSize = 9.sp,
            lineHeight = 12.sp,
            maxLines = 2)
    }
}
