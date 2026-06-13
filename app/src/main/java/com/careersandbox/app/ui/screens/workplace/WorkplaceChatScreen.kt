package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.ui.graphics.Brush
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

/* ===================== 資料模型與劇本 ===================== */

private enum class SceneMotion { NONE, SHAKE, TILT }
private enum class ScenePhase { TALKING, CHOOSING, ENDING }

private data class StanceChoice(
    val label: String,        // 選項全文
    val stance: String,       // 立場小標
    val playerLine: String,   // 你說出口的話
    val kenReact: String,     // Ken 的反應
    val moodAfter: String,    // 心情膠囊變化
    val motion: SceneMotion,  // 立繪微動
    val repMeter: String = "主管信任",
    val repDelta: Int = 0,
    val repReason: String = "",
)

private data class Beat(
    val kenPrompt: String,
    val choices: List<StanceChoice>,
)

// 場景素材(Lovart):會議室背景 + Ken 依心情換臉
// 緩和表情(ken_soft)待重抽(現版多了工程帽,與 AI 應徵者識別衝突)— 暫以中性版頂替
private val sceneBackdrop: Int? = R.drawable.bg_meeting_room
private fun kenSprite(mood: String): Int = when (mood) {
    "平靜", "緩和" -> R.drawable.interviewer_tech
    else -> R.drawable.ken_stern
}

private val beats = listOf(
    Beat(
        "坐。先說結論:上週的匯出功能延了兩天。我想知道,是估錯,還是中間出了事。",
        listOf(
            StanceChoice(
                "直說原因,不繞", "誠實扛",
                "是我低估了第三方 API 的坑。debug 花掉的時間,我沒有及早講。",
                "(他點了點頭)好,知道問題在哪就好。",
                "平靜", SceneMotion.TILT,
                repMeter = "主管信任", repDelta = 2, repReason = "你沒繞,直接扛起延期",
            ),
            StanceChoice(
                "先道歉,再解釋", "緩衝",
                "抱歉,讓你最後才知道。原因是 API 文件跟實際行為不一致。",
                "道歉收下。但我更想要的是:你卡住的當下,我就知道。",
                "平靜", SceneMotion.NONE,
                repMeter = "主管信任", repDelta = 1, repReason = "你道了歉,但他更在意即時同步",
            ),
            StanceChoice(
                "反問優先順序", "轉守為攻",
                "在講延期之前,我想先確認:匯出跟新需求,哪個優先?",
                "(他停了兩秒)問得好。但別用問題接問題,先回答我的。",
                "更不耐", SceneMotion.SHAKE,
                repMeter = "主管信任", repDelta = -1, repReason = "用問題擋問題,他不買單",
            ),
        ),
    ),
    Beat(
        "嗯。那你是哪一天發現的?發現的當下,為什麼我是最後一個知道的?",
        listOf(
            StanceChoice(
                "承認該早點說", "認溝通失誤",
                "週三就發現了。當下想先自己解,是我判斷錯,應該先說。",
                "(他在筆記上寫了一行)對。卡住不丟臉,悶著才會出事。",
                "平靜", SceneMotion.TILT,
                repMeter = "主管信任", repDelta = 2, repReason = "你承認該早點說,他記了一筆好的",
            ),
            StanceChoice(
                "說明當時的判斷", "說理",
                "我評估那時還追得回來,不想太早拉警報。",
                "我懂。但要不要拉警報,讓我跟你一起判斷,不是你一個人扛。",
                "平靜", SceneMotion.NONE,
                repMeter = "專業形象", repDelta = 1, repReason = "你說明了判斷,但他要的是一起判斷",
            ),
            StanceChoice(
                "說自己太忙忘了", "迴避",
                "就……事情比較多,一忙就忘了同步。",
                "(他盯著你兩秒)忙不是理由,是現象。再來一次。",
                "更不耐", SceneMotion.SHAKE,
                repMeter = "主管信任", repDelta = -2, repReason = "「太忙忘了」被他當成藉口",
            ),
        ),
    ),
    Beat(
        "好。週四 demo 之前,你的 plan 是什麼?具體一點,我要日期跟人。",
        listOf(
            StanceChoice(
                "給一個敢簽名的日期", "給死線",
                "週三中午前修完核心路徑,當天下午我自己先跑一輪回歸。",
                "可以。週三中午,我會記得。",
                "平靜", SceneMotion.TILT,
                repMeter = "專業形象", repDelta = 2, repReason = "你給了一個敢簽名的日期",
            ),
            StanceChoice(
                "開口要支援", "要資源",
                "如果阿哲能借我半天,週二就能收掉,風險低很多。",
                "(他想了一下)我去跟他主管說。這種話早講,半天就能省兩天。",
                "緩和", SceneMotion.TILT,
                repMeter = "主管信任", repDelta = 1, repReason = "你早點開口要支援,省了兩天",
            ),
            StanceChoice(
                "說盡量趕", "保守承諾",
                "我盡量趕,應該……來得及。",
                "「應該」進不了我的報告。給我一個你敢簽名的日期。",
                "不耐", SceneMotion.SHAKE,
                repMeter = "專業形象", repDelta = -2, repReason = "「應該」進不了他的報告",
            ),
        ),
    ),
    Beat(
        "這件事到這裡。最後,有什麼是需要我幫你擋的?",
        listOf(
            StanceChoice(
                "提一個真需求", "開口",
                "新需求的評估能不能延到 demo 後?我想先把眼前的收乾淨。",
                "成交,我去擋。專注是用換的,不是用撐的。",
                "緩和", SceneMotion.TILT,
                repMeter = "主管信任", repDelta = 2, repReason = "你提了真需求,他答應幫你擋",
            ),
            StanceChoice(
                "說目前沒有", "硬扛",
                "目前沒有,我自己可以。",
                "(他看了你一眼)行。但這扇門一直開著,別等淹到脖子才敲。",
                "平靜", SceneMotion.NONE,
                repMeter = "主管信任", repDelta = 1, repReason = "你說自己可以,他把門留著",
            ),
            StanceChoice(
                "反過來關心他", "反客為主",
                "倒是你,這週往上報的壓力,還好嗎?",
                "(他愣了一下,笑出來)輪不到你操心。滾回去工作。",
                "緩和", SceneMotion.TILT,
                repMeter = "同事情誼", repDelta = 1, repReason = "你關心了他,氣氛軟下來",
            ),
        ),
    ),
)

