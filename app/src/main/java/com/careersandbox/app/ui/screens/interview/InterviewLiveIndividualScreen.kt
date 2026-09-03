package com.careersandbox.app.ui.screens.interview

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.careersandbox.app.data.mock.MockInterviewProber
import com.careersandbox.app.data.mock.InterviewSession
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import com.careersandbox.app.data.repository.RemoteTranscribeRepository
import com.careersandbox.app.ui.components.RecordingMicButton
import com.careersandbox.app.ui.components.rememberInPageAudioRecorder

/* =====================================================================
   1 對 1 面試(場景式,真流程)
   - 場景:bg_scene_1on1(沿用沙盒一對一場景)+ 暖黑暈影,與沙盒一致。
   - 面試官以沙盒角色 Ken 呈現(有表情變體 → 依回答換臉)。
   - 階段機保留:MAIN(Prober 追問,第 2 次「請重講」,第 4 次轉)→ REVERSE(你點選反問)
       → CLOSING(面試官回答+結語)→ DONE → INTERVIEW_REPORT。
   - 探問池依 InterviewConfig.type / language 分流;Prober 為後端接點,接 LangGraph 時換掉即可。
   - 作答用真語音轉文字(免權限)→ 逐字稿餵 Prober;反問環節用點選;不打字。
   - 捨棄聊天版的靜默偵測/瞄時間微節拍(語音場景式不適用)。
   ===================================================================== */

private data class ReverseOption(val ask: String, val tag: String, val answer: String, val closing: String)

private val reverseOptions = listOf(
    ReverseOption(
        ask = "團隊接下來半年,最大的挑戰是什麼?", tag = "問挑戰",
        answer = "(他想了想)好問題。最大的挑戰是新產品線的節奏:資源沒變,目標翻倍。進來的人會直接碰到這一塊。",
        closing = "今天就到這裡。你最後這個問題,我喜歡。等通知。",
    ),
    ReverseOption(
        ask = "這個職位做得好的人,一年後通常長成什麼樣子?", tag = "問成長",
        answer = "一年後做得好的人,通常已經能自己扛一條小產品線,開始帶實習生。我們希望你長得比職缺快。",
        closing = "問得很實際。今天先到這裡,後續人資會跟你聯繫。",
    ),
    ReverseOption(
        ask = "想先確認一下,這個職位的薪資範圍和獎金結構?", tag = "直球",
        answer = "(他頓了一下)……這個階段我先不談數字,人資後續會說明。還有別的想問的嗎?",
        closing = "好,那今天先到這裡。",
    ),
    ReverseOption(
        ask = "目前沒有問題了,謝謝。", tag = "沒有問題",
        answer = "(他點點頭)行。",
        closing = "今天就到這裡,等通知。",
    ),
)
private val reverseOptionsEn = listOf(
    ReverseOption(
        ask = "What's the biggest challenge for the team in the next six months?", tag = "問挑戰",
        answer = "(He thinks for a moment.) Good question. Pacing the new product line — same resources, double the targets. You'd be right in the middle of it.",
        closing = "That's it for today. I liked that last question. We'll be in touch.",
    ),
    ReverseOption(
        ask = "What does someone great in this role look like after one year?", tag = "問成長",
        answer = "After a year, the good ones own a small product line and start mentoring interns. We want you to outgrow the job description.",
        closing = "Very practical question. That's all for today — HR will follow up.",
    ),
    ReverseOption(
        ask = "Could we confirm the salary range and bonus structure?", tag = "直球",
        answer = "(He pauses.) ...Let's not get into numbers at this stage. HR will walk you through it. Anything else?",
        closing = "Alright, that's all for today.",
    ),
    ReverseOption(
        ask = "No questions for now. Thank you.", tag = "沒有問題",
        answer = "(He nods.) Alright.",
        closing = "That's it for today. We'll be in touch.",
    ),
)

