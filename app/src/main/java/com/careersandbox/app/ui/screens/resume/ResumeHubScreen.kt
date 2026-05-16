package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.data.model.Resume
import com.careersandbox.app.data.model.ResumeVersion
import com.careersandbox.app.data.model.VersionStatus
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@Composable
fun ResumeHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeaderSection()
            MasterSection(navController, MockData.masterResume)
            DividerLine()
            JobApplicationsSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "履歷",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = (-0.5).sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "一份完整資料 · 針對職缺生衍生版",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        // 小人插畫(線上履歷)
        Image(
            painter = painterResource(R.drawable.undraw_feedback_ebmx),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(86.dp),
        )
    }
}

@Composable
private fun MasterSection(navController: NavHostController, master: Resume) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            "母版",
            color = InkGray400,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "我的完整資料",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            letterSpacing = (-0.3).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "${master.totalExperiences} 段經歷 · ${master.totalSkills} 項技能 · ${master.lastEdited}更新",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))

        // Text-link 操作列
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            TextLinkAction("檢視") {
                navController.navigate(Routes.RESUME_PROFILE)
            }
            TextLinkAction("編輯") {
                navController.navigate(Routes.RESUME_EDITOR)
            }
            TextLinkAction("PDF 匯入") {
                navController.navigate(Routes.RESUME_UPLOAD_PROCESSING)
            }
            Spacer(Modifier.weight(1f))
            // 經歷網入口
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlowPurple.copy(alpha = 0.1f))
                    .pressScale {
                        navController.navigate(Routes.EXPERIENCE_NETWORK)
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.AccountTree,
                    contentDescription = null,
                    tint = GlowPurple,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "經歷網",
                    color = GlowPurple,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TextLinkAction(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.pressScale(onClick = onClick),
    ) {
        Text(
            label,
            color = BrandDeepOrange,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(0.5.dp)
            .background(InkGray200),
    )
}

@Composable
private fun JobApplicationsSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "針對職缺",
                color = InkGray400,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                modifier = Modifier.weight(1f),
            )
            TextLinkAction("+ 新增職缺") {
                navController.navigate(Routes.NEW_JOB_APPLICATION)
            }
        }
        Spacer(Modifier.height(16.dp))

        MockData.jobApplications.forEachIndexed { index, job ->
            JobRow(
                job = job,
                defaultExpanded = index == 0, // 第一個預設展開
                navController = navController,
            )
            if (index != MockData.jobApplications.lastIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                        .height(0.5.dp)
                        .background(InkGray200)
                )
            }
        }
    }
}

@Composable
private fun JobRow(
    job: JobApplication,
    defaultExpanded: Boolean,
    navController: NavHostController,
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    val matchColor = when {
        job.matchScore >= 75 -> AccentGreen
        job.matchScore >= 50 -> BrandAmber
        else -> InkGray400
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale { expanded = !expanded },
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                job.position,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                "適配 ${job.matchScore}",
                color = matchColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "@ ${job.company} · ${job.versions.size} 版本",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = InkGray400,
                modifier = Modifier.size(18.dp),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 14.dp, start = 4.dp)
                    .fillMaxWidth(),
            ) {
                Row {
                    // 左邊時間軸線
                    Box(
                        modifier = Modifier
                            .width(0.5.dp)
                            .fillMaxHeight()
                            .background(InkGray200),
                    )
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        job.versions.forEach { ver ->
                            VersionLine(ver, job.id, navController)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                // 進入職缺詳情
                Row(
                    modifier = Modifier
                        .padding(start = 18.dp)
                        .pressScale {
                            navController.navigate(Routes.jobApplicationDetail(job.id))
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "管理所有版本",
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = BrandDeepOrange,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionLine(
    ver: ResumeVersion,
    jobId: String,
    navController: NavHostController,
) {
    val (statusColor, statusText) = when (ver.status) {
        VersionStatus.SUBMITTED -> AccentGreen to "已投遞"
        VersionStatus.EDITING -> InkBlack to "編輯中"
        VersionStatus.DRAFT -> InkGray500 to "草稿"
        VersionStatus.ARCHIVED -> InkGray400 to "封存"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .pressScale {
                navController.navigate(Routes.RESUME_PROFILE)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "v${ver.versionNumber}",
            color = InkGray400,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(28.dp),
        )
        Text(
            statusText,
            color = statusColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (ver.status == VersionStatus.SUBMITTED) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            if (ver.status == VersionStatus.SUBMITTED) ver.submittedAt else ver.createdAt,
            color = InkGray400,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
