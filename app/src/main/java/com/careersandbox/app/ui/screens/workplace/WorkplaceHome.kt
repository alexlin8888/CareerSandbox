package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.careersandbox.app.navigation.Routes

/* =====================================================================
   WorkplaceHome —— 解鎖後的手機桌面(自由探索)
   app 圖示導到既有 Nova 畫面(NavHost 已有路由);今日任務卡進當天場景。
   這就是使用者要的「自由度」:不只線性,可先點開行事曆/決議/訊息看脈絡再開工。
   ===================================================================== */

private data class HomeApp(val label: String, val icon: ImageVector, val accent: Color, val route: String)

@Composable
fun WorkplaceHome(
    navController: NavHostController,
    taskTitle: String,
    taskSubtitle: String,
    onStartTask: () -> Unit,
    dateLabel: String = "6月23日 星期一",
) {
    val apps = listOf(
        HomeApp("訊息", Icons.Filled.Forum, Color(0xFF06C755), Routes.NOVA_CHAT_LIST),
        HomeApp("郵件", Icons.Filled.Email, Color(0xFFF2531C), Routes.NOVA_MAIL_INBOX),
        HomeApp("行事曆", Icons.Filled.CalendarMonth, Color(0xFFC5392D), Routes.NOVA_CALENDAR),
        HomeApp("團隊", Icons.Filled.Groups, Color(0xFF3B82F6), Routes.NOVA_TEAM),
        HomeApp("決議", Icons.Filled.Description, Color(0xFF8A5A38), Routes.NOVA_DOC),
        HomeApp("動態", Icons.Filled.PhotoCamera, Color(0xFFE0922A), Routes.NOVA_GRAM),
        HomeApp("會議", Icons.Filled.Videocam, Color(0xFF6366F1), Routes.NOVA_MEET),
        HomeApp("週報", Icons.Filled.BarChart, Color(0xFF2E9E6B), Routes.NOVA_DASHBOARD),
    )

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
                    0f to Color(0x73000000), 0.3f to Color(0x40000000), 1f to Color(0x99000000),
                ),
            ),
        )

        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp)) {
            Spacer(Modifier.height(56.dp))
            Text("早安，新人", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(dateLabel, color = Color(0xCCFFFFFF), fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(28.dp))
            // app 格(每列 4 個)
            apps.chunked(4).forEach { row ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { app ->
                        Column(
                            Modifier.weight(1f).clickable { navController.navigate(app.route) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                Modifier.size(58.dp).clip(RoundedCornerShape(16.dp)).background(app.accent),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(app.icon, contentDescription = app.label, tint = Color.White,
                                    modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(app.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    // 補滿不足 4 格的空位,維持對齊
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            Spacer(Modifier.weight(1f))
            // 今日任務卡
            Row(
                Modifier.fillMaxWidth().padding(bottom = 30.dp)
                    .clip(RoundedCornerShape(20.dp)).background(Color(0xFFF2531C))
                    .clickable { onStartTask() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("今日任務", color = Color(0xCCFFFFFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text(taskTitle, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    Text(taskSubtitle, color = Color(0xE6FFFFFF), fontSize = 13.sp)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier.size(42.dp).clip(RoundedCornerShape(999.dp)).background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "開始", tint = Color.White,
                        modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
