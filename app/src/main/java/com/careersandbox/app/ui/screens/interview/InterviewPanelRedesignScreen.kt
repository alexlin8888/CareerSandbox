package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
   面試頁改版預覽(Panel)— 兩個不靠圓框堆疊的方向,頂部切換
   方向 A 場景式:暖黑場景 + 當前發問者立繪(固定高度)+ 名牌條(非圓框)+ 問題對話框 + 作答選項。
                與沙盒場景對話同一套語言,沉浸、不像聊天機器人。
   方向 B 視訊格:三個矩形視訊格(發問者高亮 + 發問中標籤),取代一排圓頭像;像真實遠端 panel。
   皆 mock 資料(3 位面試官 + 一題),純展示版型給你挑;不接邏輯。
   ===================================================================== */

private data class Panelist(val name: String, val role: String, val drawable: Int, val accent: Color)

private val mockPanel = listOf(
    Panelist("陳怡君", "HR 主管", R.drawable.interviewer_hr, BrandAmber),
    Panelist("林志豪", "技術主管", R.drawable.interviewer_tech, BrandOrange),
    Panelist("王思婷", "用人主管", R.drawable.interviewer_lead, BrandDeepOrange),
)
private const val MOCK_Q = "可以說說那個專案裡,你遇到最大的技術取捨是什麼?最後是怎麼決定的?"
private val MOCK_OPTS = listOf(
    "先講當時的限制條件,再帶到我的取捨",
    "從對使用者的影響切入,說明為何這樣選",
    "直接講最後的決策與量化結果",
)

@Composable
fun InterviewPanelRedesignScreen(navController: NavHostController) {
    var direction by remember { mutableIntStateOf(0) }   // 0 場景式 / 1 視訊格
    val askerIdx = 1                                      // 目前發問者:技術主管
    val dark = direction == 0

    Column(Modifier.fillMaxSize().background(if (dark) InkCharcoal else PaperOff)) {
        // ===== 頂列:返回 + 標題 + 方向切換 =====
        Row(
            Modifier.fillMaxWidth()
                .background(if (dark) InkCharcoal else PaperWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                    .background(if (dark) Color(0x33FFFFFF) else PaperOff)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = if (dark) PaperWhite else InkBlack, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("面試頁改版預覽", color = if (dark) PaperWhite else InkBlack, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Panel · 不靠圓框堆疊", color = if (dark) PaperWhite.copy(alpha = 0.6f) else InkGray500, fontSize = 11.sp)
            }
            SegToggle(direction) { direction = it }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (dark) SceneDirection(askerIdx) else VideoGridDirection(askerIdx)
        }
    }
}

@Composable
private fun SegToggle(active: Int, onSel: (Int) -> Unit) {
    Row(
        Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0x22808080)).padding(3.dp),
    ) {
        listOf("場景式", "視訊格").forEachIndexed { i, label ->
            Box(
                Modifier.clip(RoundedCornerShape(999.dp))
                    .background(if (active == i) BrandOrange else Color.Transparent)
                    .clickable { onSel(i) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(label, color = if (active == i) PaperWhite else InkGray400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ===== 方向 A:場景式 ===== */
@Composable
private fun SceneDirection(askerIdx: Int) {
    val asker = mockPanel[askerIdx]
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF2A2018), Color(0xFF14100B)))),
    ) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            // 面試小組名牌條(橫向名片,非圓框;發問者高亮)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                mockPanel.forEachIndexed { i, p ->
                    val on = i == askerIdx
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (on) Color(0x33FFB627) else Color(0x1AFFFFFF))
                            .border(if (on) 1.dp else 0.dp, if (on) p.accent else Color.Transparent, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Text(p.role, color = if (on) p.accent else PaperWhite.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(p.name, color = PaperWhite.copy(alpha = 0.55f), fontSize = 10.sp)
                    }
                }
            }
            // 當前發問者立繪(固定高度,各人一致)
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(asker.drawable),
                        contentDescription = asker.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(220.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(asker.accent).padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(6.dp).clip(RoundedCornerShape(999.dp)).background(InkBlack.copy(alpha = 0.55f)))
                        Spacer(Modifier.width(7.dp))
                        Text("${asker.name} · ${asker.role}  發問中", color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // 問題對話框(amber 名牌 + 問題)
            Box {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(Color(0xDB1C160F))
                        .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 18.dp),
                ) {
                    Text(MOCK_Q, color = PaperWhite, fontSize = 15.sp, lineHeight = 24.sp)
                }
                Box(
                    Modifier.align(Alignment.TopStart).offset(x = 18.dp, y = (-11).dp)
                        .clip(RoundedCornerShape(999.dp)).background(asker.accent)
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text(asker.role, color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(14.dp))
            // 作答選項
            MOCK_OPTS.forEachIndexed { i, opt ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PaperWhite)
                        .clickable { }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(26.dp).clip(RoundedCornerShape(999.dp)).background(Color(0x1FF2531C)), contentAlignment = Alignment.Center) {
                        Text(('A' + i).toString(), color = BrandOrange, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(opt, color = InkSlate, fontSize = 14.sp, lineHeight = 20.sp)
                }
                Spacer(Modifier.height(9.dp))
            }
        }
    }
}

/* ===== 方向 B:視訊格 ===== */
@Composable
private fun VideoGridDirection(askerIdx: Int) {
    val asker = mockPanel[askerIdx]
    Column(Modifier.fillMaxSize().background(PaperOff).padding(16.dp)) {
        Text("遠端面試 · 3 位面試官", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 15.sp)
        Spacer(Modifier.height(2.dp))
        Text("即時視訊格,發問者高亮 — 取代一排圓頭像", color = InkGray500, fontSize = 11.sp)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            mockPanel.forEachIndexed { i, p ->
                VideoTile(p, on = i == askerIdx, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(PaperWhite).padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(RoundedCornerShape(999.dp)).background(asker.accent))
                Spacer(Modifier.width(8.dp))
                Text("${asker.name} · ${asker.role}", color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(MOCK_Q, color = InkSlate, fontSize = 15.sp, lineHeight = 24.sp)
        }
        Spacer(Modifier.height(16.dp))
        MOCK_OPTS.forEachIndexed { i, opt ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .border(1.dp, InkGray200, RoundedCornerShape(14.dp)).background(PaperWhite)
                    .clickable { }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(('A' + i).toString(), color = BrandOrange, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(12.dp))
                Text(opt, color = InkSlate, fontSize = 14.sp)
            }
            Spacer(Modifier.height(9.dp))
        }
    }
}

@Composable
private fun VideoTile(p: Panelist, on: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier.aspectRatio(0.74f).clip(RoundedCornerShape(16.dp)).background(InkCharcoal)
            .border(if (on) 2.dp else 0.dp, if (on) BrandOrange else Color.Transparent, RoundedCornerShape(16.dp)),
    ) {
        Image(
            painter = painterResource(p.drawable),
            contentDescription = p.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            Column {
                Text(p.role, color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(p.name, color = PaperWhite.copy(alpha = 0.7f), fontSize = 9.sp)
            }
        }
        if (on) {
            Box(
                Modifier.align(Alignment.TopStart).padding(6.dp)
                    .clip(RoundedCornerShape(999.dp)).background(BrandOrange)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text("發問中", color = PaperWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
