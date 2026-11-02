package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm
import androidx.compose.runtime.LaunchedEffect

private val DataLine = Color(0xFFEADFD6)
private val LimitChoices = listOf(
    "先問阿凱有沒有更早的資料",
    "先把這個記下來，繼續看其他欄位",
    "先用現有資料做初步分析",
    "先回小昀，確認這個欄位是不是必要",
)

@Composable
fun SandboxDataScreen(navController: NavHostController) {
    val state = SandboxV2State
    LaunchedEffect(Unit) { state.onDataOpened() }
    val cols = SandboxDataset.columns
    var selected by remember { mutableStateOf<Int?>(null) }
    val tableScroll = rememberScrollState()

    Column(Modifier.fillMaxSize().background(PaperWarm)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "‹ 工作台", color = InkGray500, fontSize = 13.sp,
                modifier = Modifier.pressScale { navController.popBackStack() }.padding(end = 12.dp, top = 4.dp, bottom = 4.dp),
            )
            Text("資料", color = InkSlate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // What the PM asked for
        if (state.taskList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandPeach.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text("小昀要的", color = InkGray500, fontSize = 11.sp)
                state.taskList.forEachIndexed { i, t ->
                    Text("${i + 1}  $t", color = InkSlate, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }

        // Table + finish button
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 16.dp)) {
            Text(
                "點欄位名稱看說明", color = InkGray500, fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
            Box(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, DataLine, RoundedCornerShape(10.dp)),
            ) {
                Column(Modifier.horizontalScroll(tableScroll)) {
                    Row {
                        cols.forEachIndexed { i, c ->
                            val w = if (i == 0) 76.dp else 112.dp
                            val handled = i in state.limitHandled
                            Box(
                                modifier = Modifier
                                    .width(w)
                                    .background(if (selected == i) BrandPeach.copy(alpha = 0.6f) else PaperWarm)
                                    .border(0.5.dp, DataLine)
                                    .pressScale { selected = i; state.markColumnOpened(i) }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                            ) {
                                Text(
                                    if (handled) "${c.name} ✓" else c.name,
                                    color = if (handled) BrandOrange else InkSlate,
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                            }
                        }
                    }
                    SandboxDataset.rows.forEach { r ->
                        Row {
                            r.forEachIndexed { i, v ->
                                val w = if (i == 0) 76.dp else 112.dp
                                Box(Modifier.width(w).border(0.5.dp, DataLine).padding(horizontal = 10.dp, vertical = 9.dp)) {
                                    Text(if (v.isEmpty()) "—" else v, color = InkSlate, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(BrandOrange)
                    .pressScale {
                        state.finishData()
                        navController.popBackStack()
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) { Text("資料看完了", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        }

        // Column definition, pinned at the bottom
        selected?.let { i ->
            FieldCard(index = i, onClose = { selected = null })
        }
    }
}

@Composable
private fun FieldCard(index: Int, onClose: () -> Unit) {
    val state = SandboxV2State
    val col = SandboxDataset.columns[index]
    val choice = state.limitHandled[index]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color.White)
            .border(1.dp, DataLine, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(col.name, color = InkSlate, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("關閉", color = InkGray500, fontSize = 13.sp, modifier = Modifier.pressScale { onClose() })
        }
        Text(col.desc, color = InkSlate, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
        Text("備註：${col.note}", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

        val lim = col.limit ?: return@Column
        Text(
            lim.prompt, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(10.dp),
        )

        if (choice == null) {
            Text("接下來先處理什麼？", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))
            LimitChoices.forEachIndexed { k, label ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaperWarm)
                        .border(1.dp, DataLine, RoundedCornerShape(10.dp))
                        .pressScale { state.handleLimit(index, k) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) { Text(label, color = InkSlate, fontSize = 13.sp) }
            }
        } else {
            Text("你的處理：${LimitChoices[choice]}", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            when (choice) {
                0 -> {
                    Text("阿凱 · 工程師", color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp, bottom = 3.dp))
                    Text(
                        lim.engReply, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp, 14.dp, 14.dp, 14.dp))
                            .background(PaperWarm)
                            .border(1.dp, DataLine, RoundedCornerShape(4.dp, 14.dp, 14.dp, 14.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                    )
                }
                3 -> Text("已傳給小昀，回覆會在訊息裡。", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                1 -> Text("已記下來。", color = InkGray500, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}