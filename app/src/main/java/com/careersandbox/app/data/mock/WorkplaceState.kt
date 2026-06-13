package com.careersandbox.app.data.mock

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

/* =====================================================================
   職場聲望貫穿系統 —— 沙盒五天共用的全域狀態
   三條計量 + 跨天旗標(後果回收) + 變動記錄(原因彈窗 / 週報)
   ===================================================================== */

data class RepChange(
    val meter: String,   // 主管信任 / 同事情誼 / 專業形象
    val delta: Int,
    val reason: String,
    val day: Int,
)

object WorkplaceState {
    // 三條聲望(0–10,起始中間值)
    var managerTrust = mutableStateOf(3)
    var peerBond = mutableStateOf(3)
    var proImage = mutableStateOf(3)

    // 跨天旗標(後果回收用):某天做了某選擇 → 後續場景讀
    val flags = mutableStateListOf<String>()

    // 全程變動記錄(週五週報逐項回顧)
    val log = mutableStateListOf<RepChange>()

    // 入職介紹是否看過(只給新玩家)
    var seenIntro = mutableStateOf(false)

    /** 場景呼叫:加減某條計量,記錄原因,回傳給 UI 彈窗 */
    fun apply(meter: String, delta: Int, reason: String, day: Int): RepChange {
        val state = when (meter) {
            "主管信任" -> managerTrust
            "同事情誼" -> peerBond
            else -> proImage
        }
        state.value = (state.value + delta).coerceIn(0, 10)
        val change = RepChange(meter, delta, reason, day)
        log.add(change)
        return change
    }

    fun setFlag(flag: String) {
        if (flag !in flags) flags.add(flag)
    }

    fun hasFlag(flag: String): Boolean = flag in flags

    /** 動態人設:依三條數值給一句「你在這間公司是什麼樣的人」 */
    fun persona(): String {
        val t = managerTrust.value
        val b = peerBond.value
        val p = proImage.value
        return when {
            t >= 6 && b >= 6 && p >= 6 -> "能扛事、又有人緣的那種新人"
            t >= 6 && b <= 3 -> "主管眼中的能幹,但同事還沒走近"
            b >= 6 && t <= 3 -> "同事很挺你,主管還在觀察"
            p >= 6 && b <= 3 -> "專業沒話說,但有點獨來獨往"
            t <= 2 || b <= 2 || p <= 2 -> "這週有點跌跌撞撞,還在找節奏"
            else -> "穩穩地過第一週,慢慢被看見"
        }
    }

    /** 重置(再玩一次 / 新玩家) */
    fun reset() {
        managerTrust.value = 3
        peerBond.value = 3
        proImage.value = 3
        flags.clear()
        log.clear()
    }
}
