package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* =====================================================================
   Day 3:跨部門會議 —— 視覺小說引擎(雙立繪版)
   小芳答應了客戶、阿凱測試只跑六成,Ken 點名你表態。
   ===================================================================== */

private enum class MeetMotion { NONE, SHAKE, TILT }
private enum class MeetPhase { TALKING, CHOOSING, ENDING }

private data class MeetChoice(
    val label: String,
    val stance: String,
    val playerLine: String,
    val extraReact: Pair<String, String>? = null,   // 同事的反應(可選)
    val kenReact: String,
    val moodAfter: String,                           // 會議氣氛:緊 / 僵 / 緩 / 鬆
    val motion: MeetMotion,
    val repMeter: String = "專業形象",
    val repDelta: Int = 0,
    val repReason: String = "",
)

private data class MeetBeat(
    val promptSpeaker: String,
    val prompt: String,
    val choices: List<MeetChoice>,
)

private val meetingOpening = listOf(
    "小芳" to "先說好消息,客戶那邊我已經答應月底前上線。單子簽了。",
    "阿凱" to "(他沒抬頭)迴歸測試跑了六成。剩下的四成,全是付款流程。",
)

private val meetBeats = listOf(
    MeetBeat(
        "Ken", "(他看向你)你是 PM。月底上線,你的判斷?",
        listOf(
            MeetChoice(
                "先要數據,再表態", "穩",
                "我先確認一件事:剩下四成的測試,全力跑最快幾天?",
                "阿凱" to "五個工作天。一個都壓不掉。",
                "(他記了一筆)好,有數字了。繼續。",
                "緊", MeetMotion.TILT,
                repMeter = "專業形象", repDelta = 2, repReason = "先要數據再表態,Ken 記了一筆",
            ),
            MeetChoice(
                "順著業務", "快",
                "客戶都簽了,月底就上。測試邊上邊補。",
                "阿凱" to "(他終於抬頭)付款炸掉的時候,誰半夜起來修?",
                "你聽到工程的問題了。這不叫判斷,叫賭。",
                "僵", MeetMotion.SHAKE,
                repMeter = "專業形象", repDelta = -2, repReason = "邊上邊補=賭,不是判斷",
            ),
            MeetChoice(
                "站工程", "保守",
                "測試沒跑完就是不能上。延兩週。",
                "小芳" to "延兩週?客戶的違約金,算你的?",
                "立場可以。但你只回答了一邊的問題。",
                "僵", MeetMotion.NONE,
                repMeter = "專業形象", repDelta = -1, repReason = "立場對,但只回答了一邊",
            ),
        ),
    ),
    MeetBeat(
        "Ken", "兩邊都有理。問題是約簽了,測試跑不完。你打算怎麼拆?",
        listOf(
            MeetChoice(
                "分階段上線", "拆解",
                "月底先上非付款功能,付款模組第二週測完補上。客戶看得到東西,風險也鎖得住。",
                "小芳" to "……這個我可以去談。",
                "(他往後靠)這才像個方案。",
                "緩", MeetMotion.TILT,
                repMeter = "專業形象", repDelta = 2, repReason = "分階段:看得到東西,也鎖得住風險",
            ),
            MeetChoice(
                "加班硬趕", "燃燒",
                "工程這週加班趕測試,我陪著跑。月底全量上。",
                "阿凱" to "(他看了你兩秒)加班可以。品質,我不保證。",
                "用人的肝去填排程的洞。記住這個選擇的成本。",
                "緊", MeetMotion.NONE,
                repMeter = "同事情誼", repDelta = -1, repReason = "用同事的肝填排程的洞",
            ),
            MeetChoice(
                "丟回給主管", "上拋",
                "這超出我的層級,想先聽你的決定。",
                null,
                "(他盯著你)我找你來,就是要你的決定。……分階段。下次,這句話要從你嘴裡出來。",
                "僵", MeetMotion.SHAKE,
                repMeter = "主管信任", repDelta = -2, repReason = "Ken 要的是你的決定,不是上拋",
            ),
        ),
    ),
    MeetBeat(
        "小芳", "那違約條款怎麼辦?「分階段」三個字,合約上可沒有。我要拿什麼去跟客戶說?",
        listOf(
            MeetChoice(
                "給她武器", "補位",
                "跟客戶說:核心功能如期,付款模組多兩週是為了金流安全。這個說法,他們法務聽得進去。",
                "小芳" to "(她快速記下)可以,這個說法能用。",
                "跨部門,就是這樣補位的。",
                "鬆", MeetMotion.TILT,
                repMeter = "同事情誼", repDelta = 2, repReason = "你給了小芳能用的說法",
            ),
            MeetChoice(
                "切割", "自掃",
                "對外溝通是業務的職責,我顧好上線就好。",
                "小芳" to "(她笑了一下,不太好看)行,各掃門前雪嘛。",
                "牆,就是這樣砌起來的。",
                "僵", MeetMotion.SHAKE,
                repMeter = "同事情誼", repDelta = -2, repReason = "各掃門前雪,牆就這樣砌起來",
            ),
            MeetChoice(
                "拉主管背書", "借力",
                "請 Ken 發一封信給客戶窗口,分階段方案有主管背書,小芳比較好談。",
                null,
                "信我可以發。但下次,先想自己能不能扛,再來借我的名字。",
                "緩", MeetMotion.NONE,
                repMeter = "主管信任", repDelta = -1, repReason = "先想能不能扛,再借主管的名字",
            ),
        ),
    ),
    MeetBeat(
        "Ken", "好,就這麼定。散會前,今天這場,你給自己打幾分?",
        listOf(
            MeetChoice(
                "點自己的盲點", "自省",
                "七分。我太晚把測試數據攤出來,前面十分鐘大家在空轉。",
                null,
                "(他難得鬆了一下眉)能看見自己的洞,比方案值錢。",
                "鬆", MeetMotion.TILT,
                repMeter = "專業形象", repDelta = 1, repReason = "能看見自己的洞,比方案值錢",
            ),
            MeetChoice(
                "打高分", "自信",
                "九分。方案是我拆的。",
                null,
                "方案是大家湊的。九分,留給下次自己扛全場的時候。",
                "緊", MeetMotion.NONE,
                repMeter = "主管信任", repDelta = -1, repReason = "方案是大家湊的",
            ),
            MeetChoice(
                "謙到底", "低姿態",
                "不及格吧,全程被牽著走。",
                null,
                "過度貶低跟過度膨脹一樣,都不準。重打。",
                "緊", MeetMotion.NONE,
                repMeter = "專業形象", repDelta = -1, repReason = "過度貶低跟膨脹一樣不準",
            ),
        ),
    ),
)

