package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.remote.ChatHistoryTurn
import com.careersandbox.app.data.remote.CreateExperienceRequest
import com.careersandbox.app.data.repository.RemoteExperienceChatRepository
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceEditScreen(navController: NavHostController, expId: String? = null) {
    // expId == null → create mode; expId != null → edit mode (prefilled)
    val isEditMode = expId != null

    var mode by remember { mutableStateOf(if (isEditMode) EditMode.FORM else EditMode.CHAT) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("社團") }
    var timeRange by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var learning by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var aiTyping by remember { mutableStateOf(false) }
    var chatDone by remember { mutableStateOf(false) }
    var chatError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val chatRepo = remember { RemoteExperienceChatRepository() }
    val chatHistory = remember {
        mutableStateListOf("AI" to openingQuestion)
    }

    val editViewModel: ExperienceEditViewModel = viewModel { ExperienceEditViewModel() }
    val saveState = editViewModel.uiState
    val isSaving = saveState is SaveExperienceUiState.Saving
    var showTitleHint by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf(false) }

    // Edit mode: fetch the existing record once
    LaunchedEffect(Unit) {
        if (expId != null) editViewModel.load(expId)
    }

    // When the record arrives, pour its values into the form fields (once)
    var prefilled by remember { mutableStateOf(false) }
    val loaded = editViewModel.loadedExperience
    LaunchedEffect(loaded) {
        if (loaded != null && !prefilled) {
            title = loaded.title
            category = loaded.category
            timeRange = loaded.period
            role = loaded.role
            action = loaded.action
            result = loaded.result
            learning = loaded.learning
            prefilled = true
        }
    }

    // Save or delete finished successfully → leave the screen
    LaunchedEffect(saveState) {
        if (saveState is SaveExperienceUiState.Success) navController.popBackStack()
    }

    // Sends one turn to the AI backend and folds the response into local state.
    fun sendChatTurn(said: String) {
        chatInput = ""
        chatHistory.add("你" to said)
        aiTyping = true
        chatError = null
        scope.launch {
            // Everything except the message we just appended is "prior history"
            val apiHistory = chatHistory.dropLast(1).map { (speaker, text) ->
                ChatHistoryTurn(speaker = if (speaker == "AI") "assistant" else "user", text = text)
            }
            val outcome = chatRepo.sendTurn(apiHistory, said)
            outcome.onSuccess { resp ->
                val f = resp.extractedFields
                if (f.role.isNotBlank()) role = f.role
                if (f.action.isNotBlank()) action = f.action
                if (f.result.isNotBlank()) result = f.result
                if (f.learning.isNotBlank()) learning = f.learning
                if (f.title.isNotBlank() && title.isBlank()) title = f.title

                if (resp.done) {
                    chatDone = true
                    chatHistory.add("AI" to "整理好了。幫這段經歷確認標題和類別,再按「儲存」存入母版。")
                } else if (resp.nextQuestion != null) {
                    chatHistory.add("AI" to resp.nextQuestion)
                }
            }.onFailure { e ->
                chatError = e.message
                chatHistory.add("AI" to "不好意思,剛剛發生一點問題,可以再說一次看看嗎?")
            }
            aiTyping = false
        }
    }

    Scaffold(
        containerColor = PaperOff,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "編輯經驗" else "新增經驗",
                        fontWeight = FontWeight.Bold, color = InkBlack,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            if (!(mode == EditMode.CHAT && !isEditMode)) {
            Column(Modifier.fillMaxWidth().background(PaperOff).padding(20.dp)) {
                if (showTitleHint && title.isBlank()) {
                    Text("標題是必填的", color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                }
                if (saveState is SaveExperienceUiState.Error) {
                    Text(saveState.message, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                }
                if (isEditMode) {
                    // Edit mode: delete and save side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(BrandDeepOrange.copy(alpha = 0.1f))
                                .pressScale { if (!isSaving) pendingDelete = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "刪除",
                                color = BrandDeepOrange,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(InkBlack)
                                .pressScale {
                                    when {
                                        isSaving -> Unit
                                        title.isBlank() -> showTitleHint = true
                                        else -> {
                                            showTitleHint = false
                                            editViewModel.save(
                                                expId,
                                                CreateExperienceRequest(
                                                    title = title.trim(),
                                                    category = category,
                                                    period = timeRange.trim(),
                                                    role = role.trim(),
                                                    action = action.trim(),
                                                    result = result.trim(),
                                                    learning = learning.trim(),
                                                )
                                            )
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (isSaving) "儲存中..." else "確認儲存",
                                color = PaperWhite,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                } else {
                    // Reaches here only for create mode + FORM — CHAT has its own save button below
                    PrimaryDarkButton(
                        text = if (isSaving) "儲存中..." else "儲存",
                        onClick = {
                            when {
                                isSaving -> Unit
                                title.isBlank() -> showTitleHint = true
                                else -> {
                                    showTitleHint = false
                                    editViewModel.save(
                                        null,
                                        CreateExperienceRequest(
                                            title = title.trim(),
                                            category = category,
                                            period = timeRange.trim(),
                                            role = role.trim(),
                                            action = action.trim(),
                                            result = result.trim(),
                                            learning = learning.trim(),
                                        )
                                    )
                                }
                            }
                        },
                    )
                }
            }
            }
        }
    ) { pad ->
        if (isEditMode && loaded == null) {
            // Still fetching the record — don't show an empty form
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                if (editViewModel.loadError == null) {
                    CircularProgressIndicator(color = BrandOrange)
                } else {
                    Text(
                        editViewModel.loadError ?: "",
                        color = BrandDeepOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(pad).padding(horizontal = 20.dp)) {
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
                if (!isEditMode) {
                    ModeSegmented(mode = mode, onChange = { mode = it })
                    Spacer(Modifier.height(20.dp))
                } else {
                    Spacer(Modifier.height(8.dp))
                }

                when (mode) {
                    EditMode.CHAT -> Box(Modifier.weight(1f)) {
                        ChatEdit(
                            history = chatHistory,
                            input = chatInput,
                            onInputChange = { chatInput = it },
                            done = chatDone,
                            aiTyping = aiTyping,
                            role = role,
                            action = action,
                            result = result,
                            title = title,
                            onTitle = { title = it },
                            category = category,
                            onCategory = { category = it },
                            onRole = { role = it },
                            onResult = { result = it },
                            onChipTap = { chatInput = it },
                            onSend = {
                                if (chatInput.isNotBlank() && !aiTyping && !chatDone) {
                                    sendChatTurn(chatInput)
                                }
                            },
                            isSaving = isSaving,
                            onSave = {
                                when {
                                    isSaving -> Unit
                                    title.isBlank() -> showTitleHint = true
                                    else -> {
                                        showTitleHint = false
                                        editViewModel.save(
                                            null,
                                            CreateExperienceRequest(
                                                title = title.trim(),
                                                category = category,
                                                period = timeRange.trim(),
                                                role = role.trim(),
                                                action = action.trim(),
                                                result = result.trim(),
                                                learning = learning.trim(),
                                            )
                                        )
                                    }
                                }
                            },
                        )
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

    if (pendingDelete && expId != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = false },
            title = { Text("刪除這筆經歷?") },
            text = { Text("「${title}」會從你的母版移除,這個動作無法復原。") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete = false
                    editViewModel.delete(expId)
                }) { Text("刪除", color = BrandDeepOrange) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = false }) { Text("取消", color = InkGray500) }
            },
        )
    }
}

private enum class EditMode { CHAT, FORM }

private const val openingQuestion =
    "最近做過什麼讓你印象深刻的事?可以是課程、社團、實習都好。"

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
    done: Boolean,
    aiTyping: Boolean,
    role: String,
    action: String,
    result: String,
    title: String,
    onTitle: (String) -> Unit,
    category: String,
    onCategory: (String) -> Unit,
    onRole: (String) -> Unit,
    onResult: (String) -> Unit,
    onChipTap: (String) -> Unit,
    onSend: () -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit,
) {
    val filledCount = listOf(role, action, result).count { it.isNotBlank() }
    val listState = rememberLazyListState()

    // The draft card (when done) is always the item right after the last history message.
    val cardIndex = history.size

    // Auto-scroll to the newest message as the conversation grows — like a chat app.
    LaunchedEffect(history.size, aiTyping, done) {
        if (!done) {
            val lastIndex = history.lastIndex + if (aiTyping) 1 else 0
            if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
        }
    }

    // Once the draft card appears, scroll so its TOP lands in view.
    LaunchedEffect(done) {
        if (done) listState.animateScrollToItem(cardIndex)
    }

    var showTitleHintInline by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier
                            .width(if (i < filledCount) 18.dp else 7.dp)
                            .height(7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i < filledCount) BrandOrange else InkGray200),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                if (!done) "已收集 $filledCount / 3 個重點" else "整理完成",
                color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))

        // Message list: takes all remaining space and scrolls internally.
        // This is the only part that should grow — input/send stays fixed below it.
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            itemsIndexed(history) { _, (speaker, msg) ->
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
            if (aiTyping) {
                item { ChatTypingDots() }
            }
            if (history.size == 1 && !aiTyping && !done) {
                item {
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
            }
            if (done) {
                item {
                    Spacer(Modifier.height(8.dp))
                    EditableDraftCard(
                        title = title, onTitle = onTitle,
                        category = category, onCategory = onCategory,
                        role = role, onRole = onRole,
                        result = result, onResult = onResult,
                    )
                    Spacer(Modifier.height(12.dp))
                    if (showTitleHintInline && title.isBlank()) {
                        Text("標題是必填的", color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                    }
                    PrimaryDarkButton(
                        text = if (isSaving) "儲存中..." else "儲存",
                        onClick = {
                            if (title.isBlank()) showTitleHintInline = true else onSave()
                        },
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        // Input row: fixed below the scrolling list, never pushed off-screen.
        if (!done) {
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().padding(bottom = 24.dp).navigationBarsPadding()) {
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
            } else OutlinedTextField(
                value = input, onValueChange = onInputChange,
                placeholder = { Text("用口語回答就好", color = InkGray400) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = !aiTyping,
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
        }
    }
}
@Composable
private fun EditableDraftCard(
    title: String, onTitle: (String) -> Unit,
    category: String, onCategory: (String) -> Unit,
    role: String, onRole: (String) -> Unit,
    result: String, onResult: (String) -> Unit,
) {
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
                Text("經驗卡 ・ 確認與微調", color = BrandDeepOrange,
                    fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.beaver_thumbsup),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )
        }
        Spacer(Modifier.height(12.dp))

        ExpField("標題", title, onTitle)
        ExpField("你的角色", role, onRole)
        ExpField("量化成果", result, onResult, multi = true)

        Text("類別", style = MaterialTheme.typography.labelLarge,
            color = InkGray700, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("學業", "工作", "社團", "競賽", "其他").forEach { c ->
                val on = category == c
                Box(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(if (on) InkBlack else InkGray100)
                        .pressScale { onCategory(c) }
                        .padding(horizontal = 13.dp, vertical = 7.dp),
                ) {
                    Text(c, color = if (on) PaperWhite else InkGray700,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("內容已從對話帶入,之後隨時可在編輯模式補時間範圍和學到什麼。",
            color = InkGray400, fontSize = 10.sp, lineHeight = 14.sp)
    }
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
    val filled = listOf(title, category, timeRange, role, action, result, learning)
        .count { it.isNotBlank() }
    val prog by animateFloatAsState(
        targetValue = filled / 7f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "formProg",
    )
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(50))
                    .background(InkGray200),
            ) {
                Box(
                    Modifier.fillMaxWidth(prog.coerceAtLeast(0.02f)).fillMaxHeight()
                        .clip(RoundedCornerShape(50)).background(BrandOrange),
                )
            }
            Spacer(Modifier.width(10.dp))
            Text("$filled / 7", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))

        FormGroup("基本") {
            ExpField("標題", title, onTitle)
            Text("類別", style = MaterialTheme.typography.labelLarge,
                color = InkGray700, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("學業", "工作", "社團", "競賽", "其他").forEach { c ->
                    val on = category == c
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(if (on) InkBlack else InkGray100)
                            .pressScale { onCategory(c) }
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                    ) {
                        Text(c, color = if (on) PaperWhite else InkGray700,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            ExpField("時間範圍", timeRange, onTime)
        }
        FormGroup("過程") {
            ExpField("角色", role, onRole)
            ExpField("做了什麼", action, onAction, multi = true)
        }
        FormGroup("成果") {
            ExpField("結果", result, onResult, multi = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("補個數字", color = InkGray400, fontSize = 11.sp)
                Spacer(Modifier.width(8.dp))
                listOf("人數" to ",共 __ 人", "金額" to ",金額 __ 元", "百分比" to ",提升 __%").forEach { (lab, tpl) ->
                    Box(
                        Modifier.padding(end = 6.dp).clip(RoundedCornerShape(50))
                            .background(BrandPeach.copy(alpha = 0.5f))
                            .pressScale { onResult(result + tpl) }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(lab, color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            ExpField("學到什麼", learning, onLearning, multi = true)
        }
    }
}

@Composable
private fun FormGroup(eyebrow: String, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(bottom = 12.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(eyebrow, color = InkGray400, fontSize = 10.sp,
            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun ExpField(label: String, value: String, onChange: (String) -> Unit, multi: Boolean = false) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelLarge,
                color = InkGray700, fontWeight = FontWeight.SemiBold)
            if (value.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
            }
        }
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = !multi, minLines = if (multi) 3 else 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange, unfocusedBorderColor = InkGray200,
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