/* ===================== 主畫面 ===================== */

@Composable
fun WorkplaceChatScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf(ScenePhase.TALKING) }
    var beatIdx by remember { mutableIntStateOf(0) }
    var speaker by remember { mutableStateOf("Ken") }
    var fullText by remember { mutableStateOf(beats[0].kenPrompt) }
    var typed by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("不耐") }
    val pendingLines = remember { mutableStateListOf<Pair<String, String>>() }
    var awaitingChoice by remember { mutableStateOf(true) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }
    var queuedMotion by remember { mutableStateOf(SceneMotion.NONE) }
    var queuedMood by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val shakeX = remember { Animatable(0f) }
    val tiltZ = remember { Animatable(0f) }

    // 打字機
    LaunchedEffect(fullText) {
        typed = ""
        for (i in fullText.indices) {
            if (typed.length >= fullText.length) break
            typed = fullText.substring(0, i + 1)
            delay(26)
        }
    }

    fun playMotion(m: SceneMotion) {
        scope.launch {
            when (m) {
                SceneMotion.SHAKE -> shakeX.animateTo(0f, keyframes {
                    durationMillis = 360
                    -5f at 60; 5f at 140; -3f at 230; 0f at 360
                })
                SceneMotion.TILT -> {
                    tiltZ.animateTo(-3f, tween(160))
                    tiltZ.animateTo(0f, tween(260))
                }
                SceneMotion.NONE -> Unit
            }
        }
    }

    fun advance() {
        if (typed.length < fullText.length) { typed = fullText; return }
        if (phase != ScenePhase.TALKING) return
        if (pendingLines.isNotEmpty()) {
            val (s, t) = pendingLines.removeAt(0)
            speaker = s; fullText = t
            if (s == "Ken") {
                playMotion(queuedMotion); queuedMotion = SceneMotion.NONE
                queuedMood?.let { m -> mood = m }; queuedMood = null
            }
        } else if (awaitingChoice) {
            phase = ScenePhase.CHOOSING
        } else if (beatIdx < beats.lastIndex) {
            beatIdx++
            awaitingChoice = true
            speaker = "Ken"; fullText = beats[beatIdx].kenPrompt
        } else {
            phase = ScenePhase.ENDING
        }
    }

    fun choose(c: StanceChoice) {
        awaitingChoice = false
        queuedMood = c.moodAfter
        queuedMotion = c.motion
        if (c.repDelta != 0) {
            repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 1)
        }
        pendingLines.clear()
        pendingLines.add("你" to c.playerLine)
        pendingLines.add("Ken" to c.kenReact)
        phase = ScenePhase.TALKING
        val (s, t) = pendingLines.removeAt(0)
        speaker = s; fullText = t
    }

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    Box(Modifier.fillMaxSize().background(InkCharcoal)) {
        Column(Modifier.fillMaxSize()) {
            // ===== 舞台 =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                if (sceneBackdrop != null) {
                    Image(
                        painter = painterResource(sceneBackdrop),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(Modifier.fillMaxSize().background(InkCharcoal.copy(alpha = 0.35f)))
                } else {
                    // Compose 畫的暫代舞台:深色漸層 + 百葉窗光帶
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(listOf(InkBlack, InkCharcoal))
                        )
                    )
                    Column(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 48.dp, end = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        repeat(4) {
                            Box(
                                Modifier
                                    .width(120.dp)
                                    .height(10.dp)
                                    .graphicsLayer { rotationZ = -12f }
                                    .clip(RoundedCornerShape(50))
                                    .background(BrandAmber.copy(alpha = 0.10f)),
                            )
                        }
                    }
                }

                // 頂欄(疊在舞台上)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite)
                    }
                    Column {
                        Text("和主管 1on1",
                            color = PaperWhite, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Text("週一 09:30 ・ 會議室 B",
                            color = PaperWhite.copy(alpha = 0.55f),
                            style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(PaperWhite.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("心情 ・ $mood",
                            color = PaperWhite.copy(alpha = 0.85f),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Ken 立繪 + 地板陰影
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .graphicsLayer {
                            translationX = shakeX.value
                            rotationZ = tiltZ.value
                        },
                ) {
                    Image(
                        painter = painterResource(kenSprite(mood)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(176.dp),
                    )
                    Box(
                        Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(CircleShape)
                            .background(InkBlack.copy(alpha = 0.5f)),
                    )
                }
            }

            // ===== 對話面板(ADV)=====
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(PaperOff)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                // 對話框:名牌 + 打字機 + 點擊推進
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PaperWhite)
                        .clickable { advance() }
                        .padding(16.dp)
                        .heightIn(min = 96.dp),
                ) {
                    Text(
                        speaker,
                        color = if (speaker == "Ken") BrandDeepOrange else InkCharcoal,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        typed,
                        color = InkBlack,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                    )
                    if (typed.length >= fullText.length && phase == ScenePhase.TALKING) {
                        Spacer(Modifier.height(4.dp))
                        val blink = rememberInfiniteTransition(label = "adv")
                        val a by blink.animateFloat(
                            initialValue = 0.25f, targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
                            label = "blink",
                        )
                        Text("▼",
                            color = BrandOrange,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End).alpha(a))
                    }
                }

                // 選項卡
                AnimatedVisibility(
                    visible = phase == ScenePhase.CHOOSING,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(260)) { it / 3 },
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Text("你會怎麼接",
                            color = InkGray500,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        beats[beatIdx].choices.forEach { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PaperWhite)
                                    .pressScale { choose(c) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    c.label,
                                    color = InkBlack,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(BrandPeach.copy(alpha = 0.55f))
                                        .padding(horizontal = 9.dp, vertical = 3.dp),
                                ) {
                                    Text(c.stance,
                                        color = BrandDeepOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        // ===== 收尾:燈光收暗 =====
        AnimatedVisibility(
            visible = phase == ScenePhase.ENDING,
            enter = fadeIn(tween(700)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(InkCharcoal.copy(alpha = 0.97f))
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("1on1 結束",
                    color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(Modifier.height(10.dp))
                Text("你今天的選擇,Ken 都記著。",
                    color = PaperWhite.copy(alpha = 0.75f), fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("痕跡,週五揭曉。",
                    color = BrandOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandOrange)
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("回到路徑",
                        color = PaperWhite, fontWeight = FontWeight.Black)
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
                        (if (rc.delta > 0) "▲ " else "▼ ") + rc.meter + " " +
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
