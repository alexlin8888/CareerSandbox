package com.careersandbox.app.ui.screens.sandbox2

data class AnalysisOption(val name: String, val explain: String)

/** One pinned chart. questionIndex is set in question mode, intent in free mode. */
data class ChartPin(
    val metric: String,
    val seg: String,
    val method: String?,
    val questionIndex: Int?,
    val intent: String?,
    val labels: List<String>,
    val values: List<Int>,
)

/** Options and fake chart values for station 3. */
object SandboxAnalysis {

    val metrics = listOf(
        AnalysisOption("稍後再看使用率", "有用過「稍後再看」的人占多少"),
        AnalysisOption("次月留存", "第二個月還在的人占多少"),
        AnalysisOption("客服詢問比例", "問過能不能收藏的人占多少"),
        AnalysisOption("人均使用次數", "平均每人用幾次"),
        AnalysisOption("App 開啟次數", "開 App 的次數"),
        AnalysisOption("平均註冊月份", "註冊月份的平均"),
        AnalysisOption("每日新增用戶", "每天新註冊的人數"),
        AnalysisOption("付費轉換率", "免費轉付費的比例"),
    )

    val segs = listOf("依方案", "依註冊時間", "依活躍程度", "不分群")

    val methods = listOf(
        AnalysisOption("對比", "兩群人放一起比高低"),
        AnalysisOption("分組", "把人分成幾群，各算一個數"),
        AnalysisOption("交叉", "兩個條件一起切"),
        AnalysisOption("漏斗", "看一步一步掉了多少人"),
    )

    val intents = listOf(
        "哪些使用者可能比較需要",
        "使用行為和留存有沒有關係",
        "使用者是否已經有替代做法",
        "目前資料能不能支持判斷",
        "其他觀察",
    )

    fun labels(seg: String): List<String> = when (seg) {
        "依方案" -> listOf("free", "plus", "pro")
        "依註冊時間" -> listOf("六月前", "六～七月", "八月後")
        "依活躍程度" -> listOf("低", "中", "高")
        else -> listOf("全體")
    }

    /** Deterministic fake percentages so the same choice always draws the same chart. */
    fun values(metric: String, seg: String): List<Int> {
        fun fake(m: String, s: String): List<Int> {
            var h = 0
            for (ch in m + s) h = (h * 31 + ch.code) % 997
            val b = 20 + h % 40
            return listOf(b, b + 8 + (h % 13), b - 5 + (h % 9)).map { it.coerceIn(6, 92) }
        }
        return if (seg == "不分群") listOf(fake(metric, "all")[0]) else fake(metric, seg)
    }
}