private val probesDefault = listOf(
    "嗯,了解。可以再給一個更具體的例子嗎?",
    "那當時你怎麼衡量這個決定的影響?",
    "如果重來一次,你會有什麼不同的做法?",
    "這段經驗裡,你覺得自己最關鍵的貢獻是什麼?",
)
private val englishProbes = listOf(
    "I see. Can you give me a more concrete example?",
    "How did you measure the impact of that decision?",
    "If you could do it again, what would you do differently?",
    "What was your single most critical contribution there?",
)
private val probesTechType = listOf(
    "講一個你寫過最複雜的查詢或程式邏輯,它解決什麼問題?",
    "如果報表突然變慢十倍,你會從哪裡開始查?",
    "你怎麼驗證自己的分析結果沒有錯?",
    "最近自學了什麼工具或技術?怎麼學的?",
)
private val probesCaseType = listOf(
    "上線前一天發現重大 bug,修好要兩天。你怎麼辦?",
    "兩位主管同時給你衝突的指令,你怎麼處理?",
    "資源被砍一半,目標不變,你先丟掉哪一塊?",
    "使用者在社群罵爆你負責的功能,你的第一步是什麼?",
)

private fun faceFor(d: Int): Int = when {
    d >= 1 -> R.drawable.ken_happy
    d <= -1 -> R.drawable.ken_concerned
    else -> R.drawable.ken_neutral
}
private fun deltaFor(answer: String): Int = when {
    answer.isBlank() -> 0
    answer.contains("不知道") || answer.contains("不確定") || answer.contains("沒想過") -> -1
    answer.length >= 24 -> 1
    answer.length >= 10 -> 0
    else -> -1
}

