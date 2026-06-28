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
import androidx.compose.runtime.LaunchedEffect
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
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.ui.components.pressScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import com.careersandbox.app.R

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
    bgRes: Int? = null,
    repPop: RepChange? = null,
    callback: String? = null,
) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { SoundManager.sfx(R.raw.sfx_notify) }   // 決策登場提示音(進場時一次)
    Box(Modifier.fillMaxSize()) {
        // ===== 背景層:有實景圖就用圖(疊暈影+下方加深),否則用程式畫的暖色房間 =====
        if (bgRes != null) {
            Image(
                painter = painterResource(bgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().drawBehind {
                    drawRect(
                        Brush.radialGradient(
                            listOf(Color(0x00000000), Color(0x553C1E0C)),
                            center = Offset(size.width * 0.5f, size.height * 0.32f),
                            radius = size.maxDimension * 0.88f,
                        ),
                    )
                    drawRect(
                        Brush.verticalGradient(0.45f to Color(0x00000000), 1f to Color(0x73201008)),
                    )
                },
            )
        } else {
            Box(
                Modifier.fillMaxSize().drawBehind {
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
                    // 暈影
                    drawRect(
                        Brush.radialGradient(
                            listOf(Color(0x00000000), Color(0x473C1E0C)),
                            center = Offset(size.width * 0.5f, size.height * 0.30f),
                            radius = size.maxDimension * 0.92f,
                        ),
                    )
                },
            )
        }
        Column(
            Modifier.fillMaxSize().padding(start = 22.dp, end = 22.dp, top = 46.dp, bottom = 32.dp),
        ) {
            // ===== 常駐聲望 HUD(三條計量全程可見)=====
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                MeterPill("主管信任", WorkplaceState.managerTrust.value, Amber, Modifier.weight(1f))
                MeterPill("同事情誼", WorkplaceState.peerBond.value, WarmOrange, Modifier.weight(1f))
                MeterPill("專業形象", WorkplaceState.proImage.value, Color(0xFF10B981), Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))

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
                        .pressScale { SoundManager.toggleMute(ctx) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (SoundManager.muted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                        contentDescription = "靜音",
                        tint = PaperWhite,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
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

            // ===== 角色立繪(置中,地面陰影,固定高度確保各角色一致、不隨旁白長度縮放)=====
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = painterResource(portrait),
                        contentDescription = speaker,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(220.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ===== 後果回收(過去選擇/剛翻到的內容影響當下)=====
            if (callback != null) {
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(10.dp)).background(Color(0x52281C12)),
                ) {
                    Box(Modifier.width(3.dp).fillMaxHeight().background(Amber))
                    Text(
                        callback,
                        color = Color(0xFFFFF3E4), fontSize = 12.sp, lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

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
                        .pressScale { SoundManager.sfx(R.raw.sfx_confirm); onChoose(c) }
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

/* ---------- 常駐 HUD 的單條計量 ---------- */
@Composable
private fun MeterPill(label: String, value: Int, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(11.dp)).background(Color(0x6B281C12))
            .padding(horizontal = 9.dp, vertical = 6.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color(0xFFE9D9C8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("$value", color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(5.dp))
        Box(
            Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(999.dp))
                .background(Color(0x2EFFFFFF)),
        ) {
            Box(
                Modifier.fillMaxWidth((value / 10f).coerceIn(0f, 1f)).fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp)).background(color),
            )
        }
    }
}
