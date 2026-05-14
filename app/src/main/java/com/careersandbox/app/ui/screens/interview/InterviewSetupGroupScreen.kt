package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

private data class RoleOption(val title: String, val subtitle: String)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupGroupScreen(navController: NavHostController) {
    var targetJob by remember { mutableStateOf("Junior PM") }
    var teamSize by remember { mutableStateOf("4 人(含你)") }
    var topic by remember { mutableStateOf("產品方向討論") }
    var duration by remember { mutableStateOf("30 分鐘") }
    var jdText by remember { mutableStateOf("") }

    val roleOptions = listOf(
        RoleOption("一般應徵者", "預設模式,公平競爭"),
        RoleOption("較資深應徵者", "其他人比你新鮮,你被預期帶話題"),
        RoleOption("較資淺應徵者", "其他人比你資深,挑戰更大"),
        RoleOption("匿名混合", "AI 隨機決定其他人的資歷"),
    )
    var role by remember { mutableStateOf(roleOptions[0]) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("團體面試設定", fontWeight = FontWeight.Bold, color = InkBlack) },
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
                            navController.navigate(Routes.INTERVIEW_LIVE_GROUP) {
                                popUpTo(Routes.INTERVIEW_HUB)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("開始團體面試",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // Hero 插畫
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                InkBlack,
                                InkGray700,
                            )
                        )
                    ),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.undraw_online_meetings_zutp),
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
                    Text("小組討論",
                        color = BrandOrange,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelLarge,
                        letterSpacing = 2.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("AI 扮演其他應徵者",
                        color = PaperWhite,
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
                    .background(BrandYellow.copy(alpha = 0.25f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("模擬真實小組討論的競合場景",
                    color = InkBlack,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(28.dp))

            SetupGroupField("目標職位", targetJob, listOf("Junior PM", "資料分析師", "行銷企劃", "UX 研究員")) {
                targetJob = it
            }
            Spacer(Modifier.height(24.dp))
            SetupGroupField("組員人數", teamSize, listOf("3 人(含你)", "4 人(含你)", "5 人(含你)", "6 人(含你)")) {
                teamSize = it
            }
            Spacer(Modifier.height(24.dp))
            SetupGroupField("討論主題", topic,
                listOf("產品方向討論", "市場進入策略", "資源分配難題", "客戶投訴處理", "團隊衝突調解")) {
                topic = it
            }
            Spacer(Modifier.height(24.dp))

            // 角色 — 4 個更實際的選項,卡片式
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("你的角色設定",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                Spacer(Modifier.height(10.dp))
                roleOptions.forEach { option ->
                    val sel = option == role
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (sel) BrandOrange.copy(alpha = 0.12f)
                                else InkGray100
                            )
                            .pressScale { role = option }
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 圓點
                            Box(
                                Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (sel) BrandOrange else InkGray300),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (sel) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(PaperWhite)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(option.title,
                                    color = if (sel) BrandDeepOrange else InkBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(option.subtitle,
                                    color = InkGray500,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(16.dp))

            SetupGroupField("時長", duration, listOf("20 分鐘", "30 分鐘", "45 分鐘")) {
                duration = it
            }

            Spacer(Modifier.height(28.dp))

            // JD 貼上
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("貼上職缺 JD (選填)",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = jdText,
                    onValueChange = { jdText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = {
                        Text(
                            "貼上職缺敘述,AI 會根據 JD 設計討論主題與面試官提問",
                            color = InkGray400,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = InkGray300,
                    ),
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

            Spacer(Modifier.height(48.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SetupGroupField(label: String, value: String, options: List<String>, onChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { opt ->
                val sel = opt == value
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (sel) BrandOrange
                            else BrandOrange.copy(alpha = 0.08f)
                        )
                        .pressScale { onChange(opt) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(opt,
                        color = if (sel) PaperWhite else BrandDeepOrange,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.SemiBold)
                }
            }
        }
    }
}
