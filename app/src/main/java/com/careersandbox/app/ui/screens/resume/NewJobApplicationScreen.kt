package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobApplicationScreen(navController: NavHostController) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var jdText by remember { mutableStateOf("") }

    val canSubmit = company.isNotBlank() && position.isNotBlank() && jdText.length >= 20

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("新增職缺", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // 介紹卡
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandPeach.copy(alpha = 0.45f))
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = BrandDeepOrange,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "從 JD 自動生第一版",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "輸入公司名跟職位,貼上 JD 後 AI 會比對你的母版,自動生成第一個版本。之後你還可以繼續調整、生 v2 v3。",
                    color = InkGray700,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(28.dp))

            // 公司名
            FieldLabel("公司名稱")
            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("例:Acer / KKday / 字節跳動",
                        color = InkGray400, style = MaterialTheme.typography.bodyMedium)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = InkGray200,
                ),
            )

            Spacer(Modifier.height(20.dp))

            // 職位
            FieldLabel("職位")
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("例:Junior PM / 資料分析師 / UX 研究員",
                        color = InkGray400, style = MaterialTheme.typography.bodyMedium)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = InkGray200,
                ),
            )

            Spacer(Modifier.height(20.dp))

            // JD
            FieldLabel("職缺敘述 (JD)")
            OutlinedTextField(
                value = jdText,
                onValueChange = { jdText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                placeholder = {
                    Text(
                        "貼上完整職缺描述,AI 會比對你的母版找出契合段落。",
                        color = InkGray400,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = InkGray200,
                ),
            )
            if (jdText.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "${jdText.length} 字元 ${if (jdText.length < 20) "(至少 20 字元)" else ""}",
                    color = if (jdText.length >= 20) AccentGreen else InkGray500,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.End),
                )
            }

            Spacer(Modifier.height(36.dp))

            // 提交按鈕
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (canSubmit) InkBlack else InkGray300)
                    .pressScale(enabled = canSubmit) {
                        // 模擬建立 — 跳到 JD 客製化頁
                        navController.navigate(Routes.JD_CUSTOMIZE) {
                            popUpTo(Routes.RESUME_HUB) { inclusive = false }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = if (canSubmit) BrandAmber else InkGray500,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "建立並 AI 生第一版",
                        color = if (canSubmit) PaperWhite else InkGray500,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            if (!canSubmit) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "請完整填寫公司、職位、JD(至少 20 字元)",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = InkGray500,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black,
        letterSpacing = 3.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}
