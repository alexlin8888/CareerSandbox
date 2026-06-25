package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R

/* =====================================================================
   NovaGram 限時動態 —— 同事小芳「又是 deadline 週」的全屏 story
   通用限時動態語言：頂部進度條 / 發文者列 / 貼圖字卡 / 定位 / 底部互動。
   背景用暖橘漸層自繪（不依賴外部夜景圖、不新增 drawable），不碰真品牌。
   ===================================================================== */

@Composable
fun NovaGramScreen(navController: NavHostController) {
    Box(
        Modifier.fillMaxSize().background(
            // 暖橘 → espresso 夜間辦公氛圍漸層
            Brush.linearGradient(listOf(Color(0xFFC97B3C), Color(0xFF8A4A24), Color(0xFF2A1B10))),
        ),
    ) {
        // 上下壓暗，確保文字可讀
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.00f to Color(0x80000000),
                    0.18f to Color(0x00000000),
                    0.66f to Color(0x00000000),
                    1.00f to Color(0x99000000),
                ),
            ),
        )

        // ===== 頂部：進度條 + 發文者列 =====
        Column(Modifier.fillMaxWidth().align(Alignment.TopStart).padding(top = 14.dp, start = 12.dp, end = 12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                StoryProgress(1f)            // 已看完
                StoryProgress(0.42f)         // 進行中
                StoryProgress(0f)
                StoryProgress(0f)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // IG 風漸層環 + 立繪
                Box(
                    Modifier.size(34.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFFEDA75), Color(0xFFD62976), Color(0xFF962FBF))))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) { NovaCircleAvatar(size = 30.dp, res = R.drawable.colleague_gossip) }
                Spacer(Modifier.width(10.dp))
                Text("小芳", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text("2小時", color = Color(0xD9FFFFFF), fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                NovaKebabIcon(Color.White, 18.dp)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Outlined.Close, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }

        // ===== 中央貼圖字卡 =====
        Box(
            Modifier.align(Alignment.Center).rotate(-3f).clip(RoundedCornerShape(8.dp))
                .background(Color(0x52000000)).padding(horizontal = 18.dp, vertical = 11.dp),
        ) {
            Text("又是 deadline 週", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 23.sp)
        }

        // ===== 定位貼紙 =====
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 24.dp, top = 120.dp)
                .clip(RoundedCornerShape(12.dp)).background(Color(0x26FFFFFF))
                .padding(horizontal = 11.dp, vertical = 7.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NovaLocationIcon(Color.White, 15.dp)
                Spacer(Modifier.width(5.dp))
                Text("NovaPay 辦公室", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

        // ===== 底部互動列 =====
        Row(
            Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(start = 14.dp, end = 14.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(50))
                    .background(Color(0x1FFFFFFF)).padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart,
            ) { Text("傳送訊息", color = Color(0xE6FFFFFF), fontSize = 14.sp) }
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Outlined.Favorite, null, tint = Color.White, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Outlined.Send, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

/** 限時動態頂部單段進度條。 */
@Composable
private fun RowScope.StoryProgress(progress: Float) {
    Box(
        Modifier.weight(1f).height(2.5.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x66FFFFFF)),
    ) {
        if (progress > 0f) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(progress).background(Color.White))
        }
    }
}
