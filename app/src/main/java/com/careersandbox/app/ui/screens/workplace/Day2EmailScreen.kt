package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 2 · 週二：Email 風暴日（NovaMail 開信視圖,逐封處理）
   真實打底：新人第一週就被信箱淹沒。考驗分得清輕重、不亂把球丟給同事。
   每封信 = 開信視圖 + 處理選項;分流準度 → 專業形象,亂丟給 Vivian / 漏接
   真急件各有旗標。對齊你 Claude Design 的「郵件場景」版面。
   ===================================================================== */

private data class Day2Mail(
    val sender: String,
    val email: String,
    val avatarLetter: String,
    val avatarColor: Color,
    val subject: String,
    val folder: String,
    val folderColor: Color,
    val body: String,
    val choices: List<DecisionChoice>,
)

@Composable
fun Day2EmailScreen(navController: NavHostController) {
    val audioCtx = LocalContext.current
    LaunchedEffect(Unit) { WorkplaceState.currentDay.value = 2; WorkplaceState.beginAppPhase(2); SoundManager.playBgm(audioCtx, R.raw.bgm_neutral) }
    var idx by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }
    var taskStarted by remember { mutableStateOf(false) }

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    val red = Color(0xFFEA4335)
    val mails = listOf(
        Day2Mail(
            "Ken", "ken@novapay.com", "K", Color(0xFF1A73E8),
            "分帳的事", "收件匣", red,
            "今天 5 點前，給我一個能對客戶交代的版本。不用完美，但要能講。\n\n— K",
            buildList {
                add(DecisionChoice("A", "現在回：整理工程跟業務的說法，5 點前給您。",
                    "專業形象", 1, "", "d2_reply_ken"))
                add(DecisionChoice("B", "標記稍後，先看別的。",
                    "專業形象", -1, "主管的信，就這樣擺著"))
                if (WorkplaceState.hasFlag("intel_d2_mail")) {
                    add(DecisionChoice("C", "我查過往來信，客戶上週已經同意分階段。5 點前給您 demo 版的範圍，完整版排下個迭代。",
                        "主管信任", 2, "Ken 回了一個字：好", "d2_informed"))
                }
            },
        ),
        Day2Mail(
            "阿哲", "jhe@novapay.com", "哲", Color(0xFF12B5A5),
            "Re: 分帳 bug 進度", "工作", Color(0xFFF59E0B),
            "race condition 我還在追，別逼我給假的日期。下午會議我會說明。\n\n— 阿哲",
            listOf(
                DecisionChoice("A", "回他：了解，我不催日期，會議上一起看。",
                    "同事情誼", 1, "", "d2_respect_eng"),
                DecisionChoice("B", "回他：客戶等不了，今天給我一個日期。",
                    "同事情誼", -1, "他已讀，沒回", "d2_push_eng"),
            ),
        ),
        Day2Mail(
            "Vivian", "vivian@novapay.com", "V", Color(0xFFEC4899),
            "客戶問 API 匯出規格", "收件匣", red,
            "客戶問資料匯出的 API 規格，這part我真的看不懂…幫忙看一下要找誰？很急拜託拜託🙏",
            listOf(
                DecisionChoice("A", "這要問工程，我幫你 tag 阿哲。",
                    "專業形象", 1, ""),
                DecisionChoice("B", "這你自己問工程吧。",
                    "同事情誼", -1, "她隔了很久才回一個「…好」", "d2_dump_vivian"),
                DecisionChoice("C", "自己掰一個規格回她。",
                    "專業形象", -1, "這規格對不對，你其實不確定"),
            ),
        ),
        Day2Mail(
            "系統通知", "noreply@novapay.com", "!", Color(0xFFEF4444),
            "【緊急】後台帳號已鎖定", "工作", Color(0xFFF59E0B),
            "你的後台帳號因連續三次密碼錯誤已鎖定。客戶資料目前無法調出，相關查詢受影響。\n\n要解鎖，得去求 IT，或者問阿哲——那個你今天可能剛得罪的人。",
            listOf(
                DecisionChoice("A", "拉下臉，去問阿哲幫忙解。",
                    "專業形象", 2, "你抓到了真正要緊的那封"),
                DecisionChoice("B", "先標記，等手上的弄完再看。",
                    "專業形象", -2, "客戶資料還是調不出來", "d2_miss_urgent"),
            ),
        ),
        Day2Mail(
            "媽", "mom@home", "媽", Color(0xFFE0A04A),
            "Fwd: 久坐傷身,記得起來走走", "收件匣", red,
            "看到這篇想到你。中午有沒有好好吃飯？不要又一杯咖啡撐一天。",
            listOf(
                DecisionChoice("A", "回個貼圖：好啦我會吃，媽。", "同事情誼", 0, ""),
                DecisionChoice("B", "晚點再回，先把信清完。", "同事情誼", 0, ""),
            ),
        ),
    )

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

    val m = mails[idx]
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxSize()) {
            // ===== 頂部動作列 =====
            Row(
                Modifier.fillMaxWidth().padding(start = 8.dp, end = 14.dp, top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(42.dp).clip(CircleShape).clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("←", color = Color(0xFF444444), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Text("${idx + 1} / ${mails.size}", color = Color(0xFF80868B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // ===== 信件內容(可捲)=====
            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Text(m.subject, color = Color(0xFF202124), fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 31.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(m.folderColor.copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(m.folderColor))
                    Spacer(Modifier.width(6.dp))
                    Text(m.folder, color = m.folderColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(42.dp).clip(CircleShape).background(m.avatarColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(m.avatarLetter, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(m.sender, color = Color(0xFF202124), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Text("<${m.email}>", color = Color(0xFF80868B), fontSize = 13.sp)
                        }
                        Text("寄給 我", color = Color(0xFF80868B), fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(m.body, color = Color(0xFF202124), fontSize = 15.sp, lineHeight = 25.sp)
            }

            // ===== 處理選項 =====
            Column(
                Modifier.fillMaxWidth().background(Color(0xFFF7F7F5))
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
            ) {
                Text("怎麼處理這封?", color = Color(0xFF6B7280), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                m.choices.forEach { c ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White)
                            .clickable {
                                if (c.repDelta != 0) {
                                    repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 2)
                                }
                                c.flag?.let { WorkplaceState.setFlag(it) }
                                if (idx < mails.lastIndex) idx++ else done = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(28.dp).clip(CircleShape).background(Color(0x1FF2531C)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(c.letter, color = Color(0xFFF2531C), fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(c.label, color = Color(0xFF202124), fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // ===== 計量彈窗 =====
        AnimatedVisibility(
            visible = repPop != null,
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp),
        ) {
            repPop?.let { rc ->
                val up = rc.delta > 0
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFF281C12))
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(rc.meter, color = Color(0xFFFFF8F3), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text((if (up) "+" else "") + rc.delta,
                        color = if (up) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 14.sp, fontWeight = FontWeight.Black)
                    if (rc.reason.isNotBlank()) {
                        Spacer(Modifier.width(10.dp))
                        Text(rc.reason, color = Color(0xB3FFF8F3), fontSize = 12.sp)
                    }
                }
            }
        }
    }
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
