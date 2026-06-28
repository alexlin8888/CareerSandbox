package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   行事曆 日檢視 —— Day3 衝刺週
   採 agenda（事件清單）版型，避免時間格絕對定位；列高放寬、副標兩行可容。
   現在時間紅線標於當前事件（讀分帳 spec）。
   ===================================================================== */

private data class CalEvent(
    val title: String,
    val sub: String,
    val bar: Color,
    val current: Boolean = false,
    val note: String = "",
)

private val calEvents = listOf(
    CalEvent("與 Ken 的 1on1", "09:00–09:30 · 會議室 A", AccentBlue,
        note = "帶上分帳問題的初步判斷，Ken 會問你怎麼看。"),
    CalEvent("Sprint 站會", "11:00–11:15 · 線上", AccentGreen,
        note = "簡短同步進度；阿哲可能會提排程風險。"),
    CalEvent("午餐 · 小芳", "12:00", BrandAmber,
        note = "認識同事的好機會，別整頓飯都在講工作。"),
    CalEvent("讀分帳 spec", "14:00–15:30 · 專注時段", BrandOrange, current = true,
        note = "今天的重點：把分帳邏輯讀透，下午要做判斷。"),
    CalEvent("回 Vivian", "16:00 · 客戶需求", Color(0xFF8B5CF6),
        note = "客戶 demo 在催，回覆前先確認工程實際可行的範圍。"),
)

private data class WeekDay(val label: String, val num: String, val selected: Boolean)

private val weekDays = listOf(
    WeekDay("日", "22", false), WeekDay("一", "23", true), WeekDay("二", "24", false),
    WeekDay("三", "25", false), WeekDay("四", "26", false), WeekDay("五", "27", false),
    WeekDay("六", "28", false),
)

@Composable
fun NovaCalendarScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().background(PaperWhite)) {

        // ===== 月份 =====
        Text("六月", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 22.sp,
            modifier = Modifier.padding(start = 18.dp, top = 16.dp, bottom = 10.dp))

        // ===== 週列 =====
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
            weekDays.forEach { d ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(d.label, color = InkGray400, fontSize = 11.sp)
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier.size(34.dp).clip(CircleShape)
                            .background(if (d.selected) BrandOrange else Color.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(d.num, color = if (d.selected) PaperWhite else InkGray700,
                            fontSize = 14.sp, fontWeight = if (d.selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))

        // ===== 全天 banner =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("全天", color = InkGray400, fontSize = 11.sp, modifier = Modifier.width(44.dp))
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF1E6))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) { Text("分帳專案衝刺週", color = BrandDeepOrange, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))

        // ===== Agenda 事件 =====
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll).padding(vertical = 8.dp)) {
            calEvents.forEach { e ->
                var expanded by remember(e.title) { mutableStateOf(false) }
                if (e.current) {
                    // 現在時間紅線
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("14:02", color = AccentRed, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(40.dp))
                        Box(Modifier.size(7.dp).clip(CircleShape).background(AccentRed))
                        Box(Modifier.weight(1f).height(1.5.dp).background(AccentRed))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (e.current) Color(0xFFFFF1E6) else PaperOff)
                            .heightIn(min = 58.dp)
                            .clickable { expanded = !expanded },
                    ) {
                        Row(Modifier.fillMaxSize()) {
                            Box(Modifier.width(4.dp).fillMaxHeight().background(e.bar))
                            Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                                Text(e.title, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(e.sub, color = InkGray500, fontSize = 12.sp)
                                if (expanded && e.note.isNotEmpty()) {
                                    Spacer(Modifier.height(7.dp))
                                    Text(e.note, color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ===== 底部導覽 =====
        Row(
            modifier = Modifier.fillMaxWidth().background(PaperWhite).padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NovaCalendarIcon(BrandOrange, 24.dp, filled = true)
                Text("今天", color = BrandOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NovaCalendarIcon(InkGray400, 24.dp)
                Text("行事曆", color = InkGray400, fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Inbox, null, tint = InkGray400, modifier = Modifier.size(24.dp))
                Text("收件匣", color = InkGray400, fontSize = 10.sp)
            }
        }
    }
}
