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

private val DarkOrangeText = Color(0xFF993C1D)
private val SuccessGreenLight = Color(0xFFD1FAE5)
private val SuccessGreenText = Color(0xFF047857)

@Composable
fun ResumeHubScreen(navController: NavHostController) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroSection()
            Spacer(Modifier.height(14.dp))
            AnimatedSection(visible = visible, delayMs = 0) {
                SubmissionStatsCard()
            }
            Spacer(Modifier.height(10.dp))
            AnimatedSection(visible = visible, delayMs = 60) {
                MiniStatsRow()
            }
            Spacer(Modifier.height(14.dp))
            AnimatedSection(visible = visible, delayMs = 140) {
                BentoActions(navController)
            }
            Spacer(Modifier.height(20.dp))
            AnimatedSection(visible = visible, delayMs = 260) {
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

/** Hero — 完全沿用 31.5 */
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
            painter = painterResource(R.drawable.beaver_resume),
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

/**
 * ===== v12 投遞統計卡(白底大圓角)=====
 * Label「已投遞」+ 44sp 數字 + 件 + 編輯中/草稿 點點 + 80dp ring 標「投遞率」(誠實統計)
 */
@Composable
private fun SubmissionStatsCard() {
    val totalSubmitted = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.SUBMITTED }
    val totalEditing = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.EDITING }
    // 誠實統計:投遞率 = 已投遞版本 / 全部版本(還沒有回覆資料,就不假裝有)
    val totalDraft = MockData.jobApplications.flatMap { it.versions }
        .count { it.status == VersionStatus.DRAFT }
    val totalVersionsAll = MockData.jobApplications.sumOf { it.versions.size }
    val replyRate = if (totalVersionsAll > 0) totalSubmitted.toFloat() / totalVersionsAll else 0f
    val replyPct = (replyRate * 100).toInt()

    val animSubmitted by animateIntAsState(
        targetValue = totalSubmitted,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "submitted",
    )
    val animPct by animateIntAsState(
        targetValue = replyPct,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "pct",
    )
    val animRing by animateFloatAsState(
        targetValue = replyRate,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "ring",
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    "已投遞",
                    color = InkGray400,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$animSubmitted",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 44.sp,
                        lineHeight = 44.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("件", color = InkGray500, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DotLabel(color = BrandAmber, text = "編輯中 $totalEditing")
                    DotLabel(color = InkGray400, text = "草稿 $totalDraft")
                }
            }

            Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(110.dp)) {
                    val strokeW = 9.dp.toPx()
                    val diameter = size.minDimension - strokeW
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize = Size(diameter, diameter)
                    drawArc(
                        color = InkGray100,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round),
                    )
                    if (animRing > 0f) {
                        drawArc(
                            color = BrandDeepOrange,
                            startAngle = -90f,
                            sweepAngle = 360f * animRing.coerceIn(0f, 1f),
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$animPct%", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 24.sp, lineHeight = 24.sp)
                    Spacer(Modifier.height(3.dp))
                    Text("投遞率", color = InkGray400, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DotLabel(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(4.dp))
        Text(text, color = InkGray500, fontSize = 11.sp)
    }
}

/**
 * ===== 3 個 mini stat cards(line 106-119)=====
 */
@Composable
private fun MiniStatsRow() {
    val totalVersions = MockData.jobApplications.sumOf { it.versions.size }
    val totalJobs = MockData.jobApplications.size
    val totalSkills = MockData.masterResume.totalSkills

    Row(
        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MiniStatCard(label = "職缺", value = totalJobs, modifier = Modifier.weight(1f))
        MiniStatCard(label = "版本", value = totalVersions, modifier = Modifier.weight(1f))
        MiniStatCard(label = "技能", value = totalSkills, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MiniStatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    val animated by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "$label-count",
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite)
            .padding(horizontal = 12.dp, vertical = 11.dp),
    ) {
        Text(label, color = InkGray400, fontSize = 10.sp, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text("$animated", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp, lineHeight = 22.sp)
    }
}

/**
 * ===== v12 BENTO =====
 * MASTER 全寬大卡 + 4 個透明工具
 */
@Composable
private fun BentoActions(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
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
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.08f)),
        )
        Image(
            painter = painterResource(R.drawable.beaver_writing),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = 4.dp)
                .size(140.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
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
            Spacer(Modifier.height(4.dp))
            Text("你的基本資料、經歷、技能,組成這份完整履歷",
                color = PaperWhite.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp)
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
                Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Composable
private fun ToolStrip(navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 6.dp)) {
        ToolButton(Icons.Outlined.Edit, "我的經歷", Modifier.weight(1f)) {
            navController.navigate(Routes.EXPERIENCE_LIST)
        }
        ToolButton(Icons.Outlined.Explore, "職涯探索", Modifier.weight(1f)) {
            navController.navigate(Routes.CAREER_EXPLORATION)
        }
        ToolButton(Icons.Outlined.FileUpload, "上傳履歷", Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_UPLOAD_PROCESSING)
        }
        ToolButton(Icons.Outlined.Analytics, "適配分析", Modifier.weight(1f)) {
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

/** 針對職缺 + v12 卡片 */
@Composable
private fun JobApplicationsSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    append("針對")
                    withStyle(SpanStyle(color = BrandDeepOrange)) { append("職缺") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.NEW_JOB_APPLICATION)
                },
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "新增", tint = BrandDeepOrange, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp))
                Text("新增", color = BrandDeepOrange, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "從你的母版,為每家公司生成客製版本",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(12.dp))

        if (MockData.jobApplications.isEmpty()) {
            // 空狀態:還沒投遞任何工作(資料層接上、列表為空時自動顯示)
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_mailbox),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.height(8.dp))
                Text("還沒有投遞紀錄", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text("從履歷選一份,投出第一份申請吧", color = InkGray500, fontSize = 12.sp)
            }
        } else {
            MockData.jobApplications.forEachIndexed { idx, job ->
                JobProgressCard(
                    job = job,
                    cardIndex = idx,
                    animDelayMs = idx * 80,
                ) {
                    navController.navigate(Routes.jobApplicationDetail(job.id))
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

/**
 * ===== v12 職缺卡(line 177-218)=====
 * 白底,42dp avatar(idx=0 漸層,其他 peach),38sp BrandDeepOrange %,3 chips,4dp bar
 */
@Composable
private fun JobProgressCard(
    job: JobApplication,
    cardIndex: Int = 0,
    animDelayMs: Int = 0,
    onClick: () -> Unit,
) {
    val submittedCount = job.versions.count { it.status == VersionStatus.SUBMITTED }
    val latest = job.versions.maxByOrNull { it.versionNumber }
    val latestLabel = when (latest?.status) {
        VersionStatus.SUBMITTED -> "已投遞 v${latest.versionNumber}"
        VersionStatus.EDITING -> "編輯中 v${latest.versionNumber}"
        VersionStatus.DRAFT -> "草稿 v${latest.versionNumber}"
        VersionStatus.ARCHIVED -> "封存 v${latest.versionNumber}"
        null -> "尚無版本"
    }

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

    val avatarLetter = job.company.firstOrNull()?.toString() ?: "?"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 42dp avatar box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (cardIndex == 0) Modifier.background(Brush.linearGradient(listOf(BrandDeepOrange, BrandAmber)))
                        else Modifier.background(BrandPeach)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    avatarLetter,
                    color = if (cardIndex == 0) PaperWhite else DarkOrangeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(job.position, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(2.dp))
                Text("${job.company} · $latestLabel", color = InkGray500, fontSize = 11.sp)
            }
            // Right column: big %
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$animScore",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp,
                        lineHeight = 38.sp,
                    )
                    Text(
                        "%",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text("適配度", color = InkGray400, fontSize = 9.sp, letterSpacing = 1.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        // 3 chips:版本 / 投遞 / 狀態
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            StatusChip(text = "${job.versions.size} 版本", bg = BrandPeach, textColor = DarkOrangeText)
            StatusChip(text = "$submittedCount 投遞", bg = BrandPeach, textColor = DarkOrangeText)
            // Third chip: latest status
            when (latest?.status) {
                VersionStatus.SUBMITTED -> StatusChip(text = "已投遞", bg = SuccessGreenLight, textColor = SuccessGreenText)
                VersionStatus.EDITING -> StatusChip(text = "編輯中", bg = BrandPeach, textColor = DarkOrangeText)
                VersionStatus.DRAFT -> StatusChip(text = "草稿", bg = InkGray100, textColor = InkGray500)
                VersionStatus.ARCHIVED -> StatusChip(text = "封存", bg = InkGray100, textColor = InkGray500)
                null -> StatusChip(text = "無版本", bg = InkGray100, textColor = InkGray500)
            }
        }

        Spacer(Modifier.height(10.dp))

        // 4dp BrandDeepOrange bar
        Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
            drawRoundRect(
                color = InkGray100,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
            val w = size.width * animBar.coerceIn(0f, 1f)
            if (w > 0f) {
                drawRoundRect(
                    color = BrandDeepOrange,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, size.height),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun StatusChip(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Medium, fontSize = 10.sp)
    }
}
