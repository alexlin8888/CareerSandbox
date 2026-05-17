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
            StatsSection()
            Spacer(Modifier.height(28.dp))
            ActionTilesSection(navController)
            Spacer(Modifier.height(32.dp))
            JobApplicationsSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** Hero — 完全沿用 ProfileScreen 結構 */
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

/** Stats 4 數字 */
@Composable
private fun StatsSection() {
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 已投遞 — 最重要(實際成果)
        StatBlock(
            value = totalSubmitted.toString(),
            label = "已投遞",
            level = StatLevel.PRIMARY,
        )
        StatDivider()
        // 職缺 — 重要(進行中)
        StatBlock(
            value = MockData.jobApplications.size.toString(),
            label = "職缺",
            level = StatLevel.SECONDARY,
        )
        StatDivider()
        // 版本 — 次要
        StatBlock(
            value = totalVersions.toString(),
            label = "版本",
            level = StatLevel.TERTIARY,
        )
        StatDivider()
        // 技能 — 最不重要(static)
        StatBlock(
            value = MockData.masterResume.totalSkills.toString(),
            label = "技能",
            level = StatLevel.TERTIARY,
        )
    }
}

private enum class StatLevel { PRIMARY, SECONDARY, TERTIARY }

@Composable
private fun StatBlock(value: String, label: String, level: StatLevel = StatLevel.SECONDARY) {
    val (numSize, numColor, numWeight) = when (level) {
        StatLevel.PRIMARY -> Triple(36.sp, BrandDeepOrange, FontWeight.Black)
        StatLevel.SECONDARY -> Triple(26.sp, BrandOrange, FontWeight.Black)
        StatLevel.TERTIARY -> Triple(20.sp, InkGray500, FontWeight.Bold)
    }
    val labelColor = when (level) {
        StatLevel.PRIMARY -> InkBlack
        else -> InkGray500
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = numColor, fontWeight = numWeight, fontSize = numSize, lineHeight = (numSize.value + 2).sp)
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            color = labelColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (level == StatLevel.PRIMARY) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun StatDivider() {
    Box(Modifier.height(36.dp).width(0.5.dp).background(InkGray200))
}

/** 4 個 tile 格子 — B 變數 */
@Composable
private fun ActionTilesSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ActionTile("檢視母版", Icons.Outlined.Visibility, BrandOrange, Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_PROFILE)
        }
        ActionTile("編輯", Icons.Outlined.Edit, BrandDeepOrange, Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_EDITOR)
        }
        ActionTile("經歷網", Icons.Outlined.AccountTree, GlowPurple, Modifier.weight(1f)) {
            navController.navigate(Routes.EXPERIENCE_NETWORK)
        }
        ActionTile("PDF 匯入", Icons.Outlined.FileUpload, AccentGreen, Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_UPLOAD_PROCESSING)
        }
    }
}

@Composable
private fun ActionTile(
    label: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.08f))
            .pressScale(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PaperWhite),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            color = InkBlack,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** 職缺區 — C 變數,大卡片 + 進度條 */
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
        Text("${MockData.jobApplications.size} 個職缺,各自有不同版本",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        MockData.jobApplications.forEach { job ->
            JobProgressCard(job) {
                navController.navigate(Routes.jobApplicationDetail(job.id))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/**
 * 職缺卡 — 字級主次:
 * 適配度 44sp (最大,最重要) > 職位 17sp > 公司+狀態 13sp > meta 11sp
 */
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
        // 頭:左邊文字 + 右邊大適配度
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
            // 適配度 — 最大最粗,主視覺
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

        // 進度條
        ProgressBar(progress = job.matchScore / 100f, accent = matchColor)

        Spacer(Modifier.height(12.dp))

        // Meta 列 — 最小,輔助資訊
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

/** 進度條 — 用 Canvas 畫漸層 */
@Composable
private fun ProgressBar(progress: Float, accent: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
    ) {
        // 軌道
        drawRoundRect(
            color = InkGray200,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(3.dp.toPx()),
        )
        // 填充(漸層)
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
