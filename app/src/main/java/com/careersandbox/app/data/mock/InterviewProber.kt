package com.careersandbox.app.data.mock

/* =====================================================================
   面試現場 —— 面試官追問行為資料層(LangGraph evaluate_node 接點)

   面試官的核心行為:「聽完一段回答 → 決定追問什麼 + 給什麼即時反應」。
   目前用關鍵字感知 + 探問池模擬(回答提到數據就追問數據、提到團隊就追問
   協作衝突…);真接入時由後端 LangGraph 的 evaluate_node 依回答內容
   (STAR 完整度、是否量化、有無反思)生成追問,實作 InterviewProber 即可,
   呼叫點(InterviewLiveIndividualScreen.submitAnswer)完全不變。
   ===================================================================== */

/** 面試官追問行為(後端接點) */
interface InterviewProber {
    /**
     * 聽完使用者回答後挑下一句追問。
     * @param userAnswer 使用者這次的回答
     * @param followUpIdx 目前第幾次追問
     * @param fallback    沒命中關鍵字時用的預設追問池(依面試類型:一般/技術/情境)
     */
    fun probe(userAnswer: String, followUpIdx: Int, fallback: List<String>): String

    /** 回答送出後的即時微反應(「嗯。」/「(點了點頭)」…) */
    fun reaction(): String
}

/** Mock 實作:關鍵字感知 + 探問池。真接 LangGraph 時整個換掉,介面不變。 */
object MockInterviewProber : InterviewProber {

    private fun String.containsAny(vararg keys: String) = keys.any { this.contains(it) }

    private val probesData = listOf(
        "這個數字是怎麼算出來的?基準是什麼?",
        "如果數據跟你的直覺打架,你信哪個?為什麼?",
    )
    private val probesTeam = listOf(
        "團隊裡誰跟你意見最不合?那次最後怎麼收?",
        "如果有人擺爛,你的第一步是什麼?",
    )
    private val probesFail = listOf(
        "這件事裡,你自己要負的是哪一塊?",
        "同樣的錯,後來有再犯嗎?你改了什麼?",
    )
    private val probesHonest = listOf(
        "沒關係,當場想。你會從哪裡開始?",
        "可以。那換個你熟的,講一個你最有把握的決定。",
    )
    private val probesTime = listOf(
        "時間砍一半,你先丟掉哪一塊?",
        "你怎麼判斷一件事該做快的版本,還是好的版本?",
    )
    private val microReactions = listOf("嗯。", "(他停了一下)", "(低頭記了些什麼)", "(點了點頭)")

    override fun probe(userAnswer: String, followUpIdx: Int, fallback: List<String>): String = when {
        userAnswer.containsAny("不知道", "不確定", "沒想過", "沒有經驗") -> probesHonest.random()
        userAnswer.containsAny("數據", "資料", "數字", "分析", "%", "成長") -> probesData.random()
        userAnswer.containsAny("團隊", "合作", "夥伴", "組員", "溝通", "衝突") -> probesTeam.random()
        userAnswer.containsAny("失敗", "錯", "搞砸", "延期", "沒做好") -> probesFail.random()
        userAnswer.containsAny("時間", "趕", "deadline", "來不及", "期限") -> probesTime.random()
        else -> fallback[followUpIdx % fallback.size]
    }

    override fun reaction(): String = microReactions.random()
}
