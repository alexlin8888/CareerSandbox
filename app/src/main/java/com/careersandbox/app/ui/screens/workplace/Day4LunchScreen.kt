package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 4 · 週四：同事午餐（NovaGram 限動 → 午餐決策場景）
   真實打底：亞洲職場「一起吃飯感情才好」是真的;但暗礁是「分清楚場面話與
   真心話」——對主管的抱怨會被傳出去(茶水間沒有秘密);而「老鳥對新人冷漠
   是在保護自己」,你太快掏心反被當理所當然。這天表面輕鬆,底下全是試探。
   ===================================================================== */

private data class D4Beat(val speaker: String, val portrait: Int, val narration: String, val choices: List<DecisionChoice>)

@Composable
fun Day4LunchScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf("story") } // story | lunch | done
    var beat by remember { mutableIntStateOf(0) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    val beats = listOf(
        D4Beat("小芳", R.drawable.fang_neutral,
            "你坐這。欸——聽說公司要改組,還要空降一個主管。你聽說了嗎?",
            listOf(
                DecisionChoice("A", "真的假的?跟我說說。",
                    "同事情誼", 1, "聊開了,但你也下場了", "d4_gossip"),
                DecisionChoice("B", "我才剛來,這我真的不清楚。",
                    "同事情誼", 0, ""),
                DecisionChoice("C", "誒,先吃啦,菜要涼了。",
                    "同事情誼", 0, ""),
            ),
        ),
        D4Beat("小芳", R.drawable.fang_neutral,
            "那你覺得 Ken 怎樣?老實說,沒關係,就我們兩個。",
            listOf(
                DecisionChoice("A", "他給的方向算清楚,我還在適應他的節奏。",
                    "主管信任", 1, "就事論事,得體", "d4_diplomatic"),
                DecisionChoice("B", "說真的,他開會把我推上火線那下,我有點傻眼。",
                    "同事情誼", 1, "交了心——但茶水間沒有秘密", "d4_badmouth"),
                DecisionChoice("C", "我還在觀察,不好說。",
                    "同事情誼", 0, ""),
            ),
        ),
        D4Beat("阿哲", R.drawable.colleague_akai_calm,
            "（阿哲也在桌上）我週末寫了個小工具,自動把那種 race condition 的 log 撈出來標紅…欸你應該沒興趣啦。",
            listOf(
                DecisionChoice("A", "有興趣啊,你怎麼判斷哪些是真的衝突?",
                    "同事情誼", 2, "真心接住,阿哲記著了", "lunch_bonded_akai"),
                DecisionChoice("B", "喔喔,聽起來很厲害。",
                    "同事情誼", 0, "客套帶過"),
            ),
        ),
        D4Beat("小芳", R.drawable.fang_pleased,
            "新人嘛,我提醒你一句:這裡熱絡可以,但別太快掏心。茶水間,沒有秘密。",
            listOf(
                DecisionChoice("A", "謝謝芳姐,我記住了。",
                    "同事情誼", 1, "前輩的善意,收下", "d4_heed"),
                DecisionChoice("B", "我會拿捏的。",
                    "同事情誼", 0, ""),
            ),
        ),
    )

    when (phase) {
        "done" -> Day4Ending(onBack = { navController.popBackStack() })
        "story" -> NovaGramStory(
            onClose = { navController.popBackStack() },
            onLunch = { phase = "lunch" },
        )
        else -> {
            val b = beats[beat]
            SandboxDecisionScene(
                speaker = b.speaker,
                portrait = b.portrait,
                narration = b.narration,
                choices = b.choices,
                sceneLabel = "午餐時間",
                bgRes = R.drawable.bg_scene_cafe,
                repPop = repPop,
                onBack = { navController.popBackStack() },
                onChoose = { c ->
                    if (c.repDelta != 0) {
                        repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 4)
                    }
                    c.flag?.let { WorkplaceState.setFlag(it) }
                    if (beat < beats.lastIndex) beat++ else phase = "done"
                },
            )
        }
    }
}

/* ---------- 小芳的 NovaGram 限動（對齊 band_2 限動畫面）---------- */
@Composable
private fun NovaGramStory(onClose: () -> Unit, onLunch: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFFF0B36B), Color(0xFFE08A4A), Color(0xFFC56B33)),
            ),
        ),
    ) {
        Column(Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = 52.dp)) {
            // 進度條
            Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x55FFFFFF))) {
                Box(Modifier.fillMaxWidth(0.6f).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(Color.White))
            }
            Spacer(Modifier.height(12.dp))
            // header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFB85C3A)),
                    contentAlignment = Alignment.Center,
                ) { Text("芳", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
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
            Text("一頓飯,半是交情半是試探。你說的、沒說的,都被記下了。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("剩最後一天。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
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
