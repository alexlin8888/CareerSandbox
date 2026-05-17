package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.data.model.VersionStatus
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.SectionDivider
import com.careersandbox.app.ui.components.WaveHeroBackground
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
            HeroSection()
            Spacer(Modifier.height(24.dp))
            StatsSection()
            Spacer(Modifier.height(32.dp))
            ActionsSection(navController)
            Spacer(Modifier.height(36.dp))
            JobApplicationsSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** Hero 完全對齊 ProfileScreen 的設計語言 */
@Composable
private fun HeroSection() {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 260,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(0.6f),
        ) {
            Text(
                "MY RESUME",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "履歷",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                lineHeight = 40.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "一份母版,生 N 個衍生版",
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(BrandYellow)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    "${MockData.masterResume.totalExperiences} 段經歷",
                    color = InkCharcoal,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        // 插畫破框(右下) — 完全對齊 ProfileScreen 做法
        Image(
            painter = painterResource(R.drawable.undraw_feedback_ebmx),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 0.dp, y = 8.dp)
                .size(180.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

/** Stats — 對齊 ProfileScreen 的 StatsSection */
@Composable
private fun StatsSection() {
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatBlock(MockData.jobApplications.size.toString(), "職缺")
        StatDivider()
        StatBlock(totalVersions.toString(), "版本")
        StatDivider()
        StatBlock(totalSubmitted.toString(), "已投遞")
        StatDivider()
        StatBlock(MockData.masterResume.totalSkills.toString(), "技能")
    }
}

@Composable
private fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = BrandOrange, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Spacer(Modifier.height(2.dp))
        Text(label, color = InkGray500, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun StatDivider() {
    Box(
        Modifier
            .height(36.dp)
            .width(0.5.dp)
            .background(InkGray200),
    )
}

/** 4 個圓圈 icon action — 對齊首頁 QuickActions */
@Composable
private fun ActionsSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CircleAction("檢視母版", Icons.Outlined.Visibility, BrandOrange) {
            navController.navigate(Routes.RESUME_PROFILE)
        }
        CircleAction("編輯", Icons.Outlined.Edit, BrandDeepOrange) {
            navController.navigate(Routes.RESUME_EDITOR)
        }
        CircleAction("經歷網", Icons.Outlined.AccountTree, GlowPurple) {
            navController.navigate(Routes.EXPERIENCE_NETWORK)
        }
        CircleAction("PDF 匯入", Icons.Outlined.FileUpload, AccentGreen) {
            navController.navigate(Routes.RESUME_UPLOAD_PROCESSING)
        }
    }
}

@Composable
private fun CircleAction(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.pressScale(onClick = onClick),
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = InkBlack, style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)
    }
}

/** 職缺 section — 對齊首頁 ModuleSection 的 row 樣式 */
@Composable
private fun JobApplicationsSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    append("針對")
                    withStyle(SpanStyle(color = BrandOrange)) { append("職缺") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f),
            )
            Text("+ 新增",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.NEW_JOB_APPLICATION)
                })
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "${MockData.jobApplications.size} 個職缺,各自有不同版本",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(20.dp))

        val accents = listOf(BrandOrange, BrandDeepOrange, GlowPurple, AccentGreen)

        MockData.jobApplications.forEachIndexed { idx, job ->
            JobRow(
                index = idx + 1,
                accent = accents[idx % accents.size],
                job = job,
                onClick = {
                    navController.navigate(Routes.jobApplicationDetail(job.id))
                },
            )
            if (idx != MockData.jobApplications.lastIndex) {
                SectionDivider(modifier = Modifier.padding(vertical = 18.dp))
            }
        }
    }
}

/**
 * 職缺 row — 完全對齊 BorderlessModuleRow 樣式:
 * 左:大編號 / 中:標題+副標+chip / 右:圓圈 icon(顯示適配度)
 */
@Composable
private fun JobRow(
    index: Int,
    accent: Color,
    job: JobApplication,
    onClick: () -> Unit,
) {
    val matchColor = when {
        job.matchScore >= 75 -> AccentGreen
        job.matchScore >= 50 -> BrandOrange
        else -> InkGray400
    }

    Row(
        modifier = Modifier.fillMaxWidth().pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            String.format("%02d", index),
            color = accent.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            modifier = Modifier.width(48.dp),
        )

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    job.position,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Spacer(Modifier.width(8.dp))
                // 已投遞 chip(只在有已投遞版本時顯示)
                val hasSubmitted = job.versions.any { it.status == VersionStatus.SUBMITTED }
                if (hasSubmitted) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(BrandYellow)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("已投遞",
                            color = InkCharcoal,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                "${job.company} · ${job.versions.size} 版本",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        // 右側圓圈 — 顯示適配度數字(對齊首頁 icon 圓圈)
        Box(
            Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(matchColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${job.matchScore}",
                    color = matchColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    lineHeight = 17.sp,
                )
                Text(
                    "適配",
                    color = matchColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
