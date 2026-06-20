package com.careersandbox.app.ui.screens.interview

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
import androidx.compose.ui.draw.alpha
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.model.ChatMessage
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PanelInterviewer(val name: String, val drawable: Int)

private val panelInterviewers = listOf(
    PanelInterviewer("HR 主管", R.drawable.interviewer_hr),
    PanelInterviewer("技術主管", R.drawable.interviewer_tech),
    PanelInterviewer("用人主管", R.drawable.interviewer_lead),
)

private val panelScript = listOf(
    ChatMessage("p1", "HR 主管", "我們先從你開始。可以用一分鐘介紹一下自己,以及為什麼想加入我們嗎?", isInterviewer = true),
    ChatMessage("p2", "你", "你好,我是中山資管系大三的 Alex,做過社團行銷和資料分析實習,想往產品經理發展。", isUser = true),
    ChatMessage("p3", "技術主管", "你提到資料分析。實務上你最常用哪些工具?舉一個你用數據改變決策的例子。", isInterviewer = true),
    ChatMessage("p4", "你", "我主要用 SQL 和 Excel。實習時把每週手動報表自動化,產出時間從兩小時縮到半小時。", isUser = true),
    ChatMessage("p5", "用人主管", "不錯。如果讓你接一個你不熟的產品領域,你會怎麼在兩週內快速上手?", isInterviewer = true),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLivePanelScreen(navController: NavHostController) {
    val avatarMap = remember { panelInterviewers.associate { it.name to it.drawable } }
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(panelScript) } }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var typingSpeaker by remember { mutableStateOf("HR 主管") }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var entered by remember { mutableStateOf(false) }
    var reaction by remember { mutableStateOf<String?>(null) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    var voiceMode by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var recordSec by remember { mutableIntStateOf(0) }
    var holdStartAt by remember { mutableLongStateOf(0L) }
    var questionShownAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(recording) {
        if (recording) { var sct = 0; recordSec = 0; while (true) { delay(1000); sct++; recordSec = sct } }
    }

    fun submitPanel(visible: String, analyzed: String) {
        if (isTyping || reaction != null) return
        messages.add(ChatMessage("u${messages.size}", "你", visible, isUser = true))
        val (who, line) = com.careersandbox.app.data.mock.MockPanelDispatcher.dispatch(analyzed, followUpIdx)
        typingSpeaker = who
        reaction = com.careersandbox.app.data.mock.MockPanelDispatcher.reaction()
        scope.launch {
            delay(800)
            reaction = null
            isTyping = true
            delay(1300)
            messages.add(ChatMessage("p${messages.size}", who, line, isInterviewer = true))
            questionShownAt = System.currentTimeMillis()
            followUpIdx++
            isTyping = false
        }
    }
    val currentAsker = if (isTyping || reaction != null) typingSpeaker
        else (messages.lastOrNull { it.isInterviewer }?.speaker ?: "HR 主管")

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
                        Text("主管 panel 面試 ・ Junior PM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text("三位主管輪流提問 ・ 中等難度",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkGray500)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
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
            if (voiceMode) {
                VoiceBar(
                    recording = recording,
                    recordSec = recordSec,
                    onKeyboard = { if (!recording) voiceMode = false },
                    onPressStart = {
                        if (!isTyping && reaction == null) { holdStartAt = System.currentTimeMillis(); recording = true }
                    },
                    onPressEnd = {
                        if (recording) {
                            recording = false
                            val dur = ((System.currentTimeMillis() - holdStartAt) / 1000).toInt()
                            if (dur >= 1) {
                                val think = ((holdStartAt - questionShownAt) / 1000).toInt().coerceAtLeast(0)
                                submitPanel("語音回答 ・ $dur 秒\n(開口前思考 $think 秒)", "")
                            }
                        }
                    },
                )
            } else {
                PanelBottomBar(input, { input = it }, onVoice = { voiceMode = true }) {
                    if (input.isNotBlank() && !isTyping && reaction == null) {
                        val said = input
                        input = ""
                        submitPanel(said, said)
                    }
                }
            }
        }
    ) { pad ->
        if (!entered) {
            PanelIntroOverlay(Modifier.padding(pad), panelInterviewers) { entered = true }
        } else {
        Column(Modifier.padding(pad)) {
            PanelRow(panelInterviewers, currentAsker)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = InkGray400, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("三位主管會輪流發問,回答時記得保持結構。",
                    style = MaterialTheme.typography.labelSmall, color = InkGray500)
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages, key = { it.id }) { m ->
                    Box(Modifier.animateItem()) { PanelMessageBubble(m) { avatarMap[it] } }
                }
                reaction?.let { r ->
                    item(key = "reaction") { PanelReactionBubble(r, avatarMap[typingSpeaker]) }
                }
                if (isTyping) {
                    item(key = "typing") { PanelTypingBubble(typingSpeaker, avatarMap[typingSpeaker]) }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
        }
    }
}

