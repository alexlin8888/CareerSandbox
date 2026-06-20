package com.careersandbox.app.data.mock

/* =====================================================================
   主管 panel 面試 —— 多代理派發資料層(LangGraph dispatcher_node 接點)

   多代理面試的核心:聽完一段回答後,由「對的主管」問「對的問題」——
     提到數據/分析 → 技術主管追問
     提到團隊/衝突 → HR 主管追問
     答不出/沒想過 → HR 主管接住
     講成果/取捨/決定 → 用人主管追問
   這正是 LangGraph 多代理擴充要加的 dispatcher_node:依回答內容把球派給
   某個 persona,再由該 persona 的 system prompt 出題。目前用關鍵字感知模擬;
   真接入時由 dispatcher_node 做語意路由 + persona 生成,實作 PanelDispatcher
   即可,呼叫點(InterviewLivePanelScreen.submitPanel)完全不變。
   ===================================================================== */

/** 主管 panel 的派發行為(後端接點) */
interface PanelDispatcher {
    /** 聽完回答,派給對的主管問對的問題,回傳 (主管名稱, 問題) */
    fun dispatch(userAnswer: String, followUpIdx: Int): Pair<String, String>

    /** 回答送出後的即時反應(三位主管的細微反應) */
    fun reaction(): String
}

/** Mock 實作:關鍵字感知路由 + 各主管探問池。真接 LangGraph dispatcher 時整個換掉。 */
object MockPanelDispatcher : PanelDispatcher {

    private fun String.containsAny(vararg keys: String) = keys.any { this.contains(it) }

    private val techPool = listOf(
        "工具是手段。講一次你用數據推翻原本決定的經驗。",
        "這個分析如果重做,你會多補哪個維度?",
    )
    private val hrPool = listOf(
        "衝突那段多講一點:你當下實際說了什麼?",
        "你怎麼確定對方是被說服,而不是不想吵了?",
    )
    private val honestPool = listOf(
        "誠實很好。那你打算怎麼補這一塊?",
        "沒關係。換你最有把握的那段經驗,講給我們聽。",
    )
    private val leadPool = listOf(
        "如果履歷只能留一個成果,你留哪個?為什麼?",
        "這個決定如果錯了,代價是什麼?你當時想過嗎?",
    )

    /** 沒命中關鍵字時的循序追問(輪流由不同主管問) */
    private val sequentialFollowUps = listOf(
        "HR 主管" to "謝謝。換個角度,團隊合作裡你遇過最大的衝突是什麼?後來怎麼處理?",
        "技術主管" to "可以再具體一點嗎?那個判斷你是看哪些數據下的?",
        "用人主管" to "如果這個專案時程被砍一半,你會怎麼重排優先順序?",
        "HR 主管" to "最後一題:你覺得自己現在最需要補強的地方是什麼?",
    )

    private val reactions = listOf("嗯。", "(他記了一筆)", "(三位交換了眼神)", "(點頭)")

    override fun dispatch(userAnswer: String, followUpIdx: Int): Pair<String, String> = when {
        userAnswer.containsAny("不知道", "不確定", "沒想過") -> "HR 主管" to honestPool.random()
        userAnswer.containsAny("數據", "資料", "數字", "分析", "%") -> "技術主管" to techPool.random()
        userAnswer.containsAny("團隊", "合作", "衝突", "溝通", "夥伴") -> "HR 主管" to hrPool.random()
        userAnswer.containsAny("成果", "負責", "決定", "優先", "取捨") -> "用人主管" to leadPool.random()
        else -> sequentialFollowUps[followUpIdx % sequentialFollowUps.size]
    }

    override fun reaction(): String = reactions.random()
}
