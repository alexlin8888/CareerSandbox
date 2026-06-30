package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
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
   Day 3 · 週三：跨部門會議（全週高潮）
   NovaMeet 四宮格（字母格,對齊 band_2）跑對白 → 決策場景 4 拍。
   真實打底：經典「趕市場 vs 顧品質」。業務沒問工程就對客戶把日期講死、
   工程測試沒過不簽、你被夾成「兩個資深之間戰爭的工具」、對工程 imposter
   syndrome。分階段上線是真實好解,但沒有零成本選項。
   ===================================================================== */

private data class Caption(val who: String, val line: String)

@Composable
fun Day3MeetingScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 3; WorkplaceState.beginAppPhase(3); SoundManager.playBgm(audioCtx, R.raw.bgm_tense) }
    var phase by remember { mutableStateOf("meeting") } // meeting | decision | done
    var capIdx by remember { mutableIntStateOf(0) }
    var agendaSeen by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }

    val captions = listOf(
        Caption("Vivian", "客戶那邊我已經說月底了，這個一定要上。"),
        Caption("阿哲", "測試才跑六成，race condition 沒解。我不簽。"),
        Caption("Ken", "先聽聽 PM 的判斷。"),
    )

    if (!agendaSeen) {
        DayAgendaScreen(day = 3, onStart = { agendaSeen = true })
        return
    }

    if (!taskStarted) {
        WorkplaceHome(
            navController = navController,
            dayLabel = "週三 · DAY 3",
            objective = "今天跨部門會議定生死。會前先看「行事曆」確認議程、翻「決議」上次結論、開「會議」看誰會在，準備好你的版本。",
            relevantKeys = setOf("calendar", "doc", "meet"),
            unreadCounts = mapOf("calendar" to 1, "doc" to 2, "meet" to 1),
            decisionLabel = "進會議室",
            decisionHint = "看完 行事曆 · 決議 · 會議 再進去",
            onDecision = { taskStarted = true },
        )
        return
    }

    when (phase) {
        "done" -> Day3Ending(onBack = { navController.popBackStack() })
        "meeting" -> MeetingPanel(
            caption = captions[capIdx],
            isLast = capIdx == captions.lastIndex,
            onNext = { if (capIdx < captions.lastIndex) capIdx++ else phase = "decision" },
            onBack = { navController.popBackStack() },
        )
        else -> SandboxConversation(
            navController = navController,
            npcId = "ken",
            day = 3,
            opening = "會議室裡大家都看著你。排程這件事——工程說兩週、業務答應月底,客戶在等。你打算怎麼收?",
            onConcluded = {
                WorkplaceState.completeDay(3)
                phase = "done"
            },
        )
    }
}

/* ---------- NovaMeet 四宮格會議面板（對齊 band_2,字母格）---------- */
@Composable
private fun MeetingPanel(caption: Caption, isLast: Boolean, onNext: () -> Unit, onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color(0xFF0B0E14)).clickable { onNext() },
    ) {
        Column(Modifier.fillMaxSize()) {
            // 頂部標題列
            Row(
                Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 52.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) { Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(4.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("分帳上線會議", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Row(
                            Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0x33EF4444))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Spacer(Modifier.width(4.dp))
                            Text("REC", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("NovaPay 產品組 · 4 人", color = Color(0xFF8A94A6), fontSize = 11.sp)
                }
                Text("12:05", color = Color(0xFF8A94A6), fontSize = 12.sp)
            }

            // 2x2 字母格
            Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp)) {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    Day3Tile(Modifier.weight(1f), "K", Color(0xFF5B7FE3), "Ken", host = true)
                    Spacer(Modifier.width(8.dp))
                    Day3Tile(Modifier.weight(1f), "哲", Color(0xFF35B9A4), "阿哲")
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    Day3Tile(Modifier.weight(1f), "V", Color(0xFFE491B4), "Vivian", hand = true)
                    Spacer(Modifier.width(8.dp))
                    Day3Tile(Modifier.weight(1f), "你", Color(0xFF263247), "你", you = true)
                }
            }

            // 字幕條(含點擊提示,避免疊字)
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp)).background(Color(0xFF161C28))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text("${caption.who}：${caption.line}", color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (isLast) "輪到你了 — 點一下繼續 ›" else "點一下繼續 ›",
                    color = Color(0x80FFFFFF), fontSize = 11.sp, fontWeight = FontWeight.Medium,
                )
            }

            // 控制列
            Row(
                Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 2.dp, bottom = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CtrlBtn(Icons.Filled.Mic, Color(0xFF1F2937))
                CtrlBtn(Icons.Filled.Videocam, Color(0xFF1F2937))
                CtrlBtn(Icons.Filled.PanTool, Color(0xFF1F2937))
                CtrlBtn(Icons.Filled.MoreHoriz, Color(0xFF1F2937))
                Spacer(Modifier.weight(1f))
                CtrlBtn(Icons.Filled.CallEnd, Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun Day3Tile(
    modifier: Modifier,
    letter: String,
    avatarColor: Color,
    name: String,
    host: Boolean = false,
    hand: Boolean = false,
    you: Boolean = false,
) {
    Box(modifier.fillMaxHeight().clip(RoundedCornerShape(14.dp)).background(Color(0xFF1B2230))) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(letter, color = Color.White, fontSize = if (you) 22.sp else 26.sp, fontWeight = FontWeight.Bold)
            }
        }
        // 名字
        Box(
            Modifier.align(Alignment.BottomStart).padding(10.dp)
                .clip(RoundedCornerShape(6.dp)).background(Color(0x66000000))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) { Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
        // 主持人徽章
        if (host) {
            Box(
                Modifier.align(Alignment.TopStart).padding(8.dp)
                    .clip(RoundedCornerShape(6.dp)).background(Color(0x66000000))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) { Text("主持人", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        }
        // 舉手徽章
        if (hand) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(8.dp).size(22.dp)
                    .clip(CircleShape).background(Color(0xFFFFB627)),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Filled.PanTool, contentDescription = "舉手", tint = Color(0xFF281C12), modifier = Modifier.size(12.dp)) }
        }
    }
}

@Composable
private fun CtrlBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color) {
    Box(
        Modifier.size(46.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Day3Ending(onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109))),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.padding(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("週三 · 會議散了", color = Color(0xFFFFB627), fontSize = 13.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text("散會。沒有人贏。你坐在原位，剛剛那十五分鐘，你好像第一次真的「在」這間公司裡。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("不一定是好事。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
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
