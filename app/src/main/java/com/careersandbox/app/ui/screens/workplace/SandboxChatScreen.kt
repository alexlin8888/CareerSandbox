package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MeterDelta
import com.careersandbox.app.data.mock.SandboxChatEngineProvider
import com.careersandbox.app.data.mock.SandboxLine
import com.careersandbox.app.data.mock.SandboxTurnRequest
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch

/* =====================================================================
   模型驅動對話頁(混合架構的前端殼)
   玩家自由打字 → 送進 SandboxChatEngine(現為 mock,之後接後端)→
   渲染 NPC 回應 + 套用計量變化。前端不寫死劇情內文,內文是這次遊玩產生的。
   ===================================================================== */

private data class ChatBubble(
    val fromPlayer: Boolean,
    val text: String,
    val deltas: List<MeterDelta> = emptyList(),
)

private data class NpcMeta(val name: String, val avatar: Int, val opening: String)

private fun npcMeta(npcId: String, day: Int): NpcMeta = when (npcId) {
    "ken" -> NpcMeta("Ken", R.drawable.ken_neutral,
        "坐吧。第一週還順利嗎?分帳這案子,我想先聽聽你的想法。")
    "zhe" -> NpcMeta("阿哲", R.drawable.colleague_quiet,
        "欸,排程的事你怎麼看?兩週內上線我是覺得有風險啦。")
    "vivian" -> NpcMeta("Vivian", R.drawable.colleague_vivian,
        "客戶下週要 demo,時間壓得有點緊,想跟你討論一下。")
    else -> NpcMeta("同事", R.drawable.colleague_quiet, "嗨,聊一下?")
}

