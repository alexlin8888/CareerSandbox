package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.theme.PaperWhite

/* =====================================================================
   NovaDoc 決議文件 —— 你（PM）對 Ken 的「分帳上線」決議草稿
   通用文件版型（Notion 風）：麵包屑 / 標題 / 屬性 / 目標 / 範圍 / 風險。
   刻意用暖灰中性色與留白，靠通用文件語言擬真，不碰任何真品牌。
   ===================================================================== */

// Notion 風暖灰（刻意與其他畫面的 Ink 系區隔，是另一種介面表面）
private val DocInk = Color(0xFF37352F)
private val DocMuted = Color(0xFF9B9A97)
private val DocFaint = Color(0xFFEDEDED)

@Composable
fun NovaDocScreen(navController: NavHostController) {
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().background(PaperWhite)) {

        // ===== 麵包屑 / 動作列 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = DocInk, modifier = Modifier.size(20.dp))
            }
            Text(
                "NovaPay · 產品 · 分帳上線決議",
                color = DocMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = {}) { Icon(Icons.Outlined.Share, null, tint = DocInk, modifier = Modifier.size(19.dp)) }
            IconButton(onClick = {}) { NovaKebabIcon(DocInk, 20.dp) }
        }
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(DocFaint))

        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)
                .padding(start = 26.dp, end = 26.dp, top = 20.dp, bottom = 28.dp),
        ) {
            // ===== 文件圖示 + 標題 =====
            Icon(Icons.Outlined.Description, null, tint = DocInk, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(12.dp))
            Text("分帳上線決議", color = DocInk, fontWeight = FontWeight.ExtraBold, fontSize = 29.sp, lineHeight = 34.sp)
            Spacer(Modifier.height(16.dp))

            // ===== 屬性：負責人 / 狀態 =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.width(82.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, null, tint = DocMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("負責人", color = DocMuted, fontSize = 13.sp)
                }
                NovaCircleAvatar(size = 20.dp, letter = "我", bg = Color(0xFFB85C3A))
                Spacer(Modifier.width(6.dp))
                Text("你（PM）", color = DocInk, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.width(82.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Timer, null, tint = DocMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("狀態", color = DocMuted, fontSize = 13.sp)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(5.dp)).background(Color(0xFFFDEEE6))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                ) { Text("審核中", color = Color(0xFFB8421A), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(18.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(DocFaint))
            Spacer(Modifier.height(18.dp))

            // ===== 目標 =====
            Text("目標", color = DocInk, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(11.dp))
            DocBullet("讓「一鍵分帳」在 demo 前可用、且不出事")
            Spacer(Modifier.height(10.dp))
            DocBullet("在工程、業務、品質之間取得共識")
            Spacer(Modifier.height(22.dp))

            // ===== 範圍 =====
            Text("範圍", color = DocInk, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(11.dp))
            DocCheck(
                checked = true,
                content = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("基本版") }
                    append("：手動建立分帳、平均拆分")
                },
                textColor = DocInk,
            )
            Spacer(Modifier.height(10.dp))
            DocCheck(
                checked = false,
                content = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("進階版") }
                    append("（下一版）：自動金流串接")
                },
                textColor = Color(0xFF73716C),
            )
            Spacer(Modifier.height(22.dp))

            // ===== 風險 =====
            Text("風險", color = DocInk, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(11.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color(0xFFFBECEA)),
            ) {
                Box(Modifier.width(3.dp).fillMaxHeight().background(Color(0xFFE8533B)))
                Row(Modifier.padding(12.dp)) {
                    RiskTriangle()
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "金流串接 race condition，bug 未解",
                            color = DocInk, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "硬上線 → demo 出錯機率高；延期 → 對客戶承諾跳票。",
                            color = Color(0xFF8A6D63), fontSize = 13.5f.sp, lineHeight = 21.sp,
                        )
                    }
                }
            }
        }
    }
}

// ---------- 私有元件 ----------

@Composable
private fun DocBullet(text: String) {
    Row {
        Text("•", color = DocInk, fontSize = 17.sp)
        Spacer(Modifier.width(11.dp))
        Text(text, color = DocInk, fontSize = 15.5f.sp, lineHeight = 24.sp)
    }
}

@Composable
private fun DocCheck(
    checked: Boolean,
    content: androidx.compose.ui.text.AnnotatedString,
    textColor: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(18.dp).clip(RoundedCornerShape(4.dp))
                .border(1.5.dp, if (checked) DocInk else Color(0xFFC5C2BB), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) Icon(Icons.Outlined.Check, null, tint = DocInk, modifier = Modifier.size(12.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(content, color = textColor, fontSize = 15.5f.sp, lineHeight = 23.sp)
    }
}

/** 風險警示三角（取代 emoji；純圖示，非標點）。 */
@Composable
private fun RiskTriangle() {
    Canvas(Modifier.size(17.dp)) {
        val w = size.width
        val tri = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, w * 0.14f)
            lineTo(w * 0.92f, w * 0.84f)
            lineTo(w * 0.08f, w * 0.84f)
            close()
        }
        drawPath(tri, Color(0xFFE8533B))
        // 內部驚嘆桿 + 點（白）
        drawLine(Color(0xFFFBECEA), Offset(w * 0.5f, w * 0.40f), Offset(w * 0.5f, w * 0.62f), w * 0.08f, StrokeCap.Round)
        drawCircle(Color(0xFFFBECEA), w * 0.05f, Offset(w * 0.5f, w * 0.74f))
    }
}
