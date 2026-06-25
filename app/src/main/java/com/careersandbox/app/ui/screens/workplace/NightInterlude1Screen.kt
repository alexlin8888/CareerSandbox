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
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.PaperWhite

/* =====================================================================
   那天晚上 —— Day1「和主管 1on1」之後的夜晚過場
   不是新的決策關,是「下班回家滑手機」的沉浸過場:
   依白天 1on1 的結果(主管信任值 + 跨天旗標)長出不同的鎖屏訊息,
   讓選擇被「回收」成看得見的故事與人味。媽的訊息恆定,是情緒錨點。
   視覺沿用 NovaLock 的 espresso 夜色,不碰任何真品牌。
   ===================================================================== */

@Composable
fun NightInterlude1Screen(navController: NavHostController) {
    val trust = WorkplaceState.managerTrust.value
    val askedAkai = WorkplaceState.hasFlag("d1_asked_akai")
    val dodged = WorkplaceState.hasFlag("d1_dodged")
    val solo = WorkplaceState.hasFlag("d1_solo")
    val warm = WorkplaceState.hasFlag("d1_warm")

    // 主管信任分桶:決定整體語氣
    val bucket = when {
        trust >= 6 -> Bucket.HIGH
        trust <= 2 -> Bucket.LOW
        else -> Bucket.MID
    }

    val opening = when (bucket) {
        Bucket.HIGH -> "走出大樓,風是涼的。手機在口袋裡震了幾下。"
        Bucket.LOW -> "回到家,燈沒開就先坐下。手機螢幕亮著,三則。"
        Bucket.MID -> "捷運上。今天的對話還在腦裡轉。手機亮了。"
    }

    val kenMsg = when {
        bucket == Bucket.HIGH && warm ->
            "今天那關過了。難得你還問我壓力——輪不到你操心,但領情。週三中午,我記著。"
        bucket == Bucket.HIGH ->
            "今天講得清楚。週三中午前給我能跑的版本,就這樣。早點睡。"
        bucket == Bucket.LOW && dodged ->
            "把你今天說的日期,自己寫下來,現在就寄到我信箱。明天九點我要看到。"
        bucket == Bucket.LOW ->
            "今天那些話,我先記著。明天開始,我要看進度,不是看態度。"
        else ->
            "週三中午前,我要看到能跑的核心路徑。別再讓我用問的。"
    }

    val akaiMsg = when {
        askedAkai ->
            "欸,Ken 剛說你今天 1on1 提到要借我半天?沒問題,明天早上第一件事抓我。新人第一週就敢開口要人,可以喔。"
        solo ->
            "聽說你今天被 Ken 約談?還好吧。明天 sync 有什麼卡的,丟群組,別自己悶著。"
        else ->
            "明天的 sync 你會到吧?有兩個地方想先跟你對一下,不然到時候又要重來。"
    }

    val momMsg = "下班了沒?不要又叫外送。冰箱有湯,熱十分鐘就好。早點睡,不要學你爸熬夜。"

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
            // 時間 + 旁白
            Text("週一 21:47", color = Color(0xCCFFF8F3), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text("那天晚上", color = PaperWhite, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(14.dp))
            Text(opening, color = Color(0x99FFF8F3), fontSize = 14.sp, lineHeight = 22.sp)
            Spacer(Modifier.height(24.dp))

            // 三則訊息(可點開)
            NightMsgCard("Ken", "NovaChat", kenMsg, Color(0xFFB85C3A))
            Spacer(Modifier.height(12.dp))
            NightMsgCard("阿凱", "#product", akaiMsg, Color(0xFF5BB6A6))
            Spacer(Modifier.height(12.dp))
            NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))

            Spacer(Modifier.height(28.dp))
            Text(
                "睡前最後一個念頭:明天的信箱,大概不會太安靜。",
                color = Color(0x80FFF8F3), fontSize = 13.sp, lineHeight = 21.sp,
            )
            Spacer(Modifier.height(20.dp))

            // 收尾:放下手機 → 回到沙盒路徑
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(BrandOrange)
                    .pressScale { navController.popBackStack(Routes.WORKPLACE_SANDBOX, inclusive = false) },
                contentAlignment = Alignment.Center,
            ) {
                Text("把手機放下,睡了", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}

private enum class Bucket { HIGH, MID, LOW }

/** 鎖屏訊息卡:預設只露一行,點一下展開全文(像睡前滑手機)。 */
@Composable
private fun NightMsgCard(sender: String, channel: String, text: String, accent: Color) {
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
