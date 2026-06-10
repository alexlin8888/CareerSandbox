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
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.ChatMessage
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun String.containsAny(vararg keys: String) = keys.any { this.contains(it) }

// 關鍵字感知追問池(之後由 LangGraph evaluate_node 取代,介面不變)
private val probesData = listOf(
    "這個數字是怎麼算出來的?基準是什麼?",
    "如果數據跟你的直覺打架,你信哪個?為什麼?",
)
private val probesTeam = listOf(
    "團隊裡誰跟你意見最不合?那次最後怎麼收?",
    "如果有人擺爛,你的第一步是什麼?",
)
private val probesFail = listOf(
    "這件事裡,你自己要負的是哪一塊?",
    "同樣的錯,後來有再犯嗎?你改了什麼?",
)
private val probesHonest = listOf(
    "沒關係,當場想。你會從哪裡開始?",
    "可以。那換個你熟的——講一個你最有把握的決定。",
)
private val probesTime = listOf(
    "時間砍一半,你先丟掉哪一塊?",
    "你怎麼判斷一件事該做快的版本,還是好的版本?",
)
private val microReactions = listOf("嗯。", "(他停了一下)", "(低頭記了些什麼)", "(點了點頭)")

private fun pickProbe(said: String, idx: Int, fallback: List<String>): String = when {
    said.containsAny("不知道", "不確定", "沒想過", "沒有經驗") -> probesHonest.random()
    said.containsAny("數據", "資料", "數字", "分析", "%", "成長") -> probesData.random()
    said.containsAny("團隊", "合作", "夥伴", "組員", "溝通", "衝突") -> probesTeam.random()
    said.containsAny("失敗", "錯", "搞砸", "延期", "沒做好") -> probesFail.random()
    said.containsAny("時間", "趕", "deadline", "來不及", "期限") -> probesTime.random()
    else -> fallback[idx % fallback.size]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveIndividualScreen(navController: NavHostController) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(MockData.individualInterviewScript) } }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var reaction by remember { mutableStateOf<String?>(null) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    val probes = listOf(
        "嗯,了解。可以再給一個更具體的例子嗎?",
        "那當時你怎麼衡量這個決定的影響?",
        "如果重來一次,你會有什麼不同的做法?",
        "這段經驗裡,你覺得自己最關鍵的貢獻是什麼?",
    )

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
                        Text("行為面試 ・ 中等難度", style = MaterialTheme.typography.labelSmall,
                            color = InkGray500)
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
            BottomInputBar(input, { input = it }) {
                if (input.isNotBlank() && !isTyping && reaction == null) {
                    val said = input
                    messages.add(ChatMessage("u${messages.size}", "你", said, isUser = true))
                    input = ""
                    reaction = microReactions.random()
                    scope.launch {
                        delay(800)
                        reaction = null
                        isTyping = true
                        delay(1300)
                        messages.add(
                            ChatMessage("ai${messages.size}", "面試官", pickProbe(said, followUpIdx, probes), isUser = false)
                        )
                        followUpIdx++
                        isTyping = false
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
private fun BottomInputBar(input: String, onChange: (String) -> Unit, onSend: () -> Unit) {
    val ctx = LocalContext.current
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(BrandPeach)
                    .pressScale {
                        Toast.makeText(ctx, "語音輸入規劃中,先用文字回答", Toast.LENGTH_SHORT).show()
                    },
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
