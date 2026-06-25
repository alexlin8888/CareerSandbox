package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* =====================================================================
   Email 風暴 v3 ——「收件匣作戰桌」
   拖放到語意目標:現在處理盤 / 暫存格 / 工程席 / 業務席
   Papers, Please 的檢查時刻 + Overcooked 的升急劑量 + 即時判定
   ===================================================================== */

private data class StormMail(
    val id: Int,
    val sender: String,
    val senderTag: String,
    val band: Color,
    val subject: String,
    val preview: String,
    val body: String,
    val clueQuote: String,
    val clues: List<String>,
    val correct: String,           // NOW / HOLD / ENG / SALES
    val verdictRight: String,
    val verdictWrong: String,
    val urgentAt: Int? = null,     // 開賽第 N 秒升急
)

private val zoneNames = mapOf(
    "NOW" to "現在處理", "HOLD" to "暫存格", "ENG" to "工程・阿哲", "SALES" to "業務・小芳",
)

private val stormDeck = listOf(
    StormMail(1, "周副理", "主管", BrandOrange,
        "下午簡報第 7 頁的數據",
        "兩點前補上最新轉換率,直接改在共用簡報。",
        "下午跟客戶的簡報,第 7 頁的轉換率還是上季的。兩點前補上最新數據,直接改在共用簡報裡。",
        "兩點前補上最新數據",
        listOf("截止今天", "主管在等"),
        "NOW",
        "主管兩點要用,只有你能補,這封不等。",
        "兩點的死線,而且只有你補得了。這封要現在處理。"),
    StormMail(4, "陳小姐", "客戶", BrandAmber,
        "結帳頁面一直轉圈",
        "刷了三次都卡在付款,急著下單。",
        "你好,我在結帳頁面刷了三次,每次都卡在付款轉圈。我今天就想下單,麻煩看一下。",
        "每次都卡在付款轉圈",
        listOf("系統問題", "不在你權限"),
        "ENG",
        "系統故障給工程,你拖著只會更慢。",
        "付款卡住是系統問題,你修不了。給工程阿哲。"),
    StormMail(10, "福委會", "全公司", InkGray400,
        "週五下午茶問卷",
        "選珍奶或咖啡,週四前填即可。",
        "週五下午茶開放投票:珍珠奶茶 vs 手沖咖啡。週四下班前填完問卷即可。",
        "週四下班前填完即可",
        listOf("只是 FYI"),
        "HOLD",
        "這封能等。別讓它吃掉你的 90 秒。",
        "下午茶問卷沒有今天的死線。先放暫存,去救火。"),
    StormMail(7, "張先生", "新客戶", BrandAmber,
        "想了解企業方案報價",
        "50 人團隊,希望本週內談一次。",
        "我們是 50 人的團隊,想了解企業方案的報價與導入時程,希望本週內能談一次。",
        "想了解企業方案的報價",
        listOf("商務需求"),
        "SALES",
        "報價是業務的場子,小芳接最快。",
        "報價和導入要談判,這是業務的事。給小芳。"),
    StormMail(11, "產業週報", "電子報", InkGray400,
        "本週產業動態 #214",
        "AI 工具市場整理與五則新聞。",
        "本週重點:AI 工具市場規模整理、五則產業新聞、三場線上講座資訊。",
        "本週重點",
        listOf("只是 FYI"),
        "HOLD",
        "電子報永遠可以晚點看。",
        "週報沒有人在等你。放暫存。"),
    StormMail(3, "林經理", "客戶", BrandAmber,
        "API 文件連結還有嗎",
        "之前那份找不到了,方便再給一次嗎。",
        "之前那份 API 串接文件找不到了,方便再傳一次連結嗎?我們工程師在等。",
        "方便再傳一次連結嗎",
        listOf("客戶在等", "30 秒能回"),
        "NOW",
        "30 秒能回的就現在回,別讓客戶等一天。",
        "你手上就有連結,30 秒的事。現在回。"),
    StormMail(5, "業務小芳", "同事", InkCharcoal,
        "客戶問資料匯出的 API 規格",
        "這我看不懂,幫忙看該找誰。",
        "客戶問資料匯出的 API 規格與頻率限制,這部分我看不懂,幫忙看該找誰?",
        "API 規格與頻率限制",
        listOf("技術問題"),
        "ENG",
        "規格問題給工程,答案才會是對的。",
        "API 規格是工程的領域。給阿哲。"),
    StormMail(12, "人資部", "全公司", InkGray400,
        "年度健檢時段開放預約",
        "本月內完成預約即可。",
        "年度健檢時段開放預約,本月內完成即可,額滿會再加開。",
        "本月內完成即可",
        listOf("能等"),
        "HOLD",
        "本月內的事,不屬於這 90 秒。",
        "健檢是本月內辦就好。放暫存。"),
    StormMail(8, "王協理", "老客戶", BrandAmber,
        "合約展延的條款想談",
        "下一季續約,有兩條想調整。",
        "我們下一季想續約,但有兩條條款想調整,找個時間談?",
        "有兩條條款想調整",
        listOf("商務談判"),
        "SALES",
        "條款談判是業務的活,給小芳。",
        "續約要談條款,不在你桌上。給小芳。"),
    StormMail(6, "客服值班", "同事", InkCharcoal,
        "後台帳號被鎖了",
        "三次密碼錯誤,客戶資料調不出來。",
        "後台帳號被鎖了,三次密碼錯誤。現在客戶資料調不出來,前線卡住。",
        "現在客戶資料調不出來",
        listOf("擋住別人", "權限問題"),
        "ENG", 
        "解鎖權限在工程手上,而且前線正卡著。",
        "帳號只有工程能解,客服正被擋著。給阿哲。",
        urgentAt = 55),
    StormMail(2, "財務部", "財務", InkGray700,
        "請款單據今天 17:00 截止",
        "本月報帳收件最後一天。",
        "提醒:本月請款單據收件今天 17:00 截止,逾期併入下月,差旅與廠商款項都會延一個月。",
        "今天 17:00 截止",
        listOf("截止今天", "看起來無聊"),
        "NOW",
        "最無聊的信,往往掛著最硬的死線。",
        "這封看起來無聊,但 17:00 一過,你的報帳就消失一個月。要現在處理。",
        urgentAt = 40),
    StormMail(9, "展會主辦", "廠商", InkGray400,
        "年度展會贊助方案",
        "三種級距,本季截止。",
        "年度產業展贊助方案:三種級距與曝光內容,本季內回覆即可。",
        "本季內回覆即可",
        listOf("商務合作", "能等"),
        "SALES",
        "合作案給業務評估,不用你拍板。",
        "贊助合作讓業務去評。給小芳。"),
)

