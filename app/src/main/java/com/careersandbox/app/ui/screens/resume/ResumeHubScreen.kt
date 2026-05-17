package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
            StatsRow()
            Spacer(Modifier.height(24.dp))
            BentoActions(navController)
            Spacer(Modifier.height(32.dp))
            JobApplicationsSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** Hero — 完全沿用之前(不動) */
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
            Text("MY RESUME",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Text("履歷",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                lineHeight = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text("一份母版,生 N 個衍生版",
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(BrandYellow)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("${MockData.masterResume.totalExperiences} 段經歷",
                    color = InkCharcoal,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelMedium)
            }
        }

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

/** Stats:1 已投遞最大,其他小 */
@Composable
private fun StatsRow() {
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 已投遞 (PRIMARY)
        StatBlock(
            value = totalSubmitted.toString(),
            label = "已投遞",
            primary = true,
        )
        StatDivider()
        StatBlock(MockData.jobApplications.size.toString(), "職缺")
        StatDivider()
        StatBlock(totalVersions.toString(), "版本")
        StatDivider()
        StatBlock(MockData.masterResume.totalSkills.toString(), "技能")
    }
}

@Composable
private fun StatBlock(value: String, label: String, primary: Boolean = false) {
    val (numSize, numColor, numWeight) = if (primary) {
        Triple(36.sp, BrandDeepOrange, FontWeight.Black)
    } else {
        Triple(20.sp, InkGray500, FontWeight.Bold)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = numColor, fontWeight = numWeight, fontSize = numSize,
            lineHeight = (numSize.value + 2).sp)
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            color = if (primary) InkBlack else InkGray500,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (primary) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun StatDivider() {
    Box(Modifier.height(36.dp).width(0.5.dp).background(InkGray200))
}

/**
 * Bento Grid: 左大塊「檢視母版」+ 右側 4 小塊(2x2)
 */
@Composable
private fun BentoActions(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // 左:大塊「檢視母版」
        BentoMain(
            modifier = Modifier.weight(1.2f).fillMaxHeight(),
            onClick = { navController.navigate(Routes.RESUME_PROFILE) },
        )

        // 右:4 小塊 2x2
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BentoSmall(
                    icon = Icons.Outlined.Edit,
                    label = "編輯",
                    accent = BrandDeepOrange,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { navController.navigate(Routes.RESUME_EDITOR) },
                )
                BentoSmall(
                    icon = Icons.Outlined.AccountTree,
                    label = "經歷網",
                    accent = GlowPurple,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { navController.navigate(Routes.EXPERIENCE_NETWORK) },
                )
            }
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BentoSmall(
                    icon = Icons.Outlined.FileUpload,
                    label = "PDF",
                    accent = AccentGreen,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { navController.navigate(Routes.RESUME_UPLOAD_PROCESSING) },
                )
                BentoSmall(
                    icon = Icons.Outlined.Analytics,
                    label = "適配分析",
                    accent = BrandAmber,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { /* TODO 適配分析頁 */ },
                )
            }
        }
    }
}

@Composable
private fun BentoMain(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandDeepOrange)
            .pressScale(onClick = onClick)
            .padding(18.dp),
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Visibility,
                contentDescription = null,
                tint = PaperWhite,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            "檢視母版",
            color = PaperWhite,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            lineHeight = 26.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "看看完整的自己",
            color = PaperWhite.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${MockData.masterResume.totalExperiences} 段經歷",
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = PaperWhite,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun BentoSmall(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.1f))
            .pressScale(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(PaperWhite),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            label,
            color = InkBlack,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/** 針對職缺(維持上版字級層次) */
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
                fontSize = 24.sp,
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

        Spacer(Modifier.height(16.dp))

        MockData.jobApplications.forEach { job ->
            JobProgressCard(job) {
                navController.navigate(Routes.jobApplicationDetail(job.id))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun JobProgressCard(
    job: JobApplication,
    onClick: () -> Unit,
) {
    val matchColor = when {
        job.matchScore >= 75 -> AccentGreen
        job.matchScore >= 50 -> BrandOrange
        else -> InkGray400
    }
    val submittedCount = job.versions.count { it.status == VersionStatus.SUBMITTED }
    val latest = job.versions.maxByOrNull { it.versionNumber }
    val latestLabel = when (latest?.status) {
        VersionStatus.SUBMITTED -> "已投遞 v${latest.versionNumber}"
        VersionStatus.EDITING -> "編輯中 v${latest.versionNumber}"
        VersionStatus.DRAFT -> "草稿 v${latest.versionNumber}"
        VersionStatus.ARCHIVED -> "封存 v${latest.versionNumber}"
        null -> "尚無版本"
    }
    val latestDate = latest?.let {
        if (it.status == VersionStatus.SUBMITTED) it.submittedAt else it.createdAt
    } ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    job.position,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "${job.company} · $latestLabel",
                    color = InkGray500,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${job.matchScore}",
                    color = matchColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    lineHeight = 44.sp,
                )
                Text(
                    "適配度",
                    color = matchColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        ProgressBar(progress = job.matchScore / 100f, accent = matchColor)

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            MetaItem(value = "${job.versions.size}", label = "版本")
            Spacer(Modifier.width(16.dp))
            MetaItem(value = "$submittedCount", label = "投遞")
            Spacer(Modifier.weight(1f))
            Text(
                latestDate,
                color = InkGray400,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun MetaItem(value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, color = InkGray700, fontWeight = FontWeight.Bold,
            fontSize = 12.sp)
        Spacer(Modifier.width(3.dp))
        Text(label, color = InkGray400, fontSize = 10.sp)
    }
}

@Composable
private fun ProgressBar(progress: Float, accent: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
    ) {
        drawRoundRect(
            color = InkGray200,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(3.dp.toPx()),
        )
        val w = size.width * progress.coerceIn(0f, 1f)
        if (w > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(accent, accent.copy(alpha = 0.7f)),
                    endX = w,
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, size.height),
                cornerRadius = CornerRadius(3.dp.toPx()),
            )
        }
    }
}
