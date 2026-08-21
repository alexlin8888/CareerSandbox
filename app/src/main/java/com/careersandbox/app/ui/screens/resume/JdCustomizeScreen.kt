package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.StickyNote
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.mock.MockJdCustomizer
import com.careersandbox.app.data.mock.MockResumeHierarchyProvider
import kotlinx.coroutines.delay
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.careersandbox.app.data.pdf.DeviceCustomResumePdfGenerator
import com.careersandbox.app.data.pdf.buildCustomResumeDataFromCustomization
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.foundation.border
private enum class JdPhase { SELECT_JOB, ANALYZING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JdCustomizeScreen(navController: NavHostController, preselectedJobId: String? = null) {
    var phase by remember {
        mutableStateOf(if (preselectedJobId != null) JdPhase.ANALYZING else JdPhase.SELECT_JOB)
    }
    var selectedJob by remember {
        mutableStateOf(MockData.jobApplications.find { it.id == preselectedJobId })
    }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (phase) {
                            JdPhase.SELECT_JOB -> "選擇職缺"
                            JdPhase.ANALYZING -> "AI 分析中"
                            JdPhase.RESULT -> "客製化結果"
                        },
                        fontWeight = FontWeight.Bold, color = InkBlack,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        when (phase) {
            JdPhase.SELECT_JOB -> SelectJobPhase(
                onSelect = { job -> selectedJob = job; phase = JdPhase.ANALYZING },
                onAddNewJob = { navController.navigate(Routes.NEW_JOB_APPLICATION) },
                contentPadding = pad,
            )
            JdPhase.ANALYZING -> AnalyzingPhase(
                onDone = { phase = JdPhase.RESULT },
                contentPadding = pad,
            )
            JdPhase.RESULT -> ResultPhase(
                job = selectedJob,
                onBack = { phase = JdPhase.SELECT_JOB },
                onExport = { navController.navigate(Routes.pdfExportDialog("custom")) },
                onViewThisJob = {
                    selectedJob?.let { job ->
                        navController.navigate(Routes.jobApplicationDetail(job.id)) {
                            popUpTo(Routes.RESUME_HUB)
                        }
                    }
                },
                contentPadding = pad,
            )
        }
    }
}

