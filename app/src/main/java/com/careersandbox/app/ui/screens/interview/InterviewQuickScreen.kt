package com.careersandbox.app.ui.screens.interview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.VoiceBar
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

/* =====================================================================
   快速面試 —— 低門檻入口,與正式 mock 區分
   抽一題 → 60 秒倒數環 → 答完三句回饋 → 馬上再來一題
   ===================================================================== */

private data class QuickQ(val q: String, val tag: String, val tips: List<String>)

private val quickBank = listOf(
    QuickQ("用一句話介紹你自己,讓我記得住。", "破冰",
        listOf("先給標籤(你是誰),再給一個具體成果。", "別背履歷,挑一個最亮的點。", "30 字內收尾,留鉤子。")),
    QuickQ("講一個你解決過的難題,結果如何?", "behavioral",
        listOf("用情境→行動→結果的順序。", "結果盡量有數字。", "重點放你做了什麼,不是團隊。")),
    QuickQ("你為什麼想離開現在的位置/學校階段?", "動機",
        listOf("講你要去哪,不是你在逃什麼。", "不要批評前東家或同學。", "連到這個職位的成長。")),
    QuickQ("你最大的弱點是什麼?", "陷阱",
        listOf("選真的弱點,不要假謙虛。", "重點是你怎麼補它。", "給一個正在改善的具體行動。")),
    QuickQ("為什麼我們應該選你,而不是別人?", "收尾",
        listOf("挑一個你獨有的組合。", "對齊這個職位的需求。", "自信但不浮誇。")),
    QuickQ("講一次你失敗的經驗,你學到什麼?", "behavioral",
        listOf("選真的失敗,不要包裝成成功。", "重點在學到什麼、後來怎麼改。", "別怪別人。")),
    QuickQ("如果同事不配合你,你會怎麼處理?", "情境",
        listOf("先理解對方立場,再找共識。", "舉一個你真的做過的例子。", "展現溝通而非對抗。")),
    QuickQ("你期待這份工作帶給你什麼?", "動機",
        listOf("講成長與貢獻,不只是薪水。", "對齊這個職位能給的。", "具體一點,別空話。")),
)

