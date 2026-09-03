package com.careersandbox.app.ui.screens.interview

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.InterviewSession
import com.careersandbox.app.data.mock.MockPanelDispatcher
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import com.careersandbox.app.data.repository.RemoteTranscribeRepository
import com.careersandbox.app.ui.components.RecordingMicButton
import com.careersandbox.app.ui.components.rememberInPageAudioRecorder

/* =====================================================================
   主管 panel 面試(場景式,真流程)
   - 場景:bg_scene_meeting(沿用沙盒跨部門會議場景)+ 暖黑暈影,與沙盒一致。
   - 三位主管以沙盒角色呈現(有表情變體 → 可依回答換臉):
       HR 主管 = Vivian / 技術主管 = 阿哲 / 用人主管 = Ken。
   - 多代理路由用既有 MockPanelDispatcher(數據→技術、團隊→HR、成果→用人、不會→HR 接住),
     真接 LangGraph dispatcher 時換掉 MockPanelDispatcher 即可,本頁不動。
   - 作答用頁內語音(SpeechRecognizer,需 RECORD_AUDIO 權限,不跳 Google 框)→ 逐字稿餵 dispatch 做語意路由
     (比舊 mock 語音傳空字串更準);可上傳作品;不打字。
   - 表情依回答啟發式換(報告評分目前為 mock;接後端評分後可改吃真分數)。
   - 結束 → 既有 INTERVIEW_REPORT(三維評分報告頁不動)。
   ===================================================================== */

private const val OPENING_Q =
    "我們先從你開始。可以用一分鐘介紹一下自己,以及為什麼想加入我們嗎?"

private data class PanelSeat(val who: String, val accent: Color)

private val seats = listOf(
    PanelSeat("HR 主管", BrandAmber),
    PanelSeat("技術主管", BrandOrange),
    PanelSeat("用人主管", BrandDeepOrange),
)

// 主管名 + 反應強度 → 沙盒立繪;d>0 正面、d<0 負面、0 中性
private fun faceFor(who: String, d: Int): Int = when (who) {
    "技術主管" -> if (d <= -1) R.drawable.colleague_akai_frustrated else R.drawable.colleague_akai_calm
    "用人主管" -> when { d >= 1 -> R.drawable.ken_happy; d <= -1 -> R.drawable.ken_concerned; else -> R.drawable.ken_neutral }
    else -> when { d >= 1 -> R.drawable.colleague_vivian_satisfied; d <= -1 -> R.drawable.colleague_vivian_displeased; else -> R.drawable.colleague_vivian }
}

private fun accentFor(who: String): Color = seats.firstOrNull { it.who == who }?.accent ?: BrandAmber

// 反應啟發式(報告接後端評分後可改吃真分數)
private fun deltaFor(answer: String): Int = when {
    answer.isBlank() -> 0
    answer.contains("不知道") || answer.contains("不確定") || answer.contains("沒想過") -> -1
    answer.length >= 24 -> 1
    answer.length >= 10 -> 0
    else -> -1
}
private fun reactionText(d: Int): String = when {
    d >= 1 -> "認可地點點頭"
    d <= -1 -> "等你再多說一點"
    else -> "若有所思地聽著"
}

