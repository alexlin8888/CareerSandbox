package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 1 · 週一：與主管 Ken 的 1on1（決策場景，3 拍小弧）
   真實打底：新人 PM 最常見死法是 coming in too hot / 單打獨鬥逞英雄;
   但主管要的是你的判斷,不是把球推回去。沒有零成本選項。
   ===================================================================== */

private data class D1Beat(val narration: String, val choices: List<DecisionChoice>)

@Composable
fun Day1OneOnOneScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 1; WorkplaceState.beginAppPhase(1); SoundManager.playBgm(audioCtx, R.raw.bgm_warm) }
    var beat by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var unlocked by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }
    var reaction by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    val beats = listOf(
        D1Beat(
            "你才坐到位子上，螢幕還沒插電，前一個人留下的咖啡漬乾在桌角。小芳端著馬克杯經過，壓低聲音：「欸，你前面那個，做不到三個月就走了喔，加油。」說完笑了一下，那個笑你讀不懂。\n\n然後 Ken 叫你進去。「坐。」他盯著螢幕，沒抬頭，「資管的，會寫 code 嗎？」你還沒答完，他把資料夾推過來：「分帳的功能，卡很久了。客戶催、業務催，工程一直說還沒好。你這禮拜先搞懂。禮拜三產品會議，我要聽你講你的判斷。」你想說你才第一天。看他的表情，你把這句吞回去。",
            listOf(
                DecisionChoice("A", "好，我這兩天先把分帳的前因後果摸清楚，禮拜三給您一個版本。",
                    "主管信任", 1, "Ken 沒回應，但點了一下頭", "d1_listen"),
                DecisionChoice("B", "月底沒問題，我會盯著上。",
                    "專業形象", 2, "Ken 挑了一下眉，沒說話", "d1_overpromise"),
                DecisionChoice("C", "這要看公司的優先順序——您要先保 demo 還是先保品質？",
                    "主管信任", -2, "「我在問你。」Ken 把問題丟回來", "d1_passback"),
            ),
        ),
        D1Beat(
            "「假設真的來不及。」Ken 在筆記本上寫了個字。「工程要兩週，業務已經跟客戶說月底。這個夾縫，你會怎麼拆？」",
            listOf(
                DecisionChoice("A", "基本版先上、進階版下一版。客戶看得到東西，風險也鎖得住。",
                    "專業形象", 2, "Ken 寫字的手停了一下", "d1_phase"),
                DecisionChoice("B", "壓工程加班，月底硬上。",
                    "同事情誼", -1, "這個加班，是工程在扛", "d1_crunch"),
                DecisionChoice("C", "我想先聽工程跟業務各自的版本，明天會議再定。",
                    "主管信任", 2, "Ken：「可以。別拖太久。」", "d1_align"),
            ),
        ),
        D1Beat(
            "Ken 站起來。「第一週，別自己硬扛。有什麼要我頂的，現在說。」他停了一下，「喔，還有。這裡的事，不管誰跟你講的，都 cc 我一份。我不喜歡驚喜。」",
            listOf(
                DecisionChoice("A", "我需要工程的 bug 清單跟業務的客戶承諾，今天之內。",
                    "主管信任", 1, "Ken 點頭，這個你問對了", "d1_ask"),
                DecisionChoice("B", "目前沒有，我自己先摸清楚。",
                    "主管信任", 1, "Ken：「嗯。」", "d1_solo"),
                DecisionChoice("C", "能不能幫我跟業務說，先別再對客戶加碼承諾？",
                    "同事情誼", 1, "Ken 笑了一下：「這個你懂。」", "d1_shield"),
            ),
        ),
    )

    // Ken 表情:反應優先,否則依累積主管信任
    val mt = WorkplaceState.managerTrust.value

    if (!unlocked) {
        SandboxLockScreen(onUnlock = { unlocked = true })
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

    val current = beats[beat]
    SandboxDecisionScene(
        speaker = "Ken",
        portrait = reaction ?: faceKenBase(mt),
        narration = current.narration,
        choices = current.choices,
        bgRes = R.drawable.bg_scene_1on1,
        repPop = repPop,
        onBack = { navController.popBackStack() },
        onChoose = { c ->
            if (c.repDelta != 0) {
                repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 1)
            }
            c.flag?.let { WorkplaceState.setFlag(it) }
            reaction = faceKenReact(c.repDelta, WorkplaceState.managerTrust.value)
            scope.launch {
                kotlinx.coroutines.delay(1150)
                reaction = null
                if (beat < beats.lastIndex) beat++ else done = true
            }
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
