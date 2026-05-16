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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
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
            HeroSection()
            ContentSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeroSection() {
    val master = MockData.masterResume
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }

    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        // 漸層背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF993C1D),
                            BrandDeepOrange,
                            BrandAmber,
                        )
                    )
                )
        )

        // 主內容
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // 左半:文字
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "我的",
                        color = PaperWhite.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "履歷",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${master.totalExperiences} 段經歷 · ${master.totalSkills} 技能",
                    color = BrandYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    HeroStat(MockData.jobApplications.size.toString(), "職缺")
                    HeroStat(totalVersions.toString(), "版本")
                    HeroStat(totalSubmitted.toString(), "已投遞")
                }
            }

            // 右半:小人插畫
            Image(
                painter = painterResource(R.drawable.undraw_feedback_ebmx),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(110.dp)
                    .alpha(0.9f),
            )
        }
    }
}

@Composable
private fun HeroStat(num: String, label: String) {
    Column {
        Text(
            num,
            color = PaperWhite,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            lineHeight = 26.sp,
        )
        Text(
            label,
            color = PaperWhite.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun ContentSection(navController: NavHostController) {
    // 圓角覆上 hero 的內容區
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-22).dp)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(PaperWhite)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        // 經歷網入口卡
        NetworkEntryCard {
            navController.navigate(Routes.EXPERIENCE_NETWORK)
        }

        Spacer(Modifier.height(20.dp))

        // 母版操作 pill row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionPill(
                icon = Icons.Outlined.Visibility,
                label = "檢視母版",
                bgColor = BrandDeepOrange,
                modifier = Modifier.weight(1f),
            ) { navController.navigate(Routes.RESUME_PROFILE) }
            ActionPill(
                icon = Icons.Outlined.Edit,
                label = "編輯",
                bgColor = GlowPurple,
                modifier = Modifier.weight(1f),
            ) { navController.navigate(Routes.RESUME_EDITOR) }
            ActionPill(
                icon = Icons.Outlined.FileUpload,
                label = "PDF 匯入",
                bgColor = AccentGreen,
                modifier = Modifier.weight(1f),
            ) { navController.navigate(Routes.RESUME_UPLOAD_PROCESSING) }
        }

        Spacer(Modifier.height(28.dp))

        // 職缺 section header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "針對職缺",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                "+ 新增",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.NEW_JOB_APPLICATION)
                },
            )
        }
        Spacer(Modifier.height(14.dp))

        MockData.jobApplications.forEachIndexed { idx, job ->
            JobCard(job, idx, navController)
            Spacer(Modifier.height(12.dp))
        }

        // 「+ 新增職缺」虛線卡
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BrandPeach.copy(alpha = 0.25f))
                .pressScale {
                    navController.navigate(Routes.NEW_JOB_APPLICATION)
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    tint = BrandDeepOrange,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "新增職缺",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun NetworkEntryCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GlowPurple.copy(alpha = 0.15f),
                        BrandPeach.copy(alpha = 0.5f),
                        BrandAmber.copy(alpha = 0.2f),
                    )
                )
            )
            .pressScale(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(GlowPurple.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.AccountTree,
                contentDescription = null,
                tint = GlowPurple,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "經歷關聯網",
                color = Color(0xFF26215C),
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "看你的經歷怎麼連起來",
                color = GlowPurple,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = GlowPurple,
        )
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pressScale(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PaperWhite,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            color = InkBlack,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun JobCard(
    job: JobApplication,
    index: Int,
    navController: NavHostController,
) {
    // 主題色輪換(4 色)
    val brandFamily = listOf(BrandDeepOrange, GlowPurple, AccentGreen, BrandAmber)
    val companyColor = brandFamily[index % brandFamily.size]

    val matchColor = when {
        job.matchScore >= 75 -> AccentGreen
        job.matchScore >= 50 -> BrandAmber
        else -> InkGray500
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pressScale {
                navController.navigate(Routes.jobApplicationDetail(job.id))
            }
            .padding(16.dp),
    ) {
        // Top: logo + 公司資訊
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 框中框 logo
            Box(
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(companyColor),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaperWhite),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        job.company.take(1),
                        color = companyColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    job.position,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    job.company,
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Chips
        val chips = job.jdSnippet.split("、", ",", ",").take(3).map { it.trim() }.filter { it.isNotBlank() }
        // 若分不出來就用預設 chip
        val displayChips = if (chips.size >= 2) {
            chips.take(3)
        } else {
            // 從職位 + JD 抽簡易標籤
            val defaultChips = mutableListOf<String>()
            if (job.position.contains("PM", true)) defaultChips.add("PM 跨域")
            if (job.position.contains("分析") || job.jdSnippet.contains("SQL")) defaultChips.add("資料分析")
            if (job.position.contains("UX") || job.position.contains("研究")) defaultChips.add("使用者研究")
            if (job.position.contains("實習")) defaultChips.add("實習") else defaultChips.add("Full-Time")
            defaultChips.take(3)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            displayChips.forEach { tag ->
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandPeach.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        tag,
                        color = Color(0xFF993C1D),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(InkGray200),
        )
        Spacer(Modifier.height(12.dp))

        // Footer: version dots + match score
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Version dot stack
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                job.versions.forEachIndexed { vIdx, ver ->
                    val (bg, txt) = when (ver.status) {
                        VersionStatus.SUBMITTED -> Color(0xFFC0DD97) to Color(0xFF173404)
                        VersionStatus.EDITING -> BrandAmber to Color(0xFF412402)
                        VersionStatus.DRAFT -> InkGray100 to InkGray500
                        VersionStatus.ARCHIVED -> InkGray100 to InkGray400
                    }
                    Box(
                        Modifier
                            .offset(x = if (vIdx > 0) (-10).dp * vIdx else 0.dp)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(PaperWhite),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(bg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "v${ver.versionNumber}",
                                color = txt,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
                Spacer(Modifier.width(if (job.versions.size > 1) 4.dp else 8.dp))
                Text(
                    "${job.versions.size} 版本",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            // Match score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${job.matchScore}",
                    color = matchColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                )
                Text(
                    "適配",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
