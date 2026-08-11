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
import com.careersandbox.app.data.pdf.renderFirstPageAsBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign

private enum class JdPhase { INPUT, ANALYZING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JdCustomizeScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf(JdPhase.INPUT) }
    var jdText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (phase) {
                            JdPhase.INPUT -> "貼上職缺 JD"
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
            JdPhase.INPUT -> InputPhase(
                jdText = jdText,
                onJdChange = { jdText = it },
                onSubmit = { phase = JdPhase.ANALYZING },
                contentPadding = pad,
            )
            JdPhase.ANALYZING -> AnalyzingPhase(
                onDone = { phase = JdPhase.RESULT },
                contentPadding = pad,
            )
            JdPhase.RESULT -> ResultPhase(
                onBack = { phase = JdPhase.INPUT },
                onExport = { navController.navigate(Routes.pdfExportDialog("custom")) },
                onViewVersions = { navController.navigate(Routes.RESUME_HIERARCHY) },
                contentPadding = pad,
            )
        }
    }
}

// ========== 階段 1:貼 JD ==========
@Composable
private fun InputPhase(
    jdText: String,
    onJdChange: (String) -> Unit,
    onSubmit: () -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // 簡介卡
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
                Text("AI 變魔術",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "貼上你想應徵的職缺敘述,AI 會分析你的個人檔案跟這份 JD 的適配度,自動凸顯相關段落、隱藏無關內容,並給你 ATS 通過率評估。",
                color = InkGray700,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("職缺敘述",
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = jdText,
            onValueChange = onJdChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text(
                    "貼上完整 JD 內容(職位描述 + 必要條件 + 加分條件)。\n\n例如:\n「我們正在尋找一位 Junior PM,需要熟悉資料分析、A/B 測試,並有獨立帶專案的經驗...」",
                    color = InkGray400,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = InkGray200,
            ),
        )
        if (jdText.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "${jdText.length} 字元",
                color = AccentGreen,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.End),
            )
        }

        Spacer(Modifier.height(28.dp))

        // 範例提示
        Text("試試貼上這個範例:",
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(InkGray100)
                .pressScale {
                    onJdChange(
                        "Junior Product Manager - 電商產品團隊\n\n" +
                                "我們正在尋找一位充滿好奇心的 Junior PM,加入我們的電商產品團隊。\n\n" +
                                "工作內容:\n" +
                                "- 透過資料分析找出產品優化機會\n" +
                                "- 規劃 A/B 測試,驗證產品假設\n" +
                                "- 跨部門協作(設計、工程、行銷)\n" +
                                "- 撰寫 PRD 與 user story\n\n" +
                                "必要條件:\n" +
                                "- 熟悉 SQL 與資料分析工具\n" +
                                "- 有 A/B 測試、量化思考經驗\n" +
                                "- 良好的溝通與跨部門協作能力\n" +
                                "- 對使用者體驗有熱情\n\n" +
                                "加分條件:\n" +
                                "- 曾在零售、電商相關產業實習\n" +
                                "- 熟悉 SaaS 產品邏輯\n" +
                                "- 英文流利"
                    )
                }
                .padding(14.dp),
        ) {
            Text("Junior PM @ 電商產品團隊",
                color = BrandDeepOrange,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(36.dp))

        // CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (jdText.length >= 20) InkBlack else InkGray300)
                .pressScale(enabled = jdText.length >= 20) { onSubmit() },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = if (jdText.length >= 20) BrandAmber else InkGray500,
                    modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("開始 AI 客製化",
                    color = if (jdText.length >= 20) PaperWhite else InkGray500,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium)
            }
        }
        if (jdText.length < 20) {
            Spacer(Modifier.height(8.dp))
            Text("至少貼 20 個字元才能開始",
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally))
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
private fun ResultPhase(onBack: () -> Unit, onExport: () -> Unit, onViewVersions: () -> Unit, contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        val customized = remember { MockJdCustomizer.customize(MockData.experiences) }
        val highlights = remember {
            customized.filter { it.highlighted }.map { it.text to it.matchedKeywords }
        }
        val covered = remember { MockJdCustomizer.coveredKeywords(MockData.experiences) }
        val totalKw = MockJdCustomizer.jdKeywords.size
        val matchScore = remember { if (totalKw == 0) 0 else covered.size * 100 / totalKw }
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
                Text("已根據 JD 調整好這份履歷", color = InkGray500, fontSize = 13.sp)
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
                .pressScale { showSaveSheet = true },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Check, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("存成某職缺的新版本",
                    color = PaperWhite, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
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
                val data = buildCustomResumeDataFromCustomization(customized, dimmedItems.toSet())
                if (data == null) {
                    previewError = true
                } else {
                    val file = DeviceCustomResumePdfGenerator.generate(ctx, "preview_temp", data)
                    previewBitmap = renderFirstPageAsBitmap(file)
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

        // 選職缺存成新版本
        if (showSaveSheet) {
            val note = "依 JD 客製:保留 ${highlights.size} 段重點" +
                if (dimmedItems.isNotEmpty()) "、弱化 ${dimmedItems.size} 段" else ""
            AlertDialog(
                onDismissRequest = { showSaveSheet = false },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showSaveSheet = false }) { Text("取消") }
                },
                title = { Text("存到哪個職缺?") },
                text = {
                    Column {
                        Text("這次客製會存成該職缺底下的新版本(草稿)",
                            color = InkGray500, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        MockResumeHierarchyProvider.jobTargets().forEach { t ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(InkGray100)
                                    .pressScale {
                                        MockResumeHierarchyProvider.addVersion(t.id, note)
                                        savedTo = "${t.title}・${t.company}"
                                        showSaveSheet = false
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                            ) {
                                Column {
                                    Text("${t.title}・${t.company}",
                                        color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("目前 ${t.versions.size} 版",
                                        color = InkGray500, fontSize = 11.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                },
            )
        }

        // 存檔成功確認
        savedTo?.let { dest ->
            AlertDialog(
                onDismissRequest = { savedTo = null },
                confirmButton = {
                    TextButton(onClick = {
                        savedTo = null
                        onViewVersions()
                    }) { Text("查看職缺與版本") }
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
