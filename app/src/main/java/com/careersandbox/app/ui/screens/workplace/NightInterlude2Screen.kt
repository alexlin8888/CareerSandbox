package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   那天晚上 · 週二 —— Day2「Email 風暴日」之後的夜晚過場
   你代管了一天主管的收件匣。今晚的訊息依分流準度(d2_sharp/messy)與
   有沒有把不該丟的硬塞給 Vivian(d2_dumped_vivian)而不同。
   ===================================================================== */

@Composable
fun NightInterlude2Screen(navController: NavHostController) {
    val sharp = WorkplaceState.hasFlag("d2_sharp")
    val messy = WorkplaceState.hasFlag("d2_messy")
    val dumped = WorkplaceState.hasFlag("d2_dumped_vivian")

    val opening = when {
        sharp -> "信箱清空的時候,天已經黑了。但桌面乾淨,心也乾淨。手機亮了。"
        messy -> "最後一封拖出去的時候,你不太確定對不對。回到家,手機已經在閃。"
        else -> "代管一天收件匣,比想像中累。坐下,手機亮了幾下。"
    }
    val kenMsg = when {
        sharp -> "回來看了你今天代管的收件匣。分得乾淨,該擋的擋了,該往外的也沒積。出差還能放心,不容易。"
        messy -> "我看了今天的信。有幾封分錯棚,客戶那邊我先壓著。明天早上來找我,我們對一下哪些不該往外丟。"
        else -> "收件匣大致清了,有兩封我自己接手。明天照常,我九點進辦公室。"
    }
    val vivianMsg = if (dumped) {
        "今天被你轉過來一疊…有些其實不是業務的事吧?我這邊也滿手。下次拿不準的,先問我一句,別直接丟過來,好嗎?"
    } else {
        "今天那封 API 規格的,謝啦,你找對人了。客戶那邊我接著談,有進展跟你說。辛苦了。"
    }
    val akaiMsg = "今天群組那些 escalation 我都接了,有個帳號鎖死的也順手解掉。下次直接 tag 我,不用走客服那條。早點睡,明天會議見。"

    NightShell(
        time = "週二 22:10",
        opening = opening,
        foreshadow = "明天有個跨部門會議。聽說,不是每個部門都站在你這邊。",
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
    ) {
        NightMsgCard("Ken", "NovaChat", kenMsg, Color(0xFFB85C3A))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("Vivian", "業務", vivianMsg, Color(0xFFC77DFF))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿凱", "#product", akaiMsg, Color(0xFF5BB6A6))
    }
}
