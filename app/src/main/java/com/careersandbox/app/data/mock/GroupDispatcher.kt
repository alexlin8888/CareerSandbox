package com.careersandbox.app.data.mock

/* =====================================================================
   團體面試 —— AI 同儕派發 + 搶話資料層(LangGraph 多代理同儕接點)

   團體面試最有特色的兩件事:
   1) 派發:聽完你的回答,由「對的 AI 應徵者」跳出來接話 ——
        提到數據/驗證 → AI-邏輯 質疑母數;提到搶快/先做 → AI-強勢 嗆更激進;
        提到團隊/同意 → AI-親切 補位;答不出 → 主考官 接住。
   2) 搶話:你打字停頓太久,AI-強勢 不會等你,會先講(有次數上限)。
   這兩者真接入時由後端多代理(每個 AI 應徵者一個 persona + 主考官)生成,
   實作 GroupDispatcher 即可,呼叫點(InterviewLiveGroupScreen)不變。
   ===================================================================== */

/** 團體面試的 AI 同儕派發與搶話行為(後端接點) */
interface GroupDispatcher {
    /** 聽完回答,由對的 AI 應徵者(或主考官)接話,回傳 (說話者, 內容) */
    fun dispatch(userAnswer: String, followUpIdx: Int): Pair<String, String>

    /** 搶話事件:你停頓太久時,AI 搶話的第 index 句 */
    fun interruptLine(index: Int): String

    /** 搶話次數上限(每場最多被搶幾次) */
    fun interruptCap(): Int
}

/** Mock 實作:關鍵字感知派發 + 搶話池。真接 LangGraph 多代理時整個換掉。 */
object MockGroupDispatcher : GroupDispatcher {

    private fun String.containsAny(vararg keys: String) = keys.any { this.contains(it) }

    private val logicPool = listOf(
        "等等,這個數字的母數是多少?沒有對照組我不敢下結論。",
        "你這段推論跳了一步,中間的假設是什麼?",
    )
    private val assertivePool = listOf(
        "我打斷一下,結論先講,我們時間不多。",
        "這樣太慢了。我的版本:先上線再修,你要不要跟?",
    )
    private val friendlyPool = listOf(
        "我接你這段,方向我同意,分工那邊可以再具體一點嗎?",
        "你剛剛那個例子不錯,可以再展開一點。",
    )
    private val examinerHonestPool = listOf(
        "沒關係,不確定就說不確定。那你目前確定的部分是什麼?",
    )

    /** 沒命中關鍵字時的循序追問(主考官與 AI 應徵者輪流) */
    private val sequentialFollowUps = listOf(
        "主考官" to "謝謝。換個角度,如果資源只夠做一件事,你會先砍掉哪個?",
        "AI-強勢" to "我補一句,我的做法更直接:先搶下市場,細節之後再優化。",
        "AI-邏輯" to "可是這沒有數據支撐吧?我會先做小規模驗證,再決定要不要放大。",
        "AI-親切" to "我覺得你講得不錯耶,不過團隊怎麼分工那段可以再多說一點。",
        "主考官" to "那你會怎麼回應剛剛其他人提出的質疑?",
    )

    /** 搶話事件的台詞(AI-強勢 在你停頓太久時先講) */
    private val interrupts = listOf(
        "我先說,這題我有現成的案子,等大家想完時間就沒了。",
        "(舉手)我插一個快的,你慢慢想,不衝突。",
    )

    override fun dispatch(userAnswer: String, followUpIdx: Int): Pair<String, String> = when {
        userAnswer.containsAny("不知道", "不確定", "沒想過") -> "主考官" to examinerHonestPool.random()
        userAnswer.containsAny("數據", "資料", "數字", "驗證", "分析") -> "AI-邏輯" to logicPool.random()
        userAnswer.containsAny("結論", "直接", "先做", "搶", "快") -> "AI-強勢" to assertivePool.random()
        userAnswer.containsAny("大家", "同意", "補充", "一起", "團隊") -> "AI-親切" to friendlyPool.random()
        else -> sequentialFollowUps[followUpIdx % sequentialFollowUps.size]
    }

    override fun interruptLine(index: Int): String = interrupts[index]

    override fun interruptCap(): Int = interrupts.size
}
