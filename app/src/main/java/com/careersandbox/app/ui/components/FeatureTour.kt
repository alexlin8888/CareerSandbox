package com.careersandbox.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R
import com.careersandbox.app.ui.theme.*

// 每個 app 行程播一次;設定 → 幫助與支援 可隨時重看
object TourState {
    var seenThisSession = false
}

private data class TourStop(
    val icon: ImageVector,
    val title: String,
    val body: String,
)

private val tourStops = listOf(
    TourStop(
        Icons.Outlined.TrendingUp, "從職缺出發",
        "首頁的「接著做」幫你盯住目標職缺:練面試、看還缺什麼,一條線走完。",
    ),
    TourStop(
        Icons.Outlined.Mic, "AI 面試,真的會追問",
        "一對一、三位 panel、整組同儕都能練。支援語音作答;你沉默太久,面試官也會有反應。",
    ),
    TourStop(
        Icons.Outlined.Coffee, "職場沙盒,先踩雷",
        "入職第一週:和主管 1on1、Email 風暴。在這裡犯的錯,不會留案底。",
    ),
    TourStop(
        Icons.Outlined.Send, "履歷工房",
        "一份母版,對每個職缺長出客製版本,投遞狀態一目了然。",
    ),
    TourStop(
        Icons.Outlined.EmojiEvents, "痕跡,週五揭曉",
        "你做的每個選擇都被記著。回顧頁用形狀告訴你,長到哪了。",
    ),
)

@Composable
fun FeatureTourOverlay(visible: Boolean, onClose: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(250)),
    ) {
        var idx by remember { mutableIntStateOf(0) }
        val isLast = idx == tourStops.lastIndex

        fun advance() {
            if (isLast) onClose() else idx++
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InkBlack.copy(alpha = 0.93f))
                .pressScale { advance() },
        ) {
            Crossfade(targetState = idx, label = "tourStop") { i ->
                val stop = tourStops[i]
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // 聚光圈:外層暖光暈 + 內圈 + icon,河狸站在圈邊
                    Box(Modifier.size(190.dp)) {
                        Box(
                            Modifier
                                .size(190.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(BrandOrange.copy(alpha = 0.38f), InkBlack.copy(alpha = 0f))
                                    )
                                ),
                        )
                        Box(
                            Modifier
                                .size(122.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(PaperWhite.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(stop.icon, contentDescription = null,
                                tint = BrandOrange, modifier = Modifier.size(52.dp))
                        }
                        Image(
                            painter = painterResource(R.drawable.beaver_point),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(86.dp)
                                .offset(x = 18.dp, y = 12.dp),
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(stop.title,
                        color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(stop.body,
                        color = PaperWhite.copy(alpha = 0.78f),
                        fontSize = 14.sp, lineHeight = 22.sp,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(110.dp)) // 讓內容視覺重心略高於正中
                }
            }

            // 進度點 + 操作(固定底部)
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tourStops.forEachIndexed { i, _ ->
                        Box(
                            Modifier
                                .width(if (i == idx) 18.dp else 6.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (i == idx) BrandOrange else PaperWhite.copy(alpha = 0.3f)),
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .pressScale { onClose() }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text("跳過", color = PaperWhite.copy(alpha = 0.6f),
                            fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandOrange)
                            .pressScale { advance() }
                            .padding(horizontal = 28.dp, vertical = 12.dp),
                    ) {
                        Text(if (isLast) "開始探索" else "下一步",
                            color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
