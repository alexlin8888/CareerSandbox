package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroSection()
            Spacer(Modifier.height(20.dp))
            AnimatedSection(visible = visible, delayMs = 0) {
                StatsRow()
            }
            Spacer(Modifier.height(18.dp))
            AnimatedSection(visible = visible, delayMs = 120) {
                BentoActions(navController)
            }
            Spacer(Modifier.height(24.dp))
            AnimatedSection(visible = visible, delayMs = 240) {
                JobApplicationsSection(navController)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing),
            ),
    ) {
        content()
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

/** Stats:左大數字 + 右環圈 + 底 3 mini cards(完全沿用) */
@Composable
private fun StatsRow() {
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }
    val totalJobs = MockData.jobApplications.size
    val submitRate = if (totalJobs > 0) totalSubmitted.toFloat() / totalJobs else 0f
    val submitPct = (submitRate * 100).toInt()

    val animSubmitted by animateIntAsState(
        targetValue = totalSubmitted,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "submitted",
    )
    val animPct by animateIntAsState(
        targetValue = submitPct,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "pct",
    )
    val animRing by animateFloatAsState(
        targetValue = submitRate,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "ring",
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animSubmitted.toString(),
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    lineHeight = 56.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text("已投遞職缺", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text("這週你做了 $totalSubmitted 件事", color = InkGray500, fontSize = 11.sp)
            }
            SubmitRateRing(
                percent = animPct,
                progress = animRing,
                modifier = Modifier.size(90.dp),
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStatCard(label = "職缺", value = totalJobs.toString(), modifier = Modifier.weight(1f))
            MiniStatCard(label = "版本", value = totalVersions.toString(), modifier = Modifier.weight(1f))
            MiniStatCard(label = "技能", value = MockData.masterResume.totalSkills.toString(), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SubmitRateRing(percent: Int, progress: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 6.dp.toPx()
            val diameter = size.minDimension - strokeWidthPx
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = InkGray200,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
            if (progress > 0f) {
                drawArc(
                    color = BrandDeepOrange,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(1.dp))
            Text("投遞率", color = InkGray500, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun MiniStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(label, color = InkGray500, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Text(value, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 18.sp)
    }
}

/**
 * ===== v12 BENTO =====
 * MASTER 全寬大卡 + 4 個透明工具(無圓框,icon 全 BrandDeepOrange)
 */
@Composable
private fun BentoActions(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        BentoMaster(onClick = { navController.navigate(Routes.RESUME_PROFILE) })
        Spacer(Modifier.height(4.dp))
        ToolStrip(navController)
    }
}

@Composable
private fun BentoMaster(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BrandDeepOrange)
            .pressScale(onClick = onClick),
    ) {
        // 背景圓
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.08f)),
        )
        // ReadingFigure (right side)
        Image(
            painter = painterResource(R.drawable.undraw_reading_a_book_4cap),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = 4.dp)
                .size(140.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
        // 文字 (left side)
        Column(modifier = Modifier.align(Alignment.CenterStart).padding(start = 18.dp).fillMaxWidth(0.55f)) {
            Text("MASTER",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp)
            Spacer(Modifier.height(6.dp))
            Text("檢視母版",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                lineHeight = 28.sp)
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaperWhite.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text("${MockData.masterResume.totalExperiences} 段經歷",
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(5.dp))
                Icon(
                    Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(13.dp),
                )
            }
        }
    }
}

@Composable
private fun ToolStrip(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 6.dp),
    ) {
        ToolButton(Icons.Outlined.Edit, "編輯", Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_EDITOR)
        }
        ToolButton(Icons.Outlined.Explore, "職涯探索", Modifier.weight(1f)) {
            navController.navigate(Routes.CAREER_EXPLORATION)
        }
        ToolButton(Icons.Outlined.FileUpload, "PDF 匯出", Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_UPLOAD_PROCESSING)
        }
        ToolButton(Icons.Outlined.Analytics, "適配 78%", Modifier.weight(1f)) {
            navController.navigate(Routes.FIT_ANALYSIS)
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .pressScale(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(icon, contentDescription = label, tint = BrandDeepOrange, modifier = Modifier.size(26.dp))
        Text(label, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

/** 針對職缺 — 加 count-up 動畫 */
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

        Spacer(Modifier.height(12.dp))

        MockData.jobApplications.forEachIndexed { idx, job ->
            JobProgressCard(job, animDelayMs = idx * 80) {
                navController.navigate(Routes.jobApplicationDetail(job.id))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun JobProgressCard(
    job: JobApplication,
    animDelayMs: Int = 0,
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

    val animScore by animateIntAsState(
        targetValue = job.matchScore,
        animationSpec = tween(1200, delayMillis = 300 + animDelayMs, easing = FastOutSlowInEasing),
        label = "${job.id}-score",
    )
    val animBar by animateFloatAsState(
        targetValue = job.matchScore / 100f,
        animationSpec = tween(1100, delayMillis = 400 + animDelayMs, easing = FastOutSlowInEasing),
        label = "${job.id}-bar",
    )

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
                Text(job.position, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(3.dp))
                Text("${job.company} · $latestLabel", color = InkGray500, fontSize = 13.sp, lineHeight = 16.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$animScore", color = matchColor, fontWeight = FontWeight.Black, fontSize = 44.sp, lineHeight = 44.sp)
                Text("適配度", color = matchColor.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        AnimatedProgressBar(progress = animBar, accent = matchColor)

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            MetaItem(value = "${job.versions.size}", label = "版本")
            Spacer(Modifier.width(16.dp))
            MetaItem(value = "$submittedCount", label = "投遞")
            Spacer(Modifier.weight(1f))
            Text(latestDate, color = InkGray400, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MetaItem(value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, color = InkGray700, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.width(3.dp))
        Text(label, color = InkGray400, fontSize = 10.sp)
    }
}

@Composable
private fun AnimatedProgressBar(progress: Float, accent: Color) {
    Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
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