@Composable
fun InterviewLiveIndividualScreen(navController: NavHostController) {
    val lang = InterviewConfig.language
    fun t(zh: String, en: String) = if (lang == "English") en else zh

    val openingQ = t("先請你做一個簡短的自我介紹,大約一分鐘。", "Let's start. Give me a one-minute introduction — who you are, and why this role.")
    val probes = when {
        lang == "English" -> englishProbes
        InterviewConfig.type == "技術" -> probesTechType
        InterviewConfig.type == "情境" -> probesCaseType
        else -> probesDefault
    }
    val reverses = if (lang == "English") reverseOptionsEn else reverseOptions

    var phase by remember { mutableStateOf("MAIN") }   // MAIN / REVERSE / CLOSING / DONE
    var question by remember { mutableStateOf(openingQ) }
    var answer by remember { mutableStateOf("") }
    var reactingDelta by remember { mutableIntStateOf(0) }
    var followUpIdx by remember { mutableIntStateOf(0) }
    var lastProbe by remember { mutableStateOf("") }
    var repeatFired by remember { mutableStateOf(false) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var shared by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { InterviewSession.reset(); while (true) { delay(1000); elapsedSec++ } }
    val timerText = "${(elapsedSec / 60).toString().padStart(2, '0')}:${(elapsedSec % 60).toString().padStart(2, '0')}"
    val role = InterviewConfig.customRole.ifBlank { "Junior PM" }

    fun submitAnswer(transcript: String) {
        if (phase != "MAIN" || transcript.isBlank() || answer.isNotBlank()) return
        answer = transcript
        InterviewSession.record(question, transcript)
        reactingDelta = deltaFor(transcript)
        scope.launch {
            delay(1500)
            val reply = when {
                followUpIdx == 2 && !repeatFired -> {
                    repeatFired = true
                    t("(他翻了下筆記)剛剛那題,我再問一次:$lastProbe", "(He flips back a page.) Let me ask that one again: $lastProbe")
                }
                followUpIdx >= 4 -> {
                    phase = "REVERSE"
                    t("好,主要的問題就到這裡。最後,你有什麼想問我們的?", "Alright, that covers the main questions. One last thing: what would you like to ask us?")
                }
                else -> MockInterviewProber.probe(transcript, followUpIdx, probes).also { lastProbe = it }
            }
            question = reply
            answer = ""
            reactingDelta = 0
            followUpIdx += 1
        }
    }

    fun pickReverse(opt: ReverseOption) {
        if (phase != "REVERSE") return
        answer = opt.ask
        phase = "CLOSING"
        scope.launch {
            delay(900); question = opt.answer
            delay(1600); question = opt.closing
            delay(1300); phase = "DONE"
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
            painter = painterResource(R.drawable.bg_scene_1on1),
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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x22FFFFFF))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(t("1 對 1 面試 · $role", "1-on-1 · $role"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${InterviewConfig.type} · ${InterviewConfig.round} · ${InterviewConfig.difficulty}",
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
                ) { Text(t("結束", "End"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(14.dp))

            // 面試官立繪:依回答換表情
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(faceFor(if (phase == "MAIN") reactingDelta else 0)),
                        contentDescription = t("面試官", "Interviewer"),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(184.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(BrandDeepOrange).padding(horizontal = 13.dp, vertical = 5.dp),
                    ) {
                        val tag = when {
                            phase == "DONE" -> t("面試結束", "Interview complete")
                            phase == "CLOSING" -> t("回應中…", "Responding…")
                            phase == "REVERSE" -> t("換你提問", "Your turn to ask")
                            answer.isNotBlank() -> t("面試官", "Interviewer") + "（" + (if (reactingDelta >= 1) t("認可地點點頭", "nods") else if (reactingDelta <= -1) t("等你再多說一點", "waiting for more") else t("若有所思", "considering")) + "）"
                            else -> t("面試官　發問中", "Interviewer　asking")
                        }
                        Text(tag, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 對話框(面試官當前一句)
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xDB241B12))
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
            ) {
                Text(question, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
            }

            if (answer.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFF7EE)).padding(13.dp),
                ) {
                    Text(if (phase == "CLOSING") t("你的提問", "Your question") else t("你的回答（語音）", "Your answer (voice)"), color = Color(0xFF9A6A3A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(answer, color = Color(0xFF1F2937), fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // 底部依階段切換
            when (phase) {
                "REVERSE" -> {
                    Text(t("你的反問", "Your question"), color = Color(0x99FFFFFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    reverses.forEach { opt ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp)).background(Color(0x1AFFFFFF))
                                .clickable { pickReverse(opt) }
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(opt.ask, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            Box(
                                Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x33FFB627)).padding(horizontal = 8.dp, vertical = 3.dp),
                            ) { Text(opt.tag, color = BrandAmber, fontSize = 10.sp, fontWeight = FontWeight.Black) }
                        }
                    }
                }
                "DONE" -> {
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(BrandOrange)
                            .clickable { navController.navigate(Routes.INTERVIEW_REPORT) }
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(t("面試結束 · 看完整報告", "Done · View full report"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black) }
                }
                "CLOSING" -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(t("面試官回應中…", "Interviewer is responding…"), color = Color(0x73FFFFFF), fontSize = 12.sp)
                    }
                }
                else -> {
                    val pending = pendingTranscript
                    if (pending != null) {
                        Column(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFF7EE)).padding(14.dp),
                        ) {
                            Text(t("確認這段回答內容：", "Confirm your answer:"), color = Color(0xFF9A6A3A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                ) { Text(t("重新錄音", "Re-record"), color = Color(0xFF6B5B4A), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                Box(
                                    Modifier.weight(1f).clip(RoundedCornerShape(999.dp))
                                        .background(BrandOrange)
                                        .clickable {
                                            submitAnswer(pending)
                                            pendingTranscript = null
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center,
                                ) { Text(t("確認送出", "Confirm & send"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x14FFFFFF))
                                    .clickable { fileLauncher.launch("*/*") }.padding(horizontal = 11.dp, vertical = 7.dp),
                            ) { Text(t("分享工作", "Share work"), color = Color(0xCCFFFFFF), fontSize = 11.sp) }
                            if (shared) {
                                Spacer(Modifier.width(8.dp))
                                Row(
                                    Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x2910B981)).padding(horizontal = 10.dp, vertical = 7.dp),
                                ) { Text(t("已分享", "Shared"), color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            Spacer(Modifier.weight(1f))
                            if (isTranscribing) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = BrandOrange,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(28.dp),
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(t("轉錄中...", "Transcribing..."), color = Color(0x99FFFFFF), fontSize = 11.sp)
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
                        }
                    }
                }
            }
        }
    }
}
