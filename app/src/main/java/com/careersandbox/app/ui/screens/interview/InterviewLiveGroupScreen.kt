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
import com.careersandbox.app.ui.theme.*
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.CircularProgressIndicator
import com.careersandbox.app.data.repository.RemoteTranscribeRepository
import com.careersandbox.app.ui.components.RecordingMicButton
import com.careersandbox.app.ui.components.rememberInPageAudioRecorder
import androidx.compose.foundation.clickable
import com.careersandbox.app.data.repository.TranscriptionResult
import androidx.compose.ui.text.style.TextOverflow
import com.careersandbox.app.data.mock.InterviewAiSession
import com.careersandbox.app.data.remote.PersonaDto
import com.careersandbox.app.data.remote.InterviewAiApiClient
import com.careersandbox.app.data.remote.InterviewContextDto
import com.careersandbox.app.data.remote.TurnRequest
import com.careersandbox.app.data.remote.toDto

private const val USER_KEY = "你"

// Group discussions are time-boxed. Set to 60 temporarily when testing the wrap-up.
private const val GROUP_MAX_SECONDS = 15 * 60

// Avatars keyed by persona id (PersonaDto.id from POST /interviews).
// Unknown ids, such as generated competitor candidates, fall back to a colored circle.
private val ParticipantAvatars = mapOf(
    "moderator" to R.drawable.interviewer_hr,
    "hr" to R.drawable.interviewer_hr,
    "tech" to R.drawable.interviewer_tech,
    "hiring" to R.drawable.interviewer_lead,
    "peer_assertive" to R.drawable.peer_assertive,
    "peer_logic" to R.drawable.peer_logical,
    "peer_friendly" to R.drawable.peer_friendly,
    "peer_quiet" to R.drawable.peer_quiet,
)

private val ParticipantColors = mapOf(
    "moderator" to BrandOrange,
    "hr" to BrandOrange,
    "tech" to InkCharcoal,
    "hiring" to BrandDeepOrange,
    USER_KEY to BrandDeepOrange,
    "peer_assertive" to Color(0xFF7A8C5A),
    "peer_logic" to InkGray700,
    "peer_friendly" to Color(0xFFD4A574),
    "peer_quiet" to InkGray400,
)

// Used when no group session personas are available (mock flow).
private val fallbackSolo = listOf(
    PersonaDto("moderator", "主考官", "moderator"),
    PersonaDto("peer_assertive", "AI-強勢", "peer"),
    PersonaDto("peer_logic", "AI-邏輯", "peer"),
    PersonaDto("peer_friendly", "AI-親切", "peer"),
    PersonaDto("peer_quiet", "AI-沉默", "peer"),
)
private val fallbackPanel = listOf(
    PersonaDto("hiring", "用人主管", "moderator"),
    PersonaDto("tech", "技術主管", "moderator"),
    PersonaDto("hr", "HR 主管", "moderator"),
    PersonaDto("peer_assertive", "AI-強勢", "peer"),
    PersonaDto("peer_logic", "AI-邏輯", "peer"),
    PersonaDto("peer_friendly", "AI-親切", "peer"),
    PersonaDto("peer_quiet", "AI-沉默", "peer"),
)

// A speaker string may arrive as a persona id or a display name; resolve either one.
private fun List<PersonaDto>.idOf(key: String): String =
    firstOrNull { it.id == key || it.displayName == key }?.id ?: key

private fun List<PersonaDto>.nameOf(key: String): String =
    firstOrNull { it.id == key || it.displayName == key }?.displayName ?: key

private fun List<PersonaDto>.isInterviewer(key: String): Boolean =
    firstOrNull { it.id == key || it.displayName == key }?.role?.let { it != "peer" } ?: false
// Interviewers first, then the user, then peers. Order follows the persona list.
private fun rosterOf(personas: List<PersonaDto>): List<String> =
    personas.filter { it.role != "peer" }.map { it.id } +
            USER_KEY +
            personas.filter { it.role == "peer" }.map { it.id }
