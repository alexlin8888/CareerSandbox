package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   NovaTeam #product —— Day3 工程頻道炸鍋
   通用團隊頻道語言（方圓角頭像、reaction、thread）；刻意不採任何 IM 品牌色。
   ===================================================================== */

@Composable
fun NovaTeamScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().background(PaperWhite)) {

        // ===== 頂部：頻道 + 成員 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
            }
            Column(Modifier.weight(1f)) {
                Text("# product", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("分帳專案 · 8 位成員", color = InkGray500, fontSize = 11.sp)
            }
            NovaSearchIcon(InkGray700, 20.dp, modifier = Modifier.padding(end = 6.dp))
            Spacer(Modifier.width(8.dp))
            NovaKebabIcon(InkGray700, 20.dp)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))

        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll).padding(horizontal = 16.dp, vertical = 12.dp)) {

            // 今天 分隔
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.clip(RoundedCornerShape(50)).background(InkGray100).padding(horizontal = 14.dp, vertical = 4.dp)) {
                    Text("今天", color = InkGray500, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(16.dp))

            // ----- 阿哲 -----
            TeamMessage(R.drawable.colleague_akai_frustrated, "阿哲", "上午 10:32", "金流 bug 還沒解，race condition 卡住了") {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ReactionPill("2")
                    ReactionPill("1")
                }
            }
            Spacer(Modifier.height(16.dp))

            // ----- Vivian（含 thread）-----
            TeamMessage(R.drawable.colleague_vivian, "Vivian", "上午 10:41", "客戶下週要 demo，這個一定要上") {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NovaSquareAvatar(size = 20.dp, res = R.drawable.colleague_quiet, corner = 6.dp)
                    Spacer(Modifier.width(3.dp))
                    NovaSquareAvatar(size = 20.dp, res = R.drawable.ken_neutral, corner = 6.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("2 則回覆", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Text("最後回覆 10:43", color = InkGray400, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(16.dp))

            // ----- Ken（@你）-----
            TeamMessage(R.drawable.ken_neutral, "Ken", "上午 10:45", null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFF1E6)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                        Text("@你", color = BrandDeepOrange, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("看一下你的判斷，", color = InkSlate, fontSize = 14.sp)
                }
                Text("下午會議給個方向。", color = InkSlate, fontSize = 14.sp)
            }
        }

        // ===== 輸入列 =====
        Column(Modifier.fillMaxWidth().background(PaperWhite).padding(horizontal = 14.dp, vertical = 10.dp)) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PaperOff).padding(12.dp)) {
                Text("傳訊息給 #product", color = InkGray500, fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FormatGlyph("B", FontWeight.Bold)
                Spacer(Modifier.width(14.dp))
                FormatGlyph("I", FontWeight.Normal, italic = true)
                Spacer(Modifier.width(14.dp))
                Text("@", color = InkGray500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(36.dp).clip(CircleShape).background(BrandOrange), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Send, null, tint = PaperWhite, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamMessage(res: Int, name: String, time: String, text: String?, extra: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        NovaSquareAvatar(size = 40.dp, res = res, corner = 10.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text(time, color = InkGray400, fontSize = 11.sp)
            }
            Spacer(Modifier.height(3.dp))
            if (text != null) Text(text, color = InkSlate, fontSize = 14.sp, lineHeight = 20.sp)
            extra()
        }
    }
}

@Composable
private fun ReactionPill(count: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(PaperOff)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(InkGray400))
        Spacer(Modifier.width(5.dp))
        Text(count, color = InkGray700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FormatGlyph(t: String, weight: FontWeight, italic: Boolean = false) {
    Text(t, color = InkGray500, fontSize = 14.sp, fontWeight = weight,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal)
}
