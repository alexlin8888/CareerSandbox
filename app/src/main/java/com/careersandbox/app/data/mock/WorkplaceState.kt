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

    // 目前在第幾天(讓共用的 app 顯示當天情報)
    var currentDay = mutableStateOf(0)

    // 已完成的天(hub 進度打勾、自動前進當前天用)
    val completedDays = mutableStateListOf<Int>()

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

    /** 標記某天完成(玩完夜晚過場時呼叫) */
    fun completeDay(day: Int) {
        if (day !in completedDays) completedDays.add(day)
    }

    fun isDayDone(day: Int): Boolean = day in completedDays

    // ===== 翻 app 機制(紅點引導)：本階段已翻過哪些 app =====
    val visitedApps = mutableStateListOf<String>()
    private var appPhaseDay = 0   // 哪一天的翻 app 階段(回桌面 recompose 時不誤清)

    /** 進入某天翻 app 階段：只在換天時清掉已翻記錄(進 app 再回桌面不會清) */
    fun beginAppPhase(day: Int) {
        if (appPhaseDay != day) {
            appPhaseDay = day
            visitedApps.clear()
        }
    }

    fun visitApp(key: String) { if (key !in visitedApps) visitedApps.add(key) }

    fun isAppVisited(key: String): Boolean = key in visitedApps

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
        completedDays.clear()
        currentDay.value = 0
        log.clear()
        visitedApps.clear()
        appPhaseDay = 0
    }
}
