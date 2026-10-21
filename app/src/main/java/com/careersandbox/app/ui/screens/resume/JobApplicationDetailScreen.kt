package com.careersandbox.app.ui.screens.resume

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.JobTarget
import com.careersandbox.app.data.mock.ResumeVersion
import com.careersandbox.app.data.mock.SubmissionStatus
import com.careersandbox.app.data.mock.displayName
import com.careersandbox.app.data.repository.RemoteResumeHierarchyProvider
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * One job target and every resume version under it, read from job_targets /
 * resume_versions through RemoteResumeHierarchyProvider. [jobId] is the target's
 * server id, as the resume hub list and the customize flow pass it.
 */
@Composable
fun JobApplicationDetailScreen(
    navController: NavHostController,
    jobId: String,
) {
    // The provider is usually warm from the hub; refresh covers a cold start or a
    // deep link so the screen does not show "not found" before the list has landed.
    LaunchedEffect(Unit) {
        if (RemoteResumeHierarchyProvider.jobTargets().isEmpty()) RemoteResumeHierarchyProvider.refresh()
    }
    val targets = RemoteResumeHierarchyProvider.jobTargets()
    val loading = RemoteResumeHierarchyProvider.loading
    val job = targets.firstOrNull { it.id == jobId }

    if (job == null) {
        Box(Modifier.fillMaxSize().background(PaperWhite), contentAlignment = Alignment.Center) {
            if (loading) CircularProgressIndicator(color = BrandDeepOrange)
            else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("找不到職缺", color = InkGray500)
                Spacer(Modifier.height(12.dp))
                Text("返回", color = BrandDeepOrange, fontWeight = FontWeight.Bold,
                    modifier = Modifier.pressScale { navController.popBackStack() })
            }
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
            StatsSection(job) { navController.navigate(Routes.FIT_ANALYSIS) }
            Spacer(Modifier.height(20.dp))
            NextActionsSection(navController)
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
private fun HeroSection(job: JobTarget, navController: NavHostController) {
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
                .fillMaxWidth(),
        ) {
            Text("MY APPLICATION",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Text(job.title,
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = if (job.title.length > 14) 26.sp else 32.sp,
                lineHeight = 34.sp,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Text(job.company,
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
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

/** 4 個數字 stat — fit score (from B2, cached on the target) + counts by status */
@Composable
private fun StatsSection(job: JobTarget, onFitClick: () -> Unit) {
    val submitted = job.versions.count { it.status == SubmissionStatus.SUBMITTED }
    val interviewing = job.versions.count {
        it.status == SubmissionStatus.INTERVIEWING || it.status == SubmissionStatus.WAITING
    }
    val draft = job.versions.count { it.status == SubmissionStatus.DRAFT }
    val score = job.matchScore

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(Modifier.pressScale(onClick = onFitClick)) {
            StatBlock(
                score?.toString() ?: "—",
                "適配度",
                valueColor = when {
                    score == null -> InkGray400
                    score >= 75 -> AccentGreen
                    score >= 50 -> BrandOrange
                    else -> InkGray400
                },
            )
        }
        StatDivider()
        StatBlock(submitted.toString(), "已投遞")
        StatDivider()
        StatBlock(interviewing.toString(), "面試中")
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

/** 下一步 — 把職缺接回面試與適配分析(履歷 → 職缺 → 面試 一條線) */
@Composable
private fun NextActionsSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(InkBlack)
                .pressScale { navController.navigate(Routes.INTERVIEW_SETUP_INDIVIDUAL) }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Mic, contentDescription = null,
                tint = PaperWhite, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("練此職缺的面試", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("帶著 JD 進面試房", color = PaperWhite.copy(alpha = 0.6f), fontSize = 10.sp)
            }
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(BrandPeach.copy(alpha = 0.55f))
                .pressScale { navController.navigate(Routes.FIT_ANALYSIS) }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Analytics, contentDescription = null,
                tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("還缺什麼", color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("看你和 JD 的差距", color = BrandDeepOrange.copy(alpha = 0.65f), fontSize = 10.sp)
            }
        }
    }
}

/** JD 摘要 — first 80 chars of the stored JD, plus the keywords once extracted */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JdSnippetSection(job: JobTarget) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Description, contentDescription = null,
                tint = BrandDeepOrange, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("JD 摘要",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            job.jdSnippet.ifBlank { "這個職缺還沒有 JD,新增版本時可以補上。" },
            color = if (job.jdSnippet.isBlank()) InkGray500 else InkBlack,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
        )
        if (job.jdKeywords.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                job.jdKeywords.forEach { kw ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(BrandPeach.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(kw, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        if (job.createdAt.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text("建立於 ${job.createdAt}",
                color = InkGray400,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** 版本列表 */
@Composable
private fun VersionsSection(
    job: JobTarget,
    navController: NavHostController,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var busyId by remember { mutableStateOf<String?>(null) }
    var renaming by remember { mutableStateOf<ResumeVersion?>(null) }

    fun setStatus(ver: ResumeVersion, status: SubmissionStatus) {
        busyId = ver.id
        scope.launch {
            RemoteResumeHierarchyProvider.updateVersionStatus(ver.id, status)
                .onSuccess { Toast.makeText(ctx, "${ver.label} 已改為「${status.label}」", Toast.LENGTH_SHORT).show() }
                .onFailure { Toast.makeText(ctx, it.message ?: "更新失敗", Toast.LENGTH_SHORT).show() }
            busyId = null
        }
    }

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
            Text("+ 新增版本",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.jdCustomize(job.id))
                })
        }
        Spacer(Modifier.height(4.dp))
        Text(
            if (job.versions.isEmpty()) "還沒有版本,按「新增版本」用 AI 生第一版"
            else "${job.versions.size} 個版本,可獨立編輯與匯出",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(20.dp))

        job.versions.forEachIndexed { idx, ver ->
            VersionCard(
                version = ver,
                name = ver.displayName(idx + 1),
                busy = busyId == ver.id,
                onClick = { navController.navigate(Routes.resumeProfile(ver.id)) },
                onRename = { renaming = ver },
                onExport = { navController.navigate(Routes.pdfExportDialog(ver.id)) },
                onMarkSubmitted = { setStatus(ver, SubmissionStatus.SUBMITTED) },
                onChangeStatus = { setStatus(ver, it) },
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    renaming?.let { ver ->
        val number = job.versions.indexOfFirst { it.id == ver.id } + 1
        RenameVersionDialog(
            initial = ver.displayName(number),
            onConfirm = { newLabel ->
                renaming = null
                scope.launch {
                    RemoteResumeHierarchyProvider.renameVersion(ver.id, newLabel)
                        .onFailure { Toast.makeText(ctx, it.message ?: "重新命名失敗", Toast.LENGTH_SHORT).show() }
                }
            },
            onDismiss = { renaming = null },
        )
    }
}

private fun statusStyle(status: SubmissionStatus): Pair<Color, String> = when (status) {
    SubmissionStatus.DRAFT -> InkGray500 to "草稿"
    SubmissionStatus.SUBMITTED -> AccentGreen to "已投遞"
    SubmissionStatus.INTERVIEWING -> BrandDeepOrange to "面試中"
    SubmissionStatus.WAITING -> BrandOrange to "等回覆"
    SubmissionStatus.REJECTED -> AccentRed to "未錄取"
    SubmissionStatus.OFFER -> AccentGreen to "錄取"
}

@Composable
private fun VersionCard(
    version: ResumeVersion,
    name: String,
    busy: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onExport: () -> Unit,
    onMarkSubmitted: () -> Unit,
    onChangeStatus: (SubmissionStatus) -> Unit,
) {
    val (statusColor, statusText) = statusStyle(version.status)
    val dateText = version.submittedDate?.let { "投遞於 $it" } ?: version.note.ifBlank { "尚未投遞" }
    var menuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale(onClick = onClick)
            .padding(16.dp),
    ) {
        // 頭:版本標籤 + 狀態 + 日期
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        name,
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "重新命名",
                        tint = InkGray400,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onRename() },
                    )
                }
                Spacer(Modifier.height(4.dp))
                // Tapping the status opens a picker for all six states.
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { if (!busy) menuOpen = true }
                            .padding(end = 6.dp),
                    ) {
                        Text(statusText, color = statusColor, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Spacer(Modifier.width(4.dp))
                        if (busy) CircularProgressIndicator(color = statusColor, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Outlined.ExpandMore, contentDescription = "更改狀態", tint = statusColor, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        SubmissionStatus.values().forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.label, color = statusStyle(s).first, fontWeight = FontWeight.Bold) },
                                onClick = { menuOpen = false; if (s != version.status) onChangeStatus(s) },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(dateText, color = InkGray500, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400)
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VersionActionPill(
                icon = Icons.Outlined.FileDownload,
                label = "匯出 PDF",
                accent = BrandDeepOrange,
                modifier = Modifier.weight(1f),
                onClick = onExport,
            )
            if (version.status == SubmissionStatus.DRAFT) {
                VersionActionPill(
                    icon = Icons.Outlined.Check,
                    label = "標記已投遞",
                    accent = AccentGreen,
                    modifier = Modifier.weight(1f),
                    onClick = if (busy) null else onMarkSubmitted,
                )
            } else {
                VersionActionPill(
                    icon = Icons.Outlined.CheckCircle,
                    label = statusText,
                    accent = statusColor,
                    isFilled = true,
                    modifier = Modifier.weight(1f),
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
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFilled) accent else PaperWhite)
            .then(
                if (onClick != null) {
                    Modifier.pressScale(onClick = onClick)
                } else {
                    // No action, but still swallow the tap so it does not fall through to the card.
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                }
            )
            .padding(vertical = 9.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (isFilled) PaperWhite else accent, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, color = if (isFilled) PaperWhite else accent,
            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}