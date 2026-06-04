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
    val chatHistory = remember {
        mutableStateListOf("AI" to "最近做過什麼讓你印象深刻的事?可以是課程、社團、實習都好。")
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
                EditMode.CHAT -> ChatEdit(chatHistory, chatInput, { chatInput = it }) {
                    if (chatInput.isNotBlank()) {
                        chatHistory.add("你" to chatInput)
                        chatInput = ""
                        chatHistory.add("AI" to "了解。可以再多說一點時間和規模嗎?例如做了多久、有多少人參與?")
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
    history: List<Pair<String, String>>, input: String,
    onInputChange: (String) -> Unit, onSend: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().heightIn(max = 460.dp)
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
    }
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = input, onValueChange = onInputChange,
        placeholder = { Text("用口語回答就好", color = InkGray400) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        trailingIcon = {
            IconButton(onClick = onSend) {
                Icon(Icons.Outlined.Send, contentDescription = null, tint = BrandOrange)
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
