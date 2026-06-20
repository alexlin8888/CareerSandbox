package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.JobTarget
import com.careersandbox.app.data.mock.MockResumeHierarchyProvider
import com.careersandbox.app.data.mock.ResumeMaster
import com.careersandbox.app.data.mock.ResumeVersion
import com.careersandbox.app.data.mock.SubmissionStatus
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeHierarchyScreen(navController: NavHostController) {
    val master = MockResumeHierarchyProvider.master()
    val targets = MockResumeHierarchyProvider.jobTargets()
    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("職缺與版本", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = pad.calculateTopPadding() + 4.dp, bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { MasterCard(master) }
            item {
                Column {
                    Text("職缺 (${targets.size})", color = InkBlack,
                        fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("每個應徵目標一張,點開看各版本與投遞狀態",
                        color = InkGray500, fontSize = 12.sp)
                }
            }
            items(targets, key = { it.id }) { target -> JobTargetCard(target) }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun MasterCard(master: ResumeMaster) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandDeepOrange)
            .padding(18.dp),
    ) {
        Text("母版 ・ 總表", color = PaperWhite.copy(alpha = 0.85f),
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text("${master.ownerName} 的完整履歷", color = PaperWhite,
            fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text("所有客製版本都從這份取材,它本身不投出去",
            color = PaperWhite.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 16.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill("${master.experienceCount} 段經歷")
            InfoPill("${master.skills.size} 項技能")
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperWhite.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(text, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun JobTargetCard(target: JobTarget) {
    var expanded by remember { mutableStateOf(false) }
    WhiteCard(onClick = { expanded = !expanded }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${target.title} ・ ${target.company}", color = InkBlack,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text("${target.versions.size} 個版本", color = InkGray500, fontSize = 12.sp)
            }
            target.versions.firstOrNull()?.status?.let { StatusBadge(it) }
            Spacer(Modifier.width(8.dp))
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray400,
            )
        }
        androidx.compose.animation.AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(12.dp))
                Text("這個 JD 重視", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    target.jdKeywords.forEach { kw ->
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(BrandPeach)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(kw, color = BrandDeepOrange, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
                Spacer(Modifier.height(12.dp))
                target.versions.forEachIndexed { idx, v ->
                    VersionRow(v)
                    if (idx < target.versions.size - 1) Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun VersionRow(v: ResumeVersion) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(v.label, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            StatusBadge(v.status)
        }
        Spacer(Modifier.height(3.dp))
        Text(v.note, color = InkGray500, fontSize = 12.sp, lineHeight = 16.sp)
        v.submittedDate?.let {
            Spacer(Modifier.height(2.dp))
            Text("投遞於 $it", color = InkGray400, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatusBadge(status: SubmissionStatus) {
    val color: Color = when (status) {
        SubmissionStatus.DRAFT -> InkGray400
        SubmissionStatus.SUBMITTED -> AccentBlue
        SubmissionStatus.INTERVIEWING -> BrandOrange
        SubmissionStatus.WAITING -> BrandAmber
        SubmissionStatus.REJECTED -> AccentRed
        SubmissionStatus.OFFER -> AccentGreen
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(status.label, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
