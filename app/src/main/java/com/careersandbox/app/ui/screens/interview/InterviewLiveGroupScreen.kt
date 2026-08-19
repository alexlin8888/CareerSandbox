package com.careersandbox.app.ui.screens.interview

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.components.rememberInPageVoice
import com.careersandbox.app.ui.theme.*
import androidx.compose.material.icons.outlined.Stop

// 各角色河狸頭像(「你」維持色圈)
private val ParticipantAvatars = mapOf(
    "主考官" to R.drawable.interviewer_hr,
    "HR 主管" to R.drawable.interviewer_hr,
    "技術主管" to R.drawable.interviewer_tech,
    "用人主管" to R.drawable.interviewer_lead,
    "AI-強勢" to R.drawable.peer_assertive,
    "AI-邏輯" to R.drawable.peer_logical,
    "AI-親切" to R.drawable.peer_friendly,
    "AI-沉默" to R.drawable.peer_quiet,
)

// 各角色色票
private val ParticipantColors = mapOf(
    "主考官" to BrandOrange,
    "HR 主管" to BrandOrange,
    "技術主管" to InkCharcoal,
    "用人主管" to BrandDeepOrange,
    "你" to BrandDeepOrange,
    "AI-強勢" to Color(0xFF7A8C5A),
    "AI-邏輯" to InkGray700,
    "AI-親切" to Color(0xFFD4A574),
    "AI-沉默" to InkGray400,
)

private val baseRoster = listOf("主考官", "你", "AI-強勢", "AI-邏輯", "AI-親切", "AI-沉默")
private val panelRoster = listOf("HR 主管", "技術主管", "用人主管", "你", "AI-強勢", "AI-邏輯", "AI-親切", "AI-沉默")
private val panelNames = listOf("用人主管", "技術主管", "HR 主管")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveGroupScreen(navController: NavHostController) {
    val panel = InterviewConfig.groupInterviewers == 3
    var panelIdx by remember { mutableIntStateOf(0) }
    fun nextInterviewer(): String {
        val n = panelNames[panelIdx % panelNames.size]
        panelIdx++
        return n
    }
    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            addAll(
                MockData.groupInterviewScript.map {
                    if (panel && it.speaker == "主考官") it.copy(speaker = "用人主管") else it
                }
            )
        }
    }
    var input by remember { mutableStateOf("") }
    var showObservation by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var typingSpeaker by remember { mutableStateOf(if (panel) "用人主管" else "主考官") }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    var interruptCount by remember { mutableIntStateOf(0) }
    val currentSpeaker = if (isTyping) typingSpeaker
        else (messages.lastOrNull()?.speaker ?: if (panel) "用人主管" else "主考官")

    fun submitGroup(visible: String, analyzed: String) {
        if (isTyping) return
        com.careersandbox.app.data.mock.InterviewSession.recordGroupSay(visible)
        messages.add(ChatMessage("u${messages.size}", "你", visible, isUser = true))
        val (rawWho, line) = com.careersandbox.app.data.mock.MockGroupDispatcher.dispatch(analyzed, followUpIdx)
        val who = if (panel && rawWho == "主考官") nextInterviewer() else rawWho
        typingSpeaker = who
        isTyping = true
        scope.launch {
            delay(1400)
            messages.add(ChatMessage("g${messages.size}", who, line, isUser = false))
            followUpIdx++
            isTyping = false
        }
    }

    LaunchedEffect(Unit) {
        com.careersandbox.app.data.mock.InterviewSession.reset()
        com.careersandbox.app.data.mock.InterviewConfig.lastWasGroup = true
    }

    // 頁內語音(SpeechRecognizer,需 RECORD_AUDIO,不跳 Google 框):逐字稿餵 dispatch 做同儕路由
    val voice = rememberInPageVoice(languageTag = "zh-TW") { transcript -> submitGroup(transcript, transcript) }

    LaunchedEffect(messages.size, isTyping) {
        val target = if (isTyping) messages.size else messages.size - 1
        if (target >= 0) listState.animateScrollToItem(target)
    }

    // 你打字停頓超過 2.6 秒,AI-強勢會接著發言
    LaunchedEffect(input) {
        if (input.length >= 14 && !isTyping && interruptCount < com.careersandbox.app.data.mock.MockGroupDispatcher.interruptCap()) {
            delay(2600)
            if (!isTyping && input.length >= 14) {
                val line = com.careersandbox.app.data.mock.MockGroupDispatcher.interruptLine(interruptCount)
                interruptCount++
                typingSpeaker = "AI-強勢"
                isTyping = true
                delay(700)
                messages.add(ChatMessage("int${messages.size}", "AI-強勢", line, isUser = false))
                isTyping = false
            }
        }
    }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("團體面試 ・ Junior PM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text(
                            if (InterviewConfig.groupInterviewers == 3) "三位面試官 panel ・ AI 應徵者同場"
                            else "小組討論 ・ AI 應徵者同場",
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
            Column {
                if (voice.isListening) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandDeepOrange)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            "聆聽中（點麥克風結束）",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            voice.partialText.ifBlank { "…" },
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
                GroupBottomBar(
                    input, { input = it },
                    onVoice = { if (voice.isListening) voice.stop() else voice.start() },
                    isListening = voice.isListening,
                ) {
                    if (input.isNotBlank() && !isTyping) {
                        val said = input
                        input = ""
                        submitGroup(said, said)
                    }
                }
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            ParticipantsRow(currentSpeaker, panel)

            // 觀察面板
            AnimatedVisibility(visible = showObservation) {
                val userMsgs = messages.filter { it.isUser }
                val avgLen = if (userMsgs.isEmpty()) 0 else userMsgs.sumOf { it.content.length } / userMsgs.size
                ObservationPanel(
                    speakCount = userMsgs.size,
                    avgLen = avgLen,
                    onClose = { showObservation = false },
                )
            }
            if (!showObservation) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .pressScale { showObservation = true },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null,
                        tint = BrandOrange, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("顯示即時觀察",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandOrange, fontWeight = FontWeight.SemiBold)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages, key = { it.id }) { m ->
                    Box(Modifier.animateItem()) { GroupMessageBubble(m) }
                }
                if (isTyping) {
                    item(key = "typing") { GroupTypingBubble(typingSpeaker) }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ParticipantsRow(currentSpeaker: String, panel: Boolean) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(if (panel) panelRoster else baseRoster) { name ->
            val color = ParticipantColors[name] ?: BrandOrange
            val isCurrent = name == currentSpeaker
            val avatar = ParticipantAvatars[name]
            val tileAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.5f, label = "pa-$name")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (avatar != null) {
                    Image(
                        painter = painterResource(avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(48.dp).alpha(tileAlpha),
                    )
                } else {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape)
                            .background(color).alpha(tileAlpha),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(name.takeLast(1), color = PaperWhite,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) InkBlack else InkGray500,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium)
                if (isCurrent) {
                    Text("發言中", style = MaterialTheme.typography.labelSmall,
                        color = BrandOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ObservationPanel(
    speakCount: Int,
    avgLen: Int,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BrandPeach.copy(alpha = 0.55f))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Insights, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("即時觀察",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandDeepOrange, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = null,
                    tint = BrandDeepOrange,
                    modifier = Modifier.size(20.dp).pressScale(onClick = onClose))
            }
            Spacer(Modifier.height(8.dp))
            ObservationRow("你的發言次數", "$speakCount 次")
            ObservationRow("平均發言長度", "$avgLen 字")
        }
    }
}

