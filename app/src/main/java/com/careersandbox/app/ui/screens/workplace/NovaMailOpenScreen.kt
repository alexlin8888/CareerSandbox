package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   NovaMail 開信 —— 依 mailId 顯示對應內容(每封信點開看到自己的)
   通用讀信版型：動作列 / 主旨 / 寄件人列 / 本文 / 附件卡 / 可互動回覆。
   ===================================================================== */

private data class MailContent(
    val sender: String,
    val email: String,
    val to: String,
    val subject: String,
    val time: String,
    val avatar: Int?,
    val letter: String,
    val avatarBg: Color,
    val important: Boolean,
    val body: String,
    val attachment: String?,
    val presets: List<String>,
)

private val mailContents: Map<String, MailContent> = mapOf(
    "ken" to MailContent(
        "Ken", "<ken@novapay.com>", "寄給 我、Vivian、阿哲", "分帳的事", "14:04",
        R.drawable.ken_neutral, "", Color(0xFFCBD5E1), true,
        "聽說分帳功能有狀況。今天 5 點前給我你的建議：照原計畫 / 延期 / 縮減範圍，寫清楚理由。\n\n我需要能對客戶交代的版本。\n\n—— Ken",
        "分帳上線決議.pdf",
        listOf(
            "建議基本版照原計畫上線、進階版下一版。客戶看得到、工程也守得住。",
            "建議延期兩週，把 bug 清乾淨再上，品質先顧。",
            "建議縮減範圍，月底先上能對客戶交代的部分。",
        ),
    ),
    "vivian" to MailContent(
        "Vivian", "<vivian@novapay.com>", "寄給 我", "客戶下週要 demo", "13:50",
        R.drawable.colleague_vivian, "", Color(0xFFCBD5E1), true,
        "客戶董事會下週三要看分帳的 demo，時間我已經先答應了。\n\n我知道工程那邊卡關，但這場很關鍵。能不能至少生出一個能跑的版本？拜託。\n\n—— Vivian",
        null,
        listOf(
            "我來跟工程確認，至少準備一個能 demo 的版本。",
            "先跟你對一下要 demo 哪些功能，免得當場出包。",
            "這場我陪你一起，先把要講的故事想清楚。",
        ),
    ),
    "ci" to MailContent(
        "CI 建置通知", "<ci@novapay.com>", "寄給 wallet-team", "[wallet] build #4821 失敗", "10:32",
        null, "CI", Color(0xFF64748B), false,
        "Pipeline failed at stage: integration-test\n\nFAILED: SettlementServiceTest.shouldSplitPayment\nExpected: 3 entries, Actual: 2\n\n完整 log 請至 CI 後台查看。",
        null,
        listOf(
            "我看一下這個 test 為什麼掛。",
            "轉給阿哲，這是金流串接那塊。",
        ),
    ),
    "hr" to MailContent(
        "NovaPay HR", "<hr@novapay.com>", "寄給 全體同仁", "本週五團隊午餐", "11:20",
        null, "HR", Color(0xFF3B82F6), false,
        "本週五 12:00 部門聚餐，地點：三樓交誼廳。\n\n請於週四前回覆出席與飲食偏好（葷／素／過敏原）。\n\n新同事也很歡迎一起來認識大家！",
        null,
        listOf(
            "我會出席，葷食，沒有過敏原。",
            "這週比較趕，這次先不參加了。",
        ),
    ),
    "promo" to MailContent(
        "雲端服務電子報", "<news@cloudpro.com>", "寄給 你", "限時優惠：年費 5 折", "昨天",
        null, "雲", Color(0xFF10B981), false,
        "升級 Pro，享 5 倍儲存空間與進階備份。\n\n限時 48 小時，年費 5 折。\n\n[立即升級]　[查看方案]",
        null,
        listOf(
            "沒興趣，退訂這類促銷信。",
        ),
    ),
)

