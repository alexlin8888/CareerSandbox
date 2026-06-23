package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        delay(100); stage = 1
        delay(400); stage = 2
        delay(400); stage = 3
        delay(1200)
        try { onDone() } catch (_: Throwable) {}
    }

    val infinite = rememberInfiniteTransition(label = "float")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatY",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF8243), Color(0xFFF2531C), Color(0xFFD6390F)),
                ),
            ),
    ) {
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.45f))

        Column(modifier = Modifier.fillMaxSize().padding(34.dp)) {
            Spacer(Modifier.height(60.dp))

            // AI Powered badge
            AnimatedVisibility(
                visible = stage >= 1,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 },
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandYellow)
                        .padding(horizontal = 13.dp, vertical = 7.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = InkBlack, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("AI POWERED", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 0.5.sp)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // big title
            AnimatedVisibility(
                visible = stage >= 2,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 6 },
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("找工作\n")
                        withStyle(SpanStyle(color = Color(0xFF2A1505))) { append("不再") }
                        append("\n孤單。")
                    },
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    lineHeight = 60.sp,
                    letterSpacing = (-2).sp,
                )
            }

            Spacer(Modifier.height(22.dp))

            AnimatedVisibility(visible = stage >= 3, enter = fadeIn(tween(600))) {
                Column {
                    Text(
                        "AI 陪你找方向 / 練面試 / 寫履歷",
                        color = PaperWhite.copy(alpha = 0.92f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "職涯沙盒 · CAREER SANDBOX",
                        color = PaperWhite.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // loading dots
            AnimatedVisibility(visible = stage >= 3, enter = fadeIn(tween(500))) {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(22.dp).height(8.dp).clip(RoundedCornerShape(5.dp)).background(PaperWhite))
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite.copy(alpha = 0.45f)))
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PaperWhite.copy(alpha = 0.45f)))
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // beaver — floats in from bottom-right, half out of frame
        AnimatedVisibility(
            visible = stage >= 1,
            enter = fadeIn(tween(900)) + slideInVertically(tween(900)) { it / 3 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 18.dp, y = floatY.dp + 36.dp)
                .size(330.dp),
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
