package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/**
 * Resume hub — Evora-inspired redesign.
 * Structure: orange hero (view-master CTA) -> AI overview dark card ->
 *            tool tiles -> job list (logo holder + status pill).
 * Brand kept: orange gradient hero + beaver. Submission status shown
 * per-row (pills) instead of a separate aggregate card.
 */
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
            HeroSection(navController)
            Spacer(Modifier.height(22.dp))
            AnimatedSection(visible = visible, delayMs = 0) {
                AiOverviewCard(navController)
            }
            Spacer(Modifier.height(22.dp))
            AnimatedSection(visible = visible, delayMs = 120) {
                ToolStrip(navController)
            }
            Spacer(Modifier.height(24.dp))
            AnimatedSection(visible = visible, delayMs = 200) {
                JobApplicationsSection(navController)
            }
            Spacer(Modifier.height(34.dp))
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

/* ===== HERO — orange gradient, rounded bottom, view-master CTA + beaver ===== */
@Composable
private fun HeroSection(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(258.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFF7E40), Color(0xFFF2531C), Color(0xFFD33F19)),
                ),
            ),
    ) {
        // soft corner glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 44.dp, y = (-44).dp)
                .size(220.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x33FFFFFF), Color(0x00FFFFFF)),
                    ),
                    shape = CircleShape,
                ),
        )

        Image(
            painter = painterResource(R.drawable.beaver_clipboard),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = 6.dp)
                .size(150.dp),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 60.dp, end = 24.dp)
                .fillMaxWidth(0.62f),
        ) {
            Text(
                "MY RESUME",
                color = PaperWhite.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text("履歷", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 38.sp, lineHeight = 40.sp)
            Spacer(Modifier.height(9.dp))
            Text(
                "一份母版,生出每一個投遞版本",
                color = PaperWhite.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(13.dp))
                    .background(PaperWhite)
                    .pressScale { navController.navigate(Routes.RESUME_PROFILE) }
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("檢視母版", color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(7.dp))
                Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(15.dp))
            }
        }
    }
}

/* ===== AI overview (dark card, colored score bars + suggestion) ===== */
@Composable
private fun AiOverviewCard(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(InkBlack)
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(Brush.linearGradient(listOf(BrandAmber, BrandOrange)))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            ) {
                Text("AI", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 1.sp)
            }
            Spacer(Modifier.width(9.dp))
            Text("履歷總覽", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pressScale { navController.navigate(Routes.FIT_ANALYSIS) },
            ) {
                Text("看完整分析", color = BrandAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(15.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        AiBar("內容完整度", 0.82f, "82%", AccentGreen)
        Spacer(Modifier.height(11.dp))
        AiBar("量化成果", 0.58f, "58%", BrandAmber)
        Spacer(Modifier.height(11.dp))
        AiBar("關鍵字匹配", 0.76f, "76%", BrandOrange)
        Spacer(Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "量化成果偏低——在 2 段經歷補上數字(成長率、規模、節省工時),整體說服力會明顯提升。",
                color = PaperWhite.copy(alpha = 0.62f),
                fontSize = 11.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AiBar(label: String, fraction: Float, pct: String, color: Color) {
    Column {
        Row {
            Text(label, color = PaperWhite.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(pct, color = PaperWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(PaperWhite.copy(alpha = 0.1f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(color),
            )
        }
    }
}

/* ===== tools — white tiles with warm icon chip ===== */
@Composable
private fun ToolStrip(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        ToolTile(Icons.Outlined.Edit, "我的經歷", Modifier.weight(1f)) { navController.navigate(Routes.EXPERIENCE_LIST) }
        ToolTile(Icons.Outlined.Explore, "職涯探索", Modifier.weight(1f)) { navController.navigate(Routes.CAREER_EXPLORATION) }
        ToolTile(Icons.Outlined.FileUpload, "上傳履歷", Modifier.weight(1f)) { navController.navigate(Routes.RESUME_UPLOAD_PROCESSING) }
        ToolTile(Icons.Outlined.Analytics, "適配分析", Modifier.weight(1f)) { navController.navigate(Routes.FIT_ANALYSIS) }
    }
}

@Composable
private fun ToolTile(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.pressScale(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(PaperWhite),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(BrandDeepOrange.copy(alpha = 0.09f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = label, tint = BrandDeepOrange, modifier = Modifier.size(23.dp))
            }
        }
        Spacer(Modifier.height(9.dp))
        Text(label, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

/* ===== job list — clean Evora rows (logo holder + status pill) ===== */
@Composable
private fun JobApplicationsSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
                modifier = Modifier.pressScale { navController.navigate(Routes.RESUME_HIERARCHY) },
            ) {
                Text("管理版本", color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.height(13.dp))

        if (MockData.jobApplications.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_mailbox),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.height(8.dp))
                Text("還沒有針對任何職缺", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text("從母版,為第一家公司生成客製版本", color = InkGray500, fontSize = 12.sp)
            }
        } else {
            MockData.jobApplications.forEachIndexed { idx, job ->
                JobRow(job = job, index = idx) {
                    navController.navigate(Routes.jobApplicationDetail(job.id))
                }
                Spacer(Modifier.height(11.dp))
            }
        }

        AddJobRow { navController.navigate(Routes.NEW_JOB_APPLICATION) }
    }
}

@Composable
private fun JobRow(job: JobApplication, index: Int, onClick: () -> Unit) {
    val latest = job.versions.maxByOrNull { it.versionNumber }
    val initial = job.company.firstOrNull()?.toString() ?: "?"
    val logoColors = listOf(Color(0xFFC5121C), Color(0xFF73A81B), Color(0xFF0E76BC))
    val logoColor = logoColors[index % logoColors.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(19.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(start = 13.dp, top = 13.dp, bottom = 13.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // white logo holder — real logo fetched per-company in production
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(PaperWhite)
                .border(1.5.dp, InkGray200, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(initial, color = logoColor, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(job.position, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(3.dp))
            Text("${job.company} · ${job.versions.size} 個版本", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.width(10.dp))
        when (latest?.status) {
            VersionStatus.SUBMITTED -> StatusPill("已投遞", Color(0xFFDDF3E9), Color(0xFF0E7A4F))
            VersionStatus.EDITING -> StatusPill("編輯中", Color(0xFFFCEFD3), Color(0xFFA06A00))
            VersionStatus.DRAFT -> StatusPill("草稿", InkGray100, InkGray500)
            VersionStatus.ARCHIVED -> StatusPill("封存", InkGray100, InkGray500)
            null -> StatusPill("無版本", InkGray100, InkGray500)
        }
    }
}

@Composable
private fun StatusPill(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
private fun AddJobRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(19.dp))
            .border(1.5.dp, BrandDeepOrange.copy(alpha = 0.3f), RoundedCornerShape(19.dp))
            .pressScale(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Add, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(7.dp))
        Text("新增職缺", color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
