package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   NovaMeet 視訊 —— Day3 分帳上線會議
   通用視訊版型（人物格 / 主持人・舉手徽章 / 字幕 / 控制列）；不採 Meet 品牌。
   離開鈕：乾淨紅圓 + 簡單 icon（避免 mock 翻轉 glyph 的怪異感）。
   ===================================================================== */

@Composable
fun NovaMeetScreen(navController: NavHostController) {
    val tileBg = Color(0xFF1A2230)

    Column(Modifier.fillMaxSize().background(Color(0xFF0E1420))) {

        // ===== 頂部資訊列 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("分帳上線會議", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0x33EF4444))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(AccentRed))
                        Spacer(Modifier.width(4.dp))
                        Text("REC", color = AccentRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text("NovaPay 產品組 · 4 人", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Text("12:05", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        // ===== 2×2 人物格 =====
        Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp)) {
            Row(Modifier.weight(1f).fillMaxWidth()) {
                MeetTile(Modifier.weight(1f), R.drawable.ken_neutral, "Ken", tileBg, host = true)
                Spacer(Modifier.width(10.dp))
                MeetTile(Modifier.weight(1f), R.drawable.colleague_akai_calm, "阿凱", tileBg)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.weight(1f).fillMaxWidth()) {
                MeetTile(Modifier.weight(1f), R.drawable.colleague_vivian, "Vivian", tileBg, hand = true)
                Spacer(Modifier.width(10.dp))
                MeetTile(Modifier.weight(1f), null, "你", tileBg, camOff = true)
            }
        }

        // ===== 字幕 =====
        Box(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.4f)).padding(12.dp),
        ) {
            Text("Ken：那我們先聽聽 PM 這邊的判斷…", color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)
        }

        // ===== 控制列 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MeetCtrl { Icon(Icons.Outlined.Mic, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
            MeetCtrl { Icon(Icons.Outlined.Videocam, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
            MeetCtrl { NovaHandIcon(Color.White, 22.dp) }
            MeetCtrl { NovaKebabIcon(Color.White, 20.dp) }
            Spacer(Modifier.weight(1f))
            // 離開：乾淨紅圓 + 簡單 icon
            Box(
                Modifier.size(54.dp).clip(CircleShape).background(AccentRed)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Close, contentDescription = "離開", tint = Color.White, modifier = Modifier.size(26.dp)) }
        }
    }
}

@Composable
private fun MeetTile(
    modifier: Modifier,
    res: Int?,
    name: String,
    bg: Color,
    host: Boolean = false,
    hand: Boolean = false,
    camOff: Boolean = false,
) {
    Box(modifier.fillMaxHeight().clip(RoundedCornerShape(14.dp)).background(bg)) {
        if (res != null && !camOff) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // 鏡頭關閉：暗格 + 名字圓
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF374151)), contentAlignment = Alignment.Center) {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }

        // 名牌
        Box(
            Modifier.align(Alignment.BottomStart).padding(8.dp)
                .clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) { Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium) }

        // 主持人徽章
        if (host) {
            Box(
                Modifier.align(Alignment.TopStart).padding(8.dp)
                    .clip(RoundedCornerShape(50)).background(BrandOrange).padding(horizontal = 8.dp, vertical = 2.dp),
            ) { Text("主持人", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        }
        // 舉手徽章
        if (hand) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(8.dp)
                    .size(26.dp).clip(CircleShape).background(BrandAmber),
                contentAlignment = Alignment.Center,
            ) { NovaHandIcon(Color.White, 16.dp) }
        }
        // 鏡頭關閉徽章
        if (camOff) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(8.dp)
                    .clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.5f)).padding(horizontal = 7.dp, vertical = 2.dp),
            ) { Text("鏡頭關閉", color = Color.White.copy(alpha = 0.85f), fontSize = 9.sp) }
        }
    }
}

@Composable
private fun MeetCtrl(content: @Composable () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) { content() }
}
