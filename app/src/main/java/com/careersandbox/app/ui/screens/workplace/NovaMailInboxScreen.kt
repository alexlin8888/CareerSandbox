package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Videocam
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
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   NovaMail 收件匣 —— Day3 Ken 的決議信進來
   通用郵件清單語言：星號 / 重要菱形 / 附件 / 標籤；未讀粗體、已讀灰。
   ===================================================================== */

private data class NovaMailRow(
    val sender: String,
    val subject: String,
    val prev: String,
    val time: String,
    val unread: Boolean,
    val star: Boolean,
    val imp: Boolean,
    val attach: Boolean,
    val res: Int? = null,
    val letter: String = "",
    val avBg: Color = Color(0xFFCBD5E1),
    val label: String = "",
    val labelBg: Color = Color.Transparent,
    val labelCol: Color = Color.Transparent,
)

private val novaMailRows = listOf(
    NovaMailRow("Ken", "分帳的事", "聽說分帳功能有狀況。今天 5 點前…", "14:04", true, true, true, true, res = R.drawable.ken_neutral),
    NovaMailRow("Vivian", "客戶下週要 demo", "他們董事會已經排好時間了…", "13:50", true, false, true, false, res = R.drawable.colleague_vivian),
    NovaMailRow("CI 建置通知", "[wallet] build #4821 失敗", "pipeline failed at integration-test…", "10:32", true, false, false, false,
        letter = "CI", avBg = Color(0xFF64748B), label = "工作", labelBg = Color(0xFFFCE8E6), labelCol = Color(0xFFC5392D)),
    NovaMailRow("NovaPay HR", "本週五團隊午餐", "記得回覆出席與飲食偏好…", "11:20", false, false, false, false,
        letter = "HR", avBg = Color(0xFF3B82F6)),
    NovaMailRow("雲端服務電子報", "限時優惠：年費 5 折", "升級 Pro 享更多儲存空間…", "昨天", false, false, false, false,
        letter = "雲", avBg = Color(0xFF10B981), label = "促銷內容", labelBg = Color(0xFFE6F4EA), labelCol = Color(0xFF1E8E5A)),
)

@Composable
fun NovaMailInboxScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize().background(PaperWhite)) {
        Column(Modifier.fillMaxSize()) {

            // ===== 搜尋列 =====
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(50)).background(PaperOff)
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NovaSearchIcon(InkGray500, 18.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("在郵件中搜尋", color = InkGray500, fontSize = 13.sp)
                }
                Spacer(Modifier.width(10.dp))
                NovaCircleAvatar(size = 32.dp, letter = "你", bg = BrandOrange)
            }

            // ===== 分類 tabs =====
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                NovaMailTab("主要", selected = true, modifier = Modifier.weight(1f))
                NovaMailTab("促銷內容", selected = false, modifier = Modifier.weight(1f))
                NovaMailTab("社交網路", selected = false, modifier = Modifier.weight(1f))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray200))

            // ===== 郵件列表 =====
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)) {
                novaMailRows.forEach { m ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        NovaCircleAvatar(size = 42.dp, res = m.res, letter = m.letter, bg = m.avBg)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    m.sender, modifier = Modifier.weight(1f),
                                    color = if (m.unread) InkBlack else InkGray500,
                                    fontWeight = if (m.unread) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp, maxLines = 1,
                                )
                                if (m.imp) { NovaDiamond(BrandAmber, 9.dp); Spacer(Modifier.width(4.dp)) }
                                Text(m.time, color = if (m.unread) BrandDeepOrange else InkGray400, fontSize = 11.sp,
                                    fontWeight = if (m.unread) FontWeight.SemiBold else FontWeight.Normal)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                m.subject, color = if (m.unread) InkBlack else InkGray700,
                                fontWeight = if (m.unread) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp, maxLines = 1,
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(m.prev, modifier = Modifier.weight(1f), color = InkGray400, fontSize = 12.sp, maxLines = 1)
                                if (m.attach) { Spacer(Modifier.width(6.dp)); NovaClipIcon(InkGray400, 15.dp) }
                            }
                            if (m.label.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                Box(
                                    Modifier.clip(RoundedCornerShape(6.dp)).background(m.labelBg)
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) { Text(m.label, color = m.labelCol, fontSize = 10.sp, fontWeight = FontWeight.Medium) }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.Star, contentDescription = null,
                            tint = if (m.star) BrandAmber else InkGray300, modifier = Modifier.size(18.dp),
                        )
                    }
                    Box(Modifier.fillMaxWidth().padding(start = 70.dp).height(1.dp).background(InkGray100))
                }
                Spacer(Modifier.height(80.dp))
            }

            // ===== 底部導覽 =====
            Row(
                modifier = Modifier.fillMaxWidth().background(PaperWhite).padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Inbox, null, tint = BrandOrange, modifier = Modifier.size(24.dp))
                        Text("收件匣", color = BrandOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        Modifier.offset(x = 14.dp, y = (-4).dp).clip(CircleShape).background(BrandOrange)
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                    ) { Text("99+", color = PaperWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold) }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Videocam, null, tint = InkGray400, modifier = Modifier.size(24.dp))
                    Text("Meet", color = InkGray400, fontSize = 10.sp)
                }
            }
        }

        // ===== 撰寫 FAB =====
        Box(
            Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 78.dp)
                .size(56.dp).clip(CircleShape).background(BrandOrange),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Edit, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(24.dp)) }
    }
}

@Composable
private fun NovaMailTab(text: String, selected: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text, color = if (selected) BrandOrange else InkGray500, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(2.dp).background(if (selected) BrandOrange else Color.Transparent))
    }
}
