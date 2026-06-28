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
   那天晚上 · 週一 —— Day1「和主管 1on1」之後的夜晚過場
   依白天結果(主管信任 + 跨天旗標)長出不同的鎖屏訊息。媽恆定,是情緒錨點。
   旗標已對齊 Day1 實際設定:d1_ask/d1_passback/d1_solo/d1_listen/d1_shield/
   d1_overpromise/d1_press_zhe/d1_trust_zhe。
   ===================================================================== */

@Composable
fun NightInterlude1Screen(navController: NavHostController) {
    val bucket = nightBucket(WorkplaceState.managerTrust.value)
    val asked = WorkplaceState.hasFlag("d1_ask")            // 今天開口要 bug 清單
    val dodged = WorkplaceState.hasFlag("d1_passback")      // 把問題丟回給 Ken
    val solo = WorkplaceState.hasFlag("d1_solo")            // 自己先摸清楚
    val warm = WorkplaceState.hasFlag("d1_listen") || WorkplaceState.hasFlag("d1_shield")
    val overpromised = WorkplaceState.hasFlag("d1_overpromise")
    val pressedZhe = WorkplaceState.hasFlag("d1_press_zhe")
    val trustedZhe = WorkplaceState.hasFlag("d1_trust_zhe")

    val opening = when (bucket) {
        NightBucket.HIGH -> "走出大樓,風是涼的。手機在口袋裡震了幾下。"
        NightBucket.LOW -> "回到家,燈沒開就先坐下。手機螢幕亮著,三則。"
        NightBucket.MID -> "捷運上。今天的對話還在腦裡轉。手機亮了。"
    }
    val kenMsg = when {
        overpromised ->
            "你今天那句「月底沒問題」,我記著了。第一週敢拍胸脯,可以。但話講出去,就是你的了——週三中午,我看你怎麼兌現。"
        bucket == NightBucket.HIGH && warm ->
            "今天那關過了。難得你還問我壓力——輪不到你操心,但領情。週三中午,我記著。"
        bucket == NightBucket.HIGH ->
            "今天講得清楚。週三中午前給我能跑的版本,就這樣。早點睡。"
        bucket == NightBucket.LOW && dodged ->
            "把你今天說的判斷,自己寫下來,現在就寄到我信箱。明天九點我要看到。我不喜歡把問題丟回來的人。"
        bucket == NightBucket.LOW ->
            "今天那些話,我先記著。明天開始,我要看進度,不是看態度。"
        else ->
            "週三中午前,我要看到能跑的核心路徑。別再讓我用問的。"
    }
    val zheMsg = when {
        pressedZhe ->
            "聽 Ken 說,你今天覺得我那兩週是在「留 buffer」?行喔,新人第一週就會這樣看人。明天 sync,數字你自己看,我沒空陪你猜。"
        trustedZhe ->
            "Ken 說你今天幫我講了話,沒急著逼日期。新人裡少見。明天 sync,我先把 bug 清單整理給你,我們對一下真正的範圍。"
        asked ->
            "你今天說要 bug 清單?我下班前撈了一份,明早丟你。先講好,有些是跨團隊的依賴,不全是我能定的。"
        else ->
            "明天的 sync 你會到吧?有兩個地方想先跟你對一下,不然到時候又要重來。"
    }
    val momMsg = "下班了沒?不要又叫外送。冰箱有湯,熱十分鐘就好。早點睡,不要學你爸熬夜。"

    NightShell(
        time = "週一 21:47",
        opening = opening,
        foreshadow = "睡前最後一個念頭:明天的信箱,大概不會太安靜。",
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
        dayDone = 1,
    ) {
        NightMsgCard("Ken", "NovaChat", kenMsg, Color(0xFFB85C3A))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿哲", "#product", zheMsg, Color(0xFF5BB6A6))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))
    }
}
