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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import coil.compose.AsyncImage
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.JobApplication
import com.careersandbox.app.data.model.VersionStatus
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/**
 * Resume hub — Evora-inspired redesign (final).
 * Layout: wave hero -> prominent master card (overlaps hero) ->
 *         cross-resume AI overview (read-only) -> tool tiles -> job list.
 * Company logos are fetched live as favicons (Coil) since they are the
 * company's own icon; falls back to a colored initial when no domain maps.
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
            HeroSection()
            // content lifts onto the hero bottom so the master card overlaps
            Column(modifier = Modifier.offset(y = (-28).dp)) {
                MasterCard(navController)
                Spacer(Modifier.height(22.dp))
                AnimatedSection(visible = visible, delayMs = 0) { AiOverviewCard() }
                Spacer(Modifier.height(22.dp))
                AnimatedSection(visible = visible, delayMs = 120) { ToolStrip(navController) }
                Spacer(Modifier.height(24.dp))
                AnimatedSection(visible = visible, delayMs = 200) { JobApplicationsSection(navController) }
                Spacer(Modifier.height(44.dp))
            }
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

/* ===== HERO — wave background (consistent with other pages) + beaver ===== */
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
                .padding(start = 24.dp, top = 58.dp, end = 24.dp)
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
            Text("履歷", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 38.sp, lineHeight = 40.sp)
            Spacer(Modifier.height(9.dp))
            Text(
                "一份母版,生出每一個投遞版本",
                color = PaperWhite.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Image(
            painter = painterResource(R.drawable.beaver_clipboard),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 0.dp, y = 56.dp)
                .size(148.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

/* ===== MASTER CARD — prominent entry, overlaps hero ===== */
@Composable
private fun MasterCard(navController: NavHostController) {
    val resume = MockData.masterResume
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(14.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(PaperWhite)
            .pressScale { navController.navigate(Routes.RESUME_PROFILE) }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Description, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("母版履歷", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 17.sp)
            Spacer(Modifier.height(3.dp))
            Text(
                "${resume.totalExperiences} 段經歷 · ${resume.totalSkills} 項技能 · 所有版本源頭",
                color = InkGray500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.width(10.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange)))
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("檢視", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(15.dp))
        }
    }
}

/* ===== AI overview — cross-resume analysis, read-only (no navigation) ===== */
@Composable
private fun AiOverviewCard() {
    val apps = MockData.jobApplications
    val totalVersions = apps.sumOf { it.versions.size }
    val avgMatch = if (apps.isNotEmpty()) apps.map { it.matchScore }.average().toInt() else 0
    val quantFraction = 1f / totalVersions.coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF3D2419), Color(0xFF1F1611)))),
    ) {
        // decorative warm glow, top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(190.dp)
                .offset(x = 50.dp, y = (-70).dp)
                .background(Brush.radialGradient(listOf(BrandDeepOrange.copy(alpha = 0.30f), Color.Transparent))),
        )
        Column(modifier = Modifier.padding(18.dp)) {
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
            Text(
                "綜覽 ${apps.size} 職缺 · $totalVersions 版本",
                color = PaperWhite.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(15.dp))
        AiBar("量化成果覆蓋", quantFraction, "1 / $totalVersions 版本", BrandAmber)
        Spacer(Modifier.height(11.dp))
        AiBar("平均適配度", avgMatch / 100f, "$avgMatch%", BrandOrange)

        Spacer(Modifier.height(15.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(PaperWhite.copy(alpha = 0.08f)))
        Spacer(Modifier.height(14.dp))

        Finding(
            Color(0xFF5B9BD5),
            "職缺領域分散",
            "Acer、KKday、蝦皮 橫跨硬體、旅遊、電商,各份履歷該突顯不同經歷,別共用同一版。",
        )
        Spacer(Modifier.height(13.dp))
        Finding(
            BrandAmber,
            "共同缺口:量化不足",
            "版本普遍缺具體數字。補上成長率、規模、節省工時,說服力最有感。",
        )
        Spacer(Modifier.height(13.dp))
        Finding(
            AccentGreen,
            "待補關鍵字",
            "蝦皮(UX 研究)版本缺「使用者研究」「A/B 測試」等字,與 JD 匹配偏低。",
        )
        }
    }
}

@Composable
private fun AiBar(label: String, fraction: Float, value: String, color: Color) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(label, color = PaperWhite.copy(alpha = 0.72f), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(value, color = PaperWhite.copy(alpha = 0.55f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun Finding(dotColor: Color, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(2.dp))
            Text(desc, color = PaperWhite.copy(alpha = 0.6f), fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/* ===== tools ===== */
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

/* ===== job list — real company favicons + status pill ===== */
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
    val domain = companyDomain(job.company)
    val initial = job.company.firstOrNull()?.toString() ?: "?"
    val accentColors = listOf(Color(0xFFEC9430), Color(0xFF83B81A), Color(0xFF5B9BD5))
    val accent = accentColors[index % accentColors.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(19.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(start = 13.dp, top = 13.dp, bottom = 13.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // white holder — fetches the company's own icon (favicon) at runtime
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(PaperWhite)
                .border(1.5.dp, InkGray200, RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (domain != null) {
                AsyncImage(
                    model = "https://www.google.com/s2/favicons?domain=$domain&sz=128",
                    contentDescription = job.company,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Text(initial, color = accent, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
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

/** Map a company name to a domain so we can fetch its icon. Extend as job data grows. */
private fun companyDomain(company: String): String? = when (company.trim().lowercase()) {
    "acer", "宏碁" -> "acer.com"
    "kkday" -> "kkday.com"
    "字節跳動", "bytedance" -> "bytedance.com"
    "聯發科", "mediatek" -> "mediatek.com"
    "台積電", "tsmc" -> "tsmc.com"
    "華碩", "asus" -> "asus.com"
    "google", "谷歌" -> "google.com"
    "微軟", "microsoft" -> "microsoft.com"
    "nvidia", "輝達" -> "nvidia.com"
    "蝦皮", "shopee" -> "shopee.tw"
    "line" -> "line.me"
    else -> null
}
