package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

private enum class ExportPhase { SELECT_TEMPLATE, EXPORTING, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfExportDialogScreen(
    navController: NavHostController,
    versionId: String,
) {
    var phase by remember { mutableStateOf(ExportPhase.SELECT_TEMPLATE) }
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
        when (phase) {
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
            )
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
        TemplateInfo("現代橫式", "簡潔大方,適合一般職位", "Modern", BrandOrange),
        TemplateInfo("中文台式", "經典台灣求職版型,含照片", "Traditional", BrandDeepOrange),
        TemplateInfo("英文一頁式", "矽谷 Tech 風,強調量化成果", "One-Page", GlowPurple),
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
        Spacer(Modifier.height(80.dp))

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
private fun DonePhase(onClose: () -> Unit, contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(80.dp))

        Box(
            Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(AccentGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(44.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "PDF 已產生",
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "履歷_AlexLin_2026.pdf",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(40.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            val ctx = LocalContext.current
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InkBlack)
                    .pressScale {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "我的履歷 - CareerSandbox")
                            putExtra(android.content.Intent.EXTRA_TEXT, "這是我用 CareerSandbox 製作的履歷")
                        }
                        ctx.startActivity(android.content.Intent.createChooser(intent, "分享履歷"))
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
