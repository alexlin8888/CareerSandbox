package com.careersandbox.app.ui.screens.workplace

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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.model.ChatMessage
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val openingScript = listOf(
    ChatMessage("w1", "主管 Ken", "坐。先說結論:上週的匯出功能延了兩天,我想知道是估錯,還是中間出了事。", isInterviewer = true),
    ChatMessage("w2", "你", "主要是第三方 API 的文件跟實際行為不一致,debug 花掉比預期多的時間。", isUser = true),
    ChatMessage("w3", "主管 Ken", "嗯。那你是哪一天發現的?發現的當下,為什麼我是最後一個知道的?", isInterviewer = true),
)

private val managerFollowUps = listOf(
    "我不是在追究。我要的是你卡住的第一時間,我就知道。具體一點,下次你會怎麼做?",
    "如果週四 demo 前還是修不完,你的 plan B 是什麼?",
    "手上三件事:匯出收尾、新需求評估、客訴回報。你自己排,先做哪個,為什麼?",
    "好,這件事到這裡。最後——有什麼需要我幫你擋的?",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkplaceChatScreen(navController: NavHostController) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(openingScript) } }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    var followUpIdx by remember { mutableIntStateOf(0) }

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
                        Text("和主管 1on1",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text("職場沙盒 ・ 模擬場景",
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
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("結束", color = BrandOrange, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            WorkplaceBottomBar(input, { input = it }) {
                if (input.isNotBlank() && !isTyping) {
                    messages.add(ChatMessage("wu${messages.size}", "你", input, isUser = true))
                    input = ""
                    isTyping = true
                    scope.launch {
                        delay(1300)
                        messages.add(ChatMessage("wm${messages.size}", "主管 Ken",
                            managerFollowUps[followUpIdx % managerFollowUps.size],
                            isInterviewer = true))
                        followUpIdx++
                        isTyping = false
                    }
                }
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            ManagerHeader()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "scene") { SceneCard() }
                items(messages, key = { it.id }) { m ->
                    Box(Modifier.animateItem()) { WpBubble(m) }
                }
                if (isTyping) {
                    item(key = "typing") { WpTypingBubble() }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ManagerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.interviewer_tech),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Ken ・ 你的直屬主管",
                style = MaterialTheme.typography.titleSmall,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
            Text("嚴厲,但講理",
                style = MaterialTheme.typography.labelSmall,
                color = InkGray500)
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandPeach.copy(alpha = 0.55f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text("情境演練",
                color = BrandDeepOrange,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SceneCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkCharcoal)
            .padding(16.dp),
    ) {
        Text("場景",
            color = PaperWhite.copy(alpha = 0.55f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text("週一 09:30 ・ 會議室 B",
            color = PaperWhite,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text("你上週負責的匯出功能延期了兩天。主管把你約進來,門關上了。",
            color = PaperWhite.copy(alpha = 0.8f),
            fontSize = 12.sp,
            lineHeight = 18.sp)
    }
}

@Composable
private fun WpBubble(m: ChatMessage) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!m.isUser) {
            Image(
                painter = painterResource(R.drawable.interviewer_tech),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp),
            )
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
                .background(if (m.isUser) InkBlack else MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                if (!m.isUser) {
                    Text(m.speaker,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandDeepOrange, fontWeight = FontWeight.SemiBold)
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
private fun WorkplaceBottomBar(input: String, onChange: (String) -> Unit, onSend: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input, onValueChange = onChange,
                placeholder = { Text("你會怎麼回?", color = InkGray400) },
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
private fun WpTypingBubble() {
    val t = rememberInfiniteTransition(label = "wtyping")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Image(
            painter = painterResource(R.drawable.interviewer_tech),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(34.dp),
        )
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
