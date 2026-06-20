package com.careersandbox.app.ui.screens.interview

import kotlin.math.sin
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.ChatMessage
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveIndividualScreen(navController: NavHostController) {
    val lang = InterviewConfig.language
    fun t(zh: String, en: String) = if (lang == "English") en else zh
    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            addAll(if (lang == "English") englishOpeningScript else MockData.individualInterviewScript)
        }
    }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var reaction by remember { mutableStateOf<String?>(null) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf("MAIN") }            // MAIN / REVERSE / CLOSING / DONE
    var voiceMode by remember { mutableStateOf(false) }
    var repeatFired by remember { mutableStateOf(false) }
    var silenceFired by remember { mutableStateOf(false) }
    var timeGlanced by remember { mutableStateOf(false) }
    var lastProbe by remember { mutableStateOf("") }
    var questionShownAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var recording by remember { mutableStateOf(false) }
    var recordSec by remember { mutableIntStateOf(0) }
    var holdStartAt by remember { mutableLongStateOf(0L) }
    val curInput by rememberUpdatedState(input)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000); elapsedSec++
            if (elapsedSec == 360 && !timeGlanced && phase == "MAIN") {
                timeGlanced = true
                messages.add(ChatMessage("ai${messages.size}", "面試官", t("(他看了一眼時間)", "(He glances at the clock.)"), isUser = false))
            }
        }
    }
    // 沉默壓力:面試官問完 20 秒沒動靜 — 每場只提醒一次
    LaunchedEffect(messages.size, phase) {
        if (phase != "MAIN" || silenceFired) return@LaunchedEffect
        if (messages.lastOrNull()?.isUser != false) return@LaunchedEffect
        delay(20000)
        if (curInput.isBlank() && !recording && !silenceFired) {
            silenceFired = true
            messages.add(ChatMessage("ai${messages.size}", "面試官", t("不急,想清楚再說。", "Take your time. Think it through."), isUser = false))
        }
    }
    LaunchedEffect(recording) {
        if (recording) { var sct = 0; recordSec = 0; while (true) { delay(1000); sct++; recordSec = sct } }
    }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    val probes = when {
        lang == "English" -> englishProbes
        InterviewConfig.type == "技術" -> probesTechType
        InterviewConfig.type == "情境" -> probesCaseType
        else -> listOf(
            "嗯,了解。可以再給一個更具體的例子嗎?",
            "那當時你怎麼衡量這個決定的影響?",
            "如果重來一次,你會有什麼不同的做法?",
            "這段經驗裡,你覺得自己最關鍵的貢獻是什麼?",
        )
    }

    fun submitAnswer(visible: String, analyzed: String) {
        if (isTyping || reaction != null || phase != "MAIN") return
        messages.add(ChatMessage("u${messages.size}", "你", visible, isUser = true))
        reaction = com.careersandbox.app.data.mock.MockInterviewProber.reaction()
        scope.launch {
            delay(800); reaction = null
            isTyping = true; delay(1300)
            val reply = when {
                followUpIdx == 2 && !repeatFired -> {
                    repeatFired = true
                    t("(他翻了下筆記)剛剛那題,我再問一次:$lastProbe", "(He flips back a page.) Let me ask that one again: $lastProbe")
                }
                followUpIdx >= 4 -> {
                    phase = "REVERSE"
                    t("好,主要的問題就到這裡。最後,你有什麼想問我們的?", "Alright, that covers the main questions. One last thing: what would you like to ask us?")
                }
                else -> com.careersandbox.app.data.mock.MockInterviewProber.probe(analyzed, followUpIdx, probes).also { lastProbe = it }
            }
            messages.add(ChatMessage("ai${messages.size}", "面試官", reply, isUser = false))
            questionShownAt = System.currentTimeMillis()
            followUpIdx++
            isTyping = false
        }
    }

    fun pickReverse(opt: ReverseOption) {
        if (phase != "REVERSE") return
        phase = "CLOSING"
        messages.add(ChatMessage("u${messages.size}", "你", opt.ask, isUser = true))
        scope.launch {
            delay(700); isTyping = true; delay(1300)
            messages.add(ChatMessage("ai${messages.size}", "面試官", opt.answer, isUser = false))
            isTyping = false; delay(900)
            isTyping = true; delay(1100)
            messages.add(ChatMessage("ai${messages.size}", "面試官", opt.closing, isUser = false))
            isTyping = false
            phase = "DONE"
        }
    }

    LaunchedEffect(messages.size, isTyping, reaction) {
        val target = if (isTyping || reaction != null) messages.size else messages.size - 1
        if (target >= 0) listState.animateScrollToItem(target)
    }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("個人面試 ・ Junior PM", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text(
                            "${InterviewConfig.type}面試 ・ ${InterviewConfig.round} ・ ${InterviewConfig.difficulty}",
                            style = MaterialTheme.typography.labelSmall, color = InkGray500,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    // 計時器膠囊
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(InkBlack)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Timer, contentDescription = null,
                                tint = PaperWhite, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(timerText, style = MaterialTheme.typography.labelMedium,
                                color = PaperWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { navController.navigate(Routes.INTERVIEW_REPORT) }) {
                        Text("結束", color = BrandOrange, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            when {
                phase == "DONE" -> DoneBar { navController.navigate(Routes.INTERVIEW_REPORT) }
                phase == "REVERSE" || phase == "CLOSING" ->
                    ReverseBar(
                        enabled = phase == "REVERSE",
                        options = if (lang == "English") reverseOptionsEn else reverseOptions,
                    ) { pickReverse(it) }
                voiceMode -> VoiceBar(
                    recording = recording,
                    recordSec = recordSec,
                    onKeyboard = { if (!recording) voiceMode = false },
                    onPressStart = {
                        if (!isTyping && reaction == null && phase == "MAIN") {
                            holdStartAt = System.currentTimeMillis(); recording = true
                        }
                    },
                    onPressEnd = {
                        if (recording) {
                            recording = false
                            val dur = ((System.currentTimeMillis() - holdStartAt) / 1000).toInt()
                            if (dur >= 1) {
                                val think = ((holdStartAt - questionShownAt) / 1000).toInt().coerceAtLeast(0)
                                submitAnswer("語音回答 ・ $dur 秒\n(開口前思考 $think 秒)", "")
                            }
                        }
                    },
                )
                else -> BottomInputBar(input, { input = it }, onVoice = { voiceMode = true }) {
                    if (input.isNotBlank() && !isTyping && reaction == null) {
                        val said = input
                        input = ""
                        submitAnswer(said, said)
                    }
                }
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            InterviewerHeader(onThinkTime = {
                if (!isTyping && reaction == null) {
                    isTyping = true
                    scope.launch {
                        delay(900)
                        messages.add(ChatMessage("ai${messages.size}", "面試官",
                            "沒問題,慢慢想。整理好再回答,我等你。", isUser = false))
                        isTyping = false
                    }
                }
            })
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages, key = { it.id }) { m ->
                    Box(Modifier.animateItem()) { MessageBubble(m) }
                }
                reaction?.let { r ->
                    item(key = "reaction") { ReactionBubble(r) }
                }
                if (isTyping) {
                    item(key = "typing") { TypingBubble() }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun InterviewerHeader(onThinkTime: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.interviewer_lead),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("陳經理 ・ 用人主管", style = MaterialTheme.typography.titleSmall,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(AccentGreen))
                Spacer(Modifier.width(4.dp))
                Text("正在聆聽你的回答", style = MaterialTheme.typography.labelSmall,
                    color = InkGray500)
            }
        }
        TextButton(onClick = onThinkTime) {
            Icon(Icons.Outlined.Pause, contentDescription = null,
                tint = InkGray500, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("需要思考時間", style = MaterialTheme.typography.labelMedium, color = InkGray500)
        }
    }
}

@Composable
fun MessageBubble(m: ChatMessage) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (m.isUser) 18.dp else 4.dp,
                    bottomEnd = if (m.isUser) 4.dp else 18.dp,
                ))
                .background(if (m.isUser) InkBlack else MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                if (!m.isUser) {
                    Text(m.speaker, style = MaterialTheme.typography.labelSmall,
                        color = BrandOrange, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                }
                Text(m.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (m.isUser) PaperWhite else InkBlack)
            }
        }
    }
}

