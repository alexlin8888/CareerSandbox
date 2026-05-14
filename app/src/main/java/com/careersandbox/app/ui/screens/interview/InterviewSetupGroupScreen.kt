package com.careersandbox.app.ui.screens.interview

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupGroupScreen(navController: NavHostController) {
    var targetJob by remember { mutableStateOf("Junior PM") }
    var teamSize by remember { mutableStateOf("4 人(含你)") }
    var topic by remember { mutableStateOf("產品方向討論") }
    var role by remember { mutableStateOf("自由發言") }
    var duration by remember { mutableStateOf("30 分鐘") }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaperWhite)
                    .padding(20.dp)
            ) {
                // 內聯按鈕,不依賴共用元件
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
                .padding(horizontal = 24.dp),
        ) {
            // MVP 提示卡
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandYellow.copy(alpha = 0.25f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("AI 將扮演其他應徵者,模擬真實小組討論的競合場景",
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
            SetupGroupField("你的角色", role, listOf("自由發言", "主持人", "記錄員", "時間管理員")) {
                role = it
            }
            Spacer(Modifier.height(24.dp))
            SetupGroupField("時長", duration, listOf("20 分鐘", "30 分鐘", "45 分鐘")) {
                duration = it
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
        // chip 列(可換選)
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
