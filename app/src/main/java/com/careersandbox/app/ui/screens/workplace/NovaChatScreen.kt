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

private val novaChatScript = listOf(
    ChatMsg("阿哲", "早，分帳功能金流串接卡關,有個 bug 一直測不完。", "14:00", true),
    ChatMsg("阿哲", "下週一上線不可能,至少再兩週。", "14:01", true),
    ChatMsg("你", "這麼嚴重？業務那邊知道了嗎", "14:01", false),
    ChatMsg("你", "Vivian 一直催,她說客戶不能跳票", "14:01", false),
    ChatMsg("阿哲", "我先看一下狀況,等等回你。", "14:02", true),
)

@Composable
fun NovaChatScreen(navController: NavHostController) {
    var shown by remember { mutableStateOf(0) }
    val scroll = rememberScrollState()
    val lastOutIdx = remember { novaChatScript.indexOfLast { !it.incoming } }
    var draft by remember { mutableStateOf("") }
    val extraMsgs = remember { mutableStateListOf<ChatMsg>() }

    // 訊息逐則出現（對方訊息前先「輸入中」一下）
    LaunchedEffect(Unit) {
        while (shown < novaChatScript.size) {
            delay(if (novaChatScript[shown].incoming) 1200L else 650L)
            shown++
        }
    }
    LaunchedEffect(shown) { scroll.animateScrollTo(scroll.maxValue) }
    LaunchedEffect(extraMsgs.size) { scroll.animateScrollTo(scroll.maxValue) }

    val typing = shown < novaChatScript.size && novaChatScript[shown].incoming

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
            Image(
                painter = painterResource(R.drawable.colleague_quiet),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(PaperOff),
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("阿哲", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                    Spacer(Modifier.width(4.dp))
                    Text("工程組長 · 線上", color = InkGray500, fontSize = 11.sp)
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

            novaChatScript.take(shown).forEachIndexed { i, m ->
                ChatBubble(m, showRead = (i == lastOutIdx))
                Spacer(Modifier.height(8.dp))
            }
            if (typing) TypingBubble()
            extraMsgs.forEach { m ->
                ChatBubble(m, showRead = false)
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
private fun ChatBubble(m: ChatMsg, showRead: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (m.incoming) Alignment.Start else Alignment.End,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (m.incoming) {
                Image(
                    painter = painterResource(R.drawable.colleague_quiet),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(PaperWhite),
                )
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
private fun TypingBubble() {
    Row(verticalAlignment = Alignment.Bottom) {
        Image(
            painter = painterResource(R.drawable.colleague_quiet),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(28.dp).clip(CircleShape).background(PaperWhite),
        )
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
