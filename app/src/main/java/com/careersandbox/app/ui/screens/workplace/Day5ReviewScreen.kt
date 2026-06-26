package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 5 · 週五：週五回顧（決策 + 績效儀表板 + 尾聲）
   真實打底：第一週的真相不是成就感,是疲憊與自我懷疑——「每天回家累到無法
   直線思考,以為自己有問題,直到發現每個人都這樣」。所以收尾不給廉價勝利:
   讀整週旗標,逐條講出你把每個人留在哪裡,媽收束落低谷不回升。
   ===================================================================== */

private data class Consequence(val who: String, val text: String, val accent: Color)

@Composable
fun Day5ReviewScreen(navController: NavHostController) {
    var phase by remember { mutableStateOf("oneonone") } // oneonone | board | night

    when (phase) {
        "board" -> ReviewBoard(onHome = { phase = "night" })
        "night" -> NightEnding(onEnd = { navController.popBackStack() })
        else -> {
            val mt = WorkplaceState.managerTrust.value
            SandboxDecisionScene(
                speaker = "Ken",
                portrait = faceKenBase(mt),
                narration = "第一週,結束了。坐下——我跟你說說我看到的。",
                sceneLabel = "週五 · 回顧",
                bgRes = R.drawable.bg_scene_office,
                choices = listOf(
                    DecisionChoice("A", "我想先聽您的。", "主管信任", 0, ""),
                    DecisionChoice("B", "我知道有些地方我可以更好。", "主管信任", 0, ""),
                    DecisionChoice("C", "這週…我盡力了。", "主管信任", 0, ""),
                ),
                onBack = { navController.popBackStack() },
                onChoose = { phase = "board" },
            )
        }
    }
}

/* ---------- 績效儀表板：三計量 + 逐條回收後果 ---------- */
@Composable
private fun ReviewBoard(onHome: () -> Unit) {
    val mt = WorkplaceState.managerTrust.value
    val pb = WorkplaceState.peerBond.value
    val pi = WorkplaceState.proImage.value
    fun has(f: String) = WorkplaceState.hasFlag(f)

    val green = Color(0xFF2E9E6B); val red = Color(0xFFD8553A); val amber = Color(0xFFE0922A); val gray = Color(0xFF9CA3AF)

    // 逐角色後果(讀整週旗標)
    val vivian = when {
        has("d3_backed_vivian") -> Consequence("Vivian", "她跟你有了交情。客戶那案她自己扛下來,還說欠你一個。", green)
        has("d3_left_vivian") -> Consequence("Vivian", "你們只剩公事。下次她需要人,不會先想到你。", red)
        else -> Consequence("Vivian", "你跟 Vivian 不算近,也沒結怨。", gray)
    }
    val akai = when {
        has("lunch_bonded_akai") -> Consequence("阿哲", "他傳訊息給你:下週有個小案子,算你一個。", green)
        has("d3_burned_team") || has("d2_push_eng") -> Consequence("阿哲", "他沒抱怨,但也不再主動找你了。", red)
        else -> Consequence("阿哲", "你跟阿哲還在客氣的距離。", gray)
    }
    val ken = when {
        has("d3_passed_buck") -> Consequence("Ken", "我要的是你的判斷,不是把球丟回來。下次,先想好再進來。", red)
        has("d1_overpromise") -> Consequence("Ken", "你答應的月底,記得自己扛。我會看著。", amber)
        mt >= 5 -> Consequence("Ken", "這週可以。繼續。", green)
        else -> Consequence("Ken", "還在看你。第一週,先這樣。", gray)
    }
    val fang = when {
        has("d4_badmouth") -> Consequence("小芳", "你說的我幫你保密。但記得——茶水間,沒有秘密。", amber)
        has("d4_heed") -> Consequence("小芳", "她對你點點頭。前輩的善意,你接住了。", green)
        has("d4_gossip") -> Consequence("小芳", "她把你當自己人聊。是好是壞,看你怎麼拿捏。", gray)
        else -> Consequence("小芳", "你跟小芳保持著禮貌的距離。", gray)
    }
    val verdict = when {
        mt >= 6 -> "站住了。"
        mt >= 3 -> "及格邊緣。"
        else -> "這週,很勉強。"
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFFFF8F3))) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, top = 60.dp, bottom = 28.dp),
        ) {
            Text("第一週 · 結算", color = Color(0xFFF2531C), fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(6.dp))
            Text("你是個什麼樣的同事", color = Color(0xFF281C12), fontSize = 26.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(20.dp))

            MeterBar5("主管信任", mt, Color(0xFFB85C3A))
            Spacer(Modifier.height(12.dp))
            MeterBar5("同事情誼", pb, Color(0xFFE0922A))
            Spacer(Modifier.height(12.dp))
            MeterBar5("專業形象", pi, Color(0xFF2E9E6B))

            Spacer(Modifier.height(26.dp))
            Text("你把人留在哪裡", color = Color(0xFF6B7280), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            listOf(ken, vivian, akai, fang).forEach { c ->
                ConsequenceCard(c)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF281C12))
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Ken 的總評", color = Color(0xFFFFB627), fontSize = 12.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(12.dp))
                Text(verdict, color = Color(0xFFFFF8F3), fontSize = 18.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2531C)).clickable { onHome() },
                contentAlignment = Alignment.Center,
            ) { Text("回家", color = Color(0xFFFFF8F3), fontWeight = FontWeight.Black, fontSize = 15.sp) }
        }
    }
}

