package com.careersandbox.app.ui.screens.sandbox2

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.PaperWarm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val EveningBg = Color(0xFF151A24)
private val EveningCard = Color(0xFF222836)
private val EveningLine = Color(0xFF343B4D)
private val EveningOption = Color(0xFF2B3243)
private val EveningText = Color(0xFFE8E4DE)
private val EveningMuted = Color(0xFF8A93A6)
private val FriendColor = Color(0xFF7C9A92)

val MoodOptions = listOf(
    "被 PM 一直逼 很煩",
    "一直在翻資料 有點無聊",
    "還行 寫報告那段滿順的",
    "其實蠻有趣的",
)

val HardestOptions = listOf(
    "一早搞不清楚小昀要什麼",
    "看資料表那段",
    "做到一半需求變了",
    "寫摘要那段",
    "被追問的時候",
)

/** 18:00. The screen dims and 阿哲 asks how the day went. Two taps, then the result screen. */
@Composable
fun SandboxEveningScreen(navController: NavHostController) {
    val state = SandboxV2State
    val scope = rememberCoroutineScope()
    var dark by remember { mutableStateOf(false) }
    val bg by animateColorAsState(if (dark) EveningBg else PaperWarm, animationSpec = tween(1400))
    val chat = remember { mutableStateListOf<Pair<Boolean, String>>() }   // true = me
    var step by remember { mutableStateOf(0) }   // 1 mood options, 3 hardest options, 4 done
    val listState = rememberLazyListState()

    // The day is over, there is no way back into the office
    BackHandler { }

    LaunchedEffect(Unit) {
        dark = true
        delay(1800)
        chat.add(false to "今天怎樣")
        step = 1
    }
    LaunchedEffect(chat.size) {
        if (chat.isNotEmpty()) listState.animateScrollToItem(chat.lastIndex)
    }

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.size(28.dp))
            Text(
                "18:00", color = EveningMuted, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.size(12.dp))

            LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
                itemsIndexed(chat) { i, (mine, text) ->
                    val showHead = !mine && (i == 0 || chat[i - 1].first)
                    if (mine) EveningMine(text) else EveningFriend(text, showHead)
                }
            }

            when (step) {
                1 -> EveningChoices("回阿哲", MoodOptions) { k ->
                    step = 2
                    state.setEveningMood(k)
                    chat.add(true to MoodOptions[k])
                    scope.launch {
                        delay(1000)
                        chat.add(false to "哪段最煩")
                        step = 3
                    }
                }
                3 -> EveningChoices("回阿哲", HardestOptions) { k ->
                    step = 4
                    state.setEveningHardest(k)
                    chat.add(true to HardestOptions[k])
                }
                4 -> Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(BrandOrange)
                        .pressScale {
                            navController.navigate(Routes.SANDBOX_V2_RESULT) {
                                popUpTo(Routes.SANDBOX_V2_EVENING) { inclusive = true }
                            }
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("看這一天的紀錄", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                else -> Spacer(Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun EveningChoices(title: String, options: List<String>, onPick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EveningCard)
            .border(1.dp, EveningLine, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(title, color = EveningMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        options.forEachIndexed { i, text ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EveningOption)
                    .border(1.dp, EveningLine, RoundedCornerShape(10.dp))
                    .pressScale { onPick(i) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) { Text(text, color = EveningText, fontSize = 13.sp, lineHeight = 20.sp) }
        }
    }
}

@Composable
private fun EveningFriend(text: String, showHead: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = if (showHead) 12.dp else 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (showHead) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(FriendColor),
                contentAlignment = Alignment.Center,
            ) { Text("哲", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        } else {
            Spacer(Modifier.width(34.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.widthIn(max = 270.dp)) {
            if (showHead) {
                Text("阿哲", color = EveningMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
            val shape = RoundedCornerShape(4.dp, 14.dp, 14.dp, 14.dp)
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(EveningOption)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) { Text(text, color = EveningText, fontSize = 14.sp, lineHeight = 21.sp) }
        }
    }
}

@Composable
private fun EveningMine(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        val shape = RoundedCornerShape(14.dp, 4.dp, 14.dp, 14.dp)
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(BrandOrange)
                .padding(horizontal = 12.dp, vertical = 9.dp),
        ) { Text(text, color = Color.White, fontSize = 14.sp, lineHeight = 21.sp) }
    }
}