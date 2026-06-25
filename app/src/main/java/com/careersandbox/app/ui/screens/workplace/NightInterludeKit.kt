package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.PaperWhite

/* =====================================================================
   NightInterludeKit —— 沙盒日場景之間「夜晚滑手機」過場的共用件
   每天的過場差在「反應內容」(讀 WorkplaceState),外殼與訊息卡共用。
   espresso 夜色,沿用 NovaLock 視覺語言,不碰任何真品牌。
   ===================================================================== */

/** 依某條計量分桶,決定整體語氣。 */
enum class NightBucket { HIGH, MID, LOW }

fun nightBucket(value: Int, high: Int = 6, low: Int = 2): NightBucket = when {
    value >= high -> NightBucket.HIGH
    value <= low -> NightBucket.LOW
    else -> NightBucket.MID
}

/** 夜晚外殼:時間 + 「那天晚上」+ 旁白 + 內容槽(訊息卡) + 伏筆 + 收尾。 */
@Composable
fun NightShell(
    time: String,
    opening: String,
    foreshadow: String,
    navController: NavHostController,
    backTo: String,
    continueLabel: String = "把手機放下,睡了",
    dayDone: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109), Color(0xFF120B05))),
        ),
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll)
                .padding(start = 22.dp, end = 22.dp, top = 56.dp, bottom = 32.dp),
        ) {
            Text(time, color = Color(0xCCFFF8F3), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text("那天晚上", color = PaperWhite, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(14.dp))
            Text(opening, color = Color(0x99FFF8F3), fontSize = 14.sp, lineHeight = 22.sp)
            Spacer(Modifier.height(24.dp))

            content()

            Spacer(Modifier.height(28.dp))
            Text(foreshadow, color = Color(0x80FFF8F3), fontSize = 13.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(BrandOrange)
                    .pressScale {
                        dayDone?.let { WorkplaceState.completeDay(it) }
                        navController.popBackStack(backTo, inclusive = false)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(continueLabel, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}

/** 鎖屏訊息卡:預設只露一行,點一下展開全文(像睡前滑手機)。 */
@Composable
fun NightMsgCard(sender: String, channel: String, text: String, accent: Color) {
    var expanded by remember { mutableStateOf(false) }
    val preview = if (text.length > 18) text.take(18) + "…" else text

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color(0x14FFFFFF))
            .clickable { expanded = !expanded }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(8.dp))
            Text(sender, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(channel, color = Color(0x73FFF8F3), fontSize = 11.sp)
            Spacer(Modifier.weight(1f))
            Text(if (expanded) "收合" else "展開", color = Color(0x66FFF8F3), fontSize = 11.sp)
        }
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(visible = !expanded, enter = fadeIn(), exit = fadeOut()) {
            Text(preview, color = Color(0xB3FFF8F3), fontSize = 13.5f.sp, lineHeight = 20.sp)
        }
        AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
            Text(text, color = Color(0xF2FFF8F3), fontSize = 15.sp, lineHeight = 24.sp)
        }
    }
}
