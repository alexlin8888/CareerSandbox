package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   DayAgendaScreen —— 今日行程(開場給一天全貌)
   每天進場先看到時間線：幾點做什麼、哪段是翻 app、哪段是要做決定。
   讓一天有骨架、不會覺得太短。資料用 agendaForDay(day) 集中管理。
   ===================================================================== */

private enum class AgendaKind { MEETING, APP, DECISION, BREAK, REVIEW }

private data class AgendaEvent(
    val time: String,
    val title: String,
    val desc: String,
    val kind: AgendaKind,
)

private fun kindColor(k: AgendaKind): Color = when (k) {
    AgendaKind.MEETING -> BrandAmber
    AgendaKind.APP -> AccentBlue
    AgendaKind.DECISION -> BrandOrange
    AgendaKind.BREAK -> InkGray400
    AgendaKind.REVIEW -> AccentGreen
}

private fun kindLabel(k: AgendaKind): String = when (k) {
    AgendaKind.MEETING -> "會面"
    AgendaKind.APP -> "翻 app"
    AgendaKind.DECISION -> "做決定"
    AgendaKind.BREAK -> "休息"
    AgendaKind.REVIEW -> "回顧"
}

private data class DayAgenda(val label: String, val title: String, val events: List<AgendaEvent>)

private fun agendaForDay(day: Int): DayAgenda = when (day) {
    1 -> DayAgenda(
        "週一 · DAY 1", "到職第一天",
        listOf(
            AgendaEvent("09:00", "報到 · 領設備", "HR 帶你認識環境，坐定位置。", AgendaKind.MEETING),
            AgendaEvent("09:30", "晨間訊息與信件", "前任交接、同事私訊，先掌握分帳的狀況。", AgendaKind.APP),
            AgendaEvent("10:30", "與 Ken 的一對一", "主管要你對分帳上線給出判斷。", AgendaKind.DECISION),
            AgendaEvent("12:00", "午休", "喘口氣，消化一下第一個早上。", AgendaKind.BREAK),
            AgendaEvent("17:30", "第一天回顧", "收個尾，想想今天學到什麼。", AgendaKind.REVIEW),
        ),
    )
    2 -> DayAgenda(
        "週二 · DAY 2", "信箱風暴",
        listOf(
            AgendaEvent("09:00", "打開信箱", "五封信等你回，先分清楚哪封急。", AgendaKind.APP),
            AgendaEvent("10:00", "回覆業務與工程", "怎麼回，決定了你站在誰那邊。", AgendaKind.DECISION),
            AgendaEvent("12:00", "午休", "今天信很多，先休息一下。", AgendaKind.BREAK),
            AgendaEvent("15:00", "跟進回信", "確認大家都對齊了沒。", AgendaKind.APP),
            AgendaEvent("17:30", "收尾", "把今天的信都收乾淨。", AgendaKind.REVIEW),
        ),
    )
    3 -> DayAgenda(
        "週三 · DAY 3", "跨部門會議",
        listOf(
            AgendaEvent("09:00", "會前準備", "看行事曆、翻決議、開會議，備好你的版本。", AgendaKind.APP),
            AgendaEvent("11:00", "跨部門會議", "分帳範圍當場定生死。", AgendaKind.DECISION),
            AgendaEvent("12:30", "午休", "會開完了，喘口氣。", AgendaKind.BREAK),
            AgendaEvent("14:00", "整理會議決議", "貼到決議，讓大家看到結論。", AgendaKind.APP),
            AgendaEvent("17:30", "收尾", "今天的對齊收個尾。", AgendaKind.REVIEW),
        ),
    )
    4 -> DayAgenda(
        "週四 · DAY 4", "同事午餐",
        listOf(
            AgendaEvent("09:00", "晨間動態", "小芳發了限動，群組在約午餐。", AgendaKind.APP),
            AgendaEvent("12:00", "同事午餐", "飯桌上的選擇，也是一種政治。", AgendaKind.DECISION),
            AgendaEvent("14:00", "回到工作", "午餐後繼續推進度。", AgendaKind.MEETING),
            AgendaEvent("16:00", "下午的訊息", "午餐後的一些餘波。", AgendaKind.APP),
            AgendaEvent("17:30", "收尾", "今天的人際收個尾。", AgendaKind.REVIEW),
        ),
    )
    else -> DayAgenda(
        "週五 · DAY 5", "週五回顧",
        listOf(
            AgendaEvent("09:00", "看本週數據", "週報、團隊，看自己現在的位置。", AgendaKind.APP),
            AgendaEvent("11:00", "與 Ken 結算這週", "誠實面對這週你變成什麼樣的人。", AgendaKind.DECISION),
            AgendaEvent("12:00", "午休", "這週要結束了。", AgendaKind.BREAK),
            AgendaEvent("16:00", "寫週報", "不漂白地交出去。", AgendaKind.REVIEW),
            AgendaEvent("17:30", "下班", "第一週，結束了。", AgendaKind.REVIEW),
        ),
    )
}

@Composable
fun DayAgendaScreen(day: Int, onStart: () -> Unit) {
    val agenda = agendaForDay(day)
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        // 暖色漸層背景
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0xFF2A1A10), 0.5f to Color(0xFF3A2414), 1f to Color(0xFF24160C),
                ),
            ),
        )

        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(64.dp))

            // ===== 標頭 =====
            Text(agenda.label, color = BrandAmber, fontSize = 12.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text(agenda.title, color = PaperWhite, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            Text("今日行程", color = Color(0xCCFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(28.dp))

            // ===== 時間線 =====
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)) {
                agenda.events.forEachIndexed { i, e ->
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        // 時間
                        Text(
                            e.time, color = Color(0xB3FFFFFF), fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(46.dp).padding(top = 14.dp),
                        )
                        // 軌道(線 + 圓點)
                        Box(Modifier.width(22.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                            Box(Modifier.width(2.dp).fillMaxHeight().background(Color(0x33FFFFFF)))
                            Box(
                                Modifier.padding(top = 13.dp).size(13.dp).clip(CircleShape)
                                    .background(kindColor(e.kind)),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        // 內容卡
                        Column(
                            Modifier.weight(1f).padding(bottom = 14.dp)
                                .clip(RoundedCornerShape(14.dp)).background(Color(0x14FFFFFF))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(e.title, color = PaperWhite, fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Box(
                                    Modifier.clip(RoundedCornerShape(50)).background(kindColor(e.kind).copy(alpha = 0.22f))
                                        .padding(horizontal = 9.dp, vertical = 3.dp),
                                ) {
                                    Text(kindLabel(e.kind), color = kindColor(e.kind), fontSize = 10.sp,
                                        fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(e.desc, color = Color(0xB3FFFFFF), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ===== 開始按鈕 =====
            Row(
                Modifier.fillMaxWidth().padding(bottom = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(BrandOrange, BrandDeepOrange)))
                    .pressScale { onStart() }
                    .padding(vertical = 17.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("開始今天", color = PaperWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
