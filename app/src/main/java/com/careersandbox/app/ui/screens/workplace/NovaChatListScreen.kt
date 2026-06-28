package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.data.mock.SandboxContentEngineProvider
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   NovaChat 聊天列表 —— Day2 訊息總覽
   主角吃既有立繪；群組/公告/媽用通用色塊頭像。系統列由系統提供，不自畫。
   ===================================================================== */

@Composable
fun NovaChatListScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().background(PaperWhite)) {

        // ===== 頂部：大標 + 搜尋 + 撰寫 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 14.dp, top = 14.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("聊天", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(40.dp).clip(CircleShape).background(PaperOff), contentAlignment = Alignment.Center) {
                NovaSearchIcon(InkGray700, 20.dp)
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(40.dp).clip(CircleShape).background(BrandOrange), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
            }
        }

        // ===== 篩選 chips =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NovaChip("全部", selected = true)
            NovaChip("未讀 12", selected = false)
            NovaChip("群組", selected = false)
        }
        Spacer(Modifier.height(6.dp))

        // ===== 列表 =====
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)) {
            SandboxContentEngineProvider.engine.chatRows(WorkplaceState.currentDay.value, WorkplaceState.flags.toList(), WorkplaceState.managerTrust.value, WorkplaceState.peerBond.value, WorkplaceState.proImage.value).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .pressScale { navController.navigate("${Routes.NOVA_CHAT}?id=${row.id}") }
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        if (row.group) {
                            Box(Modifier.size(50.dp).clip(CircleShape).background(row.bg), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Groups, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(26.dp))
                            }
                        } else {
                            NovaCircleAvatar(size = 50.dp, res = row.res, letter = row.letter, bg = row.bg)
                        }
                        if (row.online) {
                            Box(
                                Modifier.size(13.dp).clip(CircleShape).background(PaperWhite).align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center,
                            ) { Box(Modifier.size(9.dp).clip(CircleShape).background(AccentGreen)) }
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(row.name, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (row.pin) { Spacer(Modifier.width(6.dp)); NovaPinIcon(InkGray400, 13.dp) }
                            if (row.mute) { Spacer(Modifier.width(6.dp)); NovaMuteIcon(InkGray400, 14.dp) }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(row.prev, color = InkGray500, fontSize = 13.sp, maxLines = 1)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(row.time, color = InkGray400, fontSize = 11.sp)
                        Spacer(Modifier.height(6.dp))
                        if (row.unread.isNotEmpty() && !WorkplaceState.isItemRead("chat:${row.id}")) {
                            Box(
                                Modifier.defaultMinSize(minWidth = 20.dp).clip(CircleShape).background(BrandOrange)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center,
                            ) { Text(row.unread, color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        } else {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // ===== 底部導覽 =====
        NovaBottomBar()
    }
}

@Composable
private fun NovaChip(text: String, selected: Boolean) {
    Box(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (selected) InkBlack else PaperOff)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(text, color = if (selected) PaperWhite else InkGray700, fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun NovaBottomBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(PaperWhite).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NovaNavItem(active = false) { Icon(Icons.Outlined.Home, null, tint = InkGray400, modifier = Modifier.size(24.dp)); Text("主頁", color = InkGray400, fontSize = 10.sp) }
        NovaNavItem(active = true, badge = "12") { NovaBubbleIcon(BrandOrange, 24.dp, filled = true); Text("聊天", color = BrandOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) }
        NovaNavItem(active = false) { NovaFeedIcon(InkGray400, 24.dp); Text("動態", color = InkGray400, fontSize = 10.sp) }
        NovaNavItem(active = false) { Icon(Icons.Outlined.Person, null, tint = InkGray400, modifier = Modifier.size(24.dp)); Text("我的", color = InkGray400, fontSize = 10.sp) }
    }
}

@Composable
private fun NovaNavItem(active: Boolean, badge: String = "", content: @Composable () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) { content() }
        if (badge.isNotEmpty()) {
            Box(
                Modifier.offset(x = 10.dp, y = (-4).dp).defaultMinSize(minWidth = 16.dp).clip(CircleShape)
                    .background(BrandOrange).padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center,
            ) { Text(badge, color = PaperWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
