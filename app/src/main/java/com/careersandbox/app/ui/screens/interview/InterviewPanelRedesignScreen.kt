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
import com.careersandbox.app.data.mock.InterviewEngineProvider
import com.careersandbox.app.data.mock.InterviewQuestionRequest
import com.careersandbox.app.data.mock.InterviewScoreRequest
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch

/* =====================================================================
   場景式 Panel 面試(方向 A) — engine-driven
   - 背景:bg_scene_meeting(沿用沙盒會議場景)+ 暖黑暈影,風格一致。
   - 主考官:沙盒角色(Ken 用人主管 / 阿哲 技術主管 / Vivian 業務代表),有表情變體 → 可依評分換臉。
   - 題目與評分走 InterviewEngineProvider.engine(mock↔remote):
       nextQuestion 取題;scoreAnswer 回 reactionDelta → 驅動表情 + 名牌反應。
     Mock 引擎瞬回(同題、同啟發式);後端就緒切 remote 後表情即依真評分變。
   - 作答用語音(免權限);可上傳作品(免權限);不打字。
   ===================================================================== */

private data class Seat(val name: String, val role: String, val accent: Color)

private val panel = listOf(
    Seat("Vivian", "業務代表", BrandAmber),
    Seat("阿哲", "技術主管", BrandOrange),
    Seat("Ken", "用人主管", BrandDeepOrange),
)

// 依座位(角色)+ 反應強度回傳沙盒立繪;d>0 正面、d<0 負面、0 中性
private fun faceFor(seat: Int, d: Int): Int = when (seat) {
    0 -> when { d >= 1 -> R.drawable.colleague_vivian_satisfied; d <= -1 -> R.drawable.colleague_vivian_displeased; else -> R.drawable.colleague_vivian }
    1 -> if (d <= -1) R.drawable.colleague_akai_frustrated else R.drawable.colleague_akai_calm
    else -> when { d >= 1 -> R.drawable.ken_happy; d <= -1 -> R.drawable.ken_concerned; else -> R.drawable.ken_neutral }
}

@Composable
fun InterviewPanelRedesignScreen(navController: NavHostController) {
    var idx by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var shared by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var loadingQ by remember { mutableStateOf(true) }
    var reactionDelta by remember { mutableIntStateOf(0) }
    var reactionText by remember { mutableStateOf("") }
    var ended by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sessionId = remember { "interview-" + System.currentTimeMillis() }

    val finished = idx >= panel.size || ended
    val seat = idx.coerceIn(0, panel.size - 1)
    val asker = panel[seat]
    val delta = if (answer.isBlank()) 0 else reactionDelta

    // 換主考官就向引擎取下一題(mock 瞬回;remote 可能稍等)
    LaunchedEffect(idx) {
        if (idx < panel.size) {
            loadingQ = true; answer = ""; reactionText = ""; reactionDelta = 0; shared = false
            val s = idx
            val resp = InterviewEngineProvider.engine.nextQuestion(
                InterviewQuestionRequest(sessionId, s, panel[s].role, panel[s].name, s),
            )
            if (resp.concluded) ended = true else question = resp.question
            loadingQ = false
        }
    }

    fun submitAnswer(text: String) {
        answer = text
        val s = idx.coerceIn(0, panel.size - 1)
        val q = question
        scope.launch {
            val r = InterviewEngineProvider.engine.scoreAnswer(
                InterviewScoreRequest(sessionId, s, panel[s].role, q, text),
            )
            reactionDelta = r.reactionDelta
            reactionText = r.reactionText
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!text.isNullOrBlank()) submitAnswer(text)
        }
    }
    fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-TW")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "請回答主考官的問題")
        }
        try { voiceLauncher.launch(intent) } catch (e: Exception) { }
    }
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) shared = true }

    Box(Modifier.fillMaxSize().background(Color(0xFF14100B))) {
        // 場景背景圖(沿用沙盒會議場景)+ 暖黑暈影確保可讀
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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x22FFFFFF))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Text("面試 · 跨部門 Panel", color = Color(0xCCFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))

            if (finished) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("面試結束", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(8.dp))
                        Text("辛苦了,稍後可在報告看回饋。", color = Color(0x99FFFFFF), fontSize = 13.sp)
                        Spacer(Modifier.height(20.dp))
                        Box(
                            Modifier.clip(RoundedCornerShape(999.dp)).background(BrandOrange)
                                .clickable { navController.popBackStack() }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                        ) { Text("回面試首頁", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                    }
                }
                return@Column
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                panel.forEachIndexed { i, p ->
                    val on = i == idx
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(if (on) Color(0x33FFB627) else Color(0x14FFFFFF))
                            .border(if (on) 1.dp else 0.dp, if (on) p.accent else Color.Transparent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                    ) {
                        Text(p.role, color = if (on) p.accent else Color(0xD9FFFFFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(p.name, color = Color(0x73FFFFFF), fontSize = 11.sp)
                    }
                }
            }

            // 立繪:依評分換表情
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(faceFor(seat, delta)),
                        contentDescription = asker.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(180.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(asker.accent).padding(horizontal = 13.dp, vertical = 5.dp),
                    ) {
                        Text(
                            if (answer.isBlank() || reactionText.isBlank()) "${asker.name} · ${asker.role}　發問中" else "${asker.name}（$reactionText）",
                            color = Color(0xFF3A1505), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Box {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xDB241B12))
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 14.dp),
                ) {
                    Text(
                        if (loadingQ) "（思考下一題…）" else question,
                        color = Color.White, fontSize = 14.sp, lineHeight = 22.sp,
                    )
                }
                Box(
                    Modifier.align(Alignment.TopStart).offset(x = 14.dp, y = (-11).dp)
                        .clip(RoundedCornerShape(999.dp)).background(asker.accent).padding(horizontal = 11.dp, vertical = 3.dp),
                ) { Text(asker.role, color = Color(0xFF412402), fontSize = 11.sp, fontWeight = FontWeight.Black) }
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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(60.dp).clip(CircleShape).background(BrandOrange).clickable { startVoice() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Filled.Mic, contentDescription = "語音作答", tint = Color.White, modifier = Modifier.size(26.dp)) }
                    Spacer(Modifier.height(6.dp))
                    Text("點一下用說的回答", color = Color(0x99FFFFFF), fontSize = 11.sp)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(if (answer.isNotBlank()) BrandOrange else Color(0x22FFFFFF))
                        .clickable { idx += 1 }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Text(
                        if (idx == panel.size - 1) "完成面試" else "下一位主考官 →",
                        color = if (answer.isNotBlank()) Color.White else Color(0x99FFFFFF),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