// ========== 階段 1: 選擇職缺 ==========
@Composable
private fun SelectJobPhase(
    onSelect: (com.careersandbox.app.data.model.JobApplication) -> Unit,
    onAddNewJob: () -> Unit,
    contentPadding: PaddingValues,
) {
    val jobs = MockData.jobApplications

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BrandPeach.copy(alpha = 0.45f))
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = BrandDeepOrange,
                    modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("AI 職缺客製化",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "挑一個你已經新增的職缺，AI 會依照這個職缺的 JD 內容，幫你調整履歷重點。",
                color = InkGray700,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )
        }

        Spacer(Modifier.height(24.dp))

        if (jobs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Outlined.WorkOutline, contentDescription = null,
                    tint = InkGray400, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("還沒有任何職缺", color = InkGray500, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("先新增一個職缺，才能開始客製化履歷", color = InkGray400, fontSize = 13.sp)
            }
        } else {
            Text("選擇職缺",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(10.dp))
            jobs.forEach { job ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkGray100)
                        .pressScale { onSelect(job) }
                        .padding(18.dp),
                ) {
                    Column {
                        Text(job.position, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(job.company, color = InkGray500, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            job.jdSnippet,
                            color = InkGray700,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            maxLines = 2,
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PaperWhite)
                .border(1.5.dp, BrandOrange, RoundedCornerShape(14.dp))
                .pressScale { onAddNewJob() },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("新增職缺", color = BrandDeepOrange, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

// ========== 階段 2:AI 分析中 ==========
@Composable
private fun AnalyzingPhase(onDone: () -> Unit, contentPadding: PaddingValues) {
    val steps = listOf(
        "解析 JD 關鍵字" to 700L,
        "比對你的個人檔案" to 900L,
        "計算 ATS 適配度" to 700L,
        "重組履歷重點" to 800L,
    )
    var currentStep by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        delay(300)
        steps.forEachIndexed { idx, (_, duration) ->
            currentStep = idx
            delay(duration)
        }
        delay(400)
        onDone()
    }

    // pulse 動畫
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(60.dp))

        // 中央光點(脈動)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.15f * pulse)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BrandOrange.copy(alpha = 0.5f * pulse)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("施展魔術中",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp)
        Spacer(Modifier.height(6.dp))
        Text("AI 正在為這份 JD 重組你的履歷",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(40.dp))

        // 進度
        Column(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { idx, (label, _) ->
                val isCurrent = idx == currentStep
                val isComplete = idx < currentStep

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isComplete -> BrandOrange
                                    isCurrent -> BrandPeach
                                    else -> InkGray100
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            isComplete -> Icon(Icons.Outlined.Check,
                                contentDescription = null,
                                tint = PaperWhite,
                                modifier = Modifier.size(16.dp))
                            isCurrent -> Box(
                                Modifier.size(10.dp).clip(CircleShape).background(BrandDeepOrange)
                            )
                            else -> Text("${idx + 1}",
                                color = InkGray400,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(label,
                        color = if (isComplete || isCurrent) InkBlack else InkGray400,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

// ========== 階段 3:結果 ==========
@Composable
private fun ResultPhase(
    job: com.careersandbox.app.data.model.JobApplication?,
    onBack: () -> Unit,
    onExport: () -> Unit,
    onViewThisJob: () -> Unit,
    contentPadding: PaddingValues,
) {
    var experiences by remember { mutableStateOf<List<com.careersandbox.app.data.model.Experience>?>(null) }
    LaunchedEffect(Unit) {
        experiences = com.careersandbox.app.data.repository.RemoteExperienceRepository().list().getOrNull() ?: emptyList()
    }
    val exp = experiences

    if (exp == null) {
        Box(
            modifier = Modifier.padding(contentPadding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = BrandDeepOrange)
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        val jdKeywords = job?.jdKeywords ?: emptyList()
        val customized = remember(exp, jdKeywords) { MockJdCustomizer.customize(exp, jdKeywords) }
        val highlights = remember(exp) {
            customized.filter { it.highlighted }.map { it.text to it.matchedKeywords }
        }
        val covered = remember(exp, jdKeywords) { MockJdCustomizer.coveredKeywords(exp, jdKeywords) }
        val totalKw = jdKeywords.size
        val matchScore = remember(exp) { if (totalKw == 0) 0 else covered.size * 100 / totalKw }
        var showPreview by remember { mutableStateOf(false) }
        var showSaveSheet by remember { mutableStateOf(false) }
        var savedTo by remember { mutableStateOf<String?>(null) }

        // 完成慶祝(品牌大使)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("客製化完成", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (job != null) "已依「${job.position}・${job.company}」調整好這份履歷"
                    else "已根據 JD 調整好這份履歷",
                    color = InkGray500, fontSize = 13.sp,
                )
            }
            MascotVideo(
                rawResId = R.raw.beaver_celebrate_anim,
                modifier = Modifier
                    .size(132.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )
        }
        Spacer(Modifier.height(16.dp))

        // 適配度大卡
        MatchScoreCard(score = matchScore)

        Spacer(Modifier.height(24.dp))

        // 三大指標
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricMini("關鍵字命中", "${covered.size} / $totalKw", AccentGreen, modifier = Modifier.weight(1f))
            MetricMini("硬實力", "高", BrandOrange, modifier = Modifier.weight(1f))
            MetricMini("軟實力", "中", BrandAmber, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(36.dp))

        // 已強化
        SectionTitle("已強化", "AI 認為對這份 JD 重要的段落")
        highlights.forEachIndexed { i, (text, matched) ->
            HighlightItem(text = text, matched = matched)
            if (i < highlights.size - 1) Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(28.dp))

        // 已弱化(減法)— 使用者可主動拉回
        val dimmedItems = remember {
            mutableStateListOf(
                *customized.filter { !it.highlighted }.map { it.text }.toTypedArray()
            )
        }
        if (dimmedItems.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(end = 64.dp)) {
                SectionTitle("已弱化 ${dimmedItems.size} 段", "移除無關經歷,讓 recruiter 30 秒抓到重點")
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                StickyNote(
                    text = "想留就拉回",
                    rotation = 4f,
                    modifier = Modifier.align(Alignment.TopEnd).offset(y = (-44).dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            dimmedItems.forEachIndexed { idx, item ->
                DimmedItem(text = item, onRestore = { dimmedItems.remove(item) })
                if (idx < dimmedItems.size - 1) Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(28.dp))
        }

        // 缺失關鍵字
        SectionTitle("建議補強", "這些 JD 關鍵字你的檔案沒提到")
        MissingChips(listOf("PRD 撰寫", "user story", "SaaS 邏輯"))

        Spacer(Modifier.height(36.dp))

        // 預覽客製版
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BrandPeach)
                .pressScale { showPreview = true },
            contentAlignment = Alignment.Center,
        ) {
            Text("預覽客製版",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.height(10.dp))

        // 存成某職缺的新版本(接履歷階層架構)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BrandOrange)
                .pressScale {
                    if (job != null) {
                        val note = "依 JD 客製：保留 ${highlights.size} 段重點" +
                                if (dimmedItems.isNotEmpty()) "、弱化 ${dimmedItems.size} 段" else ""
                        savedTo = "${job.position}・${job.company}"
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Check, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (job != null) "存成「${job.position}」的新版本" else "存成新版本",
                    color = PaperWhite, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        Spacer(Modifier.height(10.dp))

        // 主按鈕
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(InkBlack)
                .pressScale(onClick = onExport),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FileDownload,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("匯出客製化版 PDF",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(10.dp))
        // 次按鈕
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(InkGray100)
                .pressScale(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Text("試另一份 JD",
                color = InkBlack,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall)
        }

        if (showPreview) {
            val ctx = LocalContext.current
            var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
            var previewError by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                val data = buildCustomResumeDataFromCustomization(customized, dimmedItems.toSet(), exp)
                if (data == null) {
                    previewError = true
                } else {
                    val file = DeviceCustomResumePdfGenerator.generate(ctx, "preview_temp", data)
                    previewBitmap = DeviceCustomResumePdfGenerator.renderFirstPageAsBitmap(file)
                }
            }

            Dialog(
                onDismissRequest = { showPreview = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(InkBlack.copy(alpha = 0.92f)),
                ) {
                    // 關閉按鈕
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(20.dp)
                            .clip(CircleShape)
                            .background(PaperWhite.copy(alpha = 0.15f))
                            .pressScale { showPreview = false }
                            .padding(10.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "關閉",
                            tint = PaperWhite,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    val bitmap = previewBitmap
                    if (previewError) {
                        Text(
                            "找不到你的個人資料，請先回到個人檔案頁確認資料已載入",
                            color = PaperWhite,
                            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center,
                        )
                    } else if (bitmap == null) {
                        CircularProgressIndicator(
                            color = PaperWhite,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "客製化履歷預覽",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.94f)
                                .verticalScroll(rememberScrollState()),
                        )
                    }
                }
            }
        }


        // 存檔成功確認
        savedTo?.let { dest ->
            AlertDialog(
                onDismissRequest = { savedTo = null },
                confirmButton = {
                    TextButton(onClick = {
                        savedTo = null
                        onViewThisJob()
                    }) { Text("查看這個職缺") }
                },
                dismissButton = {
                    TextButton(onClick = { savedTo = null }) { Text("繼續客製") }
                },
                title = { Text("已存成新版本") },
                text = { Text("已加到「$dest」底下,狀態為草稿。") },
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun MatchScoreCard(score: Int) {
    val animScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "score",
    )
    val accent = when {
        score >= 75 -> AccentGreen
        score >= 50 -> BrandOrange
        else -> AccentRed
    }
    val verdict = when {
        score >= 75 -> "高度適配,建議投遞"
        score >= 50 -> "中度適配,補強關鍵字再投"
        else -> "低度適配,先找更相符的職缺"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(accent.copy(alpha = 0.1f))
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("適配度",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$animScore",
                        color = accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                        lineHeight = 56.sp,
                        letterSpacing = (-1).sp)
                    Spacer(Modifier.width(2.dp))
                    Text("%",
                        color = accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 10.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            // 環形圖
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = PaperWhite,
                        modifier = Modifier.size(22.dp))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(verdict,
            color = InkBlack,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MetricMini(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100)
            .padding(14.dp),
    ) {
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp)
        Spacer(Modifier.height(6.dp))
        Text(value,
            color = accent,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title,
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(subtitle,
            color = InkGray500,
            style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun HighlightItem(text: String, matched: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BrandPeach.copy(alpha = 0.3f))
            .padding(start = 16.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .padding(top = 6.dp, end = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BrandDeepOrange)
            )
            Text(text,
                color = InkBlack,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            matched.forEach { keyword ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandDeepOrange)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("✓ $keyword",
                        color = PaperWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DimmedItem(text: String, onRestore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(InkGray100)
            .padding(start = 16.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Close,
            contentDescription = null,
            tint = InkGray400,
            modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(text,
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.LineThrough,
            modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(BrandPeach)
                .pressScale(onClick = onRestore)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text("拉回", color = BrandDeepOrange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MissingChips(keywords: List<String>) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        keywords.forEach { kw ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AccentRed.copy(alpha = 0.1f))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text("+ $kw",
                    color = AccentRed,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MascotVideo(rawResId: Int, modifier: Modifier = Modifier, loop: Boolean = true) {
    val context = LocalContext.current
    val uri = remember(rawResId) { Uri.parse("android.resource://${context.packageName}/$rawResId") }
    val playerRef = remember { mutableStateOf<MediaPlayer?>(null) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextureView(ctx).apply {
                isOpaque = false
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                        try {
                            val mp = MediaPlayer()
                            mp.setDataSource(ctx, uri)
                            mp.setSurface(Surface(st))
                            mp.isLooping = loop
                            mp.setVolume(0f, 0f)
                            mp.setOnPreparedListener { it.start() }
                            mp.prepareAsync()
                            playerRef.value = mp
                        } catch (e: Exception) {
                            playerRef.value?.release(); playerRef.value = null
                        }
                    }
                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}
                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        playerRef.value?.release(); playerRef.value = null; return true
                    }
                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                }
            }
        },
    )
    DisposableEffect(Unit) {
        onDispose { playerRef.value?.release(); playerRef.value = null }
    }
}
