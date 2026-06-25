package com.careersandbox.app.ui.screens.workplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.data.mock.WorkplaceState

/* =====================================================================
   那天晚上 · 週五 —— Day5「週五回顧」之後的尾聲
   全週的情緒落點。依記憶的設計:情緒弧落到低谷而不回升——
   就算表現好,也只剩疲憊與未解的真實感,不給廉價的勝利。
   週一夜由媽開場,週五夜由媽收尾,人味的 throughline 在這裡閉環。
   極簡:厚旁白 + 媽一張卡。
   ===================================================================== */

@Composable
fun NightInterlude5Screen(navController: NavHostController) {
    val mt = WorkplaceState.managerTrust.value
    val pb = WorkplaceState.peerBond.value
    val pi = WorkplaceState.proImage.value
    val bucket = nightBucket((mt + pb + pi) / 3)

    val opening = when (bucket) {
        NightBucket.HIGH ->
            "撐過了。第一週,你站住了。但坐在回家的捷運上,你只覺得累——那種連高興都沒力氣的累。手機亮了一下,是媽。"
        NightBucket.LOW ->
            "第一週,你沒站穩。有些話收不回,有些洞補不上。週末很長,但你知道,週一會很快到。手機亮了,是媽。"
        NightBucket.MID ->
            "一週過去了。不算好,也沒垮。你說不上來這算不算及格。手機亮了一下,是媽。"
    }
    val momMsg = "辛苦了。第一週都是這樣,別放在心上。週末回來一趟吧,我燉了湯。工作的事,過了這個週末再想。"

    NightShell(
        time = "週五 23:58",
        opening = opening,
        foreshadow = "你看著那則訊息,很久沒回。窗外的城市還亮著。明天,還是要醒來。",
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
        dayDone = 5,
        continueLabel = "結束第一週",
    ) {
        NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))
    }
}
