package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.outlined.Mic
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceEditScreen(navController: NavHostController) {
    var mode by remember { mutableStateOf(EditMode.CHAT) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("社團") }
    var timeRange by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var learning by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(0) }
    var aiTyping by remember { mutableStateOf(false) }
    val answers = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    val chatHistory = remember {
        mutableStateListOf("AI" to chatQuestions[0])
    }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("新增經驗", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(PaperOff).padding(20.dp)) {
                PrimaryDarkButton(text = "儲存", onClick = { navController.popBackStack() })
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_writing),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "把這段經歷先完整寫下來,之後客製各版本履歷都會從這裡取材。",
                    color = InkGray500,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            ModeSegmented(mode = mode, onChange = { mode = it })
            Spacer(Modifier.height(20.dp))

            when (mode) {
                EditMode.CHAT -> ChatEdit(
                    history = chatHistory,
                    input = chatInput,
                    onInputChange = { chatInput = it },
                    step = step,
                    aiTyping = aiTyping,
                    answers = answers,
                    onChipTap = { chatInput = it },
                ) {
                    if (chatInput.isNotBlank() && !aiTyping && step < 3) {
                        val said = chatInput
                        chatInput = ""
                        chatHistory.add("你" to said)
                        answers.add(said)
                        step++
                        aiTyping = true
                        scope.launch {
                            if (step < 3) {
                                delay(900)
                                chatHistory.add("AI" to chatQuestions[step])
                            } else {
                                delay(1500)
                                chatHistory.add("AI" to "整理好了——確認下面這張經驗卡沒問題,就按「儲存」存入母版。")
                            }
                            aiTyping = false
                        }
                    }
                }
                EditMode.FORM -> FormEdit(
                    title, { title = it },
                    category, { category = it },
                    timeRange, { timeRange = it },
                    role, { role = it },
                    action, { action = it },
                    result, { result = it },
                    learning, { learning = it },
                )
            }
        }
    }
}

private enum class EditMode { CHAT, FORM }

private val chatQuestions = listOf(
    "最近做過什麼讓你印象深刻的事?可以是課程、社團、實習都好。",
    "你在裡面具體負責什麼?一句話就好。",
    "有沒有可以量化的成果?人數、金額、百分比,什麼都好。",
)

private val starterChips = listOf(
    "課程專題" to "我在課程專題做了",
    "社團活動" to "我在社團辦了",
    "實習打工" to "我在實習的時候",
    "競賽得獎" to "我參加了一場比賽,",
)

@Composable
private fun ModeSegmented(mode: EditMode, onChange: (EditMode) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100)
            .padding(4.dp),
    ) {
        SegItem("對話式 (推薦)", mode == EditMode.CHAT) { onChange(EditMode.CHAT) }
        SegItem("表單式", mode == EditMode.FORM) { onChange(EditMode.FORM) }
    }
}

@Composable
private fun RowScope.SegItem(label: String, sel: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (sel) BrandOrange
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = if (sel) PaperWhite else InkGray700,
            fontWeight = if (sel) FontWeight.Bold else FontWeight.SemiBold)
    }
}