@Composable
fun InterviewQuickScreen(navController: NavHostController) {
    var qIndex by remember { mutableIntStateOf(quickBank.indices.random()) }
    val asked = remember { mutableStateListOf<Int>() }
    var phase by remember { mutableStateOf("ANSWER") }    // ANSWER / FEEDBACK
    var answer by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(60) }
    var voiceMode by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var recordSec by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    val current = quickBank[qIndex]

    // 60 秒倒數(只在作答階段跑)
    LaunchedEffect(qIndex, phase) {
        if (phase == "ANSWER") {
            secondsLeft = 60
            while (secondsLeft > 0 && phase == "ANSWER") {
                delay(1000); secondsLeft--
            }
            if (phase == "ANSWER") phase = "FEEDBACK"
        }
    }
    LaunchedEffect(recording) {
        recordSec = 0
        while (recording) { delay(1000); recordSec++ }
    }

    fun submit() {
        if (phase == "ANSWER") { streak++; phase = "FEEDBACK" }
    }
    fun next() {
        asked.add(qIndex)
        val pool = quickBank.indices.filter { it != qIndex }
        qIndex = (pool.filter { it !in asked }.ifEmpty { pool }).random()
        answer = ""; voiceMode = false; recording = false
        phase = "ANSWER"
    }

    Box(Modifier.fillMaxSize().background(InkCharcoal)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            // 標頭
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(38.dp).clip(CircleShape)
                        .background(PaperWhite.copy(alpha = 0.1f))
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Outlined.Bolt, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("快速面試", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                if (streak > 0) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(BrandOrange.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("連續 $streak 題", color = BrandAmber, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // 倒數環 + 秒數
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CountdownRing(secondsLeft = secondsLeft, total = 60, active = phase == "ANSWER")
            }
            Spacer(Modifier.height(24.dp))

            // 題目卡
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(PaperWhite).padding(20.dp),
            ) {
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandPeach.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(current.tag, color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(10.dp))
                Text(current.q, color = InkBlack, fontSize = 20.sp,
                    fontWeight = FontWeight.Black, lineHeight = 28.sp)
            }
            Spacer(Modifier.height(16.dp))

            if (phase == "ANSWER") {
                Spacer(Modifier.weight(1f))
                if (voiceMode) {
                    VoiceBar(
                        recording = recording,
                        recordSec = recordSec,
                        onKeyboard = { voiceMode = false; recording = false },
                        onPressStart = { recording = true },
                        onPressEnd = {
                            val sec = recordSec; recording = false
                            if (sec > 0) { answer = "(語音回答・$sec 秒)"; submit() }
                        },
                    )
                } else {
                    OutlinedTextField(
                        value = answer, onValueChange = { answer = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        placeholder = { Text("講重點就好,60 秒內。", color = InkGray400) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PaperWhite,
                            unfocusedContainerColor = PaperWhite,
                            focusedBorderColor = BrandOrange, unfocusedBorderColor = InkGray200,
                        ),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(14.dp))
                                .background(PaperWhite.copy(alpha = 0.1f))
                                .pressScale { voiceMode = true },
                            contentAlignment = Alignment.Center,
                        ) { Text("改用語音", color = PaperWhite, fontWeight = FontWeight.Bold) }
                        Box(
                            Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(14.dp))
                                .background(BrandOrange).pressScale { submit() },
                            contentAlignment = Alignment.Center,
                        ) { Text("答完了", color = PaperWhite, fontWeight = FontWeight.Black) }
                    }
                }
                Spacer(Modifier.height(20.dp))
            } else {
                // FEEDBACK
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(350)) { it / 4 },
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                                .background(PaperWhite.copy(alpha = 0.08f)).padding(16.dp),
                        ) {
                            Text("三個提點", color = BrandAmber, fontSize = 11.sp,
                                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Spacer(Modifier.height(10.dp))
                            current.tips.forEachIndexed { i, tip ->
                                Row(Modifier.padding(vertical = 5.dp)) {
                                    Text("${i + 1}", color = BrandOrange,
                                        fontWeight = FontWeight.Black, fontSize = 13.sp,
                                        modifier = Modifier.width(20.dp))
                                    Text(tip, color = PaperWhite.copy(alpha = 0.85f),
                                        fontSize = 13.sp, lineHeight = 19.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("提點是通用方向,正式 mock 會針對你的回答內容給回饋。",
                            color = PaperWhite.copy(alpha = 0.4f), fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(16.dp))
                            .background(PaperWhite.copy(alpha = 0.1f))
                            .pressScale { navController.popBackStack() },
                        contentAlignment = Alignment.Center,
                    ) { Text("結束", color = PaperWhite, fontWeight = FontWeight.Bold) }
                    Box(
                        Modifier.weight(2f).height(52.dp).clip(RoundedCornerShape(16.dp))
                            .background(BrandOrange).pressScale { next() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Bolt, contentDescription = null,
                                tint = PaperWhite, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("再來一題", color = PaperWhite, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CountdownRing(secondsLeft: Int, total: Int, active: Boolean) {
    val progress = secondsLeft.toFloat() / total
    val hot = secondsLeft <= 10
    val pulse = if (hot && active) {
        val t = rememberInfiniteTransition(label = "ringPulse")
        val v by t.animateFloat(1f, 1.06f,
            infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "rp")
        v
    } else 1f
    val ringColor = if (hot) BrandDeepOrange else BrandOrange
    Box(contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size((118 * pulse).dp),
        ) {
            val sw = 10.dp.toPx()
            drawArc(
                color = PaperWhite.copy(alpha = 0.1f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                style = Stroke(width = sw, cap = StrokeCap.Round),
            )
            drawArc(
                color = ringColor,
                startAngle = -90f, sweepAngle = -360f * progress, useCenter = false,
                style = Stroke(width = sw, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$secondsLeft", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 40.sp)
            Text("秒", color = PaperWhite.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}