private data class Judgment(val mail: StormMail, val pick: String, val right: Boolean)

@Composable
fun WorkplaceEmailScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf("BRIEF") }      // BRIEF / PRACTICE / LIVE / REVIEW
    val deck = remember { mutableStateListOf<StormMail>().apply { addAll(stormDeck) } }
    val judged = remember { mutableStateListOf<Judgment>() }
    var elapsed by remember { mutableIntStateOf(0) }
    var urgentBanner by remember { mutableStateOf<String?>(null) }
    val urgentIds = remember { mutableStateListOf<Int>() }

    // 判定條
    var verdictGood by remember { mutableStateOf(true) }
    var verdictText by remember { mutableStateOf("") }
    var verdictKey by remember { mutableIntStateOf(0) }
    var verdictShown by remember { mutableStateOf(false) }
    LaunchedEffect(verdictKey) {
        if (verdictKey > 0) { verdictShown = true; delay(1700); verdictShown = false }
    }

    // 計時:LIVE 開始跑;升急腳本
    LaunchedEffect(phase) {
        if (phase == "LIVE") {
            elapsed = 0
            while (elapsed < 90) {
                delay(1000); elapsed++
                stormDeck.forEach { m ->
                    if (m.urgentAt == elapsed && deck.any { it.id == m.id }) {
                        urgentIds.add(m.id)
                        // 升急的信插到牌堆最上面
                        deck.removeAll { it.id == m.id }
                        deck.add(0, m)
                        urgentBanner = "有一封信變急了"
                    }
                }
                if (urgentBanner != null && elapsed % 3 == 0) urgentBanner = null
            }
            if (phase == "LIVE") phase = "REVIEW"
        }
    }
    LaunchedEffect(deck.size, phase) {
        if (phase == "LIVE" && deck.isEmpty()) { delay(600); phase = "REVIEW" }
    }
    var repSettled by remember { mutableStateOf(false) }
    LaunchedEffect(phase) {
        if (phase == "REVIEW" && !repSettled) {
            repSettled = true
            val correct = judged.count { it.right }
            val wrong = judged.size - correct
            // 收件匣處理的準度 → 專業形象(對 +,錯扣一點,淨值結算)
            val delta = (correct - wrong).coerceIn(-3, 3)
            if (delta != 0) {
                WorkplaceState.apply("專業形象", delta,
                    "收件匣處理:$correct 對 $wrong 錯", day = 2)
            }
        }
    }

    fun drop(mail: StormMail, zone: String) {
        val right = mail.correct == zone
        judged.add(Judgment(mail, zone, right))
        deck.removeAll { it.id == mail.id }
        verdictGood = right
        verdictText = if (right) mail.verdictRight else mail.verdictWrong
        verdictKey++
    }

    Box(Modifier.fillMaxSize().background(PaperWarm)) {
        when (phase) {
            "BRIEF" -> BriefCard(
                onStart = { phase = "PRACTICE" },
                onExit = { navController.popBackStack() },
            )
            "PRACTICE" -> PracticePhase(onDone = { phase = "LIVE" })
            "LIVE" -> WarDesk(
                deck = deck,
                judgedCount = judged.size,
                elapsed = elapsed,
                urgentIds = urgentIds,
                urgentBanner = urgentBanner,
                verdictShown = verdictShown,
                verdictGood = verdictGood,
                verdictText = verdictText,
                onDrop = { m, z -> drop(m, z) },
                onExit = { navController.popBackStack() },
            )
            "REVIEW" -> StormReview(
                judged = judged,
                elapsed = elapsed,
                onAgain = {
                    deck.clear(); deck.addAll(stormDeck)
                    judged.clear(); urgentIds.clear()
                    phase = "BRIEF"
                },
                onBack = { navController.popBackStack() },
            )
        }
    }
}

