package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 4 · 週四：同事午餐（NovaGram 限動 → 午餐決策場景）
   真實打底：亞洲職場「一起吃飯感情才好」是真的;但暗礁是「分清楚場面話與
   真心話」——對主管的抱怨會被傳出去(茶水間沒有秘密);而「老鳥對新人冷漠
   是在保護自己」,你太快掏心反被當理所當然。這天表面輕鬆,底下全是試探。
   ===================================================================== */


@Composable
fun Day4LunchScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 4; WorkplaceState.beginAppPhase(4); SoundManager.playBgm(audioCtx, R.raw.bgm_warm) }
    var phase by remember { mutableStateOf("story") } // story | lunch | done
    var agendaSeen by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }

    if (!agendaSeen) {
        DayAgendaScreen(day = 4, onStart = { agendaSeen = true })
        return
    }

    if (!taskStarted) {
        WorkplaceHome(
            navController = navController,
            dayLabel = "週四 · DAY 4",
            objective = "午餐前，小芳發了限動。看看「動態」她貼了什麼、「訊息」群組在約什麼，再決定中午怎麼接。",
            relevantKeys = setOf("gram", "chat"),
            unreadCounts = mapOf("gram" to 1, "chat" to 2),
            decisionLabel = "去吃午餐",
            decisionHint = "看完 動態 · 訊息 再過去",
            onDecision = { taskStarted = true },
        )
        return
    }

    when (phase) {
        "done" -> Day4Ending(onBack = { navController.popBackStack() })
        "story" -> NovaGramStory(
            onClose = { navController.popBackStack() },
            onLunch = { phase = "lunch" },
        )
        else -> SandboxConversation(
            navController = navController,
            npcId = "fang",
            day = 4,
            opening = "走啦吃飯!第一週撐下來啦。怎樣,這裡的人會不會很難搞?有什麼想問的儘管問。",
            onConcluded = {
                WorkplaceState.completeDay(4)
                phase = "done"
            },
        )
    }
}

/* ---------- 小芳的 NovaGram 限動（對齊 band_2 限動畫面）---------- */
@Composable
private fun NovaGramStory(onClose: () -> Unit, onLunch: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.story_fang),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0x4D000000), Color(0x1A000000), Color(0xB3000000)),
                ),
            ),
        )
        Column(Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = 52.dp)) {
            // 進度條
            Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x55FFFFFF))) {
                Box(Modifier.fillMaxWidth(0.6f).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(Color.White))
            }
            Spacer(Modifier.height(12.dp))
            // header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.fang_neutral),
                    contentDescription = "小芳",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEED9C4)),
                )
                Spacer(Modifier.width(9.dp))
                Text("小芳", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text("2 小時", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.MoreHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.clip(CircleShape).clickable { onClose() }) {
                    Icon(Icons.Filled.Close, contentDescription = "關閉", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.weight(1f))
            // 限動文字
            Text("又是 deadline 週", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 38.sp)
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x33000000))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("NovaPay 辦公室", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(20.dp))
            // 揪午餐
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0x26FFFFFF))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("小芳：欸一起吃午餐啦,帶你去那家。", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color.White).clickable { onLunch() },
                contentAlignment = Alignment.Center,
            ) { Text("一起去", color = Color(0xFFC56B33), fontWeight = FontWeight.Black, fontSize = 15.sp) }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun Day4Ending(onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109))),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.padding(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("週四 · 午餐結束", color = Color(0xFFFFB627), fontSize = 13.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text("回工位的路上，你經過茶水間。裡面兩個人壓低聲音講話，看到你，停了。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("這間公司最安靜的地方，其實是最吵的。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
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