@Composable
private fun ChatEdit(
    history: List<Pair<String, String>>,
    input: String,
    onInputChange: (String) -> Unit,
    step: Int,
    aiTyping: Boolean,
    answers: List<String>,
    onChipTap: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(3) { i ->
                Box(
                    Modifier
                        .width(if (i == step.coerceAtMost(2)) 18.dp else 7.dp)
                        .height(7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (i <= step.coerceAtMost(2)) BrandOrange else InkGray200),
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            if (step < 3) "第 ${step + 1} / 3 題" else "整理完成",
            color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        )
    }
    Spacer(Modifier.height(10.dp))
    Column(
        Modifier.fillMaxWidth().heightIn(max = 430.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        history.forEach { (speaker, msg) ->
            val isUser = speaker == "你"
            Row(
                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            ) {
                Box(
                    Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp,
                        ))
                        .background(if (isUser) InkBlack else MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) PaperWhite else InkBlack)
                }
            }
        }
        if (aiTyping) ChatTypingDots()
        if (step == 0 && history.size == 1 && !aiTyping) {
            Spacer(Modifier.height(6.dp))
            Text("不知道從哪開始?點一個起手:", color = InkGray400, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
            starterChips.chunked(2).forEach { rowChips ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowChips.forEach { (label, starter) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(BrandPeach.copy(alpha = 0.5f))
                                .pressScale { onChipTap(starter) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(label, color = BrandDeepOrange,
                                fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        if (step >= 3 && !aiTyping) {
            Spacer(Modifier.height(8.dp))
            ExperienceDraftCard(answers)
            Spacer(Modifier.height(8.dp))
        }
    }
    Spacer(Modifier.height(12.dp))
    var voiceMode by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var recordSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(recording) {
        recordSec = 0
        while (recording) { delay(1000); recordSec++ }
    }
    if (voiceMode) {
        VoiceBar(
            recording = recording,
            recordSec = recordSec,
            onKeyboard = { voiceMode = false; recording = false },
            onPressStart = { recording = true },
            onPressEnd = {
                val sec = recordSec
                recording = false
                if (sec > 0) {
                    onInputChange("(語音回答・$sec 秒)")
                    onSend()
                }
            },
        )
    } else     OutlinedTextField(
        value = input, onValueChange = onInputChange,
        placeholder = { Text("用口語回答就好", color = InkGray400) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        trailingIcon = {
            Row {
                IconButton(onClick = { voiceMode = true }) {
                    Icon(Icons.Outlined.Mic, contentDescription = null, tint = InkGray500)
                }
                IconButton(onClick = onSend) {
                    Icon(Icons.Outlined.Send, contentDescription = null, tint = BrandOrange)
                }
            }
        },
        maxLines = 4,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

@Composable
private fun FormEdit(
    title: String, onTitle: (String) -> Unit,
    category: String, onCategory: (String) -> Unit,
    timeRange: String, onTime: (String) -> Unit,
    role: String, onRole: (String) -> Unit,
    action: String, onAction: (String) -> Unit,
    result: String, onResult: (String) -> Unit,
    learning: String, onLearning: (String) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ExpField("標題", title, onTitle)
        ExpField("類別 (學業/工作/社團/競賽/其他)", category, onCategory)
        ExpField("時間範圍", timeRange, onTime)
        ExpField("角色", role, onRole)
        ExpField("做了什麼", action, onAction, multi = true)
        ExpField("結果", result, onResult, multi = true)
        ExpField("學到什麼", learning, onLearning, multi = true)
    }
}

@Composable
private fun ExpField(label: String, value: String, onChange: (String) -> Unit, multi: Boolean = false) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = InkGray700, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = !multi, minLines = if (multi) 3 else 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }
}

@Composable
private fun ChatTypingDots() {
    val t = rememberInfiniteTransition(label = "expTyping")
    val a by t.animateFloat(
        initialValue = 0.25f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "expTypingA",
    )
    Row(Modifier.padding(vertical = 5.dp)) {
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier.size(6.dp).clip(CircleShape)
                            .background(InkGray400.copy(alpha = if (i == 1) a else a * 0.6f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExperienceDraftCard(answers: List<String>) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(BrandPeach.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text("經驗卡 ・ 已整理", color = BrandDeepOrange,
                    fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.beaver_thumbsup),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        DraftRow("經歷", answers.getOrElse(0) { "" })
        DraftRow("你的角色", answers.getOrElse(1) { "" })
        DraftRow("量化成果", answers.getOrElse(2) { "" })
        Spacer(Modifier.height(8.dp))
        Text("之後每個職缺的客製版本,都會從這張卡取材。",
            color = InkGray400, fontSize = 10.sp, lineHeight = 14.sp)
    }
}

@Composable
private fun DraftRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label, color = InkGray500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(value.ifBlank { "—" }, color = InkBlack, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
    }
}
