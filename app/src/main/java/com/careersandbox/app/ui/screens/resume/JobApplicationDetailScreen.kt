package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.data.model.ResumeVersion
import com.careersandbox.app.data.model.VersionStatus
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@Composable
fun JobApplicationDetailScreen(
    navController: NavHostController,
    jobId: String,
) {
    val job = MockData.jobApplications.firstOrNull { it.id == jobId }
        ?: run {
            Box(Modifier.fillMaxSize().background(PaperWhite), contentAlignment = Alignment.Center) {
                Text("找不到職缺", color = InkGray500)
            }
            return
        }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroSection(job, navController)
            Spacer(Modifier.height(24.dp))
            StatsSection(job)
            Spacer(Modifier.height(28.dp))
            JdSnippetSection(job)
            Spacer(Modifier.height(32.dp))
            VersionsSection(job, navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** Hero — 完全對齊 ResumeHub */
@Composable
private fun HeroSection(job: JobApplication, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 240,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        // 返回鍵(左上)
        Box(
            Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.2f))
                .pressScale { navController.popBackStack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite)
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .padding(top = 36.dp)
                .fillMaxWidth(0.72f),
        ) {
            Text("MY APPLICATION",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Text(job.position,
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                lineHeight = 36.sp)
            Spacer(Modifier.height(6.dp))
            Text(job.company,
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(BrandYellow)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("${job.versions.size} 個版本",
                    color = InkCharcoal,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/** 4 個數字 stat */
@Composable
private fun StatsSection(job: JobApplication) {
    val submitted = job.versions.count { it.status == VersionStatus.SUBMITTED }
    val editing = job.versions.count { it.status == VersionStatus.EDITING }
    val draft = job.versions.count { it.status == VersionStatus.DRAFT }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatBlock(job.matchScore.toString(), "適配度",
            valueColor = when {
                job.matchScore >= 75 -> AccentGreen
                job.matchScore >= 50 -> BrandOrange
                else -> InkGray400
            })
        StatDivider()
        StatBlock(submitted.toString(), "已投遞")
        StatDivider()
        StatBlock(editing.toString(), "編輯中")
        StatDivider()
        StatBlock(draft.toString(), "草稿")
    }
}

@Composable
private fun StatBlock(value: String, label: String, valueColor: Color = BrandOrange) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Spacer(Modifier.height(2.dp))
        Text(label, color = InkGray500, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun StatDivider() {
    Box(Modifier.height(36.dp).width(0.5.dp).background(InkGray200))
}

/** JD 摘要 */
@Composable
private fun JdSnippetSection(job: JobApplication) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Description,
                contentDescription = null,
                tint = BrandDeepOrange,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("JD 摘要",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            job.jdSnippet,
            color = InkBlack,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text("建立於 ${job.createdAt}",
            color = InkGray400,
            style = MaterialTheme.typography.labelSmall)
    }
}

/** 版本列表 */
@Composable
private fun VersionsSection(
    job: JobApplication,
    navController: NavHostController,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    append("所有")
                    withStyle(SpanStyle(color = BrandOrange)) { append("版本") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f),
            )
            Text("+ 新版本",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.JD_CUSTOMIZE)
                })
        }
        Spacer(Modifier.height(4.dp))
        Text("${job.versions.size} 個版本,可獨立編輯與匯出",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        job.versions.forEach { ver ->
            VersionCard(
                version = ver,
                onClick = { navController.navigate(Routes.RESUME_PROFILE) },
                onExport = { navController.navigate(Routes.pdfExportDialog(ver.id)) },
                onMarkSubmitted = { /* TODO */ },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun VersionCard(
    version: ResumeVersion,
    onClick: () -> Unit,
    onExport: () -> Unit,
    onMarkSubmitted: () -> Unit,
) {
    val (statusColor, statusText) = when (version.status) {
        VersionStatus.SUBMITTED -> AccentGreen to "已投遞"
        VersionStatus.EDITING -> BrandDeepOrange to "編輯中"
        VersionStatus.DRAFT -> InkGray500 to "草稿"
        VersionStatus.ARCHIVED -> InkGray400 to "封存"
    }
    val dateText = if (version.status == VersionStatus.SUBMITTED)
        "投遞於 ${version.submittedAt}" else "建立於 ${version.createdAt}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale(onClick = onClick)
            .padding(16.dp),
    ) {
        // 頭:版本號 + 狀態 + 日期
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 大版本號
            Text(
                "v${version.versionNumber}",
                color = statusColor.copy(alpha = 0.6f),
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                lineHeight = 32.sp,
                modifier = Modifier.width(54.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                    if (version.status == VersionStatus.SUBMITTED) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(BrandYellow)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                "完成",
                                color = InkCharcoal,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    dateText,
                    color = InkGray500,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = InkGray400,
            )
        }

        Spacer(Modifier.height(14.dp))

        // 兩個操作按鈕
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VersionActionPill(
                icon = Icons.Outlined.FileDownload,
                label = "匯出 PDF",
                accent = BrandDeepOrange,
                modifier = Modifier.weight(1f),
                onClick = onExport,
            )
            if (version.status != VersionStatus.SUBMITTED) {
                VersionActionPill(
                    icon = Icons.Outlined.Check,
                    label = "標記已投遞",
                    accent = AccentGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onMarkSubmitted,
                )
            } else {
                VersionActionPill(
                    icon = Icons.Outlined.CheckCircle,
                    label = "已投遞",
                    accent = AccentGreen,
                    isFilled = true,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun VersionActionPill(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    isFilled: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isFilled) accent else PaperWhite
            )
            .pressScale(onClick = onClick)
            .padding(vertical = 9.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isFilled) PaperWhite else accent,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(5.dp))
        Text(
            label,
            color = if (isFilled) PaperWhite else accent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