@Composable
fun SandboxConversation(
    navController: NavHostController,
    npcId: String,
    day: Int,
    opening: String? = null,
    onConcluded: () -> Unit,
) {
    val meta = remember(npcId) { npcMeta(npcId, day) }
    val sessionId = remember { "sandbox-$npcId-$day" }
    val bubbles = remember { mutableStateListOf<ChatBubble>() }
    var input by remember { mutableStateOf("") }
    var thinking by remember { mutableStateOf(false) }
    var concluded by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    // 開場白(NPC 先說一句,引導玩家自由回覆)
    LaunchedEffect(Unit) {
        if (bubbles.isEmpty()) bubbles.add(ChatBubble(false, opening ?: meta.opening))
    }
    // 訊息變動 / 思考狀態 → 自動捲到底
    LaunchedEffect(bubbles.size, thinking) { scroll.animateScrollTo(scroll.maxValue) }

    fun send() {
        val text = input.trim()
        if (text.isEmpty() || thinking || concluded) return
        bubbles.add(ChatBubble(true, text))
        input = ""
        thinking = true
        SoundManager.sfx(R.raw.sfx_tap)
        scope.launch {
            val resp = SandboxChatEngineProvider.engine.reply(
                SandboxTurnRequest(
                    sessionId = sessionId,
                    day = day,
                    npcId = npcId,
                    playerMessage = text,
                    managerTrust = WorkplaceState.managerTrust.value,
                    peerBond = WorkplaceState.peerBond.value,
                    proImage = WorkplaceState.proImage.value,
                    flags = WorkplaceState.flags.toList(),
                    history = bubbles.map { SandboxLine(it.fromPlayer, it.text) },
                ),
            )
            // 套用模型回傳的計量變化(delta=0 的不動,只當顯示用)
            resp.meterDeltas.forEach { d ->
                if (d.delta != 0) WorkplaceState.apply(d.meter, d.delta, d.reason, day)
            }
            resp.newFlags.forEach { WorkplaceState.setFlag(it) }
            bubbles.add(ChatBubble(false, resp.npcMessage, resp.meterDeltas))
            thinking = false
            if (resp.concluded) concluded = true
        }
    }

    Column(Modifier.fillMaxSize().background(PaperOff)) {
        // ===== 頂列:返回 + 對象名 + 模型驅動標記 =====
        Row(
            Modifier.fillMaxWidth().background(PaperWhite).padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(50)).pressScale { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = InkGray700, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(8.dp))
            NovaCircleAvatar(size = 38.dp, res = meta.avatar)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(meta.name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(if (thinking) "輸入中…" else "Day $day · 即時對話", color = InkGray400, fontSize = 11.sp)
            }
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(BrandAmber.copy(alpha = 0.18f))
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Text("AI 對話", color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // ===== 計量 HUD(隨對話即時變動)=====
        Row(
            Modifier.fillMaxWidth().background(PaperWhite).padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MeterPill("主管信任", WorkplaceState.managerTrust.value, BrandAmber, Modifier.weight(1f))
            MeterPill("同事情誼", WorkplaceState.peerBond.value, BrandOrange, Modifier.weight(1f))
            MeterPill("專業形象", WorkplaceState.proImage.value, AccentGreen, Modifier.weight(1f))
        }

        // ===== 對話串 =====
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll).padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            bubbles.forEach { b ->
                if (b.fromPlayer) PlayerBubble(b.text) else NpcBubble(meta, b)
                Spacer(Modifier.height(10.dp))
            }
            if (thinking) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NovaCircleAvatar(size = 30.dp, res = meta.avatar)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(16.dp)).background(PaperWhite).padding(horizontal = 14.dp, vertical = 10.dp),
                    ) { Text("…", color = InkGray500, fontSize = 16.sp, fontWeight = FontWeight.Black) }
                }
            }
            if (concluded) {
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(BrandDeepOrange).pressScale { onConcluded() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("結束這場對話 · 繼續", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }

        // ===== 輸入列(自由打字)=====
        Row(
            Modifier.fillMaxWidth().background(PaperWhite).padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(PaperWarm)
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (input.isEmpty()) {
                    Text(
                        if (concluded) "對話已結束" else "回覆 ${meta.name}…",
                        color = InkGray400, fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = input,
                    onValueChange = { if (!concluded) input = it },
                    textStyle = TextStyle(color = InkBlack, fontSize = 14.sp),
                    cursorBrush = SolidColor(BrandOrange),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.width(10.dp))
            val canSend = input.isNotBlank() && !thinking && !concluded
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50))
                    .background(if (canSend) BrandOrange else InkGray200)
                    .then(if (canSend) Modifier.clickable { send() } else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Send, contentDescription = "送出", tint = PaperWhite, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** 獨立路由版(demo / 直接進入):聊完返回上一頁 */
@Composable
fun SandboxChatScreen(
    navController: NavHostController,
    npcId: String = "zhe",
    day: Int = 3,
) {
    SandboxConversation(
        navController = navController,
        npcId = npcId,
        day = day,
        onConcluded = { navController.popBackStack() },
    )
}

@Composable
private fun PlayerBubble(text: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            Modifier.widthIn(max = 280.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .background(BrandOrange).padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(text, color = PaperWhite, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun NpcBubble(meta: NpcMeta, b: ChatBubble) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        NovaCircleAvatar(size = 32.dp, res = meta.avatar)
        Spacer(Modifier.width(8.dp))
        Column {
            Box(
                Modifier.widthIn(max = 270.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(PaperWhite).padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(b.text, color = InkBlack, fontSize = 14.sp, lineHeight = 20.sp)
            }
            // 該回合的計量變化(模型評分結果,顯示讓玩家看到因果)
            val shown = b.deltas.filter { it.delta != 0 }
            if (shown.isNotEmpty()) {
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    shown.forEach { d ->
                        val up = d.delta > 0
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background((if (up) AccentGreen else AccentRed).copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                "${d.meter} ${if (up) "+" else ""}${d.delta}",
                                color = if (up) AccentGreen else AccentRed,
                                fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeterPill(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(PaperOff).padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = InkGray700, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("$value/10", color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50)).background(InkGray200)) {
            Box(Modifier.fillMaxWidth(value / 10f).height(4.dp).clip(RoundedCornerShape(50)).background(color))
        }
    }
}
