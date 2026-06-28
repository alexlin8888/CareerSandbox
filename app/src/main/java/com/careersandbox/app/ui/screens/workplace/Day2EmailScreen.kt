package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
   Day 2 · 週二：Email 風暴日（NovaMail 開信視圖,逐封處理）
   真實打底：新人第一週就被信箱淹沒。考驗分得清輕重、不亂把球丟給同事。
   每封信 = 開信視圖 + 處理選項;分流準度 → 專業形象,亂丟給 Vivian / 漏接
   真急件各有旗標。對齊你 Claude Design 的「郵件場景」版面。
   ===================================================================== */

@Composable
fun Day2EmailScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 2; WorkplaceState.beginAppPhase(2); SoundManager.playBgm(audioCtx, R.raw.bgm_neutral) }
    var done by remember { mutableStateOf(false) }
    var agendaSeen by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }

    if (!agendaSeen) {
        DayAgendaScreen(day = 2, onStart = { agendaSeen = true })
        return
    }

    if (!taskStarted) {
        WorkplaceHome(
            navController = navController,
            dayLabel = "週二 · DAY 2",
            objective = "信箱炸了。先把「郵件」看完、分清楚哪封急哪封能等，「訊息」也有人在敲你，再決定怎麼回業務跟工程。",
            relevantKeys = setOf("mail", "chat"),
            unreadCounts = mapOf("mail" to 5, "chat" to 2),
            decisionLabel = "回信、做決定",
            decisionHint = "看完 郵件 · 訊息 再決定",
            onDecision = { taskStarted = true },
        )
        return
    }

    if (done) {
        Day2Ending(onBack = { navController.popBackStack() })
        return
    }

    SandboxConversation(
        navController = navController,
        npcId = "vivian",
        day = 2,
        opening = "欸,你信箱應該炸了吧?客戶一直在追 demo、CI 又紅了、Ken 也在問。我們得趕快對一下——你打算先處理哪個?",
        onConcluded = {
            WorkplaceState.completeDay(2)
            done = true
        },
    )

}

@Composable
private fun Day2Ending(onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109))),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.padding(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("週二 · 信箱清空", color = Color(0xFFFFB627), fontSize = 13.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text("五點的鬧鐘響了。回完的、沒回完的、回了但不確定對不對的。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("明天，還有明天的信。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2531C)).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("回到本週", color = Color(0xFFFFF8F3), fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}
