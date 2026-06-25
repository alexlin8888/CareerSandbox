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
   ===================================================================== */

@Composable
fun NightInterlude1Screen(navController: NavHostController) {
    val bucket = nightBucket(WorkplaceState.managerTrust.value)
    val askedAkai = WorkplaceState.hasFlag("d1_asked_akai")
    val dodged = WorkplaceState.hasFlag("d1_dodged")
    val solo = WorkplaceState.hasFlag("d1_solo")
    val warm = WorkplaceState.hasFlag("d1_warm")

    val opening = when (bucket) {
        NightBucket.HIGH -> "走出大樓,風是涼的。手機在口袋裡震了幾下。"
        NightBucket.LOW -> "回到家,燈沒開就先坐下。手機螢幕亮著,三則。"
        NightBucket.MID -> "捷運上。今天的對話還在腦裡轉。手機亮了。"
    }
    val kenMsg = when {
        bucket == NightBucket.HIGH && warm ->
            "今天那關過了。難得你還問我壓力——輪不到你操心,但領情。週三中午,我記著。"
        bucket == NightBucket.HIGH ->
            "今天講得清楚。週三中午前給我能跑的版本,就這樣。早點睡。"
        bucket == NightBucket.LOW && dodged ->
            "把你今天說的日期,自己寫下來,現在就寄到我信箱。明天九點我要看到。"
        bucket == NightBucket.LOW ->
            "今天那些話,我先記著。明天開始,我要看進度,不是看態度。"
        else ->
            "週三中午前,我要看到能跑的核心路徑。別再讓我用問的。"
    }
    val akaiMsg = when {
        askedAkai ->
            "欸,Ken 剛說你今天 1on1 提到要借我半天?沒問題,明天早上第一件事抓我。新人第一週就敢開口要人,可以喔。"
        solo ->
            "聽說你今天被 Ken 約談?還好吧。明天 sync 有什麼卡的,丟群組,別自己悶著。"
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
    ) {
        NightMsgCard("Ken", "NovaChat", kenMsg, Color(0xFFB85C3A))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿凱", "#product", akaiMsg, Color(0xFF5BB6A6))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("媽", "訊息", momMsg, Color(0xFFE0A04A))
    }
}