/* ───────────────────────── 任務簡報 ───────────────────────── */

@Composable
private fun BriefCard(onStart: () -> Unit, onExit: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.beaver_point),
            contentDescription = null,
            modifier = Modifier.size(110.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text("主管出差,你代管收件匣", color = InkBlack, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("12 封信、90 秒。每封信拖到對的地方。",
            color = InkGray700, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface).padding(18.dp),
        ) {
            RuleRow(InkBlack, "現在處理", "只有你能做,而且不能等")
            RuleRow(InkGray400, "暫存格", "能等的,先放著")
            RuleRow(BrandDeepOrange, "轉交", "別人做更對,而且要轉給對的人")
        }
        Spacer(Modifier.height(14.dp))
        Text("判斷力比手速重要。", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(28.dp))
        Box(
            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                .background(InkBlack).pressScale(onClick = onStart),
            contentAlignment = Alignment.Center,
        ) { Text("先暖身兩封", color = PaperWhite, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.clip(RoundedCornerShape(50)).pressScale(onClick = onExit).padding(12.dp)) {
            Text("先離開", color = InkGray500, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RuleRow(dot: Color, name: String, desc: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(dot))
        Spacer(Modifier.width(10.dp))
        Text(name, color = InkBlack, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.width(10.dp))
        Text(desc, color = InkGray500, fontSize = 12.sp)
    }
}

/* ───────────────────────── 暖身(兩封) ───────────────────────── */

private val practiceMails = listOf(
    StormMail(101, "教練", "暖身", BrandOrange,
        "把我拖到「現在處理」盤",
        "右下角那個黑色的盤子。",
        "這是暖身信。按住卡片,拖到右下角的「現在處理」盤,鬆手。",
        "拖到右下角", listOf("暖身 1 / 2"), "NOW",
        "手感對了。", "差一點。這封要去右下角的黑盤。"),
    StormMail(102, "教練", "暖身", BrandOrange,
        "這封是技術問題,給工程",
        "拖到上面工程阿哲的頭上。",
        "客戶說系統壞了,這種信給工程。把卡片拖到上面工程・阿哲的席位。",
        "拖到工程席", listOf("暖身 2 / 2"), "ENG",
        "對,轉交就是拖到人頭上。", "技術問題要給工程,拖到阿哲頭上。"),
)

@Composable
private fun PracticePhase(onDone: () -> Unit) {
    val deck = remember { mutableStateListOf<StormMail>().apply { addAll(practiceMails) } }
    var verdictGood by remember { mutableStateOf(true) }
    var verdictText by remember { mutableStateOf("") }
    var verdictKey by remember { mutableIntStateOf(0) }
    var verdictShown by remember { mutableStateOf(false) }
    LaunchedEffect(verdictKey) {
        if (verdictKey > 0) { verdictShown = true; delay(1300); verdictShown = false }
    }
    LaunchedEffect(deck.size) {
        if (deck.isEmpty()) { delay(900); onDone() }
    }
    WarDesk(
        deck = deck,
        judgedCount = 2 - deck.size,
        elapsed = -1,                 // -1 = 暖身,不顯示計時
        urgentIds = emptyList(),
        urgentBanner = null,
        verdictShown = verdictShown,
        verdictGood = verdictGood,
        verdictText = verdictText,
        onDrop = { m, z ->
            val right = m.correct == z
            verdictGood = right
            verdictText = if (right) m.verdictRight else m.verdictWrong
            verdictKey++
            if (right) deck.removeAll { it.id == m.id }
        },
        onExit = { onDone() },
        practice = true,
    )
}

/* ───────────────────────── 作戰桌 ───────────────────────── */

@Composable
private fun WarDesk(
    deck: List<StormMail>,
    judgedCount: Int,
    elapsed: Int,
    urgentIds: List<Int>,
    urgentBanner: String?,
    verdictShown: Boolean,
    verdictGood: Boolean,
    verdictText: String,
    onDrop: (StormMail, String) -> Unit,
    onExit: () -> Unit,
    practice: Boolean = false,
) {
    val zoneRects = remember { mutableStateMapOf<String, Rect>() }
    var hovered by remember { mutableStateOf<String?>(null) }
    val current = deck.firstOrNull()

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(14.dp))
        // 標頭
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(38.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface).pressScale(onClick = onExit),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Close, contentDescription = null, tint = InkGray700, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(if (practice) "暖身 ・ 拖拖看" else "Email 風暴", color = InkBlack,
                    fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("職場沙盒 ・ 模擬場景", color = InkGray400, fontSize = 10.sp)
            }
            Spacer(Modifier.weight(1f))
            if (elapsed >= 0) {
                val left = (90 - elapsed).coerceAtLeast(0)
                val hot = left <= 15
                val pulse = if (hot) {
                    val t = rememberInfiniteTransition(label = "timerHot")
                    val v by t.animateFloat(1f, 1.1f,
                        infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "tp")
                    v
                } else 1f
                Row(
                    Modifier.scale(pulse).clip(RoundedCornerShape(50))
                        .background(if (hot) BrandDeepOrange else InkBlack)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Timer, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${left / 60}:${(left % 60).toString().padStart(2, '0')}",
                        color = PaperWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // 進度刻度
        if (!practice) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(12) { i ->
                    Box(
                        Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50))
                            .background(if (i < judgedCount) BrandOrange else InkGray200),
                    )
                }
            }
        }
        // 判定條 / 升急橫幅
        Box(Modifier.fillMaxWidth().height(44.dp), contentAlignment = Alignment.Center) {
            androidx.compose.animation.AnimatedVisibility(
                visible = verdictShown,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = fadeOut(tween(250)),
            ) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(if (verdictGood) AccentGreen.copy(alpha = 0.16f) else BrandAmber.copy(alpha = 0.22f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(4.dp).height(22.dp).clip(RoundedCornerShape(50))
                        .background(if (verdictGood) AccentGreen else BrandAmber))
                    Spacer(Modifier.width(8.dp))
                    Text(verdictText, color = InkBlack, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                }
            }
            if (!verdictShown && urgentBanner != null) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(BrandAmber.copy(alpha = 0.25f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(BrandDeepOrange))
                    Spacer(Modifier.width(8.dp))
                    Text(urgentBanner, color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 轉交席
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PersonZone("ENG", "工程・阿哲", R.drawable.colleague_quiet, hovered == "ENG", zoneRects, Modifier.weight(1f))
            PersonZone("SALES", "業務・小芳", R.drawable.colleague_gossip, hovered == "SALES", zoneRects, Modifier.weight(1f))
        }

        // 信卡舞台
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            // 牌堆露頂
            if (deck.size > 1) {
                Box(
                    Modifier.fillMaxWidth(0.86f).height(120.dp)
                        .graphicsLayer { translationY = 26.dp.toPx(); scaleX = 0.94f }
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
                )
            }
            Crossfade(targetState = current?.id, label = "mailCard") { id ->
                val mail = deck.firstOrNull { it.id == id }
                if (mail != null) {
                    MailCard(
                        mail = mail,
                        urgent = urgentIds.contains(mail.id),
                        zoneRects = zoneRects,
                        onHover = { hovered = it },
                        onDrop = { z -> hovered = null; onDrop(mail, z) },
                    )
                } else if (deck.isEmpty()) {
                    Text(if (practice) "暖身完成" else "收件匣清空",
                        color = InkGray400, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 下方兩盤
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TrayZone("HOLD", "暫存格", false, hovered == "HOLD", zoneRects, Modifier.weight(1f))
            TrayZone("NOW", "現在處理", true, hovered == "NOW", zoneRects, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PersonZone(
    key: String, label: String, avatar: Int, hot: Boolean,
    rects: MutableMap<String, Rect>, modifier: Modifier = Modifier,
) {
    val s by animateFloatAsState(if (hot) 1.06f else 1f, label = "pz$key")
    Column(
        modifier = modifier
            .scale(s)
            .onGloballyPositioned { rects[key] = it.boundsInRoot() }
            .clip(RoundedCornerShape(16.dp))
            .background(if (hot) BrandPeach.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface)
            .border(
                width = if (hot) 2.dp else 1.dp,
                color = if (hot) BrandDeepOrange else InkGray200,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(avatar),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(46.dp),
        )
        Text(label, color = InkBlack, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Text("轉交", color = InkGray400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TrayZone(
    key: String, label: String, dark: Boolean, hot: Boolean,
    rects: MutableMap<String, Rect>, modifier: Modifier = Modifier,
) {
    val s by animateFloatAsState(if (hot) 1.06f else 1f, label = "tz$key")
    Box(
        modifier = modifier
            .scale(s)
            .height(58.dp)
            .onGloballyPositioned { rects[key] = it.boundsInRoot() }
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    hot && dark -> InkCharcoal
                    hot -> BrandPeach.copy(alpha = 0.7f)
                    dark -> InkBlack
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (hot) 2.dp else 1.dp,
                color = if (hot) BrandDeepOrange else if (dark) InkBlack else InkGray200,
                shape = RoundedCornerShape(16.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (dark) PaperWhite else InkGray700,
            fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun MailCard(
    mail: StormMail,
    urgent: Boolean,
    zoneRects: Map<String, Rect>,
    onHover: (String?) -> Unit,
    onDrop: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val ax = remember(mail.id) { Animatable(0f) }
    val ay = remember(mail.id) { Animatable(0f) }
    val enter = remember(mail.id) { Animatable(0.92f) }
    var expanded by remember(mail.id) { mutableStateOf(false) }
    var cardRect by remember(mail.id) { mutableStateOf<Rect?>(null) }
    var dragging by remember(mail.id) { mutableStateOf(false) }
    LaunchedEffect(mail.id) {
        enter.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    fun hitZone(): String? {
        val c = cardRect ?: return null
        val center = Offset(c.center.x + ax.value, c.center.y + ay.value)
        return zoneRects.entries.firstOrNull { it.value.contains(center) }?.key
    }

    val urgentPulse = if (urgent) {
        val t = rememberInfiniteTransition(label = "urg")
        val v by t.animateFloat(0.5f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "uv")
        v
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .onGloballyPositioned { cardRect = it.boundsInRoot() }
            .graphicsLayer {
                translationX = ax.value
                translationY = ay.value
                rotationZ = ax.value / 44f
                scaleX = enter.value
                scaleY = enter.value
            }
            .shadow(if (dragging) 20.dp else 8.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (urgent) 2.dp else 0.dp,
                color = if (urgent) BrandDeepOrange.copy(alpha = urgentPulse) else Color.Transparent,
                shape = RoundedCornerShape(18.dp),
            )
            .pointerInput(mail.id) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDrag = { change, amt ->
                        change.consume()
                        scope.launch { ax.snapTo(ax.value + amt.x) }
                        scope.launch { ay.snapTo(ay.value + amt.y) }
                        onHover(hitZone())
                    },
                    onDragEnd = {
                        dragging = false
                        val z = hitZone()
                        if (z != null) {
                            onDrop(z)
                        } else {
                            onHover(null)
                            scope.launch { ax.animateTo(0f, spring()) }
                            scope.launch { ay.animateTo(0f, spring()) }
                        }
                    },
                    onDragCancel = {
                        dragging = false
                        onHover(null)
                        scope.launch { ax.animateTo(0f, spring()) }
                        scope.launch { ay.animateTo(0f, spring()) }
                    },
                )
            }
            .pressScale { expanded = !expanded },
    ) {
        Box(Modifier.fillMaxWidth().height(6.dp).background(mail.band))
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(mail.band.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(mail.sender.take(1), color = InkBlack, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(mail.sender, color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(mail.senderTag, color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                if (urgent) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(BrandDeepOrange)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) { Text("變急了", color = PaperWhite, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(mail.subject, color = InkBlack, fontSize = 16.sp,
                fontWeight = FontWeight.Black, lineHeight = 21.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                if (expanded) mail.body else mail.preview,
                color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp,
            )
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(BrandPeach.copy(alpha = 0.45f))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(3.dp).height(16.dp).clip(RoundedCornerShape(50)).background(BrandDeepOrange))
                    Spacer(Modifier.width(8.dp))
                    Text("關鍵句:「${mail.clueQuote}」", color = InkBlack,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                mail.clues.forEach { c ->
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(InkGray100)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) { Text(c, color = InkGray700, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.weight(1f))
                Text(if (expanded) "收合" else "點開全文", color = InkGray400, fontSize = 9.sp)
            }
        }
    }
}

/* ───────────────────────── 結算 ───────────────────────── */

@Composable
private fun StormReview(
    judged: List<Judgment>,
    elapsed: Int,
    onAgain: () -> Unit,
    onBack: () -> Unit,
) {
    val right = judged.count { it.right }
    val financeMiss = judged.firstOrNull { it.mail.id == 2 && !it.right } != null ||
        judged.none { it.mail.id == 2 }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        Text("風暴過後", color = InkGray400, fontSize = 12.sp,
            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("判斷力", color = InkBlack, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(10.dp))
            Text("$right", color = BrandDeepOrange, fontSize = 44.sp, fontWeight = FontWeight.Black)
            Text(" / 12", color = InkGray400, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(if (right >= 9) R.drawable.beaver_celebrate else R.drawable.beaver_climb),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
        }
        Text(
            "用時 ${if (elapsed >= 90) "90 秒(時間到)" else "$elapsed 秒"} ・ 速度只是配角",
            color = InkGray400, fontSize = 11.sp,
        )
        Spacer(Modifier.height(16.dp))

        if (financeMiss) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(InkBlack).padding(16.dp),
            ) {
                Text("地雷", color = BrandAmber, fontSize = 10.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "財務的請款信被你晾著,下週你的報帳會消失一個月。看起來最無聊的信,有時掛著最硬的死線。",
                    color = PaperWhite, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        Text("逐封覆盤", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        judged.forEach { j ->
            Column(
                Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(8.dp).clip(CircleShape)
                            .background(if (j.right) AccentGreen else BrandAmber),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(j.mail.subject, color = InkBlack, fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(
                        zoneNames[j.pick] ?: j.pick,
                        color = if (j.right) AccentGreen else BrandDeepOrange,
                        fontSize = 10.sp, fontWeight = FontWeight.Black,
                    )
                }
                if (!j.right) {
                    Spacer(Modifier.height(4.dp))
                    Text(j.mail.verdictWrong, color = InkGray500, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
        val skipped = stormDeck.size - judged.size
        if (skipped > 0) {
            Spacer(Modifier.height(6.dp))
            Text("還有 $skipped 封沒處理。沒做決定,也是一種決定。",
                color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                .background(InkBlack).pressScale(onClick = onAgain),
            contentAlignment = Alignment.Center,
        ) { Text("再來一輪", color = PaperWhite, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface).pressScale(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) { Text("回沙盒", color = InkGray700, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(24.dp))
    }
}
