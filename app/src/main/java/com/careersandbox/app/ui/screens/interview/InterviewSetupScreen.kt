package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupScreen(navController: NavHostController) {
    var targetJob by remember { mutableStateOf("Junior PM") }
    var type by remember { mutableStateOf("行為面試") }
    var difficulty by remember { mutableStateOf("中等") }
    var style by remember { mutableStateOf("標準") }
    var duration by remember { mutableStateOf("30 分鐘") }
    var language by remember { mutableStateOf("中文") }
    var jdText by remember { mutableStateOf("") }
    var resumeVersion by remember { mutableStateOf("母版") }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("個人面試設定", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(PaperWhite).padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkBlack)
                        .pressScale {
                            navController.navigate(Routes.INTERVIEW_LIVE_INDIVIDUAL) {
                                popUpTo(Routes.INTERVIEW_HUB)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("開始面試",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // Hero 插畫區
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                BrandOrange.copy(alpha = 0.12f),
                                BrandPeach.copy(alpha = 0.4f),
                            )
                        )
                    ),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.undraw_interview_yz52),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(140.dp),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp, end = 150.dp),
                ) {
                    Text("一對一",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelLarge,
                        letterSpacing = 2.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("模擬真實面試節奏",
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        lineHeight = 28.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandPeach.copy(alpha = 0.4f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("這是模擬,真實面試會更難。建議貼上 JD 讓 AI 更精準提問",
                    style = MaterialTheme.typography.bodySmall, color = BrandDeepOrange)
            }
            Spacer(Modifier.height(20.dp))

            Field("應徵職位") {
                OutlinedTextField(value = targetJob, onValueChange = { targetJob = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), singleLine = true,
                    colors = textFieldColors())
            }
            Field("面試類型") { ChipRow(listOf("行為面試", "技術面試", "情境面試", "壓力面試"), type) { type = it } }
            Field("難度") { ChipRow(listOf("新手", "中等", "困難", "變態"), difficulty) { difficulty = it } }
            Field("面試官風格") { ChipRow(listOf("親切", "標準", "嚴厲", "隨機"), style) { style = it } }
            Field("時長") { ChipRow(listOf("15 分鐘", "30 分鐘", "45 分鐘", "60 分鐘"), duration) { duration = it } }
            Field("語言") { ChipRow(listOf("中文", "英文", "中英混合"), language) { language = it } }

            // JD 貼上區
            Field("貼上職缺 JD (選填,讓 AI 提問更精準)") {
                OutlinedTextField(
                    value = jdText,
                    onValueChange = { jdText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = {
                        Text(
                            "把職缺敘述貼進來,AI 會針對 JD 內容客製化問題",
                            color = InkGray400,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    colors = textFieldColors(),
                )
                if (jdText.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${jdText.length} 字元已貼上",
                        color = AccentGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // === #2 選擇要給面試官看的履歷版本 ===
            Field("給面試官看的履歷版本") {
                ResumeVersionPicker(selected = resumeVersion, onSelect = { resumeVersion = it })
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Field(label: String, content: @Composable () -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall,
            color = InkBlack, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(opts: List<String>, selected: String, onSel: (String) -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        opts.forEach { opt ->
            PillChip(opt, selected = opt == selected) { onSel(opt) }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
)

/* ===================== #2 履歷版本選擇器 ===================== */

@Composable
private fun ResumeVersionPicker(selected: String, onSelect: (String) -> Unit) {
    val versions = listOf(
        "母版" to "完整綜合履歷",
        "台積電 PM 版" to "投遞中",
        "新創 PM 版" to "草稿",
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        versions.forEach { (name, sub) ->
            val isSel = name == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSel) BrandPeach.copy(alpha = 0.5f) else InkGray100.copy(alpha = 0.5f))
                    .pressScale { onSelect(name) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSel) BrandDeepOrange else InkGray200),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSel) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(PaperWhite))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        sub,
                        color = if (isSel) BrandDeepOrange else InkGray500,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "面試官只會看到你選的這份版本來出題。對應母版 → 職缺 → 版本架構。",
            color = InkGray500, fontSize = 11.sp, lineHeight = 16.sp,
        )
    }
}
