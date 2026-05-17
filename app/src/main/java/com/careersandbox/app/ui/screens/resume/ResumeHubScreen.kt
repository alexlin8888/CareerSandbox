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
 * C 變數職缺卡:
 * - 頭:職位 + 公司+狀態副標 + 右側大適配度數字
 * - 中:適配度進度條(漸層橘)
 * - 底:meta 列 — N 版本 / N 投遞 / 最新日期
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
        // 頭
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    job.position,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${job.company} · $latestLabel",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                "${job.matchScore}",
                color = matchColor,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                lineHeight = 28.sp,
            )
        }

        Spacer(Modifier.height(12.dp))

        // 進度條
        ProgressBar(progress = job.matchScore / 100f, accent = matchColor)

        Spacer(Modifier.height(10.dp))

        // Meta 列
        Row(verticalAlignment = Alignment.CenterVertically) {
            MetaItem(value = "${job.versions.size}", label = "版本")
            Spacer(Modifier.width(16.dp))
            MetaItem(value = "$submittedCount", label = "投遞")
            Spacer(Modifier.weight(1f))
            Text(
                latestDate,
                color = InkGray400,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun MetaItem(value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, color = InkBlack, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(4.dp))
        Text(label, color = InkGray500, style = MaterialTheme.typography.labelSmall)
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