@Composable
private fun ObservationRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = BrandDeepOrange.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleSmall,
            color = BrandDeepOrange, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GroupMessageBubble(m: ChatMessage) {
    val speakerColor = ParticipantColors[m.speaker] ?: InkGray500
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!m.isUser) {
            val avatar = ParticipantAvatars[m.speaker]
            if (avatar != null) {
                Image(
                    painter = painterResource(avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(34.dp),
                )
            } else {
                Box(
                    Modifier.size(32.dp).clip(CircleShape).background(speakerColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(m.speaker.takeLast(1), color = PaperWhite,
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        val isInterviewer = m.isInterviewer
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (m.isUser) 18.dp else 4.dp,
                    bottomEnd = if (m.isUser) 4.dp else 18.dp,
                ))
                .background(
                    when {
                        m.isUser -> InkBlack
                        isInterviewer -> BrandPeach
                        else -> MaterialTheme.colorScheme.surface
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                if (!m.isUser) {
                    Text(m.speaker, style = MaterialTheme.typography.labelSmall,
                        color = if (isInterviewer) BrandDeepOrange else speakerColor,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                }
                Text(m.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (m.isUser) PaperWhite
                        else if (isInterviewer) BrandDeepOrange else InkBlack)
            }
        }
    }
}

@Composable
private fun GroupBottomBar(
    input: String,
    onChange: (String) -> Unit,
    onVoice: () -> Unit,
    isListening: Boolean,
    onSend: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isListening) BrandDeepOrange else BrandPeach)
                    .pressScale { onVoice() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isListening) Icons.Outlined.Stop else Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = if (isListening) PaperWhite else BrandDeepOrange,
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = input, onValueChange = onChange,
                placeholder = { Text("輸入你的觀點⋯", color = InkGray400) },
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
private fun ActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = InkBlack,
                modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun GroupTypingBubble(speaker: String) {
    val color = ParticipantColors[speaker] ?: InkGray500
    val t = rememberInfiniteTransition(label = "gtyping")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        val avatar = ParticipantAvatars[speaker]
        if (avatar != null) {
            Image(
                painter = painterResource(avatar),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp),
            )
        } else {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(color),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    speaker.takeLast(1), color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
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