@Composable
fun NovaMailOpenScreen(navController: NavHostController, mailId: String = "ken") {
    val d = mailContents[mailId] ?: mailContents.getValue("ken")
    val scroll = rememberScrollState()
    var replying by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(PaperWhite)) {

        // ===== 動作列 =====
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Outlined.Inbox, null, tint = InkGray700, modifier = Modifier.size(22.dp)) }
            IconButton(onClick = {}) { Icon(Icons.Filled.Delete, null, tint = InkGray700, modifier = Modifier.size(22.dp)) }
            IconButton(onClick = {}) { Icon(Icons.Outlined.MailOutline, null, tint = InkGray700, modifier = Modifier.size(22.dp)) }
            IconButton(onClick = {}) { NovaKebabIcon(InkGray700, 20.dp) }
        }

        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll).padding(horizontal = 20.dp)) {

            // ===== 主旨 + 標籤 =====
            Text(d.subject, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NovaTag("收件匣", Color(0xFFEFEFEF), InkGray700)
                if (d.important) NovaTag("重要", Color(0xFFFFF1E6), BrandDeepOrange)
            }
            Spacer(Modifier.height(18.dp))

            // ===== 寄件人列 =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                NovaCircleAvatar(size = 44.dp, res = d.avatar, letter = d.letter, bg = d.avatarBg)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(d.sender, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(d.email, color = InkGray500, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(d.to, color = InkGray500, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(d.time, color = InkGray400, fontSize = 11.sp)
                    if (d.important) {
                        Spacer(Modifier.height(6.dp))
                        Icon(Icons.Filled.Star, null, tint = BrandAmber, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            // ===== 本文 =====
            Text(d.body, color = InkSlate, fontSize = 15.sp, lineHeight = 26.sp)
            Spacer(Modifier.height(22.dp))

            // ===== 附件卡(有才顯示)=====
            if (d.attachment != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(PaperOff).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFCE8E6)),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Outlined.Description, null, tint = Color(0xFFC5392D), modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.attachment, color = InkBlack, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("248 KB · PDF", color = InkGray500, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // ===== 回覆區(可互動：預設選項 / 自己打字)=====
        when {
            sent -> {
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFFE6F4EA))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Check, null, tint = Color(0xFF1E8E5A), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("回覆已送出", color = Color(0xFF1E8E5A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(draft, color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp, maxLines = 3)
                    }
                }
            }
            replying -> {
                Column(
                    Modifier.fillMaxWidth().background(PaperWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text("選一個快速回覆，或自己打：", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(9.dp))
                    d.presets.forEach { preset ->
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PaperOff)
                                .pressScale { draft = preset }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) { Text(preset, color = InkSlate, fontSize = 13.sp, lineHeight = 19.sp) }
                        Spacer(Modifier.height(7.dp))
                    }
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(PaperOff)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        ) {
                            BasicTextField(
                                value = draft,
                                onValueChange = { draft = it },
                                textStyle = TextStyle(color = InkBlack, fontSize = 13.sp, lineHeight = 19.sp),
                                cursorBrush = SolidColor(BrandOrange),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { inner ->
                                    if (draft.isEmpty()) {
                                        Text("自己打一句回覆…", color = InkGray400, fontSize = 13.sp)
                                    }
                                    inner()
                                },
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        val canSend = draft.isNotBlank()
                        Box(
                            Modifier.size(44.dp).clip(CircleShape)
                                .background(if (canSend) BrandOrange else InkGray200)
                                .pressScale { if (canSend) { sent = true } }
                                .padding(11.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Send, null, tint = PaperWhite, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Text("demo：之後由模型端依信件內容生成更自然的回覆", color = InkGray400, fontSize = 10.sp)
                }
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth().background(PaperWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(50)).background(BrandOrange)
                            .pressScale { replying = true }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("回覆", color = PaperWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(50)).background(PaperOff)
                            .pressScale { replying = true }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("轉寄", color = InkGray700, fontWeight = FontWeight.Medium, fontSize = 14.sp) }
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .pressScale { navController.popBackStack() }.padding(horizontal = 14.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("之後再說", color = InkGray500, fontSize = 14.sp) }
                }
            }
        }
    }
}

@Composable
private fun NovaTag(text: String, bg: Color, fg: Color) {
    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
