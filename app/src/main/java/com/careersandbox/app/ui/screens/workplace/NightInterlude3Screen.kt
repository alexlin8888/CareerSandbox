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
   情緒弧的觸底點。這天有 no-win 拍(你被迫選一個人去扛),夜晚的餘波依
   你對不起了誰(d3_sacrifice_eng/vivian/client)、會議的表態(d3_backed/
   left_vivian、d3_burned_team)而不同。收尾帶出「認真做事就會踩到人」的
   幻滅感——不轉正能量,讓低谷是低谷。
   ===================================================================== */

@Composable
fun NightInterlude3Screen(navController: NavHostController) {
    val bucket = nightBucket(WorkplaceState.peerBond.value)
    val backedVivian = WorkplaceState.hasFlag("d3_backed_vivian")
    val leftVivian = WorkplaceState.hasFlag("d3_left_vivian")
    val burnedTeam = WorkplaceState.hasFlag("d3_burned_team")
    val sacEng = WorkplaceState.hasFlag("d3_sacrifice_eng")
    val sacVivian = WorkplaceState.hasFlag("d3_sacrifice_vivian")
    val sacClient = WorkplaceState.hasFlag("d3_sacrifice_client")
    val sacrificed = sacEng || sacVivian || sacClient

    val opening = when {
        sacEng || burnedTeam ->
            "會議室的門關上時,你還記得阿哲沒看你那一眼。今天你選了一個人去扛。回到家,手機亮著,你有點不想點開。"
        sacVivian ->
            "你陪 Vivian 去跟客戶講了延期。違約那邊很難看,但至少她不是一個人。回家的路很長,手機亮了。"
        sacClient ->
            "砍掉一半範圍,客戶的臉色你忘不掉。Ken 說「成熟」,可是你心裡知道,飛掉的那張單,有人要去吞。手機亮著。"
        backedVivian || bucket == NightBucket.HIGH ->
            "散會後 Vivian 還跟你聊了兩句。今晚的手機,大概不會太冷。"
        else ->
            "跨部門會議,各有各的算盤。捷運上,你腦子還在轉。手機亮了。"
    }
    val vivianMsg = when {
        sacVivian ->
            "今天你陪我去跟客戶講延期…謝謝。難看歸難看,至少不是我一個人站在那裡。這個我記著。"
        sacClient ->
            "砍範圍我懂,公司角度沒錯。但那張單有我的業績,這個月大概要重算了。算了,不怪你——就是有點悶。"
        backedVivian ->
            "今天那個分階段的說法,我拿去跟客戶談,過了。欸,跨部門有你這種會補位的,難得。改天請你喝咖啡。"
        leftVivian ->
            "今天的事…算了。違約金的部分我自己想辦法。各掃門前雪,我記住了。"
        else ->
            "今天會開得有點僵,不過結論能用。客戶那邊我再喬,有結果跟你說。"
    }
    val zheMsg = when {
        sacEng || burnedTeam ->
            "所以最後還是硬上了齁。「我跟你一起盯」——這句我聽過很多次了。真出事的時候,commit 上是我的名字。我知道你也難,但這口氣,我得自己嚥。"
        WorkplaceState.hasFlag("d3_phase") ->
            "今天會議的結論我看到了,分階段對工程比較友善,謝啦。測試那六成我這兩天補起來。"
        else ->
            "今天會議結論收到。範圍我再消化一下,有問題明天 sync 提。"
    }
    val momMsg = "今天是不是又開會開到很晚?臉看起來就累。回來吃點東西,什麼都別想了,先睡。明天再說。"

    val foreshadow = if (sacrificed)
        "你關掉螢幕,盯著天花板。原來在這裡,認真做事的代價,是總有人會因為你而受傷。明天中午還要跟同事吃飯——這頓飯,大概很安靜。"
    else
        "明天中午跟同事吃飯。今天的事之後,這頓飯,氣氛大概很微妙。"

    NightShell(
        time = "週三 22:25",
        opening = opening,
        foreshadow = foreshadow,
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
        dayDone = 3,
    ) {
        NightMsgCard("Vivian", "業務", vivianMsg, Color(0xFFC77DFF))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿哲", "#product", zheMsg, Color(0xFF5BB6A6))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))
    }
}
