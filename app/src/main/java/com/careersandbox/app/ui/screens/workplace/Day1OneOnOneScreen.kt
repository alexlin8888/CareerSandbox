package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 1 · 週一：與主管 Ken 的 1on1（決策場景，3 拍小弧）
   真實打底：新人 PM 最常見死法是 coming in too hot / 單打獨鬥逞英雄;
   但主管要的是你的判斷,不是把球推回去。沒有零成本選項。
   ===================================================================== */

@Composable
fun Day1OneOnOneScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 1; WorkplaceState.beginAppPhase(1); SoundManager.playBgm(audioCtx, R.raw.bgm_warm) }
    var done by remember { mutableStateOf(false) }
    var unlocked by rememberSaveable { mutableStateOf(false) }
    var agendaSeen by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }


    if (!unlocked) {
        SandboxLockScreen(onUnlock = { unlocked = true })
        return
    }

    if (!agendaSeen) {
        DayAgendaScreen(day = 1, onStart = { agendaSeen = true })
        return
    }

    if (!taskStarted) {
        WorkplaceHome(
            navController = navController,
            dayLabel = "週一 · DAY 1",
            objective = "到職第一天。Ken 待會要找你談分帳。先看看「訊息」跟「郵件」——前一個人留下什麼、誰在傳什麼，心裡有個底再進去。",
            relevantKeys = setOf("chat", "mail"),
            unreadCounts = mapOf("chat" to 1, "mail" to 3),
            decisionLabel = "進辦公室見 Ken",
            decisionHint = "看完 訊息 · 郵件 再進去",
            onDecision = { taskStarted = true },
        )
        return
    }

    if (done) {
        Day1Ending(onBack = { navController.popBackStack() })
        return
    }

    // 決策階段:改成和 Ken 的模型驅動自由對話(取代三選一);聊完收束 → 完成當天
    SandboxConversation(
        navController = navController,
        npcId = "ken",
        day = 1,
        opening = "坐吧。第一週還順利嗎?分帳這案子,我想先聽聽你的想法——你會怎麼接?",
        onConcluded = {
            WorkplaceState.completeDay(1)
            done = true
        },
    )
}

@Composable
private fun Day1Ending(onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109))),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("週一 · 1on1 結束", color = Color(0xFFFFB627), fontSize = 13.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text("沒有人教你。但所有人都在等你。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("第一週，才第一天。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2531C))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("回到本週", color = Color(0xFFFFF8F3), fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}
