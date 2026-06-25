package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.ui.components.pressScale

/* =====================================================================
   SandboxDecisionScene —— 插畫決策場景(1:1 還原 Claude Design「決策場景 v2」)
   暖色會議室漸層 + 窗光 + 暈影｜左上「決策時刻」霧面藥丸 + 右上暫停
   置中角色立繪(地面陰影)｜霧面 espresso 對話框(amber 名牌)｜白色 A/B/C 選項卡
   選擇後由呼叫端 apply 計量,repPop 在頂部彈出原因。
   ===================================================================== */

data class DecisionChoice(
    val letter: String,            // A / B / C / D
    val label: String,
    val repMeter: String = "主管信任",
    val repDelta: Int = 0,
    val repReason: String = "",
    val flag: String? = null,      // 跨天旗標(後果回收)
)

private val DsEspresso = Color(0xFF281C12)
private val PaperWhite = Color(0xFFFFF8F3)
private val Amber = Color(0xFFFFB627)
private val WarmOrange = Color(0xFFF2531C)

@Composable
fun SandboxDecisionScene(
    speaker: String,
    portrait: Int,
    narration: String,
    choices: List<DecisionChoice>,
    onChoose: (DecisionChoice) -> Unit,
    onBack: () -> Unit,
    sceneLabel: String = "決策時刻",
    repPop: RepChange? = null,
) {
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                // 暖色牆面底漸層
                drawRect(
                    Brush.verticalGradient(
                        0f to Color(0xFFF6D9B8), 0.38f to Color(0xFFEFC59C),
                        0.70f to Color(0xFFE2A878), 1f to Color(0xFFC98B5E),
                    ),
                )
                // 左上窗光
                drawRect(
                    Brush.radialGradient(
                        listOf(Color(0xD9FFEFD2), Color(0x00FFEFD2)),
                        center = Offset(size.width * 0.24f, size.height * 0.16f),
                        radius = size.minDimension * 0.78f,
                    ),
                )
                // 右上暖光
                drawRect(
                    Brush.radialGradient(
                        listOf(Color(0x59FFB627), Color(0x00FFB627)),
                        center = Offset(size.width * 0.92f, size.height * 0.20f),
                        radius = size.minDimension * 0.6f,
                    ),
                )
                // 暈影:讓下方 UI 讀得清楚
                drawRect(
                    Brush.radialGradient(
                        listOf(Color(0x00000000), Color(0x473C1E0C)),
                        center = Offset(size.width * 0.5f, size.height * 0.30f),
                        radius = size.maxDimension * 0.92f,
                    ),
                )
            },
    ) {
        Column(
            Modifier.fillMaxSize().padding(start = 22.dp, end = 22.dp, top = 56.dp, bottom = 32.dp),
        ) {
            // ===== 頂部:決策時刻 + 暫停 =====
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(Color(0x6B281C12))
                        .padding(start = 14.dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Amber))
                    Spacer(Modifier.width(8.dp))
                    Text(sceneLabel, color = PaperWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(Color(0x6B281C12))
                        .pressScale { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(width = 4.dp, height = 15.dp).clip(RoundedCornerShape(2.dp)).background(PaperWhite))
                        Box(Modifier.size(width = 4.dp, height = 15.dp).clip(RoundedCornerShape(2.dp)).background(PaperWhite))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ===== 角色立繪(置中,地面陰影)=====
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = painterResource(portrait),
                        contentDescription = speaker,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxHeight(0.92f),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ===== 對話框(amber 名牌 + 旁白)=====
            Box {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                        .background(Color(0xDB281C12))
                        .padding(start = 22.dp, end = 22.dp, top = 26.dp, bottom = 22.dp),
                ) {
                    Text(narration, color = PaperWhite, fontSize = 16.sp, lineHeight = 27.sp)
                }
                Box(
                    Modifier.align(Alignment.TopStart).offset(x = 22.dp, y = (-13).dp)
                        .clip(RoundedCornerShape(999.dp)).background(Amber)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(speaker, color = DsEspresso, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ===== 選項卡(A/B/C 圓形字母徽章)=====
            choices.forEach { c ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                        .background(PaperWhite)
                        .pressScale { onChoose(c) }
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(30.dp).clip(CircleShape).background(Color(0x1FF2531C)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(c.letter, color = WarmOrange, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(c.label, color = DsEspresso, fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 21.sp)
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ===== 計量彈窗(頂部,選擇後彈出原因)=====
        AnimatedVisibility(
            visible = repPop != null,
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 108.dp),
        ) {
            repPop?.let { rc ->
                val up = rc.delta > 0
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(DsEspresso)
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(rc.meter, color = PaperWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        (if (up) "+" else "") + rc.delta,
                        color = if (up) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 14.sp, fontWeight = FontWeight.Black,
                    )
                    if (rc.reason.isNotBlank()) {
                        Spacer(Modifier.width(10.dp))
                        Text(rc.reason, color = PaperWhite.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
