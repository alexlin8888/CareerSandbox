package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

/* =====================================================================
   Day 4:同事午餐 —— 茶水間的軟場景
   沒有主管、沒有計時。地雷藏在閒聊裡:八卦的界線、對主管的評價。
   ===================================================================== */

private enum class LunchPhase { TALKING, CHOOSING, ENDING }

private data class LunchChoice(
    val label: String,
    val stance: String,
    val playerLine: String,
    val extraReact: Pair<String, String>? = null,
    val closing: Pair<String, String>,               // 收尾行(說話者可變)
    val moodAfter: String,                           // 氣氛:鬆 / 暖 / 尬
)

private data class LunchBeat(
    val promptSpeaker: String,
    val prompt: String,
    val choices: List<LunchChoice>,
)

private val lunchOpening = listOf(
    "小芳" to "(她壓低聲音)欸,你聽說了嗎?三樓的產品部,下個月要改組。",
    "阿凱" to "(他攪著咖啡,沒接話)",
)

private val lunchBeats = listOf(
    LunchBeat(
        "小芳", "聽說新主管是空降的,連 Ken 都還沒見過人。你覺得,咱們部門會不會也動?",
        listOf(
            LunchChoice(
                "聽,但不接力", "界線",
                "消息我收到了。不過改組這種事,等正式信吧,猜來猜去傷感情。",
                "小芳" to "(她聳聳肩)行吧,你這人真穩。",
                closing = "阿凱" to "(阿凱難得開口)……同意。",
                moodAfter = "鬆",
            ),
            LunchChoice(
                "加入猜測", "下注",
                "我猜會動。Ken 最近開會的樣子,像在準備什麼。",
                "小芳" to "(她眼睛亮了)對吧對吧。欸,這話我能往外說嗎?",
                closing = "阿凱" to "(他抬眼看了你一下,又低下去)",
                moodAfter = "尬",
            ),
            LunchChoice(
                "打聽更多", "挖",
                "你消息哪來的?還知道什麼?",
                "小芳" to "嘿,人資的小美跟我好。再說下去,要請我喝飲料了。",
                closing = "阿凱" to "(他把咖啡喝完,看了看你們)",
                moodAfter = "鬆",
            ),
        ),
    ),
    LunchBeat(
        "阿凱", "(被小芳點名,他頓了一下)……週末就,寫一點自己的小工具。自動整理測試報告的。",
        listOf(
            LunchChoice(
                "真心好奇", "靠近",
                "等等,自動整理測試報告?那不就是我們上週哀號的那個?demo 給我看。",
                "阿凱" to "(他耳朵有點紅)……午休後,到我位子。",
                closing = "小芳" to "哇,他從來沒給我看過。",
                moodAfter = "暖",
            ),
            LunchChoice(
                "客套帶過", "滑過",
                "不錯喔,很充實。",
                "阿凱" to "(他點點頭,話題就死在這裡)",
                closing = "小芳" to "(她翻了個白眼)你們兩個,乾。",
                moodAfter = "尬",
            ),
            LunchChoice(
                "順勢開玩笑", "玩",
                "工程師的週末還是在寫 code,你們是不是都這樣?",
                "阿凱" to "(他想了想)……比開會好。",
                closing = "小芳" to "(她笑出聲)這句我要記下來。",
                moodAfter = "鬆",
            ),
        ),
    ),
    LunchBeat(
        "小芳", "(她湊近)欸,說真的,你覺得 Ken 這個人怎麼樣?昨天會議,他釘你釘得不輕。",
        listOf(
            LunchChoice(
                "就事論事", "穩",
                "他釘的是方案,不是人。而且說真的,被釘完,方案有變好。",
                "小芳" to "(她想挖更多,沒挖到)嘖,無聊。",
                closing = "阿凱" to "(他嘴角動了一下)",
                moodAfter = "鬆",
            ),
            LunchChoice(
                "跟著抱怨", "洩壓",
                "真的,有時候覺得他標準高到不講理。",
                "小芳" to "對吧。放心,這話我幫你保密啦。",
                closing = "阿凱" to "(他安靜地看著你們兩個)",
                moodAfter = "尬",
            ),
            LunchChoice(
                "反問她", "回拋",
                "你先說,你覺得呢?",
                "小芳" to "我?我覺得他可怕又可靠啊。好啦,不聊這個。",
                closing = "阿凱" to "(他看了看牆上的鐘)",
                moodAfter = "鬆",
            ),
        ),
    ),
    LunchBeat(
        "阿凱", "(他站起來)十二點五十了。……下午,測試報告的事,找我。",
        listOf(
            LunchChoice(
                "定下來", "接住",
                "好,兩點半,我帶著問題去。",
                "阿凱" to "(他點頭,走兩步又回頭)……帶你的筆電。",
                closing = "小芳" to "(她小聲)他這樣,是真的要教你欸。",
                moodAfter = "暖",
            ),
            LunchChoice(
                "模糊帶過", "飄",
                "好啊好啊,有空一定。",
                "阿凱" to "(他「嗯」了一聲,門關上)",
                closing = "小芳" to "「有空」在他的字典裡,就是不會來的意思。",
                moodAfter = "尬",
            ),
            LunchChoice(
                "順便揪小芳", "擴圈",
                "一起吧,小芳上次不是也想看自動報表?",
                "小芳" to "咦,我嗎?……好啊。",
                closing = "阿凱" to "(他停了一秒)……人多,也行。",
                moodAfter = "暖",
            ),
        ),
    ),
)

