package com.careersandbox.app.ui.screens.interview

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.ChatMessage
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

// 各角色色票
private val ParticipantColors = mapOf(
    "主考官" to BrandOrange,
    "你" to BrandDeepOrange,
    "AI-強勢" to Color(0xFF7A8C5A),
    "AI-邏輯" to InkGray700,
    "AI-親切" to Color(0xFFD4A574),
    "AI-沉默" to InkGray400,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewLiveGroupScreen(navController: NavHostController) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(MockData.groupInterviewScript) } }
    var input by remember { mutableStateOf("") }
    var showObservation by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val currentSpeaker = messages.lastOrNull()?.speaker ?: "主考官"

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("團體面試 ・ 管理顧問",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text("個案討論 ・ 6 人組",
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
                            Text("09:42", style = MaterialTheme.typography.labelMedium,
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
            if (input.isNotBlank()) {
                messages.add(ChatMessage("u${messages.size}", "你", input, isUser = true))
                input = ""
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
                items(messages, key = { it.id }) { m -> GroupMessageBubble(m) }
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isCurrent) Modifier.padding(0.dp)
                            else Modifier.padding(0.dp)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(name.takeLast(1), color = PaperWhite,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
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
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(speakerColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(m.speaker.takeLast(1), color = PaperWhite,
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
            ActionButton("搶答", Icons.Outlined.Mic, Modifier.weight(1f)) {}
            ActionButton("舉手補充", Icons.Outlined.PanTool, Modifier.weight(1f)) {}
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
