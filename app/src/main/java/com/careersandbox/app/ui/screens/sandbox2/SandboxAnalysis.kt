package com.careersandbox.app.ui.screens.sandbox2

data class AnalysisOption(val name: String, val explain: String)

data class Candidate(val text: String, val kind: Char)   // o observation, c causal, v to-verify, x overgeneralized

/** A limitation checkbox in the summary. col is the dataset column it comes from, null for decoys. */
data class SummaryLimit(val text: String, val col: Int?)

private class FitRule(val ok: (String) -> Boolean, val needsSeg: Boolean)
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
    val relevantMetrics = setOf("稍後再看使用率", "次月留存", "客服詢問比例", "人均使用次數", "付費轉換率")

    val summaryLimits = listOf(
        SummaryLimit("「稍後再看」只有六月以後的資料，看不出功能上線前的行為", 3),
        SummaryLimit("留存的算法和營運端可能不一樣", 5),
        SummaryLimit("客服詢問是人工記的、只有近三個月", 6),
        SummaryLimit("樣本太少不能做統計檢定", null),
        SummaryLimit("沒有競品資料", null),
    )

    val recommendations = listOf("直接開發", "小規模試點", "先補資料再評估", "暫緩")

    private val ruleWho = FitRule({ it in setOf("稍後再看使用率", "人均使用次數") }, true)
    private val ruleRet = FitRule({ it == "次月留存" }, true)
    private val ruleAlt = FitRule({ it in setOf("客服詢問比例", "稍後再看使用率") }, false)
    private val ruleAny = FitRule({ it in relevantMetrics }, false)

    private fun ruleForQuestion(q: String): FitRule = when {
        q.startsWith("誰會用") -> ruleWho
        q.startsWith("用的人，留存") -> ruleRet
        q.startsWith("現在沒這功能") -> ruleAlt
        q.startsWith("重度使用者") -> ruleWho
        else -> ruleAny
    }

    private fun ruleForPin(p: ChartPin, taskList: List<String>): FitRule = when {
        p.questionIndex != null -> ruleForQuestion(taskList.getOrNull(p.questionIndex) ?: "")
        p.intent == "哪些使用者可能比較需要" -> ruleWho
        p.intent == "使用行為和留存有沒有關係" -> ruleRet
        p.intent == "使用者是否已經有替代做法" -> ruleAlt
        else -> ruleAny
    }

    /** Whether the pinned chart can answer the question (or intent) it was pinned for. */
    fun fits(p: ChartPin, taskList: List<String>): Boolean {
        val rule = ruleForPin(p, taskList)
        if (!rule.ok(p.metric)) return false
        if (rule.needsSeg && p.seg == "不分群") return false
        return true
    }

    /** What the PM says when a chart does not fit its question. */
    fun remark(p: ChartPin, taskList: List<String>): String {
        if (p.metric !in relevantMetrics) return "這張${p.metric}的圖是在講什麼？跟收藏清單有關嗎"
        val rule = ruleForPin(p, taskList)
        if (rule.needsSeg && p.seg == "不分群") {
            val q = (p.questionIndex?.let { taskList.getOrNull(it) } ?: "").substringBefore("（").replace("？", "")
            return "這張只有一個數字，看不出「$q」欸"
        }
        return "這張圖跟你說要回答的那題好像對不太上"
    }

    /** Four ways to write the same chart into the summary. No label says which one is right. */
    fun candidates(p: ChartPin): List<Candidate> {
        if (p.seg == "不分群") {
            val v = p.values.firstOrNull() ?: 0
            return listOf(
                Candidate("整體${p.metric}是 $v%。", 'o'),
                Candidate("${p.metric}有 $v%，所以收藏清單值得做。", 'c'),
                Candidate("整體${p.metric}是 $v%，但這不代表對收藏功能的需求，還需要驗證。", 'v'),
                Candidate("大部分使用者都需要收藏功能。", 'x'),
            )
        }
        val mx = p.values.maxOrNull() ?: 0
        val mn = p.values.minOrNull() ?: 0
        val top = p.labels[p.values.indexOf(mx)]
        val lo = p.labels[p.values.indexOf(mn)]
        return listOf(
            Candidate("在這份資料裡，${top}這群的${p.metric}最高（$mx%），${lo}最低（$mn%）。", 'o'),
            Candidate("${top}這群${p.metric}比較高，所以收藏清單值得做。", 'c'),
            Candidate("${top}這群${p.metric}較高，但這是否代表他們更需要收藏功能，還需要驗證。", 'v'),
            Candidate("所有${p.seg.replace("依", "")}的使用者都需要收藏清單。", 'x'),
        )
    }
}