package com.careersandbox.app.ui.screens.interview

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
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

// 各角色河狸頭像(「你」維持色圈)
private val ParticipantAvatars = mapOf(
    "主考官" to R.drawable.interviewer_hr,
    "AI-強勢" to R.drawable.peer_assertive,
    "AI-邏輯" to R.drawable.peer_logical,
    "AI-親切" to R.drawable.peer_friendly,
    "AI-沉默" to R.drawable.peer_quiet,
)

// 各角色色票
private val ParticipantColors = mapOf(
    "主考官" to BrandOrange,
    "你" to BrandDeepOrange,
    "AI-強勢" to Color(0xFF7A8C5A),
    "AI-邏輯" to InkGray700,
    "AI-親切" to Color(0xFFD4A574),
    "AI-沉默" to InkGray400,
)

private fun String.containsAny(vararg keys: String) = keys.any { this.contains(it) }

// 關鍵字感知:對的 AI 應徵者跳出來接你的話
private val gLogicPool = listOf(
    "等等,這個數字的母數是多少?沒有對照組我不敢下結論。",
    "你這段推論跳了一步——中間的假設是什麼?",
)
private val gAssertivePool = listOf(
    "我打斷一下——結論先講,我們時間不多。",
    "這樣太慢了。我的版本:先上線再修,你要不要跟?",
)
private val gFriendlyPool = listOf(
    "我接你這段,方向我同意,分工那邊可以再具體一點嗎?",
    "你剛剛那個例子不錯,可以再展開一點。",
)
private val gExaminerHonestPool = listOf(
    "沒關係,不確定就說不確定。那你目前確定的部分是什麼?",
)

private fun pickGroupFollowUp(said: String, idx: Int, fallback: List<Pair<String, String>>): Pair<String, String> = when {
    said.containsAny("不知道", "不確定", "沒想過") -> "主考官" to gExaminerHonestPool.random()
    said.containsAny("數據", "資料", "數字", "驗證", "分析") -> "AI-邏輯" to gLogicPool.random()
    said.containsAny("結論", "直接", "先做", "搶", "快") -> "AI-強勢" to gAssertivePool.random()
    said.containsAny("大家", "同意", "補充", "一起", "團隊") -> "AI-親切" to gFriendlyPool.random()
    else -> fallback[idx % fallback.size]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveGroupScreen(navController: NavHostController) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(MockData.groupInterviewScript) } }
    var input by remember { mutableStateOf("") }
    var showObservation by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var typingSpeaker by remember { mutableStateOf("主考官") }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    val groupFollowUps = listOf(
        "主考官" to "謝謝。換個角度,如果資源只夠做一件事,你會先砍掉哪個?",
        "AI-強勢" to "我補一句 — 我的做法更直接:先搶下市場,細節之後再優化。",
        "AI-邏輯" to "可是這沒有數據支撐吧?我會先做小規模驗證,再決定要不要放大。",
        "AI-親切" to "我覺得你講得不錯耶,不過團隊怎麼分工那段可以再多說一點。",
        "主考官" to "那你會怎麼回應剛剛其他人提出的質疑?",
    )
    val currentSpeaker = if (isTyping) typingSpeaker else (messages.lastOrNull()?.speaker ?: "主考官")

    LaunchedEffect(messages.size, isTyping) {
        val target = if (isTyping) messages.size else messages.size - 1
        if (target >= 0) listState.animateScrollToItem(target)
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
                        Text("小組討論 ・ AI 應徵者同場",
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
        bottomBar = { GroupBottomBar(input, { input = it }) {
            if (input.isNotBlank() && !isTyping) {
                val said = input
                messages.add(ChatMessage("u${messages.size}", "你", said, isUser = true))
                input = ""
                val (who, line) = pickGroupFollowUp(said, followUpIdx, groupFollowUps)
                typingSpeaker = who
                isTyping = true
                scope.launch {
                    delay(1400)
                    messages.add(ChatMessage("g${messages.size}", who, line, isUser = false))
                    followUpIdx++
                    isTyping = false
                }
            }
        } }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            ParticipantsRow(currentSpeaker)

            // 觀察面板
            AnimatedVisibility(visible = showObservation) {
                ObservationPanel(onClose = { showObservation = false })
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
private fun ParticipantsRow(currentSpeaker: String) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(ParticipantColors.entries.toList()) { (name, color) ->
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
private fun ObservationPanel(onClose: () -> Unit) {
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
            ObservationRow("你的發言次數", "2 次")
            ObservationRow("被打斷次數", "0 次")
            ObservationRow("引用他人觀點", "1 次")
            ObservationRow("平均發言長度", "32 秒")
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
private fun GroupBottomBar(input: String, onChange: (String) -> Unit, onSend: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton("搶答", Icons.Outlined.Mic, Modifier.weight(1f)) {
                onChange("我直接說結論:")
            }
            ActionButton("舉手補充", Icons.Outlined.PanTool, Modifier.weight(1f)) {
                onChange("補充剛剛的觀點:")
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
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