@Composable
private fun BottomInputBar(input: String, onChange: (String) -> Unit, onVoice: () -> Unit, onSend: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(BrandPeach)
                    .pressScale { onVoice() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Mic, contentDescription = null, tint = BrandDeepOrange)
            }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = input, onValueChange = onChange,
                placeholder = { Text("輸入你的回答⋯", color = InkGray400) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (input.isBlank()) InkGray200 else InkBlack)
                    .pressScale(enabled = input.isNotBlank()) { onSend() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Send, contentDescription = null,
                    tint = if (input.isBlank()) InkGray400 else PaperWhite)
            }
        }
    }
}

@Composable
private fun TypingBubble() {
    val t = rememberInfiniteTransition(label = "typing")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Row(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { i ->
                val a by t.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 500, delayMillis = i * 160),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "dot",
                )
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(InkGray400.copy(alpha = a)),
                )
            }
        }
    }
}

@Composable
private fun ReactionBubble(text: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Image(
            painter = painterResource(R.drawable.interviewer_lead),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(34.dp),
        )
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(text, style = MaterialTheme.typography.bodySmall, color = InkGray500)
        }
    }
}

/* ===================== 反問環節 / 語音作答 / 收尾 ===================== */

private data class ReverseOption(val ask: String, val tag: String, val answer: String, val closing: String)

