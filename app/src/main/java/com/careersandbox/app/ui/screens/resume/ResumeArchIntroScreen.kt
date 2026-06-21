package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

private data class IntroStep(
    val tag: String,
    val headline: String,
    val body: String,
    val color: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeArchIntroScreen(navController: NavHostController) {
    val steps = listOf(
        IntroStep(
            "母版", "母版是你的素材庫",
            "一份完整的履歷,把所有經歷與技能都放進去。它本身不投出去,只當作所有客製版本的取材來源。",
            BrandDeepOrange,
        ),
        IntroStep(
            "職缺", "每個應徵目標開一個職缺",
            "針對每間公司、每個職位開一個職缺。從母版挑出對這個 JD 重要的段落、弱化無關的,做出客製。",
            BrandOrange,
        ),
        IntroStep(
            "版本", "一個職缺可以有很多版本",
            "同一個職缺底下可以存多個版本,每個版本各自記投遞狀態,讓你追蹤投了哪一版、現在進度到哪。",
            AccentBlue,
        ),
    )
    var step by remember { mutableStateOf(0) }
    val cur = steps[step]
    val isLast = step == steps.lastIndex

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("怎麼用", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.indices.forEach { i ->
                    Box(
                        Modifier
                            .size(width = if (i == step) 22.dp else 8.dp, height = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i == step) cur.color else InkGray200),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(cur.color),
                contentAlignment = Alignment.Center,
            ) {
                Text(cur.tag, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 30.sp)
            }
            Spacer(Modifier.height(28.dp))
            Text(cur.headline, color = InkBlack, fontWeight = FontWeight.Black,
                fontSize = 22.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(cur.body, color = InkGray500, fontSize = 15.sp,
                lineHeight = 22.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(2f))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (step > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .pressScale { step-- }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text("上一步", color = InkGray500, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(InkBlack)
                        .pressScale { if (isLast) navController.popBackStack() else step++ }
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                ) {
                    Text(if (isLast) "開始" else "下一步",
                        color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