private val panelNames = listOf("用人主管", "技術主管", "HR 主管")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveGroupScreen(navController: NavHostController) {
    val panel = InterviewConfig.groupInterviewers == 3
    // Only trust session personas that came from a group start. Stale panel or single
    // data has no peers, so it is ignored here.
    val personas = remember {
        InterviewAiSession.personas.takeIf { list -> list.any { it.role == "peer" } }
            ?: if (panel) fallbackPanel else fallbackSolo
    }
    val useAi = remember { InterviewAiSession.sessionId != null }
    var finished by remember { mutableStateOf(false) }
    val askedTopics = remember { mutableStateListOf<String>() }
    val spokenBy = remember { mutableStateListOf<String>() }
    var lastQuestion by remember { mutableStateOf(InterviewAiSession.openingQuestion.orEmpty()) }
    val interruptLines = remember { InterviewAiSession.interruptLines }
    val interruptCap =
        if (useAi && interruptLines.isNotEmpty()) minOf(InterviewAiSession.interruptCap, interruptLines.size)
        else com.careersandbox.app.data.mock.MockGroupDispatcher.interruptCap()
    var panelIdx by remember { mutableIntStateOf(0) }
    fun nextInterviewer(): String {
        val n = panelNames[panelIdx % panelNames.size]
        panelIdx++
        return n
    }
    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            val opening = InterviewAiSession.openingQuestion
            if (useAi && !opening.isNullOrBlank()) {
                val who = InterviewAiSession.openingSpeaker.ifBlank {
                    personas.firstOrNull { it.role != "peer" }?.displayName ?: "主考官"
                }
                add(ChatMessage("open", who, opening, isUser = false, isInterviewer = personas.isInterviewer(who)))
            } else {
                addAll(
                    MockData.groupInterviewScript.map {
                        if (panel && it.speaker == "主考官") it.copy(speaker = "用人主管") else it
                    }
                )
            }
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
    // Index of the exchange after which the last interruption happened; -1 means none yet.
    var lastInterruptAtIdx by remember { mutableIntStateOf(-1) }
    val currentSpeaker = if (isTyping) typingSpeaker
        else (messages.lastOrNull()?.speaker ?: if (panel) "用人主管" else "主考官")

    fun submitGroup(
        visible: String,
        analyzed: String,
        segments: List<String> = emptyList(),
        segmentStartsMs: List<Long> = emptyList(),
        inputMode: String = "typed",
        endedBy: String = "unknown",
        segmentEndsMs: List<Long> = emptyList(),
    ) {
        if (isTyping) return
        com.careersandbox.app.data.mock.InterviewSession.recordGroupSay(visible)
        com.careersandbox.app.data.mock.InterviewSession.recordGroupUtterance(
            speaker = "你",
            content = visible,
            isUser = true,
            segments = segments,
            segmentStartsMs = segmentStartsMs,
            segmentEndsMs = segmentEndsMs,
            inputMode = inputMode,
            endedBy = endedBy,
        )
        messages.add(ChatMessage("u${messages.size}", "你", visible, isUser = true))
        isTyping = true
        scope.launch {
            val sessionId = InterviewAiSession.sessionId
            val aiReply: Pair<String, String>? = if (useAi && sessionId != null) {
                // The speaker is unknown until the reply arrives, so the typing bubble starts as the moderator.
                typingSpeaker = personas.firstOrNull { it.role != "peer" }?.displayName ?: "主考官"
                try {
                    val res = InterviewAiApiClient.interviewAiApi.submitTurn(
                        sessionId,
                        TurnRequest(
                            answer = analyzed,
                            followUpIdx = followUpIdx,
                            question = lastQuestion,
                            fallback = InterviewAiSession.fallbackProbes,
                            mode = "group",
                            inputMode = inputMode,
                            endedBy = endedBy,
                            context = InterviewContextDto(
                                round = InterviewConfig.round,
                                language = InterviewConfig.language,
                                type = InterviewConfig.type,
                                difficulty = InterviewConfig.difficulty,
                                groupInterviewers = InterviewConfig.groupInterviewers,
                                groupSize = InterviewConfig.groupSize,
                                groupRole = InterviewConfig.groupRole,
                            ),
                            spokenBy = spokenBy.toList(),
                            askedTopics = askedTopics.toList(),
                            groupSays = com.careersandbox.app.data.mock.InterviewSession
                                .groupTranscript.map { it.toDto() },
                        ),
                    )
                    val body = res.body()
                    if (res.isSuccessful && body != null) {
                        if (body.topic.isNotBlank()) askedTopics.add(body.topic)
                        val speaker = body.speaker.ifBlank { typingSpeaker }
                        spokenBy.add(speaker)
                        lastQuestion = body.nextQuestion
                        speaker to body.nextQuestion
                    } else null
                } catch (e: Exception) {
                    null
                }
            } else null

            // No session, or the request failed: fall back to the local mock dispatcher.
            val (who, line) = aiReply ?: run {
                val (rawWho, mockLine) =
                    com.careersandbox.app.data.mock.MockGroupDispatcher.dispatch(analyzed, followUpIdx)
                val mockWho = if (panel && rawWho == "主考官") nextInterviewer() else rawWho
                typingSpeaker = mockWho
                delay(1400)
                mockWho to mockLine
            }
            typingSpeaker = who
            com.careersandbox.app.data.mock.InterviewSession.recordGroupUtterance(who, line, isUser = false)
            messages.add(ChatMessage("g${messages.size}", who, line, isUser = false, isInterviewer = personas.isInterviewer(who)))
            followUpIdx++
            isTyping = false
        }
    }

    LaunchedEffect(Unit) {
        com.careersandbox.app.data.mock.InterviewSession.reset()
        com.careersandbox.app.data.mock.InterviewAiReport.reset()
        com.careersandbox.app.data.mock.InterviewConfig.lastWasGroup = true
        if (useAi) {
            // The opening question belongs in the transcript so the model sees the full dialogue.
            messages.firstOrNull()?.let {
                com.careersandbox.app.data.mock.InterviewSession.recordGroupUtterance(
                    it.speaker, it.content, isUser = false,
                )
                spokenBy.add(it.speaker)
            }
            InterviewAiSession.openingTopic.takeIf { it.isNotBlank() }?.let { askedTopics.add(it) }
        }
    }

    // 頁內語音(SpeechRecognizer,需 RECORD_AUDIO,不跳 Google 框):逐字稿餵 dispatch 做同儕路由
    var isTranscribing by remember { mutableStateOf(false) }
    var pendingResult by remember { mutableStateOf<TranscriptionResult?>(null) }
    var pendingEndedBy by remember { mutableStateOf("unknown") }
    val transcribeRepo = remember { RemoteTranscribeRepository() }
    val recorder = rememberInPageAudioRecorder(maxDurationMs = 120_000L) { file, endedBy ->
        isTranscribing = true
        pendingEndedBy = endedBy
        scope.launch {
            transcribeRepo.transcribe(file)
                .onSuccess { result -> pendingResult = result }
                .onFailure { /* 轉錄失敗：畫面會回到可以重新錄音的狀態 */ }
            isTranscribing = false
            file.delete()
        }
    }
    val timeUp = elapsedSec >= GROUP_MAX_SECONDS
    LaunchedEffect(timeUp, isTyping, isTranscribing, recorder.isRecording, pendingResult) {
        if (timeUp && !finished && !isTyping && !isTranscribing &&
            !recorder.isRecording && pendingResult == null
        ) {
            val who = personas.firstOrNull { it.role != "peer" }?.displayName ?: "主考官"
            messages.add(
                ChatMessage("end", who, "時間到了，今天的討論就到這裡，謝謝大家。", isUser = false, isInterviewer = true)
            )
            finished = true
        }
    }

    LaunchedEffect(messages.size, isTyping) {
        val target = if (isTyping) messages.size else messages.size - 1
        if (target >= 0) listState.animateScrollToItem(target)
    }

    // Fires a peer interruption after the user pauses typing.
    // The effect restarts whenever `input` changes, so try/finally guarantees
    // isTyping is cleared even when it is cancelled mid-flight.
    LaunchedEffect(input) {
        // followUpIdx grows once per user answer, so a gap of 2 means at least one answer in between.
        fun canInterrupt() =
            !finished && !isTyping && input.length >= 14 &&
                    interruptCap > interruptCount && followUpIdx - lastInterruptAtIdx >= 2
        if (canInterrupt()) {
            delay(2600)
            if (canInterrupt()) {
                val idx = interruptCount
                val line = if (useAi && idx < interruptLines.size) interruptLines[idx]
                else com.careersandbox.app.data.mock.MockGroupDispatcher.interruptLine(idx)
                val interrupter = personas.firstOrNull { it.role == "peer" }?.displayName ?: "AI-強勢"
                typingSpeaker = interrupter
                isTyping = true
                try {
                    delay(700)
                    com.careersandbox.app.data.mock.InterviewSession.recordGroupUtterance(interrupter, line, isUser = false)
                    messages.add(ChatMessage("int${messages.size}", interrupter, line, isUser = false))
                    interruptCount++
                    lastInterruptAtIdx = followUpIdx
                } finally {
                    isTyping = false
                }
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
                    TextButton(onClick = {
                        navController.navigate(Routes.INTERVIEW_REPORT) {
                            popUpTo(Routes.INTERVIEW_HUB)
                        }
                    }) {
                        Text("結束", color = BrandOrange, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            Column {
                val pending = pendingResult
                if (finished) {
                    Box(Modifier.fillMaxWidth().background(PaperOff).padding(16.dp)) {
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp))
                                .background(BrandOrange)
                                .clickable {
                                    navController.navigate(Routes.INTERVIEW_REPORT) {
                                        popUpTo(Routes.INTERVIEW_HUB)
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) { Text("查看報告", color = PaperWhite, fontWeight = FontWeight.Bold) }
                    }
                } else if (pending != null) {
                    Column(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
                        Text("確認這段發言內容：", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(pending.text, color = InkBlack, fontSize = 13.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(999.dp))
                                    .background(InkGray100)
                                    .clickable { pendingResult = null }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) { Text("重新錄音", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(999.dp))
                                    .background(BrandOrange)
                                    .clickable {
                                        submitGroup(
                                            pending.text, pending.text,
                                            pending.segmentTexts, pending.segmentStartsMs,
                                            inputMode = "voice", endedBy = pendingEndedBy,
                                            segmentEndsMs = pending.segmentEndsMs,
                                        )
                                        pendingResult = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) { Text("確認送出", color = PaperWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                } else if (isTranscribing) {
                    Row(
                        Modifier.fillMaxWidth().background(PaperOff).padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(color = BrandOrange, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("轉錄中...", color = InkGray500, fontSize = 12.sp)
                    }
                } else {
                    GroupBottomBar(
                        input, { input = it },
                        onVoice = { if (recorder.isRecording) recorder.stop() else recorder.start() },
                        isListening = recorder.isRecording,
                        amplitude = recorder.amplitude,
                        elapsedMs = recorder.elapsedMs,
                    ) {
                        if (input.isNotBlank() && !isTyping) {
                            val said = input
                            input = ""
                            submitGroup(said, said)
                        }
                    }
                }
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            ParticipantsRow(personas, currentSpeaker)

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
                    Box(Modifier.animateItem()) { GroupMessageBubble(m, personas) }
                }
                if (isTyping) {
                    item(key = "typing") { GroupTypingBubble(typingSpeaker, personas) }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ParticipantsRow(personas: List<PersonaDto>, currentSpeaker: String) {
    val currentId = personas.idOf(currentSpeaker)
    val roster = remember(personas) { rosterOf(personas) }
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(roster, key = { it }) { id ->
            val label = if (id == USER_KEY) USER_KEY else personas.nameOf(id)
            val color = ParticipantColors[id] ?: BrandOrange
            val isCurrent = id == currentId
            val avatar = ParticipantAvatars[id]
            val tileAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.5f, label = "pa-$id")
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
                        Text(label.take(1), color = PaperWhite,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) InkBlack else InkGray500,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 72.dp))
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
private fun GroupMessageBubble(m: ChatMessage, personas: List<PersonaDto>) {
    val speakerId = personas.idOf(m.speaker)
    val speakerName = personas.nameOf(m.speaker)
    val speakerColor = ParticipantColors[speakerId] ?: InkGray500
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!m.isUser) {
            val avatar = ParticipantAvatars[speakerId]
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
                    Text(speakerName.take(1), color = PaperWhite,
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
                    Text(speakerName, style = MaterialTheme.typography.labelSmall,
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
    amplitude: Float,
    elapsedMs: Long,
    onSend: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RecordingMicButton(
                isRecording = isListening,
                amplitude = amplitude,
                elapsedMs = elapsedMs,
                maxDurationMs = 120_000L,
                size = 48.dp,
                onClick = onVoice,
            )
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
private fun GroupTypingBubble(speaker: String, personas: List<PersonaDto>) {
    val speakerId = personas.idOf(speaker)
    val color = ParticipantColors[speakerId] ?: InkGray500
    val t = rememberInfiniteTransition(label = "gtyping")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        val avatar = ParticipantAvatars[speakerId]
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
                    personas.nameOf(speaker).take(1), color = PaperWhite,
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
