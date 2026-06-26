package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   Day 1 · 週一：與主管 Ken 的 1on1（決策場景，3 拍小弧）
   真實打底：新人 PM 最常見死法是 coming in too hot / 單打獨鬥逞英雄;
   但主管要的是你的判斷,不是把球推回去。沒有零成本選項。
   ===================================================================== */

private data class D1Beat(val narration: String, val choices: List<DecisionChoice>)

@Composable
fun Day1OneOnOneScreen(navController: NavHostController) {
    var beat by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }

    LaunchedEffect(repPop) {
        if (repPop != null) { kotlinx.coroutines.delay(1900); repPop = null }
    }

    val beats = listOf(
        D1Beat(
            "坐吧。分帳那個功能,工程說月底上不了,bug 還沒解。你是這功能的 PM——你的判斷是什麼?",
            listOf(
                DecisionChoice("A", "我得先跟工程確認 bug 的範圍,今天給你一個方向。",
                    "主管信任", 1, "先確認再表態,穩", "d1_listen"),
                DecisionChoice("B", "月底沒問題,我會盯著上。",
                    "專業形象", 2, "有魄力但你還不懂技術風險", "d1_overpromise"),
                DecisionChoice("C", "這看公司優先順序——您要先保 demo 還是先保品質?",
                    "主管信任", -2, "Ken 要你的判斷,不是把球丟回來", "d1_passback"),
            ),
        ),
        D1Beat(
            "假設真的來不及。工程要兩週,業務已經跟客戶說月底。這個夾縫,你會怎麼拆?",
            listOf(
                DecisionChoice("A", "先讓基本版上、進階版下一版——客戶看得到東西,風險也鎖得住。",
                    "專業形象", 2, "分階段:真實世界的好解", "d1_phase"),
                DecisionChoice("B", "壓工程加班,月底硬上。",
                    "同事情誼", -1, "用工程的肝填洞,他們會記得", "d1_crunch"),
                DecisionChoice("C", "我想先聽工程和業務各自的版本,明天會議再定。",
                    "主管信任", 2, "對齊優先,但慢一步", "d1_align"),
            ),
        ),
        D1Beat(
            "第一週,別自己硬扛。有什麼需要我頂的,現在說。",
            listOf(
                DecisionChoice("A", "我想要工程的 bug 清單和業務的客戶承諾,today 之內。",
                    "主管信任", 1, "開口要資源,清楚", "d1_ask"),
                DecisionChoice("B", "目前沒有,我自己先摸清楚。",
                    "主管信任", 1, "硬扛——能幹,但也獨", "d1_solo"),
                DecisionChoice("C", "能不能幫我跟業務說,先別再對客戶加碼承諾?",
                    "同事情誼", 1, "請主管擋一下,聰明", "d1_shield"),
            ),
        ),
    )

    // Ken 表情:依當前主管信任值
    val mt = WorkplaceState.managerTrust.value
    val portrait = when {
        beat == 0 -> R.drawable.ken_neutral
        mt >= 5 -> R.drawable.ken_soft
        mt <= 2 -> R.drawable.ken_stern
        else -> R.drawable.ken_neutral
    }

    if (done) {
        Day1Ending(onBack = { navController.popBackStack() })
        return
    }

    val current = beats[beat]
    SandboxDecisionScene(
        speaker = "Ken",
        portrait = portrait,
        narration = current.narration,
        choices = current.choices,
            bgRes = R.drawable.bg_scene_1on1,
        repPop = repPop,
        onBack = { navController.popBackStack() },
        onChoose = { c ->
            if (c.repDelta != 0) {
                repPop = WorkplaceState.apply(c.repMeter, c.repDelta, c.repReason, day = 1)
            }
            c.flag?.let { WorkplaceState.setFlag(it) }
            if (beat < beats.lastIndex) beat++ else done = true
        },
    )
}

@Composable
private fun Day1Ending(onBack: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF1A1109))),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("週一 · 1on1 結束", color = Color(0xFFFFB627), fontSize = 13.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text("你今天說的每句話,Ken 都記著。", color = Color(0xFFFFF8F3),
                fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("痕跡,週五揭曉。", color = Color(0xB3FFF8F3), fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2531C))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("回到本週", color = Color(0xFFFFF8F3), fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}
