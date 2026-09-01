package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

private enum class ExportPhase { SELECT_TEMPLATE, EXPORTING, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfExportDialogScreen(
    navController: NavHostController,
    versionId: String,
    jobId: String? = null,
) {
    var phase by remember {
        mutableStateOf(if (versionId == "master") ExportPhase.EXPORTING else ExportPhase.SELECT_TEMPLATE)
    }
    var selectedTemplate by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (phase) {
                            ExportPhase.SELECT_TEMPLATE -> "選擇模板"
                            ExportPhase.EXPORTING -> "產生 PDF"
                            ExportPhase.DONE -> "完成"
                        },
                        fontWeight = FontWeight.Bold,
                        color = InkBlack,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        AnimatedContent(
            targetState = phase,
            transitionSpec = { fadeIn(tween(220)).togetherWith(fadeOut(tween(160))) },
            label = "exportPhase",
        ) { p ->
            when (p) {
                ExportPhase.SELECT_TEMPLATE -> SelectTemplatePhase(
                    selectedTemplate = selectedTemplate,
                    onSelect = { selectedTemplate = it },
                    onExport = { phase = ExportPhase.EXPORTING },
                    contentPadding = pad,
                )
                ExportPhase.EXPORTING -> ExportingPhase(
                    onDone = { phase = ExportPhase.DONE },
                    contentPadding = pad,
                )
                ExportPhase.DONE -> DonePhase(
                    onClose = { navController.popBackStack() },
                    contentPadding = pad,
                    versionId = versionId,
                    jobId = jobId,
                )
            }
        }
    }
}

@Composable
private fun SelectTemplatePhase(
    selectedTemplate: Int,
    onSelect: (Int) -> Unit,
    onExport: () -> Unit,
    contentPadding: PaddingValues,
) {
    val templates = listOf(
        TemplateInfo("現代橫式", "簡潔大方,適合一般職位 · A4", "Modern", BrandOrange),
        TemplateInfo("中文台式", "經典台灣求職版型,含照片 · A4", "Traditional", BrandDeepOrange),
        TemplateInfo("英文一頁式", "矽谷 Tech 風,強調量化成果 · A4", "One-Page", GlowPurple),
    )

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
                .clip(RoundedCornerShape(16.dp))
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(16.dp),
        ) {
            Text(
                "公司端能直接接受的格式",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "選一個適合應徵公司文化的版型",
                color = InkGray700,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
            )
        }

        Spacer(Modifier.height(24.dp))

        templates.forEachIndexed { idx, tpl ->
            TemplateOptionCard(
                template = tpl,
                isSelected = selectedTemplate == idx,
                onClick = { onSelect(idx) },
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(InkBlack)
                .pressScale { onExport() },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = null,
                    tint = PaperWhite,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "用 ${templates[selectedTemplate].name} 匯出",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

private data class TemplateInfo(
    val name: String,
    val description: String,
    val englishName: String,
    val accent: Color,
)

@Composable
private fun TemplateOptionCard(
    template: TemplateInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) template.accent.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface
            )
            .pressScale(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 縮圖預覽塊
        Box(
            Modifier
                .size(width = 50.dp, height = 65.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(template.accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                template.englishName.take(3).uppercase(),
                color = template.accent,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                template.name,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                template.description,
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 18.sp,
            )
        }
        if (isSelected) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = template.accent,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun ExportingPhase(onDone: () -> Unit, contentPadding: PaddingValues) {
    LaunchedEffect(Unit) {
        delay(2500)
        onDone()
    }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(R.drawable.beaver_resume),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(20.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = BrandDeepOrange,
            strokeWidth = 4.dp,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "正在產生 PDF",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "套用版型 · 排版 · 嵌入字體",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun DonePhase(
    onClose: () -> Unit,
    contentPadding: PaddingValues,
    versionId: String,
    jobId: String?,
) {
    var fileName by remember {
        mutableStateOf(if (versionId == "master") "母版履歷_AlexLin_2026" else "履歷_AlexLin_2026")
    }
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Image(
            painter = painterResource(R.drawable.beaver_celebrate),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "PDF 已產生",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkGray100)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = InkBlack,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            Text(".pdf", color = InkGray500, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text("匯出前可改檔名", color = InkGray400, fontSize = 11.sp)

        Spacer(Modifier.height(40.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InkBlack)
                    .pressScale {
                        scope.launch {
                            try {
                                val file = when (versionId) {
                                    "custom" -> {
                                        val pending = com.careersandbox.app.data.pdf.PendingCustomExport.data
                                        val data = if (pending != null) {
                                            com.careersandbox.app.data.pdf.PendingCustomExport.data = null
                                            pending
                                        } else {
                                            val job = jobId?.let { id ->
                                                com.careersandbox.app.data.mock.MockData.jobApplications.find { it.id == id }
                                            }
                                            com.careersandbox.app.data.pdf.buildCustomResumeDataForExport(job)
                                                ?: throw IllegalStateException("找不到使用者資料，請先確認已登入")
                                        }
                                        com.careersandbox.app.data.pdf.DeviceCustomResumePdfGenerator.generate(ctx, fileName, data)
                                    }
                                    "master" -> {
                                        val data = com.careersandbox.app.data.pdf.buildCustomResumeDataForMasterExport()
                                            ?: throw IllegalStateException("找不到使用者資料，請先確認已登入")
                                        com.careersandbox.app.data.pdf.DeviceCustomResumePdfGenerator.generate(ctx, fileName, data)
                                    }
                                    else -> com.careersandbox.app.data.pdf.DeviceResumePdfGenerator.generate(ctx, fileName)
                                }
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    ctx, "${ctx.packageName}.fileprovider", file
                                )
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "我的履歷 - CareerSandbox")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                val chooser = android.content.Intent.createChooser(intent, "分享履歷")
                                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(chooser)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(
                                    ctx, "目前無法產生或分享 PDF", android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = null,
                        tint = PaperWhite,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "分享 / 儲存到雲端",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(InkGray100)
                    .pressScale(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "完成",
                    color = InkBlack,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
