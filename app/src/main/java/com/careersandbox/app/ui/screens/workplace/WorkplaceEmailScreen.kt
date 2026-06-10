package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MailAction { HANDLE, DEFER, DELEGATE }
private enum class StormPhase { INTRO, PLAYING, DEBRIEF }

private data class MailCard(
    val id: String,
    val sender: String,
    val senderRole: String,
    val subject: String,
    val preview: String,
    val urgentTag: String?,   // 視覺上的「急」標(可能是假緊急)
)

// 牌序經過設計:致命的那封(財務)夾在兩封花俏的中間
private val mailDeck = listOf(
    MailCard("m_boss", "Ken", "你的主管", "15:00 前給我:匯出功能的新時程",
        "延期之後的排程今天要對齊,我下午要往上報。", "急"),
    MailCard("m_fridge", "總務處", "全員公告", "【急】茶水間冰箱大掃除",
        "週五前未標名的食物將一律丟棄,請同仁盡速處理。", "急"),
    MailCard("m_finance", "王小姐", "財務部", "報帳憑證補件通知",
        "您 5 月份報帳缺兩張憑證,今日 17:00 截止,逾期本季不再受理。", null),
    MailCard("m_vendor", "DataPipe", "廠商", "方案優惠倒數,僅此一檔",
        "升級年約現省 30%,名額有限,點此預約專人介紹。", "最後機會"),
    MailCard("m_meeting", "婷婷", "PM", "站立會議改 10:30",
        "會議室換到 B,請回覆收到。", null),
    MailCard("m_api", "林經理", "客戶", "API 文件連結還有嗎",
        "之前那份串接文件找不到了,方便再給一次嗎。", null),
    MailCard("m_lunch", "阿哲", "同事", "中午要不要一起訂便當",
        "11:30 截單,今天有新的那家滷肉飯。", null),
    MailCard("m_report", "數據組", "內部", "上週漏斗報表",
        "轉換率掉了 2 個百分點,細節見附件。", null),
    MailCard("m_hr", "人資處", "HR", "年度教育訓練問卷",
        "約 5 分鐘,本月底前完成即可。", null),
    MailCard("m_pwd", "資安部", "系統通知", "密碼到期預告",
        "您的密碼將於下週三到期,屆時請更新。", null),
    MailCard("m_ref", "怡君", "前同事", "推薦信再麻煩你了",
        "不急,月底前都可以,先謝謝你。", null),
    MailCard("m_news", "產品週報", "訂閱", "本週產品圈動態",
        "12 則精選,3 分鐘看完。", null),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkplaceEmailScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf(StormPhase.INTRO) }
    var index by remember { mutableIntStateOf(0) }
    val decisions = remember { mutableStateMapOf<String, MailAction>() }
    var remaining by remember { mutableIntStateOf(90) }

    LaunchedEffect(phase) {
        if (phase == StormPhase.PLAYING) {
            while (remaining > 0 && phase == StormPhase.PLAYING) {
                delay(1000)
                remaining--
            }
            if (phase == StormPhase.PLAYING) phase = StormPhase.DEBRIEF
        }
    }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Email 風暴日",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = InkBlack)
                        Text("職場沙盒 ・ 模擬場景",
                            style = MaterialTheme.typography.labelSmall, color = InkGray500)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    if (phase == StormPhase.PLAYING) {
                        val low = remaining <= 15
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (low) AccentRed else InkBlack)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Timer, contentDescription = null,
                                    tint = PaperWhite, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${(remaining / 60)}:${(remaining % 60).toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PaperWhite, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (phase) {
                StormPhase.INTRO -> StormIntro { phase = StormPhase.PLAYING }
                StormPhase.PLAYING -> StormPlaying(
                    index = index,
                    onDecide = { action ->
                        decisions[mailDeck[index].id] = action
                        index++
                        if (index >= mailDeck.size) phase = StormPhase.DEBRIEF
                    },
                )
                StormPhase.DEBRIEF -> StormDebrief(
                    decisions = decisions,
                    secondsUsed = 90 - remaining,
                    onRetry = {
                        decisions.clear(); index = 0; remaining = 90
                        phase = StormPhase.INTRO
                    },
                    onExit = { navController.popBackStack() },
                )
            }
        }
    }
}

/* ===================== 階段一:場景說明 ===================== */

