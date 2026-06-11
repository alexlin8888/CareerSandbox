package com.careersandbox.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.theme.*
import kotlin.math.sin

// 按住說話輸入列(個人 / panel / 團體面試共用)
@Composable
fun VoiceBar(
    recording: Boolean,
    recordSec: Int,
    onKeyboard: () -> Unit,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().background(PaperOff).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(InkGray100)
                    .pressScale { onKeyboard() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Keyboard, contentDescription = null, tint = InkGray700) }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (recording) BrandDeepOrange else InkBlack)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            onPressStart()
                            tryAwaitRelease()
                            onPressEnd()
                        })
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (recording) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WaveBars()
                        Spacer(Modifier.width(10.dp))
                        Text("鬆開送出 ・ $recordSec 秒", color = PaperWhite, fontWeight = FontWeight.Black)
                    }
                } else {
                    Text("按住 說話", color = PaperWhite, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun WaveBars() {
    val t = rememberInfiniteTransition(label = "wave")
    val ph by t.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "ph",
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(7) { i ->
            val h = 8 + (7 * (1 + sin(ph + i * 0.9f))).toInt()
            Box(Modifier.width(3.dp).height(h.dp).clip(RoundedCornerShape(50)).background(PaperWhite))
        }
    }
}