@Composable
fun InterviewLivePanelScreen(navController: NavHostController) {
    var currentAsker by remember { mutableStateOf("HR 主管") }
    var question by remember { mutableStateOf(OPENING_Q) }
    var answer by remember { mutableStateOf("") }
    var reactingDelta by remember { mutableIntStateOf(0) }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var shared by remember { mutableStateOf(false) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { InterviewSession.reset(); while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    val role = InterviewConfig.customRole.ifBlank { "Junior PM" }

    fun submitAnswer(transcript: String) {
        if (transcript.isBlank() || answer.isNotBlank()) return
        answer = transcript
        InterviewSession.record(question, transcript)
        reactingDelta = deltaFor(transcript)
        val (nextWho, nextQ) = MockPanelDispatcher.dispatch(transcript, followUpIdx)
        scope.launch {
            delay(1700)
            currentAsker = nextWho
            question = nextQ
            answer = ""
            reactingDelta = 0
            followUpIdx += 1
        }
    }

    var isTranscribing by remember { mutableStateOf(false) }
    var pendingTranscript by remember { mutableStateOf<String?>(null) }
    val transcribeRepo = remember { RemoteTranscribeRepository() }
    val recorder = rememberInPageAudioRecorder(maxDurationMs = 120_000L) { file ->
        isTranscribing = true
        scope.launch {
            transcribeRepo.transcribe(file)
                .onSuccess { text -> pendingTranscript = text }
                .onFailure { /* 轉錄失敗：先讓使用者看到麥克風按鈕重新出現，可以再錄一次 */ }
            isTranscribing = false
            file.delete()
        }
    }
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) shared = true }

    Box(Modifier.fillMaxSize().background(Color(0xFF14100B))) {
        Image(
            painter = painterResource(R.drawable.bg_scene_meeting),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x59000000), 0.5f to Color(0xB3160F09), 1f to Color(0xF0140F0A),
                ),
            ),
        )

        Column(Modifier.fillMaxSize().padding(20.dp)) {
            // 頂列:返回 / 標題(吃 Config) / 計時 / 結束
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x22FFFFFF))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("主管 panel 面試 · $role", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "三位主管輪流提問 · ${InterviewConfig.round} · ${InterviewConfig.difficulty}難度",
                        color = Color(0x99FFFFFF), fontSize = 11.sp,
                    )
                }
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x22FFFFFF)).padding(horizontal = 10.dp, vertical = 5.dp),
                ) { Text(timerText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(BrandOrange)
                        .clickable { navController.navigate(Routes.INTERVIEW_REPORT) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) { Text("結束", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(14.dp))

            // 三位主管列(高亮目前發問者)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                seats.forEach { s ->
                    val on = s.who == currentAsker
                    Row(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(if (on) Color(0x33FFB627) else Color(0x14FFFFFF))
                            .border(if (on) 1.dp else 0.dp, if (on) s.accent else Color.Transparent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(faceFor(s.who, 0)),
                            contentDescription = s.who,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(26.dp).clip(CircleShape),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(s.who, color = if (on) s.accent else Color(0xB3FFFFFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 立繪:依回答換表情
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(faceFor(currentAsker, reactingDelta)),
                        contentDescription = currentAsker,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(176.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(accentFor(currentAsker)).padding(horizontal = 13.dp, vertical = 5.dp),
                    ) {
                        Text(
                            if (answer.isBlank()) "$currentAsker　發問中" else "$currentAsker（${reactionText(reactingDelta)}）",
                            color = Color(0xFF3A1505), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // 問題框 + 角色 pill
            Box {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xDB241B12))
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 14.dp),
                ) {
                    Text(question, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
                }
                Box(
                    Modifier.align(Alignment.TopStart).offset(x = 14.dp, y = (-11).dp)
                        .clip(RoundedCornerShape(999.dp)).background(accentFor(currentAsker)).padding(horizontal = 11.dp, vertical = 3.dp),
                ) { Text(currentAsker, color = Color(0xFF412402), fontSize = 11.sp, fontWeight = FontWeight.Black) }
            }

            if (answer.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFF7EE)).padding(13.dp),
                ) {
                    Text("你的回答（語音）", color = Color(0xFF9A6A3A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(answer, color = Color(0xFF1F2937), fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x14FFFFFF))
                        .clickable { fileLauncher.launch("*/*") }.padding(horizontal = 11.dp, vertical = 7.dp),
                ) { Text("上傳作品給主考官", color = Color(0xCCFFFFFF), fontSize = 11.sp) }
                if (shared) {
                    Spacer(Modifier.width(8.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x2910B981)).padding(horizontal = 10.dp, vertical = 7.dp),
                    ) { Text("作品已分享", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(Modifier.height(12.dp))
            val pending = pendingTranscript
            if (pending != null) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF7EE)).padding(14.dp),
                ) {
                    Text("確認這段回答內容：", color = Color(0xFF9A6A3A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(pending, color = Color(0xFF1F2937), fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(999.dp))
                                .background(Color(0x14000000))
                                .clickable { pendingTranscript = null }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) { Text("重新錄音", color = Color(0xFF6B5B4A), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(999.dp))
                                .background(BrandOrange)
                                .clickable {
                                    submitAnswer(pending)
                                    pendingTranscript = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) { Text("確認送出", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    if (isTranscribing) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = BrandOrange,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("轉錄中...", color = Color(0x99FFFFFF), fontSize = 11.sp)
                        }
                    } else {
                        RecordingMicButton(
                            isRecording = recorder.isRecording,
                            amplitude = recorder.amplitude,
                            elapsedMs = recorder.elapsedMs,
                            maxDurationMs = 120_000L,
                            idleEnabled = answer.isBlank(),
                            onClick = {
                                if (recorder.isRecording) recorder.stop()
                                else if (answer.isBlank()) recorder.start()
                            },
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text("等待其他小組成員發言", color = Color(0x73FFFFFF), fontSize = 11.sp)
                }
            }
        }
    }
}
