package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.navigation.Routes

/* =====================================================================
   WorkplaceHome —— 手機桌面(翻 app 階段)
   玩法迴圈：長官交代後回到桌面 → 這關要看的 app 有紅點(未讀)、發亮、可點;
   無關 app 變暗、點不動 → 翻完有紅點的 app(紅點清空) → 解鎖「做決定」。
   翻到的內容就是做決定的依據(進 app 看信/訊息/決議…)。
   ===================================================================== */

private data class HomeApp(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
)

@Composable
fun WorkplaceHome(
    navController: NavHostController,
    dayLabel: String,
    objective: String,
    relevantKeys: Set<String>,
    decisionLabel: String,
    decisionHint: String,
    onDecision: () -> Unit,
    unreadCounts: Map<String, Int> = emptyMap(),
) {
    val apps = listOf(
        HomeApp("chat", "訊息", Icons.Filled.Forum, Color(0xFF06C755), Routes.NOVA_CHAT_LIST),
        HomeApp("mail", "郵件", Icons.Filled.Email, Color(0xFFF2531C), Routes.NOVA_MAIL_INBOX),
        HomeApp("calendar", "行事曆", Icons.Filled.CalendarMonth, Color(0xFFC5392D), Routes.NOVA_CALENDAR),
        HomeApp("team", "團隊", Icons.Filled.Groups, Color(0xFF3B82F6), Routes.NOVA_TEAM),
        HomeApp("doc", "決議", Icons.Filled.Description, Color(0xFF8A5A38), Routes.NOVA_DOC),
        HomeApp("gram", "動態", Icons.Filled.PhotoCamera, Color(0xFFE0922A), Routes.NOVA_GRAM),
        HomeApp("meet", "會議", Icons.Filled.Videocam, Color(0xFF6366F1), Routes.NOVA_MEET),
        HomeApp("dashboard", "週報", Icons.Filled.BarChart, Color(0xFF2E9E6B), Routes.NOVA_DASHBOARD),
    )

    val unlocked = relevantKeys.isNotEmpty() && relevantKeys.all { WorkplaceState.isAppVisited(it) }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.phone_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x80000000), 0.3f to Color(0x45000000), 1f to Color(0xA6000000),
                ),
            ),
        )

        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp)) {
            Spacer(Modifier.height(52.dp))

            // ===== 目標 banner(長官交代了什麼、要翻哪些 app)=====
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Color(0xD1281C12)).padding(horizontal = 15.dp, vertical = 13.dp),
            ) {
                Text(dayLabel, color = Color(0xFFFFB627), fontSize = 11.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(Modifier.height(5.dp))
                Text(objective, color = Color(0xFFFFF8F3), fontSize = 13.sp, lineHeight = 20.sp)
            }

            Spacer(Modifier.height(24.dp))

            // ===== app 格(每列 4 個;相關=紅點+亮,無關=暗+點不動)=====
            apps.chunked(4).forEach { row ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { app ->
                        val relevant = app.key in relevantKeys
                        val visited = WorkplaceState.isAppVisited(app.key)
                        val unread = unreadCounts[app.key] ?: 0
                        val showDot = relevant && unread > 0 && !visited

                        val colMod = if (relevant) {
                            Modifier.weight(1f).clickable {
                                WorkplaceState.visitApp(app.key)
                                navController.navigate(app.route)
                            }
                        } else {
                            Modifier.weight(1f).alpha(0.34f)
                        }

                        Column(colMod, horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Box(
                                    Modifier.size(58.dp).clip(RoundedCornerShape(16.dp)).background(app.accent),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(app.icon, contentDescription = app.label, tint = Color.White,
                                        modifier = Modifier.size(28.dp))
                                }
                                if (showDot) {
                                    Box(
                                        Modifier.offset(x = 6.dp, y = (-6).dp).size(21.dp)
                                            .clip(CircleShape).background(Color(0xFFEF4444)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("$unread", color = Color.White, fontSize = 11.sp,
                                            fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(app.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            Spacer(Modifier.weight(1f))

            // ===== 做決定按鈕(翻完才解鎖)=====
            val btnColor = if (unlocked) Color(0xFFF2531C) else Color(0x66785A41)
            val btnMod = if (unlocked) Modifier.clickable { onDecision() } else Modifier
            Row(
                Modifier.fillMaxWidth().padding(bottom = 30.dp)
                    .clip(RoundedCornerShape(20.dp)).background(btnColor)
                    .then(btnMod)
                    .padding(horizontal = 20.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (unlocked) decisionLabel else "$decisionLabel（未解鎖）",
                        color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (unlocked) "資訊夠了，做決定" else decisionHint,
                        color = Color(0xCCFFFFFF), fontSize = 12.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier.size(42.dp).clip(CircleShape)
                        .background(if (unlocked) Color(0x33FFFFFF) else Color(0x1FFFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = null,
                        tint = Color.White.copy(alpha = if (unlocked) 1f else 0.5f),
                        modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
