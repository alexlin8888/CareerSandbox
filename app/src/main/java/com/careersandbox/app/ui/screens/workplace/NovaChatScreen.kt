package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

/* =====================================================================
   NovaChat 對話 —— Day2 開場：阿哲回報分帳 bug
   Nova 系列擬真介面的 Compose 參考範本（其餘畫面照此型複製）。
   擬真靠通用 UI 語言（頭像/泡泡/時間戳/已讀/輸入中），非品牌 logo。
   ===================================================================== */

private data class ChatMsg(
    val sender: String,
    val text: String,
    val time: String,
    val incoming: Boolean,   // true=對方(阿哲)，false=你
)

private data class ChatThread(
    val name: String,
    val avatar: Int?,
    val avatarLetter: String,
    val avatarBg: Color,
    val status: String,
    val script: List<ChatMsg>,
)

private val chatThreads: Map<String, ChatThread> = mapOf(
    "zhe" to ChatThread(
        "阿哲", R.drawable.colleague_quiet, "", Color(0xFFCBD5E1), "工程組長 · 線上",
        listOf(
            ChatMsg("阿哲", "早，分帳功能金流串接卡關，有個 bug 一直測不完。", "14:00", true),
            ChatMsg("阿哲", "下週一上線不可能，至少再兩週。", "14:01", true),
            ChatMsg("你", "這麼嚴重？業務那邊知道了嗎", "14:01", false),
            ChatMsg("你", "Vivian 一直催，她說客戶不能跳票", "14:01", false),
            ChatMsg("阿哲", "我先看一下狀況，等等回你。", "14:02", true),
        ),
    ),
    "vivian" to ChatThread(
        "Vivian", R.drawable.colleague_vivian, "", Color(0xFFCBD5E1), "業務 · 線上",
        listOf(
            ChatMsg("Vivian", "分帳的 demo 下週三客戶要看，時間我先答應了。", "13:55", true),
            ChatMsg("Vivian", "工程說要延，但這場真的不能跳票 🙏", "13:56", true),
            ChatMsg("你", "我了解，我去跟阿哲確認能不能先生一個 demo 版本", "13:57", false),
            ChatMsg("Vivian", "拜託你了！有你頂著我安心多了", "13:58", true),
        ),
    ),
    "group" to ChatThread(
        "產品群組 (8)", null, "群", Color(0xFFF59E0B), "8 位成員",
        listOf(
            ChatMsg("阿哲", "阿哲：我先 push 一版分帳修正，大家測一下。", "13:28", true),
            ChatMsg("Vivian", "Vivian：客戶下週要 demo，範圍能先確認嗎？", "13:29", true),
            ChatMsg("Ken", "Ken：今天 5 點前我要一份建議，誰整理？", "13:30", true),
            ChatMsg("你", "我來整理，等等貼到決議。", "13:31", false),
        ),
    ),
    "ken" to ChatThread(
        "Ken", R.drawable.ken_neutral, "", Color(0xFFCBD5E1), "你的主管 · 線上",
        listOf(
            ChatMsg("Ken", "看一下你信箱，分帳的事我寄給你了。", "13:45", true),
            ChatMsg("Ken", "5 點前給我建議，記得寫清楚理由。", "13:45", true),
            ChatMsg("你", "收到，我看完馬上回您。", "13:46", false),
        ),
    ),
    "notice" to ChatThread(
        "NovaPay 公告", null, "公", Color(0xFF6B7280), "官方帳號",
        listOf(
            ChatMsg("NovaPay 公告", "【系統維護】今晚 23:00 起例行維護，預計 30 分鐘。", "11:05", true),
            ChatMsg("NovaPay 公告", "維護期間部分服務暫停，造成不便敬請見諒。", "11:05", true),
        ),
    ),
    "mom" to ChatThread(
        "媽", null, "媽", Color(0xFFB85C3A), "家人",
        listOf(
            ChatMsg("媽", "記得吃飯，不要又熬夜。", "昨天", true),
            ChatMsg("媽", "工作再忙也要顧身體啊。", "昨天", true),
            ChatMsg("你", "知道啦，我會早點睡。", "昨天", false),
        ),
    ),
)

