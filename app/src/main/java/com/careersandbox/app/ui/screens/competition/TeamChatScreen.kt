package com.careersandbox.app.ui.screens.competition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class ChatMsg(val text: String, val isMe: Boolean, val sender: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamChatScreen(navController: NavHostController) {
    val messages = remember {
        mutableStateListOf(
            ChatMsg("大家好,我是隊長!很高興大家組隊成功 🎉", isMe = true, sender = "我"),
            ChatMsg("哈囉!請多指教,我負責資料分析的部分", isMe = false, sender = "陳柏宇"),
            ChatMsg("我可以幫忙做簡報跟視覺,有需要隨時喊我", isMe = false, sender = "林子晴"),
        )
    }
    var input by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PaperWarm,
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = InkBlack)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("隊伍聊天室", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        Text("3 位成員 · 線上", color = AccentGreen, fontSize = 12.sp)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(PaperWhite).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("輸入訊息…") },
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions.Default,
                    maxLines = 3,
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(BrandOrange)
                        .pressScale {
                            if (input.isNotBlank()) {
                                messages.add(ChatMsg(input.trim(), isMe = true, sender = "我"))
                                input = ""
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = "送出", tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            items(messages) { msg -> ChatBubble(msg) }
            item {
                Spacer(Modifier.height(4.dp))
                Text("※ 這是展示用的隊伍聊天室,訊息不會真的送出",
                    color = InkGray400, fontSize = 11.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMsg) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start,
    ) {
        if (!msg.isMe) {
            Text(msg.sender, color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp, bottom = 2.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (msg.isMe) 16.dp else 4.dp,
                    bottomEnd = if (msg.isMe) 4.dp else 16.dp,
                ))
                .background(if (msg.isMe) BrandOrange else PaperWhite)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(msg.text, color = if (msg.isMe) PaperWhite else InkBlack, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
