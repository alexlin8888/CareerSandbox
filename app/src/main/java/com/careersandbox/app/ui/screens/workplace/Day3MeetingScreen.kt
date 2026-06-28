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
   Day 3 · 週三：跨部門會議（全週高潮）
   NovaMeet 四宮格（字母格,對齊 band_2）跑對白 → 決策場景 4 拍。
   真實打底：經典「趕市場 vs 顧品質」。業務沒問工程就對客戶把日期講死、
   工程測試沒過不簽、你被夾成「兩個資深之間戰爭的工具」、對工程 imposter
   syndrome。分階段上線是真實好解,但沒有零成本選項。
   ===================================================================== */

private data class Caption(val who: String, val line: String)
private data class D3Beat(val narration: String, val choices: List<DecisionChoice>)

@Composable
fun Day3MeetingScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 3; WorkplaceState.beginAppPhase(3); SoundManager.playBgm(audioCtx, R.raw.bgm_tense) }
    var phase by remember { mutableStateOf("meeting") } // meeting | decision | done
    var capIdx by remember { mutableIntStateOf(0) }
    var beat by remember { mutableIntStateOf(0) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }
    var reaction by remember { mutableStateOf<Int?>(null) }
    var agendaSeen by rememberSaveable { mutableStateOf(false) }
    var taskStarted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    val captions = listOf(
        Caption("Vivian", "客戶那邊我已經說月底了，這個一定要上。"),
        Caption("阿哲", "測試才跑六成，race condition 沒解。我不簽。"),
        Caption("Ken", "先聽聽 PM 的判斷。"),
    )

    val beats = listOf(
        D3Beat(
            "Vivian 先開口，語速很快：「客戶真的等不了了，這個月底一定要上，我已經跟他們講了。」阿哲沒抬頭，聲音不大：「…這版品質沒到。現在上，會出事。」會議室安靜了兩秒。Ken 轉頭看你：「你呢？你看了一個禮拜。你怎麼看？」你才來五天。",
            buildList {
                add(DecisionChoice("A", "我想先看 bug 的實際範圍跟測試覆蓋率，再判斷能不能上。",
                    "專業形象", 2, "", "d3_data"))
                add(DecisionChoice("B", "先上，邊上邊修，出問題再 hotfix。",
                    "同事情誼", -1, "阿哲喝了一口水，沒說話", "d3_shipfast"))
                add(DecisionChoice("C", "我站工程，品質沒到就不該上。",
                    "同事情誼", 1, "你選了一邊。Ken 的表情沒變", "d3_sided_eng"))
                if (WorkplaceState.hasFlag("intel_d3_doc") && WorkplaceState.hasFlag("intel_d3_chat")) {
                    add(DecisionChoice("D", "排程要調。工程在文件裡標了風險，阿哲的負荷我也看到了。我建議砍掉次要範圍、延一週，把金流做穩。",
                        "主管信任", 3, "Ken 看了你一眼，跟剛剛不太一樣", "d3_informed"))
                }
            },
        ),
        D3Beat(
            "會議室安靜下來。三條路攤在桌上，你看著看著，發現一件事——沒有一條是乾淨的。\n\n硬上，阿哲的 race condition 沒解，出事是他的名字在 commit 上。延期，Vivian 上週跟客戶拍的胸脯就跳票，她得自己去吞。砍範圍，客戶要的東西少一半，這張單可能就飛了。\n\nKen 看著你：「我知道每條路都有人受傷。說——你要對不起誰？」",
            listOf(
                DecisionChoice("A", "對不起阿哲。先上，我跟他一起盯，出事我扛。",
                    "同事情誼", -2, "阿哲沒看你。他知道「一起盯」通常只是嘴上說說。", "d3_sacrifice_eng"),
                DecisionChoice("B", "對不起 Ken 的月底。延期，我陪 Vivian 去跟客戶解釋，不讓她一個人吞。",
                    "主管信任", -2, "Ken 在筆記本上又寫了一個字。Vivian 沒說話，但她記得是誰陪她去的。", "d3_sacrifice_vivian"),
                DecisionChoice("C", "對不起客戶。砍範圍，先交能交代的，飛了的單我們一起認。",
                    "同事情誼", -1, "Ken 點頭：「成熟。」但 Vivian 別過頭——那張單，有她的業績。", "d3_sacrifice_client"),
            ),
        ),
        D3Beat(
            "Ken 在筆記本上寫了個字：「假設月底還是得交點東西。這個功能，你會怎麼拆？」",
            listOf(
                DecisionChoice("A", "基本版先上、進階版下一版。客戶看得到東西，風險也鎖得住。",
                    "專業形象", 2, "Ken 寫字的手停了一下", "d3_phase"),
                DecisionChoice("B", "壓工程加班，月底全上。",
                    "同事情誼", -2, "阿哲的保溫瓶蓋，轉開又轉緊", "d3_burned_team"),
                DecisionChoice("C", "這超出我能定的，Ken 你決定吧。",
                    "主管信任", -2, "Ken 沒接話，又寫了個字", "d3_passed_buck"),
            ),
        ),
        D3Beat(
            "Vivian 已經對客戶把月底講死了。她看著你，眼神裡有種「拜託」。Ken 沒表態，把這題留給你。",
            listOf(
                DecisionChoice("A", "我幫你跟客戶談。月底交基本版 demo、完整版兩週後，我陪你一起講。",
                    "同事情誼", 2, "Vivian 鬆了一口氣，肩膀垮下來", "d3_backed_vivian"),
                DecisionChoice("B", "那是你對客戶的承諾，你自己處理。",
                    "同事情誼", -2, "Vivian 把筆電闔上了", "d3_left_vivian"),
            ),
        ),
        D3Beat(
            "Ken 站起來，又停住：「好。最後——你還想補什麼嗎？」這是他給你，最後一個定義自己的機會。",
            listOf(
                DecisionChoice("A", "我的判斷不一定全對。上線之後我會盯數據，有狀況馬上調。",
                    "主管信任", 2, "Ken 點頭", "d3_own"),
                DecisionChoice("B", "這都是團隊一起的功勞。",
                    "主管信任", 1, "", "d3_credit"),
                DecisionChoice("C", "抱歉我經驗還不夠，可能講得不對…",
                    "專業形象", -1, "Ken 沒接話。會議室的人開始收東西", "d3_selfdoubt"),
            ),
        ),
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