@Composable
private fun StormIntro(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.beaver_writing),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(130.dp),
        )
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(InkCharcoal)
                .padding(20.dp),
        ) {
            Text("場景",
                color = PaperWhite.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(6.dp))
            Text("週三 09:00 ・ 你的信箱",
                color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text("12 封未讀。90 秒後要進站立會議。有的信看起來很急,有的信安靜地致命。",
                color = PaperWhite.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ActionHint("← 擱置", InkGray400)
                Spacer(Modifier.width(10.dp))
                ActionHint("↑ 轉交", AccentBlue)
                Spacer(Modifier.width(10.dp))
                ActionHint("處理 →", AccentGreen)
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(InkBlack)
                .pressScale(onClick = onStart),
            contentAlignment = Alignment.Center,
        ) {
            Text("開信箱", color = PaperWhite, fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ActionHint(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

/* ===================== 階段二:滑卡 ===================== */

@Composable
private fun StormPlaying(index: Int, onDecide: (MailAction) -> Unit) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var animating by remember { mutableStateOf(false) }

    fun fling(action: MailAction) {
        if (animating) return
        animating = true
        scope.launch {
            val tx = when (action) {
                MailAction.HANDLE -> 1600f
                MailAction.DEFER -> -1600f
                MailAction.DELEGATE -> offsetX.value
            }
            val ty = if (action == MailAction.DELEGATE) -2000f else offsetY.value
            launch { offsetX.animateTo(tx, tween(220)) }
            launch { offsetY.animateTo(ty, tween(220)) }
            delay(240)
            onDecide(action)
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            animating = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        Text("第 ${index + 1} / ${mailDeck.size} 封",
            color = InkGray400, style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            // 下一張(墊底)
            if (index + 1 < mailDeck.size) {
                MailCardView(mailDeck[index + 1], modifier = Modifier.scale(0.94f).alpha(0.6f))
            }
            // 當前這張(可拖)
            if (index < mailDeck.size) {
                val rotation = (offsetX.value / 60f).coerceIn(-12f, 12f)
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = offsetX.value
                            translationY = offsetY.value
                            rotationZ = rotation
                        }
                        .pointerInput(index) {
                            detectDragGestures(
                                onDrag = { change, drag ->
                                    change.consume()
                                    scope.launch {
                                        offsetX.snapTo(offsetX.value + drag.x)
                                        offsetY.snapTo(offsetY.value + drag.y)
                                    }
                                },
                                onDragEnd = {
                                    val x = offsetX.value
                                    val y = offsetY.value
                                    when {
                                        y < -260f -> fling(MailAction.DELEGATE)
                                        x > 260f -> fling(MailAction.HANDLE)
                                        x < -260f -> fling(MailAction.DEFER)
                                        else -> scope.launch {
                                            launch { offsetX.animateTo(0f, spring()) }
                                            launch { offsetY.animateTo(0f, spring()) }
                                        }
                                    }
                                },
                            )
                        },
                ) {
                    MailCardView(mailDeck[index])
                    // 拖曳時的判定章
                    val handleA = ((offsetX.value - 60f) / 180f).coerceIn(0f, 1f)
                    val deferA = ((-offsetX.value - 60f) / 180f).coerceIn(0f, 1f)
                    val delegateA = ((-offsetY.value - 60f) / 180f).coerceIn(0f, 1f)
                    StampLabel("處理", AccentGreen, handleA,
                        Modifier.align(Alignment.TopStart).padding(16.dp))
                    StampLabel("擱置", InkGray500, deferA,
                        Modifier.align(Alignment.TopEnd).padding(16.dp))
                    StampLabel("轉交", AccentBlue, delegateA,
                        Modifier.align(Alignment.BottomCenter).padding(16.dp))
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        // 按鈕保底(滑卡之外的等價操作)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButtonPill("擱置", InkGray500, filled = false, Modifier.weight(1f)) { fling(MailAction.DEFER) }
            ActionButtonPill("轉交", AccentBlue, filled = false, Modifier.weight(1f)) { fling(MailAction.DELEGATE) }
            ActionButtonPill("處理", InkBlack, filled = true, Modifier.weight(1f)) { fling(MailAction.HANDLE) }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun MailCardView(mail: MailCard, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(PaperWhite)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(BrandPeach),
                contentAlignment = Alignment.Center,
            ) {
                Text(mail.sender.take(1),
                    color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(mail.sender, color = InkBlack,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(mail.senderRole, color = InkGray400, fontSize = 11.sp)
            }
            if (mail.urgentTag != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AccentRed.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(mail.urgentTag, color = AccentRed,
                        fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(mail.subject, color = InkBlack,
            fontWeight = FontWeight.Black, fontSize = 17.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(8.dp))
        Text(mail.preview, color = InkGray500, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(18.dp))
        Text("← 擱置 ・ ↑ 轉交 ・ 處理 →",
            color = InkGray300, fontSize = 11.sp)
    }
}

@Composable
private fun StampLabel(text: String, color: Color, a: Float, modifier: Modifier = Modifier) {
    if (a > 0f) {
        Box(
            modifier = modifier
                .alpha(a)
                .clip(RoundedCornerShape(10.dp))
                .background(color)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(text, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ActionButtonPill(
    text: String,
    color: Color,
    filled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) color else color.copy(alpha = 0.12f))
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text,
            color = if (filled) PaperWhite else color,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.labelLarge)
    }
}

/* ===================== 階段三:誠實回顧 ===================== */

@Composable
private fun StormDebrief(
    decisions: Map<String, MailAction>,
    secondsUsed: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    val handled = decisions.count { it.value == MailAction.HANDLE }
    val deferred = decisions.count { it.value == MailAction.DEFER }
    val delegated = decisions.count { it.value == MailAction.DELEGATE }
    val missed = mailDeck.size - decisions.size

    val financeAction = decisions["m_finance"]
    val financeLine = when (financeAction) {
        MailAction.HANDLE -> "最不起眼的那封——財務 17:00 截止——你接住了。它沒有掛「急」,但它才是今天的地雷。"
        MailAction.DEFER -> "最不起眼的那封——財務 17:00 截止——你擱置了。今天 17:01,你會接到電話。"
        MailAction.DELEGATE -> "財務補件那封你轉走了。憑證在你手上,別人補不了。17:01 電話還是會找你。"
        null -> "財務 17:00 截止那封,你根本沒拆到。它安靜地躺在第三封。"
    }
    val fakeBites = listOf("m_fridge", "m_vendor").count { decisions[it] == MailAction.HANDLE }
    val fakeLine = if (fakeBites > 0)
        "$fakeBites 封掛著「急」的信騙到了你的時間。緊急和重要,是兩件事。"
    else
        "兩封假緊急都沒騙到你。掛紅標的不一定重要,你看穿了。"
    val apiAction = decisions["m_api"]
    val apiLine = when (apiAction) {
        MailAction.DELEGATE -> "API 文件那封你轉給了更熟的人——對。不是每件事都該自己跳下去。"
        MailAction.HANDLE -> "API 文件那封你自己回了。能,但你的 90 秒,本來可以花在財務那封上。"
        else -> "API 文件那封被你放著。客戶在等,這種信轉交比擱置好。"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InkCharcoal)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(28.dp))
        Text("收信結束",
            color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        Text("你用了 $secondsUsed 秒,拆了 ${decisions.size} / ${mailDeck.size} 封。",
            color = PaperWhite.copy(alpha = 0.7f), fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            DebriefStat("$handled", "處理", AccentGreen)
            DebriefStat("$deferred", "擱置", InkGray400)
            DebriefStat("$delegated", "轉交", AccentBlue)
            DebriefStat("$missed", "沒拆", AccentRed)
        }

        Spacer(Modifier.height(24.dp))
        DebriefCard("那封安靜的地雷", financeLine,
            good = financeAction == MailAction.HANDLE)
        Spacer(Modifier.height(10.dp))
        DebriefCard("假緊急", fakeLine, good = fakeBites == 0)
        Spacer(Modifier.height(10.dp))
        DebriefCard("轉交的判斷", apiLine,
            good = apiAction == MailAction.DELEGATE)

        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.beaver_calm),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text("真實的週三也是這樣:拆不完,只能選。",
            color = PaperWhite.copy(alpha = 0.6f), fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PaperWhite.copy(alpha = 0.1f))
                    .pressScale(onClick = onRetry),
                contentAlignment = Alignment.Center,
            ) {
                Text("再來一次", color = PaperWhite, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandOrange)
                    .pressScale(onClick = onExit),
                contentAlignment = Alignment.Center,
            ) {
                Text("回沙盒", color = PaperWhite, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun DebriefStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Spacer(Modifier.height(2.dp))
        Text(label, color = PaperWhite.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DebriefCard(title: String, body: String, good: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PaperWhite.copy(alpha = 0.07f))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(8.dp).clip(CircleShape)
                    .background(if (good) AccentGreen else BrandAmber),
            )
            Spacer(Modifier.width(8.dp))
            Text(title, color = PaperWhite,
                fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(body, color = PaperWhite.copy(alpha = 0.85f),
            fontSize = 13.sp, lineHeight = 20.sp)
    }
}
