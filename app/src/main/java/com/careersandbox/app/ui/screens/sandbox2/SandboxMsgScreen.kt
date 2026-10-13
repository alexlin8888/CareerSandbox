package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandAmber
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm
import kotlinx.coroutines.launch

private val LineColor = Color(0xFFEADFD6)
private val HeaderH = 52.dp
private val BannerH = 56.dp
private val TaskCardColor = BrandPeach.copy(alpha = 0.6f).compositeOver(PaperWarm)

@Composable
fun SandboxMsgScreen(navController: NavHostController) {
    val state = SandboxV2State

    DisposableEffect(Unit) {
        state.onMsgScreenOpened()
        onDispose { state.onMsgScreenClosed() }
    }

    val remaining = state.questions.filter { it.key !in state.asked }
    val canAsk = state.optionsReady && !state.busy &&
            state.lines.isNotEmpty() && remaining.isNotEmpty()
    val listState = rememberLazyListState()

    // Keep the newest content in view once the chat grows past the screen.
    LaunchedEffect(state.lines.size, state.typing, canAsk) {
        val count = listState.layoutInfo.totalItemsCount
        if (count > 0) listState.animateScrollToItem(count - 1)
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(PaperWarm)) {
        val fullHeight = maxHeight

        Column(Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().height(HeaderH).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "‹ 工作台", color = InkGray500, fontSize = 13.sp,
                    modifier = Modifier.pressScale { navController.popBackStack() }.padding(end = 12.dp, top = 4.dp, bottom = 4.dp),
                )
                Text("訊息", color = InkSlate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Task banner, shown after the task card has settled
            if (state.optionsReady || state.askClosed) {
                val bannerTitle: String
                val bannerText: String
                if (!state.askClosed) {
                    bannerTitle = "現在要做的事"
                    bannerText = "弄清楚小昀要什麼。想問什麼就問。"
                } else if (state.inMeeting) {
                    bannerTitle = "小昀 會議中"
                    bannerText = "有事可以留訊息，她晚點回"
                } else {
                    bannerTitle = "小昀 在位子上"
                    bannerText = "有事現在問，她等一下要進會議"
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(BannerH)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TaskCardColor)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(bannerTitle, color = InkGray500, fontSize = 11.sp)
                    Text(bannerText, color = InkSlate, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }

            // Chat, top-down. The options card is the last item and scrolls into view.
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            ) {
                itemsIndexed(state.lines, key = { _, line -> line.id }) { index, line ->
                    val older = state.lines.getOrNull(index - 1)
                    val showHead = older?.who != line.who
                    if (line.who == Who.ME) MyBubble(line.text, showHead) else PmBubble(line.text, showHead)
                }
                item(key = "typing") {
                    if (state.typing) {
                        Text("小昀 輸入中…", color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(start = 42.dp, top = 8.dp))
                    }
                }
                item(key = "options") {
                    if (canAsk) OptionsCard(remaining)
                }
            }
        }

        // Full-screen task card that collapses into the banner slot
        if (state.introCardVisible && !state.optionsReady) {
            TaskIntroCard(fullHeight = fullHeight, onSettled = { state.confirmTask() })
        }
    }
}

@Composable
private fun TaskIntroCard(fullHeight: androidx.compose.ui.unit.Dp, onSettled: () -> Unit) {
    val progress = remember { Animatable(0f) }
    var started by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val p = progress.value

    Box(
        modifier = Modifier
            .padding(top = HeaderH * p, start = 16.dp * p, end = 16.dp * p)
            .fillMaxWidth()
            .height(fullHeight - (fullHeight - HeaderH - BannerH + HeaderH) * p)
            .clip(RoundedCornerShape(10.dp * p))
            .background(TaskCardColor),
    ) {
        // Large content, fades out during the first half of the motion
        Column(
            modifier = Modifier.align(Alignment.Center).alpha((1f - p * 2f).coerceIn(0f, 1f)).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("現在要做的事", color = InkSlate, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("弄清楚小昀要什麼", color = InkGray500, fontSize = 14.sp, modifier = Modifier.padding(top = 10.dp))
            Box(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BrandOrange)
                    .pressScale {
                        if (!started) {
                            started = true
                            scope.launch {
                                progress.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
                                onSettled()
                            }
                        }
                    }
                    .padding(horizontal = 36.dp, vertical = 12.dp),
            ) { Text("知道了", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        }
        // Banner content, fades in during the second half
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(BannerH)
                .alpha(((p - 0.5f) * 2f).coerceIn(0f, 1f))
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("現在要做的事", color = InkGray500, fontSize = 11.sp)
            Text("弄清楚小昀要什麼。想問什麼就問。", color = InkSlate, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun OptionsCard(remaining: List<PmQuestion>) {
    val state = SandboxV2State
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, LineColor, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(
            if (state.askClosed) "想到還有事要問小昀" else "回小昀",
            color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp),
        )
        remaining.forEach { q ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PaperWarm)
                    .border(1.dp, LineColor, RoundedCornerShape(10.dp))
                    .pressScale { state.ask(q.key) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(q.text, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp)
            }
        }
        if (!state.askClosed) {
            Text(
                if (state.askCount == 0) "不問了，先看資料" else "先這樣，去看資料",
                color = BrandOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .pressScale { state.skipAsking() }
                    .padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun PmBubble(text: String, showHead: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = if (showHead) 12.dp else 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (showHead) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(BrandAmber),
                contentAlignment = Alignment.Center,
            ) { Text("昀", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        } else {
            Spacer(Modifier.width(34.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.widthIn(max = 270.dp)) {
            if (showHead) {
                Text("小昀 · 產品經理", color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
            val shape = RoundedCornerShape(4.dp, 14.dp, 14.dp, 14.dp)
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(Color.White)
                    .border(1.dp, LineColor, shape)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) { Text(text, color = InkSlate, fontSize = 14.sp, lineHeight = 21.sp) }
        }
    }
}

@Composable
private fun MyBubble(text: String, showHead: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = if (showHead) 12.dp else 4.dp),
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