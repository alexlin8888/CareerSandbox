package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.DarkGlowBackdrop
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    var stage by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(80)
        stage = 1     // logo fade in
        delay(420)
        stage = 2     // 標語 fade in
        delay(420)
        stage = 3     // tagline
        delay(900)
        try { onDone() } catch (_: Throwable) {}
    }

    // 漂浮動畫
    val infinite = rememberInfiniteTransition(label = "float")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatY",
    )

    DarkGlowBackdrop(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // 上區:標籤
            AnimatedVisibility(
                visible = stage >= 1,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 },
            ) {
                Row(
                    modifier = Modifier.padding(top = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandYellow)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null,
                                tint = InkCharcoal, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("AI Powered", color = InkCharcoal,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // 中區:Logo + 主標題(不對稱左對齊)
            Column {
                AnimatedVisibility(
                    visible = stage >= 1,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 6 },
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = floatY.dp)
                            .size(80.dp)
                            .shadow(28.dp, RoundedCornerShape(24.dp),
                                spotColor = BrandOrange.copy(alpha = 0.7f))
                            .clip(RoundedCornerShape(24.dp))
                            .background(HeroGradient),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("CS", color = PaperWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp)
                    }
                }

                Spacer(Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = stage >= 2,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 },
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("找工作\n很")
                            withStyle(SpanStyle(color = BrandYellow)) { append("累") }
                            append(",\n我們")
                            withStyle(SpanStyle(color = BrandOrange)) { append("陪你") }
                            append("。")
                        },
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 52.sp,
                        lineHeight = 58.sp,
                        letterSpacing = (-1).sp,
                    )
                }

                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = stage >= 3,
                    enter = fadeIn(tween(600)),
                ) {
                    Text(
                        "Career Sandbox  ・  v0.2.0",
                        color = InkGray400,
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp,
                    )
                }
            }

            // 下區:右下角小指引
            AnimatedVisibility(
                visible = stage >= 3,
                enter = fadeIn(tween(800)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        "loading ⋯",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.6f),
                    )
                }
            }
        }
    }
}
