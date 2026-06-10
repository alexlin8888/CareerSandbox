package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    var stage by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(100)
        stage = 1
        delay(400)
        stage = 2
        delay(400)
        stage = 3
        delay(1100)
        try { onDone() } catch (_: Throwable) {}
    }

    val infinite = rememberInfiniteTransition(label = "float")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "floatY",
    )

    Box(modifier = Modifier.fillMaxSize().background(InkCharcoal)) {
        // 散落線稿裝飾
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.5f))

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
        ) {
            Spacer(Modifier.height(64.dp))

            // AI Powered 徽章
            AnimatedVisibility(
                visible = stage >= 1,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 },
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandYellow)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null,
                            tint = InkCharcoal, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("AI Powered",
                            color = InkCharcoal,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // 主標題 — 巨大不對稱
            AnimatedVisibility(
                visible = stage >= 2,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 6 },
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("找工作\n")
                        withStyle(SpanStyle(color = BrandOrange)) { append("不再") }
                        append("\n孤單。")
                    },
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    lineHeight = 62.sp,
                    letterSpacing = (-1.5).sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = stage >= 3,
                enter = fadeIn(tween(600)),
            ) {
                Column {
                    Text(
                        "AI 陪你找方向 / 練面試 / 寫履歷",
                        color = InkGray400,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("CAREER SANDBOX",
                        color = PaperWhite.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp)
                }
            }

            Spacer(Modifier.weight(1f))
        }

        // 插畫破框 — 從右下進入,半個身體在畫面外
        AnimatedVisibility(
            visible = stage >= 1,
            enter = fadeIn(tween(900)) + slideInVertically(tween(900)) { it / 3 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 24.dp, y = floatY.dp + 48.dp)
                .size(320.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.beaver_wave),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
