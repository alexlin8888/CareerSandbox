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
   那天晚上 · 週三 —— Day3「跨部門會議」之後的夜晚過場
   你在 Ken、Vivian、工程之間表了態。今晚的關係餘波依你有沒有挺 Vivian
   (d3_backed/left_vivian)、有沒有拿工程的肝填洞(d3_burned_team)而不同。
   ===================================================================== */

@Composable
fun NightInterlude3Screen(navController: NavHostController) {
    val bucket = nightBucket(WorkplaceState.peerBond.value)
    val backedVivian = WorkplaceState.hasFlag("d3_backed_vivian")
    val leftVivian = WorkplaceState.hasFlag("d3_left_vivian")
    val burnedTeam = WorkplaceState.hasFlag("d3_burned_team")

    val opening = when {
        leftVivian || burnedTeam ->
            "會議室的門關上時,氣氛沒散。你知道有些話,今天說重了。回到家,手機亮著。"
        backedVivian || bucket == NightBucket.HIGH ->
            "散會後 Vivian 還跟你聊了兩句。今晚的手機,大概不會太冷。"
        else ->
            "跨部門會議,各有各的算盤。捷運上,你腦子還在轉。手機亮了。"
    }
    val vivianMsg = when {
        backedVivian ->
            "今天那個分階段的說法,我拿去跟客戶談,過了。欸,跨部門有你這種會補位的,難得。改天請你喝咖啡。"
        leftVivian ->
            "今天的事…算了。違約金的部分我自己想辦法。各掃門前雪,我記住了。"
        else ->
            "今天會開得有點僵,不過結論能用。客戶那邊我再喬,有結果跟你說。"
    }
    val akaiMsg = if (burnedTeam) {
        "聽說月底要我們加班把測試補完?新人第一週就學會拿別人的肝填排程的洞囉。行啦,我們扛,但你心裡有數就好。"
    } else {
        "今天會議的結論我看到了,分階段對工程比較友善,謝啦。測試那六成我這兩天補起來。"
    }
    val momMsg = "今天是不是又開會開到很晚?臉看起來就累。回來吃點東西,什麼都別想了,先睡。明天再說。"

    NightShell(
        time = "週三 22:25",
        opening = opening,
        foreshadow = "明天中午跟同事吃飯。今天的事之後,這頓飯,氣氛大概很微妙。",
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
        dayDone = 3,
    ) {
        NightMsgCard("Vivian", "業務", vivianMsg, Color(0xFFC77DFF))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿凱", "#product", akaiMsg, Color(0xFF5BB6A6))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))
    }
}
