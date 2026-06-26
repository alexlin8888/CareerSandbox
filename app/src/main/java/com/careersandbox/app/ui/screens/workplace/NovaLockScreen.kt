package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   鎖屏通知 —— Day3 早晨：訊息 / 信件 / 行事曆同時湧入
   深 espresso 漸層 + 霧面通知卡。系統時鐘列由系統提供，這裡畫大時鐘本體。
   ===================================================================== */

private data class LockNotif(
    val app: String,
    val time: String,
    val title: String,
    val body: String,
    val iconBg: Color,
    val kind: Int, // 0 chat / 1 mail / 2 calendar
)

private val lockNotifs = listOf(
    LockNotif("NovaChat", "現在", "#product", "阿哲、Vivian 等 3 人傳送了 12 則新訊息", BrandOrange, 0),
    LockNotif("NovaMail · Ken", "5 分鐘前", "分帳的事", "聽說分帳功能有狀況。今天 5 點前…", Color(0xFF3B82F6), 1),
    LockNotif("行事曆", "10 分前", "14:00 讀分帳 spec", "專注時段", Color(0xFFEF4444), 2),
)

@Composable
fun NovaLockScreen(navController: NavHostController) {
    val frost = Color.White.copy(alpha = 0.12f)

    Column(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Espresso, EspressoDeep))),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))

        // ===== 鎖頭 =====
        Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(18.dp))

        // ===== 大時鐘 =====
        Text("14:02", color = Color.White, fontWeight = FontWeight.Light, fontSize = 78.sp)
        Text("6月23日 星期一", color = Color.White.copy(alpha = 0.85f), fontSize = 16.sp)
        Spacer(Modifier.height(40.dp))

        // ===== 通知卡 =====
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            lockNotifs.forEach { n ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(frost).padding(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(n.iconBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (n.kind) {
                            0 -> NovaBubbleIcon(Color.White, 20.dp, filled = true)
                            1 -> Icon(Icons.Outlined.MailOutline, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            else -> NovaCalendarIcon(Color.White, 20.dp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(n.app, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                                modifier = Modifier.weight(1f), maxLines = 1)
                            Text(n.time, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(n.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(n.body, color = Color.White.copy(alpha = 0.78f), fontSize = 12.sp, lineHeight = 17.sp, maxLines = 2)
                    }
                }
            }

            // 顯示更多
            Row(
                Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("顯示更多（4）", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(30.dp))

        // ===== 底部快捷（手電筒 / 相機）=====
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 50.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                NovaFlashlightIcon(Color.White, 24.dp)
            }
            Box(Modifier.size(52.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Videocam, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}