@Composable
private fun PanelRow(interviewers: List<PanelInterviewer>, currentAsker: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        interviewers.forEach { iv ->
            val isCurrent = iv.name == currentAsker
            val avatarSize by animateDpAsState(
                targetValue = if (isCurrent) 66.dp else 52.dp, label = "sz")
            val avatarAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.45f, label = "al")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isCurrent) BrandPeach.copy(alpha = 0.55f) else Color(0x0A000000))
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(iv.drawable),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(avatarSize).alpha(avatarAlpha),
                )
                Spacer(Modifier.height(4.dp))
                Text(iv.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) InkBlack else InkGray500,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1)
                if (isCurrent) {
                    Spacer(Modifier.height(2.dp))
                    Text("提問中",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PanelMessageBubble(m: ChatMessage, avatarOf: (String) -> Int?) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!m.isUser) {
            val dr = avatarOf(m.speaker)
            if (dr != null) {
                Image(
                    painter = painterResource(dr),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp),
                )
            } else {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(BrandOrange),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(m.speaker.takeLast(1), color = PaperWhite,
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (m.isUser) 18.dp else 4.dp,
                    bottomEnd = if (m.isUser) 4.dp else 18.dp,
                ))
                .background(if (m.isUser) InkBlack else BrandPeach)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                if (!m.isUser) {
                    Text(m.speaker, style = MaterialTheme.typography.labelSmall,
                        color = BrandDeepOrange, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                }
                Text(m.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (m.isUser) PaperWhite else BrandDeepOrange)
            }
        }
    }
}

@Composable
private fun PanelBottomBar(input: String, onChange: (String) -> Unit, onVoice: () -> Unit, onSend: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
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
                    .clip(CircleShape)
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
private fun PanelTypingBubble(speaker: String, drawable: Int?) {
    val t = rememberInfiniteTransition(label = "ptyping")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        if (drawable != null) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(36.dp),
            )
        } else {
            Box(
                Modifier.size(36.dp).clip(CircleShape).background(BrandOrange),
                contentAlignment = Alignment.Center,
            ) {
                Text(speaker.takeLast(1), color = PaperWhite,
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(BrandPeach)
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
                        .background(BrandDeepOrange.copy(alpha = a)),
                )
            }
        }
    }
}

@Composable
private fun PanelIntroOverlay(
    modifier: Modifier = Modifier,
    interviewers: List<PanelInterviewer>,
    onDone: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(1900)
        onDone()
    }
    val t = rememberInfiniteTransition(label = "breathe")
    val scale by t.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "s",
    )
    Box(
        modifier = modifier.fillMaxSize().background(PaperOff),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                interviewers.forEach { iv ->
                    Image(
                        painter = painterResource(iv.drawable),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(82.dp).scale(scale),
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("三位主管已就座",
                color = InkBlack,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            Text("深呼吸,準備好了就開始。",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(28.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandPeach.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text("即將開始⋯",
                    color = BrandDeepOrange,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PanelReactionBubble(text: String, drawable: Int?) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        if (drawable != null) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(text, style = MaterialTheme.typography.bodySmall, color = BrandDeepOrange)
        }
    }
}