private val reverseOptions = listOf(
    ReverseOption(
        ask = "團隊接下來半年,最大的挑戰是什麼?", tag = "問挑戰",
        answer = "(他想了想)好問題。最大的挑戰是新產品線的節奏:資源沒變,目標翻倍。進來的人會直接碰到這一塊。",
        closing = "今天就到這裡。你最後這個問題,我喜歡。等通知。",
    ),
    ReverseOption(
        ask = "這個職位做得好的人,一年後通常長成什麼樣子?", tag = "問成長",
        answer = "一年後做得好的人,通常已經能自己扛一條小產品線,開始帶實習生。我們希望你長得比職缺快。",
        closing = "問得很實際。今天先到這裡,後續人資會跟你聯繫。",
    ),
    ReverseOption(
        ask = "想先確認一下,這個職位的薪資範圍和獎金結構?", tag = "直球",
        answer = "(他頓了一下)……這個階段我先不談數字,人資後續會說明。還有別的想問的嗎?",
        closing = "好,那今天先到這裡。",
    ),
    ReverseOption(
        ask = "目前沒有問題了,謝謝。", tag = "沒有問題",
        answer = "(他點點頭)行。",
        closing = "今天就到這裡,等通知。",
    ),
)

@Composable
private fun ReverseBar(enabled: Boolean, options: List<ReverseOption>, onPick: (ReverseOption) -> Unit) {
    Column(Modifier.fillMaxWidth().background(PaperOff).padding(horizontal = 12.dp, vertical = 10.dp)) {
        Text("你的反問", color = InkGray500, style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        options.forEach { opt ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pressScale(enabled = enabled) { onPick(opt) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(opt.ask, color = if (enabled) InkBlack else InkGray400,
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandPeach.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(opt.tag, color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun DoneBar(onReport: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Box(
            Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(14.dp))
                .background(InkBlack).pressScale(onClick = onReport),
            contentAlignment = Alignment.Center,
        ) { Text("面試結束 ・ 看完整報告", color = PaperWhite, fontWeight = FontWeight.Black) }
    }
}



/* ===================== 語言與類型分流(之後由 LangGraph 題庫取代)===================== */

private val englishOpeningScript = listOf(
    ChatMessage("m1", "面試官",
        "Let's start. Give me a one-minute introduction — who you are, and why this role.",
        isInterviewer = true),
)

private val englishProbes = listOf(
    "I see. Can you give me a more concrete example?",
    "How did you measure the impact of that decision?",
    "If you could do it again, what would you do differently?",
    "What was your single most critical contribution there?",
)

private val probesTechType = listOf(
    "講一個你寫過最複雜的查詢或程式邏輯,它解決什麼問題?",
    "如果報表突然變慢十倍,你會從哪裡開始查?",
    "你怎麼驗證自己的分析結果沒有錯?",
    "最近自學了什麼工具或技術?怎麼學的?",
)

private val probesCaseType = listOf(
    "上線前一天發現重大 bug,修好要兩天。你怎麼辦?",
    "兩位主管同時給你衝突的指令,你怎麼處理?",
    "資源被砍一半,目標不變,你先丟掉哪一塊?",
    "使用者在社群罵爆你負責的功能,你的第一步是什麼?",
)

private val reverseOptionsEn = listOf(
    ReverseOption(
        ask = "What's the biggest challenge for the team in the next six months?", tag = "問挑戰",
        answer = "(He thinks for a moment.) Good question. Pacing the new product line — same resources, double the targets. You'd be right in the middle of it.",
        closing = "That's it for today. I liked that last question. We'll be in touch.",
    ),
    ReverseOption(
        ask = "What does someone great in this role look like after one year?", tag = "問成長",
        answer = "After a year, the good ones own a small product line and start mentoring interns. We want you to outgrow the job description.",
        closing = "Very practical question. That's all for today — HR will follow up.",
    ),
    ReverseOption(
        ask = "Could we confirm the salary range and bonus structure?", tag = "直球",
        answer = "(He pauses.) ...Let's not get into numbers at this stage. HR will walk you through it. Anything else?",
        closing = "Alright, that's all for today.",
    ),
    ReverseOption(
        ask = "No questions for now. Thank you.", tag = "沒有問題",
        answer = "(He nods.) Alright.",
        closing = "That's it for today. We'll be in touch.",
    ),
)