@Composable
fun NovaChatScreen(navController: NavHostController, chatId: String = "zhe") {
    val thread = chatThreads[chatId] ?: chatThreads.getValue("zhe")
    val script = thread.script
    var shown by remember { mutableStateOf(0) }
    val scroll = rememberScrollState()
    val lastOutIdx = remember(script) { script.indexOfLast { !it.incoming } }
    var draft by remember { mutableStateOf("") }
    val extraMsgs = remember { mutableStateListOf<ChatMsg>() }

    // 訊息逐則出現（對方訊息前先「輸入中」一下）
    LaunchedEffect(Unit) {
        while (shown < script.size) {
            delay(if (script[shown].incoming) 1200L else 650L)
            shown++
        }
    }
    LaunchedEffect(shown) { scroll.animateScrollTo(scroll.maxValue) }
    LaunchedEffect(extraMsgs.size) { scroll.animateScrollTo(scroll.maxValue) }

    val typing = shown < script.size && script[shown].incoming

    Column(Modifier.fillMaxSize().background(PaperOff)) {

        // ===== 頂部列 =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaperWhite)
                .padding(start = 2.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
            }
            NovaCircleAvatar(size = 40.dp, res = thread.avatar, letter = thread.avatarLetter, bg = thread.avatarBg)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(thread.name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                    Spacer(Modifier.width(4.dp))
                    Text(thread.status, color = InkGray500, fontSize = 11.sp)
                }
            }
        }

        // ===== 訊息區 =====
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scroll)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            // 日期膠囊
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(InkGray200)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) { Text("今天", color = InkGray700, fontSize = 11.sp) }
            }
            Spacer(Modifier.height(12.dp))

            script.take(shown).forEachIndexed { i, m ->
                ChatBubble(m, showRead = (i == lastOutIdx),
                    avatarRes = thread.avatar, avatarLetter = thread.avatarLetter, avatarBg = thread.avatarBg)
                Spacer(Modifier.height(8.dp))
            }
            if (typing) TypingBubble(thread.avatar, thread.avatarLetter, thread.avatarBg)
            extraMsgs.forEach { m ->
                ChatBubble(m, showRead = false,
                    avatarRes = thread.avatar, avatarLetter = thread.avatarLetter, avatarBg = thread.avatarBg)
                Spacer(Modifier.height(8.dp))
            }
        }

        // ===== 快速回覆 chip =====
        Row(
            Modifier.fillMaxWidth().background(PaperWhite)
                .horizontalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("我去跟業務確認排程", "先別硬上，把 bug 清完再說", "需要我幫你頂一下嗎？").forEach { q ->
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(PaperOff)
                        .pressScale { extraMsgs.add(ChatMsg("你", q, "14:03", false)) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) { Text(q, color = InkSlate, fontSize = 12.sp, maxLines = 1) }
            }
        }

        // ===== 輸入列(可互動：打字 / 送出附上你的泡泡)=====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaperWhite)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, tint = InkGray500,
                modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(PaperOff)
                    .padding(horizontal = 16.dp, vertical = 11.dp),
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    textStyle = TextStyle(color = InkBlack, fontSize = 13.sp),
                    cursorBrush = SolidColor(BrandOrange),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (draft.isEmpty()) Text("輸入訊息", color = InkGray500, fontSize = 13.sp)
                        inner()
                    },
                )
            }
            Spacer(Modifier.width(10.dp))
            val canSend = draft.isNotBlank()
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape)
                    .background(if (canSend) BrandOrange else InkGray200)
                    .pressScale { if (canSend) { extraMsgs.add(ChatMsg("你", draft.trim(), "14:03", false)); draft = "" } },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun ChatBubble(m: ChatMsg, showRead: Boolean, avatarRes: Int?, avatarLetter: String, avatarBg: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (m.incoming) Alignment.Start else Alignment.End,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (m.incoming) {
                NovaCircleAvatar(size = 28.dp, res = avatarRes, letter = avatarLetter, bg = avatarBg)
                Spacer(Modifier.width(6.dp))
            }
            Box(
                modifier = Modifier
                    .widthIn(max = 252.dp)
                    .clip(
                        if (m.incoming)
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                        else
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                    )
                    .background(if (m.incoming) PaperWhite else BrandOrange)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    m.text,
                    color = if (m.incoming) InkBlack else PaperWhite,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Row(
            modifier = Modifier.padding(start = if (m.incoming) 34.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(m.time, color = InkGray500, fontSize = 10.sp)
            if (showRead) {
                Spacer(Modifier.width(6.dp))
                Text("已讀", color = InkGray500, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun TypingBubble(avatarRes: Int?, avatarLetter: String, avatarBg: Color) {
    Row(verticalAlignment = Alignment.Bottom) {
        NovaCircleAvatar(size = 28.dp, res = avatarRes, letter = avatarLetter, bg = avatarBg)
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .background(PaperWhite)
                .padding(horizontal = 16.dp, vertical = 13.dp),
        ) {
            val tr = rememberInfiniteTransition(label = "typing")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (k in 0..2) {
                    val a by tr.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = k * 150),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "dot$k",
                    )
                    Box(Modifier.size(7.dp).clip(CircleShape).background(InkGray500.copy(alpha = a)))
                }
            }
        }
    }
}