@Composable
private fun MeterBar5(label: String, value: Int, color: Color) {
    val frac = (value.coerceIn(0, 10)) / 10f
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color(0xFF281C12), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${value.coerceIn(0, 10)} / 10", color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(7.dp))
        Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)).background(Color(0x14281C12))) {
            Box(Modifier.fillMaxWidth(frac).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(color))
        }
    }
}

@Composable
private fun ConsequenceCard(c: Consequence) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White)
            .padding(top = 14.dp, bottom = 14.dp, end = 16.dp),
    ) {
        Box(Modifier.width(4.dp).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(c.accent))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(c.who, color = Color(0xFF281C12), fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text(c.text, color = Color(0xFF4B5563), fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}

/* ---------- 夜 · 尾聲（媽收束,落低谷不回升）---------- */
@Composable
private fun NightEnding(onEnd: () -> Unit) {
    val total = WorkplaceState.managerTrust.value + WorkplaceState.peerBond.value + WorkplaceState.proImage.value
    val close = when {
        total >= 18 -> "你站住了第一週。但站住,不等於輕鬆——你只是還沒倒下。"
        total >= 12 -> "說不上好,說不上壞。週末很長,夠你喘一口氣。"
        else -> "這週,你沒站穩。沒關係——第一週,本來就這樣。"
    }
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF241710), Color(0xFF14100B), Color(0xFF0C0907))),
        ),
    ) {
        Column(
            Modifier.fillMaxSize().padding(start = 30.dp, end = 30.dp, top = 90.dp, bottom = 30.dp),
        ) {
            Text("週五 · 夜", color = Color(0xFFFFB627), fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(20.dp))
            Text(close, color = Color(0xFFFFF8F3), fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp)

            Spacer(Modifier.height(30.dp))
            // 媽的訊息
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0x1AFFFFFF))
                    .padding(18.dp),
            ) {
                Text("媽", color = Color(0xFFE0A04A), fontSize = 12.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("辛苦了。第一週都這樣,別以為只有你累。週末回來一趟,我燉了湯。",
                    color = Color(0xFFFFF8F3), fontSize = 15.sp, lineHeight = 25.sp)
            }

            Spacer(Modifier.weight(1f))
            Text("明天,還是要醒來。", color = Color(0xB3FFF8F3), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color(0x22FFFFFF)).clickable { onEnd() },
                contentAlignment = Alignment.Center,
            ) { Text("結束這一週", color = Color(0xFFFFF8F3), fontWeight = FontWeight.Black, fontSize = 15.sp) }
        }
    }
}
