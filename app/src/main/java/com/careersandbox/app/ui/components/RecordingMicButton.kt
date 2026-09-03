package com.careersandbox.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

/* =====================================================================
   共用的「錄音中」麥克風按鈕：外圈波紋隨 amplitude 即時放大/縮小，
   讓使用者一眼看出「正在收音、而且真的有偵測到聲音」，
   底下再放一行倒數文字，快沒時間時變色提醒。

   用法（取代原本 InterviewLivePanelScreen 等畫面裡手刻的麥克風 Box）：
     RecordingMicButton(
         isRecording = recorder.isRecording,
         amplitude = recorder.amplitude,
         elapsedMs = recorder.elapsedMs,
         maxDurationMs = 120_000L,
         idleEnabled = answer.isBlank(),
         onClick = { if (recorder.isRecording) recorder.stop() else recorder.start() },
     )
   ===================================================================== */

@Composable
fun RecordingMicButton(
    isRecording: Boolean,
    amplitude: Float,
    elapsedMs: Long,
    maxDurationMs: Long = 120_000L,
    idleEnabled: Boolean = true,
    activeColor: Color = Color(0xFFB84A1E),   // 對齊現有的 BrandDeepOrange
    idleColor: Color = Color(0xFFE0692B),     // 對齊現有的 BrandOrange
    disabledColor: Color = Color(0x33FFFFFF),
    size: androidx.compose.ui.unit.Dp = 60.dp,
    onClick: () -> Unit,
) {
    // 錄音中即使音量很小也要有基礎的呼吸感，不然安靜幾秒畫面會死掉；
    // 有講話時，波紋再疊加 amplitude 放更大。
    val infinite = rememberInfiniteTransition(label = "mic_breathe")
    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "breathe",
    )
    val ringBoost by animateFloatAsState(
        targetValue = if (isRecording) (0.25f + amplitude * 0.75f) else 0f,
        label = "ringBoost",
    )

    val remainingMs = (maxDurationMs - elapsedMs).coerceAtLeast(0L)
    val remainingSec = (remainingMs / 1000L).toInt()
    val isRunningOut = isRecording && remainingMs <= 10_000L

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (isRecording) {
                Canvas(modifier = Modifier.size(size * 2.2f)) {
                    val baseRadius = (size.toPx() / 2f)
                    // 兩圈波紋，半徑跟透明度都跟 breathe + ringBoost 連動
                    listOf(0f, 0.5f).forEach { phase ->
                        val local = ((breathe + phase) % 1f)
                        val radius = baseRadius + (baseRadius * 1.1f * ringBoost * local)
                        val alpha = (1f - local) * 0.35f
                        drawCircle(
                            color = activeColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                            radius = radius,
                            center = Offset(this.size.width / 2f, this.size.height / 2f),
                            style = Stroke(width = 3.dp.toPx()),
                        )
                    }
                }
            }
            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        when {
                            isRecording -> activeColor
                            idleEnabled -> idleColor
                            else -> disabledColor
                        }
                    )
                    .clickable(enabled = isRecording || idleEnabled) { onClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size.value.dp * 0.43f),
                )
            }
        }
        if (isRecording) {
            Spacer(Modifier.height(6.dp))
            Text(
                "剩 %d:%02d".format(remainingSec / 60, remainingSec % 60),
                color = if (isRunningOut) Color(0xFFFF6B6B) else Color(0xB3FFFFFF),
                fontSize = 12.sp,
            )
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF14100B)
@Composable
private fun RecordingMicButtonPreview() {
    var elapsed by remember { mutableStateOf(0L) }
    var amp by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (true) {
            delay(100)
            elapsed = System.currentTimeMillis() - start
            // 用 sin 波模擬「音量忽大忽小」，只是為了預覽看效果，跟真的錄音無關
            amp = ((kotlin.math.sin(elapsed / 300.0) + 1) / 2).toFloat()
        }
    }
    RecordingMicButton(
        isRecording = true,
        amplitude = amp,
        elapsedMs = elapsed,
        maxDurationMs = 15_000L, // 預覽故意設短，方便快速看到「剩餘秒數變紅」的效果
        onClick = {},
    )
}