private fun lunchSpeakerColor(name: String): Color = when (name) {
    "小芳" -> BrandOrange
    "阿凱" -> AccentBlue
    else -> InkCharcoal
}

@Composable
fun WorkplaceLunchScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf(LunchPhase.TALKING) }
    var beatIdx by remember { mutableIntStateOf(0) }
    var speaker by remember { mutableStateOf(lunchOpening[0].first) }
    var fullText by remember { mutableStateOf(lunchOpening[0].second) }
    var typed by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("鬆") }
    val pendingLines = remember {
        mutableStateListOf<Pair<String, String>>().apply {
            addAll(lunchOpening.drop(1))
            add(lunchBeats[0].promptSpeaker to lunchBeats[0].prompt)
        }
    }
    var awaitingChoice by remember { mutableStateOf(true) }
    var queuedMood by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fullText) {
        typed = ""
        for (i in fullText.indices) {
            if (typed.length >= fullText.length) break
            typed = fullText.substring(0, i + 1)
            delay(26)
        }
    }

    fun advance() {
        if (typed.length < fullText.length) { typed = fullText; return }
        if (phase != LunchPhase.TALKING) return
        if (pendingLines.isNotEmpty()) {
            val (s, t) = pendingLines.removeAt(0)
            speaker = s; fullText = t
            if (pendingLines.isEmpty()) {
                queuedMood?.let { m -> mood = m }; queuedMood = null
            }
        } else if (awaitingChoice) {
            phase = LunchPhase.CHOOSING
        } else if (beatIdx < lunchBeats.lastIndex) {
            beatIdx++
            awaitingChoice = true
            speaker = lunchBeats[beatIdx].promptSpeaker
            fullText = lunchBeats[beatIdx].prompt
        } else {
            phase = LunchPhase.ENDING
        }
    }

    fun choose(c: LunchChoice) {
        awaitingChoice = false
        queuedMood = c.moodAfter
        pendingLines.clear()
        pendingLines.add("你" to c.playerLine)
        c.extraReact?.let { pendingLines.add(it) }
        pendingLines.add(c.closing)
        phase = LunchPhase.TALKING
        val (s, t) = pendingLines.removeAt(0)
        speaker = s; fullText = t
    }

    // 活躍說話者的呼吸微動(軟場景的場面語言)
    val breath = rememberInfiniteTransition(label = "lunchBreath")
    val br by breath.animateFloat(
        initialValue = 1f, targetValue = 1.015f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "br",
    )

    Box(Modifier.fillMaxSize().background(InkCharcoal)) {
        Column(Modifier.fillMaxSize()) {
            // ===== 舞台:茶水間 + 雙同事 =====
            Box(Modifier.fillMaxWidth().weight(1f)) {
                Image(
                    painter = painterResource(R.drawable.bg_pantry),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(Modifier.fillMaxSize().background(InkCharcoal.copy(alpha = 0.30f)))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite)
                    }
                    Column {
                        Text("同事午餐",
                            color = PaperWhite, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Text("週四 12:30 ・ 茶水間",
                            color = PaperWhite.copy(alpha = 0.55f),
                            style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.padding(end = 8.dp).clip(RoundedCornerShape(50))
                            .background(PaperWhite.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("氣氛 ・ $mood",
                            color = PaperWhite.copy(alpha = 0.85f),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 左:小芳
                val fangActive = speaker == "小芳"
                val fa by animateFloatAsState(if (fangActive) 1f else 0.5f, label = "fa")
                val fs by animateFloatAsState(if (fangActive) 1f else 0.9f, label = "fs")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 6.dp, bottom = 6.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.colleague_gossip),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp).alpha(fa)
                            .scale(if (fangActive) fs * br else fs),
                    )
                    Box(
                        Modifier.width(96.dp).height(12.dp).clip(CircleShape)
                            .background(InkBlack.copy(alpha = 0.5f)),
                    )
                }

                // 右:阿凱
                val kaiActive = speaker == "阿凱"
                val ka by animateFloatAsState(if (kaiActive) 1f else 0.5f, label = "ka")
                val ks by animateFloatAsState(if (kaiActive) 1f else 0.9f, label = "ks")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 6.dp, bottom = 6.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.colleague_quiet),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp).alpha(ka)
                            .scale(if (kaiActive) ks * br else ks),
                    )
                    Box(
                        Modifier.width(96.dp).height(12.dp).clip(CircleShape)
                            .background(InkBlack.copy(alpha = 0.5f)),
                    )
                }
            }

            // ===== ADV 對話面板 =====
            Column(
                Modifier.fillMaxWidth().background(PaperOff)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                        .background(PaperWhite)
                        .clickable { advance() }
                        .padding(16.dp)
                        .heightIn(min = 96.dp),
                ) {
                    Text(speaker,
                        color = lunchSpeakerColor(speaker),
                        fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(typed, color = InkBlack, fontSize = 15.sp, lineHeight = 24.sp)
                    if (typed.length >= fullText.length && phase == LunchPhase.TALKING) {
                        Spacer(Modifier.height(4.dp))
                        val blink = rememberInfiniteTransition(label = "lunchAdv")
                        val a by blink.animateFloat(
                            initialValue = 0.25f, targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
                            label = "lunchBlink",
                        )
                        Text("▼", color = BrandOrange, fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End).alpha(a))
                    }
                }

                AnimatedVisibility(
                    visible = phase == LunchPhase.CHOOSING,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(260)) { it / 3 },
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Text("你會怎麼接",
                            color = InkGray500,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        lunchBeats[beatIdx].choices.forEach { c ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PaperWhite)
                                    .pressScale { choose(c) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(c.label, color = InkBlack,
                                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                    modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    Modifier.clip(RoundedCornerShape(50))
                                        .background(BrandPeach.copy(alpha = 0.55f))
                                        .padding(horizontal = 9.dp, vertical = 3.dp),
                                ) {
                                    Text(c.stance, color = BrandDeepOrange,
                                        fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        // ===== 收尾 =====
        AnimatedVisibility(
            visible = phase == LunchPhase.ENDING,
            enter = fadeIn(tween(700)),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(InkCharcoal.copy(alpha = 0.97f))
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("午休結束",
                    color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(Modifier.height(10.dp))
                Text("十五分鐘的閒聊。有人記住了你的界線,有人記住了你的溫度。",
                    color = PaperWhite.copy(alpha = 0.75f), fontSize = 13.sp,
                    lineHeight = 19.sp)
                Spacer(Modifier.height(4.dp))
                Text("痕跡,週五揭曉。",
                    color = BrandOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(28.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandOrange)
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("回到路徑", color = PaperWhite, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