private fun meetKenSprite(mood: String): Int = when (mood) {
    "緩", "鬆" -> R.drawable.interviewer_tech
    else -> R.drawable.ken_stern
}

private fun colleagueSprite(name: String): Int = when (name) {
    "小芳" -> R.drawable.colleague_gossip
    else -> R.drawable.colleague_quiet
}

private fun speakerColor(name: String): Color = when (name) {
    "Ken" -> BrandDeepOrange
    "小芳" -> BrandOrange
    "阿凱" -> AccentBlue
    else -> InkCharcoal
}

@Composable
fun WorkplaceMeetingScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf(MeetPhase.TALKING) }
    var beatIdx by remember { mutableIntStateOf(0) }
    var speaker by remember { mutableStateOf(meetingOpening[0].first) }
    var fullText by remember { mutableStateOf(meetingOpening[0].second) }
    var typed by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("緊") }
    var lastColleague by remember { mutableStateOf("小芳") }
    val pendingLines = remember {
        mutableStateListOf<Pair<String, String>>().apply {
            addAll(meetingOpening.drop(1))
            add("Ken" to meetBeats[0].prompt)
        }
    }
    var awaitingChoice by remember { mutableStateOf(true) }
    var queuedMotion by remember { mutableStateOf(MeetMotion.NONE) }
    var queuedMood by remember { mutableStateOf<String?>(null) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }

    val scope = rememberCoroutineScope()
    val shakeX = remember { Animatable(0f) }
    val tiltZ = remember { Animatable(0f) }

    LaunchedEffect(fullText) {
        typed = ""
        for (i in fullText.indices) {
            if (typed.length >= fullText.length) break
            typed = fullText.substring(0, i + 1)
            delay(26)
        }
    }
    LaunchedEffect(speaker) {
        if (speaker == "小芳" || speaker == "阿凱") lastColleague = speaker
    }

    fun playMotion(m: MeetMotion) {
        scope.launch {
            when (m) {
                MeetMotion.SHAKE -> shakeX.animateTo(0f, keyframes {
                    durationMillis = 360
                    -5f at 60; 5f at 140; -3f at 230; 0f at 360
                })
                MeetMotion.TILT -> {
                    tiltZ.animateTo(-3f, tween(160))
                    tiltZ.animateTo(0f, tween(260))
                }
                MeetMotion.NONE -> Unit
            }
        }
    }

    fun advance() {
        if (typed.length < fullText.length) { typed = fullText; return }
        if (phase != MeetPhase.TALKING) return
        if (pendingLines.isNotEmpty()) {
            val (s, t) = pendingLines.removeAt(0)
            speaker = s; fullText = t
            if (s == "Ken") {
                playMotion(queuedMotion); queuedMotion = MeetMotion.NONE
                queuedMood?.let { m -> mood = m }; queuedMood = null
            }
        } else if (awaitingChoice) {
            phase = MeetPhase.CHOOSING
        } else if (beatIdx < meetBeats.lastIndex) {
            beatIdx++
            awaitingChoice = true
            speaker = meetBeats[beatIdx].promptSpeaker
            fullText = meetBeats[beatIdx].prompt
        } else {
            phase = MeetPhase.ENDING
        }
    }

    fun choose(c: MeetChoice) {
        if (c.repDelta != 0) {
            repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 3)
        }
        awaitingChoice = false
        queuedMood = c.moodAfter
        queuedMotion = c.motion
        pendingLines.clear()
        pendingLines.add("你" to c.playerLine)
        c.extraReact?.let { pendingLines.add(it) }
        pendingLines.add("Ken" to c.kenReact)
        phase = MeetPhase.TALKING
        val (s, t) = pendingLines.removeAt(0)
        speaker = s; fullText = t
    }

    Box(Modifier.fillMaxSize().background(InkCharcoal)) {
        Column(Modifier.fillMaxSize()) {
            // ===== 舞台:大會議室 + 雙立繪 =====
            Box(Modifier.fillMaxWidth().weight(1f)) {
                Image(
                    painter = painterResource(R.drawable.bg_conference_room),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(Modifier.fillMaxSize().background(InkCharcoal.copy(alpha = 0.35f)))

                // 頂欄
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite)
                    }
                    Column {
                        Text("跨部門會議",
                            color = PaperWhite, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Text("週三 14:00 ・ 大會議室",
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

                // 左:同事立繪(說話者亮、聽者暗)
                val colleagueActive = speaker == lastColleague
                val ca by animateFloatAsState(if (colleagueActive) 1f else 0.5f, label = "ca")
                val cs by animateFloatAsState(if (colleagueActive) 1f else 0.9f, label = "cs")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 6.dp, bottom = 6.dp),
                ) {
                    Image(
                        painter = painterResource(colleagueSprite(lastColleague)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp).alpha(ca).scale(cs),
                    )
                    Box(
                        Modifier.width(96.dp).height(12.dp).clip(CircleShape)
                            .background(InkBlack.copy(alpha = 0.5f)),
                    )
                }

                // 右:Ken 立繪(微動掛在他身上)
                val kenActive = speaker == "Ken"
                val ka by animateFloatAsState(if (kenActive) 1f else 0.5f, label = "ka")
                val ks by animateFloatAsState(if (kenActive) 1f else 0.9f, label = "ks")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 6.dp, bottom = 6.dp)
                        .graphicsLayer {
                            translationX = shakeX.value
                            rotationZ = tiltZ.value
                        },
                ) {
                    Image(
                        painter = painterResource(meetKenSprite(mood)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(160.dp).alpha(ka).scale(ks),
                    )
                    Box(
                        Modifier.width(104.dp).height(12.dp).clip(CircleShape)
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
                        color = speakerColor(speaker),
                        fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(typed, color = InkBlack, fontSize = 15.sp, lineHeight = 24.sp)
                    if (typed.length >= fullText.length && phase == MeetPhase.TALKING) {
                        Spacer(Modifier.height(4.dp))
                        val blink = rememberInfiniteTransition(label = "meetAdv")
                        val a by blink.animateFloat(
                            initialValue = 0.25f, targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
                            label = "meetBlink",
                        )
                        Text("▼", color = BrandOrange, fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End).alpha(a))
                    }
                }

                AnimatedVisibility(
                    visible = phase == MeetPhase.CHOOSING,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(260)) { it / 3 },
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Text("你會怎麼接",
                            color = InkGray500,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        meetBeats[beatIdx].choices.forEach { c ->
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
            visible = phase == MeetPhase.ENDING,
            enter = fadeIn(tween(700)),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(InkCharcoal.copy(alpha = 0.97f))
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("會議結束",
                    color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(Modifier.height(10.dp))
                Text("三個部門的帳,今天都記在你身上了。",
                    color = PaperWhite.copy(alpha = 0.75f), fontSize = 13.sp)
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

        // 聲望變動彈窗
        repPop?.let { rc ->
            Box(
                Modifier.fillMaxSize().padding(top = 80.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Row(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(if (rc.delta > 0) AccentGreen else AccentRed)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        (if (rc.delta > 0) "\u25B2 " else "\u25BC ") + rc.meter + " " +
                            (if (rc.delta > 0) "+" else "") + rc.delta,
                        color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 13.sp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(rc.reason, color = PaperWhite.copy(alpha = 0.9f), fontSize = 11.sp)
                }
            }
        }

    }
}
