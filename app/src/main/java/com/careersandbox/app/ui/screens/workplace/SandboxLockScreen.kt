package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R

/* =====================================================================
   SandboxLockScreen —— 鎖屏開場(忠於設計稿)
   通知就是這週的故事鉤子:Ken 的分帳信、14:30 會議、Vivian 客戶 demo 焦慮。
   開機 → 看見一整週的壓力 → 點一下/滑開進入。
   桌布用 phone_wallpaper(使用者之後可換真桌布,同名覆蓋)。
   ===================================================================== */

private data class LockHook(
    val icon: ImageVector, val accent: Color,
    val app: String, val time: String, val title: String, val preview: String,
)

@Composable
fun SandboxLockScreen(onUnlock: () -> Unit) {
    val notifs = listOf(
        LockHook(Icons.Filled.Forum, Color(0xFF06C755), "團隊聊天", "現在",
            "#product · 阿哲、Vivian 等 3 人", "傳送了 12 則新訊息"),
        LockHook(Icons.Filled.Email, Color(0xFFF2531C), "郵件 · Ken", "5 分鐘前",
            "分帳的事", "聽說分帳功能有狀況。今天下班前給我一個說法…"),
        LockHook(Icons.Filled.CalendarMonth, Color(0xFFC5392D), "行事曆", "10 分鐘前",
            "14:30 產品進度會議", "會議室 B · 即將開始"),
        LockHook(Icons.Filled.Forum, Color(0xFF06C755), "團隊聊天", "12 分鐘前",
            "Vivian", "客戶下週就要 demo 啊，怎麼辦"),
    )

    Box(
        Modifier.fillMaxSize().clickable { onUnlock() },
    ) {
        // 桌布 + 暖色調 + 上下加深
        Image(
            painter = painterResource(R.drawable.phone_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x99583A22), 0.35f to Color(0x40583A22),
                    0.75f to Color(0x40000000), 1f to Color(0xB3000000),
                ),
            ),
        )

        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            // 狀態列
            Row(
                Modifier.fillMaxWidth().padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("遠傳電信 4G+", color = Color(0xCCFFFFFF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Text("78%", color = Color(0xCCFFFFFF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(40.dp))
            // 大時鐘
            Text("14:02", color = Color.White, fontSize = 76.sp, fontWeight = FontWeight.Light)
            Text("6月23日 星期一 · 你的第一週", color = Color(0xE6FFFFFF), fontSize = 15.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(30.dp))
            // 通知堆疊(霧面卡)
            notifs.forEach { n ->
                NotifCard(n)
                Spacer(Modifier.height(10.dp))
            }
            Text("▾ 還有 4 則通知", color = Color(0xB3FFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp))

            Spacer(Modifier.weight(1f))
            // 解鎖提示
            Column(
                Modifier.fillMaxWidth().padding(bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = Color(0xB3FFFFFF),
                    modifier = Modifier.size(26.dp))
                Text("滑動解鎖，開始這一天", color = Color(0xCCFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun NotifCard(n: LockHook) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color(0x33FFFFFF))
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(n.accent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(n.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(n.app, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text("· ${n.time}", color = Color(0x99FFFFFF), fontSize = 12.sp)
            }
            Spacer(Modifier.height(2.dp))
            Text(n.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 19.sp)
            Text(n.preview, color = Color(0xCCFFFFFF), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
