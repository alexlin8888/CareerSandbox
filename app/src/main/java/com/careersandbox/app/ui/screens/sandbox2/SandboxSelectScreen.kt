package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.BottomNavSpace
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.BrandPeach
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm

private data class IndustryItem(val name: String, val enabled: Boolean)

private val industries = listOf(
    IndustryItem("軟體業", true),
    IndustryItem("金融業", false),
    IndustryItem("製造業", false),
    IndustryItem("醫療產業", false),
)

@Composable
fun SandboxSelectScreen(onStart: () -> Unit) {
    var selected by remember { mutableStateOf("軟體業") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWarm)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text("職場沙盒", color = InkSlate, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(6.dp))
        Text(
            "選一個職業，用一個工作日的時間，體驗它實際在做什麼。",
            color = InkGray500, fontSize = 14.sp, lineHeight = 22.sp,
        )

        SectionLabel("產業")
        industries.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { item ->
                    IndustryCard(
                        item = item,
                        isSelected = item.name == selected,
                        modifier = Modifier.weight(1f),
                        onClick = { if (item.enabled) selected = item.name },
                    )
                }
            }
        }

        SectionLabel("職業")
        OccupationCard(onStart = onStart)
        Spacer(Modifier.height(8.dp))
        DisabledRow("前端工程師")
        DisabledRow("產品企劃")

        Spacer(Modifier.height(BottomNavSpace))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, color = InkGray500, fontSize = 12.sp,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun IndustryCard(
    item: IndustryItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .alpha(if (item.enabled) 1f else 0.5f)
            .clip(shape)
            .background(Color.White)
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) BrandOrange else Color(0xFFEADFD6), shape)
            .pressScale { onClick() }
            .padding(14.dp),
    ) {
        Text(item.name, color = InkSlate, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(if (item.enabled) "可體驗" else "準備中", color = InkGray500, fontSize = 11.sp)
    }
}

@Composable
private fun OccupationCard(onStart: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, BrandOrange, shape)
            .padding(16.dp),
    ) {
        Text("資料分析師", color = InkSlate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "在一間 80 人的記帳 App 公司，替產品團隊看資料、給出建議。",
            color = InkGray500, fontSize = 13.sp, lineHeight = 20.sp,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Tag("一個工作日")
            Tag("不計分")
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandOrange)
                .pressScale { onStart() },
            contentAlignment = Alignment.Center,
        ) {
            Text("開始這一天", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Tag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandPeach)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) { Text(text, color = Color(0xFF7A3A10), fontSize = 11.sp) }
}

@Composable
private fun DisabledRow(name: String) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .alpha(0.5f)
            .border(1.dp, Color(0xFFEADFD6), shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(name, color = InkGray500, fontSize = 14.sp)
        Text("準備中", color = InkGray500, fontSize = 14.sp)
    }
}