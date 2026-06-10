package com.careersandbox.app.ui.screens.resume

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ResumeUploadProcessingScreen(navController: NavHostController) {
    // #14 先選匯入來源(多格式),再進解析動畫
    var source by remember { mutableStateOf<String?>(null) }
    if (source == null) {
        UploadSourcePicker(
            onBack = { navController.popBackStack() },
            onPick = { source = it },
        )
    } else {
        UploadProcessing(navController, source!!)
    }
}

@Composable
private fun UploadProcessing(navController: NavHostController, sourceLabel: String) {
    val steps = listOf(
        "讀取檔案" to 600L,
        "分析履歷結構" to 900L,
        "萃取個人經歷" to 700L,
        "結構化為個人檔案" to 800L,
    )
    var currentStep by remember { mutableIntStateOf(-1) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        steps.forEachIndexed { idx, (_, duration) ->
            currentStep = idx
            delay(duration)
        }
        done = true
        delay(700)
        // 結束後自動跳到 profile
        navController.navigate(Routes.RESUME_PROFILE) {
            popUpTo(Routes.RESUME_HUB) { inclusive = false }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PaperWhite),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))

            // 上方插畫
            Image(
                painter = painterResource(
                    if (done) R.drawable.beaver_celebrate else R.drawable.beaver_resume
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp),
            )

            Spacer(Modifier.height(40.dp))

            Text(
                if (done) "完成" else "AI 解析中",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (done) "正在打開你的個人檔案" else "正在解析你的「$sourceLabel」",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(Modifier.height(48.dp))

            // 進度步驟
            Column(modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { idx, (label, _) ->
                    val isCurrent = idx == currentStep && !done
                    val isComplete = idx < currentStep || done

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
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
                                isComplete -> Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = PaperWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                isCurrent -> Box(
                                    Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandDeepOrange)
                                )
                                else -> Text(
                                    "${idx + 1}",
                                    color = InkGray400,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            label,
                            color = if (isComplete || isCurrent) InkBlack else InkGray400,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

/* ===================== #14 / #13 匯入來源選擇器 ===================== */

private data class UploadSrc(
    val icon: ImageVector,
    val label: String,
    val desc: String,
)

@Composable
private fun UploadSourcePicker(
    onBack: () -> Unit,
    onPick: (String) -> Unit,
) {
    val sources = listOf(
        UploadSrc(Icons.Outlined.Apartment, "LinkedIn 匯出檔", "從 LinkedIn 個人檔案的匯出資料快速建立"),
        UploadSrc(Icons.Outlined.Description, "PDF 履歷", "上傳現成的 PDF 履歷,自動辨識內容"),
        UploadSrc(Icons.Outlined.Description, "Word 文件", "上傳 .doc / .docx 履歷檔案"),
        UploadSrc(Icons.Outlined.FileUpload, "其他求職平台", "104、CakeResume、Yourator 等平台的匯出檔"),
        UploadSrc(Icons.Outlined.Edit, "貼上純文字", "直接貼上履歷文字,系統幫你結構化"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite),
    ) {
        // 自訂頂部列(避免 M3 TopAppBar 的 opt-in)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = InkBlack)
            }
            Text("匯入履歷", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text("從哪裡匯入?", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 26.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "支援多種格式,選一個來源開始 — 系統會幫你解析成完整的母版。",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
            )
            Spacer(Modifier.height(24.dp))

            sources.forEach { s ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .pressScale { onPick(s.label) }
                        .clip(RoundedCornerShape(16.dp))
                        .background(PaperWarm)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(BrandPeach),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(s.icon, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.label, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(s.desc, color = InkGray500, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400)
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(InkGray100.copy(alpha = 0.5f))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    "不論哪種來源,解析後都會整理成同一份母版 — 之後再依不同職缺客製成各種版本。",
                    color = InkGray700,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
