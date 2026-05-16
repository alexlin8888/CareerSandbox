package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
            Spacer(Modifier.height(8.dp))
            MasterResumeCard(navController, MockData.masterResume)
            Spacer(Modifier.height(32.dp))
            JobApplicationsSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            "我的履歷",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            letterSpacing = (-0.5).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "一份完整母版 · 針對職缺生衍生版本",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun MasterResumeCard(navController: NavHostController, master: Resume) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BrandPeach.copy(alpha = 0.5f))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(BrandDeepOrange)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            ) {
                Text(
                    "母版",
                    color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                "上次更新 ${master.lastEdited}",
                color = InkGray500,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "我的完整履歷",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "所有經歷不修飾全部寫下 · ${master.totalExperiences} 段經歷 · ${master.totalSkills} 項技能",
            color = InkGray700,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MasterActionChip(
                icon = Icons.Outlined.Visibility,
                label = "檢視",
                onClick = { navController.navigate(Routes.RESUME_PROFILE) },
                modifier = Modifier.weight(1f),
            )
            MasterActionChip(
                icon = Icons.Outlined.Edit,
                label = "編輯",
                onClick = { navController.navigate(Routes.RESUME_EDITOR) },
                modifier = Modifier.weight(1f),
            )
            MasterActionChip(
                icon = Icons.Outlined.FileUpload,
                label = "PDF 匯入",
                onClick = { navController.navigate(Routes.RESUME_UPLOAD_PROCESSING) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MasterActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = BrandDeepOrange,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                color = InkBlack,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun JobApplicationsSection(navController: NavHostController) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "針對職缺的版本",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(InkBlack)
                    .pressScale {
                        navController.navigate(Routes.NEW_JOB_APPLICATION)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "新增職缺",
                    color = PaperWhite,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            MockData.jobApplications.forEach { job ->
                JobApplicationCard(job, navController)
                Spacer(Modifier.height(12.dp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(InkGray100.copy(alpha = 0.5f))
                    .pressScale {
                        navController.navigate(Routes.NEW_JOB_APPLICATION)
                    }
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null,
                        tint = InkGray500,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "新增職缺(貼上 JD,AI 自動生第一版)",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun JobApplicationCard(
    job: JobApplication,
    navController: NavHostController,
) {
    val matchColor = when {
        job.matchScore >= 75 -> AccentGreen
        job.matchScore >= 50 -> BrandOrange
        else -> InkGray500
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pressScale {
                navController.navigate(Routes.jobApplicationDetail(job.id))
            }
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    job.position,
                    color = InkBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "@ ${job.company}",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${job.matchScore}%",
                    color = matchColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                )
                Text(
                    "適配度",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        job.versions.forEach { ver ->
            VersionRow(ver)
        }
    }
}

@Composable
private fun VersionRow(version: ResumeVersion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(InkGray100)
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                "v${version.versionNumber}",
                color = InkGray700,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            version.status.label,
            color = InkBlack,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusBgColor(version.status))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                if (version.status == VersionStatus.SUBMITTED) version.submittedAt else version.createdAt,
                color = statusTextColor(version.status),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun statusBgColor(status: VersionStatus): Color = when (status) {
    VersionStatus.DRAFT -> InkGray100
    VersionStatus.EDITING -> BrandPeach.copy(alpha = 0.5f)
    VersionStatus.SUBMITTED -> AccentGreen.copy(alpha = 0.15f)
    VersionStatus.ARCHIVED -> InkGray100
}

private fun statusTextColor(status: VersionStatus): Color = when (status) {
    VersionStatus.DRAFT -> InkGray500
    VersionStatus.EDITING -> BrandDeepOrange
    VersionStatus.SUBMITTED -> AccentGreen
    VersionStatus.ARCHIVED -> InkGray500
}
