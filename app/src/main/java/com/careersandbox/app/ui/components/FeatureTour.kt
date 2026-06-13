package com.careersandbox.app.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*
import kotlin.math.roundToInt

/* =====================================================================
   功能導覽 v2 —— 錨定底部導航的 coach marks
   每一站介紹一個功能 → 暗場挖洞露出該功能的「真實 tab」→ 箭頭指著它
   只給新使用者(SharedPreferences 首啟旗標);設定可手動重看
   ===================================================================== */

private const val PREFS = "career_sandbox_prefs"
private const val KEY_SEEN_TOUR = "has_seen_feature_tour"

object TourState {
    /** 設定頁手動重看時設 true,外殼觀察它強制播放 */
    var forceShow by mutableStateOf(false)

    fun shouldShowOnLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_SEEN_TOUR, false)
    }

    fun markSeen(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SEEN_TOUR, true).apply()
    }
}

/** BottomNav 把每個 tab 的螢幕座標寫進來,導覽讀它定位箭頭與挖洞 */
object TourAnchors {
    val bounds = mutableStateMapOf<String, Rect>()
}

private data class TourStop(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val body: String,
)

// 五站對齊五個導航目的地
private val tourStops = listOf(
    TourStop(
        Routes.HOME, Icons.Outlined.Home, "首頁:從職缺出發",
        "「接著做」幫你盯住目標職缺,練面試、看還缺什麼,一條線走完。",
    ),
    TourStop(
        Routes.RESUME_HUB, Icons.Outlined.Description, "履歷工房",
        "一份母版,對每個職缺長出客製版本,投遞狀態一目了然。",
    ),
    TourStop(
        Routes.INTERVIEW_HUB, Icons.Outlined.Mic, "AI 面試,真的會追問",
        "一對一、三位 panel、整組同儕都能練。也有 60 秒快速面試,隨手來一場。",
    ),
    TourStop(
        Routes.WORKPLACE_SANDBOX, Icons.Outlined.Coffee, "職場沙盒,先踩雷",
        "入職一週:和主管 1on1、Email 風暴、跨部門會議。在這裡犯的錯不會留案底。",
    ),
    TourStop(
        Routes.PROFILE, Icons.Outlined.Person, "我的:留下痕跡",
        "你做的每個選擇都被記著,成長軌跡用形狀告訴你長到哪了。",
    ),
)

@Composable
fun FeatureTourOverlay(visible: Boolean, onClose: () -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn(tween(300)), exit = fadeOut(tween(250))) {
        var step by remember { mutableIntStateOf(0) }
        val stop = tourStops[step]
        val anchor = TourAnchors.bounds[stop.route]
        val density = LocalDensity.current

        fun advance() {
            if (step < tourStops.lastIndex) step++ else onClose()
        }

        val pulse = rememberInfiniteTransition(label = "tourPulse")
        val ring by pulse.animateFloat(
            initialValue = 0.6f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "tourRing",
        )

        Box(
            Modifier.fillMaxSize().pressScale(onClick = { advance() }),
        ) {
            // 暗場 + 在 tab 位置挖一個圓洞(露出真實導航)
            Canvas(
                Modifier.fillMaxSize().graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
            ) {
                drawRect(InkBlack.copy(alpha = 0.82f))
                anchor?.let {
                    val r = (it.maxDimension / 2f) + 12.dp.toPx()
                    // 脈動光圈
                    drawCircle(
                        color = BrandOrange.copy(alpha = 0.35f * ring),
                        radius = r + 8.dp.toPx(),
                        center = it.center,
                    )
                    // 挖洞
                    drawCircle(color = Color.Transparent, radius = r, center = it.center, blendMode = BlendMode.Clear)
                }
            }

            // 箭頭:從卡片指向 tab(畫在洞的正上方)
            anchor?.let {
                Canvas(
                    Modifier
                        .offset {
                            IntOffset(
                                (it.center.x - 12.dp.toPx()).roundToInt(),
                                (it.top - 14.dp.toPx() - 12.dp.toPx()).roundToInt(),
                            )
                        }
                        .size(24.dp),
                ) {
                    val p = Path().apply {
                        moveTo(size.width / 2f, size.height)        // 尖端朝下
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(p, BrandOrange)
                }
            }

            // 導覽卡:固定在導航上方
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 132.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PaperWhite)
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(BrandPeach.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) { Icon(stop.icon, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Text(stop.title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(stop.body, color = InkGray700, fontSize = 13.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 進度點
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tourStops.indices.forEach { i ->
                            Box(
                                Modifier
                                    .width(if (i == step) 18.dp else 6.dp).height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (i == step) BrandOrange else InkGray200),
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        if (step == 0) "跳過" else "上一步",
                        color = InkGray400, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.pressScale { if (step == 0) onClose() else step-- }.padding(8.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(InkBlack)
                            .pressScale { advance() }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                    ) {
                        Text(
                            if (step == tourStops.lastIndex) "開始使用" else "下一個",
                            color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}
