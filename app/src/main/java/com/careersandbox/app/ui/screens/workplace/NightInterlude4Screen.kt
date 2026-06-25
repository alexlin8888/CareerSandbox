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
   那天晚上 · 週四 —— Day4「同事午餐」之後的夜晚過場
   茶水間的軟場景之後:小芳的八卦會不會回流(d4_gossiped)、有沒有跟阿凱
   走近(lunch_bonded_akai),Vivian 則延續 Day3 的關係(d3_backed/left)。
   ===================================================================== */

@Composable
fun NightInterlude4Screen(navController: NavHostController) {
    val bucket = nightBucket(WorkplaceState.peerBond.value)
    val gossiped = WorkplaceState.hasFlag("d4_gossiped")
    val badmouth = WorkplaceState.hasFlag("d4_badmouth")
    val bondedAkai = WorkplaceState.hasFlag("lunch_bonded_akai")
    val backedVivian = WorkplaceState.hasFlag("d3_backed_vivian")
    val leftVivian = WorkplaceState.hasFlag("d3_left_vivian")

    val opening = when {
        gossiped -> "茶水間那些話,午休結束還在你耳邊繞。回到家,手機亮著。"
        bondedAkai || bucket == NightBucket.HIGH -> "今天午餐難得輕鬆,阿凱還聊起他週末寫的小工具。手機亮了。"
        else -> "一頓飯,半是八卦半是試探。捷運上,手機亮了幾下。"
    }
    val fangMsg = when {
        badmouth -> "欸,你中午說的那些對 Ken 的看法…我幫你保密啦,但茶水間沒有秘密,你懂的。下次小心點。"
        gossiped -> "欸欸,你中午說的那個改組,我跟人資的小美求證了,好像真的!不過你放心,我沒說是你講的啦……大概。"
        else -> "今天聊得開心!不過 Ken 那題你滑得真快,滴水不漏喔。改天再約啦。"
    }
    val akaiMsg = if (bondedAkai) {
        "欸,中午你說想看我那個整理測試報告的工具?我清一下丟你。很少給人看的,別嫌醜。"
    } else {
        "中午人有點多,沒聊到。明天 sync 見。"
    }
    val vivianMsg = when {
        backedVivian -> "跟你說一聲,分階段那案客戶買單了。欠你的那杯咖啡,我記著。"
        leftVivian -> "客戶那邊我自己搞定了。就…報備一下。"
        else -> "客戶案進度同步給你,細節明天再說。"
    }

    NightShell(
        time = "週四 21:30",
        opening = opening,
        foreshadow = "明天就週五了。這一週的每個選擇,Ken 都記著——明天,該結帳了。",
        navController = navController,
        backTo = Routes.WORKPLACE_SANDBOX,
    ) {
        NightMsgCard("小芳", "訊息", fangMsg, Color(0xFFE0A04A))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("阿凱", "#product", akaiMsg, Color(0xFF5BB6A6))
        Spacer(Modifier.height(12.dp))
        NightMsgCard("Vivian", "業務", vivianMsg, Color(0xFFC77DFF))
    }
}
